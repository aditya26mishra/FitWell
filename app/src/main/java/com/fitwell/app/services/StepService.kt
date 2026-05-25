package com.fitwell.app.services

import android.app.*
import android.content.*
import android.hardware.*
import android.os.*
import androidx.core.app.NotificationCompat
import com.fitwell.app.R
import com.fitwell.app.activities.MainActivity
import com.fitwell.app.utils.Dates

class StepService : Service(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var stepCounterSensor: Sensor? = null
    private var stepDetectorSensor: Sensor? = null
    private var useCounterSensor = false

    private val binder = LocalBinder()
    private var stepCallback: ((Int) -> Unit)? = null

    private lateinit var prefs: SharedPreferences

    // Raw sensor value captured at the START of today — used to offset sensor readings to 0
    private var todayBase = -1L   // -1 = not yet set for today
    // Steps accumulated today (what the user sees)
    var currentSteps = 0
        private set

    companion object {
        const val CHANNEL_ID = "fw_steps"
        const val NOTIF_ID   = 1001
        const val PREF_FILE  = "fw_steps"
        const val KEY_STEPS  = "steps_today"
        const val KEY_DATE   = "step_date"
        const val KEY_BASE   = "sensor_base_long"  // renamed to avoid stale Int pref
    }

    inner class LocalBinder : Binder() {
        fun service() = this@StepService
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onCreate() {
        super.onCreate()
        prefs = getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE)
        createChannel()
        startForeground(NOTIF_ID, buildNotif(0))
        restoreState()
        registerSensors()
    }

    private fun restoreState() {
        val savedDate = prefs.getString(KEY_DATE, "") ?: ""
        val today = Dates.today()
        if (savedDate == today) {
            // Same day — restore previously saved state
            currentSteps = prefs.getInt(KEY_STEPS, 0)
            todayBase = prefs.getLong(KEY_BASE, -1L)
        } else {
            // New day — reset EVERYTHING to zero, re-baseline when first sensor event arrives
            resetForNewDay(today)
        }
    }

    private fun resetForNewDay(today: String) {
        prefs.edit()
            .putString(KEY_DATE, today)
            .putInt(KEY_STEPS, 0)
            .putLong(KEY_BASE, -1L)
            .apply()
        todayBase    = -1L
        currentSteps = 0
    }

    private fun registerSensors() {
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepCounterSensor  = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)

        when {
            stepCounterSensor != null -> {
                useCounterSensor = true
                sensorManager.registerListener(this, stepCounterSensor,
                    SensorManager.SENSOR_DELAY_NORMAL)
            }
            stepDetectorSensor != null -> {
                useCounterSensor = false
                sensorManager.registerListener(this, stepDetectorSensor,
                    SensorManager.SENSOR_DELAY_NORMAL)
            }
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event ?: return

        // Always check for day rollover first
        val today = Dates.today()
        val savedDate = prefs.getString(KEY_DATE, "") ?: ""
        if (savedDate != today) {
            resetForNewDay(today)
        }

        if (useCounterSensor && event.sensor.type == Sensor.TYPE_STEP_COUNTER) {
            val rawFromSensor = event.values[0].toLong()

            if (todayBase == -1L) {
                // First reading of today — use this as our baseline (so user sees 0)
                todayBase = rawFromSensor
                prefs.edit().putLong(KEY_BASE, todayBase).apply()
                currentSteps = 0
            } else {
                // Calculate steps taken since today started
                currentSteps = (rawFromSensor - todayBase).toInt().coerceAtLeast(0)
            }

        } else if (!useCounterSensor && event.sensor.type == Sensor.TYPE_STEP_DETECTOR) {
            // STEP_DETECTOR fires exactly once per step — just increment
            currentSteps++
        } else {
            return
        }

        prefs.edit().putInt(KEY_STEPS, currentSteps).apply()
        stepCallback?.invoke(currentSteps)
        updateNotif(currentSteps)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    fun setCallback(cb: (Int) -> Unit) {
        stepCallback = cb
        cb(currentSteps) // deliver current value immediately to new subscriber
    }

    fun clearCallback() { stepCallback = null }

    private fun buildNotif(steps: Int): Notification {
        val pi = PendingIntent.getActivity(this, 0,
            Intent(this, MainActivity::class.java), PendingIntent.FLAG_IMMUTABLE)
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("FitWell")
            .setContentText("Steps today: $steps")
            .setSmallIcon(R.drawable.ic_steps)
            .setContentIntent(pi)
            .setOngoing(true)
            .setSilent(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun updateNotif(steps: Int) {
        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
            .notify(NOTIF_ID, buildNotif(steps))
    }

    private fun createChannel() {
        val ch = NotificationChannel(CHANNEL_ID, "Step Counter", NotificationManager.IMPORTANCE_LOW)
            .apply { description = "Live step tracking" }
        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(ch)
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
    }
}

package com.fitwell.app.receivers

import android.app.*
import android.content.*
import androidx.core.app.NotificationCompat
import com.fitwell.app.R
import com.fitwell.app.services.StepService
import com.fitwell.app.utils.Session

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            if (Session(context).loggedIn) {
                context.startForegroundService(Intent(context, StepService::class.java))
            }
        }
    }
}

class HydrationReceiver : BroadcastReceiver() {
    companion object {
        const val CHANNEL_ID = "fw_hydration"

        fun schedule(context: Context) {
            val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            // Remind every 2 hours from 8am to 10pm
            val hours = listOf(8, 10, 12, 14, 16, 18, 20, 22)
            createChannel(context)
            hours.forEachIndexed { i, hour ->
                val cal = java.util.Calendar.getInstance().apply {
                    set(java.util.Calendar.HOUR_OF_DAY, hour)
                    set(java.util.Calendar.MINUTE, 0)
                    set(java.util.Calendar.SECOND, 0)
                    if (timeInMillis < System.currentTimeMillis()) add(java.util.Calendar.DAY_OF_YEAR, 1)
                }
                val pi = PendingIntent.getBroadcast(
                    context, 2000 + i,
                    Intent(context, HydrationReceiver::class.java),
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                try {
                    am.setRepeating(AlarmManager.RTC_WAKEUP, cal.timeInMillis,
                        AlarmManager.INTERVAL_DAY, pi)
                } catch (e: Exception) { /* SCHEDULE_EXACT_ALARM not granted */ }
            }
        }

        fun cancel(context: Context) {
            val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            for (i in 0..7) {
                val pi = PendingIntent.getBroadcast(context, 2000 + i,
                    Intent(context, HydrationReceiver::class.java),
                    PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE) ?: continue
                am.cancel(pi)
            }
        }

        private fun createChannel(context: Context) {
            val ch = NotificationChannel(CHANNEL_ID, "Hydration Reminders",
                NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = "Periodic reminders to drink water"
            }
            (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                .createNotificationChannel(ch)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        val notif = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_water)
            .setContentTitle("Time to hydrate!")
            .setContentText("Drink a glass of water to stay on track.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()
        (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
            .notify(3000 + (System.currentTimeMillis() % 100).toInt(), notif)
    }
}

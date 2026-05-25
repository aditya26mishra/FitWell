package com.fitwell.app.activities

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.fitwell.app.R
import com.fitwell.app.database.AppDatabase
import com.fitwell.app.database.UserEntity
import com.fitwell.app.receivers.HydrationReceiver
import com.fitwell.app.services.StepService
import com.fitwell.app.utils.*
import kotlinx.coroutines.launch
import java.util.*

class AuthActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var session: Session
    private var isSubmitting = false

    // Login
    private lateinit var loginCard: View
    private lateinit var loginEmail: TextInputEditText
    private lateinit var loginPass: TextInputEditText
    private lateinit var btnLogin: MaterialButton
    private lateinit var loginProgress: ProgressBar

    // Register
    private lateinit var registerCard: View
    private lateinit var regName: TextInputEditText
    private lateinit var regEmail: TextInputEditText
    private lateinit var regPass: TextInputEditText
    private lateinit var regAge: TextInputEditText
    private lateinit var spinnerHeightUnit: Spinner
    private lateinit var tilHeightCm: TextInputLayout
    private lateinit var rowHeightFtIn: View
    private lateinit var etRegHeight: TextInputEditText
    private lateinit var etRegHeightFt: TextInputEditText
    private lateinit var etRegHeightIn: TextInputEditText
    private lateinit var spinnerWeightUnit: Spinner
    private lateinit var tilRegWeight: TextInputLayout
    private lateinit var tilRegTargetWeight: TextInputLayout
    private lateinit var etRegWeight: TextInputEditText
    private lateinit var etRegTargetWeight: TextInputEditText
    private lateinit var spinnerGender: Spinner
    private lateinit var spinnerActivity: Spinner
    private lateinit var spinnerGoal: Spinner
    private lateinit var spinnerDiet: Spinner
    private lateinit var btnRegister: MaterialButton
    private lateinit var regProgress: ProgressBar
    private lateinit var goalDateRow: View
    private lateinit var btnGoalDate: MaterialButton
    private var selectedGoalDate = ""

    private val goalKeys = arrayOf("weight_loss","muscle_gain","maintenance","general_fitness")
    private val actKeys  = arrayOf("sedentary","light","moderate","active","very_active")
    private val dietKeys = arrayOf("non_vegetarian","vegetarian","vegan","pescatarian","egg_based")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        db = AppDatabase.get(this); session = Session(this)
        val tabLayout = findViewById<TabLayout>(R.id.authTabs)
        loginCard = findViewById(R.id.loginCard); registerCard = findViewById(R.id.registerCard)
        bindLoginViews(); bindRegisterViews(); setupSpinners()
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                loginCard.visibility    = if (tab.position == 0) View.VISIBLE else View.GONE
                registerCard.visibility = if (tab.position == 1) View.VISIBLE else View.GONE
                isSubmitting = false
            }
            override fun onTabUnselected(t: TabLayout.Tab) {}
            override fun onTabReselected(t: TabLayout.Tab) {}
        })
        btnLogin.setOnClickListener    { doLogin() }
        btnRegister.setOnClickListener { doRegister() }
    }

    private fun bindLoginViews() {
        loginEmail    = findViewById(R.id.etLoginEmail)
        loginPass     = findViewById(R.id.etLoginPass)
        btnLogin      = findViewById(R.id.btnLogin)
        loginProgress = findViewById(R.id.loginProgress)
    }

    private fun bindRegisterViews() {
        regName            = findViewById(R.id.etRegName)
        regEmail           = findViewById(R.id.etRegEmail)
        regPass            = findViewById(R.id.etRegPass)
        regAge             = findViewById(R.id.etRegAge)
        spinnerHeightUnit  = findViewById(R.id.spinnerHeightUnit)
        tilHeightCm        = findViewById(R.id.tilHeightCm)
        rowHeightFtIn      = findViewById(R.id.rowHeightFtIn)
        etRegHeight        = findViewById(R.id.etRegHeight)
        etRegHeightFt      = findViewById(R.id.etRegHeightFt)
        etRegHeightIn      = findViewById(R.id.etRegHeightIn)
        spinnerWeightUnit  = findViewById(R.id.spinnerWeightUnit)
        tilRegWeight       = findViewById(R.id.tilRegWeight)
        tilRegTargetWeight = findViewById(R.id.tilRegTargetWeight)
        etRegWeight        = findViewById(R.id.etRegWeight)
        etRegTargetWeight  = findViewById(R.id.etRegTargetWeight)
        spinnerGender      = findViewById(R.id.spinnerGender)
        spinnerActivity    = findViewById(R.id.spinnerActivity)
        spinnerGoal        = findViewById(R.id.spinnerGoal)
        spinnerDiet        = findViewById(R.id.spinnerDiet)
        btnRegister        = findViewById(R.id.btnRegister)
        regProgress        = findViewById(R.id.regProgress)
        goalDateRow        = findViewById(R.id.goalDateRow)
        btnGoalDate        = findViewById(R.id.btnGoalDate)
    }

    private fun setupSpinners() {
        spinnerHeightUnit.adapter = ArrayAdapter(this, R.layout.item_spinner, arrayOf("cm","ft / in"))
        spinnerWeightUnit.adapter = ArrayAdapter(this, R.layout.item_spinner, arrayOf("kg","lbs"))
        spinnerGender.adapter     = ArrayAdapter(this, R.layout.item_spinner, arrayOf("Male","Female","Other"))
        spinnerActivity.adapter   = ArrayAdapter(this, R.layout.item_spinner, arrayOf(
            "Sedentary (desk job)","Light (1-2×/week)","Moderate (3-5×/week)","Active (6-7×/week)","Very Active (athlete)"))
        spinnerGoal.adapter       = ArrayAdapter(this, R.layout.item_spinner, arrayOf(
            "Weight Loss","Muscle Gain","Maintenance","General Fitness"))
        spinnerDiet.adapter       = ArrayAdapter(this, R.layout.item_spinner, arrayOf(
            "Non-Vegetarian","Vegetarian","Vegan","Pescatarian","Egg-based"))

        spinnerHeightUnit.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p: AdapterView<*>?, v: View?, pos: Int, id: Long) {
                tilHeightCm.visibility   = if (pos == 0) View.VISIBLE else View.GONE
                rowHeightFtIn.visibility = if (pos == 1) View.VISIBLE else View.GONE
            }
            override fun onNothingSelected(p: AdapterView<*>?) {}
        }
        spinnerWeightUnit.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p: AdapterView<*>?, v: View?, pos: Int, id: Long) {
                tilRegWeight.hint       = if (pos == 0) "Weight (kg)" else "Weight (lbs)"
                tilRegTargetWeight.hint = if (pos == 0) "Target Weight (kg) — optional" else "Target Weight (lbs) — optional"
            }
            override fun onNothingSelected(p: AdapterView<*>?) {}
        }
        spinnerGoal.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p: AdapterView<*>?, v: View?, pos: Int, id: Long) {
                goalDateRow.visibility = if (goalKeys[pos] == "weight_loss") View.VISIBLE else View.GONE
            }
            override fun onNothingSelected(p: AdapterView<*>?) {}
        }
        btnGoalDate.setOnClickListener { pickDate() }
    }

    private fun pickDate() {
        val cal = Calendar.getInstance(); cal.add(Calendar.MONTH, 3)
        DatePickerDialog(this, { _, y, m, d ->
            selectedGoalDate = "%04d-%02d-%02d".format(y, m+1, d)
            btnGoalDate.text = "Goal date: $selectedGoalDate"
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
    }

    // ── Login ─────────────────────────────────────────────────────────────────
    private fun doLogin() {
        if (isSubmitting) return
        val emailRaw = loginEmail.text.toString()
        val passRaw  = loginPass.text.toString()
        if (emailRaw.trim().isEmpty() || passRaw.isEmpty()) {
            toast("Please fill in all fields before signing in"); return
        }
        Validator.email(emailRaw)?.let { toast(it); return }
        val email = emailRaw.trim().lowercase()
        isSubmitting = true
        loginProgress.visibility = View.VISIBLE; btnLogin.isEnabled = false
        lifecycleScope.launch {
            val byEmail = db.userDao().getByEmail(email)
            if (byEmail == null) {
                runOnUiThread {
                    loginProgress.visibility = View.GONE; btnLogin.isEnabled = true; isSubmitting = false
                    toast("No account found for this email address")
                }; return@launch
            }
            val user = db.userDao().login(email, Crypto.hash(passRaw))
            runOnUiThread {
                loginProgress.visibility = View.GONE; btnLogin.isEnabled = true; isSubmitting = false
                if (user != null) {
                    session.userId = user.id; session.loggedIn = true
                    startForegroundService(Intent(this@AuthActivity, StepService::class.java))
                    HydrationReceiver.schedule(this@AuthActivity)
                    goMain()
                } else toast("Incorrect password — please try again")
            }
        }
    }

    // ── Register ──────────────────────────────────────────────────────────────
    private fun doRegister() {
        if (isSubmitting) return

        Validator.fullName(regName.text.toString())?.let { toast(it); return }
        val name = regName.text.toString().trim()

        Validator.email(regEmail.text.toString())?.let { toast(it); return }
        val email = regEmail.text.toString().trim().lowercase()

        Validator.password(regPass.text.toString())?.let { toast(it); return }

        Validator.age(regAge.text.toString())?.let { toast(it); return }
        val age = regAge.text.toString().trim().toInt()

        val heightCm: Float = if (spinnerHeightUnit.selectedItemPosition == 0) {
            Validator.heightCm(etRegHeight.text.toString())?.let { toast(it); return } ?: Unit
            etRegHeight.text.toString().trim().toFloat()
        } else {
            Validator.heightFt(etRegHeightFt.text.toString(), etRegHeightIn.text.toString())?.let { toast(it); return } ?: Unit
            Validator.ftInToCm(etRegHeightFt.text.toString().trim().toInt(), etRegHeightIn.text.toString().trim().toInt())
        }

        val weightKg: Float = if (spinnerWeightUnit.selectedItemPosition == 0) {
            Validator.weightKg(etRegWeight.text.toString())?.let { toast(it); return } ?: Unit
            etRegWeight.text.toString().trim().toFloat()
        } else {
            Validator.weightLbs(etRegWeight.text.toString())?.let { toast(it); return } ?: Unit
            Validator.lbsToKg(etRegWeight.text.toString().trim().toFloat())
        }

        val targetWeightKg: Float = if (spinnerWeightUnit.selectedItemPosition == 0) {
            Validator.targetWeightKg(etRegTargetWeight.text.toString())?.let { toast(it); return } ?: Unit
            etRegTargetWeight.text.toString().trim().toFloatOrNull() ?: weightKg
        } else {
            Validator.targetWeightLbs(etRegTargetWeight.text.toString())?.let { toast(it); return } ?: Unit
            etRegTargetWeight.text.toString().trim().toFloatOrNull()?.let { Validator.lbsToKg(it) } ?: weightKg
        }

        val gender   = arrayOf("male","female","other")[spinnerGender.selectedItemPosition]
        val actLevel = actKeys[spinnerActivity.selectedItemPosition]
        val goal     = goalKeys[spinnerGoal.selectedItemPosition]
        val diet     = dietKeys[spinnerDiet.selectedItemPosition]

        isSubmitting = true
        regProgress.visibility = View.VISIBLE; btnRegister.isEnabled = false
        lifecycleScope.launch {
            if (db.userDao().getByEmail(email) != null) {
                runOnUiThread {
                    regProgress.visibility = View.GONE; btnRegister.isEnabled = true; isSubmitting = false
                    toast("An account with this email already exists")
                }; return@launch
            }
            val bmr = Calc.bmr(weightKg, heightCm, age, gender)
            val tdee = Calc.tdee(bmr, actLevel)
            val calGoal = if (goal == "weight_loss" && selectedGoalDate.isNotEmpty())
                WeightLossCalc.dailyCalorieGoal(weightKg, targetWeightKg, selectedGoalDate, tdee)
            else Calc.calorieGoal(tdee, goal)

            val user = UserEntity(name=name, email=email, passwordHash=Crypto.hash(regPass.text.toString()),
                age=age, heightCm=heightCm, weightKg=weightKg, gender=gender, activityLevel=actLevel,
                goal=goal, targetWeightKg=targetWeightKg, dailyCalorieGoal=calGoal,
                dailyStepGoal=10000, dietPreference=diet,
                goalTargetDate=if (goal=="weight_loss") selectedGoalDate else "")
            val id = db.userDao().insert(user).toInt()
            session.userId = id; session.loggedIn = true
            runOnUiThread { regProgress.visibility = View.GONE; btnRegister.isEnabled = true; isSubmitting = false; goMain() }
        }
    }

    private fun goMain() {
        startForegroundService(Intent(this, StepService::class.java))
        HydrationReceiver.schedule(this)
        startActivity(Intent(this, MainActivity::class.java)); finish()
    }

    private fun toast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
}

package com.fitwell.app.fragments

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import com.fitwell.app.R
import com.fitwell.app.activities.AuthActivity
import com.fitwell.app.database.AppDatabase
import com.fitwell.app.receivers.HydrationReceiver
import com.fitwell.app.utils.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {
    private lateinit var db: AppDatabase
    private var uid = -1

    override fun onCreateView(inflater: LayoutInflater, c: ViewGroup?, s: Bundle?): View =
        inflater.inflate(R.layout.fragment_profile, c, false)

    override fun onViewCreated(view: View, s: Bundle?) {
        db = AppDatabase.get(requireContext())
        uid = Session(requireContext()).userId

        val tvName     = view.findViewById<TextView>(R.id.tvProfileName)
        val tvEmail    = view.findViewById<TextView>(R.id.tvProfileEmail)
        val tvAge      = view.findViewById<TextView>(R.id.tvProfileAge)
        val tvHeight   = view.findViewById<TextView>(R.id.tvProfileHeight)
        val tvWeight   = view.findViewById<TextView>(R.id.tvProfileWeight)
        val tvGoal     = view.findViewById<TextView>(R.id.tvProfileGoal)
        val tvBmi      = view.findViewById<TextView>(R.id.tvProfileBmi)
        val tvCalGoal  = view.findViewById<TextView>(R.id.tvProfileCalGoal)
        val btnEditCal = view.findViewById<TextView>(R.id.btnEditCalGoalInline)
        val tvStepGoal = view.findViewById<TextView>(R.id.tvProfileStepGoal)
        val tvDiet     = view.findViewById<TextView>(R.id.tvProfileDiet)
        val tvActivity = view.findViewById<TextView>(R.id.tvProfileActivity)
        val btnEdit    = view.findViewById<Button>(R.id.btnEditPrefs)
        val btnPlan    = view.findViewById<Button>(R.id.btnViewPlan)
        val btnLogout  = view.findViewById<Button>(R.id.btnLogout)
        val switchDark = view.findViewById<SwitchMaterial>(R.id.switchDarkMode)
        val switchNotif= view.findViewById<SwitchMaterial>(R.id.switchNotifications)
        val spinnerLang= view.findViewById<Spinner>(R.id.spinnerLanguage)

        val languages = arrayOf("English", "Hindi", "Spanish", "French", "German")
        val langCodes = arrayOf("en", "hi", "es", "fr", "de")
        spinnerLang.adapter = ArrayAdapter(requireContext(), R.layout.item_spinner, languages)

        // Shared prefs for settings — single source of truth
        val prefs = requireContext().getSharedPreferences("fw_settings", android.content.Context.MODE_PRIVATE)

        // Dark mode: read current state and set switch WITHOUT triggering listener
        val isDark = prefs.getBoolean("dark_mode", false)
        switchDark.setOnCheckedChangeListener(null)
        switchDark.isChecked = isDark

        switchDark.setOnCheckedChangeListener { _, checked ->
            prefs.edit().putBoolean("dark_mode", checked).apply()
            AppCompatDelegate.setDefaultNightMode(
                if (checked) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
        }

        viewLifecycleOwner.lifecycleScope.launch {
            db.userDao().getByIdFlow(uid).collectLatest { user ->
                user ?: return@collectLatest
                tvName.text     = user.name
                tvEmail.text    = user.email
                tvAge.text      = "Age: ${user.age}"
                tvHeight.text   = "Height: ${user.heightCm} cm"
                tvWeight.text   = "Weight: ${user.weightKg} kg"
                tvGoal.text     = "Goal: ${user.goal.replace("_"," ").replaceFirstChar{it.uppercase()}}"
                val bmi = Calc.bmi(user.weightKg, user.heightCm)
                tvBmi.text      = "BMI: ${"%.1f".format(bmi)} — ${Calc.bmiCategory(bmi)}"
                tvCalGoal.text  = "Calorie Goal: ${user.dailyCalorieGoal} kcal/day"
                tvStepGoal.text = "Step Goal: ${"%,d".format(user.dailyStepGoal)} steps/day"
                tvDiet.text     = "Diet: ${user.dietPreference.replace("_"," ").replaceFirstChar{it.uppercase()}}"
                tvActivity.text = "Activity: ${user.activityLevel.replace("_"," ").replaceFirstChar{it.uppercase()}}"
                switchNotif.isChecked = user.notificationsEnabled
                val li = langCodes.indexOf(user.language).coerceAtLeast(0)
                spinnerLang.setSelection(li)
            }
        }

        switchNotif.setOnCheckedChangeListener { _, checked ->
            viewLifecycleOwner.lifecycleScope.launch {
                val user = db.userDao().getById(uid) ?: return@launch
                db.userDao().update(user.copy(notificationsEnabled = checked))
                if (checked) HydrationReceiver.schedule(requireContext())
                else HydrationReceiver.cancel(requireContext())
            }
        }

        // Language — store in user DB and apply locale
        spinnerLang.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p: AdapterView<*>?, v: View?, pos: Int, id: Long) {
                val code = langCodes[pos]
                viewLifecycleOwner.lifecycleScope.launch {
                    val user = db.userDao().getById(uid) ?: return@launch
                    if (user.language != code) {
                        db.userDao().update(user.copy(language = code))
                        applyLocale(code)
                    }
                }
            }
            override fun onNothingSelected(p: AdapterView<*>?) {}
        }

        btnEdit.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                val user = db.userDao().getById(uid) ?: return@launch
                showEditPrefsDialog(user)
            }
        }

        val btnEditCredentials = view.findViewById<Button>(R.id.btnEditCredentials)
        btnEditCredentials.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                val user = db.userDao().getById(uid) ?: return@launch
                showEditProfileDialog(user)
            }
        }


        btnPlan.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                val user = db.userDao().getById(uid) ?: return@launch
                showBeautifulPlanDialog(user)
            }
        }

        btnEditCal.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                val user = db.userDao().getById(uid) ?: return@launch
                showQuickCalorieEdit(user)
            }
        }

        btnLogout.setOnClickListener {
            AlertDialog.Builder(requireContext()).setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Logout") { _, _ ->
                    HydrationReceiver.cancel(requireContext())
                    Session(requireContext()).logout()
                    startActivity(Intent(requireContext(), AuthActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    })
                }.setNegativeButton("Cancel", null).show()
        }
    }

    private fun applyLocale(code: String) {
        val locale = java.util.Locale(code)
        java.util.Locale.setDefault(locale)
        val config = android.content.res.Configuration(resources.configuration)
        config.setLocale(locale)
        requireContext().createConfigurationContext(config)
        // Restart activity to fully apply locale change
        requireActivity().recreate()
    }


    private fun showQuickCalorieEdit(user: com.fitwell.app.database.UserEntity) {
        val ctx = requireContext()
        val et = com.google.android.material.textfield.TextInputEditText(ctx).apply {
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
            setText(user.dailyCalorieGoal.toString())
            hint = "Calories per day"
        }
        val til = com.google.android.material.textfield.TextInputLayout(ctx, null,
            com.google.android.material.R.attr.textInputOutlinedStyle).apply {
            hint = "Daily Calorie Goal"
            setPadding(48, 24, 48, 8)
            addView(et)
        }
        AlertDialog.Builder(ctx)
            .setTitle("Set Daily Calorie Goal")
            .setMessage("This overrides the auto-calculated goal.")
            .setView(til)
            .setPositiveButton("Save") { _, _ ->
                val calErr = Validator.calorieGoal(et.text.toString())
                if (calErr != null) {
                    Toast.makeText(ctx, calErr, Toast.LENGTH_LONG).show()
                    return@setPositiveButton
                }
                val newGoal = et.text.toString().trim().toInt()
                viewLifecycleOwner.lifecycleScope.launch {
                    db.userDao().update(user.copy(dailyCalorieGoal = newGoal))
                    Toast.makeText(ctx, "Calorie goal updated to $newGoal kcal!", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showBeautifulPlanDialog(user: com.fitwell.app.database.UserEntity) {
        val ctx = requireContext()
        val scroll = ScrollView(ctx)
        val container = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 24, 48, 32)
        }

        // ── Workout section ──
        val tvWorkoutHeader = TextView(ctx).apply {
            text = "WORKOUT PLAN"
            textSize = 11f
            setTextColor(ctx.getColor(R.color.textSecondary))
            letterSpacing = 0.1f
            setPadding(0, 0, 0, 12)
        }
        container.addView(tvWorkoutHeader)

        Recs.workoutPlan(user.goal).forEach { item ->
            addPlanCard(ctx, container, item, R.color.colorPrimary)
        }

        // Divider
        val divider = View(ctx).apply {
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1).apply {
                topMargin = 16; bottomMargin = 16
            }
            setBackgroundColor(ctx.getColor(R.color.divider))
        }
        container.addView(divider)

        // ── Diet section ──
        val tvDietHeader = TextView(ctx).apply {
            text = "DIET PLAN"
            textSize = 11f
            setTextColor(ctx.getColor(R.color.textSecondary))
            letterSpacing = 0.1f
            setPadding(0, 0, 0, 12)
        }
        container.addView(tvDietHeader)

        Recs.dietPlan(user.goal, user.dietPreference, user.dailyCalorieGoal).forEach { item ->
            addPlanCard(ctx, container, item, R.color.colorGreen)
        }

        scroll.addView(container)
        AlertDialog.Builder(ctx)
            .setTitle("Your Personalised Plan")
            .setView(scroll)
            .setPositiveButton("Got it", null)
            .show()
    }

    private fun addPlanCard(ctx: android.content.Context, container: LinearLayout, item: String, accentColor: Int) {
        val card = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 20, 32, 20)
            background = ctx.getDrawable(R.drawable.bg_card)
            val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { bottomMargin = 10 }
            layoutParams = lp
        }
        val colonIdx = item.indexOf(":")
        val title = if (colonIdx > 0) item.substring(0, colonIdx).trim() else item
        val detail = if (colonIdx > 0) item.substring(colonIdx + 1).trim() else ""

        val tvTitle = TextView(ctx).apply {
            text = title
            textSize = 13f
            setTextColor(ctx.getColor(accentColor))
            typeface = android.graphics.Typeface.create("sans-serif-medium", android.graphics.Typeface.NORMAL)
        }
        card.addView(tvTitle)
        if (detail.isNotEmpty()) {
            val tvDetail = TextView(ctx).apply {
                text = detail
                textSize = 13f
                setTextColor(ctx.getColor(R.color.textPrimary))
                setPadding(0, 6, 0, 0)
            }
            card.addView(tvDetail)
        }
        container.addView(card)
    }

    private fun showEditPrefsDialog(user: com.fitwell.app.database.UserEntity) {
        val dv = layoutInflater.inflate(R.layout.dialog_edit_profile, null)
        val etHeight   = dv.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etEditHeight)
        val etWeight   = dv.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etEditWeight)
        val etTarget   = dv.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etEditTarget)
        val etSteps    = dv.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etEditSteps)
        val etCalGoal  = dv.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etEditCalGoal)
        val btnGoalDate= dv.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnEditGoalDate)
        val spGoal     = dv.findViewById<Spinner>(R.id.spEditGoal)
        val spDiet     = dv.findViewById<Spinner>(R.id.spEditDiet)
        val spActivity = dv.findViewById<Spinner>(R.id.spEditActivity)

        val goalKeys = arrayOf("weight_loss","muscle_gain","maintenance","general_fitness")
        val goals    = arrayOf("Weight Loss","Muscle Gain","Maintenance","General Fitness")
        val dietKeys = arrayOf("non_vegetarian","vegetarian","vegan","pescatarian","egg_based")
        val diets    = arrayOf("Non-Vegetarian","Vegetarian","Vegan","Pescatarian","Egg-based")
        val actKeys  = arrayOf("sedentary","light","moderate","active","very_active")
        val acts     = arrayOf("Sedentary","Light","Moderate","Active","Very Active")

        spGoal.adapter     = ArrayAdapter(requireContext(), R.layout.item_spinner, goals)
        spDiet.adapter     = ArrayAdapter(requireContext(), R.layout.item_spinner, diets)
        spActivity.adapter = ArrayAdapter(requireContext(), R.layout.item_spinner, acts)

        etHeight.setText(user.heightCm.toString())
        etWeight.setText(user.weightKg.toString())
        etTarget.setText(user.targetWeightKg.toString())
        etSteps.setText(user.dailyStepGoal.toString())
        etCalGoal.setText(user.dailyCalorieGoal.toString())
        spGoal.setSelection(goalKeys.indexOf(user.goal).coerceAtLeast(0))
        spDiet.setSelection(dietKeys.indexOf(user.dietPreference).coerceAtLeast(0))
        spActivity.setSelection(actKeys.indexOf(user.activityLevel).coerceAtLeast(0))

        // Goal date
        var selectedGoalDate = user.goalTargetDate
        if (selectedGoalDate.isNotEmpty()) {
            btnGoalDate.text = "Goal date: $selectedGoalDate"
        }
        btnGoalDate.setOnClickListener {
            val cal = java.util.Calendar.getInstance()
            if (selectedGoalDate.isNotEmpty()) {
                try {
                    val parts = selectedGoalDate.split("-")
                    cal.set(parts[0].toInt(), parts[1].toInt() - 1, parts[2].toInt())
                } catch (e: Exception) { /* use today */ }
            } else {
                cal.add(java.util.Calendar.MONTH, 3)
            }
            android.app.DatePickerDialog(requireContext(), { _, y, m, d ->
                selectedGoalDate = "%04d-%02d-%02d".format(y, m+1, d)
                btnGoalDate.text = "Goal date: $selectedGoalDate"
            }, cal.get(java.util.Calendar.YEAR), cal.get(java.util.Calendar.MONTH), cal.get(java.util.Calendar.DAY_OF_MONTH)).show()
        }

        AlertDialog.Builder(requireContext()).setTitle("Edit Preferences").setView(dv)
            .setPositiveButton("Save") { _, _ ->
                Validator.heightCm(etHeight.text.toString())?.let { Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show(); return@setPositiveButton }
                Validator.weightKg(etWeight.text.toString())?.let { Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show(); return@setPositiveButton }
                Validator.targetWeightKg(etTarget.text.toString())?.let { Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show(); return@setPositiveButton }
                Validator.stepGoal(etSteps.text.toString())?.let { Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show(); return@setPositiveButton }
                val calRaw = etCalGoal.text.toString().trim()
                if (calRaw.isNotEmpty()) {
                    Validator.calorieGoal(calRaw)?.let { Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show(); return@setPositiveButton }
                }
                val h    = etHeight.text.toString().trim().toFloatOrNull() ?: user.heightCm
                val w    = etWeight.text.toString().trim().toFloatOrNull() ?: user.weightKg
                val t    = etTarget.text.toString().trim().toFloatOrNull() ?: user.targetWeightKg
                val sg   = etSteps.text.toString().trim().toIntOrNull() ?: user.dailyStepGoal
                val goal = goalKeys[spGoal.selectedItemPosition]
                val diet = dietKeys[spDiet.selectedItemPosition]
                val act  = actKeys[spActivity.selectedItemPosition]

                // Calculate calorie goal — priority order:
                // 1. Manual override (if user explicitly typed something different)
                // 2. Weight-loss with goal date → calculated from deficit
                // 3. Standard TDEE-based calculation
                val bmr  = com.fitwell.app.utils.Calc.bmr(w, h, user.age, user.gender)
                val tdee = com.fitwell.app.utils.Calc.tdee(bmr, act)

                val manualCal = etCalGoal.text.toString().toIntOrNull()
                val autoCalGoal = if (goal == "weight_loss" && selectedGoalDate.isNotEmpty()) {
                    com.fitwell.app.utils.WeightLossCalc.dailyCalorieGoal(w, t, selectedGoalDate, tdee)
                } else {
                    com.fitwell.app.utils.Calc.calorieGoal(tdee, goal)
                }
                // Use manual only if user changed it from the current value
                val finalCalGoal = if (manualCal != null && manualCal != user.dailyCalorieGoal) manualCal
                                   else autoCalGoal

                viewLifecycleOwner.lifecycleScope.launch {
                    db.userDao().update(user.copy(
                        heightCm=h, weightKg=w, targetWeightKg=t,
                        dailyStepGoal=sg, goal=goal, dietPreference=diet,
                        activityLevel=act, dailyCalorieGoal=finalCalGoal,
                        goalTargetDate=selectedGoalDate))
                    Toast.makeText(requireContext(), "Preferences updated!", Toast.LENGTH_SHORT).show()
                }
            }.setNegativeButton("Cancel", null).show()
    }


    private fun showEditProfileDialog(user: com.fitwell.app.database.UserEntity) {
        val ctx = requireContext()
        val scroll = android.widget.ScrollView(ctx)
        val root = android.widget.LinearLayout(ctx).apply {
            orientation = android.widget.LinearLayout.VERTICAL; setPadding(48, 24, 48, 32)
        }
        fun field(hint: String, inputType: Int, default: String): com.google.android.material.textfield.TextInputEditText {
            val til = com.google.android.material.textfield.TextInputLayout(ctx,null,
                com.google.android.material.R.attr.textInputOutlinedStyle).apply {
                this.hint = hint
                val lp = android.widget.LinearLayout.LayoutParams(android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                    android.widget.LinearLayout.LayoutParams.WRAP_CONTENT).apply { bottomMargin = 16 }
                layoutParams = lp
            }
            val et = com.google.android.material.textfield.TextInputEditText(til.context).apply {
                this.inputType = inputType; setText(default)
                layoutParams = android.widget.LinearLayout.LayoutParams(android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                    android.widget.LinearLayout.LayoutParams.WRAP_CONTENT)
            }
            til.addView(et); root.addView(til)
            return et
        }
        val etName  = field("Full Name", android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_FLAG_CAP_WORDS, user.name)
        val etEmail = field("Email", android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS, user.email)
        val etNewPass = field("New Password (leave blank to keep)", android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD, "")
        val etConfPass = field("Confirm New Password", android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD, "")
        scroll.addView(root)
        android.app.AlertDialog.Builder(ctx).setTitle("Edit Profile").setView(scroll)
            .setPositiveButton("Save") { _, _ ->
                val newName  = etName.text.toString()
                val newEmail = etEmail.text.toString()
                val newPass  = etNewPass.text.toString()
                val confPass = etConfPass.text.toString()
                // Name validation
                com.fitwell.app.utils.Validator.fullName(newName)?.let {
                    android.widget.Toast.makeText(ctx, it, android.widget.Toast.LENGTH_LONG).show(); return@setPositiveButton }
                // Email validation
                com.fitwell.app.utils.Validator.email(newEmail)?.let {
                    android.widget.Toast.makeText(ctx, it, android.widget.Toast.LENGTH_LONG).show(); return@setPositiveButton }
                // Password: check match FIRST, then strength
                if (newPass.isNotEmpty() || confPass.isNotEmpty()) {
                    com.fitwell.app.utils.Validator.passwordsMatch(newPass, confPass)?.let {
                        android.widget.Toast.makeText(ctx, it, android.widget.Toast.LENGTH_LONG).show(); return@setPositiveButton }
                    com.fitwell.app.utils.Validator.password(newPass)?.let {
                        android.widget.Toast.makeText(ctx, it, android.widget.Toast.LENGTH_LONG).show(); return@setPositiveButton }
                }
                val trimmedName  = newName.trim()
                val trimmedEmail = newEmail.trim().lowercase()
                viewLifecycleOwner.lifecycleScope.launch {
                    // Check duplicate email (only if changed)
                    if (trimmedEmail != user.email) {
                        val existing = db.userDao().getByEmail(trimmedEmail)
                        if (existing != null && existing.id != user.id) {
                            android.widget.Toast.makeText(ctx, "This email is already registered to another account", android.widget.Toast.LENGTH_LONG).show()
                            return@launch
                        }
                    }
                    val hash = if (newPass.isNotEmpty()) com.fitwell.app.utils.Crypto.hash(newPass) else user.passwordHash
                    db.userDao().update(user.copy(name=trimmedName, email=trimmedEmail, passwordHash=hash))
                    android.widget.Toast.makeText(ctx, "Profile updated!", android.widget.Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null).show()
    }

}
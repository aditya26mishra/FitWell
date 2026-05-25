package com.fitwell.app.fragments

import android.app.AlertDialog
import android.content.*
import android.os.*
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.fitwell.app.R
import com.fitwell.app.adapters.WorkoutLogAdapter
import com.fitwell.app.database.*
import com.fitwell.app.services.StepService
import com.fitwell.app.utils.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

// ── Calorie Home ──────────────────────────────────────────────────────────────
class CalorieHomeFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, c: ViewGroup?, s: Bundle?): View =
        inflater.inflate(R.layout.fragment_calorie_home, c, false)

    override fun onViewCreated(view: View, s: Bundle?) {
        val db    = AppDatabase.get(requireContext())
        val uid   = Session(requireContext()).userId
        val today = Dates.today()

        val tvConsumed   = view.findViewById<TextView>(R.id.tvCalConsumed)
        val tvGoal       = view.findViewById<TextView>(R.id.tvCalGoal)
        val tvRemaining  = view.findViewById<TextView>(R.id.tvCalRemaining)
        val progressCal  = view.findViewById<ProgressBar>(R.id.progressCal)
        val tvProtein    = view.findViewById<TextView>(R.id.tvMacroProtein)
        val tvCarbs      = view.findViewById<TextView>(R.id.tvMacroCarbs)
        val tvFat        = view.findViewById<TextView>(R.id.tvMacroFat)
        val pbProtein    = view.findViewById<ProgressBar>(R.id.pbHomeProtein)
        val pbCarbs      = view.findViewById<ProgressBar>(R.id.pbHomeCarbs)
        val pbFat        = view.findViewById<ProgressBar>(R.id.pbHomeFat)
        val tvBurned     = view.findViewById<TextView>(R.id.tvCalBurned)
        val homeCalCard  = view.findViewById<View>(R.id.homeCalorieCard)
        val microContainer = view.findViewById<LinearLayout>(R.id.homeMicroContainer)

        // Tap calorie card → switch to Nutrition tab
        homeCalCard.setOnClickListener {
            val bottomNav = requireActivity().findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottomNav)
            bottomNav.selectedItemId = R.id.nutritionFragment
        }

        viewLifecycleOwner.lifecycleScope.launch {
            db.userDao().getByIdFlow(uid).collectLatest { user ->
                user ?: return@collectLatest
                val g = user.dailyCalorieGoal
                tvGoal.text = "of $g kcal goal"
                progressCal.max = g

                val proteinRdi = RDI.protein(user.weightKg, user.goal)
                val carbsRdi   = RDI.carbs(g)
                val fatRdi     = RDI.fat(g)
                pbProtein.max = proteinRdi
                pbCarbs.max   = carbsRdi
                pbFat.max     = fatRdi

                launch {
                    db.foodLogDao().getForDate(uid, today).collectLatest { logs ->
                        val cal   = logs.sumOf { it.calories.toDouble() }.toInt()
                        val prot  = logs.sumOf { it.protein.toDouble() }.toFloat()
                        val carb  = logs.sumOf { it.carbs.toDouble() }.toFloat()
                        val fat   = logs.sumOf { it.fat.toDouble() }.toFloat()
                        val vitA  = logs.sumOf { it.vitaminA.toDouble() }.toFloat()
                        val vitC  = logs.sumOf { it.vitaminC.toDouble() }.toFloat()
                        val vitD  = logs.sumOf { it.vitaminD.toDouble() }.toFloat()
                        val vitB12= logs.sumOf { it.vitaminB12.toDouble() }.toFloat()
                        val calcium=logs.sumOf { it.calcium.toDouble() }.toFloat()
                        val iron  = logs.sumOf { it.iron.toDouble() }.toFloat()
                        val potassium=logs.sumOf { it.potassium.toDouble() }.toFloat()
                        val magnesium=logs.sumOf { it.magnesium.toDouble() }.toFloat()
                        val zinc  = logs.sumOf { it.zinc.toDouble() }.toFloat()

                        tvConsumed.text  = "$cal"
                        tvRemaining.text = "${(g - cal).coerceAtLeast(0)} kcal remaining"
                        progressCal.progress = cal.coerceAtMost(g)
                        tvProtein.text   = "${prot.toInt()}g"
                        tvCarbs.text     = "${carb.toInt()}g"
                        tvFat.text       = "${fat.toInt()}g"
                        pbProtein.progress = prot.toInt().coerceAtMost(proteinRdi)
                        pbCarbs.progress   = carb.toInt().coerceAtMost(carbsRdi)
                        pbFat.progress     = fat.toInt().coerceAtMost(fatRdi)

                        // Build micro rows
                        microContainer.removeAllViews()
                        val ctx = requireContext()
                        listOf(
                            Triple("Vitamin A", vitA, RDI.vitaminA.toFloat()),
                            Triple("Vitamin C", vitC, RDI.vitaminC.toFloat()),
                            Triple("Vitamin D", vitD, RDI.vitaminD.toFloat()),
                            Triple("Calcium", calcium, RDI.calcium.toFloat()),
                            Triple("Iron", iron, RDI.iron.toFloat()),
                            Triple("Potassium", potassium, RDI.potassium.toFloat()),
                            Triple("Magnesium", magnesium, RDI.magnesium.toFloat()),
                            Triple("Zinc", zinc, RDI.zinc.toFloat())
                        ).forEach { (name, consumed, rdi) ->
                            val pct = if (rdi > 0) (consumed / rdi * 100).toInt().coerceAtMost(100) else 0
                            val color = when { pct >= 80 -> R.color.colorGreen; pct >= 40 -> R.color.colorOrange; else -> R.color.colorRed }
                            val status = when { pct >= 80 -> "✓"; pct >= 40 -> "~"; else -> "↓" }
                            val row = LinearLayout(ctx).apply { orientation = LinearLayout.VERTICAL; setPadding(0,10,0,10) }
                            val labelRow = LinearLayout(ctx).apply { orientation = LinearLayout.HORIZONTAL; gravity = android.view.Gravity.CENTER_VERTICAL }
                            labelRow.addView(TextView(ctx).apply {
                                text = name; textSize = 12f; setTextColor(ctx.getColor(R.color.textPrimary))
                                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                            })
                            labelRow.addView(TextView(ctx).apply {
                                text = "${consumed.toInt()}/${rdi.toInt()}  $status"
                                textSize = 11f; setTextColor(ctx.getColor(color))
                                typeface = android.graphics.Typeface.create("sans-serif-medium", android.graphics.Typeface.NORMAL)
                            })
                            row.addView(labelRow)
                            row.addView(ProgressBar(ctx, null, android.R.attr.progressBarStyleHorizontal).apply {
                                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 6).apply { topMargin = 3 }
                                max = 100; progress = pct
                                progressDrawable = ctx.getDrawable(when { pct >= 80 -> R.drawable.progress_green; pct >= 40 -> R.drawable.progress_orange; else -> R.drawable.progress_red })
                            })
                            microContainer.addView(row)
                        }
                    }
                }
                launch {
                    // Combine workout calories + step calories
                    db.workoutLogDao().getBurnedForDate(uid, today).collectLatest { workoutBurned ->
                        val stepLog = db.stepLogDao().getForDate(uid, today)
                        val stepBurned = stepLog?.caloriesBurned?.toInt() ?: 0
                        val total = (workoutBurned?.toInt() ?: 0) + stepBurned
                        tvBurned.text = "${total} kcal burned"
                    }
                }
            }
        }
    }
}


// ── Step Fragment ─────────────────────────────────────────────────────────────
class StepFragment : Fragment() {
    private var service: StepService? = null
    private var bound = false
    private var uid = -1
    private var currentGoal = 10000  // track goal locally for live % update
    private lateinit var db: AppDatabase

    private lateinit var tvSteps: TextView
    private lateinit var tvGoal: TextView
    private lateinit var tvDist: TextView
    private lateinit var tvCal: TextView
    private lateinit var tvPct: TextView
    private lateinit var progressSteps: ProgressBar
    private lateinit var tvMotivation: TextView

    private val conn = object : ServiceConnection {
        override fun onServiceConnected(n: ComponentName?, b: IBinder?) {
            service = (b as StepService.LocalBinder).service()
            bound = true
            service?.setCallback { steps -> requireActivity().runOnUiThread { updateUI(steps) } }
        }
        override fun onServiceDisconnected(n: ComponentName?) { bound = false }
    }

    override fun onCreateView(inflater: LayoutInflater, c: ViewGroup?, s: Bundle?): View =
        inflater.inflate(R.layout.fragment_steps, c, false)

    override fun onViewCreated(view: View, s: Bundle?) {
        db = AppDatabase.get(requireContext())
        uid = Session(requireContext()).userId

        tvSteps = view.findViewById(R.id.tvSteps)
        tvGoal = view.findViewById(R.id.tvStepGoal)
        tvDist = view.findViewById(R.id.tvDistance)
        tvCal = view.findViewById(R.id.tvStepCal)
        tvPct = view.findViewById(R.id.tvStepPct)
        progressSteps = view.findViewById(R.id.progressSteps)
        tvMotivation = view.findViewById(R.id.tvMotivation)

        val etGoal = view.findViewById<EditText>(R.id.etNewGoal)
        val btnSetGoal = view.findViewById<Button>(R.id.btnSetGoal)

        // Observe goal changes live — updates progressBar max and % instantly
        viewLifecycleOwner.lifecycleScope.launch {
            db.userDao().getByIdFlow(uid).collectLatest { user ->
                user ?: return@collectLatest
                currentGoal = user.dailyStepGoal
                progressSteps.max = currentGoal
                tvGoal.text = "Goal: ${"%,d".format(currentGoal)} steps"
                // Re-render current step count with new goal
                service?.currentSteps?.let { updateUI(it) }
            }
        }

        btnSetGoal.setOnClickListener {
            val err = Validator.stepGoal(etGoal.text.toString())
            if (err != null) {
                Toast.makeText(requireContext(), err, Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            val g = etGoal.text.toString().trim().toInt()
            viewLifecycleOwner.lifecycleScope.launch {
                val user = db.userDao().getById(uid) ?: return@launch
                db.userDao().update(user.copy(dailyStepGoal = g))
                etGoal.text.clear()
                Toast.makeText(requireContext(), "Goal updated!", Toast.LENGTH_SHORT).show()
                // currentGoal and UI update happen via the Flow observer above
            }
        }
    }

    private fun updateUI(steps: Int) {
        viewLifecycleOwner.lifecycleScope.launch {
            val user = db.userDao().getById(uid) ?: return@launch
            val goal = user.dailyStepGoal
            currentGoal = goal
            val dist = Calc.stepsToKm(steps, user.heightCm)
            val cal = Calc.stepsToKcal(steps, user.weightKg)
            val pct = if (goal > 0) ((steps.toFloat() / goal) * 100).toInt().coerceAtMost(100) else 0

            tvSteps.text = "%,d".format(steps)
            tvDist.text = "%.2f km".format(dist)
            tvCal.text = "${cal.toInt()} kcal"
            tvPct.text = "$pct%"
            progressSteps.max = goal
            progressSteps.progress = steps

            tvMotivation.text = when {
                pct >= 100 -> "Goal achieved! Keep going!"
                pct >= 75  -> "Almost there! ${"%,d".format(goal - steps)} steps to go!"
                pct >= 50  -> "Halfway done! Keep it up!"
                pct >= 25  -> "Good start! ${"%,d".format(goal - steps)} more steps!"
                else       -> "Let's get moving! Target: ${"%,d".format(goal)} steps"
            }

            val today = Dates.today()
            val existing = db.stepLogDao().getForDate(uid, today)
            val log = StepLogEntity(id = existing?.id ?: 0, userId = uid, date = today,
                steps = steps, goalSteps = goal, distanceKm = dist, caloriesBurned = cal)
            if (existing == null) db.stepLogDao().insert(log) else db.stepLogDao().update(log)
        }
    }

    override fun onStart() {
        super.onStart()
        requireContext().bindService(Intent(requireContext(), StepService::class.java), conn, Context.BIND_AUTO_CREATE)
    }

    override fun onStop() {
        super.onStop()
        if (bound) { service?.clearCallback(); requireContext().unbindService(conn); bound = false }
    }
}

// ── Workout Fragment ──────────────────────────────────────────────────────────
class WorkoutFragment : Fragment() {
    private lateinit var db: AppDatabase
    private var uid = -1

    override fun onCreateView(inflater: LayoutInflater, c: ViewGroup?, s: Bundle?): View =
        inflater.inflate(R.layout.fragment_workout, c, false)

    private var workoutSelectedDate = Dates.today()
    private var workoutLogJob: kotlinx.coroutines.Job? = null
    private var workoutBurnJob: kotlinx.coroutines.Job? = null

    override fun onViewCreated(view: View, s: Bundle?) {
        db = AppDatabase.get(requireContext())
        uid = Session(requireContext()).userId

        val rv = view.findViewById<RecyclerView>(R.id.rvWorkouts)
        val tvCount = view.findViewById<TextView>(R.id.tvWorkoutCount)
        val tvBurned = view.findViewById<TextView>(R.id.tvWorkoutBurned)
        val fab = view.findViewById<ExtendedFloatingActionButton>(R.id.fabAddWorkout)
        val btnSuggest = view.findViewById<Button>(R.id.btnSuggest)

        val adapter = WorkoutLogAdapter { log ->
            viewLifecycleOwner.lifecycleScope.launch { db.workoutLogDao().delete(log) }
        }
        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = adapter

        fun loadWorkouts(date: String) {
            workoutLogJob?.cancel()
            workoutLogJob = viewLifecycleOwner.lifecycleScope.launch {
                db.workoutLogDao().getForDate(uid, date).collectLatest { logs ->
                    adapter.submitList(logs)
                    tvCount.text = "${logs.size} exercise${if (logs.size != 1) "s" else ""} logged"
                }
            }
            workoutBurnJob?.cancel()
            workoutBurnJob = viewLifecycleOwner.lifecycleScope.launch {
                db.workoutLogDao().getBurnedForDate(uid, date).collectLatest { burned ->
                    tvBurned.text = "${burned?.toInt() ?: 0} kcal burned"
                }
            }
        }

        // Build date chips
        val dateRow = view.findViewById<android.widget.LinearLayout>(R.id.workoutDateRow)
        buildWorkoutDateChips(dateRow, 7) { date ->
            workoutSelectedDate = date
            // Cancel in-flight jobs before switching dates
            workoutLogJob?.cancel()
            workoutBurnJob?.cancel()
            loadWorkouts(date)
        }
        loadWorkouts(workoutSelectedDate)

        fab.setOnClickListener { showAddDialog() }
        btnSuggest.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                val user = db.userDao().getById(uid) ?: return@launch
                showBeautifulPlanDialog(
                    title = "Workout Plan",
                    subtitle = user.goal.replace("_", " ").replaceFirstChar { it.uppercase() },
                    items = Recs.workoutPlan(user.goal)
                )
            }
        }
    }

    private fun showBeautifulPlanDialog(title: String, subtitle: String, items: List<String>) {
        val ctx = requireContext()
        val scroll = android.widget.ScrollView(ctx)
        val container = android.widget.LinearLayout(ctx).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(48, 32, 48, 32)
        }

        val tvSub = TextView(ctx).apply {
            text = subtitle
            textSize = 12f
            setTextColor(ctx.getColor(R.color.textSecondary))
            setPadding(0, 0, 0, 16)
        }
        container.addView(tvSub)

        items.forEachIndexed { i, item ->
            val card = android.widget.LinearLayout(ctx).apply {
                orientation = android.widget.LinearLayout.VERTICAL
                setPadding(32, 24, 32, 24)
                background = ctx.getDrawable(R.drawable.bg_card)
                val lp = android.widget.LinearLayout.LayoutParams(
                    android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                    android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { bottomMargin = 16 }
                layoutParams = lp
            }
            val dayLine = item.split(":").firstOrNull()?.trim() ?: ""
            val detail = item.substringAfter(":", "").trim()

            val tvDay = TextView(ctx).apply {
                text = dayLine
                textSize = 13f
                setTextColor(ctx.getColor(R.color.colorPrimary))
                typeface = android.graphics.Typeface.create("sans-serif-medium", android.graphics.Typeface.NORMAL)
            }
            val tvDetail = TextView(ctx).apply {
                text = detail
                textSize = 13f
                setTextColor(ctx.getColor(R.color.textPrimary))
                setPadding(0, 6, 0, 0)
            }
            card.addView(tvDay)
            if (detail.isNotEmpty()) card.addView(tvDetail)
            container.addView(card)
        }

        scroll.addView(container)
        AlertDialog.Builder(ctx)
            .setTitle(title)
            .setView(scroll)
            .setPositiveButton("Got it", null)
            .show()
    }

    private fun buildWorkoutDateChips(container: android.widget.LinearLayout, days: Int, onSelect: (String) -> Unit) {
        container.removeAllViews()
        val ctx = requireContext()
        for (i in 0 until days) {
            val date = Dates.daysAgo(i)
            val label = if (i == 0) "Today" else if (i == 1) "Yesterday" else Dates.weekDay(date) + " " + Dates.chart(date)
            val chip = com.google.android.material.chip.Chip(ctx).apply {
                text = label; isCheckable = true; isChecked = (i == 0)
                setOnClickListener {
                    for (j in 0 until container.childCount)
                        (container.getChildAt(j) as? com.google.android.material.chip.Chip)?.isChecked = false
                    isChecked = true; onSelect(date)
                }
                layoutParams = android.widget.LinearLayout.LayoutParams(
                    android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
                    android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { marginEnd = 8 }
            }
            container.addView(chip)
        }
    }

    private fun showAddDialog() {
        val dv = layoutInflater.inflate(R.layout.dialog_workout, null)
        val etName = dv.findViewById<AutoCompleteTextView>(R.id.etExerciseName)
        val spinnerMuscle = dv.findViewById<Spinner>(R.id.spinnerMuscle)
        val etSets = dv.findViewById<EditText>(R.id.etSets)
        val etReps = dv.findViewById<EditText>(R.id.etReps)
        val etWeight = dv.findViewById<EditText>(R.id.etWeight)
        val etDuration = dv.findViewById<EditText>(R.id.etDuration)
        val etNotes = dv.findViewById<EditText>(R.id.etNotes)

        val muscles = arrayOf("Chest","Back","Legs","Shoulders","Biceps","Triceps","Core","Cardio","Full Body")
        spinnerMuscle.adapter = ArrayAdapter(requireContext(), R.layout.item_spinner, muscles)
        val names = ExerciseDb.items.map { it.name }
        etName.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, names))
        etName.threshold = 1
        etName.setOnItemClickListener { _, _, _, _ ->
            ExerciseDb.items.find { it.name == etName.text.toString() }?.let { ex ->
                etSets.setText(ex.defaultSets.toString())
                etReps.setText(ex.defaultReps.toString())
                val idx = muscles.indexOfFirst { it.equals(ex.muscle, true) }
                if (idx >= 0) spinnerMuscle.setSelection(idx)
            }
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Log Exercise")
            .setView(dv)
            .setPositiveButton("Save") { _, _ ->
                val nameRaw = etName.text.toString().trim()
                if (nameRaw.isEmpty()) { Toast.makeText(requireContext(), "Exercise name cannot be empty", Toast.LENGTH_LONG).show(); return@setPositiveButton }
                val name = nameRaw
                Validator.setsOrReps(etSets.text.toString(), "Sets")?.let {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show(); return@setPositiveButton }
                Validator.setsOrReps(etReps.text.toString(), "Reps")?.let {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show(); return@setPositiveButton }
                Validator.durationMinutes(etDuration.text.toString())?.let {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show(); return@setPositiveButton }
                val sets = etSets.text.toString().trim().toInt()
                val reps = etReps.text.toString().trim().toInt()
                val wt = etWeight.text.toString().toFloatOrNull() ?: 0f
                val dur = etDuration.text.toString().trim().toIntOrNull() ?: 0
                val muscle = muscles[spinnerMuscle.selectedItemPosition]
                val ex = ExerciseDb.items.find { it.name == name }
                val burned = (ex?.kcalPerMin ?: 7f) * dur.coerceAtLeast(1)
                val log = WorkoutLogEntity(userId = uid, date = workoutSelectedDate, exerciseName = name,
                    muscleGroup = muscle, sets = sets, reps = reps, weightKg = wt,
                    durationMinutes = dur, caloriesBurned = burned,
                    notes = etNotes.text.toString(), imageUrl = ex?.imageUrl ?: "")
                viewLifecycleOwner.lifecycleScope.launch { db.workoutLogDao().insert(log) }
            }
            .setNegativeButton("Cancel", null).show()
    }
}

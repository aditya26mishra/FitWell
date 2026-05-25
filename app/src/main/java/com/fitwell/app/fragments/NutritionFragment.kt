package com.fitwell.app.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.fitwell.app.R
import com.fitwell.app.activities.ScanActivity
import com.fitwell.app.adapters.FoodLogAdapter
import com.fitwell.app.adapters.FoodSearchAdapter
import com.fitwell.app.database.*
import com.fitwell.app.utils.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class NutritionFragment : Fragment() {

    private lateinit var db: AppDatabase
    private var uid = -1
    private var selectedDate = Dates.today()

    private lateinit var tvDate: TextView
    private lateinit var tvTotalCal: TextView
    private lateinit var tvCalGoal: TextView
    private lateinit var progressCal: ProgressBar
    private lateinit var mealTabLayout: TabLayout
    private lateinit var rvMeal: RecyclerView
    private lateinit var fab: ExtendedFloatingActionButton
    private lateinit var btnScan: Button
    private lateinit var btnDietPlan: Button
    private lateinit var tvEmptyMeal: TextView

    // Macro progress views
    private lateinit var macroSection: View
    private lateinit var tvMacroProtein: TextView
    private lateinit var tvMacroCarbs: TextView
    private lateinit var tvMacroFat: TextView
    private lateinit var pbProtein: ProgressBar
    private lateinit var pbCarbs: ProgressBar
    private lateinit var pbFat: ProgressBar
    private lateinit var btnToggleMacros: TextView

    // Micro progress views
    private lateinit var microSection: View
    private lateinit var btnToggleMicros: TextView

    private var currentMeal = "breakfast"
    private var mealLoadJob: kotlinx.coroutines.Job? = null
    private var totalsJob: kotlinx.coroutines.Job? = null
    private val meals      = listOf("breakfast","lunch","dinner","snack")
    private val mealLabels = listOf("Breakfast","Lunch","Dinner","Snacks")
    private var macroExpanded = true
    private var microExpanded = false

    // Current user reference for RDI calculations
    private var currentUser: UserEntity? = null

    private lateinit var mealAdapter: FoodLogAdapter

    private val scanLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val d = result.data ?: return@registerForActivityResult
            showAddDialog(
                prefillName   = d.getStringExtra("food_name") ?: "",
                prefillCal    = d.getFloatExtra("calories", 0f),
                prefillProtein= d.getFloatExtra("protein", 0f),
                prefillCarbs  = d.getFloatExtra("carbs", 0f),
                prefillFat    = d.getFloatExtra("fat", 0f),
                prefillFiber  = d.getFloatExtra("fiber", 0f),
                prefillSodium = d.getFloatExtra("sodium", 0f)
            )
        }
    }

    override fun onCreateView(inflater: LayoutInflater, c: ViewGroup?, s: Bundle?): View =
        inflater.inflate(R.layout.fragment_nutrition, c, false)

    override fun onViewCreated(view: View, s: Bundle?) {
        db  = AppDatabase.get(requireContext())
        uid = Session(requireContext()).userId

        tvDate      = view.findViewById(R.id.tvNutritionDate)
        tvTotalCal  = view.findViewById(R.id.tvTotalCal)
        tvCalGoal   = view.findViewById(R.id.tvNutritionGoal)
        progressCal = view.findViewById(R.id.progressNutrition)
        mealTabLayout = view.findViewById(R.id.mealTabLayout)
        rvMeal      = view.findViewById(R.id.rvMeal)
        fab         = view.findViewById(R.id.fabAddFood)
        btnScan     = view.findViewById(R.id.btnScan)
        btnDietPlan = view.findViewById(R.id.btnDietPlan)
        tvEmptyMeal = view.findViewById(R.id.tvEmptyMeal)

        macroSection   = view.findViewById(R.id.macroSection)
        tvMacroProtein = view.findViewById(R.id.tvNutritionProtein)
        tvMacroCarbs   = view.findViewById(R.id.tvNutritionCarbs)
        tvMacroFat     = view.findViewById(R.id.tvNutritionFat)
        pbProtein      = view.findViewById(R.id.pbProtein)
        pbCarbs        = view.findViewById(R.id.pbCarbs)
        pbFat          = view.findViewById(R.id.pbFat)
        btnToggleMacros = view.findViewById(R.id.btnToggleMacros)
        microSection   = view.findViewById(R.id.microSection)
        btnToggleMicros = view.findViewById(R.id.btnToggleMicros)

        tvDate.text = Dates.pretty(selectedDate)

        // Build last-7-days chip row
        val dateRow = view.findViewById<android.widget.LinearLayout>(R.id.nutritionDateRow)
        buildDateChips(dateRow, 7) { date ->
            selectedDate = date
            tvDate.text = Dates.pretty(date)
            // Cancel any in-flight jobs before switching dates
            totalsJob?.cancel()
            mealLoadJob?.cancel()
            loadTotals()
            loadMeal(currentMeal)
        }

        // Tap calorie card → navigate to nutrition tab (already here, so scroll to top)
        view.findViewById<View>(R.id.calorieSummaryCard).setOnClickListener {
            view.findViewById<android.widget.ScrollView>(R.id.nutritionScrollView)
                ?.smoothScrollTo(0, 0)
        }

        // Macro toggle
        btnToggleMacros.setOnClickListener {
            macroExpanded = !macroExpanded
            macroSection.visibility = if (macroExpanded) View.VISIBLE else View.GONE
            btnToggleMacros.text = if (macroExpanded) "▲ Macros" else "▼ Macros"
        }
        btnToggleMicros.setOnClickListener {
            microExpanded = !microExpanded
            microSection.visibility = if (microExpanded) View.VISIBLE else View.GONE
            btnToggleMicros.text = if (microExpanded) "▲ Micronutrients" else "▼ Micronutrients"
        }

        // Meal tabs
        mealLabels.forEach { mealTabLayout.addTab(mealTabLayout.newTab().setText(it)) }
        mealTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) { currentMeal = meals[tab.position]; mealLoadJob?.cancel(); loadMeal(currentMeal) }
            override fun onTabUnselected(t: TabLayout.Tab) {}
            override fun onTabReselected(t: TabLayout.Tab) {}
        })

        mealAdapter = FoodLogAdapter(
            onDelete        = { log -> viewLifecycleOwner.lifecycleScope.launch { db.foodLogDao().delete(log) } },
            onMicroClick    = { log -> showMicroDialog(log) },
            onQuantityChange = { log, newQty -> updateQuantity(log, newQty) }
        )
        rvMeal.layoutManager = LinearLayoutManager(requireContext())
        rvMeal.adapter = mealAdapter
        rvMeal.isNestedScrollingEnabled = false

        loadTotals()
        loadMeal(currentMeal)

        fab.setOnClickListener { showAddDialog() }
        btnScan.setOnClickListener { scanLauncher.launch(Intent(requireContext(), ScanActivity::class.java)) }
        btnDietPlan.setOnClickListener { showDietPlanDialog() }
    }

    private fun updateQuantity(log: FoodLogEntity, newQty: Float) {
        viewLifecycleOwner.lifecycleScope.launch {
            // Always scale proportionally from the CURRENTLY STORED values.
            // The logged entry already has the correct macros/micros for log.quantity
            // at whatever serving size the user originally chose — we must never
            // reset back to the database standard serving.
            val oldQty = log.quantity.coerceAtLeast(0.001f)
            val factor = newQty / oldQty   // e.g. going 1→2 gives factor=2.0

            val updated = log.copy(
                quantity   = newQty,
                calories   = log.calories   * factor,
                protein    = log.protein    * factor,
                carbs      = log.carbs      * factor,
                fat        = log.fat        * factor,
                fiber      = log.fiber      * factor,
                sugar      = log.sugar      * factor,
                sodium     = log.sodium     * factor,
                vitaminA   = log.vitaminA   * factor,
                vitaminC   = log.vitaminC   * factor,
                vitaminD   = log.vitaminD   * factor,
                vitaminB12 = log.vitaminB12 * factor,
                calcium    = log.calcium    * factor,
                iron       = log.iron       * factor,
                potassium  = log.potassium  * factor,
                magnesium  = log.magnesium  * factor,
                zinc       = log.zinc       * factor
                // unit stays unchanged — the serving size label is preserved
            )
            db.foodLogDao().update(updated)
        }
    }

    private fun loadTotals() {
        totalsJob?.cancel()
        totalsJob = viewLifecycleOwner.lifecycleScope.launch {
            db.userDao().getByIdFlow(uid).collectLatest { user ->
                user ?: return@collectLatest
                currentUser = user
                val goal = user.dailyCalorieGoal
                tvCalGoal.text = "$goal kcal"
                progressCal.max = goal

                val proteinGoal  = RDI.protein(user.weightKg, user.goal)
                val carbsGoal    = RDI.carbs(goal)
                val fatGoal      = RDI.fat(goal)
                pbProtein.max = proteinGoal
                pbCarbs.max   = carbsGoal
                pbFat.max     = fatGoal

                launch {
                    db.foodLogDao().getForDate(uid, selectedDate).collectLatest { logs ->
                        val cal   = logs.sumOf { it.calories.toDouble() }.toInt()
                        val prot  = logs.sumOf { it.protein.toDouble() }.toFloat()
                        val carb  = logs.sumOf { it.carbs.toDouble() }.toFloat()
                        val fat   = logs.sumOf { it.fat.toDouble() }.toFloat()
                        val fiber = logs.sumOf { it.fiber.toDouble() }.toFloat()
                        val sugar = logs.sumOf { it.sugar.toDouble() }.toFloat()
                        val sodium= logs.sumOf { it.sodium.toDouble() }.toFloat()
                        val vitA  = logs.sumOf { it.vitaminA.toDouble() }.toFloat()
                        val vitC  = logs.sumOf { it.vitaminC.toDouble() }.toFloat()
                        val vitD  = logs.sumOf { it.vitaminD.toDouble() }.toFloat()
                        val vitB12= logs.sumOf { it.vitaminB12.toDouble() }.toFloat()
                        val calcium= logs.sumOf { it.calcium.toDouble() }.toFloat()
                        val iron  = logs.sumOf { it.iron.toDouble() }.toFloat()
                        val potassium= logs.sumOf { it.potassium.toDouble() }.toFloat()
                        val magnesium= logs.sumOf { it.magnesium.toDouble() }.toFloat()
                        val zinc  = logs.sumOf { it.zinc.toDouble() }.toFloat()

                        tvTotalCal.text = "$cal"
                        progressCal.progress = cal.coerceAtMost(goal)

                        // Macros with progress
                        tvMacroProtein.text = "${prot.toInt()}g / ${proteinGoal}g"
                        tvMacroCarbs.text   = "${carb.toInt()}g / ${carbsGoal}g"
                        tvMacroFat.text     = "${fat.toInt()}g / ${fatGoal}g"
                        pbProtein.progress  = prot.toInt().coerceAtMost(proteinGoal)
                        pbCarbs.progress    = carb.toInt().coerceAtMost(carbsGoal)
                        pbFat.progress      = fat.toInt().coerceAtMost(fatGoal)

                        // Micros — update the dynamic micro section
                        updateMicroSection(
                            view ?: return@collectLatest,
                            mapOf(
                                "Vitamin A" to Pair(vitA, RDI.vitaminA.toFloat()),
                                "Vitamin C" to Pair(vitC, RDI.vitaminC.toFloat()),
                                "Vitamin D" to Pair(vitD, RDI.vitaminD.toFloat()),
                                "Vitamin B12" to Pair(vitB12, RDI.vitaminB12),
                                "Calcium" to Pair(calcium, RDI.calcium.toFloat()),
                                "Iron" to Pair(iron, RDI.iron.toFloat()),
                                "Potassium" to Pair(potassium, RDI.potassium.toFloat()),
                                "Magnesium" to Pair(magnesium, RDI.magnesium.toFloat()),
                                "Zinc" to Pair(zinc, RDI.zinc.toFloat()),
                                "Fiber" to Pair(fiber, RDI.fiber().toFloat()),
                                "Sugar" to Pair(sugar, RDI.sugar().toFloat()),
                                "Sodium" to Pair(sodium, RDI.sodium.toFloat())
                            )
                        )
                    }
                }
            }
        }
    }

    private fun updateMicroSection(root: View, micros: Map<String, Pair<Float, Float>>) {
        val container = root.findViewById<LinearLayout>(R.id.microContainer) ?: return
        container.removeAllViews()
        val ctx = requireContext()
        for ((name, pair) in micros) {
            val consumed = pair.first
            val rdi      = pair.second
            val pct      = if (rdi > 0) (consumed / rdi * 100).toInt().coerceAtMost(100) else 0
            val status   = when { pct >= 80 -> "✓" ; pct >= 40 -> "~" ; else -> "↓" }
            val color    = when { pct >= 80 -> R.color.colorGreen ; pct >= 40 -> R.color.colorOrange ; else -> R.color.colorRed }

            val row = LinearLayout(ctx).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(0, 10, 0, 10)
            }
            val labelRow = LinearLayout(ctx).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = android.view.Gravity.CENTER_VERTICAL
            }
            val tvLabel = TextView(ctx).apply {
                text = name; textSize = 12f
                setTextColor(ctx.getColor(R.color.textPrimary))
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }
            val tvVal = TextView(ctx).apply {
                text = "${consumed.toInt()}/${rdi.toInt()}"
                textSize = 11f; setTextColor(ctx.getColor(R.color.textSecondary))
                setPadding(0,0,8,0)
            }
            val tvStatus = TextView(ctx).apply {
                text = status; textSize = 13f; setTextColor(ctx.getColor(color))
                typeface = android.graphics.Typeface.create("sans-serif-medium", android.graphics.Typeface.NORMAL)
            }
            labelRow.addView(tvLabel); labelRow.addView(tvVal); labelRow.addView(tvStatus)
            val pb = ProgressBar(ctx, null, android.R.attr.progressBarStyleHorizontal).apply {
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 8)
                    .apply { topMargin = 4 }
                max = 100; progress = pct
                progressDrawable = ctx.getDrawable(
                    if (pct >= 80) R.drawable.progress_green
                    else if (pct >= 40) R.drawable.progress_orange
                    else R.drawable.progress_accent
                )
            }
            row.addView(labelRow); row.addView(pb)
            container.addView(row)
            // divider
            val div = View(ctx).apply {
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1).apply { topMargin = 2 }
                setBackgroundColor(ctx.getColor(R.color.divider))
            }
            container.addView(div)
        }
    }

    private fun loadMeal(meal: String) {
        // Cancel any previous meal-loading job before starting a new one
        mealLoadJob?.cancel()
        mealLoadJob = viewLifecycleOwner.lifecycleScope.launch {
            db.foodLogDao().getForMeal(uid, selectedDate, meal).collectLatest { logs ->
                mealAdapter.submitList(logs)
                tvEmptyMeal.visibility = if (logs.isEmpty()) View.VISIBLE else View.GONE
            }
        }
    }

    private fun showMicroDialog(log: FoodLogEntity) {
        val ctx = requireContext()
        val scroll = android.widget.ScrollView(ctx)
        val root = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL; setPadding(48, 24, 48, 32)
        }
        fun section(title: String) {
            root.addView(TextView(ctx).apply {
                text = title; textSize = 11f; letterSpacing = 0.1f
                setTextColor(ctx.getColor(R.color.textSecondary)); setPadding(0, 16, 0, 8)
            })
        }
        fun row(label: String, value: String) {
            val r = LinearLayout(ctx).apply { orientation = LinearLayout.HORIZONTAL; setPadding(0,6,0,6) }
            r.addView(TextView(ctx).apply {
                text = label; textSize = 13f; setTextColor(ctx.getColor(R.color.textSecondary))
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            })
            r.addView(TextView(ctx).apply {
                text = value; textSize = 13f; setTextColor(ctx.getColor(R.color.textPrimary))
                typeface = android.graphics.Typeface.create("sans-serif-medium", android.graphics.Typeface.NORMAL)
            })
            root.addView(r)
            root.addView(View(ctx).apply {
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1)
                setBackgroundColor(ctx.getColor(R.color.divider))
            })
        }
        // Header: quantity separate from calories
        root.addView(TextView(ctx).apply {
            text = log.foodName; textSize = 16f
            setTextColor(ctx.getColor(R.color.colorPrimary))
            typeface = android.graphics.Typeface.create("sans-serif-medium", android.graphics.Typeface.NORMAL)
            setPadding(0,0,0,4)
        })
        root.addView(TextView(ctx).apply {
            text = "Quantity: ×${log.quantity.toInt()} (${log.unit})"; textSize = 13f
            setTextColor(ctx.getColor(R.color.textSecondary)); setPadding(0,0,0,4)
        })
        root.addView(TextView(ctx).apply {
            text = "Total Calories: ${log.calories.toInt()} kcal"; textSize = 14f
            setTextColor(ctx.getColor(R.color.accent))
            typeface = android.graphics.Typeface.create("sans-serif-medium", android.graphics.Typeface.NORMAL)
            setPadding(0,0,0,8)
        })
        section("MACRONUTRIENTS")
        row("Protein",        "${log.protein.toInt()}g")
        row("Carbohydrates",  "${log.carbs.toInt()}g")
        row("Fat",            "${log.fat.toInt()}g")
        row("Fiber",          "${log.fiber.toInt()}g")
        row("Sugar",          "${log.sugar.toInt()}g")
        row("Sodium",         "${log.sodium.toInt()}mg")
        val hasMicros = log.vitaminA>0||log.vitaminC>0||log.vitaminD>0||log.vitaminB12>0||
            log.calcium>0||log.iron>0||log.potassium>0||log.magnesium>0||log.zinc>0
        if (hasMicros) {
            section("MICRONUTRIENTS")
            if (log.vitaminA  > 0) row("Vitamin A",  "${log.vitaminA.toInt()} mcg")
            if (log.vitaminC  > 0) row("Vitamin C",  "${log.vitaminC.toInt()} mg")
            if (log.vitaminD  > 0) row("Vitamin D",  "${log.vitaminD.toInt()} mcg")
            if (log.vitaminB12> 0) row("Vitamin B12","${log.vitaminB12} mcg")
            if (log.calcium   > 0) row("Calcium",    "${log.calcium.toInt()} mg")
            if (log.iron      > 0) row("Iron",        "${log.iron} mg")
            if (log.potassium > 0) row("Potassium",  "${log.potassium.toInt()} mg")
            if (log.magnesium > 0) row("Magnesium",  "${log.magnesium.toInt()} mg")
            if (log.zinc      > 0) row("Zinc",       "${log.zinc} mg")
        }
        scroll.addView(root)
        AlertDialog.Builder(ctx).setTitle("Nutrition Details").setView(scroll).setPositiveButton("Close", null).show()
    }

    private fun showDietPlanDialog() {
        viewLifecycleOwner.lifecycleScope.launch {
            val user = db.userDao().getById(uid) ?: return@launch
            val ctx  = requireContext()
            val scroll = android.widget.ScrollView(ctx)
            val container = LinearLayout(ctx).apply { orientation = LinearLayout.VERTICAL; setPadding(48,24,48,32) }
            // Goal-date calorie note for weight loss
            if (user.goal == "weight_loss" && user.goalTargetDate.isNotEmpty()) {
                val days = WeightLossCalc.daysUntil(user.goalTargetDate)
                container.addView(androidx.cardview.widget.CardView(ctx).apply {
                    radius = 24f; cardElevation = 4f
                    setCardBackgroundColor(ctx.getColor(R.color.colorGreenLight))
                    val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { bottomMargin = 16 }
                    layoutParams = lp
                    addView(TextView(ctx).apply {
                        text = "Goal: reach ${user.targetWeightKg}kg by ${user.goalTargetDate}" +
                            if (days > 0) " ($days days left)" else " (overdue)"
                        textSize = 13f; setTextColor(ctx.getColor(R.color.colorGreen))
                        typeface = android.graphics.Typeface.create("sans-serif-medium", android.graphics.Typeface.NORMAL)
                        setPadding(32, 20, 32, 20)
                    })
                })
                container.addView(TextView(ctx).apply {
                    text = "Daily Calorie Target: ${user.dailyCalorieGoal} kcal"
                    textSize = 14f; setTextColor(ctx.getColor(R.color.textPrimary))
                    typeface = android.graphics.Typeface.create("sans-serif-medium", android.graphics.Typeface.NORMAL)
                    setPadding(0,0,0,12)
                })
            }
            Recs.dietPlan(user.goal, user.dietPreference, user.dailyCalorieGoal).forEach { item ->
                val card = LinearLayout(ctx).apply {
                    orientation = LinearLayout.VERTICAL; setPadding(32,20,32,20)
                    background = ctx.getDrawable(R.drawable.bg_card)
                    val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { bottomMargin = 10 }
                    layoutParams = lp
                }
                val colonIdx = item.indexOf(":")
                val title  = if (colonIdx > 0) item.substring(0, colonIdx).trim() else item
                val detail = if (colonIdx > 0) item.substring(colonIdx+1).trim() else ""
                card.addView(TextView(ctx).apply {
                    text = title; textSize = 13f; setTextColor(ctx.getColor(R.color.colorGreen))
                    typeface = android.graphics.Typeface.create("sans-serif-medium", android.graphics.Typeface.NORMAL)
                })
                if (detail.isNotEmpty()) card.addView(TextView(ctx).apply {
                    text = detail; textSize = 13f; setTextColor(ctx.getColor(R.color.textPrimary)); setPadding(0,6,0,0)
                })
                container.addView(card)
            }
            scroll.addView(container)
            AlertDialog.Builder(ctx).setTitle("Your Diet Plan").setView(scroll).setPositiveButton("Got it", null).show()
        }
    }

    private fun showAddDialog(
        prefillName: String = "", prefillCal: Float = 0f, prefillProtein: Float = 0f,
        prefillCarbs: Float = 0f, prefillFat: Float = 0f, prefillFiber: Float = 0f, prefillSodium: Float = 0f
    ) {
        val dv = layoutInflater.inflate(R.layout.dialog_add_food, null)
        val etSearch      = dv.findViewById<EditText>(R.id.etFoodSearch)
        val rvSearch      = dv.findViewById<RecyclerView>(R.id.rvSearchResults)
        val etName        = dv.findViewById<EditText>(R.id.etFoodName)
        val etQty         = dv.findViewById<EditText>(R.id.etQty)
        val etServingUnit = dv.findViewById<EditText>(R.id.etServingUnit)
        val etCal         = dv.findViewById<EditText>(R.id.etCal)
        val etProt        = dv.findViewById<EditText>(R.id.etProtein)
        val etCarb        = dv.findViewById<EditText>(R.id.etCarbs)
        val etFat         = dv.findViewById<EditText>(R.id.etFat)
        val etFiber       = dv.findViewById<EditText>(R.id.etFiber)
        val spinnerMeal   = dv.findViewById<Spinner>(R.id.spinnerMeal)
        val tvPerServing  = dv.findViewById<TextView>(R.id.tvPerServing)

        spinnerMeal.adapter = ArrayAdapter(requireContext(), R.layout.item_spinner, mealLabels)
        spinnerMeal.setSelection(meals.indexOf(currentMeal).coerceAtLeast(0))

        if (prefillName.isNotEmpty()) {
            etName.setText(prefillName); etCal.setText(prefillCal.toInt().toString())
            etProt.setText(prefillProtein.toInt().toString()); etCarb.setText(prefillCarbs.toInt().toString())
            etFat.setText(prefillFat.toInt().toString()); etFiber.setText(prefillFiber.toInt().toString())
            etQty.setText("1"); etServingUnit.setText("serving")
        }

        var selectedFood: FoodItem? = null
        // Base macros per 1 standard serving (set when food is selected, used as reference for scaling)
        var baseKcalPerServing = 0f
        var baseProteinPerServing = 0f
        var baseCarbsPerServing = 0f
        var baseFatPerServing = 0f
        var baseFiberPerServing = 0f
        var baseSugarPerServing = 0f
        var baseSodiumPerServing = 0f
        var baseVitA = 0f; var baseVitC = 0f; var baseVitD = 0f; var baseVitB12 = 0f
        var baseCa = 0f; var baseFe = 0f; var baseK = 0f; var baseMg = 0f; var baseZn = 0f
        var stdServingGrams = 100f // grams in one standard serving of the selected food

        val searchAdapter = FoodSearchAdapter { food ->
            selectedFood = food
            stdServingGrams = food.serving
            // Store per-serving reference values
            baseKcalPerServing = food.kcal; baseProteinPerServing = food.protein
            baseCarbsPerServing = food.carbs; baseFatPerServing = food.fat
            baseFiberPerServing = food.fiber; baseSugarPerServing = food.sugar
            baseSodiumPerServing = food.sodium
            baseVitA = food.vitA; baseVitC = food.vitC; baseVitD = food.vitD; baseVitB12 = food.vitB12
            baseCa = food.calcium; baseFe = food.iron; baseK = food.potassium
            baseMg = food.magnesium; baseZn = food.zinc

            etName.setText(food.name)
            etQty.setText("1")
            etServingUnit.setText(food.serving.toInt().toString()) // grams per serving (editable)
            etCal.setText(food.kcal.toInt().toString())
            etProt.setText(food.protein.toInt().toString())
            etCarb.setText(food.carbs.toInt().toString())
            etFat.setText(food.fat.toInt().toString())
            etFiber.setText(food.fiber.toInt().toString())
            tvPerServing.text = "Per serving = ${food.serving.toInt()}${food.unit}"
            rvSearch.visibility = View.GONE; etSearch.text.clear()
        }
        rvSearch.layoutManager = LinearLayoutManager(requireContext())
        rvSearch.adapter = searchAdapter

        // Recalculate all macros/micros based on qty × (current serving grams / standard serving grams)
        fun recalcMacros() {
            val food = selectedFood ?: return
            val qty = etQty.text.toString().toFloatOrNull() ?: 1f
            val currentServingGrams = etServingUnit.text.toString().toFloatOrNull() ?: stdServingGrams
            // Scale factor: how many "standard servings" is this?
            val factor = qty * (currentServingGrams / stdServingGrams)
            etCal.setText((baseKcalPerServing * factor).toInt().toString())
            etProt.setText((baseProteinPerServing * factor).toInt().toString())
            etCarb.setText((baseCarbsPerServing * factor).toInt().toString())
            etFat.setText((baseFatPerServing * factor).toInt().toString())
            etFiber.setText((baseFiberPerServing * factor).toInt().toString())
            // Update the serving description
            tvPerServing.text = "${qty.toInt()} × ${currentServingGrams.toInt()}${food.unit} = ${(qty * currentServingGrams).toInt()}${food.unit} total"
        }

        val macroWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { recalcMacros() }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }
        etQty.addTextChangedListener(macroWatcher)
        etServingUnit.addTextChangedListener(macroWatcher)
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val q = s.toString().trim()
                if (q.isNotEmpty()) {
                    val r = FoodDb.search(q); searchAdapter.submitList(r)
                    rvSearch.visibility = if (r.isNotEmpty()) View.VISIBLE else View.GONE
                } else rvSearch.visibility = View.GONE
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        AlertDialog.Builder(requireContext()).setTitle("Log Food").setView(dv)
            .setPositiveButton("Save") { _, _ ->
                // Food name
                val nameRaw = etName.text.toString().trim()
                if (nameRaw.isEmpty()) { Toast.makeText(requireContext(), "Food name cannot be empty", Toast.LENGTH_LONG).show(); return@setPositiveButton }
                val name = nameRaw
                // Quantity
                val qtyErr = Validator.foodQuantity(etQty.text.toString())
                if (qtyErr != null) { Toast.makeText(requireContext(), qtyErr, Toast.LENGTH_LONG).show(); return@setPositiveButton }
                val qty  = etQty.text.toString().trim().toFloat()
                // Serving size
                val sizeErr = Validator.servingSize(etServingUnit.text.toString())
                if (sizeErr != null) { Toast.makeText(requireContext(), sizeErr, Toast.LENGTH_LONG).show(); return@setPositiveButton }
                val unit = "${etServingUnit.text.toString().trim()}g"
                val meal = meals[spinnerMeal.selectedItemPosition]
                val food = selectedFood
                viewLifecycleOwner.lifecycleScope.launch {
                    // Calculate the scale factor based on qty × serving size change
                    val currentServingGrams = etServingUnit.text.toString().toFloatOrNull() ?: stdServingGrams
                    val factor = qty * (currentServingGrams / stdServingGrams.coerceAtLeast(1f))
                    val unitLabel = if (food != null) "${currentServingGrams.toInt()}${food.unit}" else unit

                    // Check for duplicate — if same food in same meal selectedDate, just increment qty
                    val existing = db.foodLogDao().findByNameMealDate(uid, selectedDate, meal, name, unitLabel)
                    if (existing != null) {
                        updateQuantity(existing, existing.quantity + qty)
                        Toast.makeText(requireContext(), "Quantity updated!", Toast.LENGTH_SHORT).show()
                        return@launch
                    }
                    val log = FoodLogEntity(
                        userId=uid, date=selectedDate, mealType=meal, foodName=name, quantity=qty, unit=unitLabel,
                        calories  = etCal.text.toString().toFloatOrNull() ?: 0f,
                        protein   = etProt.text.toString().toFloatOrNull() ?: 0f,
                        carbs     = etCarb.text.toString().toFloatOrNull() ?: 0f,
                        fat       = etFat.text.toString().toFloatOrNull() ?: 0f,
                        fiber     = etFiber.text.toString().toFloatOrNull() ?: 0f,
                        sodium    = if (prefillSodium > 0) prefillSodium else baseSodiumPerServing * factor,
                        sugar     = baseSugarPerServing * factor,
                        vitaminA  = baseVitA * factor,
                        vitaminC  = baseVitC * factor,
                        vitaminD  = baseVitD * factor,
                        vitaminB12= baseVitB12 * factor,
                        calcium   = baseCa * factor,
                        iron      = baseFe * factor,
                        potassium = baseK * factor,
                        magnesium = baseMg * factor,
                        zinc      = baseZn * factor,
                        imageUrl  = food?.imageUrl ?: ""
                    )
                    db.foodLogDao().insert(log)
                    Toast.makeText(requireContext(), "Food logged!", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null).show()
    }

    private fun buildDateChips(container: android.widget.LinearLayout, days: Int, onSelect: (String) -> Unit) {
        container.removeAllViews()
        val ctx = requireContext()
        val today = Dates.today()
        for (i in 0 until days) {
            val date = Dates.daysAgo(i)
            val label = if (i == 0) "Today" else if (i == 1) "Yesterday" else Dates.weekDay(date) + " " + Dates.chart(date)
            val chip = com.google.android.material.chip.Chip(ctx).apply {
                text = label
                isCheckable = true
                isChecked = (date == selectedDate)
                setOnClickListener {
                    // Uncheck siblings
                    for (j in 0 until container.childCount) {
                        (container.getChildAt(j) as? com.google.android.material.chip.Chip)?.isChecked = false
                    }
                    isChecked = true
                    onSelect(date)
                }
                val lp = android.widget.LinearLayout.LayoutParams(
                    android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
                    android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { marginEnd = 8 }
                layoutParams = lp
            }
            container.addView(chip)
        }
    }

}
package com.fitwell.app.fragments

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.github.mikephil.charting.charts.*
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.fitwell.app.R
import com.fitwell.app.database.*
import com.fitwell.app.utils.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ProgressFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, c: ViewGroup?, s: Bundle?): View =
        inflater.inflate(R.layout.fragment_progress, c, false)

    override fun onViewCreated(view: View, s: Bundle?) {
        val vp = view.findViewById<ViewPager2>(R.id.progressViewPager)
        val tabs = view.findViewById<TabLayout>(R.id.progressTabs)
        vp.adapter = ProgressPagerAdapter(this)
        TabLayoutMediator(tabs, vp) { tab, pos ->
            tab.text = when(pos) { 0 -> "Calories"; 1 -> "Steps"; else -> "Workouts" }
        }.attach()
    }
}

class ProgressPagerAdapter(f: Fragment) : FragmentStateAdapter(f) {
    override fun getItemCount() = 3
    override fun createFragment(pos: Int): Fragment = when(pos) {
        0 -> CaloriesChartFragment()
        1 -> StepsChartFragment()
        else -> WorkoutsChartFragment()
    }
}

// ── Shared range options ──────────────────────────────────────────────────────
val RANGE_OPTIONS = arrayOf("7 days", "14 days", "30 days", "60 days", "90 days")
val RANGE_DAYS   = intArrayOf(7, 14, 30, 60, 90)

// ── Calories Chart Tab ────────────────────────────────────────────────────────
class CaloriesChartFragment : Fragment() {
    private var rangeDays = 14

    override fun onCreateView(inflater: LayoutInflater, c: ViewGroup?, s: Bundle?): View =
        inflater.inflate(R.layout.fragment_chart_calories, c, false)

    override fun onViewCreated(view: View, s: Bundle?) {
        val db = AppDatabase.get(requireContext())
        val uid = Session(requireContext()).userId
        val chart = view.findViewById<LineChart>(R.id.lineChartCalories)
        val tvAvg = view.findViewById<TextView>(R.id.tvAvgCalories)
        val tvBest = view.findViewById<TextView>(R.id.tvBestDay)
        val tvStreak = view.findViewById<TextView>(R.id.tvStreak)
        val spinnerRange = view.findViewById<Spinner>(R.id.spinnerRange)

        spinnerRange.adapter = ArrayAdapter(requireContext(), R.layout.item_spinner, RANGE_OPTIONS)
        spinnerRange.setSelection(1) // default 14 days

        fun loadChart() {
            viewLifecycleOwner.lifecycleScope.launch {
                db.userDao().getByIdFlow(uid).collectLatest { user ->
                    user ?: return@collectLatest
                    val goal = user.dailyCalorieGoal.toFloat()
                    launch {
                        db.foodLogDao().getCaloriesByRange(uid, Dates.daysAgo(rangeDays)).collectLatest { data ->
                            val labels = data.map { Dates.chart(it.date) }
                            val entries = data.mapIndexed { i, d -> Entry(i.toFloat(), d.calories) }
                            val goalLine = data.mapIndexed { i, _ -> Entry(i.toFloat(), goal) }
                            val avgVal = if (data.isNotEmpty()) data.sumOf { it.calories.toDouble() }.toInt() / data.size else 0
                            tvAvg.text = "Avg: $avgVal kcal/day"
                            tvBest.text = data.maxByOrNull { it.calories }?.let {
                                "Best: ${it.calories.toInt()} kcal on ${Dates.chart(it.date)}"
                            } ?: "No data yet"
                            val streak = data.map { it.calories }.reversed().takeWhile { it <= goal }.size
                            tvStreak.text = if (streak > 0) "On-goal streak: $streak days" else "Log food to start streak"

                            if (entries.isEmpty()) { chart.clear(); chart.invalidate(); return@collectLatest }
                            styleLineChart(chart, requireContext())
                            val ds1 = LineDataSet(entries, "Calories").apply {
                                color = ContextCompat.getColor(requireContext(), R.color.accent)
                                setCircleColor(ContextCompat.getColor(requireContext(), R.color.accent))
                                lineWidth = 2.5f; circleRadius = 4f
                                mode = LineDataSet.Mode.CUBIC_BEZIER
                                setDrawFilled(true)
                                fillColor = ContextCompat.getColor(requireContext(), R.color.accentLight)
                                fillAlpha = 60; valueTextSize = 9f
                            }
                            val ds2 = LineDataSet(goalLine, "Goal").apply {
                                color = ContextCompat.getColor(requireContext(), R.color.colorGreen)
                                lineWidth = 1.5f; enableDashedLine(10f, 5f, 0f)
                                setDrawCircles(false); setDrawValues(false)
                            }
                            chart.data = LineData(ds1, ds2)
                            chart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
                            chart.animateX(600); chart.invalidate()
                        }
                    }
                }
            }
        }

        spinnerRange.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p: AdapterView<*>?, v: View?, pos: Int, id: Long) {
                rangeDays = RANGE_DAYS[pos]; loadChart()
            }
            override fun onNothingSelected(p: AdapterView<*>?) {}
        }
        loadChart()
    }
}

// ── Steps Chart Tab ───────────────────────────────────────────────────────────
class StepsChartFragment : Fragment() {
    private var rangeDays = 14

    override fun onCreateView(inflater: LayoutInflater, c: ViewGroup?, s: Bundle?): View =
        inflater.inflate(R.layout.fragment_chart_steps, c, false)

    override fun onViewCreated(view: View, s: Bundle?) {
        val db = AppDatabase.get(requireContext())
        val uid = Session(requireContext()).userId
        val chart = view.findViewById<BarChart>(R.id.barChartSteps)
        val tvAvg = view.findViewById<TextView>(R.id.tvAvgSteps)
        val tvTotal = view.findViewById<TextView>(R.id.tvTotalSteps)
        val tvBest = view.findViewById<TextView>(R.id.tvBestStepDay)
        val spinnerRange = view.findViewById<Spinner>(R.id.spinnerRange)

        spinnerRange.adapter = ArrayAdapter(requireContext(), R.layout.item_spinner, RANGE_OPTIONS)
        spinnerRange.setSelection(1)

        fun loadChart() {
            viewLifecycleOwner.lifecycleScope.launch {
                db.stepLogDao().getByRange(uid, Dates.daysAgo(rangeDays)).collectLatest { data ->
                    val totalSteps = data.sumOf { it.steps.toLong() }
                    tvAvg.text = if (data.isNotEmpty()) "Avg: ${"%,d".format(totalSteps / data.size)}" else "No data yet"
                    tvTotal.text = "Total: ${"%,d".format(totalSteps)} steps"
                    tvBest.text = data.maxByOrNull { it.steps }?.let {
                        "Best: ${"%,d".format(it.steps)} on ${Dates.weekDay(it.date)}"
                    } ?: ""
                    if (data.isEmpty()) { chart.clear(); chart.invalidate(); return@collectLatest }
                    val labels = data.map { Dates.chart(it.date) }
                    val entries = data.mapIndexed { i, d -> BarEntry(i.toFloat(), d.steps.toFloat()) }
                    styleBarChart(chart, requireContext())
                    val ds = BarDataSet(entries, "Daily Steps").apply {
                        val colors = data.map {
                            if (it.steps >= it.goalSteps) ContextCompat.getColor(requireContext(), R.color.colorGreen)
                            else ContextCompat.getColor(requireContext(), R.color.colorBlue)
                        }
                        setColors(colors); valueTextSize = 9f
                    }
                    chart.data = BarData(ds).apply { barWidth = 0.6f }
                    chart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
                    chart.animateY(600); chart.invalidate()
                }
            }
        }

        spinnerRange.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p: AdapterView<*>?, v: View?, pos: Int, id: Long) {
                rangeDays = RANGE_DAYS[pos]; loadChart()
            }
            override fun onNothingSelected(p: AdapterView<*>?) {}
        }
        loadChart()
    }
}

// ── Workouts Chart Tab ────────────────────────────────────────────────────────
class WorkoutsChartFragment : Fragment() {
    private var rangeDays = 14

    override fun onCreateView(inflater: LayoutInflater, c: ViewGroup?, s: Bundle?): View =
        inflater.inflate(R.layout.fragment_chart_workouts, c, false)

    override fun onViewCreated(view: View, s: Bundle?) {
        val db = AppDatabase.get(requireContext())
        val uid = Session(requireContext()).userId
        val barChart = view.findViewById<BarChart>(R.id.barChartWorkouts)
        val tvWeekly = view.findViewById<TextView>(R.id.tvWeeklyWorkouts)
        val tvTotal = view.findViewById<TextView>(R.id.tvTotalExercises)
        val etWeight = view.findViewById<EditText>(R.id.etLogWeight)
        val btnLog = view.findViewById<Button>(R.id.btnLogWeight)
        val lineWeight = view.findViewById<LineChart>(R.id.lineWeight)
        val tvBmi = view.findViewById<TextView>(R.id.tvProgressBmi)
        val tvBmiCat = view.findViewById<TextView>(R.id.tvProgressBmiCat)
        val spinnerRange = view.findViewById<Spinner>(R.id.spinnerRange)

        spinnerRange.adapter = ArrayAdapter(requireContext(), R.layout.item_spinner, RANGE_OPTIONS)
        spinnerRange.setSelection(1)

        viewLifecycleOwner.lifecycleScope.launch {
            db.userDao().getByIdFlow(uid).collectLatest { user ->
                user ?: return@collectLatest
                val bmi = Calc.bmi(user.weightKg, user.heightCm)
                tvBmi.text = "BMI: ${"%.1f".format(bmi)}"
                tvBmiCat.text = Calc.bmiCategory(bmi)
            }
        }

        val btnToggleWeightLog = view.findViewById<android.widget.TextView>(R.id.btnToggleWeightLog)
        val weightHistorySection = view.findViewById<android.widget.LinearLayout>(R.id.weightHistorySection)
        val weightHistoryContainer = view.findViewById<android.widget.LinearLayout>(R.id.weightHistoryContainer)
        var historyExpanded = false

        btnToggleWeightLog.setOnClickListener {
            historyExpanded = !historyExpanded
            weightHistorySection.visibility = if (historyExpanded) android.view.View.VISIBLE else android.view.View.GONE
            btnToggleWeightLog.text = if (historyExpanded) "▲ History" else "▼ History"
        }

        viewLifecycleOwner.lifecycleScope.launch {
            db.weightLogDao().getAll(uid).collectLatest { weights ->
                // Update chart
                val sorted = weights.sortedBy { it.date }
                if (sorted.size >= 2) {
                    val labels = sorted.map { Dates.chart(it.date) }
                    val entries = sorted.mapIndexed { i, w -> Entry(i.toFloat(), w.weightKg) }
                    styleLineChart(lineWeight, requireContext())
                    val ds = LineDataSet(entries, "Weight (kg)").apply {
                        color = ContextCompat.getColor(requireContext(), R.color.colorPurple)
                        setCircleColor(ContextCompat.getColor(requireContext(), R.color.colorPurple))
                        lineWidth = 2f; circleRadius = 3f
                        mode = LineDataSet.Mode.CUBIC_BEZIER; valueTextSize = 9f
                    }
                    lineWeight.data = LineData(ds)
                    lineWeight.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
                    lineWeight.animateX(600); lineWeight.invalidate()
                }

                // Build history list (most recent first)
                weightHistoryContainer.removeAllViews()
                val ctx = requireContext()
                sorted.reversed().take(14).forEach { entry ->
                    val row = android.widget.LinearLayout(ctx).apply {
                        orientation = android.widget.LinearLayout.HORIZONTAL
                        gravity = android.view.Gravity.CENTER_VERTICAL
                        setPadding(0, 8, 0, 8)
                    }
                    val tvEntry = android.widget.TextView(ctx).apply {
                        text = "${Dates.pretty(entry.date)}  —  ${"%.1f".format(entry.weightKg)} kg"
                        textSize = 13f
                        setTextColor(ctx.getColor(R.color.textPrimary))
                        layoutParams = android.widget.LinearLayout.LayoutParams(0,
                            android.widget.LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                    }
                    val btnDelete = android.widget.TextView(ctx).apply {
                        text = "×"
                        textSize = 18f
                        setTextColor(ctx.getColor(R.color.colorRed))
                        setPadding(12, 0, 4, 0)
                        setOnClickListener {
                            android.app.AlertDialog.Builder(ctx)
                                .setTitle("Remove entry")
                                .setMessage("Remove ${"%.1f".format(entry.weightKg)} kg on ${Dates.pretty(entry.date)}?")
                                .setPositiveButton("Remove") { _, _ ->
                                    viewLifecycleOwner.lifecycleScope.launch {
                                        db.weightLogDao().delete(entry)
                                    }
                                }
                                .setNegativeButton("Cancel", null)
                                .show()
                        }
                    }
                    val divider = android.view.View(ctx).apply {
                        layoutParams = android.widget.LinearLayout.LayoutParams(
                            android.widget.LinearLayout.LayoutParams.MATCH_PARENT, 1).apply { topMargin = 8 }
                        setBackgroundColor(ctx.getColor(R.color.divider))
                    }
                    row.addView(tvEntry); row.addView(btnDelete)
                    weightHistoryContainer.addView(row)
                    weightHistoryContainer.addView(divider)
                }
            }
        }

        fun loadChart() {
            viewLifecycleOwner.lifecycleScope.launch {
                db.workoutLogDao().getCountByRange(uid, Dates.daysAgo(rangeDays)).collectLatest { data ->
                    val weekTotal = data.filter { it.date >= Dates.daysAgo(7) }
                        .sumOf { it.calories.toDouble() }.toInt()
                    val rangeTotal = data.sumOf { it.calories.toDouble() }.toInt()
                    tvWeekly.text = "This week: $weekTotal sessions"
                    tvTotal.text = "${rangeDays}-day total: $rangeTotal sessions"
                    if (data.isEmpty()) { barChart.clear(); barChart.invalidate(); return@collectLatest }
                    val labels = data.map { Dates.chart(it.date) }
                    val entries = data.mapIndexed { i, d -> BarEntry(i.toFloat(), d.calories.toFloat()) }
                    styleBarChart(barChart, requireContext())
                    val ds = BarDataSet(entries, "Exercises").apply {
                        color = ContextCompat.getColor(requireContext(), R.color.accent)
                        valueTextSize = 9f
                    }
                    barChart.data = BarData(ds).apply { barWidth = 0.6f }
                    barChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
                    barChart.animateY(600); barChart.invalidate()
                }
            }
        }

        spinnerRange.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p: AdapterView<*>?, v: View?, pos: Int, id: Long) {
                rangeDays = RANGE_DAYS[pos]; loadChart()
            }
            override fun onNothingSelected(p: AdapterView<*>?) {}
        }
        loadChart()

        // Set up unit spinner for weight logging
        val spinnerWeightUnit = view.findViewById<android.widget.Spinner>(R.id.spinnerLogWeightUnit)
        spinnerWeightUnit?.adapter = ArrayAdapter(requireContext(), R.layout.item_spinner, arrayOf("kg","lbs"))

        btnLog.setOnClickListener {
            val raw = etWeight.text.toString()
            val isKg = spinnerWeightUnit?.selectedItemPosition != 1
            val err = if (isKg) Validator.logWeightKg(raw) else Validator.logWeightLbs(raw)
            if (err != null) {
                Toast.makeText(requireContext(), err, Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            val rawVal = raw.trim().toFloat()
            val wKg = if (isKg) rawVal else Validator.lbsToKg(rawVal)
            viewLifecycleOwner.lifecycleScope.launch {
                db.weightLogDao().insert(WeightLogEntity(userId = uid, date = Dates.today(), weightKg = wKg))
                etWeight.text.clear()
                val display = if (isKg) "${wKg}kg" else "${rawVal}lbs (${String.format("%.1f", wKg)}kg)"
                Toast.makeText(requireContext(), "Weight logged: $display", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

fun styleLineChart(chart: LineChart, ctx: android.content.Context) {
    chart.apply {
        description.isEnabled = false
        setTouchEnabled(true); isDragEnabled = true; setScaleEnabled(true)
        setDrawGridBackground(false); legend.isEnabled = true
        xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM; granularity = 1f
            setDrawGridLines(false); labelRotationAngle = -30f; textSize = 10f
        }
        axisLeft.apply { setDrawGridLines(true); textSize = 10f }
        axisRight.isEnabled = false; setExtraOffsets(8f, 8f, 8f, 16f)
    }
}

fun styleBarChart(chart: BarChart, ctx: android.content.Context) {
    chart.apply {
        description.isEnabled = false
        setTouchEnabled(true); isDragEnabled = true
        setDrawGridBackground(false); legend.isEnabled = true
        xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM; granularity = 1f
            setDrawGridLines(false); labelRotationAngle = -30f; textSize = 10f
        }
        axisLeft.apply { setDrawGridLines(true); textSize = 10f }
        axisRight.isEnabled = false; setExtraOffsets(8f, 8f, 8f, 16f)
    }
}

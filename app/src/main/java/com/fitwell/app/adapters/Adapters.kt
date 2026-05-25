package com.fitwell.app.adapters

import android.view.*
import android.widget.*
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fitwell.app.R
import com.fitwell.app.database.FoodLogEntity
import com.fitwell.app.database.WorkoutLogEntity
import com.fitwell.app.utils.FoodItem

// ── Food Log Adapter ──────────────────────────────────────────────────────────
class FoodLogAdapter(
    private val onDelete: (FoodLogEntity) -> Unit,
    private val onMicroClick: (FoodLogEntity) -> Unit,
    private val onQuantityChange: (FoodLogEntity, Float) -> Unit = { _, _ -> }
) : ListAdapter<FoodLogEntity, FoodLogAdapter.VH>(object : DiffUtil.ItemCallback<FoodLogEntity>() {
    override fun areItemsTheSame(a: FoodLogEntity, b: FoodLogEntity) = a.id == b.id
    override fun areContentsTheSame(a: FoodLogEntity, b: FoodLogEntity) = a == b
}) {
    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        val img: ImageView      = v.findViewById(R.id.imgFood)
        val tvName: TextView    = v.findViewById(R.id.tvFoodName)
        val tvCals: TextView    = v.findViewById(R.id.tvFoodCals)
        val tvMacros: TextView  = v.findViewById(R.id.tvFoodMacros)
        val tvQty: TextView     = v.findViewById(R.id.tvFoodQty)
        val btnMinus: ImageButton = v.findViewById(R.id.btnQtyMinus)
        val btnPlus: ImageButton  = v.findViewById(R.id.btnQtyPlus)
        val btnDelete: ImageButton = v.findViewById(R.id.btnDelete)
        val btnMicro: TextView  = v.findViewById(R.id.btnMicro)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_food_log, parent, false))

    override fun onBindViewHolder(h: VH, pos: Int) {
        val item = getItem(pos)
        h.tvName.text  = item.foodName
        h.tvCals.text  = "${item.calories.toInt()} kcal"
        // Macros on one line, quantity on separate counter
        h.tvMacros.text = "P ${item.protein.toInt()}g · C ${item.carbs.toInt()}g · F ${item.fat.toInt()}g"
        h.tvQty.text   = "×${item.quantity.toInt()}"

        h.btnMinus.setOnClickListener {
            if (item.quantity > 1f) onQuantityChange(item, item.quantity - 1f)
        }
        h.btnPlus.setOnClickListener  { onQuantityChange(item, item.quantity + 1f) }
        h.btnDelete.setOnClickListener { onDelete(item) }
        h.btnMicro.setOnClickListener  { onMicroClick(item) }

        if (item.imageUrl.isNotEmpty()) {
            Glide.with(h.img).load(item.imageUrl)
                .placeholder(R.drawable.ic_food_placeholder).circleCrop().into(h.img)
        } else {
            h.img.setImageResource(R.drawable.ic_food_placeholder)
        }
    }
}

// ── Food Search Adapter ───────────────────────────────────────────────────────
class FoodSearchAdapter(private val onSelect: (FoodItem) -> Unit) :
    ListAdapter<FoodItem, FoodSearchAdapter.VH>(object : DiffUtil.ItemCallback<FoodItem>() {
        override fun areItemsTheSame(a: FoodItem, b: FoodItem) = a.name == b.name
        override fun areContentsTheSame(a: FoodItem, b: FoodItem) = a == b
    }) {
    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        val img: ImageView    = v.findViewById(R.id.imgSearchFood)
        val tvName: TextView  = v.findViewById(R.id.tvSearchName)
        val tvInfo: TextView  = v.findViewById(R.id.tvSearchInfo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_food_search, parent, false))

    override fun onBindViewHolder(h: VH, pos: Int) {
        val f = getItem(pos)
        h.tvName.text = f.name
        h.tvInfo.text = "${f.kcal.toInt()} kcal · P${f.protein.toInt()}g C${f.carbs.toInt()}g F${f.fat.toInt()}g · per ${f.serving.toInt()}${f.unit}"
        h.itemView.setOnClickListener { onSelect(f) }
        Glide.with(h.img).load(f.imageUrl)
            .placeholder(R.drawable.ic_food_placeholder).circleCrop().into(h.img)
    }
}

// ── Workout Log Adapter ───────────────────────────────────────────────────────
class WorkoutLogAdapter(private val onDelete: (WorkoutLogEntity) -> Unit) :
    ListAdapter<WorkoutLogEntity, WorkoutLogAdapter.VH>(object : DiffUtil.ItemCallback<WorkoutLogEntity>() {
        override fun areItemsTheSame(a: WorkoutLogEntity, b: WorkoutLogEntity) = a.id == b.id
        override fun areContentsTheSame(a: WorkoutLogEntity, b: WorkoutLogEntity) = a == b
    }) {
    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        val img: ImageView     = v.findViewById(R.id.imgWorkout)
        val tvName: TextView   = v.findViewById(R.id.tvWorkoutName)
        val tvDetails: TextView= v.findViewById(R.id.tvWorkoutDetails)
        val tvCals: TextView   = v.findViewById(R.id.tvWorkoutCals)
        val btnDelete: ImageButton = v.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_workout_log, parent, false))

    override fun onBindViewHolder(h: VH, pos: Int) {
        val item = getItem(pos)
        h.tvName.text    = item.exerciseName
        val wt = if (item.weightKg > 0) " · ${item.weightKg}kg" else ""
        h.tvDetails.text = "${item.sets}×${item.reps}$wt · ${item.muscleGroup}"
        h.tvCals.text    = if (item.caloriesBurned > 0) "${item.caloriesBurned.toInt()} kcal" else ""
        h.btnDelete.setOnClickListener { onDelete(item) }
        if (item.imageUrl.isNotEmpty()) {
            Glide.with(h.img).load(item.imageUrl)
                .placeholder(R.drawable.ic_exercise_placeholder).circleCrop().into(h.img)
        } else {
            h.img.setImageResource(R.drawable.ic_exercise_placeholder)
        }
    }
}

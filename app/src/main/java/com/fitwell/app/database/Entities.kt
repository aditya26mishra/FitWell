package com.fitwell.app.database

import androidx.room.*

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val email: String,
    val passwordHash: String,
    val age: Int,
    val heightCm: Float,
    val weightKg: Float,
    val gender: String,
    val activityLevel: String,
    val goal: String,
    val targetWeightKg: Float,
    val dailyCalorieGoal: Int,
    val dailyStepGoal: Int,
    val dietPreference: String,
    val language: String = "en",
    val notificationsEnabled: Boolean = true,
    // Weight-loss goal date (ISO yyyy-MM-dd, empty = not set)
    val goalTargetDate: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "food_log",
    foreignKeys = [ForeignKey(entity = UserEntity::class, parentColumns = ["id"],
        childColumns = ["userId"], onDelete = ForeignKey.CASCADE)],
    indices = [Index("userId")]
)
data class FoodLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val date: String,
    val mealType: String,
    val foodName: String,
    val quantity: Float,
    val unit: String = "g",
    val calories: Float,
    val protein: Float,
    val carbs: Float,
    val fat: Float,
    val fiber: Float = 0f,
    val sugar: Float = 0f,
    val sodium: Float = 0f,
    val vitaminA: Float = 0f,
    val vitaminC: Float = 0f,
    val vitaminD: Float = 0f,
    val vitaminB12: Float = 0f,
    val calcium: Float = 0f,
    val iron: Float = 0f,
    val potassium: Float = 0f,
    val magnesium: Float = 0f,
    val zinc: Float = 0f,
    val imageUrl: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "step_log",
    foreignKeys = [ForeignKey(entity = UserEntity::class, parentColumns = ["id"],
        childColumns = ["userId"], onDelete = ForeignKey.CASCADE)],
    indices = [Index("userId")]
)
data class StepLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val date: String,
    val steps: Int,
    val goalSteps: Int,
    val distanceKm: Float,
    val caloriesBurned: Float
)

@Entity(
    tableName = "workout_log",
    foreignKeys = [ForeignKey(entity = UserEntity::class, parentColumns = ["id"],
        childColumns = ["userId"], onDelete = ForeignKey.CASCADE)],
    indices = [Index("userId")]
)
data class WorkoutLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val date: String,
    val exerciseName: String,
    val muscleGroup: String,
    val sets: Int,
    val reps: Int,
    val weightKg: Float,
    val durationMinutes: Int,
    val caloriesBurned: Float,
    val notes: String = "",
    val imageUrl: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "weight_log",
    foreignKeys = [ForeignKey(entity = UserEntity::class, parentColumns = ["id"],
        childColumns = ["userId"], onDelete = ForeignKey.CASCADE)],
    indices = [Index("userId")]
)
data class WeightLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val date: String,
    val weightKg: Float,
    val timestamp: Long = System.currentTimeMillis()
)

package com.fitwell.app.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: UserEntity): Long

    @Update
    suspend fun update(user: UserEntity)

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    fun getByIdFlow(id: Int): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): UserEntity?

    @Query("SELECT * FROM users WHERE email = :email AND passwordHash = :hash LIMIT 1")
    suspend fun login(email: String, hash: String): UserEntity?
}

@Dao
interface FoodLogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: FoodLogEntity): Long

    @Delete
    suspend fun delete(log: FoodLogEntity)

    @Update
    suspend fun update(log: FoodLogEntity)

    @Query("SELECT * FROM food_log WHERE userId=:uid AND date=:date ORDER BY timestamp DESC")
    fun getForDate(uid: Int, date: String): Flow<List<FoodLogEntity>>

    @Query("SELECT * FROM food_log WHERE userId=:uid AND date=:date AND mealType=:meal ORDER BY timestamp DESC")
    fun getForMeal(uid: Int, date: String, meal: String): Flow<List<FoodLogEntity>>

    @Query("SELECT date, SUM(calories) as calories FROM food_log WHERE userId=:uid AND date>=:from GROUP BY date ORDER BY date")
    fun getCaloriesByRange(uid: Int, from: String): Flow<List<DateValue>>


    @Query("SELECT * FROM food_log WHERE userId=:uid AND date=:date AND mealType=:meal AND foodName=:name AND unit=:unit LIMIT 1")
    suspend fun findByNameMealDate(uid: Int, date: String, meal: String, name: String, unit: String): FoodLogEntity?

    @Query("SELECT * FROM food_log WHERE userId=:uid ORDER BY timestamp DESC LIMIT 30")
    suspend fun getRecent(uid: Int): List<FoodLogEntity>
}

data class DateValue(val date: String, val calories: Float)

@Dao
interface StepLogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: StepLogEntity): Long

    @Update
    suspend fun update(log: StepLogEntity)

    @Query("SELECT * FROM step_log WHERE userId=:uid AND date=:date LIMIT 1")
    suspend fun getForDate(uid: Int, date: String): StepLogEntity?

    @Query("SELECT * FROM step_log WHERE userId=:uid AND date=:date LIMIT 1")
    fun getForDateFlow(uid: Int, date: String): Flow<StepLogEntity?>

    @Query("SELECT * FROM step_log WHERE userId=:uid AND date>=:from ORDER BY date")
    fun getByRange(uid: Int, from: String): Flow<List<StepLogEntity>>

    @Query("SELECT AVG(steps) FROM step_log WHERE userId=:uid AND date>=:from")
    fun getAvg(uid: Int, from: String): Flow<Float?>
}

@Dao
interface WorkoutLogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: WorkoutLogEntity): Long

    @Delete
    suspend fun delete(log: WorkoutLogEntity)

    @Update
    suspend fun update(log: WorkoutLogEntity)

    @Query("SELECT * FROM workout_log WHERE userId=:uid AND date=:date ORDER BY timestamp DESC")
    fun getForDate(uid: Int, date: String): Flow<List<WorkoutLogEntity>>

    @Query("SELECT * FROM workout_log WHERE userId=:uid ORDER BY timestamp DESC LIMIT 50")
    fun getRecent(uid: Int): Flow<List<WorkoutLogEntity>>

    @Query("SELECT date, COUNT(*) as calories FROM workout_log WHERE userId=:uid AND date>=:from GROUP BY date ORDER BY date")
    fun getCountByRange(uid: Int, from: String): Flow<List<DateValue>>

    @Query("SELECT SUM(caloriesBurned) FROM workout_log WHERE userId=:uid AND date=:date")
    fun getBurnedForDate(uid: Int, date: String): Flow<Float?>
}

@Dao
interface WeightLogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: WeightLogEntity): Long

    @Delete
    suspend fun delete(log: WeightLogEntity)

    @Query("SELECT * FROM weight_log WHERE userId=:uid ORDER BY date DESC")
    fun getAll(uid: Int): Flow<List<WeightLogEntity>>

    @Query("SELECT * FROM weight_log WHERE userId=:uid ORDER BY date DESC LIMIT 1")
    fun getLatest(uid: Int): Flow<WeightLogEntity?>
}

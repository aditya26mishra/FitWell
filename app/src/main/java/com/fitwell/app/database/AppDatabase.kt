package com.fitwell.app.database

import android.content.Context
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [UserEntity::class, FoodLogEntity::class, StepLogEntity::class,
        WorkoutLogEntity::class, WeightLogEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun foodLogDao(): FoodLogDao
    abstract fun stepLogDao(): StepLogDao
    abstract fun workoutLogDao(): WorkoutLogDao
    abstract fun weightLogDao(): WeightLogDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Add goalTargetDate column; remove darkMode (no-op in SQLite — just add new)
                db.execSQL("ALTER TABLE users ADD COLUMN goalTargetDate TEXT NOT NULL DEFAULT ''")
            }
        }

        fun get(context: Context): AppDatabase = INSTANCE ?: synchronized(this) {
            Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "fitwell.db")
                .addMigrations(MIGRATION_1_2)
                .fallbackToDestructiveMigration()
                .build().also { INSTANCE = it }
        }
    }
}

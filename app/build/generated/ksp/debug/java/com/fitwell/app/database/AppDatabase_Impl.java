package com.fitwell.app.database;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile UserDao _userDao;

  private volatile FoodLogDao _foodLogDao;

  private volatile StepLogDao _stepLogDao;

  private volatile WorkoutLogDao _workoutLogDao;

  private volatile WeightLogDao _weightLogDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(2) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `users` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `email` TEXT NOT NULL, `passwordHash` TEXT NOT NULL, `age` INTEGER NOT NULL, `heightCm` REAL NOT NULL, `weightKg` REAL NOT NULL, `gender` TEXT NOT NULL, `activityLevel` TEXT NOT NULL, `goal` TEXT NOT NULL, `targetWeightKg` REAL NOT NULL, `dailyCalorieGoal` INTEGER NOT NULL, `dailyStepGoal` INTEGER NOT NULL, `dietPreference` TEXT NOT NULL, `language` TEXT NOT NULL, `notificationsEnabled` INTEGER NOT NULL, `goalTargetDate` TEXT NOT NULL, `createdAt` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `food_log` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `userId` INTEGER NOT NULL, `date` TEXT NOT NULL, `mealType` TEXT NOT NULL, `foodName` TEXT NOT NULL, `quantity` REAL NOT NULL, `unit` TEXT NOT NULL, `calories` REAL NOT NULL, `protein` REAL NOT NULL, `carbs` REAL NOT NULL, `fat` REAL NOT NULL, `fiber` REAL NOT NULL, `sugar` REAL NOT NULL, `sodium` REAL NOT NULL, `vitaminA` REAL NOT NULL, `vitaminC` REAL NOT NULL, `vitaminD` REAL NOT NULL, `vitaminB12` REAL NOT NULL, `calcium` REAL NOT NULL, `iron` REAL NOT NULL, `potassium` REAL NOT NULL, `magnesium` REAL NOT NULL, `zinc` REAL NOT NULL, `imageUrl` TEXT NOT NULL, `timestamp` INTEGER NOT NULL, FOREIGN KEY(`userId`) REFERENCES `users`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_food_log_userId` ON `food_log` (`userId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `step_log` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `userId` INTEGER NOT NULL, `date` TEXT NOT NULL, `steps` INTEGER NOT NULL, `goalSteps` INTEGER NOT NULL, `distanceKm` REAL NOT NULL, `caloriesBurned` REAL NOT NULL, FOREIGN KEY(`userId`) REFERENCES `users`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_step_log_userId` ON `step_log` (`userId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `workout_log` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `userId` INTEGER NOT NULL, `date` TEXT NOT NULL, `exerciseName` TEXT NOT NULL, `muscleGroup` TEXT NOT NULL, `sets` INTEGER NOT NULL, `reps` INTEGER NOT NULL, `weightKg` REAL NOT NULL, `durationMinutes` INTEGER NOT NULL, `caloriesBurned` REAL NOT NULL, `notes` TEXT NOT NULL, `imageUrl` TEXT NOT NULL, `timestamp` INTEGER NOT NULL, FOREIGN KEY(`userId`) REFERENCES `users`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_workout_log_userId` ON `workout_log` (`userId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `weight_log` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `userId` INTEGER NOT NULL, `date` TEXT NOT NULL, `weightKg` REAL NOT NULL, `timestamp` INTEGER NOT NULL, FOREIGN KEY(`userId`) REFERENCES `users`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_weight_log_userId` ON `weight_log` (`userId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '8c3925426b41ee1ef14a22e87e9cef4e')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `users`");
        db.execSQL("DROP TABLE IF EXISTS `food_log`");
        db.execSQL("DROP TABLE IF EXISTS `step_log`");
        db.execSQL("DROP TABLE IF EXISTS `workout_log`");
        db.execSQL("DROP TABLE IF EXISTS `weight_log`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        db.execSQL("PRAGMA foreign_keys = ON");
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsUsers = new HashMap<String, TableInfo.Column>(18);
        _columnsUsers.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("email", new TableInfo.Column("email", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("passwordHash", new TableInfo.Column("passwordHash", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("age", new TableInfo.Column("age", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("heightCm", new TableInfo.Column("heightCm", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("weightKg", new TableInfo.Column("weightKg", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("gender", new TableInfo.Column("gender", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("activityLevel", new TableInfo.Column("activityLevel", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("goal", new TableInfo.Column("goal", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("targetWeightKg", new TableInfo.Column("targetWeightKg", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("dailyCalorieGoal", new TableInfo.Column("dailyCalorieGoal", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("dailyStepGoal", new TableInfo.Column("dailyStepGoal", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("dietPreference", new TableInfo.Column("dietPreference", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("language", new TableInfo.Column("language", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("notificationsEnabled", new TableInfo.Column("notificationsEnabled", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("goalTargetDate", new TableInfo.Column("goalTargetDate", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysUsers = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesUsers = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoUsers = new TableInfo("users", _columnsUsers, _foreignKeysUsers, _indicesUsers);
        final TableInfo _existingUsers = TableInfo.read(db, "users");
        if (!_infoUsers.equals(_existingUsers)) {
          return new RoomOpenHelper.ValidationResult(false, "users(com.fitwell.app.database.UserEntity).\n"
                  + " Expected:\n" + _infoUsers + "\n"
                  + " Found:\n" + _existingUsers);
        }
        final HashMap<String, TableInfo.Column> _columnsFoodLog = new HashMap<String, TableInfo.Column>(25);
        _columnsFoodLog.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoodLog.put("userId", new TableInfo.Column("userId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoodLog.put("date", new TableInfo.Column("date", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoodLog.put("mealType", new TableInfo.Column("mealType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoodLog.put("foodName", new TableInfo.Column("foodName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoodLog.put("quantity", new TableInfo.Column("quantity", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoodLog.put("unit", new TableInfo.Column("unit", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoodLog.put("calories", new TableInfo.Column("calories", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoodLog.put("protein", new TableInfo.Column("protein", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoodLog.put("carbs", new TableInfo.Column("carbs", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoodLog.put("fat", new TableInfo.Column("fat", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoodLog.put("fiber", new TableInfo.Column("fiber", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoodLog.put("sugar", new TableInfo.Column("sugar", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoodLog.put("sodium", new TableInfo.Column("sodium", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoodLog.put("vitaminA", new TableInfo.Column("vitaminA", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoodLog.put("vitaminC", new TableInfo.Column("vitaminC", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoodLog.put("vitaminD", new TableInfo.Column("vitaminD", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoodLog.put("vitaminB12", new TableInfo.Column("vitaminB12", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoodLog.put("calcium", new TableInfo.Column("calcium", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoodLog.put("iron", new TableInfo.Column("iron", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoodLog.put("potassium", new TableInfo.Column("potassium", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoodLog.put("magnesium", new TableInfo.Column("magnesium", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoodLog.put("zinc", new TableInfo.Column("zinc", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoodLog.put("imageUrl", new TableInfo.Column("imageUrl", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoodLog.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysFoodLog = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysFoodLog.add(new TableInfo.ForeignKey("users", "CASCADE", "NO ACTION", Arrays.asList("userId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesFoodLog = new HashSet<TableInfo.Index>(1);
        _indicesFoodLog.add(new TableInfo.Index("index_food_log_userId", false, Arrays.asList("userId"), Arrays.asList("ASC")));
        final TableInfo _infoFoodLog = new TableInfo("food_log", _columnsFoodLog, _foreignKeysFoodLog, _indicesFoodLog);
        final TableInfo _existingFoodLog = TableInfo.read(db, "food_log");
        if (!_infoFoodLog.equals(_existingFoodLog)) {
          return new RoomOpenHelper.ValidationResult(false, "food_log(com.fitwell.app.database.FoodLogEntity).\n"
                  + " Expected:\n" + _infoFoodLog + "\n"
                  + " Found:\n" + _existingFoodLog);
        }
        final HashMap<String, TableInfo.Column> _columnsStepLog = new HashMap<String, TableInfo.Column>(7);
        _columnsStepLog.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsStepLog.put("userId", new TableInfo.Column("userId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsStepLog.put("date", new TableInfo.Column("date", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsStepLog.put("steps", new TableInfo.Column("steps", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsStepLog.put("goalSteps", new TableInfo.Column("goalSteps", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsStepLog.put("distanceKm", new TableInfo.Column("distanceKm", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsStepLog.put("caloriesBurned", new TableInfo.Column("caloriesBurned", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysStepLog = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysStepLog.add(new TableInfo.ForeignKey("users", "CASCADE", "NO ACTION", Arrays.asList("userId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesStepLog = new HashSet<TableInfo.Index>(1);
        _indicesStepLog.add(new TableInfo.Index("index_step_log_userId", false, Arrays.asList("userId"), Arrays.asList("ASC")));
        final TableInfo _infoStepLog = new TableInfo("step_log", _columnsStepLog, _foreignKeysStepLog, _indicesStepLog);
        final TableInfo _existingStepLog = TableInfo.read(db, "step_log");
        if (!_infoStepLog.equals(_existingStepLog)) {
          return new RoomOpenHelper.ValidationResult(false, "step_log(com.fitwell.app.database.StepLogEntity).\n"
                  + " Expected:\n" + _infoStepLog + "\n"
                  + " Found:\n" + _existingStepLog);
        }
        final HashMap<String, TableInfo.Column> _columnsWorkoutLog = new HashMap<String, TableInfo.Column>(13);
        _columnsWorkoutLog.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkoutLog.put("userId", new TableInfo.Column("userId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkoutLog.put("date", new TableInfo.Column("date", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkoutLog.put("exerciseName", new TableInfo.Column("exerciseName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkoutLog.put("muscleGroup", new TableInfo.Column("muscleGroup", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkoutLog.put("sets", new TableInfo.Column("sets", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkoutLog.put("reps", new TableInfo.Column("reps", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkoutLog.put("weightKg", new TableInfo.Column("weightKg", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkoutLog.put("durationMinutes", new TableInfo.Column("durationMinutes", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkoutLog.put("caloriesBurned", new TableInfo.Column("caloriesBurned", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkoutLog.put("notes", new TableInfo.Column("notes", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkoutLog.put("imageUrl", new TableInfo.Column("imageUrl", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkoutLog.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysWorkoutLog = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysWorkoutLog.add(new TableInfo.ForeignKey("users", "CASCADE", "NO ACTION", Arrays.asList("userId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesWorkoutLog = new HashSet<TableInfo.Index>(1);
        _indicesWorkoutLog.add(new TableInfo.Index("index_workout_log_userId", false, Arrays.asList("userId"), Arrays.asList("ASC")));
        final TableInfo _infoWorkoutLog = new TableInfo("workout_log", _columnsWorkoutLog, _foreignKeysWorkoutLog, _indicesWorkoutLog);
        final TableInfo _existingWorkoutLog = TableInfo.read(db, "workout_log");
        if (!_infoWorkoutLog.equals(_existingWorkoutLog)) {
          return new RoomOpenHelper.ValidationResult(false, "workout_log(com.fitwell.app.database.WorkoutLogEntity).\n"
                  + " Expected:\n" + _infoWorkoutLog + "\n"
                  + " Found:\n" + _existingWorkoutLog);
        }
        final HashMap<String, TableInfo.Column> _columnsWeightLog = new HashMap<String, TableInfo.Column>(5);
        _columnsWeightLog.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWeightLog.put("userId", new TableInfo.Column("userId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWeightLog.put("date", new TableInfo.Column("date", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWeightLog.put("weightKg", new TableInfo.Column("weightKg", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWeightLog.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysWeightLog = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysWeightLog.add(new TableInfo.ForeignKey("users", "CASCADE", "NO ACTION", Arrays.asList("userId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesWeightLog = new HashSet<TableInfo.Index>(1);
        _indicesWeightLog.add(new TableInfo.Index("index_weight_log_userId", false, Arrays.asList("userId"), Arrays.asList("ASC")));
        final TableInfo _infoWeightLog = new TableInfo("weight_log", _columnsWeightLog, _foreignKeysWeightLog, _indicesWeightLog);
        final TableInfo _existingWeightLog = TableInfo.read(db, "weight_log");
        if (!_infoWeightLog.equals(_existingWeightLog)) {
          return new RoomOpenHelper.ValidationResult(false, "weight_log(com.fitwell.app.database.WeightLogEntity).\n"
                  + " Expected:\n" + _infoWeightLog + "\n"
                  + " Found:\n" + _existingWeightLog);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "8c3925426b41ee1ef14a22e87e9cef4e", "0ced8b248abdacd70c4df3c23ffebe40");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "users","food_log","step_log","workout_log","weight_log");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    final boolean _supportsDeferForeignKeys = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP;
    try {
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = FALSE");
      }
      super.beginTransaction();
      if (_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA defer_foreign_keys = TRUE");
      }
      _db.execSQL("DELETE FROM `users`");
      _db.execSQL("DELETE FROM `food_log`");
      _db.execSQL("DELETE FROM `step_log`");
      _db.execSQL("DELETE FROM `workout_log`");
      _db.execSQL("DELETE FROM `weight_log`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = TRUE");
      }
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(UserDao.class, UserDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(FoodLogDao.class, FoodLogDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(StepLogDao.class, StepLogDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(WorkoutLogDao.class, WorkoutLogDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(WeightLogDao.class, WeightLogDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public UserDao userDao() {
    if (_userDao != null) {
      return _userDao;
    } else {
      synchronized(this) {
        if(_userDao == null) {
          _userDao = new UserDao_Impl(this);
        }
        return _userDao;
      }
    }
  }

  @Override
  public FoodLogDao foodLogDao() {
    if (_foodLogDao != null) {
      return _foodLogDao;
    } else {
      synchronized(this) {
        if(_foodLogDao == null) {
          _foodLogDao = new FoodLogDao_Impl(this);
        }
        return _foodLogDao;
      }
    }
  }

  @Override
  public StepLogDao stepLogDao() {
    if (_stepLogDao != null) {
      return _stepLogDao;
    } else {
      synchronized(this) {
        if(_stepLogDao == null) {
          _stepLogDao = new StepLogDao_Impl(this);
        }
        return _stepLogDao;
      }
    }
  }

  @Override
  public WorkoutLogDao workoutLogDao() {
    if (_workoutLogDao != null) {
      return _workoutLogDao;
    } else {
      synchronized(this) {
        if(_workoutLogDao == null) {
          _workoutLogDao = new WorkoutLogDao_Impl(this);
        }
        return _workoutLogDao;
      }
    }
  }

  @Override
  public WeightLogDao weightLogDao() {
    if (_weightLogDao != null) {
      return _weightLogDao;
    } else {
      synchronized(this) {
        if(_weightLogDao == null) {
          _weightLogDao = new WeightLogDao_Impl(this);
        }
        return _weightLogDao;
      }
    }
  }
}

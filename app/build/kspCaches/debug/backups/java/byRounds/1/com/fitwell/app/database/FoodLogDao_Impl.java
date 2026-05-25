package com.fitwell.app.database;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class FoodLogDao_Impl implements FoodLogDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<FoodLogEntity> __insertionAdapterOfFoodLogEntity;

  private final EntityDeletionOrUpdateAdapter<FoodLogEntity> __deletionAdapterOfFoodLogEntity;

  private final EntityDeletionOrUpdateAdapter<FoodLogEntity> __updateAdapterOfFoodLogEntity;

  public FoodLogDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfFoodLogEntity = new EntityInsertionAdapter<FoodLogEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `food_log` (`id`,`userId`,`date`,`mealType`,`foodName`,`quantity`,`unit`,`calories`,`protein`,`carbs`,`fat`,`fiber`,`sugar`,`sodium`,`vitaminA`,`vitaminC`,`vitaminD`,`vitaminB12`,`calcium`,`iron`,`potassium`,`magnesium`,`zinc`,`imageUrl`,`timestamp`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final FoodLogEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getUserId());
        statement.bindString(3, entity.getDate());
        statement.bindString(4, entity.getMealType());
        statement.bindString(5, entity.getFoodName());
        statement.bindDouble(6, entity.getQuantity());
        statement.bindString(7, entity.getUnit());
        statement.bindDouble(8, entity.getCalories());
        statement.bindDouble(9, entity.getProtein());
        statement.bindDouble(10, entity.getCarbs());
        statement.bindDouble(11, entity.getFat());
        statement.bindDouble(12, entity.getFiber());
        statement.bindDouble(13, entity.getSugar());
        statement.bindDouble(14, entity.getSodium());
        statement.bindDouble(15, entity.getVitaminA());
        statement.bindDouble(16, entity.getVitaminC());
        statement.bindDouble(17, entity.getVitaminD());
        statement.bindDouble(18, entity.getVitaminB12());
        statement.bindDouble(19, entity.getCalcium());
        statement.bindDouble(20, entity.getIron());
        statement.bindDouble(21, entity.getPotassium());
        statement.bindDouble(22, entity.getMagnesium());
        statement.bindDouble(23, entity.getZinc());
        statement.bindString(24, entity.getImageUrl());
        statement.bindLong(25, entity.getTimestamp());
      }
    };
    this.__deletionAdapterOfFoodLogEntity = new EntityDeletionOrUpdateAdapter<FoodLogEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `food_log` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final FoodLogEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfFoodLogEntity = new EntityDeletionOrUpdateAdapter<FoodLogEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `food_log` SET `id` = ?,`userId` = ?,`date` = ?,`mealType` = ?,`foodName` = ?,`quantity` = ?,`unit` = ?,`calories` = ?,`protein` = ?,`carbs` = ?,`fat` = ?,`fiber` = ?,`sugar` = ?,`sodium` = ?,`vitaminA` = ?,`vitaminC` = ?,`vitaminD` = ?,`vitaminB12` = ?,`calcium` = ?,`iron` = ?,`potassium` = ?,`magnesium` = ?,`zinc` = ?,`imageUrl` = ?,`timestamp` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final FoodLogEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getUserId());
        statement.bindString(3, entity.getDate());
        statement.bindString(4, entity.getMealType());
        statement.bindString(5, entity.getFoodName());
        statement.bindDouble(6, entity.getQuantity());
        statement.bindString(7, entity.getUnit());
        statement.bindDouble(8, entity.getCalories());
        statement.bindDouble(9, entity.getProtein());
        statement.bindDouble(10, entity.getCarbs());
        statement.bindDouble(11, entity.getFat());
        statement.bindDouble(12, entity.getFiber());
        statement.bindDouble(13, entity.getSugar());
        statement.bindDouble(14, entity.getSodium());
        statement.bindDouble(15, entity.getVitaminA());
        statement.bindDouble(16, entity.getVitaminC());
        statement.bindDouble(17, entity.getVitaminD());
        statement.bindDouble(18, entity.getVitaminB12());
        statement.bindDouble(19, entity.getCalcium());
        statement.bindDouble(20, entity.getIron());
        statement.bindDouble(21, entity.getPotassium());
        statement.bindDouble(22, entity.getMagnesium());
        statement.bindDouble(23, entity.getZinc());
        statement.bindString(24, entity.getImageUrl());
        statement.bindLong(25, entity.getTimestamp());
        statement.bindLong(26, entity.getId());
      }
    };
  }

  @Override
  public Object insert(final FoodLogEntity log, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfFoodLogEntity.insertAndReturnId(log);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final FoodLogEntity log, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfFoodLogEntity.handle(log);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final FoodLogEntity log, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfFoodLogEntity.handle(log);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<FoodLogEntity>> getForDate(final int uid, final String date) {
    final String _sql = "SELECT * FROM food_log WHERE userId=? AND date=? ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, uid);
    _argIndex = 2;
    _statement.bindString(_argIndex, date);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"food_log"}, new Callable<List<FoodLogEntity>>() {
      @Override
      @NonNull
      public List<FoodLogEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfMealType = CursorUtil.getColumnIndexOrThrow(_cursor, "mealType");
          final int _cursorIndexOfFoodName = CursorUtil.getColumnIndexOrThrow(_cursor, "foodName");
          final int _cursorIndexOfQuantity = CursorUtil.getColumnIndexOrThrow(_cursor, "quantity");
          final int _cursorIndexOfUnit = CursorUtil.getColumnIndexOrThrow(_cursor, "unit");
          final int _cursorIndexOfCalories = CursorUtil.getColumnIndexOrThrow(_cursor, "calories");
          final int _cursorIndexOfProtein = CursorUtil.getColumnIndexOrThrow(_cursor, "protein");
          final int _cursorIndexOfCarbs = CursorUtil.getColumnIndexOrThrow(_cursor, "carbs");
          final int _cursorIndexOfFat = CursorUtil.getColumnIndexOrThrow(_cursor, "fat");
          final int _cursorIndexOfFiber = CursorUtil.getColumnIndexOrThrow(_cursor, "fiber");
          final int _cursorIndexOfSugar = CursorUtil.getColumnIndexOrThrow(_cursor, "sugar");
          final int _cursorIndexOfSodium = CursorUtil.getColumnIndexOrThrow(_cursor, "sodium");
          final int _cursorIndexOfVitaminA = CursorUtil.getColumnIndexOrThrow(_cursor, "vitaminA");
          final int _cursorIndexOfVitaminC = CursorUtil.getColumnIndexOrThrow(_cursor, "vitaminC");
          final int _cursorIndexOfVitaminD = CursorUtil.getColumnIndexOrThrow(_cursor, "vitaminD");
          final int _cursorIndexOfVitaminB12 = CursorUtil.getColumnIndexOrThrow(_cursor, "vitaminB12");
          final int _cursorIndexOfCalcium = CursorUtil.getColumnIndexOrThrow(_cursor, "calcium");
          final int _cursorIndexOfIron = CursorUtil.getColumnIndexOrThrow(_cursor, "iron");
          final int _cursorIndexOfPotassium = CursorUtil.getColumnIndexOrThrow(_cursor, "potassium");
          final int _cursorIndexOfMagnesium = CursorUtil.getColumnIndexOrThrow(_cursor, "magnesium");
          final int _cursorIndexOfZinc = CursorUtil.getColumnIndexOrThrow(_cursor, "zinc");
          final int _cursorIndexOfImageUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "imageUrl");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final List<FoodLogEntity> _result = new ArrayList<FoodLogEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final FoodLogEntity _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final int _tmpUserId;
            _tmpUserId = _cursor.getInt(_cursorIndexOfUserId);
            final String _tmpDate;
            _tmpDate = _cursor.getString(_cursorIndexOfDate);
            final String _tmpMealType;
            _tmpMealType = _cursor.getString(_cursorIndexOfMealType);
            final String _tmpFoodName;
            _tmpFoodName = _cursor.getString(_cursorIndexOfFoodName);
            final float _tmpQuantity;
            _tmpQuantity = _cursor.getFloat(_cursorIndexOfQuantity);
            final String _tmpUnit;
            _tmpUnit = _cursor.getString(_cursorIndexOfUnit);
            final float _tmpCalories;
            _tmpCalories = _cursor.getFloat(_cursorIndexOfCalories);
            final float _tmpProtein;
            _tmpProtein = _cursor.getFloat(_cursorIndexOfProtein);
            final float _tmpCarbs;
            _tmpCarbs = _cursor.getFloat(_cursorIndexOfCarbs);
            final float _tmpFat;
            _tmpFat = _cursor.getFloat(_cursorIndexOfFat);
            final float _tmpFiber;
            _tmpFiber = _cursor.getFloat(_cursorIndexOfFiber);
            final float _tmpSugar;
            _tmpSugar = _cursor.getFloat(_cursorIndexOfSugar);
            final float _tmpSodium;
            _tmpSodium = _cursor.getFloat(_cursorIndexOfSodium);
            final float _tmpVitaminA;
            _tmpVitaminA = _cursor.getFloat(_cursorIndexOfVitaminA);
            final float _tmpVitaminC;
            _tmpVitaminC = _cursor.getFloat(_cursorIndexOfVitaminC);
            final float _tmpVitaminD;
            _tmpVitaminD = _cursor.getFloat(_cursorIndexOfVitaminD);
            final float _tmpVitaminB12;
            _tmpVitaminB12 = _cursor.getFloat(_cursorIndexOfVitaminB12);
            final float _tmpCalcium;
            _tmpCalcium = _cursor.getFloat(_cursorIndexOfCalcium);
            final float _tmpIron;
            _tmpIron = _cursor.getFloat(_cursorIndexOfIron);
            final float _tmpPotassium;
            _tmpPotassium = _cursor.getFloat(_cursorIndexOfPotassium);
            final float _tmpMagnesium;
            _tmpMagnesium = _cursor.getFloat(_cursorIndexOfMagnesium);
            final float _tmpZinc;
            _tmpZinc = _cursor.getFloat(_cursorIndexOfZinc);
            final String _tmpImageUrl;
            _tmpImageUrl = _cursor.getString(_cursorIndexOfImageUrl);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            _item = new FoodLogEntity(_tmpId,_tmpUserId,_tmpDate,_tmpMealType,_tmpFoodName,_tmpQuantity,_tmpUnit,_tmpCalories,_tmpProtein,_tmpCarbs,_tmpFat,_tmpFiber,_tmpSugar,_tmpSodium,_tmpVitaminA,_tmpVitaminC,_tmpVitaminD,_tmpVitaminB12,_tmpCalcium,_tmpIron,_tmpPotassium,_tmpMagnesium,_tmpZinc,_tmpImageUrl,_tmpTimestamp);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<FoodLogEntity>> getForMeal(final int uid, final String date, final String meal) {
    final String _sql = "SELECT * FROM food_log WHERE userId=? AND date=? AND mealType=? ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 3);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, uid);
    _argIndex = 2;
    _statement.bindString(_argIndex, date);
    _argIndex = 3;
    _statement.bindString(_argIndex, meal);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"food_log"}, new Callable<List<FoodLogEntity>>() {
      @Override
      @NonNull
      public List<FoodLogEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfMealType = CursorUtil.getColumnIndexOrThrow(_cursor, "mealType");
          final int _cursorIndexOfFoodName = CursorUtil.getColumnIndexOrThrow(_cursor, "foodName");
          final int _cursorIndexOfQuantity = CursorUtil.getColumnIndexOrThrow(_cursor, "quantity");
          final int _cursorIndexOfUnit = CursorUtil.getColumnIndexOrThrow(_cursor, "unit");
          final int _cursorIndexOfCalories = CursorUtil.getColumnIndexOrThrow(_cursor, "calories");
          final int _cursorIndexOfProtein = CursorUtil.getColumnIndexOrThrow(_cursor, "protein");
          final int _cursorIndexOfCarbs = CursorUtil.getColumnIndexOrThrow(_cursor, "carbs");
          final int _cursorIndexOfFat = CursorUtil.getColumnIndexOrThrow(_cursor, "fat");
          final int _cursorIndexOfFiber = CursorUtil.getColumnIndexOrThrow(_cursor, "fiber");
          final int _cursorIndexOfSugar = CursorUtil.getColumnIndexOrThrow(_cursor, "sugar");
          final int _cursorIndexOfSodium = CursorUtil.getColumnIndexOrThrow(_cursor, "sodium");
          final int _cursorIndexOfVitaminA = CursorUtil.getColumnIndexOrThrow(_cursor, "vitaminA");
          final int _cursorIndexOfVitaminC = CursorUtil.getColumnIndexOrThrow(_cursor, "vitaminC");
          final int _cursorIndexOfVitaminD = CursorUtil.getColumnIndexOrThrow(_cursor, "vitaminD");
          final int _cursorIndexOfVitaminB12 = CursorUtil.getColumnIndexOrThrow(_cursor, "vitaminB12");
          final int _cursorIndexOfCalcium = CursorUtil.getColumnIndexOrThrow(_cursor, "calcium");
          final int _cursorIndexOfIron = CursorUtil.getColumnIndexOrThrow(_cursor, "iron");
          final int _cursorIndexOfPotassium = CursorUtil.getColumnIndexOrThrow(_cursor, "potassium");
          final int _cursorIndexOfMagnesium = CursorUtil.getColumnIndexOrThrow(_cursor, "magnesium");
          final int _cursorIndexOfZinc = CursorUtil.getColumnIndexOrThrow(_cursor, "zinc");
          final int _cursorIndexOfImageUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "imageUrl");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final List<FoodLogEntity> _result = new ArrayList<FoodLogEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final FoodLogEntity _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final int _tmpUserId;
            _tmpUserId = _cursor.getInt(_cursorIndexOfUserId);
            final String _tmpDate;
            _tmpDate = _cursor.getString(_cursorIndexOfDate);
            final String _tmpMealType;
            _tmpMealType = _cursor.getString(_cursorIndexOfMealType);
            final String _tmpFoodName;
            _tmpFoodName = _cursor.getString(_cursorIndexOfFoodName);
            final float _tmpQuantity;
            _tmpQuantity = _cursor.getFloat(_cursorIndexOfQuantity);
            final String _tmpUnit;
            _tmpUnit = _cursor.getString(_cursorIndexOfUnit);
            final float _tmpCalories;
            _tmpCalories = _cursor.getFloat(_cursorIndexOfCalories);
            final float _tmpProtein;
            _tmpProtein = _cursor.getFloat(_cursorIndexOfProtein);
            final float _tmpCarbs;
            _tmpCarbs = _cursor.getFloat(_cursorIndexOfCarbs);
            final float _tmpFat;
            _tmpFat = _cursor.getFloat(_cursorIndexOfFat);
            final float _tmpFiber;
            _tmpFiber = _cursor.getFloat(_cursorIndexOfFiber);
            final float _tmpSugar;
            _tmpSugar = _cursor.getFloat(_cursorIndexOfSugar);
            final float _tmpSodium;
            _tmpSodium = _cursor.getFloat(_cursorIndexOfSodium);
            final float _tmpVitaminA;
            _tmpVitaminA = _cursor.getFloat(_cursorIndexOfVitaminA);
            final float _tmpVitaminC;
            _tmpVitaminC = _cursor.getFloat(_cursorIndexOfVitaminC);
            final float _tmpVitaminD;
            _tmpVitaminD = _cursor.getFloat(_cursorIndexOfVitaminD);
            final float _tmpVitaminB12;
            _tmpVitaminB12 = _cursor.getFloat(_cursorIndexOfVitaminB12);
            final float _tmpCalcium;
            _tmpCalcium = _cursor.getFloat(_cursorIndexOfCalcium);
            final float _tmpIron;
            _tmpIron = _cursor.getFloat(_cursorIndexOfIron);
            final float _tmpPotassium;
            _tmpPotassium = _cursor.getFloat(_cursorIndexOfPotassium);
            final float _tmpMagnesium;
            _tmpMagnesium = _cursor.getFloat(_cursorIndexOfMagnesium);
            final float _tmpZinc;
            _tmpZinc = _cursor.getFloat(_cursorIndexOfZinc);
            final String _tmpImageUrl;
            _tmpImageUrl = _cursor.getString(_cursorIndexOfImageUrl);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            _item = new FoodLogEntity(_tmpId,_tmpUserId,_tmpDate,_tmpMealType,_tmpFoodName,_tmpQuantity,_tmpUnit,_tmpCalories,_tmpProtein,_tmpCarbs,_tmpFat,_tmpFiber,_tmpSugar,_tmpSodium,_tmpVitaminA,_tmpVitaminC,_tmpVitaminD,_tmpVitaminB12,_tmpCalcium,_tmpIron,_tmpPotassium,_tmpMagnesium,_tmpZinc,_tmpImageUrl,_tmpTimestamp);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<DateValue>> getCaloriesByRange(final int uid, final String from) {
    final String _sql = "SELECT date, SUM(calories) as calories FROM food_log WHERE userId=? AND date>=? GROUP BY date ORDER BY date";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, uid);
    _argIndex = 2;
    _statement.bindString(_argIndex, from);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"food_log"}, new Callable<List<DateValue>>() {
      @Override
      @NonNull
      public List<DateValue> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfDate = 0;
          final int _cursorIndexOfCalories = 1;
          final List<DateValue> _result = new ArrayList<DateValue>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DateValue _item;
            final String _tmpDate;
            _tmpDate = _cursor.getString(_cursorIndexOfDate);
            final float _tmpCalories;
            _tmpCalories = _cursor.getFloat(_cursorIndexOfCalories);
            _item = new DateValue(_tmpDate,_tmpCalories);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object findByNameMealDate(final int uid, final String date, final String meal,
      final String name, final String unit, final Continuation<? super FoodLogEntity> $completion) {
    final String _sql = "SELECT * FROM food_log WHERE userId=? AND date=? AND mealType=? AND foodName=? AND unit=? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 5);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, uid);
    _argIndex = 2;
    _statement.bindString(_argIndex, date);
    _argIndex = 3;
    _statement.bindString(_argIndex, meal);
    _argIndex = 4;
    _statement.bindString(_argIndex, name);
    _argIndex = 5;
    _statement.bindString(_argIndex, unit);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<FoodLogEntity>() {
      @Override
      @Nullable
      public FoodLogEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfMealType = CursorUtil.getColumnIndexOrThrow(_cursor, "mealType");
          final int _cursorIndexOfFoodName = CursorUtil.getColumnIndexOrThrow(_cursor, "foodName");
          final int _cursorIndexOfQuantity = CursorUtil.getColumnIndexOrThrow(_cursor, "quantity");
          final int _cursorIndexOfUnit = CursorUtil.getColumnIndexOrThrow(_cursor, "unit");
          final int _cursorIndexOfCalories = CursorUtil.getColumnIndexOrThrow(_cursor, "calories");
          final int _cursorIndexOfProtein = CursorUtil.getColumnIndexOrThrow(_cursor, "protein");
          final int _cursorIndexOfCarbs = CursorUtil.getColumnIndexOrThrow(_cursor, "carbs");
          final int _cursorIndexOfFat = CursorUtil.getColumnIndexOrThrow(_cursor, "fat");
          final int _cursorIndexOfFiber = CursorUtil.getColumnIndexOrThrow(_cursor, "fiber");
          final int _cursorIndexOfSugar = CursorUtil.getColumnIndexOrThrow(_cursor, "sugar");
          final int _cursorIndexOfSodium = CursorUtil.getColumnIndexOrThrow(_cursor, "sodium");
          final int _cursorIndexOfVitaminA = CursorUtil.getColumnIndexOrThrow(_cursor, "vitaminA");
          final int _cursorIndexOfVitaminC = CursorUtil.getColumnIndexOrThrow(_cursor, "vitaminC");
          final int _cursorIndexOfVitaminD = CursorUtil.getColumnIndexOrThrow(_cursor, "vitaminD");
          final int _cursorIndexOfVitaminB12 = CursorUtil.getColumnIndexOrThrow(_cursor, "vitaminB12");
          final int _cursorIndexOfCalcium = CursorUtil.getColumnIndexOrThrow(_cursor, "calcium");
          final int _cursorIndexOfIron = CursorUtil.getColumnIndexOrThrow(_cursor, "iron");
          final int _cursorIndexOfPotassium = CursorUtil.getColumnIndexOrThrow(_cursor, "potassium");
          final int _cursorIndexOfMagnesium = CursorUtil.getColumnIndexOrThrow(_cursor, "magnesium");
          final int _cursorIndexOfZinc = CursorUtil.getColumnIndexOrThrow(_cursor, "zinc");
          final int _cursorIndexOfImageUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "imageUrl");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final FoodLogEntity _result;
          if (_cursor.moveToFirst()) {
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final int _tmpUserId;
            _tmpUserId = _cursor.getInt(_cursorIndexOfUserId);
            final String _tmpDate;
            _tmpDate = _cursor.getString(_cursorIndexOfDate);
            final String _tmpMealType;
            _tmpMealType = _cursor.getString(_cursorIndexOfMealType);
            final String _tmpFoodName;
            _tmpFoodName = _cursor.getString(_cursorIndexOfFoodName);
            final float _tmpQuantity;
            _tmpQuantity = _cursor.getFloat(_cursorIndexOfQuantity);
            final String _tmpUnit;
            _tmpUnit = _cursor.getString(_cursorIndexOfUnit);
            final float _tmpCalories;
            _tmpCalories = _cursor.getFloat(_cursorIndexOfCalories);
            final float _tmpProtein;
            _tmpProtein = _cursor.getFloat(_cursorIndexOfProtein);
            final float _tmpCarbs;
            _tmpCarbs = _cursor.getFloat(_cursorIndexOfCarbs);
            final float _tmpFat;
            _tmpFat = _cursor.getFloat(_cursorIndexOfFat);
            final float _tmpFiber;
            _tmpFiber = _cursor.getFloat(_cursorIndexOfFiber);
            final float _tmpSugar;
            _tmpSugar = _cursor.getFloat(_cursorIndexOfSugar);
            final float _tmpSodium;
            _tmpSodium = _cursor.getFloat(_cursorIndexOfSodium);
            final float _tmpVitaminA;
            _tmpVitaminA = _cursor.getFloat(_cursorIndexOfVitaminA);
            final float _tmpVitaminC;
            _tmpVitaminC = _cursor.getFloat(_cursorIndexOfVitaminC);
            final float _tmpVitaminD;
            _tmpVitaminD = _cursor.getFloat(_cursorIndexOfVitaminD);
            final float _tmpVitaminB12;
            _tmpVitaminB12 = _cursor.getFloat(_cursorIndexOfVitaminB12);
            final float _tmpCalcium;
            _tmpCalcium = _cursor.getFloat(_cursorIndexOfCalcium);
            final float _tmpIron;
            _tmpIron = _cursor.getFloat(_cursorIndexOfIron);
            final float _tmpPotassium;
            _tmpPotassium = _cursor.getFloat(_cursorIndexOfPotassium);
            final float _tmpMagnesium;
            _tmpMagnesium = _cursor.getFloat(_cursorIndexOfMagnesium);
            final float _tmpZinc;
            _tmpZinc = _cursor.getFloat(_cursorIndexOfZinc);
            final String _tmpImageUrl;
            _tmpImageUrl = _cursor.getString(_cursorIndexOfImageUrl);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            _result = new FoodLogEntity(_tmpId,_tmpUserId,_tmpDate,_tmpMealType,_tmpFoodName,_tmpQuantity,_tmpUnit,_tmpCalories,_tmpProtein,_tmpCarbs,_tmpFat,_tmpFiber,_tmpSugar,_tmpSodium,_tmpVitaminA,_tmpVitaminC,_tmpVitaminD,_tmpVitaminB12,_tmpCalcium,_tmpIron,_tmpPotassium,_tmpMagnesium,_tmpZinc,_tmpImageUrl,_tmpTimestamp);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getRecent(final int uid,
      final Continuation<? super List<FoodLogEntity>> $completion) {
    final String _sql = "SELECT * FROM food_log WHERE userId=? ORDER BY timestamp DESC LIMIT 30";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, uid);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<FoodLogEntity>>() {
      @Override
      @NonNull
      public List<FoodLogEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfMealType = CursorUtil.getColumnIndexOrThrow(_cursor, "mealType");
          final int _cursorIndexOfFoodName = CursorUtil.getColumnIndexOrThrow(_cursor, "foodName");
          final int _cursorIndexOfQuantity = CursorUtil.getColumnIndexOrThrow(_cursor, "quantity");
          final int _cursorIndexOfUnit = CursorUtil.getColumnIndexOrThrow(_cursor, "unit");
          final int _cursorIndexOfCalories = CursorUtil.getColumnIndexOrThrow(_cursor, "calories");
          final int _cursorIndexOfProtein = CursorUtil.getColumnIndexOrThrow(_cursor, "protein");
          final int _cursorIndexOfCarbs = CursorUtil.getColumnIndexOrThrow(_cursor, "carbs");
          final int _cursorIndexOfFat = CursorUtil.getColumnIndexOrThrow(_cursor, "fat");
          final int _cursorIndexOfFiber = CursorUtil.getColumnIndexOrThrow(_cursor, "fiber");
          final int _cursorIndexOfSugar = CursorUtil.getColumnIndexOrThrow(_cursor, "sugar");
          final int _cursorIndexOfSodium = CursorUtil.getColumnIndexOrThrow(_cursor, "sodium");
          final int _cursorIndexOfVitaminA = CursorUtil.getColumnIndexOrThrow(_cursor, "vitaminA");
          final int _cursorIndexOfVitaminC = CursorUtil.getColumnIndexOrThrow(_cursor, "vitaminC");
          final int _cursorIndexOfVitaminD = CursorUtil.getColumnIndexOrThrow(_cursor, "vitaminD");
          final int _cursorIndexOfVitaminB12 = CursorUtil.getColumnIndexOrThrow(_cursor, "vitaminB12");
          final int _cursorIndexOfCalcium = CursorUtil.getColumnIndexOrThrow(_cursor, "calcium");
          final int _cursorIndexOfIron = CursorUtil.getColumnIndexOrThrow(_cursor, "iron");
          final int _cursorIndexOfPotassium = CursorUtil.getColumnIndexOrThrow(_cursor, "potassium");
          final int _cursorIndexOfMagnesium = CursorUtil.getColumnIndexOrThrow(_cursor, "magnesium");
          final int _cursorIndexOfZinc = CursorUtil.getColumnIndexOrThrow(_cursor, "zinc");
          final int _cursorIndexOfImageUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "imageUrl");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final List<FoodLogEntity> _result = new ArrayList<FoodLogEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final FoodLogEntity _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final int _tmpUserId;
            _tmpUserId = _cursor.getInt(_cursorIndexOfUserId);
            final String _tmpDate;
            _tmpDate = _cursor.getString(_cursorIndexOfDate);
            final String _tmpMealType;
            _tmpMealType = _cursor.getString(_cursorIndexOfMealType);
            final String _tmpFoodName;
            _tmpFoodName = _cursor.getString(_cursorIndexOfFoodName);
            final float _tmpQuantity;
            _tmpQuantity = _cursor.getFloat(_cursorIndexOfQuantity);
            final String _tmpUnit;
            _tmpUnit = _cursor.getString(_cursorIndexOfUnit);
            final float _tmpCalories;
            _tmpCalories = _cursor.getFloat(_cursorIndexOfCalories);
            final float _tmpProtein;
            _tmpProtein = _cursor.getFloat(_cursorIndexOfProtein);
            final float _tmpCarbs;
            _tmpCarbs = _cursor.getFloat(_cursorIndexOfCarbs);
            final float _tmpFat;
            _tmpFat = _cursor.getFloat(_cursorIndexOfFat);
            final float _tmpFiber;
            _tmpFiber = _cursor.getFloat(_cursorIndexOfFiber);
            final float _tmpSugar;
            _tmpSugar = _cursor.getFloat(_cursorIndexOfSugar);
            final float _tmpSodium;
            _tmpSodium = _cursor.getFloat(_cursorIndexOfSodium);
            final float _tmpVitaminA;
            _tmpVitaminA = _cursor.getFloat(_cursorIndexOfVitaminA);
            final float _tmpVitaminC;
            _tmpVitaminC = _cursor.getFloat(_cursorIndexOfVitaminC);
            final float _tmpVitaminD;
            _tmpVitaminD = _cursor.getFloat(_cursorIndexOfVitaminD);
            final float _tmpVitaminB12;
            _tmpVitaminB12 = _cursor.getFloat(_cursorIndexOfVitaminB12);
            final float _tmpCalcium;
            _tmpCalcium = _cursor.getFloat(_cursorIndexOfCalcium);
            final float _tmpIron;
            _tmpIron = _cursor.getFloat(_cursorIndexOfIron);
            final float _tmpPotassium;
            _tmpPotassium = _cursor.getFloat(_cursorIndexOfPotassium);
            final float _tmpMagnesium;
            _tmpMagnesium = _cursor.getFloat(_cursorIndexOfMagnesium);
            final float _tmpZinc;
            _tmpZinc = _cursor.getFloat(_cursorIndexOfZinc);
            final String _tmpImageUrl;
            _tmpImageUrl = _cursor.getString(_cursorIndexOfImageUrl);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            _item = new FoodLogEntity(_tmpId,_tmpUserId,_tmpDate,_tmpMealType,_tmpFoodName,_tmpQuantity,_tmpUnit,_tmpCalories,_tmpProtein,_tmpCarbs,_tmpFat,_tmpFiber,_tmpSugar,_tmpSodium,_tmpVitaminA,_tmpVitaminC,_tmpVitaminD,_tmpVitaminB12,_tmpCalcium,_tmpIron,_tmpPotassium,_tmpMagnesium,_tmpZinc,_tmpImageUrl,_tmpTimestamp);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}

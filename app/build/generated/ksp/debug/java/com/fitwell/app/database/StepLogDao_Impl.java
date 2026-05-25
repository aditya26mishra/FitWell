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
import java.lang.Float;
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
public final class StepLogDao_Impl implements StepLogDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<StepLogEntity> __insertionAdapterOfStepLogEntity;

  private final EntityDeletionOrUpdateAdapter<StepLogEntity> __updateAdapterOfStepLogEntity;

  public StepLogDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfStepLogEntity = new EntityInsertionAdapter<StepLogEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `step_log` (`id`,`userId`,`date`,`steps`,`goalSteps`,`distanceKm`,`caloriesBurned`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final StepLogEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getUserId());
        statement.bindString(3, entity.getDate());
        statement.bindLong(4, entity.getSteps());
        statement.bindLong(5, entity.getGoalSteps());
        statement.bindDouble(6, entity.getDistanceKm());
        statement.bindDouble(7, entity.getCaloriesBurned());
      }
    };
    this.__updateAdapterOfStepLogEntity = new EntityDeletionOrUpdateAdapter<StepLogEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `step_log` SET `id` = ?,`userId` = ?,`date` = ?,`steps` = ?,`goalSteps` = ?,`distanceKm` = ?,`caloriesBurned` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final StepLogEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getUserId());
        statement.bindString(3, entity.getDate());
        statement.bindLong(4, entity.getSteps());
        statement.bindLong(5, entity.getGoalSteps());
        statement.bindDouble(6, entity.getDistanceKm());
        statement.bindDouble(7, entity.getCaloriesBurned());
        statement.bindLong(8, entity.getId());
      }
    };
  }

  @Override
  public Object insert(final StepLogEntity log, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfStepLogEntity.insertAndReturnId(log);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final StepLogEntity log, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfStepLogEntity.handle(log);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object getForDate(final int uid, final String date,
      final Continuation<? super StepLogEntity> $completion) {
    final String _sql = "SELECT * FROM step_log WHERE userId=? AND date=? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, uid);
    _argIndex = 2;
    _statement.bindString(_argIndex, date);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<StepLogEntity>() {
      @Override
      @Nullable
      public StepLogEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfSteps = CursorUtil.getColumnIndexOrThrow(_cursor, "steps");
          final int _cursorIndexOfGoalSteps = CursorUtil.getColumnIndexOrThrow(_cursor, "goalSteps");
          final int _cursorIndexOfDistanceKm = CursorUtil.getColumnIndexOrThrow(_cursor, "distanceKm");
          final int _cursorIndexOfCaloriesBurned = CursorUtil.getColumnIndexOrThrow(_cursor, "caloriesBurned");
          final StepLogEntity _result;
          if (_cursor.moveToFirst()) {
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final int _tmpUserId;
            _tmpUserId = _cursor.getInt(_cursorIndexOfUserId);
            final String _tmpDate;
            _tmpDate = _cursor.getString(_cursorIndexOfDate);
            final int _tmpSteps;
            _tmpSteps = _cursor.getInt(_cursorIndexOfSteps);
            final int _tmpGoalSteps;
            _tmpGoalSteps = _cursor.getInt(_cursorIndexOfGoalSteps);
            final float _tmpDistanceKm;
            _tmpDistanceKm = _cursor.getFloat(_cursorIndexOfDistanceKm);
            final float _tmpCaloriesBurned;
            _tmpCaloriesBurned = _cursor.getFloat(_cursorIndexOfCaloriesBurned);
            _result = new StepLogEntity(_tmpId,_tmpUserId,_tmpDate,_tmpSteps,_tmpGoalSteps,_tmpDistanceKm,_tmpCaloriesBurned);
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
  public Flow<StepLogEntity> getForDateFlow(final int uid, final String date) {
    final String _sql = "SELECT * FROM step_log WHERE userId=? AND date=? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, uid);
    _argIndex = 2;
    _statement.bindString(_argIndex, date);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"step_log"}, new Callable<StepLogEntity>() {
      @Override
      @Nullable
      public StepLogEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfSteps = CursorUtil.getColumnIndexOrThrow(_cursor, "steps");
          final int _cursorIndexOfGoalSteps = CursorUtil.getColumnIndexOrThrow(_cursor, "goalSteps");
          final int _cursorIndexOfDistanceKm = CursorUtil.getColumnIndexOrThrow(_cursor, "distanceKm");
          final int _cursorIndexOfCaloriesBurned = CursorUtil.getColumnIndexOrThrow(_cursor, "caloriesBurned");
          final StepLogEntity _result;
          if (_cursor.moveToFirst()) {
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final int _tmpUserId;
            _tmpUserId = _cursor.getInt(_cursorIndexOfUserId);
            final String _tmpDate;
            _tmpDate = _cursor.getString(_cursorIndexOfDate);
            final int _tmpSteps;
            _tmpSteps = _cursor.getInt(_cursorIndexOfSteps);
            final int _tmpGoalSteps;
            _tmpGoalSteps = _cursor.getInt(_cursorIndexOfGoalSteps);
            final float _tmpDistanceKm;
            _tmpDistanceKm = _cursor.getFloat(_cursorIndexOfDistanceKm);
            final float _tmpCaloriesBurned;
            _tmpCaloriesBurned = _cursor.getFloat(_cursorIndexOfCaloriesBurned);
            _result = new StepLogEntity(_tmpId,_tmpUserId,_tmpDate,_tmpSteps,_tmpGoalSteps,_tmpDistanceKm,_tmpCaloriesBurned);
          } else {
            _result = null;
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
  public Flow<List<StepLogEntity>> getByRange(final int uid, final String from) {
    final String _sql = "SELECT * FROM step_log WHERE userId=? AND date>=? ORDER BY date";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, uid);
    _argIndex = 2;
    _statement.bindString(_argIndex, from);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"step_log"}, new Callable<List<StepLogEntity>>() {
      @Override
      @NonNull
      public List<StepLogEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfSteps = CursorUtil.getColumnIndexOrThrow(_cursor, "steps");
          final int _cursorIndexOfGoalSteps = CursorUtil.getColumnIndexOrThrow(_cursor, "goalSteps");
          final int _cursorIndexOfDistanceKm = CursorUtil.getColumnIndexOrThrow(_cursor, "distanceKm");
          final int _cursorIndexOfCaloriesBurned = CursorUtil.getColumnIndexOrThrow(_cursor, "caloriesBurned");
          final List<StepLogEntity> _result = new ArrayList<StepLogEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final StepLogEntity _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final int _tmpUserId;
            _tmpUserId = _cursor.getInt(_cursorIndexOfUserId);
            final String _tmpDate;
            _tmpDate = _cursor.getString(_cursorIndexOfDate);
            final int _tmpSteps;
            _tmpSteps = _cursor.getInt(_cursorIndexOfSteps);
            final int _tmpGoalSteps;
            _tmpGoalSteps = _cursor.getInt(_cursorIndexOfGoalSteps);
            final float _tmpDistanceKm;
            _tmpDistanceKm = _cursor.getFloat(_cursorIndexOfDistanceKm);
            final float _tmpCaloriesBurned;
            _tmpCaloriesBurned = _cursor.getFloat(_cursorIndexOfCaloriesBurned);
            _item = new StepLogEntity(_tmpId,_tmpUserId,_tmpDate,_tmpSteps,_tmpGoalSteps,_tmpDistanceKm,_tmpCaloriesBurned);
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
  public Flow<Float> getAvg(final int uid, final String from) {
    final String _sql = "SELECT AVG(steps) FROM step_log WHERE userId=? AND date>=?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, uid);
    _argIndex = 2;
    _statement.bindString(_argIndex, from);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"step_log"}, new Callable<Float>() {
      @Override
      @Nullable
      public Float call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Float _result;
          if (_cursor.moveToFirst()) {
            final Float _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getFloat(0);
            }
            _result = _tmp;
          } else {
            _result = null;
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}

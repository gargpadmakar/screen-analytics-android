package com.example.screenanalytics.storage;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
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
public final class AnalyticsEventDao_Impl implements AnalyticsEventDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<AnalyticsEventEntity> __insertionAdapterOfAnalyticsEventEntity;

  private final SharedSQLiteStatement __preparedStmtOfClearAllEvents;

  public AnalyticsEventDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfAnalyticsEventEntity = new EntityInsertionAdapter<AnalyticsEventEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `analytics_events` (`event_id`,`screen_name`,`timestamp`,`session_id`,`duration_millis`) VALUES (?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final AnalyticsEventEntity entity) {
        statement.bindString(1, entity.getEventId());
        statement.bindString(2, entity.getScreenName());
        statement.bindLong(3, entity.getTimestamp());
        statement.bindString(4, entity.getSessionId());
        statement.bindLong(5, entity.getDurationMillis());
      }
    };
    this.__preparedStmtOfClearAllEvents = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM analytics_events";
        return _query;
      }
    };
  }

  @Override
  public Object insertEvent(final AnalyticsEventEntity event,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfAnalyticsEventEntity.insert(event);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object clearAllEvents(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClearAllEvents.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfClearAllEvents.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<AnalyticsEventEntity>> getAllEvents() {
    final String _sql = "SELECT * FROM analytics_events ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"analytics_events"}, new Callable<List<AnalyticsEventEntity>>() {
      @Override
      @NonNull
      public List<AnalyticsEventEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfEventId = CursorUtil.getColumnIndexOrThrow(_cursor, "event_id");
          final int _cursorIndexOfScreenName = CursorUtil.getColumnIndexOrThrow(_cursor, "screen_name");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfSessionId = CursorUtil.getColumnIndexOrThrow(_cursor, "session_id");
          final int _cursorIndexOfDurationMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "duration_millis");
          final List<AnalyticsEventEntity> _result = new ArrayList<AnalyticsEventEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AnalyticsEventEntity _item;
            final String _tmpEventId;
            _tmpEventId = _cursor.getString(_cursorIndexOfEventId);
            final String _tmpScreenName;
            _tmpScreenName = _cursor.getString(_cursorIndexOfScreenName);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final String _tmpSessionId;
            _tmpSessionId = _cursor.getString(_cursorIndexOfSessionId);
            final long _tmpDurationMillis;
            _tmpDurationMillis = _cursor.getLong(_cursorIndexOfDurationMillis);
            _item = new AnalyticsEventEntity(_tmpEventId,_tmpScreenName,_tmpTimestamp,_tmpSessionId,_tmpDurationMillis);
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
  public Object getTotalEventCount(final Continuation<? super Long> $completion) {
    final String _sql = "SELECT COUNT(*) FROM analytics_events";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Long _result;
          if (_cursor.moveToFirst()) {
            final long _tmp;
            _tmp = _cursor.getLong(0);
            _result = _tmp;
          } else {
            _result = 0L;
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
  public Object getScreenStatistics(
      final Continuation<? super List<ScreenStatisticsData>> $completion) {
    final String _sql = "SELECT screen_name as screenName, COUNT(*) as viewCount, AVG(duration_millis) as averageDurationMillis, MIN(duration_millis) as minDurationMillis, MAX(duration_millis) as maxDurationMillis FROM analytics_events GROUP BY screen_name ORDER BY viewCount DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<ScreenStatisticsData>>() {
      @Override
      @NonNull
      public List<ScreenStatisticsData> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfScreenName = 0;
          final int _cursorIndexOfViewCount = 1;
          final int _cursorIndexOfAverageDurationMillis = 2;
          final int _cursorIndexOfMinDurationMillis = 3;
          final int _cursorIndexOfMaxDurationMillis = 4;
          final List<ScreenStatisticsData> _result = new ArrayList<ScreenStatisticsData>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ScreenStatisticsData _item;
            final String _tmpScreenName;
            _tmpScreenName = _cursor.getString(_cursorIndexOfScreenName);
            final long _tmpViewCount;
            _tmpViewCount = _cursor.getLong(_cursorIndexOfViewCount);
            final long _tmpAverageDurationMillis;
            _tmpAverageDurationMillis = _cursor.getLong(_cursorIndexOfAverageDurationMillis);
            final Long _tmpMinDurationMillis;
            if (_cursor.isNull(_cursorIndexOfMinDurationMillis)) {
              _tmpMinDurationMillis = null;
            } else {
              _tmpMinDurationMillis = _cursor.getLong(_cursorIndexOfMinDurationMillis);
            }
            final Long _tmpMaxDurationMillis;
            if (_cursor.isNull(_cursorIndexOfMaxDurationMillis)) {
              _tmpMaxDurationMillis = null;
            } else {
              _tmpMaxDurationMillis = _cursor.getLong(_cursorIndexOfMaxDurationMillis);
            }
            _item = new ScreenStatisticsData(_tmpScreenName,_tmpViewCount,_tmpAverageDurationMillis,_tmpMinDurationMillis,_tmpMaxDurationMillis);
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

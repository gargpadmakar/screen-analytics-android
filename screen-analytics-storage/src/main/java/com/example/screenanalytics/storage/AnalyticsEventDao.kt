package com.example.screenanalytics.storage

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AnalyticsEventDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: AnalyticsEventEntity)

    @Query("SELECT * FROM analytics_events ORDER BY timestamp DESC")
    fun getAllEvents(): Flow<List<AnalyticsEventEntity>>
    
    @Query("DELETE FROM analytics_events")
    suspend fun clearAllEvents()

    @Query("SELECT COUNT(*) FROM analytics_events")
    suspend fun getTotalEventCount(): Long
    
    @Query("SELECT screen_name as screenName, COUNT(*) as viewCount, AVG(duration_millis) as averageDurationMillis, MIN(duration_millis) as minDurationMillis, MAX(duration_millis) as maxDurationMillis FROM analytics_events GROUP BY screen_name ORDER BY viewCount DESC")
    suspend fun getScreenStatistics(): List<com.example.screenanalytics.storage.ScreenStatisticsData>
}

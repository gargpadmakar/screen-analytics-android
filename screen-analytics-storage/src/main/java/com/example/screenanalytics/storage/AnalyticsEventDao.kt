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
}

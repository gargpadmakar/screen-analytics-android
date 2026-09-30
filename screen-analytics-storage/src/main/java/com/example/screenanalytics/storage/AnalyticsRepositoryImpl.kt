package com.example.screenanalytics.storage

import com.example.screenanalytics.core.AnalyticsRepository
import com.example.screenanalytics.core.ScreenEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AnalyticsRepositoryImpl(
    private val database: AnalyticsDatabase,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) : AnalyticsRepository {

    override fun saveEvent(event: ScreenEvent) {
        coroutineScope.launch {
            val entity = AnalyticsEventEntity(
                eventId = event.eventId,
                screenName = event.screenName,
                timestamp = event.timestamp,
                sessionId = event.sessionId,
                durationMillis = event.durationMillis
            )
            database.analyticsDao().insertEvent(entity)
            println("ScreenAnalytics: Event saved to Room: ${event.screenName}")
        }
    }
}

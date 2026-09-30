package com.example.screenanalytics.core

object AnalyticsExportManager {

    fun exportCsv(events: List<ScreenEvent>): String {
        val builder = java.lang.StringBuilder()
        builder.append("event_id,screen_name,timestamp,session_id,duration_ms\n")
        
        for (event in events) {
            builder.append("${event.eventId},${event.screenName},${event.timestamp},${event.sessionId},${event.durationMillis}\n")
        }
        return builder.toString()
    }
}

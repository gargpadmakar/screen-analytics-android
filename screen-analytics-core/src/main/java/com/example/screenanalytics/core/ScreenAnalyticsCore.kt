package com.example.screenanalytics.core

data class ScreenEvent(
    val eventId: String,
    val screenName: String,
    val timestamp: Long,
    val sessionId: String,
    val durationMillis: Long,
    val properties: Map<String, String> = emptyMap()
)

interface AnalyticsRepository {
    fun saveEvent(event: ScreenEvent)
}

class SessionManager {
    var currentSessionId: String = java.util.UUID.randomUUID().toString()
        private set

    fun startNewSession() {
        currentSessionId = java.util.UUID.randomUUID().toString()
    }
}

class DurationTracker {
    private var startTimeMillis: Long = 0

    fun start() {
        startTimeMillis = System.currentTimeMillis()
    }

    fun stop(): Long {
        val duration = System.currentTimeMillis() - startTimeMillis
        startTimeMillis = 0
        return duration
    }
}

object ScreenAnalyticsCore {
    var isEnabled: Boolean = true
    private var repository: AnalyticsRepository? = null
    private val sessionManager = SessionManager()
    
    fun init(repo: AnalyticsRepository) {
        this.repository = repo
    }

    fun trackScreen(screenName: String, durationMillis: Long) {
        if (!isEnabled) return
        
        val event = ScreenEvent(
            eventId = java.util.UUID.randomUUID().toString(),
            screenName = screenName,
            timestamp = System.currentTimeMillis(),
            sessionId = sessionManager.currentSessionId,
            durationMillis = durationMillis
        )
        
        try {
            repository?.saveEvent(event)
            // Debug logging
            println("ScreenAnalytics: Tracked screen $screenName for $durationMillis ms")
        } catch (e: Exception) {
            println("ScreenAnalytics: Error saving event - ${e.message}")
        }
    }
}

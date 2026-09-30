package com.example.screenanalytics.core

class ScreenDeduplicationManager {
    private var lastScreenName: String? = null
    private var lastTimestamp: Long = 0

    fun shouldTrack(screenName: String, timestamp: Long): Boolean {
        // Prevent duplicate consecutive screen events within 500ms
        if (screenName == lastScreenName && (timestamp - lastTimestamp) < 500) {
            return false
        }
        
        lastScreenName = screenName
        lastTimestamp = timestamp
        return true
    }
    
    fun reset() {
        lastScreenName = null
        lastTimestamp = 0
    }
}

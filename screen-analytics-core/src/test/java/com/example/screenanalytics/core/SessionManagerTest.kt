package com.example.screenanalytics.core

import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class SessionManagerTest {

    @Test
    fun `initial session ID is not null`() {
        val sessionManager = SessionManager()
        assertNotNull(sessionManager.currentSessionId)
    }

    @Test
    fun `startNewSession generates new session ID`() {
        val sessionManager = SessionManager()
        val oldId = sessionManager.currentSessionId
        
        sessionManager.startNewSession()
        val newId = sessionManager.currentSessionId
        
        assertNotEquals(oldId, newId)
    }
}

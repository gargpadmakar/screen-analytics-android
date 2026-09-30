package com.example.screenanalytics.compose

import androidx.navigation.NavController
import com.example.screenanalytics.core.DurationTracker
import com.example.screenanalytics.core.ScreenAnalyticsCore

object ScreenAnalyticsCompose {
    private val durationTracker = DurationTracker()
    private var currentScreenName: String? = null

    fun attach(navController: NavController) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            // Stop tracking previous screen
            val duration = durationTracker.stop()
            currentScreenName?.let {
                ScreenAnalyticsCore.trackScreen(it, duration)
            }

            // Start tracking new screen
            val newScreenName = destination.route ?: "UnknownScreen"
            currentScreenName = newScreenName
            durationTracker.start()
        }
    }
}

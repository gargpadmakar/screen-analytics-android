package com.example.screenanalytics.android

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.example.screenanalytics.core.DurationTracker
import com.example.screenanalytics.core.ScreenAnalyticsCore

object ScreenAnalytics {
    private var isTracking = false
    private val durationTracker = DurationTracker()
    private var currentScreenName: String? = null

    fun init(application: Application, repository: com.example.screenanalytics.core.AnalyticsRepository) {
        ScreenAnalyticsCore.init(repository)
    }

    fun startActivityTracking(application: Application) {
        if (isTracking) return
        isTracking = true

        application.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
            override fun onActivityStarted(activity: Activity) {}

            override fun onActivityResumed(activity: Activity) {
                currentScreenName = activity.javaClass.simpleName
                durationTracker.start()
            }

            override fun onActivityPaused(activity: Activity) {
                val duration = durationTracker.stop()
                currentScreenName?.let {
                    ScreenAnalyticsCore.trackScreen(it, duration)
                }
                currentScreenName = null
            }

            override fun onActivityStopped(activity: Activity) {}
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
            override fun onActivityDestroyed(activity: Activity) {}
        })
    }
}

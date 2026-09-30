package com.example.screenanalytics.android

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.example.screenanalytics.core.DurationTracker
import com.example.screenanalytics.core.ScreenAnalyticsCore

object ScreenAnalytics {
    private var isTracking = false
    private val durationTracker = DurationTracker()
    private var currentScreenName: String? = null

    fun init(application: Application, repository: com.example.screenanalytics.core.AnalyticsRepository) {
        ScreenAnalyticsCore.init(repository)
    }

    fun startActivityTracking(application: Application, trackFragments: Boolean = true) {
        if (isTracking) return
        isTracking = true

        application.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                if (trackFragments && activity is FragmentActivity) {
                    val fragmentActivity = activity
                    fragmentActivity.supportFragmentManager.registerFragmentLifecycleCallbacks(
                        object : FragmentManager.FragmentLifecycleCallbacks() {
                            override fun onFragmentResumed(fm: FragmentManager, f: Fragment) {
                                val screenName = f.javaClass.simpleName
                                // In a real implementation, you'd integrate the DeduplicationManager here
                                currentScreenName = screenName
                                durationTracker.start()
                            }

                            override fun onFragmentPaused(fm: FragmentManager, f: Fragment) {
                                val duration = durationTracker.stop()
                                currentScreenName?.let {
                                    ScreenAnalyticsCore.trackScreen(it, duration)
                                }
                                currentScreenName = null
                            }
                        }, 
                        true // true for recursive (child fragments)
                    )
                }
            }
            
            override fun onActivityStarted(activity: Activity) {}

            override fun onActivityResumed(activity: Activity) {
                // Track activity if it's not overridden by fragments
                val screenName = activity.javaClass.simpleName
                currentScreenName = screenName
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

package com.example.samplexmlapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.screenanalytics.android.ScreenAnalytics
import com.example.screenanalytics.core.ScreenAnalyticsCore
import com.example.screenanalytics.storage.AnalyticsDatabase
import com.example.screenanalytics.storage.AnalyticsRepositoryImpl

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize SDK manually for sample purposes (usually done in Application class)
        val db = AnalyticsDatabase.getDatabase(applicationContext)
        val repository = AnalyticsRepositoryImpl(db)
        ScreenAnalyticsCore.init(repository)

        // Register Activity Lifecycle Callbacks to automatically track XML Screens!
        ScreenAnalytics.startActivityTracking(application, trackFragments = true)

        findViewById<Button>(R.id.btnDetail).setOnClickListener {
            startActivity(Intent(this, DetailActivity::class.java))
        }

        findViewById<Button>(R.id.btnDashboard).setOnClickListener {
            startActivity(Intent(this, DashboardActivity::class.java))
        }
    }
}

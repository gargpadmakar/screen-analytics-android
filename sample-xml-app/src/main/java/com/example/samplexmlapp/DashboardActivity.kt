package com.example.samplexmlapp

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.screenanalytics.storage.AnalyticsDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DashboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val tvStats = findViewById<TextView>(R.id.tvStats)
        
        CoroutineScope(Dispatchers.IO).launch {
            val db = AnalyticsDatabase.getDatabase(applicationContext)
            val stats = db.analyticsDao().getScreenStatistics()
            
            var display = "Real-Time XML Analytics:\n\n"
            stats.forEach { stat ->
                display += "Screen: ${stat.screenName}\nViews: ${stat.viewCount}\nAvg Time: ${stat.averageDurationMillis / 1000}s\n\n"
            }
            
            withContext(Dispatchers.Main) {
                tvStats.text = display
            }
        }
    }
}

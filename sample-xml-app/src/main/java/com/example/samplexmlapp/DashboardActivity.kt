package com.example.samplexmlapp

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.screenanalytics.storage.AnalyticsDatabase
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DashboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val dynamicList = findViewById<LinearLayout>(R.id.dynamicList)
        val btnInject = findViewById<android.widget.Button>(R.id.btnInject)
        
        btnInject.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val db = AnalyticsDatabase.getDatabase(applicationContext)
                val repo = com.example.screenanalytics.storage.AnalyticsRepositoryImpl(db)
                repo.saveEvent(com.example.screenanalytics.core.ScreenEvent(
                    eventId = java.util.UUID.randomUUID().toString(),
                    screenName = "Manual_XML_Event_${System.currentTimeMillis() % 1000}",
                    timestamp = System.currentTimeMillis(),
                    sessionId = "ManualSession",
                    durationMillis = 3000L
                ))
                withContext(Dispatchers.Main) {
                    android.widget.Toast.makeText(this@DashboardActivity, "Event Injected! Re-open dashboard to see.", android.widget.Toast.LENGTH_SHORT).show()
                }
            }
        }
        
        CoroutineScope(Dispatchers.IO).launch {
            val db = AnalyticsDatabase.getDatabase(applicationContext)
            val stats = db.analyticsDao().getScreenStatistics()
            
            withContext(Dispatchers.Main) {
                dynamicList.removeAllViews() // clear loading or previous views
                
                if (stats.isEmpty()) {
                    val tvEmpty = TextView(this@DashboardActivity).apply {
                        text = "No analytics data found yet. Try navigating around!"
                        textSize = 16f
                    }
                    dynamicList.addView(tvEmpty)
                    return@withContext
                }

                stats.forEach { stat ->
                    // Create Card
                    val card = MaterialCardView(this@DashboardActivity).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        ).apply {
                            setMargins(0, 0, 0, 32)
                        }
                        radius = 32f // 16dp roughly
                        cardElevation = 8f
                    }

                    // Create inner container
                    val innerContainer = LinearLayout(this@DashboardActivity).apply {
                        orientation = LinearLayout.HORIZONTAL
                        layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                        setPadding(48, 48, 48, 48) // 16dp
                        gravity = Gravity.CENTER_VERTICAL
                    }

                    // Left Column (Titles)
                    val leftCol = LinearLayout(this@DashboardActivity).apply {
                        orientation = LinearLayout.VERTICAL
                        layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
                    }
                    
                    val tvScreenName = TextView(this@DashboardActivity).apply {
                        text = stat.screenName
                        textSize = 18f
                        setTypeface(null, android.graphics.Typeface.BOLD)
                        setTextColor(Color.BLACK)
                    }
                    val tvAvgTime = TextView(this@DashboardActivity).apply {
                        text = "Average time: ${stat.averageDurationMillis / 1000}s"
                        textSize = 14f
                        setTextColor(Color.DKGRAY)
                    }
                    leftCol.addView(tvScreenName)
                    leftCol.addView(tvAvgTime)

                    // Right Column (Views Badge)
                    val badge = TextView(this@DashboardActivity).apply {
                        text = "${stat.viewCount} views"
                        textSize = 14f
                        setTypeface(null, android.graphics.Typeface.BOLD)
                        setTextColor(Color.parseColor("#6200EE"))
                        setBackgroundColor(Color.parseColor("#1A6200EE")) // 10% opacity primary
                        setPadding(32, 16, 32, 16)
                    }

                    innerContainer.addView(leftCol)
                    innerContainer.addView(badge)
                    card.addView(innerContainer)

                    dynamicList.addView(card)
                }
            }
        }
    }
}

package com.example.samplecomposeapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.screenanalytics.compose.ScreenAnalyticsCompose
import com.example.screenanalytics.core.ScreenAnalyticsCore
import com.example.screenanalytics.storage.AnalyticsDatabase
import com.example.screenanalytics.storage.AnalyticsRepositoryImpl

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize SDK manually for sample purposes
        val db = AnalyticsDatabase.getDatabase(applicationContext)
        val repository = AnalyticsRepositoryImpl(db.analyticsEventDao())
        ScreenAnalyticsCore.init(repository)

        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    
    // Attach SDK for automatic screen tracking!
    ScreenAnalyticsCompose.attach(navController)

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Home Screen (Zero Analytics Code Here!)")
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { navController.navigate("profile") }) {
                    Text("Go to Profile")
                }
            }
        }
        composable("profile") {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Profile Screen (Zero Analytics Code Here!)")
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { navController.popBackStack() }) {
                    Text("Go Back")
                }
            }
        }
    }
}

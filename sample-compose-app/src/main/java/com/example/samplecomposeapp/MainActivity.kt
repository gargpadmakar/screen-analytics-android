package com.example.samplecomposeapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.screenanalytics.storage.ScreenStatisticsData
import kotlinx.coroutines.launch

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
                Text("Home Screen (Zero Analytics Code!)", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { navController.navigate("profile") }) {
                    Text("Go to Profile")
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { navController.navigate("dashboard") }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)) {
                    Text("View Analytics Dashboard")
                }
            }
        }
        composable("profile") {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Profile Screen (Zero Analytics Code!)", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { navController.popBackStack() }) {
                    Text("Go Back")
                }
            }
        }
        composable("dashboard") {
            DashboardScreen()
        }
    }
}

@Composable
fun DashboardScreen() {
    val coroutineScope = rememberCoroutineScope()
    var stats by remember { mutableStateOf<List<ScreenStatisticsData>>(emptyList()) }
    var totalEvents by remember { mutableStateOf(0L) }

    LaunchedEffect(Unit) {
        // Query the local Room database to prove the SDK is tracking
        totalEvents = ScreenAnalyticsCore.repository.getTotalEventCount()
        stats = ScreenAnalyticsCore.repository.getScreenStatistics()
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Local Developer Dashboard", style = MaterialTheme.typography.headlineMedium)
        Text("Total Screen View Events Recorded: \", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(16.dp))
        
        LazyColumn {
            items(stats) { stat ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Screen: \", style = MaterialTheme.typography.titleMedium)
                        Text(text = "Views: \")
                        Text(text = "Avg Time: \ seconds")
                    }
                }
            }
        }
    }
}

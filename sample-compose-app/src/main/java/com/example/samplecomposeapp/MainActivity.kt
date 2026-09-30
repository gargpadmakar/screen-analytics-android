package com.example.samplecomposeapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.screenanalytics.compose.ScreenAnalyticsCompose
import com.example.screenanalytics.core.AnalyticsExportManager
import com.example.screenanalytics.core.ScreenAnalyticsCore
import com.example.screenanalytics.storage.AnalyticsDatabase
import com.example.screenanalytics.storage.AnalyticsRepositoryImpl
import com.example.screenanalytics.storage.ScreenStatisticsData
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize SDK manually for sample purposes
        val db = AnalyticsDatabase.getDatabase(applicationContext)
        val repository = AnalyticsRepositoryImpl(db)
        ScreenAnalyticsCore.init(repository)

        setContent {
            MaterialTheme(colorScheme = lightColorScheme(
                primary = Color(0xFF6200EE),
                secondary = Color(0xFF03DAC5),
                background = Color(0xFFF5F5F5),
                surface = Color.White,
                onPrimary = Color.White,
                onSurface = Color.Black
            )) {
                Surface(
                    modifier = Modifier.fillMaxSize().systemBarsPadding(), 
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    
    // Attach SDK for automatic screen tracking!
    ScreenAnalyticsCompose.attach(navController)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Analytics Premium Demo", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) { innerPadding ->
        NavHost(
            navController = navController, 
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") { HomeScreen(navController) }
            composable("explore") { ExploreScreen(navController) }
            composable("profile") { ProfileScreen(navController) }
            composable("settings") { SettingsScreen() }
            composable("product_detail/{productId}") { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId") ?: "0"
                ProductDetailScreen(productId, navController)
            }
            composable("dashboard") { DashboardScreen() }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        BottomNavItem("Home", "home", Icons.Default.Home),
        BottomNavItem("Explore", "explore", Icons.Default.Search),
        BottomNavItem("Profile", "profile", Icons.Default.Person),
        BottomNavItem("Stats", "dashboard", Icons.Default.Info)
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(imageVector = item.icon, contentDescription = item.title) },
                label = { Text(text = item.title) },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo("home") { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray,
                    indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                )
            )
        }
    }
}

data class BottomNavItem(val title: String, val route: String, val icon: ImageVector)

@Composable
fun HomeScreen(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PremiumCard("Welcome to Zero-Code Analytics", "Navigate around using the bottom bar. Every click and screen view is being tracked silently in the background!")
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { navController.navigate("product_detail/123") },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("View Featured Product (Test Dynamic Route)")
        }
    }
}

@Composable
fun ExploreScreen(navController: NavController) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        items(5) { index ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Item ${index + 1}", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                    Text("Click to see product details", color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { navController.navigate("product_detail/${index + 100}") }) {
                        Text("View Item")
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileScreen(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(100.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Padmakar Garg", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onSurface)
        Text("padmakar@example.com", color = Color.Gray)
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = { navController.navigate("settings") },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray, contentColor = Color.Black)
        ) {
            Icon(Icons.Default.Settings, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("App Settings")
        }
    }
}

@Composable
fun SettingsScreen() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        PremiumCard("Settings", "This is a deeply nested screen. Let's see if the SDK records the time spent here.")
    }
}

@Composable
fun ProductDetailScreen(productId: String, navController: NavController) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        PremiumCard("Product Details", "You are viewing product #$productId. Notice how the SDK sanitizes this dynamic route in the dashboard!")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { navController.popBackStack() }) {
            Text("Go Back")
        }
    }
}

@Composable
fun PremiumCard(title: String, description: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(title, style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(description, style = MaterialTheme.typography.bodyLarge, color = Color.DarkGray)
        }
    }
}

@Composable
fun DashboardScreen() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var stats by remember { mutableStateOf<List<ScreenStatisticsData>>(emptyList()) }
    var totalEvents by remember { mutableStateOf(0L) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        // Auto-refresh loop to show real-time changes while on this screen
        while (true) {
            val db = AnalyticsDatabase.getDatabase(context.applicationContext)
            totalEvents = db.analyticsDao().getTotalEventCount()
            stats = db.analyticsDao().getScreenStatistics()
            isLoading = false
            delay(2000) // Refresh every 2 seconds
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Storage & Offline Testing", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
        Text("Data is fetched securely from local SQLite", color = Color.Gray, style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(16.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Total Database Records", color = Color.White)
                Text("$totalEvents", style = MaterialTheme.typography.displayMedium, color = Color.White, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            OutlinedButton(onClick = {
                coroutineScope.launch {
                    val db = AnalyticsDatabase.getDatabase(context.applicationContext)
                    db.analyticsDao().clearAllEvents()
                    totalEvents = 0
                    stats = emptyList()
                }
            }) {
                Text("Clear DB")
            }
            
            Button(onClick = {
                coroutineScope.launch {
                    try {
                        val db = AnalyticsDatabase.getDatabase(context.applicationContext)
                        // Collect the first emission of the Flow
                        val rawEvents = kotlinx.coroutines.flow.first(db.analyticsDao().getAllEvents())
                        
                        // Convert Storage Entities to Core Models for the ExportManager
                        val coreEvents = rawEvents.map { entity ->
                            com.example.screenanalytics.core.ScreenEvent(
                                eventId = entity.eventId,
                                screenName = entity.screenName,
                                timestamp = entity.timestamp,
                                sessionId = entity.sessionId,
                                durationMillis = entity.durationMillis
                            )
                        }
                        
                        // Generate CSV
                        val csvData = AnalyticsExportManager.exportCsv(coreEvents)
                        
                        // Print to Logcat for developer to see
                        println("==== EXPORTED CSV DATA ====\n$csvData\n===========================")
                        
                        // Show success message
                        kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                            android.widget.Toast.makeText(context, "CSV Exported to Logcat! (${coreEvents.size} records)", android.widget.Toast.LENGTH_LONG).show()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }) {
                Text("Export as CSV")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        Text("Screen Analytics (Real-Time)", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
        Spacer(modifier = Modifier.height(8.dp))

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            LazyColumn {
                items(stats) { stat ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = stat.screenName, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = "Average time: ${stat.averageDurationMillis / 1000}s", color = Color.Gray, style = MaterialTheme.typography.bodyMedium)
                            }
                            Box(
                                modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)).padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text("${stat.viewCount} views", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

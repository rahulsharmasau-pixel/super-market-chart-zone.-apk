package com.marketchartzone.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.marketchartzone.app.ui.admin.AdminPanelScreen
import com.marketchartzone.app.ui.admin.AdminViewModel
import com.marketchartzone.app.ui.auth.AuthScreen
import com.marketchartzone.app.ui.auth.AuthStatus
import com.marketchartzone.app.ui.auth.AuthViewModel
import com.marketchartzone.app.ui.community.CommunityScreen
import com.marketchartzone.app.ui.community.CommunityViewModel
import com.marketchartzone.app.ui.home.HomeScreen
import com.marketchartzone.app.ui.home.HomeViewModel
import com.marketchartzone.app.ui.market.MarketScreen
import com.marketchartzone.app.ui.market.MarketViewModel
import com.marketchartzone.app.ui.profile.ProfileScreen
import com.marketchartzone.app.ui.profile.ProfileViewModel
import com.marketchartzone.app.ui.theme.MarketChartZoneTheme
import com.marketchartzone.app.ui.theme.TealAccent
import com.marketchartzone.app.ui.tools.ToolsScreen
import com.marketchartzone.app.ui.tools.ToolsViewModel
import com.marketchartzone.app.ui.trades.TradesScreen
import com.marketchartzone.app.ui.trades.TradesViewModel

class MainActivity : ComponentActivity() {
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        // Handled notification permission result
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != android.content.pm.PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        enableEdgeToEdge()
        setContent {
            MarketChartZoneTheme {
                val authViewModel: AuthViewModel = viewModel()
                val authState by authViewModel.authState.collectAsState()

                when (val state = authState) {
                    is AuthStatus.Loading -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                            CircularProgressIndicator(color = TealAccent)
                        }
                    }
                    is AuthStatus.Unauthenticated -> {
                        AuthScreen(viewModel = authViewModel)
                    }
                    is AuthStatus.Authenticated -> {
                        MainAppContent(user = state.user, onSignOut = { authViewModel.signOut() })
                    }
                }
            }
        }
    }
}

@Composable
fun MainAppContent(user: com.marketchartzone.app.data.model.UserProfile, onSignOut: () -> Unit) {
    var currentTab by remember { mutableStateOf(0) } // 0: Home, 1: Trades, 2: Market, 3: Community, 4: Tools, 5: Profile, 6: Admin
    val homeViewModel: HomeViewModel = viewModel()
    val tradesViewModel: TradesViewModel = viewModel()
    val marketViewModel: MarketViewModel = viewModel()
    val communityViewModel: CommunityViewModel = viewModel()
    val toolsViewModel: ToolsViewModel = viewModel()
    val profileViewModel: ProfileViewModel = viewModel()
    val adminViewModel: AdminViewModel = viewModel()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (currentTab < 5) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    tonalElevation = 8.dp
                ) {
                    val items = listOf("Home", "Trades", "Market", "Community", "Tools")
                    val icons = listOf(Icons.Default.Home, Icons.Default.TrendingUp, Icons.Default.ShowChart, Icons.Default.Forum, Icons.Default.Calculate)

                    items.forEachIndexed { index, title ->
                        NavigationBarItem(
                            selected = currentTab == index,
                            onClick = { currentTab = index },
                            icon = {
                                Icon(
                                    imageVector = icons[index],
                                    contentDescription = title
                                )
                            },
                            label = { Text(title) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = TealAccent,
                                selectedTextColor = TealAccent,
                                indicatorColor = MaterialTheme.colorScheme.background,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (currentTab) {
                0 -> HomeScreen(
                    user = user,
                    viewModel = homeViewModel,
                    onNavigateToTrades = { currentTab = 1 },
                    onNavigateToMarket = { currentTab = 2 },
                    onNavigateToTools = { currentTab = 4 },
                    onNavigateToProfile = { currentTab = 5 }
                )
                1 -> TradesScreen(viewModel = tradesViewModel)
                2 -> MarketScreen(viewModel = marketViewModel)
                3 -> CommunityScreen(user = user, viewModel = communityViewModel)
                4 -> ToolsScreen(viewModel = toolsViewModel)
                5 -> ProfileScreen(
                    user = user,
                    viewModel = profileViewModel,
                    onSignOut = onSignOut,
                    onNavigateToAdmin = { currentTab = 6 }
                )
                6 -> AdminPanelScreen(
                    viewModel = adminViewModel,
                    onBack = { currentTab = 5 }
                )
            }
        }
    }
}

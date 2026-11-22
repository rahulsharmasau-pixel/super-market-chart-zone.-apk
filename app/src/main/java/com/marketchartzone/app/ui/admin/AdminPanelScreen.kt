package com.marketchartzone.app.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.marketchartzone.app.data.model.*
import com.marketchartzone.app.ui.theme.*

@Composable
fun AdminPanelScreen(
    viewModel: AdminViewModel,
    onBack: () -> Unit
) {
    if (viewModel.adminCheckLoading) {
        Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = GoldAccent)
        }
        return
    }

    if (!viewModel.isAdminAuthenticated) {
        AdminAuthScreen(viewModel = viewModel, onBack = onBack)
        return
    }

    val users by viewModel.users.collectAsState()
    val vipTrades by viewModel.vipTrades.collectAsState()
    val marketAnalysis by viewModel.marketAnalysis.collectAsState()
    val reports by viewModel.reports.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TealAccent)
                }
                Text(
                    text = "Admin Panel (Creator)",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontSize = 20.sp),
                    color = GoldAccent
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Admin Tabs
        ScrollableTabRow(
            selectedTabIndex = viewModel.selectedAdminTab,
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = TealAccent,
            edgePadding = 0.dp
        ) {
            Tab(selected = viewModel.selectedAdminTab == 0, onClick = { viewModel.selectedAdminTab = 0 }, text = { Text("Dashboard") })
            Tab(selected = viewModel.selectedAdminTab == 1, onClick = { viewModel.selectedAdminTab = 1 }, text = { Text("VIP Trades") })
            Tab(selected = viewModel.selectedAdminTab == 2, onClick = { viewModel.selectedAdminTab = 2 }, text = { Text("Market Analysis") })
            Tab(selected = viewModel.selectedAdminTab == 3, onClick = { viewModel.selectedAdminTab = 3 }, text = { Text("Users (${users.size})") })
            Tab(selected = viewModel.selectedAdminTab == 4, onClick = { viewModel.selectedAdminTab = 4 }, text = { Text("Reports (${reports.size})") })
            Tab(selected = viewModel.selectedAdminTab == 5, onClick = { viewModel.selectedAdminTab = 5 }, text = { Text("Push Broadcast") })
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (viewModel.selectedAdminTab) {
            0 -> AdminDashboardTab(users.size, vipTrades.size, marketAnalysis.size, reports.size)
            1 -> AdminVipTradesTab(viewModel, vipTrades)
            2 -> AdminMarketAnalysisTab(viewModel, marketAnalysis)
            3 -> AdminUsersTab(viewModel, users)
            4 -> AdminReportsTab(viewModel, reports)
            5 -> AdminPushTab(viewModel)
        }
    }
}

@Composable
fun AdminAuthScreen(viewModel: AdminViewModel, onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().widthIn(max = 400.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TealAccent)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        text = if (viewModel.isSetupMode) "Admin One-Time Setup" else "Admin Login",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold, fontSize = 20.sp),
                        color = GoldAccent
                    )
                    if (viewModel.isSetupMode) {
                        Text(
                            text = "As the app creator, set your unique Admin User ID and Password. This setup can only be done once.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    OutlinedTextField(
                        value = viewModel.inputAdminId,
                        onValueChange = { viewModel.inputAdminId = it },
                        label = { Text("Admin User ID / Email") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = viewModel.inputPassword,
                        onValueChange = { viewModel.inputPassword = it },
                        label = { Text("Admin Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    if (viewModel.errorMessage != null) {
                        Text(viewModel.errorMessage!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    }

                    Button(
                        onClick = { viewModel.performAdminAuth() },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GoldAccent)
                    ) {
                        Text(text = if (viewModel.isSetupMode) "Complete Setup" else "Login to Admin Panel", color = NavyDark, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun AdminDashboardTab(userCount: Int, tradeCount: Int, analysisCount: Int, reportCount: Int) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Centralized Management Overview", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = TealAccent)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(modifier = Modifier.weight(1f)) { StatCard("Total Users", "$userCount") }
            Box(modifier = Modifier.weight(1f)) { StatCard("VIP Trades", "$tradeCount") }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(modifier = Modifier.weight(1f)) { StatCard("Market Analysis", "$analysisCount") }
            Box(modifier = Modifier.weight(1f)) { StatCard("Active Reports", "$reportCount") }
        }
    }
}

@Composable
fun StatCard(title: String, value: String) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.Start) {
            Text(title, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold), color = TealAccent)
        }
    }
}

@Composable
fun AdminVipTradesTab(viewModel: AdminViewModel, trades: List<VipTrade>) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Publish VIP Signal", fontWeight = FontWeight.Bold, color = TealAccent)
                    OutlinedTextField(
                        value = viewModel.newTradeTitle,
                        onValueChange = { viewModel.newTradeTitle = it },
                        label = { Text("Signal Title (e.g., EUR/USD BUY)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = viewModel.newTradeDescription,
                        onValueChange = { viewModel.newTradeDescription = it },
                        label = { Text("Signal Description / Entry / TP / SL") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                    Button(
                        onClick = { viewModel.createVipTrade() },
                        colors = ButtonDefaults.buttonColors(containerColor = TealAccent),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Publish VIP Signal", color = NavyDark, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        items(trades) { trade ->
            Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(trade.title, fontWeight = FontWeight.Bold, color = TealAccent)
                        Text(trade.description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onBackground, maxLines = 2)
                    }
                    IconButton(onClick = { viewModel.deleteVipTrade(trade.id) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}

@Composable
fun AdminMarketAnalysisTab(viewModel: AdminViewModel, analysisList: List<MarketAnalysis>) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Publish Market Analysis", fontWeight = FontWeight.Bold, color = TealAccent)
                    OutlinedTextField(value = viewModel.newAnalysisTitle, onValueChange = { viewModel.newAnalysisTitle = it }, label = { Text("Analysis Title") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = viewModel.newAnalysisExplanation, onValueChange = { viewModel.newAnalysisExplanation = it }, label = { Text("Explanation") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
                    OutlinedTextField(value = viewModel.newSupport, onValueChange = { viewModel.newSupport = it }, label = { Text("Support Levels") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = viewModel.newResistance, onValueChange = { viewModel.newResistance = it }, label = { Text("Resistance Levels") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = viewModel.newKeyLevels, onValueChange = { viewModel.newKeyLevels = it }, label = { Text("Key Levels") }, modifier = Modifier.fillMaxWidth())
                    Button(onClick = { viewModel.createMarketAnalysis() }, colors = ButtonDefaults.buttonColors(containerColor = TealAccent), modifier = Modifier.fillMaxWidth()) {
                        Text("Publish Analysis", color = NavyDark, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        items(analysisList) { analysis ->
            Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(analysis.title, fontWeight = FontWeight.Bold, color = TealAccent)
                        Text(analysis.marketExplanation, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onBackground, maxLines = 2)
                    }
                    IconButton(onClick = { viewModel.deleteMarketAnalysis(analysis.id) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}

@Composable
fun AdminUsersTab(viewModel: AdminViewModel, users: List<UserProfile>) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Text("Registered Users Management (Delete Accounts)", fontWeight = FontWeight.Bold, color = TealAccent)
        }
        items(users) { user ->
            Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(user.displayName.ifEmpty { "Trader" }, fontWeight = FontWeight.Bold, color = TealAccent)
                        Text(user.email, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        if (user.isAdmin) Text("ADMIN", color = GoldAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    IconButton(onClick = { viewModel.deleteUser(user.uid) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete User Account", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}

@Composable
fun AdminReportsTab(viewModel: AdminViewModel, reports: List<Report>) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        if (reports.isEmpty()) {
            item { Text("No reports submitted.", color = MaterialTheme.colorScheme.onSurfaceVariant) }
        }
        items(reports) { report ->
            Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Target: ${report.targetType} (${report.status})", fontWeight = FontWeight.Bold, color = GoldAccent)
                    Text("Reason: ${report.reason}", color = MaterialTheme.colorScheme.onBackground)
                    if (report.status != "Resolved") {
                        Button(
                            onClick = { viewModel.resolveReport(report.id) },
                            colors = ButtonDefaults.buttonColors(containerColor = MintAccent),
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("Mark Resolved", color = NavyDark, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminPushTab(viewModel: AdminViewModel) {
    Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("Send Broadcast Push Notification", fontWeight = FontWeight.Bold, color = TealAccent)
            OutlinedTextField(value = viewModel.pushTitle, onValueChange = { viewModel.pushTitle = it }, label = { Text("Notification Title") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = viewModel.pushMessage, onValueChange = { viewModel.pushMessage = it }, label = { Text("Notification Message") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
            Button(
                onClick = { viewModel.sendPushBroadcast() },
                colors = ButtonDefaults.buttonColors(containerColor = TealAccent),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = NavyDark)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Broadcast to All Users", color = NavyDark, fontWeight = FontWeight.Bold)
            }
            if (viewModel.pushStatus != null) {
                Text(viewModel.pushStatus!!, color = MintAccent, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

package com.marketchartzone.app.ui.tools

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.marketchartzone.app.ui.theme.*

@Composable
fun ToolsScreen(viewModel: ToolsViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Trading Calculators",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold, fontSize = 22.sp),
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(16.dp))

        TabRow(
            selectedTabIndex = viewModel.selectedTab,
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = TealAccent,
            indicator = {},
            divider = {}
        ) {
            Tab(
                selected = viewModel.selectedTab == 0,
                onClick = { viewModel.selectedTab = 0 },
                text = { Text("Profit Calculator", fontWeight = FontWeight.Bold) },
                selectedContentColor = NavyDark,
                unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .background(if (viewModel.selectedTab == 0) TealAccent else MaterialTheme.colorScheme.surfaceVariant)
            )
            Tab(
                selected = viewModel.selectedTab == 1,
                onClick = { viewModel.selectedTab == 1 },
                text = { Text("Position Size", fontWeight = FontWeight.Bold) },
                selectedContentColor = NavyDark,
                unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .background(if (viewModel.selectedTab == 1) TealAccent else MaterialTheme.colorScheme.surfaceVariant)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (viewModel.selectedTab == 0) {
            ProfitCalculatorContent(viewModel = viewModel)
        } else {
            PositionSizeCalculatorContent(viewModel = viewModel)
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun ProfitCalculatorContent(viewModel: ToolsViewModel) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("Calculate Potential Profit / Loss", style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold), color = TealAccent)

            OutlinedTextField(
                value = viewModel.profitPair,
                onValueChange = { viewModel.profitPair = it },
                label = { Text("Currency Pair") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            )

            OutlinedTextField(
                value = viewModel.volumeLots,
                onValueChange = { viewModel.volumeLots = it },
                label = { Text("Volume (Lots)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier.weight(1f)) {
                    Button(
                        onClick = { viewModel.isLong = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = if (viewModel.isLong) MintAccent else NavyCardElevated)
                    ) {
                        Text("BUY (Long)", color = if (viewModel.isLong) NavyDark else TextSecondaryDark, fontWeight = FontWeight.Bold)
                    }
                }
                Box(modifier = Modifier.weight(1f)) {
                    Button(
                        onClick = { viewModel.isLong = false },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = if (!viewModel.isLong) GoldAccent else NavyCardElevated)
                    ) {
                        Text("SELL (Short)", color = if (!viewModel.isLong) NavyDark else TextSecondaryDark, fontWeight = FontWeight.Bold)
                    }
                }
            }

            OutlinedTextField(
                value = viewModel.openPrice,
                onValueChange = { viewModel.openPrice = it },
                label = { Text("Open Price") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            )

            OutlinedTextField(
                value = viewModel.closePrice,
                onValueChange = { viewModel.closePrice = it },
                label = { Text("Close Price") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    Button(
                        onClick = { viewModel.calculateProfit() },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = TealAccent)
                    ) {
                        Text("Calculate", color = NavyDark, fontWeight = FontWeight.Bold)
                    }
                }
                Box(modifier = Modifier.weight(1f)) {
                    OutlinedButton(
                        onClick = { viewModel.resetProfit() },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Reset")
                    }
                }
            }

            if (viewModel.profitResult != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = NavyCardElevated,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = viewModel.profitResult!!,
                        style = MaterialTheme.typography.titleLarge.copy(fontSize = 16.sp, fontWeight = FontWeight.Bold),
                        color = MintAccent,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun PositionSizeCalculatorContent(viewModel: ToolsViewModel) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("Calculate Position Size & Risk", style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold), color = TealAccent)

            OutlinedTextField(
                value = viewModel.accountBalance,
                onValueChange = { viewModel.accountBalance = it },
                label = { Text("Account Balance ($)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            )

            OutlinedTextField(
                value = viewModel.riskPercentage,
                onValueChange = { viewModel.riskPercentage = it },
                label = { Text("Risk Percentage (%)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            )

            OutlinedTextField(
                value = viewModel.stopLossPips,
                onValueChange = { viewModel.stopLossPips = it },
                label = { Text("Stop Loss (Pips)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    Button(
                        onClick = { viewModel.calculatePositionSize() },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = TealAccent)
                    ) {
                        Text("Calculate", color = NavyDark, fontWeight = FontWeight.Bold)
                    }
                }
                Box(modifier = Modifier.weight(1f)) {
                    OutlinedButton(
                        onClick = { viewModel.resetPositionSize() },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Reset")
                    }
                }
            }

            if (viewModel.positionResultRisk != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = NavyCardElevated,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = viewModel.positionResultRisk!!,
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                            color = GoldAccent
                        )
                        if (viewModel.positionResultLots != null) {
                            Text(
                                text = viewModel.positionResultLots!!,
                                style = MaterialTheme.typography.titleLarge.copy(fontSize = 16.sp, fontWeight = FontWeight.Bold),
                                color = MintAccent
                            )
                        }
                    }
                }
            }
        }
    }
}

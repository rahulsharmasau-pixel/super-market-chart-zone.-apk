package com.marketchartzone.app.ui.tools

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlin.math.abs

class ToolsViewModel : ViewModel() {
    var selectedTab by mutableStateOf(0) // 0: Profit Calculator, 1: Position Size

    // Profit Calculator fields
    var profitPair by mutableStateOf("EUR/USD")
    var volumeLots by mutableStateOf("1.0")
    var isLong by mutableStateOf(true)
    var openPrice by mutableStateOf("1.0850")
    var closePrice by mutableStateOf("1.0920")
    var profitResult by mutableStateOf<String?>(null)

    // Position Size Calculator fields
    var accountBalance by mutableStateOf("10000")
    var riskPercentage by mutableStateOf("1.0")
    var stopLossPips by mutableStateOf("30")
    var positionPair by mutableStateOf("EUR/USD")
    var positionResultRisk by mutableStateOf<String?>(null)
    var positionResultLots by mutableStateOf<String?>(null)

    fun calculateProfit() {
        try {
            val vol = volumeLots.toDoubleOrNull() ?: 0.0
            val open = openPrice.toDoubleOrNull() ?: 0.0
            val close = closePrice.toDoubleOrNull() ?: 0.0
            if (vol <= 0 || open <= 0 || close <= 0) {
                profitResult = "Please enter valid numbers."
                return
            }
            val diff = if (isLong) close - open else open - close
            // Standard Forex lot = 100,000 units. Pip value approx $10 per lot for EUR/USD
            val profitUSD = diff * 100000 * vol
            val formatted = String.format(java.util.Locale.US, "$%.2f", profitUSD)
            profitResult = if (profitUSD >= 0) "Estimated Profit: +$formatted" else "Estimated Loss: -$formatted"
        } catch (e: Exception) {
            profitResult = "Calculation error."
        }
    }

    fun resetProfit() {
        volumeLots = "1.0"
        openPrice = "1.0850"
        closePrice = "1.0920"
        profitResult = null
    }

    fun calculatePositionSize() {
        try {
            val balance = accountBalance.toDoubleOrNull() ?: 0.0
            val riskPct = riskPercentage.toDoubleOrNull() ?: 0.0
            val pips = stopLossPips.toDoubleOrNull() ?: 0.0
            if (balance <= 0 || riskPct <= 0 || pips <= 0) {
                positionResultRisk = "Please enter valid numbers."
                positionResultLots = null
                return
            }
            val riskAmount = balance * (riskPct / 100.0)
            // Risk per pip for standard lot (100,000) is roughly $10/pip. For mini lot $1/pip.
            // Lot size = Risk Amount / (Pips * PipValuePerLot) where PipValuePerLot = $10
            val lotSize = riskAmount / (pips * 10.0)
            positionResultRisk = String.format(java.util.Locale.US, "Risk Amount: $%.2f", riskAmount)
            positionResultLots = String.format(java.util.Locale.US, "Recommended Lot Size: %.2f lots", lotSize)
        } catch (e: Exception) {
            positionResultRisk = "Calculation error."
            positionResultLots = null
        }
    }

    fun resetPositionSize() {
        accountBalance = "10000"
        riskPercentage = "1.0"
        stopLossPips = "30"
        positionResultRisk = null
        positionResultLots = null
    }
}

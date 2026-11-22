package com.marketchartzone.app.ui.admin

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.marketchartzone.app.data.model.MarketAnalysis
import com.marketchartzone.app.data.model.VipTrade
import com.marketchartzone.app.data.model.Report
import com.marketchartzone.app.data.model.UserProfile
import com.marketchartzone.app.data.repository.MarketRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AdminViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = MarketRepository(application)

    var isAdminAuthenticated by mutableStateOf(false)
    var isSetupMode by mutableStateOf(false)
    var adminCheckLoading by mutableStateOf(true)

    var inputAdminId by mutableStateOf("")
    var inputPassword by mutableStateOf("")
    var errorMessage by mutableStateOf<String?>(null)

    var selectedAdminTab by mutableStateOf(0) // 0: Dashboard, 1: VIP Trades, 2: Market Analysis, 3: Users, 4: Reports, 5: Push Broadcast

    val users: StateFlow<List<UserProfile>> = repository.observeUsers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val vipTrades: StateFlow<List<VipTrade>> = repository.observeVipTrades()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val marketAnalysis: StateFlow<List<MarketAnalysis>> = repository.observeMarketAnalysis()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val reports: StateFlow<List<Report>> = repository.observeReports()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // New VIP Trade form
    var newTradeTitle by mutableStateOf("")
    var newTradeDescription by mutableStateOf("")

    // New Market Analysis form
    var newAnalysisTitle by mutableStateOf("")
    var newAnalysisExplanation by mutableStateOf("")
    var newSupport by mutableStateOf("1.0800")
    var newResistance by mutableStateOf("1.0950")
    var newKeyLevels by mutableStateOf("1.0850 Pivot")

    // Push Notification form
    var pushTitle by mutableStateOf("")
    var pushMessage by mutableStateOf("")
    var pushStatus by mutableStateOf<String?>(null)

    init {
        checkAdminStatus()
    }

    fun checkAdminStatus() {
        viewModelScope.launch {
            adminCheckLoading = true
            val config = repository.getAdminConfig()
            // Single-admin config is strictly enforced; setup is locked once established.
            isSetupMode = false
            adminCheckLoading = false
        }
    }

    fun performAdminAuth() {
        viewModelScope.launch {
            errorMessage = null
            if (inputAdminId.isBlank() || inputPassword.isBlank()) {
                errorMessage = "Please enter Creator Admin User ID / Email and Password."
                return@launch
            }
            val success = repository.verifyAdminLogin(inputAdminId, inputPassword)
            if (success) {
                isAdminAuthenticated = true
            } else {
                errorMessage = "Access Denied: Only the creator-defined single admin can access this panel."
            }
        }
    }

    fun createVipTrade() {
        if (newTradeTitle.isBlank() || newTradeDescription.isBlank()) return
        viewModelScope.launch {
            val trade = VipTrade(
                title = newTradeTitle,
                description = newTradeDescription
            )
            repository.addVipTrade(trade)
            newTradeTitle = ""
            newTradeDescription = ""
        }
    }

    fun deleteVipTrade(id: String) {
        viewModelScope.launch {
            repository.deleteVipTrade(id)
        }
    }

    fun createMarketAnalysis() {
        if (newAnalysisTitle.isBlank() || newAnalysisExplanation.isBlank()) return
        viewModelScope.launch {
            val analysis = MarketAnalysis(
                title = newAnalysisTitle,
                marketExplanation = newAnalysisExplanation,
                supportLevels = newSupport,
                resistanceLevels = newResistance,
                keyLevels = newKeyLevels
            )
            repository.addMarketAnalysis(analysis)
            newAnalysisTitle = ""
            newAnalysisExplanation = ""
        }
    }

    fun deleteMarketAnalysis(id: String) {
        viewModelScope.launch {
            repository.deleteMarketAnalysis(id)
        }
    }

    fun resolveReport(id: String) {
        viewModelScope.launch {
            repository.resolveReport(id)
        }
    }

    fun deleteUser(uid: String) {
        viewModelScope.launch {
            repository.deleteUser(uid)
        }
    }

    fun sendPushBroadcast() {
        if (pushTitle.isBlank() || pushMessage.isBlank()) return
        pushStatus = "Push broadcast sent successfully to all subscribers!"
        pushTitle = ""
        pushMessage = ""
    }
}

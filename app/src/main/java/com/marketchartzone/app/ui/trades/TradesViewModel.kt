package com.marketchartzone.app.ui.trades

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.marketchartzone.app.data.model.VipTrade
import com.marketchartzone.app.data.repository.MarketRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class TradesViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = MarketRepository(application)

    val vipTrades: StateFlow<List<VipTrade>> = repository.observeVipTrades()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    var searchQuery by mutableStateOf("")
}

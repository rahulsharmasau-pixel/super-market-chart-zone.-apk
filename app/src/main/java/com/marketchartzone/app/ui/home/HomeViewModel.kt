package com.marketchartzone.app.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.marketchartzone.app.data.model.MarketAnalysis
import com.marketchartzone.app.data.model.VipTrade
import com.marketchartzone.app.data.repository.MarketRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = MarketRepository(application)

    val vipTrades: StateFlow<List<VipTrade>> = repository.observeVipTrades()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val marketAnalysis: StateFlow<List<MarketAnalysis>> = repository.observeMarketAnalysis()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}

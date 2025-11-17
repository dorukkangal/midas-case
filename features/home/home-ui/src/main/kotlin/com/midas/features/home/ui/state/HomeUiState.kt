package com.midas.features.home.ui.state

import com.midas.core.ui.model.ErrorUiState
import com.midas.features.home.ui.model.CoinUiModel

data class HomeUiState(
    val coins: List<CoinUiModel> = emptyList(),
    val trendingCoins: List<CoinUiModel> = emptyList(),
    val searchResults: List<CoinUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isTrendingLoading: Boolean = false,
    val isSearching: Boolean = false,
    val loadCoinsError: ErrorUiState? = null,
    val loadTrendingCoinsError: ErrorUiState? = null,
)

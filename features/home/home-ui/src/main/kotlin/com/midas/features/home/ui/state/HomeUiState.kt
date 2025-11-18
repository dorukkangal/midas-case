package com.midas.features.home.ui.state

import com.midas.features.home.ui.model.CoinUiModel

data class HomeUiState(
    val coins: List<CoinUiModel> = emptyList(),
    val trendingCoins: List<CoinUiModel> = emptyList(),
    val searchResults: List<CoinUiModel> = emptyList(),
    val page: Int = 1,
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isSearching: Boolean = false,
    val loadError: Throwable? = null,
)

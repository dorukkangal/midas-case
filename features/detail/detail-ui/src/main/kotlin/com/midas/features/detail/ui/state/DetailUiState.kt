package com.midas.features.detail.ui.state

import com.midas.core.ui.model.ErrorUiState
import com.midas.features.detail.ui.model.CoinDetailUiModel

data class DetailUiState(
    val coinDetail: CoinDetailUiModel? = null,
    val isFavorite: Boolean = false,
    val isLoading: Boolean = false,
    val isFavoriteLoading: Boolean = false,
    val loadDetailError: ErrorUiState? = null,
    val loadFavoriteError: ErrorUiState? = null,
)

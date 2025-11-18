package com.midas.features.detail.ui.state

import com.midas.features.detail.ui.model.CoinDetailUiModel

data class DetailUiState(
    val coinDetail: CoinDetailUiModel? = null,
    val isFavorite: Boolean = false,
    val isDetailLoading: Boolean = false,
    val isFavoriteLoading: Boolean = false,
    val loadDetailError: Throwable? = null,
    val loadFavoriteError: Throwable? = null,
)

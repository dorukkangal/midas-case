package com.midas.features.favorites.ui.state

import com.midas.core.ui.model.ErrorUiState
import com.midas.features.favorites.ui.model.CoinUiModel
import com.midas.features.favorites.ui.model.SortOrderUiModel

data class FavoritesUiState(
    val favorites: List<CoinUiModel> = emptyList(),
    val sortOrder: SortOrderUiModel? = null,
    val isLoading: Boolean = false,
    val isFavoriteLoading: Boolean = false,
    val error: ErrorUiState? = null,
    val loadFavoriteError: ErrorUiState? = null,
)

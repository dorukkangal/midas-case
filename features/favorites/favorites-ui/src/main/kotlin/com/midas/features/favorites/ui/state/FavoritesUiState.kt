package com.midas.features.favorites.ui.state

import com.midas.features.favorites.ui.model.CoinUiModel
import com.midas.features.favorites.ui.model.SortOrderUiModel

data class FavoritesUiState(
    val favorites: List<CoinUiModel> = emptyList(),
    val sortOrder: SortOrderUiModel? = null,
    val isFavoritesLoading: Boolean = false,
    val isUpdateFavoriteLoading: Boolean = false,
    val loadFavoritesError: Throwable? = null,
    val updateFavoriteError: Throwable? = null,
)

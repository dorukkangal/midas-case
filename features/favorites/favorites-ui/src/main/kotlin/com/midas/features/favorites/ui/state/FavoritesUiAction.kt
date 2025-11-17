package com.midas.features.favorites.ui.state

import com.midas.features.favorites.ui.model.CoinUiModel
import com.midas.features.favorites.ui.model.SortOrderUiModel

/**
 * User actions/intents for Favorites screen
 * Represents all possible user interactions with the favorites screen
 */
sealed interface FavoritesUiAction {
    data object LoadFavorites : FavoritesUiAction
    data class RemoveFavorite(val coin: CoinUiModel) : FavoritesUiAction
    data class ChangeSortOrder(val sortOrder: SortOrderUiModel) : FavoritesUiAction
    data object ClearAllFavorites : FavoritesUiAction
    data object DismissError : FavoritesUiAction
}

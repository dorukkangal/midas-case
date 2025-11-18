package com.midas.features.home.ui.state

/**
 * User actions/intents for Home screen
 * Represents all possible user interactions with the Home screen
 */
sealed class HomeUiAction {
    data class SearchCoins(val query: String) : HomeUiAction()
    data object RefreshData : HomeUiAction()
    data object RetryLoading : HomeUiAction()
    data object ClearSearch : HomeUiAction()
    data object DismissError : HomeUiAction()
}

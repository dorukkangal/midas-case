package com.midas.features.home.ui

import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.midas.core.ui.navigation.Routes
import com.midas.features.home.ui.state.HomeUiAction

/**
 * Navigation setup for Home screen
 * Handles ViewModel creation, state collection, event handling, and navigation
 */
fun NavGraphBuilder.home(navController: NavController) {
    composable<Routes.Home> {
        val viewModel = hiltViewModel<HomeViewModel>()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

        HomeScreen(
            uiState = uiState,
            searchQuery = searchQuery,
            onCoinClick = {
                // TODO: Navigate to details screen
            },
            onFavoritesClick = {
                // TODO: Navigate to favorites screen
            },
            onSearchQueryChange = { viewModel.handleAction(HomeUiAction.SearchCoins(it)) },
            onClearSearch = { viewModel.handleAction(HomeUiAction.ClearSearch) },
            onRefresh = { viewModel.handleAction(HomeUiAction.RefreshData) },
            onRetryClick = { viewModel.handleAction(HomeUiAction.RetryLoading) },
            onErrorDismiss = { viewModel.handleAction(HomeUiAction.DismissError) }
        )
    }
}

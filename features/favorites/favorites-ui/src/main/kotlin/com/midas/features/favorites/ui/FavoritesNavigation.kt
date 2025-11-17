package com.midas.features.favorites.ui

import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.midas.core.ui.navigation.Routes
import com.midas.features.favorites.ui.state.FavoritesUiAction

fun NavGraphBuilder.favorites(navController: NavController) {
    composable<Routes.Favorites> {
        val viewModel = hiltViewModel<FavoritesViewModel>()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        FavoritesScreen(
            uiState = uiState,
            onNavigateUp = { navController.popBackStack() },
            onChangeSortOrder = { viewModel.handleAction(FavoritesUiAction.ChangeSortOrder(it)) },
            onRefresh = { viewModel.handleAction(FavoritesUiAction.LoadFavorites) },
            onCoinClick = { navController.navigate(Routes.Detail(it.id)) },
            onRemove = { viewModel.handleAction(FavoritesUiAction.RemoveFavorite(it)) },
            onClearAll = { viewModel.handleAction(FavoritesUiAction.ClearAllFavorites) },
            onRetryClick = { viewModel.handleAction(FavoritesUiAction.LoadFavorites) },
            onErrorDismiss = { viewModel.handleAction(FavoritesUiAction.DismissError) },
        )
    }
}

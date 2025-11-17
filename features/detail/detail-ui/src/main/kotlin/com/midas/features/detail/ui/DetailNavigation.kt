package com.midas.features.detail.ui

import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.midas.core.ui.navigation.Routes
import com.midas.features.detail.ui.state.DetailUiAction

fun NavGraphBuilder.detail(navController: NavController) {
    composable<Routes.Detail> {
        val viewModel = hiltViewModel<DetailViewModel>()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        DetailScreen(
            uiState = uiState,
            onNavigateUp = { navController.popBackStack() },
            onRefresh = { viewModel.handleAction(DetailUiAction.RefreshDetail) },
            onToggleFavorite = { viewModel.handleAction(DetailUiAction.ToggleFavorite) },
            onRetryClick = { viewModel.handleAction(DetailUiAction.RefreshDetail) },
            onErrorDismiss = { viewModel.handleAction(DetailUiAction.DismissError) },
        )
    }
}

package com.midas.features.favorites.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.midas.features.favorites.domain.usecase.ClearAllFavoritesUseCase
import com.midas.features.favorites.domain.usecase.GetAllFavoritesUseCase
import com.midas.features.favorites.domain.usecase.ToggleFavoriteUseCase
import com.midas.features.favorites.ui.mapper.toCoin
import com.midas.features.favorites.ui.mapper.toCoinUiModel
import com.midas.features.favorites.ui.mapper.toSortOrder
import com.midas.features.favorites.ui.model.CoinUiModel
import com.midas.features.favorites.ui.model.SortOrderUiModel
import com.midas.features.favorites.ui.state.FavoritesUiAction
import com.midas.features.favorites.ui.state.FavoritesUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val getAllFavoritesUseCase: GetAllFavoritesUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val clearAllFavoritesUseCase: ClearAllFavoritesUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    fun handleAction(action: FavoritesUiAction) {
        when (action) {
            is FavoritesUiAction.LoadFavorites -> loadFavorites()
            is FavoritesUiAction.ChangeSortOrder -> changeSortOrder(action.sortOrder)
            is FavoritesUiAction.RemoveFavorite -> removeFavorite(action.coin)
            is FavoritesUiAction.ClearAllFavorites -> clearAllFavorites()
            is FavoritesUiAction.DismissError -> dismissError()
        }
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isFavoritesLoading = true,
                    loadFavoritesError = null
                )
            }

            getAllFavoritesUseCase(
                params = GetAllFavoritesUseCase.Params(
                    sortOrder = _uiState.value.sortOrder?.toSortOrder(),
                )
            ).collect { result ->
                result.fold(
                    onSuccess = { favorites ->
                        _uiState.update {
                            it.copy(
                                favorites = favorites.map { coin -> coin.toCoinUiModel() },
                                isFavoritesLoading = false,
                                loadFavoritesError = null
                            )
                        }
                    },
                    onFailure = { error ->
                        _uiState.update {
                            it.copy(
                                isFavoritesLoading = false,
                                loadFavoritesError = error,
                            )
                        }
                    }
                )
            }
        }
    }

    private fun removeFavorite(coin: CoinUiModel) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isUpdateFavoriteLoading = true,
                    loadFavoritesError = null
                )
            }

            toggleFavoriteUseCase(
                params = ToggleFavoriteUseCase.Params(
                    coin = coin.toCoin(),
                )
            ).collect { result ->
                result.fold(
                    onSuccess = {
                        loadFavorites()
                    },
                    onFailure = { error ->
                        _uiState.update {
                            it.copy(
                                isUpdateFavoriteLoading = false,
                                updateFavoriteError = error,
                            )
                        }
                    }
                )
            }
        }
    }

    private fun clearAllFavorites() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isFavoritesLoading = true,
                    loadFavoritesError = null
                )
            }

            clearAllFavoritesUseCase()
                .collect { result ->
                    result.fold(
                        onSuccess = {
                            _uiState.update {
                                it.copy(
                                    favorites = emptyList(),
                                    isFavoritesLoading = false,
                                    loadFavoritesError = null
                                )
                            }
                        },
                        onFailure = { error ->
                            _uiState.update {
                                it.copy(
                                    isFavoritesLoading = false,
                                    loadFavoritesError = error,
                                )
                            }
                        }
                    )
                }
        }
    }

    private fun changeSortOrder(sortOrder: SortOrderUiModel) {
        _uiState.update {
            it.copy(sortOrder = sortOrder)
        }
        loadFavorites()
    }

    private fun dismissError() {
        _uiState.update {
            it.copy(
                loadFavoritesError = null,
                updateFavoriteError = null,
            )
        }
    }
}

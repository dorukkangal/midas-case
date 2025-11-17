package com.midas.features.detail.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.midas.core.ui.model.ErrorUiState
import com.midas.features.detail.domain.usecase.GetCoinDetailUseCase
import com.midas.features.detail.ui.mapper.toCoin
import com.midas.features.detail.ui.mapper.toCoinDetailUiModel
import com.midas.features.detail.ui.state.DetailUiAction
import com.midas.features.detail.ui.state.DetailUiState
import com.midas.features.favorites.domain.usecase.IsFavoriteUseCase
import com.midas.features.favorites.domain.usecase.ToggleFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val getCoinDetailUseCase: GetCoinDetailUseCase,
    private val isFavoriteUseCase: IsFavoriteUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val coinId: String = checkNotNull(savedStateHandle["coinId"]) {
        "Coin ID is required"
    }

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    init {
        loadCoinDetail()
        checkFavoriteStatus()
    }

    fun handleAction(action: DetailUiAction) {
        when (action) {
            is DetailUiAction.RefreshDetail -> loadCoinDetail()
            is DetailUiAction.ToggleFavorite -> toggleFavorite()
            is DetailUiAction.DismissError -> dismissError()
        }
    }

    private fun loadCoinDetail() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    loadDetailError = null,
                )
            }

            getCoinDetailUseCase(
                params = GetCoinDetailUseCase.Params(
                    coinId = coinId,
                )
            ).collect { result ->
                result.fold(
                    onSuccess = { coinDetail ->
                        _uiState.update {
                            it.copy(
                                coinDetail = coinDetail.toCoinDetailUiModel(),
                                isLoading = false,
                                loadDetailError = null,
                            )
                        }
                    },
                    onFailure = { error ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                loadDetailError = ErrorUiState(
                                    message = error.message,
                                ),
                            )
                        }
                    }
                )
            }
        }
    }

    private fun checkFavoriteStatus() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isFavoriteLoading = true,
                    loadFavoriteError = null
                )
            }

            isFavoriteUseCase(
                params = IsFavoriteUseCase.Params(
                    coinId = coinId,
                )
            ).collect { result ->
                result.fold(
                    onSuccess = { isFavorite ->
                        _uiState.update {
                            it.copy(
                                isFavorite = isFavorite,
                                isFavoriteLoading = false,
                                loadFavoriteError = null,
                            )
                        }
                    },
                    onFailure = { error ->
                        _uiState.update {
                            it.copy(
                                isFavoriteLoading = false,
                                loadDetailError = ErrorUiState(
                                    message = error.message,
                                ),
                            )
                        }
                    }
                )
            }
        }
    }

    private fun toggleFavorite() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isFavoriteLoading = true,
                    loadFavoriteError = null
                )
            }

            val currentDetail = _uiState.value.coinDetail ?: return@launch
            toggleFavoriteUseCase(
                params = ToggleFavoriteUseCase.Params(
                    coin = currentDetail.toCoin(),
                )
            ).collect { result ->
                result.fold(
                    onSuccess = { favoriteAction ->
                        _uiState.update {
                            it.copy(
                                isFavorite = favoriteAction == ToggleFavoriteUseCase.FavoriteAction.ADDED,
                                isFavoriteLoading = false,
                                loadFavoriteError = null,
                            )
                        }
                    },
                    onFailure = { error ->
                        _uiState.update {
                            it.copy(
                                isFavoriteLoading = false,
                                loadDetailError = ErrorUiState(
                                    message = error.message,
                                ),
                            )
                        }
                    }
                )
            }
        }
    }

    private fun dismissError() {
        _uiState.update {
            it.copy(
                loadDetailError = null,
                loadFavoriteError = null,
            )
        }
    }
}

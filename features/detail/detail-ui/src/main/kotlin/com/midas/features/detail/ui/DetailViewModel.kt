package com.midas.features.detail.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import kotlinx.coroutines.flow.combine
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

    fun handleAction(action: DetailUiAction) {
        when (action) {
            is DetailUiAction.RefreshDetail -> {
                loadCoinDetail()
            }

            is DetailUiAction.ToggleFavorite -> {
                toggleFavorite()
            }

            is DetailUiAction.DismissError -> {
                dismissError()
            }
        }
    }

    private fun loadCoinDetail() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isDetailLoading = true,
                    isFavoriteLoading = true,
                    loadDetailError = null,
                    loadFavoriteError = null,
                )
            }

            combine(
                getCoinDetailUseCase(
                    params = GetCoinDetailUseCase.Params(
                        coinId = coinId,
                    )
                ),
                isFavoriteUseCase(
                    params = IsFavoriteUseCase.Params(
                        coinId = coinId,
                    )
                )
            ) { coinDetailResult, isFavoriteResult ->
                Pair(coinDetailResult, isFavoriteResult)
            }.collect { (coinDetailResult, isFavoriteResult) ->
                coinDetailResult.fold(
                    onSuccess = { coinDetail ->
                        _uiState.update {
                            it.copy(
                                coinDetail = coinDetail.toCoinDetailUiModel(),
                                isDetailLoading = false,
                                loadDetailError = null,
                            )
                        }
                    },
                    onFailure = { error ->
                        _uiState.update {
                            it.copy(
                                isDetailLoading = false,
                                loadDetailError = error,
                            )
                        }
                    }
                )

                isFavoriteResult.fold(
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
                                loadFavoriteError = error,
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
                                loadDetailError = error,
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

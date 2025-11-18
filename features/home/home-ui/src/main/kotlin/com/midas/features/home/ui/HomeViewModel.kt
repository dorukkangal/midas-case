package com.midas.features.home.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.midas.features.home.domain.usecase.GetCoinsUseCase
import com.midas.features.home.domain.usecase.GetTrendingCoinsUseCase
import com.midas.features.home.domain.usecase.SearchCoinsUseCase
import com.midas.features.home.ui.mapper.toCoinUiModel
import com.midas.features.home.ui.state.HomeUiAction
import com.midas.features.home.ui.state.HomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getCoinsUseCase: GetCoinsUseCase,
    private val getTrendingCoinsUseCase: GetTrendingCoinsUseCase,
    private val searchCoinsUseCase: SearchCoinsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        observeSearchQuery()
    }

    fun handleAction(action: HomeUiAction) {
        when (action) {
            is HomeUiAction.SearchCoins -> updateSearchQuery(action.query)
            is HomeUiAction.RefreshData -> refreshAllData()
            is HomeUiAction.RetryLoading -> retryLoading()
            is HomeUiAction.ClearSearch -> clearSearch()
            is HomeUiAction.DismissError -> dismissError()
        }
    }

    private fun observeSearchQuery() {
        _searchQuery
            .debounce(300)
            .distinctUntilChanged()
            .filter { it.length >= 2 || it.isEmpty() }
            .onEach { query ->
                if (query.isBlank()) {
                    // Clear search results when query is empty
                    _uiState.update {
                        it.copy(
                            searchResults = emptyList(),
                            isSearching = false
                        )
                    }
                } else {
                    searchCoins(query)
                }
            }
            .launchIn(viewModelScope)
    }

    /**
     * Loads both coins and trending coins in parallel using combine.
     * This ensures both data sets are fetched simultaneously for better performance.
     */
    private fun loadCoinsAndTrending() {
        viewModelScope.launch {
            // Set loading states
            _uiState.update {
                it.copy(
                    isLoading = true,
                    isRefreshing = false,
                    loadError = null,
                )
            }

            // Combine both flows to load data in parallel
            combine(
                getCoinsUseCase(),
                getTrendingCoinsUseCase()
            ) { coinsResult, trendingResult ->
                Pair(coinsResult, trendingResult)
            }.collect { (coinsResult, trendingResult) ->
                // Handle coins result
                coinsResult.fold(
                    onSuccess = { coins ->
                        _uiState.update {
                            it.copy(
                                coins = coins.map { coin -> coin.toCoinUiModel() },
                                isLoading = false,
                                loadError = null
                            )
                        }
                    },
                    onFailure = { error ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                loadError = error,
                            )
                        }
                    }
                )

                // Handle trending result
                trendingResult.fold(
                    onSuccess = { trendingCoins ->
                        _uiState.update {
                            it.copy(
                                trendingCoins = trendingCoins.map { coin -> coin.toCoinUiModel() },
                                isLoading = false,
                                loadError = null
                            )
                        }
                    },
                    onFailure = { error ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                loadError = error,
                            )
                        }
                    }
                )

                // Clear refreshing state when both are done
                _uiState.update {
                    it.copy(isRefreshing = false)
                }
            }
        }
    }

    private fun searchCoins(query: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isSearching = true
                )
            }

            searchCoinsUseCase(query)
                .collect { result ->
                    result.fold(
                        onSuccess = { searchResults ->
                            _uiState.update {
                                it.copy(
                                    searchResults = searchResults.map { coin -> coin.toCoinUiModel() },
                                    isSearching = false
                                )
                            }
                        },
                        onFailure = {
                            _uiState.update {
                                it.copy(
                                    searchResults = emptyList(),
                                    isSearching = false
                                )
                            }
                        }
                    )
                }
        }
    }

    private fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    private fun refreshAllData() {
        loadCoinsAndTrending()
    }

    private fun retryLoading() {
        loadCoinsAndTrending()
    }

    private fun clearSearch() {
        _searchQuery.value = ""
    }

    private fun dismissError() {
        _uiState.update {
            it.copy(
                loadError = null,
            )
        }
    }
}

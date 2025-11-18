package com.midas.features.home.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.midas.core.ui.components.CoinListItemSkeleton
import com.midas.core.ui.components.PullToRefreshBox
import com.midas.core.ui.components.TrendingCoinCardSkeleton
import com.midas.core.ui.dialog.PopupDialog
import com.midas.core.ui.theme.sizing
import com.midas.features.home.ui.components.CoinListItem
import com.midas.features.home.ui.components.SearchBar
import com.midas.features.home.ui.components.TrendingCoinCard
import com.midas.features.home.ui.model.CoinUiModel
import com.midas.features.home.ui.state.HomeUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    uiState: HomeUiState,
    searchQuery: String,
    onCoinClick: (CoinUiModel) -> Unit,
    onFavoritesClick: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onClearSearch: () -> Unit,
    onRefresh: () -> Unit,
    onRetryClick: () -> Unit,
    onErrorDismiss: () -> Unit,
) {
    LaunchedEffect(Unit) {
        onRefresh()
    }

    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(MaterialTheme.sizing.spaceMedium)
            ) {
                // Top Bar with Search and Favorites
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SearchBar(
                        query = searchQuery,
                        onQueryChange = onSearchQueryChange,
                        onClearClick = onClearSearch,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.size(MaterialTheme.sizing.spaceSmall))

                    // Favorites Button
                    IconButton(
                        onClick = onFavoritesClick
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bookmark,
                            contentDescription = stringResource(R.string.favorites),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(MaterialTheme.sizing.spaceMedium))

                // Content based on search state with pull-to-refresh
                if (searchQuery.isNotBlank()) {
                    SearchResults(
                        searchResults = uiState.searchResults,
                        isSearching = uiState.isSearching,
                        onCoinClick = onCoinClick
                    )
                } else {
                    PullToRefreshBox(
                        isRefreshing = uiState.isRefreshing,
                        onRefresh = onRefresh
                    ) {
                        MainContent(
                            uiState = uiState,
                            onCoinClick = onCoinClick
                        )
                    }
                }
            }

            // Error Dialogs
            uiState.loadCoinsError?.let { error ->
                PopupDialog(
                    message = error.message
                        ?: stringResource(com.midas.core.ui.R.string.unknown_error),
                    confirmButton = stringResource(com.midas.core.ui.R.string.retry),
                    onConfirm = onRetryClick,
                    onDismiss = onErrorDismiss
                )
            }

            uiState.loadTrendingCoinsError?.let { error ->
                PopupDialog(
                    message = error.message
                        ?: stringResource(com.midas.core.ui.R.string.unknown_error),
                    confirmButton = stringResource(com.midas.core.ui.R.string.retry),
                    onConfirm = onRetryClick,
                    onDismiss = onErrorDismiss
                )
            }
        }
    }
}

@Composable
private fun MainContent(
    uiState: HomeUiState,
    onCoinClick: (CoinUiModel) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(
            MaterialTheme.sizing.spaceMedium
        )
    ) {
        // Trending Section
        item {
            TrendingSection(
                trendingCoins = uiState.trendingCoins,
                isLoading = uiState.isTrendingLoading,
                onCoinClick = onCoinClick
            )
        }

        // Main Coins Section
        item {
            Text(
                text = stringResource(R.string.all_crypto_currencies),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }

        // Loading State
        if (uiState.isLoading && uiState.coins.isEmpty()) {
            items(10) {
                CoinListItemSkeleton()
            }
        }

        // Coins List
        items(
            items = uiState.coins,
            key = { it.id }
        ) { coin ->
            CoinListItem(
                coin = coin,
                onClick = { onCoinClick(coin) }
            )
        }
    }
}

@Composable
private fun TrendingSection(
    trendingCoins: List<CoinUiModel>,
    isLoading: Boolean,
    onCoinClick: (CoinUiModel) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.trending),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(MaterialTheme.sizing.iconSizeSmall),
                    strokeWidth = 2.dp
                )
            }
        }

        Spacer(modifier = Modifier.height(MaterialTheme.sizing.spaceSmall))

        if (trendingCoins.isNotEmpty()) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = trendingCoins,
                    key = { it.id }
                ) { coin ->
                    TrendingCoinCard(
                        coin = coin,
                        onClick = { onCoinClick(coin) }
                    )
                }
            }
        } else if (isLoading) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(5) {
                    TrendingCoinCardSkeleton()
                }
            }
        }
    }
}

@Composable
private fun SearchResults(
    searchResults: List<CoinUiModel>,
    isSearching: Boolean,
    onCoinClick: (CoinUiModel) -> Unit
) {
    Column {
        Text(
            text = stringResource(R.string.search_results),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(MaterialTheme.sizing.spaceSmall))

        if (isSearching) {
            repeat(5) {
                CoinListItemSkeleton()
                Spacer(modifier = Modifier.height(MaterialTheme.sizing.spaceSmall))
            }
        } else if (searchResults.isEmpty()) {
            Text(
                text = stringResource(R.string.no_results_found),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(
                    MaterialTheme.sizing.spaceSmall
                )
            ) {
                items(
                    items = searchResults,
                    key = { it.id }
                ) { coin ->
                    CoinListItem(
                        coin = coin,
                        onClick = { onCoinClick(coin) }
                    )
                }
            }
        }
    }
}

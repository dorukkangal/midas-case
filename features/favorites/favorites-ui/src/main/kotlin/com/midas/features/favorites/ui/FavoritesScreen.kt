package com.midas.features.favorites.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.midas.core.ui.components.PullToRefreshBox
import com.midas.core.ui.dialog.PopupDialog
import com.midas.core.ui.theme.sizing
import com.midas.core.ui.util.rememberDebouncedCallback
import com.midas.features.favorites.ui.model.CoinUiModel
import com.midas.features.favorites.ui.model.SortOrderUiModel
import com.midas.features.favorites.ui.state.FavoritesUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    uiState: FavoritesUiState,
    onNavigateUp: () -> Unit,
    onChangeSortOrder: (SortOrderUiModel) -> Unit,
    onRefresh: () -> Unit,
    onCoinClick: (CoinUiModel) -> Unit,
    onRemove: (CoinUiModel) -> Unit,
    onClearAll: () -> Unit,
    onRetryClick: () -> Unit,
    onErrorDismiss: () -> Unit,
) {
    var showSortMenu by remember { mutableStateOf(false) }
    var showClearDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        onRefresh()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.favorites)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = rememberDebouncedCallback(callback = onNavigateUp)) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(com.midas.core.ui.R.string.back)
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showSortMenu = true }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = stringResource(R.string.sort)
                        )
                    }
                    DropdownMenu(
                        expanded = showSortMenu,
                        onDismissRequest = { showSortMenu = false }
                    ) {
                        SortOrderUiModel.entries.forEach { sortOrder ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = stringResource(sortOrder.displayName)
                                    )
                                },
                                onClick = {
                                    onChangeSortOrder(sortOrder)
                                    showSortMenu = false
                                }
                            )
                        }

                        HorizontalDivider()

                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = stringResource(R.string.clear_all),
                                    color = MaterialTheme.colorScheme.error,
                                )
                            },
                            onClick = {
                                onClearAll()
                                showSortMenu = false
                                showClearDialog = true
                            }
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                uiState.isFavoritesLoading && uiState.favorites.isEmpty() -> {
                    LoadingState(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                uiState.loadFavoritesError != null -> {
                    ErrorState(
                        message = uiState.loadFavoritesError.message
                            ?: stringResource(com.midas.core.ui.R.string.unknown_error),
                        onRetryClick = onRetryClick,
                        onErrorDismiss = onErrorDismiss,
                    )
                }

                uiState.updateFavoriteError != null -> {
                    ErrorState(
                        message = stringResource(R.string.failed_to_update_favorites),
                        onRetryClick = onRetryClick,
                        onErrorDismiss = onErrorDismiss,
                    )
                }

                uiState.favorites.isEmpty() -> {
                    EmptyState(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    PullToRefreshBox(
                        isRefreshing = uiState.isFavoritesLoading,
                        onRefresh = onRefresh,
                    ) {
                        FavoritesContent(
                            favorites = uiState.favorites,
                            sortOrder = uiState.sortOrder,
                            onCoinClick = onCoinClick,
                            onRemoveFavorite = { coin ->
                                onRemove(coin)
                            }
                        )
                    }
                }
            }
        }
    }

    // Clear all dialog
    if (showClearDialog) {
        PopupDialog(
            title = stringResource(R.string.remove_all_favorites),
            message = stringResource(
                R.string.are_you_sure_you_want_to_remove_all_favorite_coins,
                uiState.favorites.size
            ),
            confirmButton = stringResource(com.midas.core.ui.R.string.ok),
            cancelButton = stringResource(com.midas.core.ui.R.string.cancel),
            onConfirm = {
                onClearAll()
            },
            onDismiss = {
                showClearDialog = false
            },
        )
    }
}

@Composable
private fun FavoritesContent(
    favorites: List<CoinUiModel>,
    sortOrder: SortOrderUiModel?,
    onCoinClick: (CoinUiModel) -> Unit,
    onRemoveFavorite: (CoinUiModel) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(MaterialTheme.sizing.spaceMedium),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Row(
                modifier = Modifier.padding(MaterialTheme.sizing.spaceMedium),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.favorites_count, favorites.size),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = stringResource(
                        R.string.sorted_by,
                        sortOrder?.displayName?.let { stringResource(it) } ?: "-"
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }

        // Favorites list
        LazyColumn(
            contentPadding = PaddingValues(
                horizontal = MaterialTheme.sizing.spaceMedium,
                vertical = MaterialTheme.sizing.spaceSmall
            ),
            verticalArrangement = Arrangement.spacedBy(
                MaterialTheme.sizing.spaceSmall
            )
        ) {
            items(
                items = favorites,
                key = { it.id }
            ) { coin ->
                FavoriteCoinItem(
                    coin = coin,
                    onClick = { onCoinClick(coin) },
                    onRemove = { onRemoveFavorite(coin) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FavoriteCoinItem(
    coin: CoinUiModel,
    onClick: () -> Unit,
    onRemove: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(MaterialTheme.sizing.spaceMedium),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Coin info
            Row(
                horizontalArrangement = Arrangement.spacedBy(
                    MaterialTheme.sizing.spaceMedium
                ),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(coin.image)
                        .memoryCacheKey(coin.id)
                        .diskCacheKey(coin.id)
                        .crossfade(true)
                        .build(),
                    contentDescription = coin.name,
                    modifier = Modifier.size(40.dp)
                )

                Column {
                    Text(
                        text = coin.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = coin.symbol.uppercase(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            // Price and change
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = coin.currentPrice?.displayAmount ?: "-",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                coin.priceChangePercentage24h?.let { change ->
                    Text(
                        text = change.displayPercentage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = change.changeColor,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Delete button
            IconButton(
                onClick = { showDeleteDialog = true },
                modifier = Modifier.padding(
                    start = MaterialTheme.sizing.spaceSmall
                )
            ) {
                Icon(
                    Icons.Filled.Delete,
                    contentDescription = stringResource(R.string.remove_from_favorites),
                )
            }
        }
    }

    // Delete confirmation dialog
    if (showDeleteDialog) {
        PopupDialog(
            title = stringResource(R.string.remove_from_favorites),
            message = stringResource(R.string.remove_from_your_favorites, coin.name),
            confirmButton = stringResource(com.midas.core.ui.R.string.remove),
            cancelButton = stringResource(com.midas.core.ui.R.string.cancel),
            onConfirm = {
                onRemove()
            },
            onDismiss = {
                showDeleteDialog = false
            }
        )
    }
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(
            MaterialTheme.sizing.spaceSmall
        )
    ) {
        Text(
            text = stringResource(R.string.no_favorites_yet),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = stringResource(R.string.start_adding_coins_to_your_favorites),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.secondary,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun LoadingState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(
            MaterialTheme.sizing.spaceMedium
        )
    ) {
        CircularProgressIndicator()
        Text(stringResource(R.string.loading_favorites))
    }
}

@Composable
private fun ErrorState(
    message: String,
    onRetryClick: () -> Unit,
    onErrorDismiss: () -> Unit,
) {
    PopupDialog(
        message = message,
        confirmButton = stringResource(com.midas.core.ui.R.string.retry),
        onConfirm = onRetryClick,
        onDismiss = onErrorDismiss,
    )
}

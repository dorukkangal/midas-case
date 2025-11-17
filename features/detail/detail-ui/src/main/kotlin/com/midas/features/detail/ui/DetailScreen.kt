package com.midas.features.detail.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.midas.core.ui.components.PullToRefreshBox
import com.midas.core.ui.dialog.PopupDialog
import com.midas.core.ui.theme.sizing
import com.midas.features.detail.ui.model.CoinDetailUiModel
import com.midas.features.detail.ui.state.DetailUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    uiState: DetailUiState,
    onNavigateUp: () -> Unit,
    onRefresh: () -> Unit,
    onToggleFavorite: () -> Unit,
    onRetryClick: () -> Unit,
    onErrorDismiss: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.coin_detail)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(com.midas.core.ui.R.string.back)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onToggleFavorite) {
                        Icon(
                            imageVector = if (uiState.isFavorite) {
                                Icons.Filled.Bookmark
                            } else {
                                Icons.Filled.BookmarkBorder
                            },
                            contentDescription = stringResource(R.string.favorite),
                        )
                    }
                }
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                uiState.isLoading && uiState.coinDetail == null -> {
                    LoadingState()
                }

                uiState.loadDetailError != null -> {
                    ErrorState(
                        message = uiState.loadDetailError.message
                            ?: stringResource(com.midas.core.ui.R.string.unknown_error),
                        onRetryClick = onRetryClick,
                        onErrorDismiss = onErrorDismiss,
                    )
                }

                uiState.loadFavoriteError != null -> {
                    ErrorState(
                        message = stringResource(R.string.failed_to_update_favorites),
                        onRetryClick = onRetryClick,
                        onErrorDismiss = onErrorDismiss,
                    )
                }

                uiState.coinDetail != null -> {
                    PullToRefreshBox(
                        isRefreshing = uiState.isLoading,
                        onRefresh = onRefresh,
                    ) {
                        DetailContent(
                            coinDetail = uiState.coinDetail,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailContent(
    coinDetail: CoinDetailUiModel,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(MaterialTheme.sizing.spaceMedium),
        verticalArrangement = Arrangement.spacedBy(
            MaterialTheme.sizing.spaceMedium
        )
    ) {
        // Header with coin info
        CoinHeader(coinDetail)

        // Price Info Card
        PriceInfoCard(coinDetail)

        // Market Stats Card
        MarketStatsCard(coinDetail)

        // Description Card
        if (coinDetail.description.isNullOrEmpty().not()) {
            DescriptionCard(coinDetail.description)
        }

        // Categories Card
        if (coinDetail.categories.isNotEmpty()) {
            CategoriesCard(coinDetail.categories)
        }

        // Additional Info Card
        AdditionalInfoCard(coinDetail)
    }
}

@Composable
private fun CoinHeader(coinDetail: CoinDetailUiModel) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(MaterialTheme.sizing.spaceMedium)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(
                MaterialTheme.sizing.spaceMedium
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(coinDetail.image)
                    .memoryCacheKey(coinDetail.id)
                    .diskCacheKey(coinDetail.id)
                    .crossfade(true)
                    .build(),
                contentDescription = coinDetail.name,
                modifier = Modifier.size(MaterialTheme.sizing.spaceXLarge)
            )

            Column {
                Text(
                    text = coinDetail.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = coinDetail.symbol.uppercase(),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
                coinDetail.marketCapRank?.let { rank ->
                    Text(
                        text = stringResource(R.string.market_cap_rank, rank),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
            }
        }
    }
}

@Composable
private fun PriceInfoCard(coinDetail: CoinDetailUiModel) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(MaterialTheme.sizing.spaceMedium),
            verticalArrangement = Arrangement.spacedBy(
                MaterialTheme.sizing.spaceSmall
            )
        ) {
            Text(
                text = stringResource(R.string.price_information),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            HorizontalDivider()

            coinDetail.marketData.currentPrice?.let { price ->
                PriceRow(
                    label = stringResource(R.string.current_price),
                    value = price.displayAmount
                )
            }

            coinDetail.marketData.priceChangePercentage24h?.let { change ->
                PriceRow(
                    label = stringResource(R.string.last_day_change),
                    value = change.displayPercentage,
                    valueColor = change.changeColor
                )
            }

            coinDetail.marketData.high24h?.let { high ->
                PriceRow(
                    label = stringResource(R.string.highest_value_in_last_day),
                    value = high.displayAmount
                )
            }

            coinDetail.marketData.low24h?.let { low ->
                PriceRow(
                    label = stringResource(R.string.lowest_value_in_last_day),
                    value = low.displayAmount
                )
            }

            coinDetail.marketData.priceChangePercentage7d?.let { change ->
                PriceRow(
                    label = stringResource(R.string.last_week_change),
                    value = change.displayPercentage,
                    valueColor = change.changeColor
                )
            }

            coinDetail.marketData.priceChangePercentage30d?.let { change ->
                PriceRow(
                    label = stringResource(R.string.last_month_change_change),
                    value = change.displayPercentage,
                    valueColor = change.changeColor
                )
            }
        }
    }
}

@Composable
private fun MarketStatsCard(coinDetail: CoinDetailUiModel) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(MaterialTheme.sizing.spaceMedium),
            verticalArrangement = Arrangement.spacedBy(
                MaterialTheme.sizing.spaceSmall
            )
        ) {
            Text(
                text = stringResource(R.string.market_statistics),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            HorizontalDivider()

            coinDetail.marketData.marketCap?.let { marketCap ->
                StatRow(
                    label = stringResource(R.string.market_cap),
                    value = marketCap.displayNumber
                )
            }

            coinDetail.marketData.totalVolume?.let { volume ->
                StatRow(
                    label = stringResource(R.string.last_day_volume),
                    value = volume.displayNumber
                )
            }

            coinDetail.marketData.circulatingSupply?.let { supply ->
                StatRow(
                    label = stringResource(R.string.circulating_supply),
                    value = supply.displayNumber
                )
            }

            coinDetail.marketData.totalSupply?.let { supply ->
                StatRow(
                    label = stringResource(R.string.total_supply),
                    value = supply.displayNumber
                )
            }

            coinDetail.marketData.maxSupply?.let { supply ->
                StatRow(
                    label = stringResource(R.string.max_supply),
                    value = supply.displayNumber
                )
            }
        }
    }
}

@Composable
private fun DescriptionCard(description: String) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(MaterialTheme.sizing.spaceMedium),
            verticalArrangement = Arrangement.spacedBy(
                MaterialTheme.sizing.spaceSmall
            )
        ) {
            Text(
                text = stringResource(R.string.about),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            HorizontalDivider()

            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Justify
            )
        }
    }
}

@Composable
private fun CategoriesCard(categories: List<String>) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(MaterialTheme.sizing.spaceMedium),
            verticalArrangement = Arrangement.spacedBy(
                MaterialTheme.sizing.spaceSmall
            )
        ) {
            Text(
                text = stringResource(R.string.categories),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            HorizontalDivider()

            categories.take(5).forEach { category ->
                AssistChip(
                    onClick = { },
                    label = { Text(category) }
                )
            }
        }
    }
}

@Composable
private fun AdditionalInfoCard(coinDetail: CoinDetailUiModel) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(MaterialTheme.sizing.spaceMedium),
            verticalArrangement = Arrangement.spacedBy(
                MaterialTheme.sizing.spaceSmall
            )
        ) {
            Text(
                text = stringResource(R.string.additional_information),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            HorizontalDivider()

            coinDetail.genesisDate?.let { date ->
                StatRow(
                    label = stringResource(R.string.genesis_date),
                    value = date
                )
            }
        }
    }
}

@Composable
private fun PriceRow(
    label: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = valueColor
        )
    }
}

@Composable
private fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
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
        Text(stringResource(R.string.loading_coin_details))
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

package com.midas.features.detail.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.midas.core.ui.mapper.toAmountUiModel
import com.midas.core.ui.mapper.toLargeNumberUiModel
import com.midas.core.ui.mapper.toPercentageUiModel
import com.midas.features.detail.ui.model.CoinDetailUiModel
import com.midas.features.detail.ui.model.MarketDataUiModel
import com.midas.features.detail.ui.state.DetailUiState
import org.junit.Rule
import org.junit.Test

class DetailScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun detailScreen_displaysLoadingState() {
        // Given
        val uiState = DetailUiState(isLoading = true)

        // When
        composeTestRule.setContent {
            DetailScreen(
                uiState = uiState,
                onNavigateUp = {},
                onRefresh = {},
                onToggleFavorite = {},
                onRetryClick = {},
                onErrorDismiss = {}
            )
        }

        // Then
        composeTestRule
            .onNodeWithContentDescription("Loading coin details...")
            .assertExists()
    }

    @Test
    fun detailScreen_displaysCoinDetails() {
        // Given
        val mockCoinDetail = createMockCoinDetail()
        val uiState = DetailUiState(coinDetail = mockCoinDetail)

        // When
        composeTestRule.setContent {
            DetailScreen(
                uiState = uiState,
                onNavigateUp = {},
                onRefresh = {},
                onToggleFavorite = {},
                onRetryClick = {},
                onErrorDismiss = {}
            )
        }

        // Then
        composeTestRule
            .onNodeWithText("Bitcoin")
            .assertExists()
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("BTC")
            .assertExists()
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("$50,000.00")
            .assertExists()
    }

    @Test
    fun detailScreen_displaysMarketData() {
        // Given
        val mockCoinDetail = createMockCoinDetail()
        val uiState = DetailUiState(coinDetail = mockCoinDetail)

        // When
        composeTestRule.setContent {
            DetailScreen(
                uiState = uiState,
                onNavigateUp = {},
                onRefresh = {},
                onToggleFavorite = {},
                onRetryClick = {},
                onErrorDismiss = {}
            )
        }

        // Then
        composeTestRule
            .onNodeWithText("Market Cap")
            .assertExists()

        composeTestRule
            .onNodeWithText("1.00B")
            .assertExists()

        composeTestRule
            .onNodeWithText("24h Volume")
            .assertExists()
    }

    @Test
    fun detailScreen_displaysPriceChanges() {
        // Given
        val mockCoinDetail = createMockCoinDetail()
        val uiState = DetailUiState(coinDetail = mockCoinDetail)

        // When
        composeTestRule.setContent {
            DetailScreen(
                uiState = uiState,
                onNavigateUp = {},
                onRefresh = {},
                onToggleFavorite = {},
                onRetryClick = {},
                onErrorDismiss = {}
            )
        }

        // Then
        composeTestRule
            .onNodeWithText("24h Change")
            .assertExists()

        composeTestRule
            .onNodeWithText("+2.00%")
            .assertExists()
    }

    @Test
    fun detailScreen_displaysDescription() {
        // Given
        val mockCoinDetail = createMockCoinDetail(
            description = "Bitcoin is a decentralized digital currency"
        )
        val uiState = DetailUiState(coinDetail = mockCoinDetail)

        // When
        composeTestRule.setContent {
            DetailScreen(
                uiState = uiState,
                onNavigateUp = {},
                onRefresh = {},
                onToggleFavorite = {},
                onRetryClick = {},
                onErrorDismiss = {}
            )
        }

        // Then
        composeTestRule
            .onNodeWithText("About")
            .assertExists()

        composeTestRule
            .onNodeWithText("Bitcoin is a decentralized digital currency")
            .assertExists()
    }

    @Test
    fun detailScreen_backButtonTriggersCallback() {
        // Given
        val mockCoinDetail = createMockCoinDetail()
        val uiState = DetailUiState(coinDetail = mockCoinDetail)
        var backClicked = false

        // When
        composeTestRule.setContent {
            DetailScreen(
                uiState = uiState,
                onNavigateUp = { backClicked = true },
                onRefresh = {},
                onToggleFavorite = {},
                onRetryClick = {},
                onErrorDismiss = {}
            )
        }

        composeTestRule
            .onNodeWithContentDescription("Back")
            .performClick()

        // Then
        assert(backClicked)
    }

    @Test
    fun detailScreen_favoriteButtonTriggersCallback() {
        // Given
        val mockCoinDetail = createMockCoinDetail()
        val uiState = DetailUiState(
            coinDetail = mockCoinDetail,
            isFavorite = false
        )
        var favoriteClicked = false

        // When
        composeTestRule.setContent {
            DetailScreen(
                uiState = uiState,
                onNavigateUp = {},
                onToggleFavorite = { favoriteClicked = true },
                onRefresh = {},
                onRetryClick = {},
                onErrorDismiss = {}
            )
        }

        composeTestRule
            .onNodeWithContentDescription("Favorite")
            .performClick()

        // Then
        assert(favoriteClicked)
    }

    @Test
    fun detailScreen_displaysFavoriteIcon_whenFavorite() {
        // Given
        val mockCoinDetail = createMockCoinDetail()
        val uiState = DetailUiState(
            coinDetail = mockCoinDetail,
            isFavorite = true
        )

        // When
        composeTestRule.setContent {
            DetailScreen(
                uiState = uiState,
                onNavigateUp = {},
                onRefresh = {},
                onToggleFavorite = {},
                onRetryClick = {},
                onErrorDismiss = {}
            )
        }

        // Then
        composeTestRule
            .onNodeWithContentDescription("Favorite")
            .assertExists()
    }

    @Test
    fun detailScreen_displaysCategories() {
        // Given
        val mockCoinDetail = createMockCoinDetail(
            categories = listOf("Cryptocurrency", "Store of Value", "Payment")
        )
        val uiState = DetailUiState(coinDetail = mockCoinDetail)

        // When
        composeTestRule.setContent {
            DetailScreen(
                uiState = uiState,
                onNavigateUp = {},
                onRefresh = {},
                onToggleFavorite = {},
                onRetryClick = {},
                onErrorDismiss = {}
            )
        }

        // Then
        composeTestRule
            .onNodeWithText("Cryptocurrency")
            .assertExists()

        composeTestRule
            .onNodeWithText("Store of Value")
            .assertExists()
    }

    @Test
    fun detailScreen_displaysHighAndLow24h() {
        // Given
        val mockCoinDetail = createMockCoinDetail()
        val uiState = DetailUiState(coinDetail = mockCoinDetail)

        // When
        composeTestRule.setContent {
            DetailScreen(
                uiState = uiState,
                onNavigateUp = {},
                onRefresh = {},
                onToggleFavorite = {},
                onRetryClick = {},
                onErrorDismiss = {}
            )
        }

        // Then
        composeTestRule
            .onNodeWithText("24h High")
            .assertExists()

        composeTestRule
            .onNodeWithText("$51,000.00")
            .assertExists()

        composeTestRule
            .onNodeWithText("24h Low")
            .assertExists()

        composeTestRule
            .onNodeWithText("$49,000.00")
            .assertExists()
    }

    @Test
    fun detailScreen_displaysCirculatingSupply() {
        // Given
        val mockCoinDetail = createMockCoinDetail()
        val uiState = DetailUiState(coinDetail = mockCoinDetail)

        // When
        composeTestRule.setContent {
            DetailScreen(
                uiState = uiState,
                onNavigateUp = {},
                onRefresh = {},
                onToggleFavorite = {},
                onRetryClick = {},
                onErrorDismiss = {}
            )
        }

        // Then
        composeTestRule
            .onNodeWithText("Circulating Supply")
            .assertExists()

        composeTestRule
            .onNodeWithText("19.00M")
            .assertExists()
    }

    @Test
    fun detailScreen_displaysMarketCapRank() {
        // Given
        val mockCoinDetail = createMockCoinDetail(marketCapRank = 1)
        val uiState = DetailUiState(coinDetail = mockCoinDetail)

        // When
        composeTestRule.setContent {
            DetailScreen(
                uiState = uiState,
                onNavigateUp = {},
                onRefresh = {},
                onToggleFavorite = {},
                onRetryClick = {},
                onErrorDismiss = {}
            )
        }

        // Then
        composeTestRule
            .onNodeWithText("Rank #1")
            .assertExists()
    }

    private fun createMockCoinDetail(
        id: String = "bitcoin",
        name: String = "Bitcoin",
        symbol: String = "BTC",
        description: String = "Test description",
        marketCapRank: Int = 1,
        categories: List<String> = listOf("Cryptocurrency")
    ) = CoinDetailUiModel(
        id = id,
        name = name,
        symbol = symbol,
        description = description,
        image = "https://example.com/$id.png",
        marketCapRank = marketCapRank,
        marketData = MarketDataUiModel(
            currentPrice = 50000.0.toAmountUiModel(),
            marketCap = 1000000000.0.toLargeNumberUiModel(),
            totalVolume = 50000000.0.toLargeNumberUiModel(),
            high24h = 51000.0.toAmountUiModel(),
            low24h = 49000.0.toAmountUiModel(),
            priceChangePercentage24h = 2.0.toPercentageUiModel(),
            priceChangePercentage7d = 5.0.toPercentageUiModel(),
            priceChangePercentage30d = 10.0.toPercentageUiModel(),
            circulatingSupply = 19000000.0.toLargeNumberUiModel(),
            totalSupply = 21000000.0.toLargeNumberUiModel(),
            maxSupply = 21000000.0.toLargeNumberUiModel()
        ),
        categories = categories,
        genesisDate = "2009-01-03"
    )
}

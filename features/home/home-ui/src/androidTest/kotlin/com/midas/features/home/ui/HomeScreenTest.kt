package com.midas.features.home.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.midas.core.ui.mapper.toAmountUiModel
import com.midas.core.ui.mapper.toLargeNumberUiModel
import com.midas.core.ui.mapper.toPercentageUiModel
import com.midas.features.home.ui.model.CoinUiModel
import com.midas.features.home.ui.state.HomeUiState
import org.junit.Rule
import org.junit.Test

class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun homeScreen_displaysCoinList() {
        // Given
        val mockCoins = listOf(
            createMockCoin("bitcoin", "Bitcoin", "BTC"),
            createMockCoin("ethereum", "Ethereum", "ETH")
        )
        val uiState = HomeUiState(coins = mockCoins)

        // When
        composeTestRule.setContent {
            HomeScreen(
                uiState = uiState,
                searchQuery = "",
                onCoinClick = {},
                onFavoritesClick = {},
                onSearchQueryChange = {},
                onClearSearch = {},
                onRefresh = {},
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
            .onNodeWithText("Ethereum")
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun homeScreen_displaysTrendingSection() {
        // Given
        val mockTrendingCoins = listOf(
            createMockCoin("bitcoin", "Bitcoin", "BTC"),
            createMockCoin("ethereum", "Ethereum", "ETH")
        )
        val uiState = HomeUiState(trendingCoins = mockTrendingCoins)

        // When
        composeTestRule.setContent {
            HomeScreen(
                uiState = uiState,
                searchQuery = "",
                onCoinClick = {},
                onFavoritesClick = {},
                onSearchQueryChange = {},
                onClearSearch = {},
                onRefresh = {},
                onRetryClick = {},
                onErrorDismiss = {}
            )
        }

        // Then
        composeTestRule
            .onNodeWithText("\uD83D\uDD25 Trending")
            .assertExists()
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Bitcoin")
            .assertExists()
    }

    @Test
    fun homeScreen_displaysSearchBar() {
        // Given
        val uiState = HomeUiState()

        // When
        composeTestRule.setContent {
            HomeScreen(
                uiState = uiState,
                searchQuery = "",
                onCoinClick = {},
                onFavoritesClick = {},
                onSearchQueryChange = {},
                onClearSearch = {},
                onRefresh = {},
                onRetryClick = {},
                onErrorDismiss = {}
            )
        }

        // Then
        composeTestRule
            .onNodeWithText("Search cryptocurrencies...")
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun homeScreen_searchQueryTriggersCallback() {
        // Given
        val uiState = HomeUiState()
        var queryChanged = false
        var lastQuery = ""

        // When
        composeTestRule.setContent {
            HomeScreen(
                uiState = uiState,
                searchQuery = "",
                onCoinClick = {},
                onFavoritesClick = {},
                onSearchQueryChange = { query ->
                    queryChanged = true
                    lastQuery = query
                },
                onClearSearch = {},
                onRefresh = {},
                onRetryClick = {},
                onErrorDismiss = {}
            )
        }

        composeTestRule
            .onNodeWithText("Search cryptocurrencies...")
            .performTextInput("Bitcoin")

        // Then
        assert(queryChanged)
        assert(lastQuery == "Bitcoin")
    }

    @Test
    fun homeScreen_displaysSearchResults() {
        // Given
        val mockSearchResults = listOf(
            createMockCoin("bitcoin", "Bitcoin", "BTC")
        )
        val uiState = HomeUiState(searchResults = mockSearchResults)

        // When
        composeTestRule.setContent {
            HomeScreen(
                uiState = uiState,
                searchQuery = "Bitcoin",
                onCoinClick = {},
                onFavoritesClick = {},
                onSearchQueryChange = {},
                onClearSearch = {},
                onRefresh = {},
                onRetryClick = {},
                onErrorDismiss = {}
            )
        }

        // Then
        composeTestRule
            .onNodeWithText("Search Results")
            .assertExists()

        composeTestRule
            .onNodeWithText("Bitcoin")
            .assertExists()
    }

    @Test
    fun homeScreen_coinClickTriggersCallback() {
        // Given
        val mockCoins = listOf(createMockCoin("bitcoin", "Bitcoin", "BTC"))
        val uiState = HomeUiState(coins = mockCoins)
        var clicked = false
        var clickedCoinId = ""

        // When
        composeTestRule.setContent {
            HomeScreen(
                uiState = uiState,
                searchQuery = "",
                onCoinClick = { coin ->
                    clicked = true
                    clickedCoinId = coin.id
                },
                onFavoritesClick = {},
                onSearchQueryChange = {},
                onClearSearch = {},
                onRefresh = {},
                onRetryClick = {},
                onErrorDismiss = {}
            )
        }

        composeTestRule
            .onNodeWithText("Bitcoin")
            .performClick()

        // Then
        assert(clicked)
        assert(clickedCoinId == "bitcoin")
    }

    @Test
    fun homeScreen_favoritesButtonTriggersCallback() {
        // Given
        val uiState = HomeUiState()
        var clicked = false

        // When
        composeTestRule.setContent {
            HomeScreen(
                uiState = uiState,
                searchQuery = "",
                onCoinClick = {},
                onFavoritesClick = { clicked = true },
                onSearchQueryChange = {},
                onClearSearch = {},
                onRefresh = {},
                onRetryClick = {},
                onErrorDismiss = {}
            )
        }

        composeTestRule
            .onNodeWithContentDescription("Favorites")
            .performClick()

        // Then
        assert(clicked)
    }

    @Test
    fun homeScreen_displaysEmptyStateWhenNoCoins() {
        // Given
        val uiState = HomeUiState(
            coins = emptyList(),
            isLoading = false
        )

        // When
        composeTestRule.setContent {
            HomeScreen(
                uiState = uiState,
                searchQuery = "",
                onCoinClick = {},
                onFavoritesClick = {},
                onSearchQueryChange = {},
                onClearSearch = {},
                onRefresh = {},
                onRetryClick = {},
                onErrorDismiss = {}
            )
        }

        // Then - Should not crash and display the screen
        composeTestRule
            .onNodeWithText("Search cryptocurrencies...")
            .assertExists()
    }

    @Test
    fun homeScreen_displaysEmptySearchResults() {
        // Given
        val uiState = HomeUiState(
            searchResults = emptyList(),
            isSearching = false
        )

        // When
        composeTestRule.setContent {
            HomeScreen(
                uiState = uiState,
                searchQuery = "NonExistentCoin",
                onCoinClick = {},
                onFavoritesClick = {},
                onSearchQueryChange = {},
                onClearSearch = {},
                onRefresh = {},
                onRetryClick = {},
                onErrorDismiss = {}
            )
        }

        // Then
        composeTestRule
            .onNodeWithText("No results found")
            .assertExists()
    }

    @Test
    fun homeScreen_clearSearchTriggersCallback() {
        // Given
        val uiState = HomeUiState()
        var cleared = false

        // When
        composeTestRule.setContent {
            HomeScreen(
                uiState = uiState,
                searchQuery = "Bitcoin",
                onCoinClick = {},
                onFavoritesClick = {},
                onSearchQueryChange = {},
                onClearSearch = { cleared = true },
                onRefresh = {},
                onRetryClick = {},
                onErrorDismiss = {}
            )
        }

        composeTestRule
            .onNodeWithContentDescription("Clear")
            .performClick()

        // Then
        assert(cleared)
    }

    @Test
    fun homeScreen_displaysMultipleCoinsInList() {
        // Given
        val mockCoins = (1..10).map { index ->
            createMockCoin(
                id = "coin$index",
                name = "Coin $index",
                symbol = "C$index"
            )
        }
        val uiState = HomeUiState(coins = mockCoins)

        // When
        composeTestRule.setContent {
            HomeScreen(
                uiState = uiState,
                searchQuery = "",
                onCoinClick = {},
                onFavoritesClick = {},
                onSearchQueryChange = {},
                onClearSearch = {},
                onRefresh = {},
                onRetryClick = {},
                onErrorDismiss = {}
            )
        }

        // Then - Check first and last coins
        composeTestRule
            .onNodeWithText("Coin 1")
            .assertExists()

        composeTestRule
            .onNodeWithText("Coin 10")
            .assertExists()
    }

    private fun createMockCoin(
        id: String = "bitcoin",
        name: String = "Bitcoin",
        symbol: String = "BTC",
        price: Double = 50000.0
    ) = CoinUiModel(
        id = id,
        name = name,
        symbol = symbol,
        image = "https://example.com/$id.png",
        currentPrice = price.toAmountUiModel(),
        marketCap = (price * 1000000).toLargeNumberUiModel(),
        marketCapRank = 1,
        priceChangePercentage24h = 2.5.toPercentageUiModel()
    )
}

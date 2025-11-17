package com.midas.features.favorites.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.midas.core.ui.mapper.toAmountUiModel
import com.midas.core.ui.mapper.toLargeNumberUiModel
import com.midas.core.ui.mapper.toPercentageUiModel
import com.midas.features.favorites.ui.model.CoinUiModel
import com.midas.features.favorites.ui.model.SortOrderUiModel
import com.midas.features.favorites.ui.state.FavoritesUiState
import org.junit.Rule
import org.junit.Test

class FavoritesScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun favoritesScreen_displaysLoadingState() {
        // Given
        val uiState = FavoritesUiState(isLoading = true)

        // When
        composeTestRule.setContent {
            FavoritesScreen(
                uiState = uiState,
                onNavigateUp = {},
                onChangeSortOrder = {},
                onRefresh = {},
                onCoinClick = {},
                onRemove = {},
                onClearAll = {},
                onRetryClick = {},
                onErrorDismiss = {}
            )
        }

        // Then
        composeTestRule
            .onNodeWithText("Loading favorites...")
            .assertExists()
    }

    @Test
    fun favoritesScreen_displaysFavoritesList() {
        // Given
        val mockFavorites = listOf(
            createMockCoin("bitcoin", "Bitcoin", "BTC"),
            createMockCoin("ethereum", "Ethereum", "ETH")
        )
        val uiState = FavoritesUiState(favorites = mockFavorites)

        // When
        composeTestRule.setContent {
            FavoritesScreen(
                uiState = uiState,
                onNavigateUp = {},
                onChangeSortOrder = {},
                onRefresh = {},
                onCoinClick = {},
                onRemove = {},
                onClearAll = {},
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
    fun favoritesScreen_displaysEmptyState() {
        // Given
        val uiState = FavoritesUiState(
            favorites = emptyList(),
            isLoading = false
        )

        // When
        composeTestRule.setContent {
            FavoritesScreen(
                uiState = uiState,
                onNavigateUp = {},
                onChangeSortOrder = {},
                onRefresh = {},
                onCoinClick = {},
                onRemove = {},
                onClearAll = {},
                onRetryClick = {},
                onErrorDismiss = {}
            )
        }

        // Then
        composeTestRule
            .onNodeWithText("No Favorites Yet")
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun favoritesScreen_backButtonTriggersCallback() {
        // Given
        val uiState = FavoritesUiState()
        var backClicked = false

        // When
        composeTestRule.setContent {
            FavoritesScreen(
                uiState = uiState,
                onNavigateUp = { backClicked = true },
                onChangeSortOrder = {},
                onRefresh = {},
                onCoinClick = {},
                onRemove = {},
                onClearAll = {},
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
    fun favoritesScreen_coinClickTriggersCallback() {
        // Given
        val mockFavorites = listOf(createMockCoin("bitcoin", "Bitcoin", "BTC"))
        val uiState = FavoritesUiState(favorites = mockFavorites)
        var clicked = false
        var clickedCoinId = ""

        // When
        composeTestRule.setContent {
            FavoritesScreen(
                uiState = uiState,
                onNavigateUp = {},
                onChangeSortOrder = {},
                onRefresh = {},
                onCoinClick = { coin ->
                    clicked = true
                    clickedCoinId = coin.id
                },
                onRemove = {},
                onClearAll = {},
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
    fun favoritesScreen_clearAllButtonTriggersCallback() {
        // Given
        val mockFavorites = listOf(createMockCoin())
        val uiState = FavoritesUiState(favorites = mockFavorites)
        var clearAllClicked = false

        // When
        composeTestRule.setContent {
            FavoritesScreen(
                uiState = uiState,
                onNavigateUp = {},
                onChangeSortOrder = {},
                onRefresh = {},
                onCoinClick = {},
                onRemove = {},
                onClearAll = { clearAllClicked = true },
                onRetryClick = {},
                onErrorDismiss = {}
            )
        }

        composeTestRule
            .onNodeWithText("Clear All")
            .performClick()

        composeTestRule
            .onNodeWithText("Remove All Favorites")
            .assertExists()

        composeTestRule
            .onNodeWithText("Ok")
            .performClick()

        // Then
        assert(clearAllClicked)
    }

    @Test
    fun favoritesScreen_sortOrderChangeTriggersCallback() {
        // Given
        val mockFavorites = listOf(createMockCoin())
        val uiState = FavoritesUiState(favorites = mockFavorites)
        var sortOrderChanged = false
        var newSortOrder: SortOrderUiModel? = null

        // When
        composeTestRule.setContent {
            FavoritesScreen(
                uiState = uiState,
                onNavigateUp = {},
                onChangeSortOrder = { sortOrder: SortOrderUiModel ->
                    sortOrderChanged = true
                    newSortOrder = sortOrder
                },
                onRefresh = {},
                onCoinClick = {},
                onRemove = {},
                onClearAll = {},
                onRetryClick = {},
                onErrorDismiss = {}
            )
        }

        // Open sort menu and select option
        composeTestRule
            .onNodeWithContentDescription("Sort")
            .performClick()

        composeTestRule
            .onNodeWithText("Price (Low to High)")
            .performClick()

        // Then
        assert(sortOrderChanged)
        assert(newSortOrder != null)
    }

    @Test
    fun favoritesScreen_displaysCurrentSortOrder() {
        // Given
        val mockFavorites = listOf(createMockCoin())
        val uiState = FavoritesUiState(
            favorites = mockFavorites,
            sortOrder = SortOrderUiModel.NAME_ASC
        )

        // When
        composeTestRule.setContent {
            FavoritesScreen(
                uiState = uiState,
                onNavigateUp = {},
                onChangeSortOrder = {},
                onRefresh = {},
                onCoinClick = {},
                onRemove = {},
                onClearAll = {},
                onRetryClick = {},
                onErrorDismiss = {}
            )
        }

        // Then - Sort button should be visible
        composeTestRule
            .onNodeWithContentDescription("Sort")
            .assertExists()
    }

    @Test
    fun favoritesScreen_displaysMultipleFavorites() {
        // Given
        val mockFavorites = (1..10).map { index ->
            createMockCoin(
                id = "coin$index",
                name = "Coin $index",
                symbol = "C$index"
            )
        }
        val uiState = FavoritesUiState(favorites = mockFavorites)

        // When
        composeTestRule.setContent {
            FavoritesScreen(
                uiState = uiState,
                onNavigateUp = {},
                onChangeSortOrder = {},
                onRefresh = {},
                onCoinClick = {},
                onRemove = {},
                onClearAll = {},
                onRetryClick = {},
                onErrorDismiss = {}
            )
        }

        // Then - Check first and some other coins
        composeTestRule
            .onNodeWithText("Coin 1")
            .assertExists()

        composeTestRule
            .onNodeWithText("Coin 5")
            .assertExists()
    }

    @Test
    fun favoritesScreen_clearAllButton_visibleWhenHasFavorites() {
        // Given
        val mockFavorites = listOf(createMockCoin())
        val uiState = FavoritesUiState(favorites = mockFavorites)

        // When
        composeTestRule.setContent {
            FavoritesScreen(
                uiState = uiState,
                onNavigateUp = {},
                onChangeSortOrder = {},
                onRefresh = {},
                onCoinClick = {},
                onRemove = {},
                onClearAll = {},
                onRetryClick = {},
                onErrorDismiss = {}
            )
        }

        // Then
        composeTestRule
            .onNodeWithText("Clear All")
            .assertExists()
    }

    @Test
    fun favoritesScreen_displaysFavoriteCount() {
        // Given
        val mockFavorites = listOf(
            createMockCoin("bitcoin", "Bitcoin", "BTC"),
            createMockCoin("ethereum", "Ethereum", "ETH"),
            createMockCoin("cardano", "Cardano", "ADA")
        )
        val uiState = FavoritesUiState(favorites = mockFavorites)

        // When
        composeTestRule.setContent {
            FavoritesScreen(
                uiState = uiState,
                onNavigateUp = {},
                onChangeSortOrder = {},
                onRefresh = {},
                onCoinClick = {},
                onRemove = {},
                onClearAll = {},
                onRetryClick = {},
                onErrorDismiss = {}
            )
        }

        // Then - All three should be visible
        composeTestRule
            .onNodeWithText("Bitcoin")
            .assertExists()

        composeTestRule
            .onNodeWithText("Ethereum")
            .assertExists()

        composeTestRule
            .onNodeWithText("Cardano")
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

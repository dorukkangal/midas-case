package com.midas.features.home.ui.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.midas.core.ui.mapper.toAmountUiModel
import com.midas.core.ui.mapper.toLargeNumberUiModel
import com.midas.core.ui.mapper.toPercentageUiModel
import com.midas.features.home.ui.model.CoinUiModel
import org.junit.Rule
import org.junit.Test

class CoinListItemTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun coinListItem_displaysAllInformation() {
        // Given
        val coin = createMockCoin()

        // When
        composeTestRule.setContent {
            CoinListItem(
                coin = coin,
                onClick = {}
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

        // Price is displayed using AmountUiModel's displayAmount
        composeTestRule
            .onNodeWithText("$50,000.00")
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun coinListItem_showsPositivePriceChange() {
        // Given
        val coin = createMockCoin(priceChangePercentage24h = 5.5)

        // When
        composeTestRule.setContent {
            CoinListItem(
                coin = coin,
                onClick = {}
            )
        }

        // Then - Format is "+5.50%"
        composeTestRule
            .onNodeWithText("+5.50%")
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun coinListItem_showsNegativePriceChange() {
        // Given
        val coin = createMockCoin(priceChangePercentage24h = -3.2)

        // When
        composeTestRule.setContent {
            CoinListItem(
                coin = coin,
                onClick = {}
            )
        }

        // Then - Format is "-3.20%"
        composeTestRule
            .onNodeWithText("-3.20%")
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun coinListItem_showsZeroPriceChange() {
        // Given
        val coin = createMockCoin(priceChangePercentage24h = 0.0)

        // When
        composeTestRule.setContent {
            CoinListItem(
                coin = coin,
                onClick = {}
            )
        }

        // Then - Zero is treated as positive
        composeTestRule
            .onNodeWithText("+0.00%")
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun coinListItem_clickTriggersCallback() {
        // Given
        var clicked = false
        val coin = createMockCoin()

        // When
        composeTestRule.setContent {
            CoinListItem(
                coin = coin,
                onClick = { clicked = true }
            )
        }

        composeTestRule
            .onNodeWithText("Bitcoin")
            .performClick()

        // Then
        assert(clicked)
    }

    @Test
    fun coinListItem_displaysMarketCapRank() {
        // Given
        val coin = createMockCoin(marketCapRank = 1)

        // When
        composeTestRule.setContent {
            CoinListItem(
                coin = coin,
                onClick = {}
            )
        }

        // Then
        composeTestRule
            .onNodeWithText("#1")
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun coinListItem_displaysMarketCapRank_doubleDigit() {
        // Given
        val coin = createMockCoin(marketCapRank = 42)

        // When
        composeTestRule.setContent {
            CoinListItem(
                coin = coin,
                onClick = {}
            )
        }

        // Then
        composeTestRule
            .onNodeWithText("#42")
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun coinListItem_handlesNullPriceChange() {
        // Given
        val coin = createMockCoin(priceChangePercentage24h = null)

        // When
        composeTestRule.setContent {
            CoinListItem(
                coin = coin,
                onClick = {}
            )
        }

        // Then - Should display coin without price change
        composeTestRule
            .onNodeWithText("Bitcoin")
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun coinListItem_handlesNullMarketCapRank() {
        // Given
        val coin = createMockCoin(marketCapRank = null)

        // When
        composeTestRule.setContent {
            CoinListItem(
                coin = coin,
                onClick = {}
            )
        }

        // Then - Should display coin without rank
        composeTestRule
            .onNodeWithText("Bitcoin")
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun coinListItem_displaysLargePrice() {
        // Given
        val coin = createMockCoin(price = 123456.78)

        // When
        composeTestRule.setContent {
            CoinListItem(
                coin = coin,
                onClick = {}
            )
        }

        // Then
        composeTestRule
            .onNodeWithText("$123,456.78")
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun coinListItem_displaysSmallPrice() {
        // Given
        val coin = createMockCoin(price = 0.0123)

        // When
        composeTestRule.setContent {
            CoinListItem(
                coin = coin,
                onClick = {}
            )
        }

        // Then
        composeTestRule
            .onNodeWithText("$0.01")
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun coinListItem_multipleClicks_triggerMultipleCallbacks() {
        // Given
        var clickCount = 0
        val coin = createMockCoin()

        // When
        composeTestRule.setContent {
            CoinListItem(
                coin = coin,
                onClick = { clickCount++ }
            )
        }

        val coinNode = composeTestRule.onNodeWithText("Bitcoin")
        coinNode.performClick()
        coinNode.performClick()
        coinNode.performClick()

        // Then
        assert(clickCount == 3)
    }

    private fun createMockCoin(
        id: String = "bitcoin",
        name: String = "Bitcoin",
        symbol: String = "BTC",
        price: Double = 50000.0,
        priceChangePercentage24h: Double? = 2.5,
        marketCapRank: Int? = 1
    ) = CoinUiModel(
        id = id,
        name = name,
        symbol = symbol,
        image = "https://example.com/$id.png",
        currentPrice = price.toAmountUiModel(),
        marketCap = (price * 1000000).toLargeNumberUiModel(),
        marketCapRank = marketCapRank,
        priceChangePercentage24h = priceChangePercentage24h?.toPercentageUiModel(),
    )
}

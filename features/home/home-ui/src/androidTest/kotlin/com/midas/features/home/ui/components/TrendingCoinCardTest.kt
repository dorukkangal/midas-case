package com.midas.features.home.ui.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import com.midas.core.ui.mapper.toAmountUiModel
import com.midas.core.ui.mapper.toLargeNumberUiModel
import com.midas.core.ui.mapper.toPercentageUiModel
import com.midas.features.home.ui.model.CoinUiModel
import org.junit.Rule
import org.junit.Test

class TrendingCoinCardTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun trendingCoinCard_displaysName() {
        // Given
        val coin = createMockCoin(name = "Bitcoin")

        // When
        composeTestRule.setContent {
            TrendingCoinCard(
                coin = coin,
                onClick = {}
            )
        }

        // Then
        composeTestRule
            .onNodeWithText("Bitcoin")
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun trendingCoinCard_displaysSymbol() {
        // Given
        val coin = createMockCoin(symbol = "BTC")

        // When
        composeTestRule.setContent {
            TrendingCoinCard(
                coin = coin,
                onClick = {}
            )
        }

        // Then
        composeTestRule
            .onNodeWithText("BTC")
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun trendingCoinCard_displaysPrice() {
        // Given
        val coin = createMockCoin(price = 50000.0)

        // When
        composeTestRule.setContent {
            TrendingCoinCard(
                coin = coin,
                onClick = {}
            )
        }

        // Then
        composeTestRule
            .onNodeWithText("$50,000.00")
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun trendingCoinCard_displaysPositiveChange() {
        // Given
        val coin = createMockCoin(priceChangePercentage24h = 10.5)

        // When
        composeTestRule.setContent {
            TrendingCoinCard(
                coin = coin,
                onClick = {}
            )
        }

        // Then
        composeTestRule
            .onNodeWithText("+10.50%")
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun trendingCoinCard_displaysNegativeChange() {
        // Given
        val coin = createMockCoin(priceChangePercentage24h = -5.3)

        // When
        composeTestRule.setContent {
            TrendingCoinCard(
                coin = coin,
                onClick = {}
            )
        }

        // Then - Format is "-5.30%"
        composeTestRule
            .onNodeWithText("-5.30%")
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun trendingCoinCard_displaysZeroChange() {
        // Given
        val coin = createMockCoin(priceChangePercentage24h = 0.0)

        // When
        composeTestRule.setContent {
            TrendingCoinCard(
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
    fun trendingCoinCard_clickTriggersCallback() {
        // Given
        var clicked = false
        val coin = createMockCoin()

        // When
        composeTestRule.setContent {
            TrendingCoinCard(
                coin = coin,
                onClick = { clicked = true }
            )
        }

        composeTestRule
            .onRoot()
            .performClick()

        // Then
        assert(clicked)
    }

    @Test
    fun trendingCoinCard_multipleClicks_triggerMultipleCallbacks() {
        // Given
        var clickCount = 0
        val coin = createMockCoin()

        // When
        composeTestRule.setContent {
            TrendingCoinCard(
                coin = coin,
                onClick = { clickCount++ }
            )
        }

        val cardNode = composeTestRule.onRoot()
        cardNode.performClick()
        cardNode.performClick()

        // Then
        assert(clickCount == 2)
    }

    @Test
    fun trendingCoinCard_displaysLargePriceChange() {
        // Given
        val coin = createMockCoin(priceChangePercentage24h = 99.99)

        // When
        composeTestRule.setContent {
            TrendingCoinCard(
                coin = coin,
                onClick = {}
            )
        }

        // Then
        composeTestRule
            .onNodeWithText("+99.99%")
            .assertExists()
    }

    @Test
    fun trendingCoinCard_displaysSmallPrice() {
        // Given
        val coin = createMockCoin(price = 0.01)

        // When
        composeTestRule.setContent {
            TrendingCoinCard(
                coin = coin,
                onClick = {}
            )
        }

        // Then
        composeTestRule
            .onNodeWithText("$0.01")
            .assertExists()
    }

    @Test
    fun trendingCoinCard_displaysVeryLargePrice() {
        // Given
        val coin = createMockCoin(price = 999999.99)

        // When
        composeTestRule.setContent {
            TrendingCoinCard(
                coin = coin,
                onClick = {}
            )
        }

        // Then
        composeTestRule
            .onNodeWithText("$999,999.99")
            .assertExists()
    }

    @Test
    fun trendingCoinCard_handlesNullPriceChange() {
        // Given
        val coin = createMockCoin(priceChangePercentage24h = null)

        // When
        composeTestRule.setContent {
            TrendingCoinCard(
                coin = coin,
                onClick = {}
            )
        }

        // Then - Should still display name and price
        composeTestRule
            .onNodeWithText("Bitcoin")
            .assertExists()

        composeTestRule
            .onNodeWithText("$50,000.00")
            .assertExists()
    }

    @Test
    fun trendingCoinCard_displaysLongCoinName() {
        // Given
        val coin = createMockCoin(name = "Very Long Cryptocurrency Name")

        // When
        composeTestRule.setContent {
            TrendingCoinCard(
                coin = coin,
                onClick = {}
            )
        }

        // Then
        composeTestRule
            .onNodeWithText("Very Long Cryptocurrency Name")
            .assertExists()
    }

    @Test
    fun trendingCoinCard_displaysUppercaseSymbol() {
        // Given
        val coin = createMockCoin(symbol = "ETH")

        // When
        composeTestRule.setContent {
            TrendingCoinCard(
                coin = coin,
                onClick = {}
            )
        }

        // Then
        composeTestRule
            .onNodeWithText("ETH")
            .assertExists()
    }

    @Test
    fun trendingCoinCard_displaysAllElements() {
        // Given
        val coin = createMockCoin(
            name = "Ethereum",
            symbol = "ETH",
            price = 3000.0,
            priceChangePercentage24h = 7.5
        )

        // When
        composeTestRule.setContent {
            TrendingCoinCard(
                coin = coin,
                onClick = {}
            )
        }

        // Then - All elements should be present
        composeTestRule
            .onNodeWithText("Ethereum")
            .assertExists()

        composeTestRule
            .onNodeWithText("ETH")
            .assertExists()

        composeTestRule
            .onNodeWithText("$3,000.00")
            .assertExists()

        composeTestRule
            .onNodeWithText("+7.50%")
            .assertExists()
    }

    private fun createMockCoin(
        id: String = "bitcoin",
        name: String = "Bitcoin",
        symbol: String = "BTC",
        price: Double = 50000.0,
        priceChangePercentage24h: Double? = 10.0
    ) = CoinUiModel(
        id = id,
        name = name,
        symbol = symbol,
        image = "https://example.com/$id.png",
        currentPrice = price.toAmountUiModel(),
        marketCap = (price * 1000000).toLargeNumberUiModel(),
        marketCapRank = 1,
        priceChangePercentage24h = priceChangePercentage24h?.toPercentageUiModel(),
    )
}

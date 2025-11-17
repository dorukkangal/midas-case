package com.midas.features.detail.data.remote.mapper

import com.google.common.truth.Truth.assertThat
import com.midas.features.detail.data.remote.model.CoinDetailResponse
import com.midas.features.detail.data.remote.model.CoinImageResponse
import com.midas.features.detail.data.remote.model.DescriptionResponse
import com.midas.features.detail.data.remote.model.MarketDataResponse
import org.junit.Test

class CoinDetailMapperTest {

    // ==================== toCoinDetail Tests ====================

    @Test
    fun `toCoinDetail maps all fields correctly`() {
        // Given
        val response = createMockCoinDetailResponse()

        // When
        val coinDetail = response.toCoinDetail()

        // Then
        assertThat(coinDetail.id).isEqualTo("bitcoin")
        assertThat(coinDetail.name).isEqualTo("Bitcoin")
        assertThat(coinDetail.symbol).isEqualTo("BTC")
        assertThat(coinDetail.description).isEqualTo("Bitcoin is a cryptocurrency")
        assertThat(coinDetail.image).isEqualTo("https://example.com/large.png")
        assertThat(coinDetail.marketCapRank).isEqualTo(1)
        assertThat(coinDetail.categories).containsExactly("Cryptocurrency", "DeFi")
        assertThat(coinDetail.genesisDate).isEqualTo("2009-01-03")
    }

    @Test
    fun `toCoinDetail handles null marketCapRank`() {
        // Given
        val response = createMockCoinDetailResponse(marketCapRank = null)

        // When
        val coinDetail = response.toCoinDetail()

        // Then
        assertThat(coinDetail.marketCapRank).isNull()
    }

    @Test
    fun `toCoinDetail handles null genesisDate`() {
        // Given
        val response = createMockCoinDetailResponse(genesisDate = null)

        // When
        val coinDetail = response.toCoinDetail()

        // Then
        assertThat(coinDetail.genesisDate).isNull()
    }

    @Test
    fun `toCoinDetail handles empty categories`() {
        // Given
        val response = createMockCoinDetailResponse(categories = emptyList())

        // When
        val coinDetail = response.toCoinDetail()

        // Then
        assertThat(coinDetail.categories).isEmpty()
    }

    // ==================== toDescription Tests ====================

    @Test
    fun `toDescription removes HTML tags`() {
        // Given
        val descriptionResponse = DescriptionResponse(
            en = "<p>Bitcoin is a <strong>cryptocurrency</strong></p>"
        )

        // When
        val description = descriptionResponse.toDescription()

        // Then
        assertThat(description).isEqualTo("Bitcoin is a cryptocurrency")
    }

    @Test
    fun `toDescription removes multiple spaces`() {
        // Given
        val descriptionResponse = DescriptionResponse(
            en = "Bitcoin    is   a    cryptocurrency"
        )

        // When
        val description = descriptionResponse.toDescription()

        // Then
        assertThat(description).isEqualTo("Bitcoin is a cryptocurrency")
    }

    @Test
    fun `toDescription trims whitespace`() {
        // Given
        val descriptionResponse = DescriptionResponse(
            en = "  Bitcoin is a cryptocurrency  "
        )

        // When
        val description = descriptionResponse.toDescription()

        // Then
        assertThat(description).isEqualTo("Bitcoin is a cryptocurrency")
    }

    @Test
    fun `toDescription handles complex HTML`() {
        // Given
        val descriptionResponse = DescriptionResponse(
            en = """
                <div class="container">
                    <h1>Bitcoin</h1>
                    <p>Bitcoin is a <a href="test">cryptocurrency</a> invented in <strong>2008</strong>.</p>
                    <ul><li>Item 1</li><li>Item 2</li></ul>
                </div>
            """.trimIndent()
        )

        // When
        val description = descriptionResponse.toDescription()

        // Then
        assertThat(description).contains("Bitcoin")
        assertThat(description).contains("cryptocurrency")
        assertThat(description).contains("2008")
        assertThat(description).doesNotContain("<")
        assertThat(description).doesNotContain(">")
    }

    @Test
    fun `toDescription returns null for blank string`() {
        // Given
        val descriptionResponse = DescriptionResponse(en = "   ")

        // When
        val description = descriptionResponse.toDescription()

        // Then
        assertThat(description).isNull()
    }

    @Test
    fun `toDescription returns null for empty string`() {
        // Given
        val descriptionResponse = DescriptionResponse(en = "")

        // When
        val description = descriptionResponse.toDescription()

        // Then
        assertThat(description).isNull()
    }

    @Test
    fun `toDescription returns null for string with only HTML tags`() {
        // Given
        val descriptionResponse = DescriptionResponse(en = "<p></p><div></div>")

        // When
        val description = descriptionResponse.toDescription()

        // Then
        assertThat(description).isNull()
    }

    // ==================== toMarketData Tests ====================

    @Test
    fun `toMarketData maps all USD fields correctly`() {
        // Given
        val marketDataResponse = createMockMarketDataResponse()

        // When
        val marketData = marketDataResponse.toMarketData()

        // Then - Maps use uppercase currency codes
        assertThat(marketData.currentPrice).isEqualTo(50000.0)
        assertThat(marketData.marketCap).isEqualTo(1000000000000L)
        assertThat(marketData.totalVolume).isEqualTo(50000000000L)
        assertThat(marketData.high24h).isEqualTo(51000.0)
        assertThat(marketData.low24h).isEqualTo(49000.0)
        assertThat(marketData.priceChangePercentage24h).isEqualTo(2.5)
        assertThat(marketData.priceChangePercentage7d).isEqualTo(5.0)
        assertThat(marketData.priceChangePercentage30d).isEqualTo(10.0)
        assertThat(marketData.circulatingSupply).isEqualTo(19000000.0)
        assertThat(marketData.totalSupply).isEqualTo(21000000.0)
        assertThat(marketData.maxSupply).isEqualTo(21000000.0)
    }

    @Test
    fun `toMarketData handles null price change percentages`() {
        // Given
        val marketDataResponse = createMockMarketDataResponse(
            priceChangePercentage24h = null,
            priceChangePercentage7d = null,
            priceChangePercentage30d = null
        )

        // When
        val marketData = marketDataResponse.toMarketData()

        // Then
        assertThat(marketData.priceChangePercentage24h).isNull()
        assertThat(marketData.priceChangePercentage7d).isNull()
        assertThat(marketData.priceChangePercentage30d).isNull()
    }

    @Test
    fun `toMarketData handles null supply values`() {
        // Given
        val marketDataResponse = createMockMarketDataResponse(
            circulatingSupply = null,
            totalSupply = null,
            maxSupply = null
        )

        // When
        val marketData = marketDataResponse.toMarketData()

        // Then
        assertThat(marketData.circulatingSupply).isNull()
        assertThat(marketData.totalSupply).isNull()
        assertThat(marketData.maxSupply).isNull()
    }

    @Test
    fun `toMarketData handles negative price changes`() {
        // Given
        val marketDataResponse = createMockMarketDataResponse(
            priceChangePercentage24h = -3.5,
            priceChangePercentage7d = -7.2,
            priceChangePercentage30d = -15.8
        )

        // When
        val marketData = marketDataResponse.toMarketData()

        // Then
        assertThat(marketData.priceChangePercentage24h).isEqualTo(-3.5)
        assertThat(marketData.priceChangePercentage7d).isEqualTo(-7.2)
        assertThat(marketData.priceChangePercentage30d).isEqualTo(-15.8)
    }

    // ==================== Helper Methods ====================

    private fun createMockCoinDetailResponse(
        id: String = "bitcoin",
        name: String = "Bitcoin",
        symbol: String = "BTC",
        description: String = "Bitcoin is a cryptocurrency",
        marketCapRank: Int? = 1,
        categories: List<String> = listOf("Cryptocurrency", "DeFi"),
        genesisDate: String? = "2009-01-03"
    ) = CoinDetailResponse(
        id = id,
        name = name,
        symbol = symbol,
        description = DescriptionResponse(en = description),
        image = CoinImageResponse(
            thumb = "https://example.com/thumb.png",
            small = "https://example.com/small.png",
            large = "https://example.com/large.png"
        ),
        marketCapRank = marketCapRank,
        marketData = createMockMarketDataResponse(),
        categories = categories,
        genesisDate = genesisDate
    )

    private fun createMockMarketDataResponse(
        priceChangePercentage24h: Double? = 2.5,
        priceChangePercentage7d: Double? = 5.0,
        priceChangePercentage30d: Double? = 10.0,
        circulatingSupply: Double? = 19000000.0,
        totalSupply: Double? = 21000000.0,
        maxSupply: Double? = 21000000.0
    ) = MarketDataResponse(
        currentPrice = mapOf("USD" to 50000.0), // Currency.getInstance returns uppercase code
        marketCap = mapOf("USD" to 1000000000000L),
        totalVolume = mapOf("USD" to 50000000000L),
        high24h = mapOf("USD" to 51000.0),
        low24h = mapOf("USD" to 49000.0),
        priceChangePercentage24h = priceChangePercentage24h,
        priceChangePercentage7d = priceChangePercentage7d,
        priceChangePercentage30d = priceChangePercentage30d,
        circulatingSupply = circulatingSupply,
        totalSupply = totalSupply,
        maxSupply = maxSupply
    )
}

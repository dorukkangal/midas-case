package com.midas.features.home.data.remote.mapper

import com.google.common.truth.Truth.assertThat
import com.midas.features.home.data.remote.model.CoinResponse
import org.junit.Test

class CoinMapperTest {

    @Test
    fun `toCoin maps all fields correctly`() {
        // Given
        val coinResponse = CoinResponse(
            id = "bitcoin",
            name = "Bitcoin",
            symbol = "btc",
            image = "https://example.com/bitcoin.png",
            currentPrice = 50000.0,
            marketCap = 1000000000000L,
            marketCapRank = 1,
            priceChangePercentage24h = 2.5
        )

        // When
        val coin = coinResponse.toCoin()

        // Then
        assertThat(coin.id).isEqualTo("bitcoin")
        assertThat(coin.name).isEqualTo("Bitcoin")
        assertThat(coin.symbol).isEqualTo("btc")
        assertThat(coin.image).isEqualTo("https://example.com/bitcoin.png")
        assertThat(coin.currentPrice).isEqualTo(50000.0)
        assertThat(coin.marketCap).isEqualTo(1000000000000L)
        assertThat(coin.marketCapRank).isEqualTo(1)
        assertThat(coin.priceChangePercentage24h).isEqualTo(2.5)
    }

    @Test
    fun `toCoin handles null currentPrice`() {
        // Given
        val coinResponse = CoinResponse(
            id = "bitcoin",
            name = "Bitcoin",
            symbol = "btc",
            image = "https://example.com/bitcoin.png",
            currentPrice = null,
            marketCap = 1000000000000L,
            marketCapRank = 1,
            priceChangePercentage24h = 2.5
        )

        // When
        val coin = coinResponse.toCoin()

        // Then
        assertThat(coin.currentPrice).isNull()
    }

    @Test
    fun `toCoin handles null marketCap`() {
        // Given
        val coinResponse = CoinResponse(
            id = "bitcoin",
            name = "Bitcoin",
            symbol = "btc",
            image = "https://example.com/bitcoin.png",
            currentPrice = 50000.0,
            marketCap = null,
            marketCapRank = 1,
            priceChangePercentage24h = 2.5
        )

        // When
        val coin = coinResponse.toCoin()

        // Then
        assertThat(coin.marketCap).isNull()
    }

    @Test
    fun `toCoin handles null marketCapRank`() {
        // Given
        val coinResponse = CoinResponse(
            id = "bitcoin",
            name = "Bitcoin",
            symbol = "btc",
            image = "https://example.com/bitcoin.png",
            currentPrice = 50000.0,
            marketCap = 1000000000000L,
            marketCapRank = null,
            priceChangePercentage24h = 2.5
        )

        // When
        val coin = coinResponse.toCoin()

        // Then
        assertThat(coin.marketCapRank).isNull()
    }

    @Test
    fun `toCoin handles null priceChangePercentage24h`() {
        // Given
        val coinResponse = CoinResponse(
            id = "bitcoin",
            name = "Bitcoin",
            symbol = "btc",
            image = "https://example.com/bitcoin.png",
            currentPrice = 50000.0,
            marketCap = 1000000000000L,
            marketCapRank = 1,
            priceChangePercentage24h = null
        )

        // When
        val coin = coinResponse.toCoin()

        // Then
        assertThat(coin.priceChangePercentage24h).isNull()
    }

    @Test
    fun `toCoin handles all null optional fields`() {
        // Given
        val coinResponse = CoinResponse(
            id = "bitcoin",
            name = "Bitcoin",
            symbol = "btc",
            image = "https://example.com/bitcoin.png",
            currentPrice = null,
            marketCap = null,
            marketCapRank = null,
            priceChangePercentage24h = null
        )

        // When
        val coin = coinResponse.toCoin()

        // Then
        assertThat(coin.id).isEqualTo("bitcoin")
        assertThat(coin.name).isEqualTo("Bitcoin")
        assertThat(coin.symbol).isEqualTo("btc")
        assertThat(coin.image).isEqualTo("https://example.com/bitcoin.png")
        assertThat(coin.currentPrice).isNull()
        assertThat(coin.marketCap).isNull()
        assertThat(coin.marketCapRank).isNull()
        assertThat(coin.priceChangePercentage24h).isNull()
    }

    @Test
    fun `toCoin handles negative price change`() {
        // Given
        val coinResponse = CoinResponse(
            id = "bitcoin",
            name = "Bitcoin",
            symbol = "btc",
            image = "https://example.com/bitcoin.png",
            currentPrice = 50000.0,
            marketCap = 1000000000000L,
            marketCapRank = 1,
            priceChangePercentage24h = -3.2
        )

        // When
        val coin = coinResponse.toCoin()

        // Then
        assertThat(coin.priceChangePercentage24h).isEqualTo(-3.2)
    }

    @Test
    fun `toCoin handles very large market cap`() {
        // Given
        val coinResponse = CoinResponse(
            id = "bitcoin",
            name = "Bitcoin",
            symbol = "btc",
            image = "https://example.com/bitcoin.png",
            currentPrice = 50000.0,
            marketCap = 9223372036854775807L, // Long.MAX_VALUE
            marketCapRank = 1,
            priceChangePercentage24h = 2.5
        )

        // When
        val coin = coinResponse.toCoin()

        // Then
        assertThat(coin.marketCap).isEqualTo(9223372036854775807L)
    }
}

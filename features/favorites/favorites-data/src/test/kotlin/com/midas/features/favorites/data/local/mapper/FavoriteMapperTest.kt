package com.midas.features.favorites.data.local.mapper

import com.google.common.truth.Truth.assertThat
import com.midas.features.favorites.data.local.model.FavoriteCoinEntity
import com.midas.features.home.domain.model.Coin
import org.junit.Test

class FavoriteMapperTest {

    // ==================== toFavoriteCoinEntity Tests ====================

    @Test
    fun `toFavoriteCoinEntity maps all fields correctly`() {
        // Given
        val coin = Coin(
            id = "bitcoin",
            name = "Bitcoin",
            symbol = "BTC",
            image = "https://example.com/bitcoin.png",
            currentPrice = 50000.0,
            marketCap = 1000000000000L,
            marketCapRank = 1,
            priceChangePercentage24h = 2.5
        )

        // When
        val entity = coin.toFavoriteCoinEntity()

        // Then
        assertThat(entity.coinId).isEqualTo("bitcoin")
        assertThat(entity.name).isEqualTo("Bitcoin")
        assertThat(entity.symbol).isEqualTo("BTC")
        assertThat(entity.image).isEqualTo("https://example.com/bitcoin.png")
        assertThat(entity.addedAt).isGreaterThan(0L)
    }

    @Test
    fun `toFavoriteCoinEntity generates addedAt timestamp`() {
        // Given
        val coin = createMockCoin()
        val beforeTime = System.currentTimeMillis()

        // When
        val entity = coin.toFavoriteCoinEntity()
        val afterTime = System.currentTimeMillis()

        // Then
        assertThat(entity.addedAt).isAtLeast(beforeTime)
        assertThat(entity.addedAt).isAtMost(afterTime)
    }

    @Test
    fun `toFavoriteCoinEntity ignores price data`() {
        // Given
        val coin = Coin(
            id = "bitcoin",
            name = "Bitcoin",
            symbol = "BTC",
            image = "https://example.com/bitcoin.png",
            currentPrice = 50000.0,  // Should not be stored
            marketCap = 1000000000000L,  // Should not be stored
            marketCapRank = 1,  // Should not be stored
            priceChangePercentage24h = 2.5  // Should not be stored
        )

        // When
        val entity = coin.toFavoriteCoinEntity()

        // Then - Only basic fields are mapped
        assertThat(entity.coinId).isEqualTo("bitcoin")
        assertThat(entity.name).isEqualTo("Bitcoin")
        assertThat(entity.symbol).isEqualTo("BTC")
        assertThat(entity.image).isEqualTo("https://example.com/bitcoin.png")
    }

    @Test
    fun `toFavoriteCoinEntity handles uppercase symbol`() {
        // Given
        val coin = createMockCoin(symbol = "BTC")

        // When
        val entity = coin.toFavoriteCoinEntity()

        // Then
        assertThat(entity.symbol).isEqualTo("BTC")
    }

    @Test
    fun `toFavoriteCoinEntity handles lowercase symbol`() {
        // Given
        val coin = createMockCoin(symbol = "btc")

        // When
        val entity = coin.toFavoriteCoinEntity()

        // Then
        assertThat(entity.symbol).isEqualTo("btc")
    }

    // ==================== toCoin Tests ====================

    @Test
    fun `toCoin maps all fields correctly`() {
        // Given
        val entity = FavoriteCoinEntity(
            coinId = "bitcoin",
            name = "Bitcoin",
            symbol = "BTC",
            image = "https://example.com/bitcoin.png",
            addedAt = System.currentTimeMillis()
        )

        // When
        val coin = entity.toCoin()

        // Then
        assertThat(coin.id).isEqualTo("bitcoin")
        assertThat(coin.name).isEqualTo("Bitcoin")
        assertThat(coin.symbol).isEqualTo("BTC")
        assertThat(coin.image).isEqualTo("https://example.com/bitcoin.png")
    }

    @Test
    fun `toCoin sets price fields to null`() {
        // Given
        val entity = FavoriteCoinEntity(
            coinId = "bitcoin",
            name = "Bitcoin",
            symbol = "BTC",
            image = "https://example.com/bitcoin.png",
            addedAt = System.currentTimeMillis()
        )

        // When
        val coin = entity.toCoin()

        // Then - Price data should be null since it's not stored in entity
        assertThat(coin.currentPrice).isNull()
        assertThat(coin.marketCap).isNull()
        assertThat(coin.marketCapRank).isNull()
        assertThat(coin.priceChangePercentage24h).isNull()
    }

    @Test
    fun `toCoin preserves special characters in name`() {
        // Given
        val entity = FavoriteCoinEntity(
            coinId = "bitcoin",
            name = "Bitcoin & Cryptocurrency",
            symbol = "BTC",
            image = "https://example.com/bitcoin.png",
            addedAt = System.currentTimeMillis()
        )

        // When
        val coin = entity.toCoin()

        // Then
        assertThat(coin.name).isEqualTo("Bitcoin & Cryptocurrency")
    }

    // ==================== Round-trip Tests ====================

    @Test
    fun `round-trip conversion preserves basic fields`() {
        // Given
        val originalCoin = Coin(
            id = "bitcoin",
            name = "Bitcoin",
            symbol = "BTC",
            image = "https://example.com/bitcoin.png",
            currentPrice = 50000.0,
            marketCap = 1000000000000L,
            marketCapRank = 1,
            priceChangePercentage24h = 2.5
        )

        // When
        val entity = originalCoin.toFavoriteCoinEntity()
        val resultCoin = entity.toCoin()

        // Then - Basic fields preserved
        assertThat(resultCoin.id).isEqualTo(originalCoin.id)
        assertThat(resultCoin.name).isEqualTo(originalCoin.name)
        assertThat(resultCoin.symbol).isEqualTo(originalCoin.symbol)
        assertThat(resultCoin.image).isEqualTo(originalCoin.image)

        // Price data is lost (expected behavior)
        assertThat(resultCoin.currentPrice).isNull()
        assertThat(resultCoin.marketCap).isNull()
    }

    @Test
    fun `multiple conversions generate different timestamps`() {
        // Given
        val coin = createMockCoin()

        // When
        val entity1 = coin.toFavoriteCoinEntity()
        Thread.sleep(10) // Small delay
        val entity2 = coin.toFavoriteCoinEntity()

        // Then
        assertThat(entity2.addedAt).isGreaterThan(entity1.addedAt)
    }

    // ==================== Helper Methods ====================

    private fun createMockCoin(
        id: String = "bitcoin",
        name: String = "Bitcoin",
        symbol: String = "BTC",
        image: String = "https://example.com/bitcoin.png"
    ) = Coin(
        id = id,
        name = name,
        symbol = symbol,
        image = image,
        currentPrice = 50000.0,
        marketCap = 1000000000000L,
        marketCapRank = 1,
        priceChangePercentage24h = 2.5
    )
}

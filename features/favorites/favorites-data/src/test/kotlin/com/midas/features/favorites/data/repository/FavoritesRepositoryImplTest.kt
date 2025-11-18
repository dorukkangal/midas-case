package com.midas.features.favorites.data.repository

import com.google.common.truth.Truth.assertThat
import com.midas.features.favorites.data.local.dao.FavoriteCoinDao
import com.midas.features.favorites.data.local.model.FavoriteCoinEntity
import com.midas.features.home.domain.model.Coin
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class FavoritesRepositoryImplTest {

    private lateinit var favoriteCoinDao: FavoriteCoinDao
    private lateinit var repository: FavoritesRepositoryImpl

    @Before
    fun setup() {
        favoriteCoinDao = mockk(relaxed = true)
        repository = FavoritesRepositoryImpl(favoriteCoinDao)
    }

    // ==================== getFavoriteCoins Tests ====================

    @Test
    fun `getFavoriteCoins returns success with mapped coins`() = runTest {
        // Given
        val entities = listOf(
            createMockEntity("bitcoin", "Bitcoin"),
            createMockEntity("ethereum", "Ethereum")
        )
        coEvery { favoriteCoinDao.getAllFavoriteCoins() } returns entities

        // When
        val result = repository.getFavoriteCoins()

        // Then
        assertThat(result.isSuccess).isTrue()
        val coins = result.getOrNull()
        assertThat(coins).hasSize(2)
        assertThat(coins?.get(0)?.id).isEqualTo("bitcoin")
        assertThat(coins?.get(1)?.id).isEqualTo("ethereum")
    }

    @Test
    fun `getFavoriteCoins returns empty list when no favorites`() = runTest {
        // Given
        coEvery { favoriteCoinDao.getAllFavoriteCoins() } returns emptyList()

        // When
        val result = repository.getFavoriteCoins()

        // Then
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEmpty()
    }

    @Test
    fun `getFavoriteCoins returns failure when dao throws exception`() = runTest {
        // Given
        val exception = Exception("Database error")
        coEvery { favoriteCoinDao.getAllFavoriteCoins() } throws exception

        // When
        val result = repository.getFavoriteCoins()

        // Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isEqualTo(exception)
    }

    @Test
    fun `getFavoriteCoins calls dao getAllFavoriteCoins`() = runTest {
        // Given
        coEvery { favoriteCoinDao.getAllFavoriteCoins() } returns emptyList()

        // When
        repository.getFavoriteCoins()

        // Then
        coVerify(exactly = 1) {
            favoriteCoinDao.getAllFavoriteCoins()
        }
    }

    // ==================== getFavoriteCoinsCount Tests ====================

    @Test
    fun `getFavoriteCoinsCount returns correct count`() = runTest {
        // Given
        coEvery { favoriteCoinDao.getFavoriteCoinsCount() } returns 5

        // When
        val result = repository.getFavoriteCoinsCount()

        // Then
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(5)
    }

    @Test
    fun `getFavoriteCoinsCount returns zero when no favorites`() = runTest {
        // Given
        coEvery { favoriteCoinDao.getFavoriteCoinsCount() } returns 0

        // When
        val result = repository.getFavoriteCoinsCount()

        // Then
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(0)
    }

    @Test
    fun `getFavoriteCoinsCount returns failure when dao throws exception`() = runTest {
        // Given
        val exception = Exception("Database error")
        coEvery { favoriteCoinDao.getFavoriteCoinsCount() } throws exception

        // When
        val result = repository.getFavoriteCoinsCount()

        // Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isEqualTo(exception)
    }

    // ==================== isFavorite Tests ====================

    @Test
    fun `isFavorite returns true when coin is favorite`() = runTest {
        // Given
        coEvery { favoriteCoinDao.isFavorite("bitcoin") } returns true

        // When
        val result = repository.isFavorite("bitcoin")

        // Then
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isTrue()
    }

    @Test
    fun `isFavorite returns false when coin is not favorite`() = runTest {
        // Given
        coEvery { favoriteCoinDao.isFavorite("bitcoin") } returns false

        // When
        val result = repository.isFavorite("bitcoin")

        // Then
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isFalse()
    }

    @Test
    fun `isFavorite passes correct coinId to dao`() = runTest {
        // Given
        coEvery { favoriteCoinDao.isFavorite(any()) } returns false

        // When
        repository.isFavorite("ethereum")

        // Then
        coVerify(exactly = 1) {
            favoriteCoinDao.isFavorite("ethereum")
        }
    }

    @Test
    fun `isFavorite returns failure when dao throws exception`() = runTest {
        // Given
        val exception = Exception("Database error")
        coEvery { favoriteCoinDao.isFavorite(any()) } throws exception

        // When
        val result = repository.isFavorite("bitcoin")

        // Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isEqualTo(exception)
    }

    // ==================== addToFavorites Tests ====================

    @Test
    fun `addToFavorites inserts coin successfully`() = runTest {
        // Given
        val coin = createMockCoin()
        coEvery { favoriteCoinDao.insertFavoriteCoin(any()) } returns Unit

        // When
        val result = repository.addToFavorites(coin)

        // Then
        assertThat(result.isSuccess).isTrue()
    }

    @Test
    fun `addToFavorites calls dao with correct entity`() = runTest {
        // Given
        val coin = createMockCoin("ethereum", "Ethereum")
        coEvery { favoriteCoinDao.insertFavoriteCoin(any()) } returns Unit

        // When
        repository.addToFavorites(coin)

        // Then
        coVerify(exactly = 1) {
            favoriteCoinDao.insertFavoriteCoin(match {
                it.coinId == "ethereum" && it.name == "Ethereum"
            })
        }
    }

    @Test
    fun `addToFavorites returns failure when dao throws exception`() = runTest {
        // Given
        val coin = createMockCoin()
        val exception = Exception("Insert failed")
        coEvery { favoriteCoinDao.insertFavoriteCoin(any()) } throws exception

        // When
        val result = repository.addToFavorites(coin)

        // Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isEqualTo(exception)
    }

    // ==================== removeFromFavorites Tests ====================

    @Test
    fun `removeFromFavorites deletes coin successfully`() = runTest {
        // Given
        coEvery { favoriteCoinDao.deleteFavoriteCoinById(any()) } returns Unit

        // When
        val result = repository.removeFromFavorites("bitcoin")

        // Then
        assertThat(result.isSuccess).isTrue()
    }

    @Test
    fun `removeFromFavorites passes correct coinId to dao`() = runTest {
        // Given
        coEvery { favoriteCoinDao.deleteFavoriteCoinById(any()) } returns Unit

        // When
        repository.removeFromFavorites("ethereum")

        // Then
        coVerify(exactly = 1) {
            favoriteCoinDao.deleteFavoriteCoinById("ethereum")
        }
    }

    @Test
    fun `removeFromFavorites returns failure when dao throws exception`() = runTest {
        // Given
        val exception = Exception("Delete failed")
        coEvery { favoriteCoinDao.deleteFavoriteCoinById(any()) } throws exception

        // When
        val result = repository.removeFromFavorites("bitcoin")

        // Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isEqualTo(exception)
    }

    // ==================== clearAllFavorites Tests ====================

    @Test
    fun `clearAllFavorites deletes all coins successfully`() = runTest {
        // Given
        coEvery { favoriteCoinDao.deleteAllFavoriteCoins() } returns Unit

        // When
        val result = repository.clearAllFavorites()

        // Then
        assertThat(result.isSuccess).isTrue()
    }

    @Test
    fun `clearAllFavorites calls dao deleteAllFavoriteCoins`() = runTest {
        // Given
        coEvery { favoriteCoinDao.deleteAllFavoriteCoins() } returns Unit

        // When
        repository.clearAllFavorites()

        // Then
        coVerify(exactly = 1) {
            favoriteCoinDao.deleteAllFavoriteCoins()
        }
    }

    @Test
    fun `clearAllFavorites returns failure when dao throws exception`() = runTest {
        // Given
        val exception = Exception("Clear failed")
        coEvery { favoriteCoinDao.deleteAllFavoriteCoins() } throws exception

        // When
        val result = repository.clearAllFavorites()

        // Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isEqualTo(exception)
    }

    // ==================== Helper Methods ====================

    private fun createMockEntity(
        coinId: String = "bitcoin",
        name: String = "Bitcoin",
        symbol: String = "BTC"
    ) = FavoriteCoinEntity(
        coinId = coinId,
        name = name,
        symbol = symbol,
        image = "https://example.com/$coinId.png",
        addedAt = System.currentTimeMillis()
    )

    private fun createMockCoin(
        id: String = "bitcoin",
        name: String = "Bitcoin"
    ) = Coin(
        id = id,
        name = name,
        symbol = "BTC",
        image = "https://example.com/$id.png",
        currentPrice = 50000.0,
        marketCap = 1000000000000L,
        marketCapRank = 1,
        priceChangePercentage24h = 2.5
    )
}

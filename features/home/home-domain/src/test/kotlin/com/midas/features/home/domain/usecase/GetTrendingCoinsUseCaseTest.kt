package com.midas.features.home.domain.usecase

import app.cash.turbine.test
import com.midas.features.home.domain.model.Coin
import com.midas.features.home.domain.repository.CoinRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetTrendingCoinsUseCaseTest {

    private lateinit var coinRepository: CoinRepository
    private lateinit var getTrendingCoinsUseCase: GetTrendingCoinsUseCase

    @Before
    fun setup() {
        coinRepository = mockk()
        getTrendingCoinsUseCase = GetTrendingCoinsUseCase(coinRepository)
    }

    @Test
    fun `invoke should return trending coins sorted by price change descending`() = runTest {
        // Given
        val mockCoins = listOf(
            createMockCoin("bitcoin", "Bitcoin", "BTC", 50000.0, 1, -2.5),
            createMockCoin("ethereum", "Ethereum", "ETH", 3000.0, 2, 5.0),
            createMockCoin("cardano", "Cardano", "ADA", 1.0, 3, 10.0),
            createMockCoin("solana", "Solana", "SOL", 100.0, 4, 7.5)
        )
        coEvery {
            coinRepository.getTrendingCoins()
        } returns Result.success(mockCoins)

        // When
        val result = getTrendingCoinsUseCase()

        // Then
        result.test {
            val emission = awaitItem()
            assertTrue(emission.isSuccess)
            val coins = emission.getOrNull()!!
            assertEquals(4, coins.size)
            // Should be sorted by price change descending
            assertEquals(10.0, coins[0].priceChangePercentage24h!!, 0.01)
            assertEquals(7.5, coins[1].priceChangePercentage24h!!, 0.01)
            assertEquals(5.0, coins[2].priceChangePercentage24h!!, 0.01)
            assertEquals(-2.5, coins[3].priceChangePercentage24h!!, 0.01)
            awaitComplete()
        }

        coVerify(exactly = 1) { coinRepository.getTrendingCoins() }
    }

    @Test
    fun `invoke should filter out coins with rank greater than 100`() = runTest {
        // Given
        val mockCoins = listOf(
            createMockCoin("bitcoin", "Bitcoin", "BTC", 50000.0, 1, 5.0),
            createMockCoin("ethereum", "Ethereum", "ETH", 3000.0, 50, 10.0),
            createMockCoin("invalid", "Invalid", "INV", 1.0, 101, 15.0), // Rank > 100
            createMockCoin("cardano", "Cardano", "ADA", 1.0, 100, 8.0)
        )
        coEvery {
            coinRepository.getTrendingCoins()
        } returns Result.success(mockCoins)

        // When
        val result = getTrendingCoinsUseCase()

        // Then
        result.test {
            val emission = awaitItem()
            assertTrue(emission.isSuccess)
            val coins = emission.getOrNull()!!
            assertEquals(3, coins.size)
            assertTrue(coins.all { it.marketCapRank!! <= 100 })
            awaitComplete()
        }
    }

    @Test
    fun `invoke should filter out coins with empty name or symbol`() = runTest {
        // Given
        val mockCoins = listOf(
            createMockCoin("bitcoin", "Bitcoin", "BTC", 50000.0, 1, 5.0),
            createMockCoin("invalid1", "", "INV", 1.0, 2, 10.0), // Empty name
            createMockCoin("invalid2", "Invalid", "", 1.0, 3, 8.0), // Empty symbol
            createMockCoin("ethereum", "Ethereum", "ETH", 3000.0, 4, 7.0)
        )
        coEvery {
            coinRepository.getTrendingCoins()
        } returns Result.success(mockCoins)

        // When
        val result = getTrendingCoinsUseCase()

        // Then
        result.test {
            val emission = awaitItem()
            assertTrue(emission.isSuccess)
            val coins = emission.getOrNull()!!
            assertEquals(2, coins.size)
            assertTrue(coins.all { it.name.isNotBlank() })
            assertTrue(coins.all { it.symbol.isNotBlank() })
            awaitComplete()
        }
    }

    @Test
    fun `invoke should limit results to maximum 10 coins`() = runTest {
        // Given
        val mockCoins = List(15) { index ->
            createMockCoin(
                "coin$index",
                "Coin $index",
                "C$index",
                100.0,
                index + 1,
                (15 - index).toDouble() // Descending price changes
            )
        }
        coEvery {
            coinRepository.getTrendingCoins()
        } returns Result.success(mockCoins)

        // When
        val result = getTrendingCoinsUseCase()

        // Then
        result.test {
            val emission = awaitItem()
            assertTrue(emission.isSuccess)
            val coins = emission.getOrNull()!!
            assertEquals(10, coins.size)
            awaitComplete()
        }
    }

    @Test
    fun `invoke should handle null price change percentage as zero`() = runTest {
        // Given
        val mockCoins = listOf(
            createMockCoin("bitcoin", "Bitcoin", "BTC", 50000.0, 1, 5.0),
            createMockCoin("ethereum", "Ethereum", "ETH", 3000.0, 2, null), // Null price change
            createMockCoin("cardano", "Cardano", "ADA", 1.0, 3, 10.0)
        )
        coEvery {
            coinRepository.getTrendingCoins()
        } returns Result.success(mockCoins)

        // When
        val result = getTrendingCoinsUseCase()

        // Then
        result.test {
            val emission = awaitItem()
            assertTrue(emission.isSuccess)
            val coins = emission.getOrNull()!!
            assertEquals(3, coins.size)
            // Null should be treated as 0, so it should be last
            assertEquals("Cardano", coins[0].name) // 10.0
            assertEquals("Bitcoin", coins[1].name) // 5.0
            assertEquals("Ethereum", coins[2].name) // null (treated as 0)
            awaitComplete()
        }
    }

    @Test
    fun `invoke should handle repository failure`() = runTest {
        // Given
        val exception = Exception("Network error")
        coEvery {
            coinRepository.getTrendingCoins()
        } returns Result.failure(exception)

        // When
        val result = getTrendingCoinsUseCase()

        // Then
        result.test {
            val emission = awaitItem()
            assertTrue(emission.isFailure)
            assertEquals("Network error", emission.exceptionOrNull()?.message)
            awaitComplete()
        }
    }

    @Test
    fun `invoke should filter out coins with null rank`() = runTest {
        // Given
        val mockCoins = listOf(
            createMockCoin("bitcoin", "Bitcoin", "BTC", 50000.0, 1, 5.0),
            createMockCoin("invalid", "Invalid", "INV", 1.0, null, 10.0), // Null rank
            createMockCoin("ethereum", "Ethereum", "ETH", 3000.0, 2, 7.0)
        )
        coEvery {
            coinRepository.getTrendingCoins()
        } returns Result.success(mockCoins)

        // When
        val result = getTrendingCoinsUseCase()

        // Then
        result.test {
            val emission = awaitItem()
            assertTrue(emission.isSuccess)
            val coins = emission.getOrNull()!!
            assertEquals(2, coins.size)
            assertTrue(coins.all { it.marketCapRank != null })
            awaitComplete()
        }
    }

    @Test
    fun `invoke should return empty list when no coins match criteria`() = runTest {
        // Given - all coins have rank > 100
        val mockCoins = listOf(
            createMockCoin("coin1", "Coin 1", "C1", 100.0, 101, 5.0),
            createMockCoin("coin2", "Coin 2", "C2", 100.0, 150, 10.0),
            createMockCoin("coin3", "Coin 3", "C3", 100.0, 200, 7.0)
        )
        coEvery {
            coinRepository.getTrendingCoins()
        } returns Result.success(mockCoins)

        // When
        val result = getTrendingCoinsUseCase()

        // Then
        result.test {
            val emission = awaitItem()
            assertTrue(emission.isSuccess)
            val coins = emission.getOrNull()!!
            assertEquals(0, coins.size)
            awaitComplete()
        }
    }

    @Test
    fun `invoke should handle empty list from repository`() = runTest {
        // Given
        coEvery {
            coinRepository.getTrendingCoins()
        } returns Result.success(emptyList())

        // When
        val result = getTrendingCoinsUseCase()

        // Then
        result.test {
            val emission = awaitItem()
            assertTrue(emission.isSuccess)
            val coins = emission.getOrNull()!!
            assertEquals(0, coins.size)
            awaitComplete()
        }
    }

    private fun createMockCoin(
        id: String,
        name: String,
        symbol: String,
        price: Double,
        rank: Int?,
        priceChange24h: Double?
    ) = Coin(
        id = id,
        name = name,
        symbol = symbol,
        image = "https://example.com/$id.png",
        currentPrice = price,
        marketCap = (price * 1000000).toLong(),
        marketCapRank = rank,
        priceChangePercentage24h = priceChange24h,
    )
}

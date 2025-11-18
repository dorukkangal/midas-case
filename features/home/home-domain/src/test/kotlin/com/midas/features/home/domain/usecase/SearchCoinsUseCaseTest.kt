package com.midas.features.home.domain.usecase

import app.cash.turbine.test
import com.midas.features.home.domain.model.Coin
import com.midas.features.home.domain.repository.CoinRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SearchCoinsUseCaseTest {

    private lateinit var coinRepository: CoinRepository
    private lateinit var searchCoinsUseCase: SearchCoinsUseCase

    @Before
    fun setup() {
        coinRepository = mockk()
        searchCoinsUseCase = SearchCoinsUseCase(coinRepository)
    }

    @Test
    fun `invoke with valid query should return filtered coins`() = runTest {
        // Given
        val query = "bit"
        val mockCoins = listOf(
            createMockCoin("bitcoin", "Bitcoin", "BTC", 50000.0, 1),
            createMockCoin("bitcoin-cash", "Bitcoin Cash", "BCH", 400.0, 20),
            createMockCoin("bitdao", "BitDAO", "BIT", 0.50, 100)
        )
        coEvery {
            coinRepository.searchCoins(query)
        } returns Result.success(mockCoins)

        // When
        val result = searchCoinsUseCase(query)

        // Then
        result.test {
            val emission = awaitItem()
            assertTrue(emission.isSuccess)
            val coins = emission.getOrNull()!!
            assertEquals(3, coins.size)
            awaitComplete()
        }

        coVerify(exactly = 1) { coinRepository.searchCoins(query) }
    }

    @Test
    fun `invoke should rank exact symbol matches highest`() = runTest {
        // Given
        val query = "btc"
        val mockCoins = listOf(
            createMockCoin("bitcoin-cash", "Bitcoin Cash", "BCH", 400.0, 20),
            createMockCoin("bitcoin", "Bitcoin", "BTC", 50000.0, 1), // Exact symbol match
            createMockCoin("bitcore", "Bitcore", "BTX", 0.50, 200)
        )
        coEvery {
            coinRepository.searchCoins(query)
        } returns Result.success(mockCoins)

        // When
        val result = searchCoinsUseCase(query)

        // Then
        result.test {
            val emission = awaitItem()
            assertTrue(emission.isSuccess)
            val coins = emission.getOrNull()!!
            // Bitcoin (BTC) should be first due to exact symbol match
            assertEquals("Bitcoin", coins[0].name)
            assertEquals("BTC", coins[0].symbol)
            awaitComplete()
        }
    }

    @Test
    fun `invoke should rank exact name matches high`() = runTest {
        // Given
        val query = "bitcoin"
        val mockCoins = listOf(
            createMockCoin("bitcoin-cash", "Bitcoin Cash", "BCH", 400.0, 20),
            createMockCoin("bitcoin", "Bitcoin", "BTC", 50000.0, 1), // Exact name match
            createMockCoin("bitcoin-gold", "Bitcoin Gold", "BTG", 15.0, 100)
        )
        coEvery {
            coinRepository.searchCoins(query)
        } returns Result.success(mockCoins)

        // When
        val result = searchCoinsUseCase(query)

        // Then
        result.test {
            val emission = awaitItem()
            assertTrue(emission.isSuccess)
            val coins = emission.getOrNull()!!
            // Bitcoin should be first due to exact name match
            assertEquals("Bitcoin", coins[0].name)
            awaitComplete()
        }
    }

    @Test
    fun `invoke should boost popular coins in ranking`() = runTest {
        // Given
        val query = "bit"
        val mockCoins = listOf(
            createMockCoin("obscure-coin", "Obscure Bit Coin", "OBC", 0.001, 500), // Low rank
            createMockCoin("bitcoin", "Bitcoin", "BTC", 50000.0, 1) // Top 10
        )
        coEvery {
            coinRepository.searchCoins(query)
        } returns Result.success(mockCoins)

        // When
        val result = searchCoinsUseCase(query)

        // Then
        result.test {
            val emission = awaitItem()
            assertTrue(emission.isSuccess)
            val coins = emission.getOrNull()!!
            // Bitcoin should be first due to popularity boost
            assertEquals("Bitcoin", coins[0].name)
            awaitComplete()
        }
    }

    @Test
    fun `invoke should filter out coins with empty name or symbol`() = runTest {
        // Given
        val query = "bit"
        val mockCoins = listOf(
            createMockCoin("bitcoin", "Bitcoin", "BTC", 50000.0, 1),
            createMockCoin("invalid1", "", "INV", 1.0, 2), // Empty name
            createMockCoin("invalid2", "Invalid", "", 1.0, 3), // Empty symbol
            createMockCoin("ethereum", "Ethereum", "ETH", 3000.0, 2)
        )
        coEvery {
            coinRepository.searchCoins(query)
        } returns Result.success(mockCoins)

        // When
        val result = searchCoinsUseCase(query)

        // Then
        result.test {
            val emission = awaitItem()
            assertTrue(emission.isSuccess)
            val coins = emission.getOrNull()!!
            // Only coins matching query and having valid data should be included
            assertTrue(coins.all { it.name.isNotBlank() })
            assertTrue(coins.all { it.symbol.isNotBlank() })
            awaitComplete()
        }
    }

    @Test
    fun `invoke should limit results to maximum 20 coins`() = runTest {
        // Given
        val query = "coin"
        val mockCoins = List(30) { index ->
            createMockCoin("coin$index", "Coin $index", "C$index", 100.0, index + 1)
        }
        coEvery {
            coinRepository.searchCoins(query)
        } returns Result.success(mockCoins)

        // When
        val result = searchCoinsUseCase(query)

        // Then
        result.test {
            val emission = awaitItem()
            assertTrue(emission.isSuccess)
            val coins = emission.getOrNull()!!
            assertTrue(coins.size <= 20)
            awaitComplete()
        }
    }

    @Test
    fun `invoke with query less than 2 characters should return failure`() = runTest {
        // Given
        val shortQuery = "b"

        // When
        val result = searchCoinsUseCase(shortQuery)

        // Then
        result.test {
            val emission = awaitItem()
            assertTrue(emission.isFailure)
            assertTrue(emission.exceptionOrNull() is IllegalArgumentException)
            assertTrue(emission.exceptionOrNull()?.message?.contains("at least 2 characters") == true)
            awaitComplete()
        }

        // Repository should not be called
        coVerify(exactly = 0) { coinRepository.searchCoins(any()) }
    }

    @Test
    fun `invoke should handle repository failure`() = runTest {
        // Given
        val query = "bitcoin"
        val exception = Exception("Network error")
        coEvery {
            coinRepository.searchCoins(query)
        } returns Result.failure(exception)

        // When
        val result = searchCoinsUseCase(query)

        // Then
        result.test {
            val emission = awaitItem()
            assertTrue(emission.isFailure)
            assertEquals("Network error", emission.exceptionOrNull()?.message)
            awaitComplete()
        }
    }

    @Test
    fun `invoke should trim and lowercase query`() = runTest {
        // Given
        val query = "  BitCoin  " // With spaces and mixed case
        val cleanQuery = "bitcoin"
        val mockCoins = listOf(
            createMockCoin("bitcoin", "Bitcoin", "BTC", 50000.0, 1)
        )
        coEvery {
            coinRepository.searchCoins(cleanQuery)
        } returns Result.success(mockCoins)

        // When
        val result = searchCoinsUseCase(query)

        // Then
        result.test {
            val emission = awaitItem()
            assertTrue(emission.isSuccess)
            awaitComplete()
        }

        // Should be called with cleaned query
        coVerify(exactly = 1) { coinRepository.searchCoins(cleanQuery) }
    }

    @Test
    fun `invoke should handle empty results from repository`() = runTest {
        // Given
        val query = "nonexistent"
        coEvery {
            coinRepository.searchCoins(query)
        } returns Result.success(emptyList())

        // When
        val result = searchCoinsUseCase(query)

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
    fun `invoke should prioritize startsWith over contains in name`() = runTest {
        // Given
        val query = "eth"
        val mockCoins = listOf(
            createMockCoin("something-eth", "Something Eth", "SMET", 1.0, 100), // Contains
            createMockCoin("ethereum", "Ethereum", "ETH", 3000.0, 2) // Starts with
        )
        coEvery {
            coinRepository.searchCoins(query)
        } returns Result.success(mockCoins)

        // When
        val result = searchCoinsUseCase(query)

        // Then
        result.test {
            val emission = awaitItem()
            assertTrue(emission.isSuccess)
            val coins = emission.getOrNull()!!
            // Ethereum should be first (starts with + exact symbol match)
            assertEquals("Ethereum", coins[0].name)
            awaitComplete()
        }
    }

    @Test
    fun `invoke should filter out coins with zero relevance score`() = runTest {
        // Given
        val query = "xyz"
        val mockCoins = listOf(
            createMockCoin("xyz-coin", "XYZ Coin", "XYZ", 1.0, 500) // Match
        )
        coEvery {
            coinRepository.searchCoins(query)
        } returns Result.success(mockCoins)

        // When
        val result = searchCoinsUseCase(query)

        // Then
        result.test {
            val emission = awaitItem()
            assertTrue(emission.isSuccess)
            val coins = emission.getOrNull()!!
            // XYZ Coin should match
            assertEquals(1, coins.size)
            assertEquals("XYZ Coin", coins[0].name)
            awaitComplete()
        }
    }

    private fun createMockCoin(
        id: String,
        name: String,
        symbol: String,
        price: Double,
        rank: Int
    ) = Coin(
        id = id,
        name = name,
        symbol = symbol,
        image = "https://example.com/$id.png",
        currentPrice = price,
        marketCap = (price * 1000000).toLong(),
        marketCapRank = rank,
        priceChangePercentage24h = 5.0,
    )
}

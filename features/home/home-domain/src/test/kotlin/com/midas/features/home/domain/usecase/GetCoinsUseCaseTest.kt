package com.midas.features.home.domain.usecase

import app.cash.turbine.test
import com.midas.features.home.domain.model.Coin
import com.midas.features.home.domain.model.SortOrder
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

class GetCoinsUseCaseTest {

    private lateinit var coinRepository: CoinRepository
    private lateinit var getCoinsUseCase: GetCoinsUseCase

    @Before
    fun setup() {
        coinRepository = mockk()
        getCoinsUseCase = GetCoinsUseCase(coinRepository)
    }

    @Test
    fun `invoke with default params should return filtered coins`() = runTest {
        // Given
        val mockCoins = listOf(
            createMockCoin("bitcoin", "Bitcoin", "BTC", 50000.0, 1),
            createMockCoin("ethereum", "Ethereum", "ETH", 3000.0, 2),
            createMockCoin("cardano", "Cardano", "ADA", 1.0, 3)
        )
        coEvery {
            coinRepository.getCoins(
                vsCurrency = "usd",
                order = "market_cap_desc",
                perPage = 50,
                page = 1
            )
        } returns Result.success(mockCoins)

        // When
        val result = getCoinsUseCase(GetCoinsUseCase.Params())

        // Then
        result.test {
            val emission = awaitItem()
            assertTrue(emission.isSuccess)
            assertEquals(3, emission.getOrNull()?.size)
            awaitComplete()
        }

        coVerify(exactly = 1) {
            coinRepository.getCoins(
                vsCurrency = "usd",
                order = "market_cap_desc",
                perPage = 50,
                page = 1
            )
        }
    }

    @Test
    fun `invoke should filter out invalid coins with empty name or zero price`() = runTest {
        // Given
        val mockCoins = listOf(
            createMockCoin("bitcoin", "Bitcoin", "BTC", 50000.0, 1),
            createMockCoin("invalid1", "", "INV", 100.0, 2), // Empty name
            createMockCoin("invalid2", "Invalid", "INV", 0.0, 3), // Zero price
            createMockCoin("invalid3", "Invalid", "INV", null, 4), // Null price
            createMockCoin("ethereum", "Ethereum", "ETH", 3000.0, 5)
        )
        coEvery {
            coinRepository.getCoins(any(), any(), any(), any())
        } returns Result.success(mockCoins)

        // When
        val result = getCoinsUseCase(GetCoinsUseCase.Params())

        // Then
        result.test {
            val emission = awaitItem()
            assertTrue(emission.isSuccess)
            val coins = emission.getOrNull()!!
            assertEquals(2, coins.size)
            assertTrue(coins.all { it.currentPrice != null && it.currentPrice > 0 })
            assertTrue(coins.all { it.name.isNotBlank() })
            awaitComplete()
        }
    }

    @Test
    fun `invoke with PRICE_DESC order should sort by price descending`() = runTest {
        // Given
        val mockCoins = listOf(
            createMockCoin("cardano", "Cardano", "ADA", 1.0, 3),
            createMockCoin("bitcoin", "Bitcoin", "BTC", 50000.0, 1),
            createMockCoin("ethereum", "Ethereum", "ETH", 3000.0, 2)
        )
        coEvery {
            coinRepository.getCoins(
                vsCurrency = "usd",
                order = "price_desc",
                perPage = 50,
                page = 1
            )
        } returns Result.success(mockCoins)

        // When
        val result = getCoinsUseCase(
            GetCoinsUseCase.Params(sortOrder = SortOrder.PRICE_DESC)
        )

        // Then
        result.test {
            val emission = awaitItem()
            assertTrue(emission.isSuccess)
            val coins = emission.getOrNull()!!
            assertEquals(50000.0, coins[0].currentPrice!!, 0.01)
            assertEquals(3000.0, coins[1].currentPrice!!, 0.01)
            assertEquals(1.0, coins[2].currentPrice!!, 0.01)
            awaitComplete()
        }
    }

    @Test
    fun `invoke with PRICE_ASC order should sort by price ascending`() = runTest {
        // Given
        val mockCoins = listOf(
            createMockCoin("bitcoin", "Bitcoin", "BTC", 50000.0, 1),
            createMockCoin("cardano", "Cardano", "ADA", 1.0, 3),
            createMockCoin("ethereum", "Ethereum", "ETH", 3000.0, 2)
        )
        coEvery {
            coinRepository.getCoins(any(), any(), any(), any())
        } returns Result.success(mockCoins)

        // When
        val result = getCoinsUseCase(
            GetCoinsUseCase.Params(sortOrder = SortOrder.PRICE_ASC)
        )

        // Then
        result.test {
            val emission = awaitItem()
            assertTrue(emission.isSuccess)
            val coins = emission.getOrNull()!!
            assertEquals(1.0, coins[0].currentPrice!!, 0.01)
            assertEquals(3000.0, coins[1].currentPrice!!, 0.01)
            assertEquals(50000.0, coins[2].currentPrice!!, 0.01)
            awaitComplete()
        }
    }

    @Test
    fun `invoke with NAME_ASC order should sort alphabetically`() = runTest {
        // Given
        val mockCoins = listOf(
            createMockCoin("ethereum", "Ethereum", "ETH", 3000.0, 2),
            createMockCoin("cardano", "Cardano", "ADA", 1.0, 3),
            createMockCoin("bitcoin", "Bitcoin", "BTC", 50000.0, 1)
        )
        coEvery {
            coinRepository.getCoins(any(), any(), any(), any())
        } returns Result.success(mockCoins)

        // When
        val result = getCoinsUseCase(
            GetCoinsUseCase.Params(sortOrder = SortOrder.NAME_ASC)
        )

        // Then
        result.test {
            val emission = awaitItem()
            assertTrue(emission.isSuccess)
            val coins = emission.getOrNull()!!
            assertEquals("Bitcoin", coins[0].name)
            assertEquals("Cardano", coins[1].name)
            assertEquals("Ethereum", coins[2].name)
            awaitComplete()
        }
    }

    @Test
    fun `invoke with CHANGE_24H_DESC order should sort by price change`() = runTest {
        // Given
        val mockCoins = listOf(
            createMockCoin("bitcoin", "Bitcoin", "BTC", 50000.0, 1, -2.5),
            createMockCoin("ethereum", "Ethereum", "ETH", 3000.0, 2, 5.0),
            createMockCoin("cardano", "Cardano", "ADA", 1.0, 3, 10.0)
        )
        coEvery {
            coinRepository.getCoins(any(), any(), any(), any())
        } returns Result.success(mockCoins)

        // When
        val result = getCoinsUseCase(
            GetCoinsUseCase.Params(sortOrder = SortOrder.CHANGE_24H_DESC)
        )

        // Then
        result.test {
            val emission = awaitItem()
            assertTrue(emission.isSuccess)
            val coins = emission.getOrNull()!!
            assertEquals(10.0, coins[0].priceChangePercentage24h!!, 0.01)
            assertEquals(5.0, coins[1].priceChangePercentage24h!!, 0.01)
            assertEquals(-2.5, coins[2].priceChangePercentage24h!!, 0.01)
            awaitComplete()
        }
    }

    @Test
    fun `invoke should handle repository failure`() = runTest {
        // Given
        val exception = Exception("Network error")
        coEvery {
            coinRepository.getCoins(any(), any(), any(), any())
        } returns Result.failure(exception)

        // When
        val result = getCoinsUseCase(GetCoinsUseCase.Params())

        // Then
        result.test {
            val emission = awaitItem()
            assertTrue(emission.isFailure)
            assertEquals("Network error", emission.exceptionOrNull()?.message)
            awaitComplete()
        }
    }

    @Test
    fun `invoke with perPage limit should pass to repository`() = runTest {
        // Given
        val mockCoins = List(10) { index ->
            createMockCoin("coin$index", "Coin $index", "C$index", 100.0, index + 1)
        }
        coEvery {
            coinRepository.getCoins(
                vsCurrency = "usd",
                order = "market_cap_desc",
                perPage = 10,
                page = 1
            )
        } returns Result.success(mockCoins)

        // When
        val result = getCoinsUseCase(GetCoinsUseCase.Params(perPage = 10))

        // Then
        result.test {
            val emission = awaitItem()
            assertTrue(emission.isSuccess)
            val coins = emission.getOrNull()!!
            assertEquals(10, coins.size)
            awaitComplete()
        }

        coVerify(exactly = 1) {
            coinRepository.getCoins(
                vsCurrency = "usd",
                order = "market_cap_desc",
                perPage = 10,
                page = 1
            )
        }
    }

    @Test
    fun `invoke with custom currency should pass to repository`() = runTest {
        // Given
        val mockCoins = listOf(
            createMockCoin("bitcoin", "Bitcoin", "BTC", 45000.0, 1)
        )
        coEvery {
            coinRepository.getCoins(
                vsCurrency = "eur",
                order = "market_cap_desc",
                perPage = 50,
                page = 1
            )
        } returns Result.success(mockCoins)

        // When
        val result = getCoinsUseCase(
            GetCoinsUseCase.Params(vsCurrency = "eur")
        )

        // Then
        result.test {
            val emission = awaitItem()
            assertTrue(emission.isSuccess)
            awaitComplete()
        }

        coVerify(exactly = 1) {
            coinRepository.getCoins(
                vsCurrency = "eur",
                order = "market_cap_desc",
                perPage = 50,
                page = 1
            )
        }
    }

    @Test
    fun `invoke with page parameter should pass to repository`() = runTest {
        // Given
        val mockCoins = listOf(
            createMockCoin("bitcoin", "Bitcoin", "BTC", 50000.0, 51)
        )
        coEvery {
            coinRepository.getCoins(
                vsCurrency = "usd",
                order = "market_cap_desc",
                perPage = 50,
                page = 2
            )
        } returns Result.success(mockCoins)

        // When
        val result = getCoinsUseCase(
            GetCoinsUseCase.Params(page = 2)
        )

        // Then
        result.test {
            val emission = awaitItem()
            assertTrue(emission.isSuccess)
            awaitComplete()
        }

        coVerify(exactly = 1) {
            coinRepository.getCoins(
                vsCurrency = "usd",
                order = "market_cap_desc",
                perPage = 50,
                page = 2
            )
        }
    }

    private fun createMockCoin(
        id: String,
        name: String,
        symbol: String,
        price: Double?,
        rank: Int,
        priceChange24h: Double = 5.0
    ) = Coin(
        id = id,
        name = name,
        symbol = symbol,
        image = "https://example.com/$id.png",
        currentPrice = price,
        marketCap = price?.let { (it * 1000000).toLong() },
        marketCapRank = rank,
        priceChangePercentage24h = priceChange24h,
    )
}

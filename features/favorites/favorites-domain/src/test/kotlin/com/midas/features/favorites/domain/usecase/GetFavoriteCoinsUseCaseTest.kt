package com.midas.features.favorites.domain.usecase

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.midas.features.favorites.domain.repository.FavoritesRepository
import com.midas.features.home.domain.model.Coin
import com.midas.features.home.domain.model.SortOrder
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class GetFavoriteCoinsUseCaseTest {

    private lateinit var favoritesRepository: FavoritesRepository
    private lateinit var getAllFavoritesUseCase: GetAllFavoritesUseCase

    @Before
    fun setup() {
        favoritesRepository = mockk()
        getAllFavoritesUseCase = GetAllFavoritesUseCase(favoritesRepository)
    }

    @Test
    fun `invoke with ADDED_DATE_DESC should return coins in original order`() = runTest {
        // Given
        val coins = createTestCoins()
        val params = GetAllFavoritesUseCase.Params(sortOrder = null) // Default order - added date desc

        coEvery {
            favoritesRepository.getFavoriteCoins()
        } returns Result.success(coins)

        // When
        getAllFavoritesUseCase(params).test {
            // Then
            val result = awaitItem()
            assertThat(result.isSuccess).isTrue()

            val returnedCoins = result.getOrNull()
            assertThat(returnedCoins).hasSize(3)
            assertThat(returnedCoins?.get(0)?.id).isEqualTo("bitcoin")
            assertThat(returnedCoins?.get(1)?.id).isEqualTo("ethereum")
            assertThat(returnedCoins?.get(2)?.id).isEqualTo("cardano")

            awaitComplete()
        }

        coVerify(exactly = 1) {
            favoritesRepository.getFavoriteCoins()
        }
    }

    @Test
    fun `invoke with null sort order should return coins in original order from repository`() =
        runTest {
            // Given
            val coins = createTestCoins().reversed() // Reverse to test different ordering
            coEvery {
                favoritesRepository.getFavoriteCoins()
            } returns Result.success(coins)
            val params = GetAllFavoritesUseCase.Params(sortOrder = null) // No sorting applied

            // When
            getAllFavoritesUseCase(params).test {
                // Then
                val result = awaitItem()
                assertThat(result.isSuccess).isTrue()

                val returnedCoins = result.getOrNull()
                assertThat(returnedCoins).hasSize(3)
                // Should return in same order as repository (reversed from original)
                assertThat(returnedCoins?.get(0)?.id).isEqualTo("cardano")
                assertThat(returnedCoins?.get(1)?.id).isEqualTo("ethereum")
                assertThat(returnedCoins?.get(2)?.id).isEqualTo("bitcoin")

                awaitComplete()
            }

            coVerify(exactly = 1) {
                favoritesRepository.getFavoriteCoins()
            }
        }

    @Test
    fun `invoke with NAME_ASC should sort coins by name alphabetically`() = runTest {
        // Given
        val coins = createTestCoins()
        coEvery {
            favoritesRepository.getFavoriteCoins()
        } returns Result.success(coins)
        val params = GetAllFavoritesUseCase.Params(sortOrder = SortOrder.NAME_ASC)

        // When
        getAllFavoritesUseCase(params).test {
            // Then
            val result = awaitItem()
            assertThat(result.isSuccess).isTrue()

            val returnedCoins = result.getOrNull()
            assertThat(returnedCoins).hasSize(3)
            assertThat(returnedCoins?.get(0)?.name).isEqualTo("Bitcoin")
            assertThat(returnedCoins?.get(1)?.name).isEqualTo("Cardano")
            assertThat(returnedCoins?.get(2)?.name).isEqualTo("Ethereum")

            awaitComplete()
        }

        coVerify(exactly = 1) {
            favoritesRepository.getFavoriteCoins()
        }
    }

    @Test
    fun `invoke with NAME_DESC should sort coins by name reverse alphabetically`() = runTest {
        // Given
        val coins = createTestCoins()
        coEvery {
            favoritesRepository.getFavoriteCoins()
        } returns Result.success(coins)
        val params = GetAllFavoritesUseCase.Params(sortOrder = SortOrder.NAME_DESC)

        // When
        getAllFavoritesUseCase(params).test {
            // Then
            val result = awaitItem()
            assertThat(result.isSuccess).isTrue()

            val returnedCoins = result.getOrNull()
            assertThat(returnedCoins).hasSize(3)
            assertThat(returnedCoins?.get(0)?.name).isEqualTo("Ethereum")
            assertThat(returnedCoins?.get(1)?.name).isEqualTo("Cardano")
            assertThat(returnedCoins?.get(2)?.name).isEqualTo("Bitcoin")

            awaitComplete()
        }

        coVerify(exactly = 1) {
            favoritesRepository.getFavoriteCoins()
        }
    }

    @Test
    fun `invoke with PRICE_DESC should sort coins by price descending`() = runTest {
        // Given
        val coins = createTestCoins()
        coEvery {
            favoritesRepository.getFavoriteCoins()
        } returns Result.success(coins)
        val params = GetAllFavoritesUseCase.Params(sortOrder = SortOrder.PRICE_DESC)

        // When
        getAllFavoritesUseCase(params).test {
            // Then
            val result = awaitItem()
            assertThat(result.isSuccess).isTrue()

            val returnedCoins = result.getOrNull()
            assertThat(returnedCoins).hasSize(3)
            assertThat(returnedCoins?.get(0)?.currentPrice).isEqualTo(50000.0)
            assertThat(returnedCoins?.get(1)?.currentPrice).isEqualTo(3000.0)
            assertThat(returnedCoins?.get(2)?.currentPrice).isEqualTo(1.0)

            awaitComplete()
        }

        coVerify(exactly = 1) {
            favoritesRepository.getFavoriteCoins()
        }
    }

    @Test
    fun `invoke with PRICE_ASC should sort coins by price ascending`() = runTest {
        // Given
        val coins = createTestCoins()
        coEvery {
            favoritesRepository.getFavoriteCoins()
        } returns Result.success(coins)
        val params = GetAllFavoritesUseCase.Params(sortOrder = SortOrder.PRICE_ASC)

        // When
        getAllFavoritesUseCase(params).test {
            // Then
            val result = awaitItem()
            assertThat(result.isSuccess).isTrue()

            val returnedCoins = result.getOrNull()
            assertThat(returnedCoins).hasSize(3)
            assertThat(returnedCoins?.get(0)?.currentPrice).isEqualTo(1.0)
            assertThat(returnedCoins?.get(1)?.currentPrice).isEqualTo(3000.0)
            assertThat(returnedCoins?.get(2)?.currentPrice).isEqualTo(50000.0)

            awaitComplete()
        }

        coVerify(exactly = 1) {
            favoritesRepository.getFavoriteCoins()
        }
    }

    @Test
    fun `invoke with MARKET_CAP_DESC should sort coins by market cap descending`() = runTest {
        // Given
        val coins = createTestCoins()
        coEvery {
            favoritesRepository.getFavoriteCoins()
        } returns Result.success(coins)
        val params = GetAllFavoritesUseCase.Params(sortOrder = SortOrder.MARKET_CAP_DESC)

        // When
        getAllFavoritesUseCase(params).test {
            // Then
            val result = awaitItem()
            assertThat(result.isSuccess).isTrue()

            val returnedCoins = result.getOrNull()
            assertThat(returnedCoins).hasSize(3)
            assertThat(returnedCoins?.get(0)?.marketCap).isEqualTo(1000000000L)
            assertThat(returnedCoins?.get(1)?.marketCap).isEqualTo(500000000L)
            assertThat(returnedCoins?.get(2)?.marketCap).isEqualTo(10000000L)

            awaitComplete()
        }

        coVerify(exactly = 1) {
            favoritesRepository.getFavoriteCoins()
        }
    }

    @Test
    fun `invoke with MARKET_CAP_ASC should sort coins by market cap ascending`() = runTest {
        // Given
        val coins = createTestCoins()
        coEvery {
            favoritesRepository.getFavoriteCoins()
        } returns Result.success(coins)
        val params = GetAllFavoritesUseCase.Params(sortOrder = SortOrder.MARKET_CAP_ASC)

        // When
        getAllFavoritesUseCase(params).test {
            // Then
            val result = awaitItem()
            assertThat(result.isSuccess).isTrue()

            val returnedCoins = result.getOrNull()
            assertThat(returnedCoins).hasSize(3)
            assertThat(returnedCoins?.get(0)?.marketCap).isEqualTo(10000000L)
            assertThat(returnedCoins?.get(1)?.marketCap).isEqualTo(500000000L)
            assertThat(returnedCoins?.get(2)?.marketCap).isEqualTo(1000000000L)

            awaitComplete()
        }

        coVerify(exactly = 1) {
            favoritesRepository.getFavoriteCoins()
        }
    }

    @Test
    fun `invoke with CHANGE_24H_DESC should sort coins by 24h change descending`() = runTest {
        // Given
        val coins = createTestCoins()
        coEvery {
            favoritesRepository.getFavoriteCoins()
        } returns Result.success(coins)
        val params = GetAllFavoritesUseCase.Params(sortOrder = SortOrder.CHANGE_24H_DESC)

        // When
        getAllFavoritesUseCase(params).test {
            // Then
            val result = awaitItem()
            assertThat(result.isSuccess).isTrue()

            val returnedCoins = result.getOrNull()
            assertThat(returnedCoins).hasSize(3)
            assertThat(returnedCoins?.get(0)?.priceChangePercentage24h).isEqualTo(5.0)
            assertThat(returnedCoins?.get(1)?.priceChangePercentage24h).isEqualTo(2.0)
            assertThat(returnedCoins?.get(2)?.priceChangePercentage24h).isEqualTo(-3.0)

            awaitComplete()
        }

        coVerify(exactly = 1) {
            favoritesRepository.getFavoriteCoins()
        }
    }

    @Test
    fun `invoke with CHANGE_24H_ASC should sort coins by 24h change ascending`() = runTest {
        // Given
        val coins = createTestCoins()
        coEvery {
            favoritesRepository.getFavoriteCoins()
        } returns Result.success(coins)
        val params = GetAllFavoritesUseCase.Params(sortOrder = SortOrder.CHANGE_24H_ASC)

        // When
        getAllFavoritesUseCase(params).test {
            // Then
            val result = awaitItem()
            assertThat(result.isSuccess).isTrue()

            val returnedCoins = result.getOrNull()
            assertThat(returnedCoins).hasSize(3)
            assertThat(returnedCoins?.get(0)?.priceChangePercentage24h).isEqualTo(-3.0)
            assertThat(returnedCoins?.get(1)?.priceChangePercentage24h).isEqualTo(2.0)
            assertThat(returnedCoins?.get(2)?.priceChangePercentage24h).isEqualTo(5.0)

            awaitComplete()
        }

        coVerify(exactly = 1) {
            favoritesRepository.getFavoriteCoins()
        }
    }

    @Test
    fun `invoke with empty list should return empty list`() = runTest {
        // Given
        coEvery {
            favoritesRepository.getFavoriteCoins()
        } returns Result.success(emptyList())
        val params = GetAllFavoritesUseCase.Params(sortOrder = SortOrder.NAME_ASC)

        // When
        getAllFavoritesUseCase(params).test {
            // Then
            val result = awaitItem()
            assertThat(result.isSuccess).isTrue()

            val returnedCoins = result.getOrNull()
            assertThat(returnedCoins).isEmpty()

            awaitComplete()
        }

        coVerify(exactly = 1) {
            favoritesRepository.getFavoriteCoins()
        }
    }

    @Test
    fun `invoke with null market cap should handle sorting correctly`() = runTest {
        // Given
        val coins = listOf(
            createCoin(id = "coin1", marketCap = null),
            createCoin(id = "coin2", marketCap = 1000L),
            createCoin(id = "coin3", marketCap = null)
        )
        coEvery {
            favoritesRepository.getFavoriteCoins()
        } returns Result.success(coins)
        val params = GetAllFavoritesUseCase.Params(sortOrder = SortOrder.MARKET_CAP_DESC)

        // When
        getAllFavoritesUseCase(params).test {
            // Then
            val result = awaitItem()
            assertThat(result.isSuccess).isTrue()

            val returnedCoins = result.getOrNull()
            assertThat(returnedCoins).hasSize(3)
            assertThat(returnedCoins?.get(0)?.id).isEqualTo("coin2") // Non-null comes first

            awaitComplete()
        }
    }

    @Test
    fun `invoke with null price should handle sorting correctly`() = runTest {
        // Given
        val coins = listOf(
            createCoin(id = "coin1", currentPrice = null),
            createCoin(id = "coin2", currentPrice = 100.0),
            createCoin(id = "coin3", currentPrice = null)
        )
        coEvery {
            favoritesRepository.getFavoriteCoins()
        } returns Result.success(coins)
        val params = GetAllFavoritesUseCase.Params(sortOrder = SortOrder.PRICE_DESC)

        // When
        getAllFavoritesUseCase(params).test {
            // Then
            val result = awaitItem()
            assertThat(result.isSuccess).isTrue()

            val returnedCoins = result.getOrNull()
            assertThat(returnedCoins).hasSize(3)
            assertThat(returnedCoins?.get(0)?.id).isEqualTo("coin2") // Non-null comes first

            awaitComplete()
        }
    }

    @Test
    fun `invoke should handle repository error`() = runTest {
        // Given
        val exception = Exception("Database error")
        coEvery {
            favoritesRepository.getFavoriteCoins()
        } returns Result.failure(exception)
        val params = GetAllFavoritesUseCase.Params(sortOrder = SortOrder.NAME_ASC)

        // When
        getAllFavoritesUseCase(params).test {
            // Then
            val result = awaitItem()
            assertThat(result.isFailure).isTrue()
            assertThat(result.exceptionOrNull()).isEqualTo(exception)

            awaitComplete()
        }
    }

    @Test
    fun `invoke should handle repository exception`() = runTest {
        // Given
        val exception = RuntimeException("Unexpected error")
        coEvery {
            favoritesRepository.getFavoriteCoins()
        } returns Result.failure(exception)
        val params = GetAllFavoritesUseCase.Params(sortOrder = SortOrder.NAME_ASC)

        // When
        getAllFavoritesUseCase(params).test {
            // Then - The use case catches exceptions and wraps them in Result.failure
            val result = awaitItem()
            assertThat(result.isFailure).isTrue()
            assertThat(result.exceptionOrNull()).isInstanceOf(RuntimeException::class.java)

            awaitComplete()
        }
    }

    private fun createTestCoins(): List<Coin> {
        return listOf(
            createCoin(
                id = "bitcoin",
                name = "Bitcoin",
                symbol = "BTC",
                currentPrice = 50000.0,
                marketCap = 1000000000L,
                priceChangePercentage24h = 2.0
            ),
            createCoin(
                id = "ethereum",
                name = "Ethereum",
                symbol = "ETH",
                currentPrice = 3000.0,
                marketCap = 500000000L,
                priceChangePercentage24h = 5.0
            ),
            createCoin(
                id = "cardano",
                name = "Cardano",
                symbol = "ADA",
                currentPrice = 1.0,
                marketCap = 10000000L,
                priceChangePercentage24h = -3.0
            )
        )
    }

    private fun createCoin(
        id: String = "test",
        name: String = "Test Coin",
        symbol: String = "TEST",
        currentPrice: Double? = 1.0,
        marketCap: Long? = 1000000L,
        priceChangePercentage24h: Double? = 0.0
    ): Coin {
        return Coin(
            id = id,
            symbol = symbol,
            name = name,
            image = "https://example.com/image.png",
            currentPrice = currentPrice,
            marketCap = marketCap,
            marketCapRank = 1,
            priceChangePercentage24h = priceChangePercentage24h,
        )
    }
}

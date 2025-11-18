package com.midas.features.favorites.domain.usecase

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.midas.features.favorites.domain.repository.FavoritesRepository
import com.midas.features.home.domain.model.Coin
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class ToggleFavoriteUseCaseTest {

    private lateinit var favoritesRepository: FavoritesRepository
    private lateinit var toggleFavoriteUseCase: ToggleFavoriteUseCase

    @Before
    fun setup() {
        favoritesRepository = mockk()
        toggleFavoriteUseCase = ToggleFavoriteUseCase(favoritesRepository)
    }

    @Test
    fun `invoke with non-favorite coin should add to favorites`() = runTest {
        // Given
        val coin = createTestCoin()
        coEvery {
            favoritesRepository.isFavorite(coin.id)
        } returns Result.success(false)
        coEvery {
            favoritesRepository.addToFavorites(coin)
        } returns Result.success(Unit)

        // When
        toggleFavoriteUseCase(ToggleFavoriteUseCase.Params(coin)).test {
            // Then
            val result = awaitItem()
            assertThat(result.isSuccess).isTrue()
            assertThat(result.getOrNull()).isEqualTo(ToggleFavoriteUseCase.FavoriteAction.ADDED)

            awaitComplete()
        }

        coVerify(exactly = 1) { favoritesRepository.isFavorite(coin.id) }
        coVerify(exactly = 1) { favoritesRepository.addToFavorites(coin) }
        coVerify(exactly = 0) { favoritesRepository.removeFromFavorites(any()) }
    }

    @Test
    fun `invoke with favorite coin should remove from favorites`() = runTest {
        // Given
        val coin = createTestCoin()
        coEvery {
            favoritesRepository.isFavorite(coin.id)
        } returns Result.success(true)
        coEvery {
            favoritesRepository.removeFromFavorites(coin.id)
        } returns Result.success(Unit)

        // When
        toggleFavoriteUseCase(ToggleFavoriteUseCase.Params(coin)).test {
            // Then
            val result = awaitItem()
            assertThat(result.isSuccess).isTrue()
            assertThat(result.getOrNull()).isEqualTo(ToggleFavoriteUseCase.FavoriteAction.REMOVED)

            awaitComplete()
        }

        coVerify(exactly = 1) { favoritesRepository.isFavorite(coin.id) }
        coVerify(exactly = 1) { favoritesRepository.removeFromFavorites(coin.id) }
        coVerify(exactly = 0) { favoritesRepository.addToFavorites(any()) }
    }

    @Test
    fun `invoke should handle isFavorite error gracefully`() = runTest {
        // Given
        val coin = createTestCoin()
        val exception = RuntimeException("Database error")
        coEvery {
            favoritesRepository.isFavorite(coin.id)
        } returns Result.failure(exception)

        // When
        toggleFavoriteUseCase(ToggleFavoriteUseCase.Params(coin)).test {
            // Then
            val result = awaitItem()
            assertThat(result.isFailure).isTrue()
            assertThat(result.exceptionOrNull()).isEqualTo(exception)

            awaitComplete()
        }

        coVerify(exactly = 1) { favoritesRepository.isFavorite(coin.id) }
        coVerify(exactly = 0) { favoritesRepository.addToFavorites(any()) }
        coVerify(exactly = 0) { favoritesRepository.removeFromFavorites(any()) }
    }

    @Test
    fun `invoke should handle isFavorite exception gracefully`() = runTest {
        // Given
        val coin = createTestCoin()
        val exception = RuntimeException("Unexpected error")
        coEvery {
            favoritesRepository.isFavorite(coin.id)
        } returns Result.failure(exception)

        // When
        toggleFavoriteUseCase(ToggleFavoriteUseCase.Params(coin)).test {
            // Then - The use case catches exceptions and wraps them in Result.failure
            val result = awaitItem()
            assertThat(result.isFailure).isTrue()
            assertThat(result.exceptionOrNull()).isInstanceOf(RuntimeException::class.java)

            awaitComplete()
        }

        coVerify(exactly = 1) { favoritesRepository.isFavorite(coin.id) }
    }

    @Test
    fun `invoke should handle addToFavorites failure`() = runTest {
        // Given
        val coin = createTestCoin()
        val exception = Exception("Add failed")
        coEvery {
            favoritesRepository.isFavorite(coin.id)
        } returns Result.success(false)
        coEvery {
            favoritesRepository.addToFavorites(coin)
        } returns Result.failure(exception)

        // When
        toggleFavoriteUseCase(ToggleFavoriteUseCase.Params(coin)).test {
            // Then
            val result = awaitItem()
            assertThat(result.isFailure).isTrue()
            assertThat(result.exceptionOrNull()).isEqualTo(exception)

            awaitComplete()
        }

        coVerify(exactly = 1) { favoritesRepository.isFavorite(coin.id) }
        coVerify(exactly = 1) { favoritesRepository.addToFavorites(coin) }
    }

    @Test
    fun `invoke should handle addToFavorites exception`() = runTest {
        // Given
        val coin = createTestCoin()
        val exception = Exception("Add exception")
        coEvery {
            favoritesRepository.isFavorite(coin.id)
        } returns Result.success(false)
        coEvery {
            favoritesRepository.addToFavorites(coin)
        } returns Result.failure(exception)

        // When
        toggleFavoriteUseCase(ToggleFavoriteUseCase.Params(coin)).test {
            // Then
            val result = awaitItem()
            assertThat(result.isFailure).isTrue()
            assertThat(result.exceptionOrNull()).isInstanceOf(Exception::class.java)

            awaitComplete()
        }

        coVerify(exactly = 1) { favoritesRepository.isFavorite(coin.id) }
    }

    @Test
    fun `invoke should handle removeFromFavorites failure`() = runTest {
        // Given
        val coin = createTestCoin()
        val exception = Exception("Remove failed")
        coEvery {
            favoritesRepository.isFavorite(coin.id)
        } returns Result.success(true)
        coEvery {
            favoritesRepository.removeFromFavorites(coin.id)
        } returns Result.failure(exception)

        // When
        toggleFavoriteUseCase(ToggleFavoriteUseCase.Params(coin)).test {
            // Then
            val result = awaitItem()
            assertThat(result.isFailure).isTrue()
            assertThat(result.exceptionOrNull()).isEqualTo(exception)

            awaitComplete()
        }

        coVerify(exactly = 1) { favoritesRepository.isFavorite(coin.id) }
        coVerify(exactly = 1) { favoritesRepository.removeFromFavorites(coin.id) }
    }

    @Test
    fun `invoke should handle removeFromFavorites exception`() = runTest {
        // Given
        val coin = createTestCoin()
        val exception = Exception("Remove exception")
        coEvery {
            favoritesRepository.isFavorite(coin.id)
        } returns Result.success(true)
        coEvery {
            favoritesRepository.removeFromFavorites(coin.id)
        } returns Result.failure(exception)

        // When
        toggleFavoriteUseCase(ToggleFavoriteUseCase.Params(coin)).test {
            // Then
            val result = awaitItem()
            assertThat(result.isFailure).isTrue()
            assertThat(result.exceptionOrNull()).isInstanceOf(Exception::class.java)

            awaitComplete()
        }

        coVerify(exactly = 1) { favoritesRepository.isFavorite(coin.id) }
    }

    @Test
    fun `invoke multiple times should toggle correctly`() = runTest {
        // Given
        val coin = createTestCoin()

        // First toggle: Add
        coEvery {
            favoritesRepository.isFavorite(coin.id)
        } returns Result.success(false)
        coEvery {
            favoritesRepository.addToFavorites(coin)
        } returns Result.success(Unit)

        // When
        toggleFavoriteUseCase(ToggleFavoriteUseCase.Params(coin)).test {
            // Then
            val result = awaitItem()
            assertThat(result.isSuccess).isTrue()
            assertThat(result.getOrNull()).isEqualTo(ToggleFavoriteUseCase.FavoriteAction.ADDED)
            awaitComplete()
        }

        // Second toggle: Remove
        coEvery {
            favoritesRepository.isFavorite(coin.id)
        } returns Result.success(true)
        coEvery {
            favoritesRepository.removeFromFavorites(coin.id)
        } returns Result.success(Unit)

        // When
        toggleFavoriteUseCase(ToggleFavoriteUseCase.Params(coin)).test {
            // Then
            val result = awaitItem()
            assertThat(result.isSuccess).isTrue()
            assertThat(result.getOrNull()).isEqualTo(ToggleFavoriteUseCase.FavoriteAction.REMOVED)
            awaitComplete()
        }

        coVerify(exactly = 2) { favoritesRepository.isFavorite(coin.id) }
        coVerify(exactly = 1) { favoritesRepository.addToFavorites(coin) }
        coVerify(exactly = 1) { favoritesRepository.removeFromFavorites(coin.id) }
    }

    private fun createTestCoin(
        id: String = "bitcoin",
        name: String = "Bitcoin",
        symbol: String = "BTC"
    ): Coin {
        return Coin(
            id = id,
            symbol = symbol,
            name = name,
            image = "https://example.com/image.png",
            currentPrice = 50000.0,
            marketCap = 1000000000L,
            marketCapRank = 1,
            priceChangePercentage24h = 2.0,
        )
    }
}

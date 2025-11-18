package com.midas.features.favorites.domain.usecase

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.midas.features.favorites.domain.repository.FavoritesRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class IsFavoriteUseCaseTest {

    private lateinit var favoritesRepository: FavoritesRepository
    private lateinit var isFavoriteUseCase: IsFavoriteUseCase

    @Before
    fun setup() {
        favoritesRepository = mockk()
        isFavoriteUseCase = IsFavoriteUseCase(favoritesRepository)
    }

    @Test
    fun `invoke with favorite coin should return true`() = runTest {
        // Given
        val coinId = "bitcoin"
        val params = IsFavoriteUseCase.Params(coinId = coinId)

        coEvery {
            favoritesRepository.isFavorite(coinId)
        } returns Result.success(true)

        // When
        isFavoriteUseCase(params).test {
            // Then
            val result = awaitItem()
            assertThat(result.isSuccess).isTrue()
            assertThat(result.getOrNull()).isTrue()

            awaitComplete()
        }

        coVerify(exactly = 1) {
            favoritesRepository.isFavorite(coinId)
        }
    }

    @Test
    fun `invoke with non-favorite coin should return false`() = runTest {
        // Given
        val coinId = "ethereum"
        val params = IsFavoriteUseCase.Params(coinId = coinId)

        coEvery {
            favoritesRepository.isFavorite(coinId)
        } returns Result.success(false)

        // When
        isFavoriteUseCase(params).test {
            // Then
            val result = awaitItem()
            assertThat(result.isSuccess).isTrue()
            assertThat(result.getOrNull()).isFalse()

            awaitComplete()
        }

        coVerify(exactly = 1) {
            favoritesRepository.isFavorite(coinId)
        }
    }

    @Test
    fun `invoke should pass correct coinId to repository`() = runTest {
        // Given
        val coinId = "cardano"
        val params = IsFavoriteUseCase.Params(coinId = coinId)

        coEvery {
            favoritesRepository.isFavorite(coinId)
        } returns Result.success(true)

        // When
        isFavoriteUseCase(params).test {
            // Then
            val result = awaitItem()
            assertThat(result.isSuccess).isTrue()
            awaitComplete()
        }

        coVerify(exactly = 1) {
            favoritesRepository.isFavorite(coinId)
        }
    }

    @Test
    fun `invoke with empty coinId should still call repository`() = runTest {
        // Given
        val coinId = ""
        val params = IsFavoriteUseCase.Params(coinId = coinId)

        coEvery {
            favoritesRepository.isFavorite(coinId)
        } returns Result.success(false)

        // When
        isFavoriteUseCase(params).test {
            // Then
            val result = awaitItem()
            assertThat(result.isSuccess).isTrue()
            assertThat(result.getOrNull()).isFalse()

            awaitComplete()
        }

        coVerify(exactly = 1) {
            favoritesRepository.isFavorite(coinId)
        }
    }

    @Test
    fun `invoke multiple times with same coinId should call repository each time`() = runTest {
        // Given
        val coinId = "bitcoin"
        val params = IsFavoriteUseCase.Params(coinId = coinId)

        coEvery {
            favoritesRepository.isFavorite(coinId)
        } returns Result.success(true)

        // When - First call
        isFavoriteUseCase(params).test {
            assertThat(awaitItem().getOrNull()).isTrue()
            awaitComplete()
        }

        // When - Second call
        isFavoriteUseCase(params).test {
            assertThat(awaitItem().getOrNull()).isTrue()
            awaitComplete()
        }

        // Then
        coVerify(exactly = 2) {
            favoritesRepository.isFavorite(coinId)
        }
    }

    @Test
    fun `invoke with different coinIds should call repository with correct IDs`() = runTest {
        // Given
        val coinId1 = "bitcoin"
        val coinId2 = "ethereum"
        val params1 = IsFavoriteUseCase.Params(coinId = coinId1)
        val params2 = IsFavoriteUseCase.Params(coinId = coinId2)

        coEvery {
            favoritesRepository.isFavorite(coinId1)
        } returns Result.success(true)

        coEvery {
            favoritesRepository.isFavorite(coinId2)
        } returns Result.success(false)

        // When
        isFavoriteUseCase(params1).test {
            assertThat(awaitItem().getOrNull()).isTrue()
            awaitComplete()
        }

        isFavoriteUseCase(params2).test {
            assertThat(awaitItem().getOrNull()).isFalse()
            awaitComplete()
        }

        // Then
        coVerify(exactly = 1) { favoritesRepository.isFavorite(coinId1) }
        coVerify(exactly = 1) { favoritesRepository.isFavorite(coinId2) }
    }

    @Test
    fun `invoke should handle repository error gracefully`() = runTest {
        // Given
        val coinId = "bitcoin"
        val params = IsFavoriteUseCase.Params(coinId = coinId)
        val exception = Exception("Database error")

        coEvery {
            favoritesRepository.isFavorite(coinId)
        } returns Result.failure(exception)

        // When
        isFavoriteUseCase(params).test {
            // Then
            val result = awaitItem()
            assertThat(result.isFailure).isTrue()
            assertThat(result.exceptionOrNull()).isEqualTo(exception)

            awaitComplete()
        }

        coVerify(exactly = 1) {
            favoritesRepository.isFavorite(coinId)
        }
    }

    @Test
    fun `invoke should handle repository exception`() = runTest {
        // Given
        val coinId = "bitcoin"
        val params = IsFavoriteUseCase.Params(coinId = coinId)
        val exception = RuntimeException("Unexpected error")

        coEvery {
            favoritesRepository.isFavorite(coinId)
        } returns Result.failure(exception)

        // When
        isFavoriteUseCase(params).test {
            // Then
            val result = awaitItem()
            assertThat(result.isFailure).isTrue()
            assertThat(result.exceptionOrNull()).isInstanceOf(RuntimeException::class.java)
            assertThat(result.exceptionOrNull()?.message).isEqualTo("Unexpected error")

            awaitComplete()
        }

        coVerify(exactly = 1) {
            favoritesRepository.isFavorite(coinId)
        }
    }

    @Test
    fun `invoke should handle database connection error`() = runTest {
        // Given
        val coinId = "bitcoin"
        val params = IsFavoriteUseCase.Params(coinId = coinId)
        val exception = Exception("Database connection failed")

        coEvery {
            favoritesRepository.isFavorite(coinId)
        } returns Result.failure(exception)

        // When
        isFavoriteUseCase(params).test {
            // Then
            val result = awaitItem()
            assertThat(result.isFailure).isTrue()
            assertThat(result.exceptionOrNull()?.message).contains("Database connection")

            awaitComplete()
        }
    }

    @Test
    fun `invoke with special characters in coinId should work correctly`() = runTest {
        // Given
        val coinId = "bitcoin-cash-sv"
        val params = IsFavoriteUseCase.Params(coinId = coinId)

        coEvery {
            favoritesRepository.isFavorite(coinId)
        } returns Result.success(true)

        // When
        isFavoriteUseCase(params).test {
            // Then
            val result = awaitItem()
            assertThat(result.isSuccess).isTrue()
            assertThat(result.getOrNull()).isTrue()

            awaitComplete()
        }

        coVerify(exactly = 1) {
            favoritesRepository.isFavorite(coinId)
        }
    }

    @Test
    fun `invoke with very long coinId should work correctly`() = runTest {
        // Given
        val coinId = "a".repeat(1000)
        val params = IsFavoriteUseCase.Params(coinId = coinId)

        coEvery {
            favoritesRepository.isFavorite(coinId)
        } returns Result.success(false)

        // When
        isFavoriteUseCase(params).test {
            // Then
            val result = awaitItem()
            assertThat(result.isSuccess).isTrue()
            assertThat(result.getOrNull()).isFalse()

            awaitComplete()
        }

        coVerify(exactly = 1) {
            favoritesRepository.isFavorite(coinId)
        }
    }

    @Test
    fun `invoke should not cache results between calls`() = runTest {
        // Given
        val coinId = "bitcoin"
        val params = IsFavoriteUseCase.Params(coinId = coinId)

        // First call returns true
        coEvery {
            favoritesRepository.isFavorite(coinId)
        } returns Result.success(true)

        // When - First call
        isFavoriteUseCase(params).test {
            assertThat(awaitItem().getOrNull()).isTrue()
            awaitComplete()
        }

        // Change mock to return false
        coEvery {
            favoritesRepository.isFavorite(coinId)
        } returns Result.success(false)

        // When - Second call
        isFavoriteUseCase(params).test {
            // Then - Should reflect the new state
            assertThat(awaitItem().getOrNull()).isFalse()
            awaitComplete()
        }

        coVerify(exactly = 2) {
            favoritesRepository.isFavorite(coinId)
        }
    }
}

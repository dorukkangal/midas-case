package com.midas.features.favorites.domain.usecase

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.midas.features.favorites.domain.repository.FavoritesRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class ClearAllFavoritesUseCaseTest {

    private lateinit var favoritesRepository: FavoritesRepository
    private lateinit var clearAllFavoritesUseCase: ClearAllFavoritesUseCase

    @Before
    fun setup() {
        favoritesRepository = mockk()
        clearAllFavoritesUseCase = ClearAllFavoritesUseCase(favoritesRepository)
    }

    @Test
    fun `invoke should clear all favorites successfully`() = runTest {
        // Given
        coEvery {
            favoritesRepository.clearAllFavorites()
        } returns flow { emit(Result.success(Unit)) }

        // When
        clearAllFavoritesUseCase().test {
            // Then
            val result = awaitItem()
            assertThat(result.isSuccess).isTrue()
            assertThat(result.getOrNull()).isEqualTo(Unit)

            awaitComplete()
        }

        coVerify(exactly = 1) {
            favoritesRepository.clearAllFavorites()
        }
    }

    @Test
    fun `invoke should call repository clearAllFavorites method`() = runTest {
        // Given
        coEvery {
            favoritesRepository.clearAllFavorites()
        } returns flow { emit(Result.success(Unit)) }

        // When
        clearAllFavoritesUseCase().test {
            awaitItem()
            awaitComplete()
        }

        // Then
        coVerify(exactly = 1) {
            favoritesRepository.clearAllFavorites()
        }
    }

    @Test
    fun `invoke multiple times should call repository each time`() = runTest {
        // Given
        coEvery {
            favoritesRepository.clearAllFavorites()
        } returns flow { emit(Result.success(Unit)) }

        // When - First call
        clearAllFavoritesUseCase().test {
            assertThat(awaitItem().isSuccess).isTrue()
            awaitComplete()
        }

        // When - Second call
        clearAllFavoritesUseCase().test {
            assertThat(awaitItem().isSuccess).isTrue()
            awaitComplete()
        }

        // When - Third call
        clearAllFavoritesUseCase().test {
            assertThat(awaitItem().isSuccess).isTrue()
            awaitComplete()
        }

        // Then
        coVerify(exactly = 3) {
            favoritesRepository.clearAllFavorites()
        }
    }

    @Test
    fun `invoke should handle repository error gracefully`() = runTest {
        // Given
        val exception = Exception("Failed to clear favorites")
        coEvery {
            favoritesRepository.clearAllFavorites()
        } returns flow { emit(Result.failure(exception)) }

        // When
        clearAllFavoritesUseCase().test {
            // Then
            val result = awaitItem()
            assertThat(result.isFailure).isTrue()
            assertThat(result.exceptionOrNull()).isEqualTo(exception)

            awaitComplete()
        }

        coVerify(exactly = 1) {
            favoritesRepository.clearAllFavorites()
        }
    }

    @Test
    fun `invoke should handle database error`() = runTest {
        // Given
        val exception = Exception("Database error occurred")
        coEvery {
            favoritesRepository.clearAllFavorites()
        } returns flow { emit(Result.failure(exception)) }

        // When
        clearAllFavoritesUseCase().test {
            // Then
            val result = awaitItem()
            assertThat(result.isFailure).isTrue()
            assertThat(result.exceptionOrNull()?.message).contains("Database error")

            awaitComplete()
        }
    }

    @Test
    fun `invoke should handle repository exception`() = runTest {
        // Given
        val exception = RuntimeException("Unexpected error")
        coEvery {
            favoritesRepository.clearAllFavorites()
        } returns flow { throw exception }

        // When
        try {
            clearAllFavoritesUseCase().test {
                awaitError()
            }
        } catch (e: Exception) {
            // Then
            assertThat(e).isInstanceOf(RuntimeException::class.java)
            assertThat(e.message).isEqualTo("Unexpected error")
        }

        coVerify(exactly = 1) {
            favoritesRepository.clearAllFavorites()
        }
    }

    @Test
    fun `invoke should handle SQL exception`() = runTest {
        // Given
        val exception = Exception("SQL constraint violation")
        coEvery {
            favoritesRepository.clearAllFavorites()
        } returns flow { emit(Result.failure(exception)) }

        // When
        clearAllFavoritesUseCase().test {
            // Then
            val result = awaitItem()
            assertThat(result.isFailure).isTrue()
            assertThat(result.exceptionOrNull()?.message).contains("SQL")

            awaitComplete()
        }
    }

    @Test
    fun `invoke should handle empty favorites table gracefully`() = runTest {
        // Given - Repository returns success even if table is already empty
        coEvery {
            favoritesRepository.clearAllFavorites()
        } returns flow { emit(Result.success(Unit)) }

        // When
        clearAllFavoritesUseCase().test {
            // Then
            val result = awaitItem()
            assertThat(result.isSuccess).isTrue()

            awaitComplete()
        }
    }

    @Test
    fun `invoke should propagate repository success correctly`() = runTest {
        // Given
        coEvery {
            favoritesRepository.clearAllFavorites()
        } returns flow { emit(Result.success(Unit)) }

        // When
        clearAllFavoritesUseCase().test {
            // Then
            val result = awaitItem()
            assertThat(result.isSuccess).isTrue()
            assertThat(result.isFailure).isFalse()
            assertThat(result.getOrNull()).isNotNull()

            awaitComplete()
        }
    }

    @Test
    fun `invoke should propagate repository failure correctly`() = runTest {
        // Given
        val exception = Exception("Clear operation failed")
        coEvery {
            favoritesRepository.clearAllFavorites()
        } returns flow { emit(Result.failure(exception)) }

        // When
        clearAllFavoritesUseCase().test {
            // Then
            val result = awaitItem()
            assertThat(result.isFailure).isTrue()
            assertThat(result.isSuccess).isFalse()
            assertThat(result.exceptionOrNull()).isNotNull()

            awaitComplete()
        }
    }

    @Test
    fun `invoke should handle timeout exception`() = runTest {
        // Given
        val exception = Exception("Operation timeout")
        coEvery {
            favoritesRepository.clearAllFavorites()
        } returns flow { emit(Result.failure(exception)) }

        // When
        clearAllFavoritesUseCase().test {
            // Then
            val result = awaitItem()
            assertThat(result.isFailure).isTrue()
            assertThat(result.exceptionOrNull()?.message).contains("timeout")

            awaitComplete()
        }
    }

    @Test
    fun `invoke should handle permission denied error`() = runTest {
        // Given
        val exception = SecurityException("Permission denied")
        coEvery {
            favoritesRepository.clearAllFavorites()
        } returns flow { emit(Result.failure(exception)) }

        // When
        clearAllFavoritesUseCase().test {
            // Then
            val result = awaitItem()
            assertThat(result.isFailure).isTrue()
            assertThat(result.exceptionOrNull()).isInstanceOf(SecurityException::class.java)

            awaitComplete()
        }
    }

    @Test
    fun `invoke should not modify repository state on failure`() = runTest {
        // Given
        val exception = Exception("Operation failed")
        coEvery {
            favoritesRepository.clearAllFavorites()
        } returns flow { emit(Result.failure(exception)) }

        // When
        clearAllFavoritesUseCase().test {
            val result = awaitItem()
            assertThat(result.isFailure).isTrue()
            awaitComplete()
        }

        // Then - Repository was called exactly once, no retries
        coVerify(exactly = 1) {
            favoritesRepository.clearAllFavorites()
        }
    }

    @Test
    fun `invoke should complete flow after emitting result`() = runTest {
        // Given
        coEvery {
            favoritesRepository.clearAllFavorites()
        } returns flow { emit(Result.success(Unit)) }

        // When
        clearAllFavoritesUseCase().test {
            // Then
            awaitItem() // First emission
            awaitComplete() // Flow should complete
        }
    }

    @Test
    fun `invoke should handle concurrent calls independently`() = runTest {
        // Given
        coEvery {
            favoritesRepository.clearAllFavorites()
        } returns flow { emit(Result.success(Unit)) }

        // When - Multiple concurrent calls (simulated sequentially in test)
        val results = mutableListOf<Result<Unit>>()

        clearAllFavoritesUseCase().test {
            results.add(awaitItem())
            awaitComplete()
        }

        clearAllFavoritesUseCase().test {
            results.add(awaitItem())
            awaitComplete()
        }

        // Then - All calls should succeed independently
        assertThat(results).hasSize(2)
        assertThat(results.all { it.isSuccess }).isTrue()

        coVerify(exactly = 2) {
            favoritesRepository.clearAllFavorites()
        }
    }
}

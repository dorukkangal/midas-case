package com.midas.features.favorites.ui

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.midas.features.favorites.domain.usecase.ClearAllFavoritesUseCase
import com.midas.features.favorites.domain.usecase.GetAllFavoritesUseCase
import com.midas.features.favorites.domain.usecase.ToggleFavoriteUseCase
import com.midas.features.favorites.ui.model.CoinUiModel
import com.midas.features.favorites.ui.model.SortOrderUiModel
import com.midas.features.favorites.ui.state.FavoritesUiAction
import com.midas.features.home.domain.model.Coin
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FavoritesViewModelTest {

    private lateinit var getAllFavoritesUseCase: GetAllFavoritesUseCase
    private lateinit var toggleFavoriteUseCase: ToggleFavoriteUseCase
    private lateinit var clearAllFavoritesUseCase: ClearAllFavoritesUseCase
    private lateinit var viewModel: FavoritesViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getAllFavoritesUseCase = mockk()
        toggleFavoriteUseCase = mockk()
        clearAllFavoritesUseCase = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init loads favorites successfully`() = runTest {
        // Given
        val mockFavorites = listOf(createMockCoin(), createMockCoin(id = "ethereum"))

        coEvery {
            getAllFavoritesUseCase(any())
        } returns flow { emit(Result.success(mockFavorites)) }

        // When
        viewModel = FavoritesViewModel(
            getAllFavoritesUseCase,
            toggleFavoriteUseCase,
            clearAllFavoritesUseCase
        )
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.favorites).hasSize(2)
            assertThat(state.isFavoritesLoading).isFalse()
            assertThat(state.loadFavoritesError).isNull()
        }

        coVerify(exactly = 1) { getAllFavoritesUseCase(any()) }
    }

    @Test
    fun `handleAction LoadFavorites reloads favorites`() = runTest {
        // Given
        setupInitialMocks()
        viewModel = FavoritesViewModel(
            getAllFavoritesUseCase,
            toggleFavoriteUseCase,
            clearAllFavoritesUseCase
        )
        advanceUntilIdle()

        val refreshedFavorites = listOf(
            createMockCoin(),
            createMockCoin(id = "ethereum"),
            createMockCoin(id = "cardano")
        )
        coEvery {
            getAllFavoritesUseCase(any())
        } returns flow { emit(Result.success(refreshedFavorites)) }

        // When
        viewModel.handleAction(FavoritesUiAction.LoadFavorites)
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.favorites).hasSize(3)
            assertThat(state.isFavoritesLoading).isFalse()
        }

        coVerify(atLeast = 2) { getAllFavoritesUseCase(any()) }
    }

    @Test
    fun `handleAction LoadFavorites handles error correctly`() = runTest {
        // Given
        val exception = Exception("Database error")

        coEvery {
            getAllFavoritesUseCase(any())
        } returns flow { emit(Result.failure(exception)) }

        // When
        viewModel = FavoritesViewModel(
            getAllFavoritesUseCase,
            toggleFavoriteUseCase,
            clearAllFavoritesUseCase
        )
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.isFavoritesLoading).isFalse()
            assertThat(state.loadFavoritesError).isNotNull()
            assertThat(state.loadFavoritesError?.message).isEqualTo("Database error")
        }
    }

    @Test
    fun `handleAction ChangeSortOrder changes sort and reloads`() = runTest {
        // Given
        setupInitialMocks()
        viewModel = FavoritesViewModel(
            getAllFavoritesUseCase,
            toggleFavoriteUseCase,
            clearAllFavoritesUseCase
        )
        advanceUntilIdle()

        // When
        viewModel.handleAction(FavoritesUiAction.ChangeSortOrder(SortOrderUiModel.PRICE_DESC))
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.sortOrder).isEqualTo(SortOrderUiModel.PRICE_DESC)
        }

        coVerify(atLeast = 2) { getAllFavoritesUseCase(any()) }
    }

    @Test
    fun `handleAction RemoveFavorite removes coin and reloads list`() = runTest {
        // Given
        setupInitialMocks()
        viewModel = FavoritesViewModel(
            getAllFavoritesUseCase,
            toggleFavoriteUseCase,
            clearAllFavoritesUseCase
        )
        advanceUntilIdle()

        val coinToRemove = createMockUiCoin()

        coEvery {
            toggleFavoriteUseCase(any())
        } returns flow {
            emit(Result.success(ToggleFavoriteUseCase.FavoriteAction.REMOVED))
        }

        // When
        viewModel.handleAction(FavoritesUiAction.RemoveFavorite(coinToRemove))
        advanceUntilIdle()

        // Then
        coVerify(exactly = 1) { toggleFavoriteUseCase(any()) }
        coVerify(atLeast = 2) { getAllFavoritesUseCase(any()) }
    }

    @Test
    fun `handleAction RemoveFavorite handles error correctly`() = runTest {
        // Given
        setupInitialMocks()
        viewModel = FavoritesViewModel(
            getAllFavoritesUseCase,
            toggleFavoriteUseCase,
            clearAllFavoritesUseCase
        )
        advanceUntilIdle()

        val coinToRemove = createMockUiCoin()
        val exception = Exception("Remove failed")

        coEvery {
            toggleFavoriteUseCase(any())
        } returns flow { emit(Result.failure(exception)) }

        // When
        viewModel.handleAction(FavoritesUiAction.RemoveFavorite(coinToRemove))
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.isUpdateFavoriteLoading).isFalse()
            assertThat(state.updateFavoriteError).isNotNull()
        }
    }

    @Test
    fun `handleAction ClearAllFavorites clears all favorites`() = runTest {
        // Given
        setupInitialMocks()
        viewModel = FavoritesViewModel(
            getAllFavoritesUseCase,
            toggleFavoriteUseCase,
            clearAllFavoritesUseCase
        )
        advanceUntilIdle()

        coEvery {
            clearAllFavoritesUseCase()
        } returns flow { emit(Result.success(Unit)) }

        // When
        viewModel.handleAction(FavoritesUiAction.ClearAllFavorites)
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.favorites).isEmpty()
            assertThat(state.isFavoritesLoading).isFalse()
            assertThat(state.loadFavoritesError).isNull()
        }

        coVerify(exactly = 1) { clearAllFavoritesUseCase() }
    }

    @Test
    fun `handleAction ClearAllFavorites handles error correctly`() = runTest {
        // Given
        setupInitialMocks()
        viewModel = FavoritesViewModel(
            getAllFavoritesUseCase,
            toggleFavoriteUseCase,
            clearAllFavoritesUseCase
        )
        advanceUntilIdle()

        val exception = Exception("Clear failed")
        coEvery {
            clearAllFavoritesUseCase()
        } returns flow { emit(Result.failure(exception)) }

        // When
        viewModel.handleAction(FavoritesUiAction.ClearAllFavorites)
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.isFavoritesLoading).isFalse()
            assertThat(state.loadFavoritesError).isNotNull()
            assertThat(state.loadFavoritesError?.message).isEqualTo("Clear failed")
        }
    }

    @Test
    fun `handleAction DismissError clears all errors`() = runTest {
        // Given
        val exception = Exception("Error")

        coEvery {
            getAllFavoritesUseCase(any())
        } returns flow { emit(Result.failure(exception)) }

        viewModel = FavoritesViewModel(
            getAllFavoritesUseCase,
            toggleFavoriteUseCase,
            clearAllFavoritesUseCase
        )
        advanceUntilIdle()

        // When
        viewModel.handleAction(FavoritesUiAction.DismissError)
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.loadFavoritesError).isNull()
            assertThat(state.updateFavoriteError).isNull()
        }
    }

    private fun setupInitialMocks() {
        coEvery {
            getAllFavoritesUseCase(any())
        } returns flow { emit(Result.success(emptyList())) }
    }

    private fun createMockCoin(
        id: String = "bitcoin",
        name: String = "Bitcoin",
        symbol: String = "BTC"
    ) = Coin(
        id = id,
        name = name,
        symbol = symbol,
        image = "https://example.com/$id.png",
        currentPrice = 50000.0,
        marketCap = 1000000000L,
        marketCapRank = 1,
        priceChangePercentage24h = 2.5,
    )

    private fun createMockUiCoin(
        id: String = "bitcoin"
    ) = CoinUiModel(
        id = id,
        name = "Bitcoin",
        symbol = "BTC",
        image = "https://example.com/$id.png",
        currentPrice = null,
        marketCap = null,
        marketCapRank = 1,
        priceChangePercentage24h = null
    )
}

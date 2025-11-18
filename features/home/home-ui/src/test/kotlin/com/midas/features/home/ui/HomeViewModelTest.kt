package com.midas.features.home.ui

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.midas.features.home.domain.model.Coin
import com.midas.features.home.domain.usecase.GetCoinsUseCase
import com.midas.features.home.domain.usecase.GetTrendingCoinsUseCase
import com.midas.features.home.domain.usecase.SearchCoinsUseCase
import com.midas.features.home.ui.state.HomeUiAction
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private lateinit var getCoinsUseCase: GetCoinsUseCase
    private lateinit var getTrendingCoinsUseCase: GetTrendingCoinsUseCase
    private lateinit var searchCoinsUseCase: SearchCoinsUseCase
    private lateinit var viewModel: HomeViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getCoinsUseCase = mockk()
        getTrendingCoinsUseCase = mockk()
        searchCoinsUseCase = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init loads coins and trending coins in parallel successfully`() = runTest {
        // Given
        val mockCoins = listOf(createMockCoin())
        val mockTrendingCoins = listOf(createMockCoin(id = "ethereum"))

        coEvery { getCoinsUseCase(any()) } returns flow {
            emit(Result.success(mockCoins))
        }
        coEvery { getTrendingCoinsUseCase() } returns flow {
            emit(Result.success(mockTrendingCoins))
        }

        // When
        viewModel = HomeViewModel(getCoinsUseCase, getTrendingCoinsUseCase, searchCoinsUseCase)
        advanceUntilIdle()

        // Then - Both should be loaded using combine (parallel)
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.coins).hasSize(1)
            assertThat(state.trendingCoins).hasSize(1)
            assertThat(state.isLoading).isFalse()
            assertThat(state.isTrendingLoading).isFalse()
        }

        coVerify(exactly = 1) { getCoinsUseCase(any()) }
        coVerify(exactly = 1) { getTrendingCoinsUseCase() }
    }

    @Test
    fun `handleAction LoadCoins updates state with coins`() = runTest {
        // Given
        val mockCoins = listOf(createMockCoin(), createMockCoin(id = "ethereum"))
        setupInitialMocks()
        viewModel = HomeViewModel(getCoinsUseCase, getTrendingCoinsUseCase, searchCoinsUseCase)
        advanceUntilIdle()

        coEvery { getCoinsUseCase(any()) } returns flow {
            emit(Result.success(mockCoins))
        }

        // When
        viewModel.handleAction(HomeUiAction.LoadCoins)
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.coins).hasSize(2)
            assertThat(state.isLoading).isFalse()
            assertThat(state.loadError).isNull()
        }
    }

    @Test
    fun `handleAction LoadCoins handles error correctly`() = runTest {
        // Given
        val exception = Exception("Network error")
        setupInitialMocks()
        viewModel = HomeViewModel(getCoinsUseCase, getTrendingCoinsUseCase, searchCoinsUseCase)
        advanceUntilIdle()

        coEvery { getCoinsUseCase(any()) } returns flow {
            emit(Result.failure(exception))
        }

        // When
        viewModel.handleAction(HomeUiAction.LoadCoins)
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.isLoading).isFalse()
            assertThat(state.loadError).isNotNull()
            assertThat(state.loadError?.message).isEqualTo("Network error")
        }
    }

    @Test
    fun `handleAction LoadTrendingCoins updates state with trending coins`() = runTest {
        // Given
        val mockTrendingCoins = listOf(
            createMockCoin(id = "bitcoin"),
            createMockCoin(id = "ethereum"),
            createMockCoin(id = "cardano")
        )
        setupInitialMocks()
        viewModel = HomeViewModel(getCoinsUseCase, getTrendingCoinsUseCase, searchCoinsUseCase)
        advanceUntilIdle()

        coEvery { getTrendingCoinsUseCase() } returns flow {
            emit(Result.success(mockTrendingCoins))
        }

        // When
        viewModel.handleAction(HomeUiAction.LoadTrendingCoins)
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.trendingCoins).hasSize(3)
            assertThat(state.isTrendingLoading).isFalse()
            assertThat(state.loadTrendingCoinsError).isNull()
        }
    }

    @Test
    fun `handleAction LoadTrendingCoins handles error correctly`() = runTest {
        // Given
        val exception = Exception("API error")
        setupInitialMocks()
        viewModel = HomeViewModel(getCoinsUseCase, getTrendingCoinsUseCase, searchCoinsUseCase)
        advanceUntilIdle()

        coEvery { getTrendingCoinsUseCase() } returns flow {
            emit(Result.failure(exception))
        }

        // When
        viewModel.handleAction(HomeUiAction.LoadTrendingCoins)
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.isTrendingLoading).isFalse()
            assertThat(state.loadTrendingCoinsError).isNotNull()
            assertThat(state.loadTrendingCoinsError?.message).isEqualTo("API error")
        }
    }

    @Test
    fun `handleAction SearchCoins performs search after debounce`() = runTest {
        // Given
        val mockSearchResults = listOf(createMockCoin(name = "Bitcoin"))
        setupInitialMocks()
        coEvery { searchCoinsUseCase(any()) } returns flow {
            emit(Result.success(mockSearchResults))
        }

        viewModel = HomeViewModel(getCoinsUseCase, getTrendingCoinsUseCase, searchCoinsUseCase)
        advanceUntilIdle()

        // When
        viewModel.handleAction(HomeUiAction.SearchCoins("Bitcoin"))
        advanceTimeBy(301) // Wait for debounce
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.searchResults).hasSize(1)
            assertThat(state.isSearching).isFalse()
        }

        coVerify(exactly = 1) { searchCoinsUseCase("Bitcoin") }
    }

    @Test
    fun `search query debounce prevents multiple rapid calls`() = runTest {
        // Given
        setupInitialMocks()
        coEvery { searchCoinsUseCase(any()) } returns flow {
            emit(Result.success(emptyList()))
        }

        viewModel = HomeViewModel(getCoinsUseCase, getTrendingCoinsUseCase, searchCoinsUseCase)
        advanceUntilIdle()

        // When - Rapid typing
        viewModel.handleAction(HomeUiAction.SearchCoins("B"))
        advanceTimeBy(100)
        viewModel.handleAction(HomeUiAction.SearchCoins("Bi"))
        advanceTimeBy(100)
        viewModel.handleAction(HomeUiAction.SearchCoins("Bit"))
        advanceTimeBy(100)
        viewModel.handleAction(HomeUiAction.SearchCoins("Bitcoin"))
        advanceTimeBy(301)
        advanceUntilIdle()

        // Then - Only last query executed
        coVerify(exactly = 1) { searchCoinsUseCase("Bitcoin") }
        coVerify(exactly = 0) { searchCoinsUseCase("B") }
        coVerify(exactly = 0) { searchCoinsUseCase("Bi") }
        coVerify(exactly = 0) { searchCoinsUseCase("Bit") }
    }

    @Test
    fun `handleAction ClearSearch clears query and results`() = runTest {
        // Given
        setupInitialMocks()
        coEvery { searchCoinsUseCase(any()) } returns flow {
            emit(Result.success(listOf(createMockCoin())))
        }

        viewModel = HomeViewModel(getCoinsUseCase, getTrendingCoinsUseCase, searchCoinsUseCase)
        advanceUntilIdle()

        viewModel.handleAction(HomeUiAction.SearchCoins("Bitcoin"))
        advanceTimeBy(301)
        advanceUntilIdle()

        // When
        viewModel.handleAction(HomeUiAction.ClearSearch)
        advanceTimeBy(301)
        advanceUntilIdle()

        // Then
        viewModel.searchQuery.test {
            assertThat(awaitItem()).isEmpty()
        }

        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.searchResults).isEmpty()
            assertThat(state.isSearching).isFalse()
        }
    }

    @Test
    fun `handleAction RefreshData refreshes both coins and trending in parallel`() = runTest {
        // Given
        val mockCoins = listOf(createMockCoin())
        val mockTrendingCoins = listOf(createMockCoin(id = "ethereum"))

        coEvery { getCoinsUseCase(any()) } returns flow {
            emit(Result.success(mockCoins))
        }
        coEvery { getTrendingCoinsUseCase() } returns flow {
            emit(Result.success(mockTrendingCoins))
        }

        viewModel = HomeViewModel(getCoinsUseCase, getTrendingCoinsUseCase, searchCoinsUseCase)
        advanceUntilIdle()

        // When
        viewModel.handleAction(HomeUiAction.RefreshData)
        advanceUntilIdle()

        // Then - Both should be called at least twice (init + refresh) using combine
        coVerify(atLeast = 2) { getCoinsUseCase(any()) }
        coVerify(atLeast = 2) { getTrendingCoinsUseCase() }
    }

    @Test
    fun `handleAction DismissError clears all errors`() = runTest {
        // Given
        val exception = Exception("Error")
        coEvery { getCoinsUseCase(any()) } returns flow {
            emit(Result.failure(exception))
        }
        coEvery { getTrendingCoinsUseCase() } returns flow {
            emit(Result.failure(exception))
        }

        viewModel = HomeViewModel(getCoinsUseCase, getTrendingCoinsUseCase, searchCoinsUseCase)
        advanceUntilIdle()

        // When
        viewModel.handleAction(HomeUiAction.DismissError)
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.loadError).isNull()
            assertThat(state.loadTrendingCoinsError).isNull()
        }
    }

    @Test
    fun `empty search query clears search results`() = runTest {
        // Given
        setupInitialMocks()
        coEvery { searchCoinsUseCase(any()) } returns flow {
            emit(Result.success(listOf(createMockCoin())))
        }

        viewModel = HomeViewModel(getCoinsUseCase, getTrendingCoinsUseCase, searchCoinsUseCase)
        advanceUntilIdle()

        viewModel.handleAction(HomeUiAction.SearchCoins("Bitcoin"))
        advanceTimeBy(301)
        advanceUntilIdle()

        // When - Search with empty query
        viewModel.handleAction(HomeUiAction.SearchCoins(""))
        advanceTimeBy(301)
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.searchResults).isEmpty()
            assertThat(state.isSearching).isFalse()
        }
    }

    @Test
    fun `search handles error gracefully`() = runTest {
        // Given
        setupInitialMocks()
        coEvery { searchCoinsUseCase(any()) } returns flow {
            emit(Result.failure(Exception("Search error")))
        }

        viewModel = HomeViewModel(getCoinsUseCase, getTrendingCoinsUseCase, searchCoinsUseCase)
        advanceUntilIdle()

        // When
        viewModel.handleAction(HomeUiAction.SearchCoins("Bitcoin"))
        advanceTimeBy(301)
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.searchResults).isEmpty()
            assertThat(state.isSearching).isFalse()
        }
    }

    private fun setupInitialMocks() {
        coEvery { getCoinsUseCase(any()) } returns flow {
            emit(Result.success(emptyList()))
        }
        coEvery { getTrendingCoinsUseCase() } returns flow {
            emit(Result.success(emptyList()))
        }
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
}

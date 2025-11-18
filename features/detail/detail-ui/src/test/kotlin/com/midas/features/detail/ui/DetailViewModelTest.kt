package com.midas.features.detail.ui

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.midas.features.detail.domain.model.CoinDetail
import com.midas.features.detail.domain.model.MarketData
import com.midas.features.detail.domain.usecase.GetCoinDetailUseCase
import com.midas.features.detail.ui.state.DetailUiAction
import com.midas.features.favorites.domain.usecase.IsFavoriteUseCase
import com.midas.features.favorites.domain.usecase.ToggleFavoriteUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
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
class DetailViewModelTest {

    private lateinit var getCoinDetailUseCase: GetCoinDetailUseCase
    private lateinit var isFavoriteUseCase: IsFavoriteUseCase
    private lateinit var toggleFavoriteUseCase: ToggleFavoriteUseCase
    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var viewModel: DetailViewModel

    private val testDispatcher = StandardTestDispatcher()
    private val testCoinId = "bitcoin"

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getCoinDetailUseCase = mockk()
        isFavoriteUseCase = mockk()
        toggleFavoriteUseCase = mockk()
        savedStateHandle = mockk()

        every { savedStateHandle.get<String>("coinId") } returns testCoinId
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is empty before RefreshDetail action`() = runTest {
        // Given
        setupInitialMocks()

        // When
        viewModel = DetailViewModel(
            getCoinDetailUseCase,
            isFavoriteUseCase,
            toggleFavoriteUseCase,
            savedStateHandle
        )
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.coinDetail).isNull()
            assertThat(state.isFavorite).isFalse()
            assertThat(state.isDetailLoading).isFalse()
            assertThat(state.isFavoriteLoading).isFalse()
            assertThat(state.loadDetailError).isNull()
        }

        // No use case should be called on init
        coVerify(exactly = 0) { getCoinDetailUseCase(any()) }
        coVerify(exactly = 0) { isFavoriteUseCase(any()) }
    }

    @Test
    fun `handleAction RefreshDetail loads coin detail and checks favorite status`() = runTest {
        // Given
        val mockCoinDetail = createMockCoinDetail()

        coEvery {
            getCoinDetailUseCase(any())
        } returns flow { emit(Result.success(mockCoinDetail)) }

        coEvery {
            isFavoriteUseCase(any())
        } returns flow { emit(Result.success(false)) }

        viewModel = DetailViewModel(
            getCoinDetailUseCase,
            isFavoriteUseCase,
            toggleFavoriteUseCase,
            savedStateHandle
        )

        // When
        viewModel.handleAction(DetailUiAction.RefreshDetail)
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.coinDetail).isNotNull()
            assertThat(state.coinDetail?.name).isEqualTo("Bitcoin")
            assertThat(state.isFavorite).isFalse()
            assertThat(state.isDetailLoading).isFalse()
            assertThat(state.isFavoriteLoading).isFalse()
            assertThat(state.loadDetailError).isNull()
        }

        coVerify(exactly = 1) { getCoinDetailUseCase(any()) }
        coVerify(exactly = 1) { isFavoriteUseCase(any()) }
    }

    @Test
    fun `handleAction RefreshDetail reloads coin detail on subsequent calls`() = runTest {
        // Given
        val initialDetail = createMockCoinDetail(name = "Bitcoin")
        coEvery {
            getCoinDetailUseCase(any())
        } returns flow { emit(Result.success(initialDetail)) }

        coEvery {
            isFavoriteUseCase(any())
        } returns flow { emit(Result.success(false)) }

        viewModel = DetailViewModel(
            getCoinDetailUseCase,
            isFavoriteUseCase,
            toggleFavoriteUseCase,
            savedStateHandle
        )

        // First load
        viewModel.handleAction(DetailUiAction.RefreshDetail)
        advanceUntilIdle()

        // Update mock for refresh
        val refreshedDetail = createMockCoinDetail(name = "Bitcoin Updated")
        coEvery {
            getCoinDetailUseCase(any())
        } returns flow { emit(Result.success(refreshedDetail)) }

        // When - Refresh
        viewModel.handleAction(DetailUiAction.RefreshDetail)
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.coinDetail?.name).isEqualTo("Bitcoin Updated")
            assertThat(state.isDetailLoading).isFalse()
        }

        // Should be called twice (initial + refresh)
        coVerify(exactly = 2) { getCoinDetailUseCase(any()) }
        coVerify(exactly = 2) { isFavoriteUseCase(any()) }
    }

    @Test
    fun `handleAction RefreshDetail handles error correctly`() = runTest {
        // Given
        val exception = Exception("Network error")

        coEvery {
            getCoinDetailUseCase(any())
        } returns flow { emit(Result.failure(exception)) }

        coEvery {
            isFavoriteUseCase(any())
        } returns flow { emit(Result.success(false)) }

        viewModel = DetailViewModel(
            getCoinDetailUseCase,
            isFavoriteUseCase,
            toggleFavoriteUseCase,
            savedStateHandle
        )

        // When
        viewModel.handleAction(DetailUiAction.RefreshDetail)
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.coinDetail).isNull()
            assertThat(state.isDetailLoading).isFalse()
            assertThat(state.loadDetailError).isNotNull()
            assertThat(state.loadDetailError?.message).isEqualTo("Network error")
        }

        coVerify(exactly = 1) { getCoinDetailUseCase(any()) }
        coVerify(exactly = 1) { isFavoriteUseCase(any()) }
    }

    @Test
    fun `handleAction ToggleFavorite adds coin to favorites when not favorite`() = runTest {
        // Given
        setupInitialMocks()
        viewModel = DetailViewModel(
            getCoinDetailUseCase,
            isFavoriteUseCase,
            toggleFavoriteUseCase,
            savedStateHandle
        )

        // Load initial data first
        viewModel.handleAction(DetailUiAction.RefreshDetail)
        advanceUntilIdle()

        coEvery {
            toggleFavoriteUseCase(any())
        } returns flow {
            emit(Result.success(ToggleFavoriteUseCase.FavoriteAction.ADDED))
        }

        // When
        viewModel.handleAction(DetailUiAction.ToggleFavorite)
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.isFavorite).isTrue()
            assertThat(state.isFavoriteLoading).isFalse()
        }

        coVerify(exactly = 1) { toggleFavoriteUseCase(any()) }
    }

    @Test
    fun `handleAction ToggleFavorite removes coin from favorites when favorite`() = runTest {
        // Given
        val mockCoinDetail = createMockCoinDetail()

        coEvery {
            getCoinDetailUseCase(any())
        } returns flow { emit(Result.success(mockCoinDetail)) }

        coEvery {
            isFavoriteUseCase(any())
        } returns flow { emit(Result.success(true)) }

        viewModel = DetailViewModel(
            getCoinDetailUseCase,
            isFavoriteUseCase,
            toggleFavoriteUseCase,
            savedStateHandle
        )

        // Load initial data first
        viewModel.handleAction(DetailUiAction.RefreshDetail)
        advanceUntilIdle()

        coEvery {
            toggleFavoriteUseCase(any())
        } returns flow {
            emit(Result.success(ToggleFavoriteUseCase.FavoriteAction.REMOVED))
        }

        // When
        viewModel.handleAction(DetailUiAction.ToggleFavorite)
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.isFavorite).isFalse()
            assertThat(state.isFavoriteLoading).isFalse()
        }

        coVerify(exactly = 1) { toggleFavoriteUseCase(any()) }
    }

    @Test
    fun `handleAction ToggleFavorite handles error correctly`() = runTest {
        // Given
        setupInitialMocks()
        viewModel = DetailViewModel(
            getCoinDetailUseCase,
            isFavoriteUseCase,
            toggleFavoriteUseCase,
            savedStateHandle
        )

        // Load initial data first
        viewModel.handleAction(DetailUiAction.RefreshDetail)
        advanceUntilIdle()

        val exception = Exception("Toggle failed")
        coEvery {
            toggleFavoriteUseCase(any())
        } returns flow { emit(Result.failure(exception)) }

        // When
        viewModel.handleAction(DetailUiAction.ToggleFavorite)
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.isFavoriteLoading).isFalse()
            assertThat(state.loadDetailError).isNotNull()
        }

        coVerify(exactly = 1) { toggleFavoriteUseCase(any()) }
    }

    @Test
    fun `handleAction DismissError clears all errors`() = runTest {
        // Given
        val exception = Exception("Error")

        coEvery {
            getCoinDetailUseCase(any())
        } returns flow { emit(Result.failure(exception)) }

        coEvery {
            isFavoriteUseCase(any())
        } returns flow { emit(Result.failure(exception)) }

        viewModel = DetailViewModel(
            getCoinDetailUseCase,
            isFavoriteUseCase,
            toggleFavoriteUseCase,
            savedStateHandle
        )

        // Trigger error by calling RefreshDetail
        viewModel.handleAction(DetailUiAction.RefreshDetail)
        advanceUntilIdle()

        // Verify errors exist
        viewModel.uiState.test {
            val stateWithError = awaitItem()
            assertThat(stateWithError.loadDetailError).isNotNull()
        }

        // When
        viewModel.handleAction(DetailUiAction.DismissError)
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.loadDetailError).isNull()
            assertThat(state.loadFavoriteError).isNull()
        }
    }

    @Test
    fun `handleAction RefreshDetail checks favorite status and updates state`() = runTest {
        // Given
        val mockCoinDetail = createMockCoinDetail()

        coEvery {
            getCoinDetailUseCase(any())
        } returns flow { emit(Result.success(mockCoinDetail)) }

        coEvery {
            isFavoriteUseCase(any())
        } returns flow { emit(Result.success(true)) }

        viewModel = DetailViewModel(
            getCoinDetailUseCase,
            isFavoriteUseCase,
            toggleFavoriteUseCase,
            savedStateHandle
        )

        // When
        viewModel.handleAction(DetailUiAction.RefreshDetail)
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.isFavorite).isTrue()
            assertThat(state.isFavoriteLoading).isFalse()
            assertThat(state.coinDetail).isNotNull()
            assertThat(state.isDetailLoading).isFalse()
        }

        coVerify(exactly = 1) { getCoinDetailUseCase(any()) }
        coVerify(exactly = 1) { isFavoriteUseCase(any()) }
    }

    private fun setupInitialMocks() {
        val mockCoinDetail = createMockCoinDetail()

        coEvery {
            getCoinDetailUseCase(any())
        } returns flow { emit(Result.success(mockCoinDetail)) }

        coEvery {
            isFavoriteUseCase(any())
        } returns flow { emit(Result.success(false)) }
    }

    private fun createMockCoinDetail(
        id: String = "bitcoin",
        name: String = "Bitcoin",
        symbol: String = "btc"
    ) = CoinDetail(
        id = id,
        name = name,
        symbol = symbol,
        description = "Test description",
        image = "https://example.com/$id.png",
        marketCapRank = 1,
        marketData = MarketData(
            currentPrice = 50000.0,
            marketCap = 1000000000L,
            totalVolume = 50000000L,
            high24h = 51000.0,
            low24h = 49000.0,
            priceChangePercentage24h = 2.0,
            priceChangePercentage7d = 5.0,
            priceChangePercentage30d = 10.0,
            circulatingSupply = 19000000.0,
            totalSupply = 21000000.0,
            maxSupply = 21000000.0
        ),
        categories = listOf("Cryptocurrency", "Store of Value"),
        genesisDate = "2009-01-03"
    )
}

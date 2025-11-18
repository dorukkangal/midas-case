package com.midas.features.home.data.repository

import com.google.common.truth.Truth.assertThat
import com.midas.features.home.data.remote.api.CoinApiService
import com.midas.features.home.data.remote.model.CoinResponse
import com.midas.features.home.data.remote.model.SearchCoin
import com.midas.features.home.data.remote.model.SearchResponse
import com.midas.features.home.data.remote.model.TrendingCoin
import com.midas.features.home.data.remote.model.TrendingCoinWrapper
import com.midas.features.home.data.remote.model.TrendingResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class CoinRepositoryImplTest {

    private lateinit var apiService: CoinApiService
    private lateinit var repository: CoinRepositoryImpl

    @Before
    fun setup() {
        apiService = mockk()
        repository = CoinRepositoryImpl(apiService)
    }

    // ==================== getCoins Tests ====================

    @Test
    fun `getCoins returns success with mapped coins`() = runTest {
        // Given
        val mockResponse = listOf(
            createMockCoinResponse("bitcoin", "Bitcoin"),
            createMockCoinResponse("ethereum", "Ethereum")
        )
        coEvery {
            apiService.getCoins(any(), any(), any(), any())
        } returns Result.success(mockResponse)

        // When
        val result = repository.getCoins("usd", "market_cap_desc", 10, 1)

        // Then
        assertThat(result.isSuccess).isTrue()
        val coins = result.getOrNull()
        assertThat(coins).hasSize(2)
        assertThat(coins?.get(0)?.id).isEqualTo("bitcoin")
        assertThat(coins?.get(0)?.name).isEqualTo("Bitcoin")
        assertThat(coins?.get(1)?.id).isEqualTo("ethereum")
        assertThat(coins?.get(1)?.name).isEqualTo("Ethereum")
    }

    @Test
    fun `getCoins passes correct parameters to apiService`() = runTest {
        // Given
        val mockResponse = listOf(createMockCoinResponse())
        coEvery {
            apiService.getCoins(any(), any(), any(), any())
        } returns Result.success(mockResponse)

        // When
        repository.getCoins("eur", "market_cap_asc", 50, 2)

        // Then
        coVerify(exactly = 1) {
            apiService.getCoins("eur", "market_cap_asc", 50, 2)
        }
    }

    @Test
    fun `getCoins returns failure when apiService fails`() = runTest {
        // Given
        val exception = Exception("Network error")
        coEvery {
            apiService.getCoins(any(), any(), any(), any())
        } returns Result.failure(exception)

        // When
        val result = repository.getCoins("usd", "market_cap_desc", 10, 1)

        // Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isEqualTo(exception)
    }

    @Test
    fun `getCoins handles empty list`() = runTest {
        // Given
        coEvery {
            apiService.getCoins(any(), any(), any(), any())
        } returns Result.success(emptyList())

        // When
        val result = repository.getCoins("usd", "market_cap_desc", 10, 1)

        // Then
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEmpty()
    }

    // ==================== searchCoins Tests ====================

    @Test
    fun `searchCoins returns success with mapped coins`() = runTest {
        // Given
        val mockResponse = createMockSearchResponse()
        coEvery {
            apiService.searchCoins(any())
        } returns Result.success(mockResponse)

        // When
        val result = repository.searchCoins("bitcoin")

        // Then
        assertThat(result.isSuccess).isTrue()
        val coins = result.getOrNull()
        assertThat(coins).hasSize(2)
        assertThat(coins?.get(0)?.name).isEqualTo("Bitcoin")
        assertThat(coins?.get(1)?.name).isEqualTo("Ethereum")
    }

    @Test
    fun `searchCoins passes correct query to apiService`() = runTest {
        // Given
        val mockResponse = createMockSearchResponse()
        coEvery {
            apiService.searchCoins(any())
        } returns Result.success(mockResponse)

        // When
        repository.searchCoins("ethereum")

        // Then
        coVerify(exactly = 1) {
            apiService.searchCoins("ethereum")
        }
    }

    @Test
    fun `searchCoins returns failure when apiService fails`() = runTest {
        // Given
        val exception = Exception("Search failed")
        coEvery {
            apiService.searchCoins(any())
        } returns Result.failure(exception)

        // When
        val result = repository.searchCoins("bitcoin")

        // Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isEqualTo(exception)
    }

    @Test
    fun `searchCoins handles empty result`() = runTest {
        // Given
        coEvery {
            apiService.searchCoins(any())
        } returns Result.success(SearchResponse(emptyList()))

        // When
        val result = repository.searchCoins("nonexistent")

        // Then
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEmpty()
    }

    // ==================== getTrendingCoins Tests ====================

    @Test
    fun `getTrendingCoins returns success with mapped coins`() = runTest {
        // Given
        val mockResponse = createMockTrendingResponse()
        coEvery {
            apiService.getTrendingCoins()
        } returns Result.success(mockResponse)

        // When
        val result = repository.getTrendingCoins()

        // Then
        assertThat(result.isSuccess).isTrue()
        val coins = result.getOrNull()
        assertThat(coins).hasSize(2)
        assertThat(coins?.get(0)?.id).isEqualTo("bitcoin")
        assertThat(coins?.get(0)?.name).isEqualTo("Bitcoin")
        assertThat(coins?.get(1)?.id).isEqualTo("ethereum")
        assertThat(coins?.get(1)?.name).isEqualTo("Ethereum")
    }

    @Test
    fun `getTrendingCoins calls apiService`() = runTest {
        // Given
        val mockResponse = TrendingResponse(coins = emptyList())
        coEvery {
            apiService.getTrendingCoins()
        } returns Result.success(mockResponse)

        // When
        repository.getTrendingCoins()

        // Then
        coVerify(exactly = 1) {
            apiService.getTrendingCoins()
        }
    }

    @Test
    fun `getTrendingCoins returns failure when apiService fails`() = runTest {
        // Given
        val exception = Exception("Failed to fetch trending coins")
        coEvery {
            apiService.getTrendingCoins()
        } returns Result.failure(exception)

        // When
        val result = repository.getTrendingCoins()

        // Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isEqualTo(exception)
    }

    @Test
    fun `getTrendingCoins handles empty list`() = runTest {
        // Given
        val mockResponse = TrendingResponse(coins = emptyList())
        coEvery {
            apiService.getTrendingCoins()
        } returns Result.success(mockResponse)

        // When
        val result = repository.getTrendingCoins()

        // Then
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEmpty()
    }

    // ==================== Helper Methods ====================

    private fun createMockCoinResponse(
        id: String = "bitcoin",
        name: String = "Bitcoin"
    ) = CoinResponse(
        id = id,
        name = name,
        symbol = "BTC",
        image = "https://example.com/$id.png",
        currentPrice = 50000.0,
        marketCap = 1000000000000L,
        marketCapRank = 1,
        priceChangePercentage24h = 2.5
    )

    private fun createMockTrendingResponse() = TrendingResponse(
        coins = listOf(
            TrendingCoinWrapper(
                item = TrendingCoin(
                    id = "bitcoin",
                    name = "Bitcoin",
                    symbol = "BTC",
                    image = "https://example.com/bitcoin.png",
                    marketCapRank = 1,
                )
            ),
            TrendingCoinWrapper(
                item = TrendingCoin(
                    id = "ethereum",
                    name = "Ethereum",
                    symbol = "ETH",
                    image = "https://example.com/ethereum.png",
                    marketCapRank = 2,
                )
            )
        )
    )

    private fun createMockSearchResponse() = SearchResponse(
        coins = listOf(
            SearchCoin(
                id = "bitcoin",
                name = "Bitcoin",
                symbol = "BTC",
                image = "https://example.com/bitcoin.png",
                marketCapRank = 1,
            ),
            SearchCoin(
                id = "ethereum",
                name = "Ethereum",
                symbol = "ETH",
                image = "https://example.com/ethereum.png",
                marketCapRank = 2,
            )
        )
    )
}

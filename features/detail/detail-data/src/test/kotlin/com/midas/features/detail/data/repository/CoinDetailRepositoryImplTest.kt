package com.midas.features.detail.data.repository

import com.google.common.truth.Truth.assertThat
import com.midas.features.detail.data.remote.api.CoinDetailApiService
import com.midas.features.detail.data.remote.model.CoinDetailResponse
import com.midas.features.detail.data.remote.model.CoinImageResponse
import com.midas.features.detail.data.remote.model.DescriptionResponse
import com.midas.features.detail.data.remote.model.MarketDataResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class CoinDetailRepositoryImplTest {

    private lateinit var apiService: CoinDetailApiService
    private lateinit var repository: CoinDetailRepositoryImpl

    @Before
    fun setup() {
        apiService = mockk()
        repository = CoinDetailRepositoryImpl(apiService)
    }

    @Test
    fun `getCoinDetail returns success with mapped coin detail`() = runTest {
        // Given
        val mockResponse = createMockCoinDetailResponse()
        coEvery {
            apiService.getCoinDetail(any())
        } returns Result.success(mockResponse)

        // When
        val result = repository.getCoinDetail("bitcoin")

        // Then
        assertThat(result.isSuccess).isTrue()
        val coinDetail = result.getOrNull()
        assertThat(coinDetail).isNotNull()
        assertThat(coinDetail?.id).isEqualTo("bitcoin")
        assertThat(coinDetail?.name).isEqualTo("Bitcoin")
        assertThat(coinDetail?.symbol).isEqualTo("BTC")
        assertThat(coinDetail?.description).isEqualTo("Bitcoin is a cryptocurrency")
        assertThat(coinDetail?.image).isEqualTo("https://example.com/large.png")
    }

    @Test
    fun `getCoinDetail passes correct coinId to apiService`() = runTest {
        // Given
        val mockResponse = createMockCoinDetailResponse()
        coEvery {
            apiService.getCoinDetail(any())
        } returns Result.success(mockResponse)

        // When
        repository.getCoinDetail("ethereum")

        // Then
        coVerify(exactly = 1) {
            apiService.getCoinDetail("ethereum")
        }
    }

    @Test
    fun `getCoinDetail returns failure when apiService fails`() = runTest {
        // Given
        val exception = Exception("Network error")
        coEvery {
            apiService.getCoinDetail(any())
        } returns Result.failure(exception)

        // When
        val result = repository.getCoinDetail("bitcoin")

        // Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isEqualTo(exception)
    }

    @Test
    fun `getCoinDetail maps market data correctly`() = runTest {
        // Given
        val mockResponse = createMockCoinDetailResponse()
        coEvery {
            apiService.getCoinDetail(any())
        } returns Result.success(mockResponse)

        // When
        val result = repository.getCoinDetail("bitcoin")

        // Then
        val marketData = result.getOrNull()?.marketData
        assertThat(marketData).isNotNull()
        assertThat(marketData?.currentPrice).isEqualTo(50000.0)
        assertThat(marketData?.marketCap).isEqualTo(1000000000000L)
        assertThat(marketData?.totalVolume).isEqualTo(50000000000L)
    }

    @Test
    fun `getCoinDetail handles HTML in description`() = runTest {
        // Given
        val mockResponse = createMockCoinDetailResponse(
            description = "<p>Bitcoin is a <strong>cryptocurrency</strong></p>"
        )
        coEvery {
            apiService.getCoinDetail(any())
        } returns Result.success(mockResponse)

        // When
        val result = repository.getCoinDetail("bitcoin")

        // Then
        val description = result.getOrNull()?.description
        assertThat(description).isEqualTo("Bitcoin is a cryptocurrency")
        assertThat(description).doesNotContain("<")
        assertThat(description).doesNotContain(">")
    }

    @Test
    fun `getCoinDetail handles null marketCapRank`() = runTest {
        // Given
        val mockResponse = createMockCoinDetailResponse(marketCapRank = null)
        coEvery {
            apiService.getCoinDetail(any())
        } returns Result.success(mockResponse)

        // When
        val result = repository.getCoinDetail("bitcoin")

        // Then
        assertThat(result.getOrNull()?.marketCapRank).isNull()
    }

    @Test
    fun `getCoinDetail handles null genesisDate`() = runTest {
        // Given
        val mockResponse = createMockCoinDetailResponse(genesisDate = null)
        coEvery {
            apiService.getCoinDetail(any())
        } returns Result.success(mockResponse)

        // When
        val result = repository.getCoinDetail("bitcoin")

        // Then
        assertThat(result.getOrNull()?.genesisDate).isNull()
    }

    @Test
    fun `getCoinDetail handles empty categories`() = runTest {
        // Given
        val mockResponse = createMockCoinDetailResponse(categories = emptyList())
        coEvery {
            apiService.getCoinDetail(any())
        } returns Result.success(mockResponse)

        // When
        val result = repository.getCoinDetail("bitcoin")

        // Then
        assertThat(result.getOrNull()?.categories).isEmpty()
    }

    @Test
    fun `getCoinDetail uses large image from response`() = runTest {
        // Given
        val mockResponse = createMockCoinDetailResponse()
        coEvery {
            apiService.getCoinDetail(any())
        } returns Result.success(mockResponse)

        // When
        val result = repository.getCoinDetail("bitcoin")

        // Then
        assertThat(result.getOrNull()?.image).isEqualTo("https://example.com/large.png")
    }

    // ==================== Helper Methods ====================

    private fun createMockCoinDetailResponse(
        id: String = "bitcoin",
        name: String = "Bitcoin",
        symbol: String = "BTC",
        description: String = "Bitcoin is a cryptocurrency",
        marketCapRank: Int? = 1,
        categories: List<String> = listOf("Cryptocurrency"),
        genesisDate: String? = "2009-01-03"
    ) = CoinDetailResponse(
        id = id,
        name = name,
        symbol = symbol,
        description = DescriptionResponse(en = description),
        image = CoinImageResponse(
            thumb = "https://example.com/thumb.png",
            small = "https://example.com/small.png",
            large = "https://example.com/large.png"
        ),
        marketCapRank = marketCapRank,
        marketData = MarketDataResponse(
            currentPrice = mapOf("USD" to 50000.0),
            marketCap = mapOf("USD" to 1000000000000L),
            totalVolume = mapOf("USD" to 50000000000L),
            high24h = mapOf("USD" to 51000.0),
            low24h = mapOf("USD" to 49000.0),
            priceChangePercentage24h = 2.5,
            priceChangePercentage7d = 5.0,
            priceChangePercentage30d = 10.0,
            circulatingSupply = 19000000.0,
            totalSupply = 21000000.0,
            maxSupply = 21000000.0
        ),
        categories = categories,
        genesisDate = genesisDate
    )
}

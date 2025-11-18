package com.midas.features.detail.domain.usecase

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.midas.features.detail.domain.model.CoinDetail
import com.midas.features.detail.domain.model.MarketData
import com.midas.features.detail.domain.repository.CoinDetailRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class GetCoinDetailUseCaseTest {

    private lateinit var coinDetailRepository: CoinDetailRepository
    private lateinit var getCoinDetailUseCase: GetCoinDetailUseCase

    @Before
    fun setup() {
        coinDetailRepository = mockk()
        getCoinDetailUseCase = GetCoinDetailUseCase(coinDetailRepository)
    }

    @Test
    fun `invoke with valid coinId should return coin detail`() = runTest {
        // Given
        val coinId = "bitcoin"
        val coinDetail = createTestCoinDetail(id = coinId)
        val params = GetCoinDetailUseCase.Params(coinId = coinId)

        coEvery {
            coinDetailRepository.getCoinDetail(coinId)
        } returns Result.success(coinDetail)

        // When
        getCoinDetailUseCase(params).test {
            // Then
            val result = awaitItem()
            assertThat(result.isSuccess).isTrue()

            val detail = result.getOrNull()
            assertThat(detail).isNotNull()
            assertThat(detail?.id).isEqualTo(coinId)
            assertThat(detail?.name).isEqualTo("Bitcoin")
            assertThat(detail?.symbol).isEqualTo("btc")

            awaitComplete()
        }

        coVerify(exactly = 1) {
            coinDetailRepository.getCoinDetail(coinId)
        }
    }

    @Test
    fun `invoke should pass correct coinId to repository`() = runTest {
        // Given
        val coinId = "ethereum"
        val coinDetail = createTestCoinDetail(id = coinId, name = "Ethereum")
        val params = GetCoinDetailUseCase.Params(coinId = coinId)

        coEvery {
            coinDetailRepository.getCoinDetail(coinId)
        } returns Result.success(coinDetail)

        // When
        getCoinDetailUseCase(params).test {
            // Then
            val result = awaitItem()
            assertThat(result.isSuccess).isTrue()
            assertThat(result.getOrNull()?.id).isEqualTo(coinId)

            awaitComplete()
        }

        coVerify(exactly = 1) {
            coinDetailRepository.getCoinDetail(coinId)
        }
    }

    @Test
    fun `invoke should return market data correctly`() = runTest {
        // Given
        val coinId = "bitcoin"
        val marketData = MarketData(
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
        )
        val coinDetail = createTestCoinDetail(id = coinId, marketData = marketData)
        val params = GetCoinDetailUseCase.Params(coinId = coinId)

        coEvery {
            coinDetailRepository.getCoinDetail(coinId)
        } returns Result.success(coinDetail)

        // When
        getCoinDetailUseCase(params).test {
            // Then
            val result = awaitItem()
            assertThat(result.isSuccess).isTrue()

            val detail = result.getOrNull()
            assertThat(detail?.marketData?.currentPrice).isEqualTo(50000.0)
            assertThat(detail?.marketData?.marketCap).isEqualTo(1000000000L)
            assertThat(detail?.marketData?.priceChangePercentage24h).isEqualTo(2.0)

            awaitComplete()
        }
    }

    @Test
    fun `invoke should handle null description`() = runTest {
        // Given
        val coinId = "test"
        val coinDetail = createTestCoinDetail(id = coinId, description = null)
        val params = GetCoinDetailUseCase.Params(coinId = coinId)

        coEvery {
            coinDetailRepository.getCoinDetail(coinId)
        } returns Result.success(coinDetail)

        // When
        getCoinDetailUseCase(params).test {
            // Then
            val result = awaitItem()
            assertThat(result.isSuccess).isTrue()
            assertThat(result.getOrNull()?.description).isNull()

            awaitComplete()
        }
    }

    @Test
    fun `invoke should handle empty description`() = runTest {
        // Given
        val coinId = "test"
        val coinDetail = createTestCoinDetail(id = coinId, description = "")
        val params = GetCoinDetailUseCase.Params(coinId = coinId)

        coEvery {
            coinDetailRepository.getCoinDetail(coinId)
        } returns Result.success(coinDetail)

        // When
        getCoinDetailUseCase(params).test {
            // Then
            val result = awaitItem()
            assertThat(result.isSuccess).isTrue()
            assertThat(result.getOrNull()?.description).isEmpty()

            awaitComplete()
        }
    }

    @Test
    fun `invoke should return categories correctly`() = runTest {
        // Given
        val coinId = "bitcoin"
        val categories = listOf("Cryptocurrency", "Store of Value", "Payments")
        val coinDetail = createTestCoinDetail(id = coinId, categories = categories)
        val params = GetCoinDetailUseCase.Params(coinId = coinId)

        coEvery {
            coinDetailRepository.getCoinDetail(coinId)
        } returns Result.success(coinDetail)

        // When
        getCoinDetailUseCase(params).test {
            // Then
            val result = awaitItem()
            assertThat(result.isSuccess).isTrue()
            assertThat(result.getOrNull()?.categories).isEqualTo(categories)

            awaitComplete()
        }
    }

    @Test
    fun `invoke should handle null genesis date`() = runTest {
        // Given
        val coinId = "test"
        val coinDetail = createTestCoinDetail(id = coinId, genesisDate = null)
        val params = GetCoinDetailUseCase.Params(coinId = coinId)

        coEvery {
            coinDetailRepository.getCoinDetail(coinId)
        } returns Result.success(coinDetail)

        // When
        getCoinDetailUseCase(params).test {
            // Then
            val result = awaitItem()
            assertThat(result.isSuccess).isTrue()
            assertThat(result.getOrNull()?.genesisDate).isNull()

            awaitComplete()
        }
    }

    @Test
    fun `invoke should handle repository error gracefully`() = runTest {
        // Given
        val coinId = "invalid"
        val exception = Exception("Network error")
        val params = GetCoinDetailUseCase.Params(coinId = coinId)

        coEvery {
            coinDetailRepository.getCoinDetail(coinId)
        } returns Result.failure(exception)

        // When
        getCoinDetailUseCase(params).test {
            // Then
            val result = awaitItem()
            assertThat(result.isFailure).isTrue()
            assertThat(result.exceptionOrNull()).isEqualTo(exception)

            awaitComplete()
        }

        coVerify(exactly = 1) {
            coinDetailRepository.getCoinDetail(coinId)
        }
    }

    @Test
    fun `invoke should handle repository exception`() = runTest {
        // Given
        val coinId = "test"
        val exception = RuntimeException("Unexpected error")
        val params = GetCoinDetailUseCase.Params(coinId = coinId)

        coEvery {
            coinDetailRepository.getCoinDetail(coinId)
        } returns Result.failure(exception)

        // When
        getCoinDetailUseCase(params).test {
            val result = awaitItem()
            assertThat(result.isFailure).isTrue()
            assertThat(result.exceptionOrNull()).isInstanceOf(RuntimeException::class.java)
            assertThat(result.exceptionOrNull()?.message).isEqualTo("Unexpected error")
            awaitComplete()
        }

        coVerify(exactly = 1) {
            coinDetailRepository.getCoinDetail(coinId)
        }
    }

    @Test
    fun `invoke with null market cap rank should handle correctly`() = runTest {
        // Given
        val coinId = "test"
        val coinDetail = createTestCoinDetail(id = coinId, marketCapRank = null)
        val params = GetCoinDetailUseCase.Params(coinId = coinId)

        coEvery {
            coinDetailRepository.getCoinDetail(coinId)
        } returns Result.success(coinDetail)

        // When
        getCoinDetailUseCase(params).test {
            // Then
            val result = awaitItem()
            assertThat(result.isSuccess).isTrue()
            assertThat(result.getOrNull()?.marketCapRank).isNull()

            awaitComplete()
        }
    }

    @Test
    fun `invoke with null market data values should handle correctly`() = runTest {
        // Given
        val coinId = "test"
        val marketData = MarketData(
            currentPrice = null,
            marketCap = null,
            totalVolume = null,
            high24h = null,
            low24h = null,
            priceChangePercentage24h = null,
            priceChangePercentage7d = null,
            priceChangePercentage30d = null,
            circulatingSupply = null,
            totalSupply = null,
            maxSupply = null
        )
        val coinDetail = createTestCoinDetail(id = coinId, marketData = marketData)
        val params = GetCoinDetailUseCase.Params(coinId = coinId)

        coEvery {
            coinDetailRepository.getCoinDetail(coinId)
        } returns Result.success(coinDetail)

        // When
        getCoinDetailUseCase(params).test {
            // Then
            val result = awaitItem()
            assertThat(result.isSuccess).isTrue()

            val detail = result.getOrNull()
            assertThat(detail?.marketData?.currentPrice).isNull()
            assertThat(detail?.marketData?.marketCap).isNull()
            assertThat(detail?.marketData?.priceChangePercentage24h).isNull()

            awaitComplete()
        }
    }

    @Test
    fun `invoke multiple times should call repository each time`() = runTest {
        // Given
        val coinId = "bitcoin"
        val coinDetail = createTestCoinDetail(id = coinId)
        val params = GetCoinDetailUseCase.Params(coinId = coinId)

        coEvery {
            coinDetailRepository.getCoinDetail(coinId)
        } returns Result.success(coinDetail)

        // When - First call
        getCoinDetailUseCase(params).test {
            assertThat(awaitItem().isSuccess).isTrue()
            awaitComplete()
        }

        // When - Second call
        getCoinDetailUseCase(params).test {
            assertThat(awaitItem().isSuccess).isTrue()
            awaitComplete()
        }

        // Then
        coVerify(exactly = 2) {
            coinDetailRepository.getCoinDetail(coinId)
        }
    }

    private fun createTestCoinDetail(
        id: String = "bitcoin",
        name: String = "Bitcoin",
        symbol: String = "btc",
        description: String? = "Test description",
        marketCapRank: Int? = 1,
        marketData: MarketData = MarketData(
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
        categories: List<String> = listOf("Cryptocurrency", "Store of Value"),
        genesisDate: String? = "2009-01-03"
    ): CoinDetail {
        return CoinDetail(
            id = id,
            symbol = symbol,
            name = name,
            description = description,
            image = "https://example.com/image.png",
            marketCapRank = marketCapRank,
            marketData = marketData,
            categories = categories,
            genesisDate = genesisDate
        )
    }
}

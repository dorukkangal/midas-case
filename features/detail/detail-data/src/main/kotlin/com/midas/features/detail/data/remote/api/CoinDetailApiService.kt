package com.midas.features.detail.data.remote.api

import com.midas.core.util.toNetworkException
import com.midas.features.detail.data.remote.model.CoinDetailResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CoinDetailApiService @Inject constructor(
    private val httpClient: HttpClient
) {

    /**
     * Get detailed information about a specific coin
     *
     * @param coinId The coin id (e.g., "bitcoin", "ethereum")
     */
    suspend fun getCoinDetail(coinId: String): Result<CoinDetailResponse> {
        return try {
            val response = httpClient.get("coins/$coinId") {
                parameter("localization", false)
                parameter("tickers", false)
                parameter("market_data", true)
                parameter("community_data", false)
                parameter("developer_data", false)
                parameter("sparkline", false)
            }.body<CoinDetailResponse>()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e.toNetworkException())
        }
    }
}

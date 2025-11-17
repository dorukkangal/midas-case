package com.midas.features.home.data.remote.api

import com.midas.core.util.toNetworkException
import com.midas.features.home.data.remote.model.CoinResponse
import com.midas.features.home.data.remote.model.SearchResponse
import com.midas.features.home.data.remote.model.TrendingResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CoinApiService @Inject constructor(
    private val httpClient: HttpClient
) {

    /**
     * Get list of coins with market data
     *
     * @param vsCurrency Target currency (default: usd)
     * @param order Sort order (default: market_cap_desc)
     * @param perPage Number of results per page (default: 50)
     * @param page Page number (default: 1)
     * @param sparkline Include sparkline data (default: false)
     * @param priceChangePercentage Include price change percentage (default: 24h)
     */
    suspend fun getCoins(
        vsCurrency: String = "usd",
        order: String = "market_cap_desc",
        perPage: Int = 50,
        page: Int = 1,
        sparkline: Boolean = false,
        priceChangePercentage: String = "24h"
    ): Result<List<CoinResponse>> {
        return try {
            val response = httpClient.get("coins/markets") {
                parameter("vs_currency", vsCurrency)
                parameter("order", order)
                parameter("per_page", perPage)
                parameter("page", page)
                parameter("sparkline", sparkline)
                parameter("price_change_percentage", priceChangePercentage)
            }.body<List<CoinResponse>>()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e.toNetworkException())
        }
    }

    /**
     * Search for coins by name or symbol
     *
     * @param query Search query
     */
    suspend fun searchCoins(query: String): Result<SearchResponse> {
        return try {
            val response = httpClient.get("search") {
                parameter("query", query)
            }.body<SearchResponse>()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e.toNetworkException())
        }
    }

    /**
     * Get trending coins
     */
    suspend fun getTrendingCoins(): Result<TrendingResponse> {
        return try {
            val response = httpClient.get("search/trending").body<TrendingResponse>()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e.toNetworkException())
        }
    }
}

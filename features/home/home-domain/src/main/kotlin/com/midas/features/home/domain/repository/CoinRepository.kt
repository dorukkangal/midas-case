package com.midas.features.home.domain.repository

import com.midas.features.home.domain.model.Coin
import kotlinx.coroutines.flow.Flow

interface CoinRepository {

    /**
     * Get all coins
     *
     * @param vsCurrency Target currency
     * @param order Sort order
     * @param perPage Results per page
     * @param page Page number
     */
    suspend fun getCoins(
        vsCurrency: String,
        order: String,
        perPage: Int,
        page: Int,
    ): Flow<Result<List<Coin>>>

    /**
     * Search coins by name or symbol
     *
     * @param query Search query
     */
    suspend fun searchCoins(query: String): Flow<Result<List<Coin>>>

    /**
     * Get trending coins
     */
    suspend fun getTrendingCoins(): Flow<Result<List<Coin>>>
}

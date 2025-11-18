package com.midas.features.home.domain.repository

import com.midas.features.home.domain.model.Coin

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
    ): Result<List<Coin>>

    /**
     * Search coins by name or symbol
     *
     * @param query Search query
     */
    suspend fun searchCoins(query: String): Result<List<Coin>>

    /**
     * Get trending coins
     */
    suspend fun getTrendingCoins(): Result<List<Coin>>
}

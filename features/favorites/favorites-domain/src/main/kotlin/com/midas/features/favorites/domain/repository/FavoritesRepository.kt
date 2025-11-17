package com.midas.features.favorites.domain.repository

import com.midas.features.home.domain.model.Coin
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {

    /**
     * Get all favorite coins
     */
    suspend fun getFavoriteCoins(): Flow<Result<List<Coin>>>

    /**
     * Get favorite coins count
     */
    suspend fun getFavoriteCoinsCount(): Flow<Result<Int>>

    /**
     * Check if coin is in favorites
     *
     * @param coinId The coin id to check
     */
    suspend fun isFavorite(coinId: String): Flow<Result<Boolean>>

    /**
     * Add coin to favorites
     *
     * @param coin The coin to add to favorites
     */
    suspend fun addToFavorites(coin: Coin): Flow<Result<Unit>>

    /**
     * Remove coin from favorites
     *
     * @param coinId The coin id to remove from favorites
     */
    suspend fun removeFromFavorites(coinId: String): Flow<Result<Unit>>

    /**
     * Clear all favorites
     */
    suspend fun clearAllFavorites(): Flow<Result<Unit>>
}

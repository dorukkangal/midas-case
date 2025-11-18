package com.midas.features.favorites.domain.repository

import com.midas.features.home.domain.model.Coin
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {

    /**
     * Get all favorite coins
     */
    suspend fun getFavoriteCoins(): Result<List<Coin>>

    /**
     * Get favorite coins count
     */
    suspend fun getFavoriteCoinsCount(): Result<Int>

    /**
     * Check if coin is in favorites
     *
     * @param coinId The coin id to check
     */
    suspend fun isFavorite(coinId: String): Result<Boolean>

    /**
     * Add coin to favorites
     *
     * @param coin The coin to add to favorites
     */
    suspend fun addToFavorites(coin: Coin): Result<Unit>

    /**
     * Remove coin from favorites
     *
     * @param coinId The coin id to remove from favorites
     */
    suspend fun removeFromFavorites(coinId: String): Result<Unit>

    /**
     * Clear all favorites
     */
    suspend fun clearAllFavorites(): Result<Unit>
}

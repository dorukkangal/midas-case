package com.midas.features.favorites.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.midas.features.favorites.data.local.model.FavoriteCoinEntity

@Dao
interface FavoriteCoinDao {

    @Query("SELECT * FROM favorite_coins ORDER BY added_at DESC")
    suspend fun getAllFavoriteCoins(): List<FavoriteCoinEntity>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_coins WHERE coin_id = :coinId)")
    suspend fun isFavorite(coinId: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteCoin(favoriteCoin: FavoriteCoinEntity)

    @Query("DELETE FROM favorite_coins WHERE coin_id = :coinId")
    suspend fun deleteFavoriteCoinById(coinId: String)

    @Query("DELETE FROM favorite_coins")
    suspend fun deleteAllFavoriteCoins()

    @Query("SELECT COUNT(*) FROM favorite_coins")
    suspend fun getFavoriteCoinsCount(): Int
}

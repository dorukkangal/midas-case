package com.midas.features.favorites.data.repository

import com.midas.features.favorites.data.local.dao.FavoriteCoinDao
import com.midas.features.favorites.data.local.mapper.toCoin
import com.midas.features.favorites.data.local.mapper.toFavoriteCoinEntity
import com.midas.features.favorites.domain.repository.FavoritesRepository
import com.midas.features.home.domain.model.Coin
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoritesRepositoryImpl @Inject constructor(
    private val favoriteCoinDao: FavoriteCoinDao
) : FavoritesRepository {

    override suspend fun getFavoriteCoins(): Result<List<Coin>> = runCatching {
        favoriteCoinDao.getAllFavoriteCoins().map { it.toCoin() }
    }

    override suspend fun getFavoriteCoinsCount(): Result<Int> = runCatching {
        favoriteCoinDao.getFavoriteCoinsCount()
    }

    override suspend fun isFavorite(coinId: String): Result<Boolean> = runCatching {
        favoriteCoinDao.isFavorite(coinId)
    }

    override suspend fun addToFavorites(coin: Coin): Result<Unit> = runCatching {
        favoriteCoinDao.insertFavoriteCoin(coin.toFavoriteCoinEntity())
    }

    override suspend fun removeFromFavorites(coinId: String): Result<Unit> = runCatching {
        favoriteCoinDao.deleteFavoriteCoinById(coinId)
    }

    override suspend fun clearAllFavorites(): Result<Unit> = runCatching {
        favoriteCoinDao.deleteAllFavoriteCoins()
    }
}

package com.midas.features.favorites.data.repository

import com.midas.features.favorites.data.local.dao.FavoriteCoinDao
import com.midas.features.favorites.data.local.mapper.toCoin
import com.midas.features.favorites.data.local.mapper.toFavoriteCoinEntity
import com.midas.features.favorites.domain.repository.FavoritesRepository
import com.midas.features.home.domain.model.Coin
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoritesRepositoryImpl @Inject constructor(
    private val favoriteCoinDao: FavoriteCoinDao
) : FavoritesRepository {

    override suspend fun getFavoriteCoins(): Flow<Result<List<Coin>>> = flow {
        try {
            val result = favoriteCoinDao.getAllFavoriteCoins()
            emit(
                Result.success(
                    result.map { it.toCoin() }
                )
            )
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override suspend fun getFavoriteCoinsCount(): Flow<Result<Int>> = flow {
        try {
            val result = favoriteCoinDao.getFavoriteCoinsCount()
            emit(Result.success(result))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override suspend fun isFavorite(coinId: String): Flow<Result<Boolean>> = flow {
        try {
            val result = favoriteCoinDao.isFavorite(coinId)
            emit(Result.success(result))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override suspend fun addToFavorites(coin: Coin): Flow<Result<Unit>> = flow {
        try {
            val favoriteEntity = coin.toFavoriteCoinEntity()
            favoriteCoinDao.insertFavoriteCoin(favoriteEntity)
            emit(Result.success(Unit))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override suspend fun removeFromFavorites(coinId: String): Flow<Result<Unit>> = flow {
        try {
            favoriteCoinDao.deleteFavoriteCoinById(coinId)
            emit(Result.success(Unit))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override suspend fun clearAllFavorites(): Flow<Result<Unit>> = flow {
        try {
            favoriteCoinDao.deleteAllFavoriteCoins()
            emit(Result.success(Unit))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}

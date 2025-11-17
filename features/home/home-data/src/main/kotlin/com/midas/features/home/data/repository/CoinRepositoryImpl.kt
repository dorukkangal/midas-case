package com.midas.features.home.data.repository

import com.midas.features.home.data.remote.api.CoinApiService
import com.midas.features.home.data.remote.mapper.toCoin
import com.midas.features.home.domain.model.Coin
import com.midas.features.home.domain.repository.CoinRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CoinRepositoryImpl @Inject constructor(
    private val apiService: CoinApiService,
) : CoinRepository {

    override suspend fun getCoins(
        vsCurrency: String,
        order: String,
        perPage: Int,
        page: Int,
    ): Flow<Result<List<Coin>>> = flow {
        val remoteResult = apiService.getCoins(vsCurrency, order, perPage, page)
        remoteResult.fold(
            onSuccess = { coins ->
                emit(Result.success(coins.map { it.toCoin() }))
            },
            onFailure = { error ->
                emit(Result.failure(error))
            }
        )
    }

    override suspend fun searchCoins(query: String): Flow<Result<List<Coin>>> = flow {
        val remoteResult = apiService.searchCoins(query)
        remoteResult.fold(
            onSuccess = { response ->
                emit(Result.success(response.coins.map { it.toCoin() }))
            },
            onFailure = { error ->
                emit(Result.failure(error))
            }
        )
    }

    override suspend fun getTrendingCoins(): Flow<Result<List<Coin>>> = flow {
        val remoteResult = apiService.getTrendingCoins()
        remoteResult.fold(
            onSuccess = { trendingCoins ->
                emit(Result.success(trendingCoins.coins.map { it.item.toCoin() }))
            },
            onFailure = { error ->
                emit(Result.failure(error))
            }
        )
    }
}

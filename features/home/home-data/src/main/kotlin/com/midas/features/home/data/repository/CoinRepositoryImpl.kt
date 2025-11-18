package com.midas.features.home.data.repository

import com.midas.features.home.data.remote.api.CoinApiService
import com.midas.features.home.data.remote.mapper.toCoin
import com.midas.features.home.domain.model.Coin
import com.midas.features.home.domain.repository.CoinRepository
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
    ): Result<List<Coin>> =
        apiService.getCoins(
            vsCurrency = vsCurrency,
            order = order,
            perPage = perPage,
            page = page
        ).map { coins -> coins.map { it.toCoin() } }

    override suspend fun searchCoins(query: String): Result<List<Coin>> =
        apiService.searchCoins(query)
            .map { response -> response.coins.map { it.toCoin() } }

    override suspend fun getTrendingCoins(): Result<List<Coin>> =
        apiService.getTrendingCoins()
            .map { response -> response.coins.map { it.item.toCoin() } }
}

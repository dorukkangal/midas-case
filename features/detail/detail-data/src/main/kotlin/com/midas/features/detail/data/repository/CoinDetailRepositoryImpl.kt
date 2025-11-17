package com.midas.features.detail.data.repository

import com.midas.features.detail.data.remote.api.CoinDetailApiService
import com.midas.features.detail.data.remote.mapper.toCoinDetail
import com.midas.features.detail.domain.model.CoinDetail
import com.midas.features.detail.domain.repository.CoinDetailRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CoinDetailRepositoryImpl @Inject constructor(
    private val apiService: CoinDetailApiService
) : CoinDetailRepository {

    override suspend fun getCoinDetail(
        coinId: String,
    ): Flow<Result<CoinDetail>> = flow {
        val remoteResult = apiService.getCoinDetail(coinId)
        remoteResult.fold(
            onSuccess = { coinDetail ->
                emit(Result.success(coinDetail.toCoinDetail()))
            },
            onFailure = { error ->
                emit(Result.failure(error))
            }
        )
    }
}

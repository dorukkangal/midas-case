package com.midas.features.detail.domain.usecase

import com.midas.features.detail.domain.model.CoinDetail
import com.midas.features.detail.domain.repository.CoinDetailRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Use case for getting detailed coin information
 * Handles business logic for coin detail fetching and processing
 */
class GetCoinDetailUseCase @Inject constructor(
    private val coinDetailRepository: CoinDetailRepository
) {

    /**
     * Execute the use case to get coin details
     *
     * @param params Parameters for fetching coin details
     *
     * @return Flow of Result containing coin detail information
     */
    operator fun invoke(params: Params): Flow<Result<CoinDetail>> = flow {
        emit(coinDetailRepository.getCoinDetail(params.coinId))
    }.catch { e ->
        emit(Result.failure(e))
    }

    data class Params(
        val coinId: String,
    )
}

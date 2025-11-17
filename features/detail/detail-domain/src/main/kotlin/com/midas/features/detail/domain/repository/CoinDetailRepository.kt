package com.midas.features.detail.domain.repository

import com.midas.features.detail.domain.model.CoinDetail
import kotlinx.coroutines.flow.Flow

interface CoinDetailRepository {

    /**
     * Get detailed coin information from remote API
     *
     * @param coinId The coin id (e.g., "bitcoin", "ethereum")
     */
    suspend fun getCoinDetail(
        coinId: String,
    ): Flow<Result<CoinDetail>>
}

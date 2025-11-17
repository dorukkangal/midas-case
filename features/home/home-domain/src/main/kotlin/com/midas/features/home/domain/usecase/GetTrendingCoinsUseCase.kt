package com.midas.features.home.domain.usecase

import com.midas.features.home.domain.model.Coin
import com.midas.features.home.domain.repository.CoinRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Use case for getting trending coins
 * Handles business logic for trending coin identification and filtering
 */
class GetTrendingCoinsUseCase @Inject constructor(
    private val coinRepository: CoinRepository
) {

    companion object {
        private const val MAX_TRENDING_COINS = 10
        private const val MAX_RANK_FOR_TRENDING = 100
    }

    /**
     * Execute the use case to get trending coins
     *
     * @return Flow of Result containing trending coins
     */
    suspend operator fun invoke(): Flow<Result<List<Coin>>> {
        return coinRepository.getTrendingCoins()
            .map { result ->
                result.map { coins ->
                    filterTrendingCoins(coins)
                }
            }
            .catch { throwable ->
                emit(Result.failure(throwable))
            }
    }

    /**
     * Filter and process trending coins with business logic:
     * 1. Filter by top 100 market cap rank
     * 2. Sort by price change percentage (descending)
     * 3. Limit to 10 coins
     */
    private fun filterTrendingCoins(coins: List<Coin>): List<Coin> {
        return coins
            .filter { coin ->
                // Basic validation
                coin.name.isNotBlank() &&
                        coin.symbol.isNotBlank() &&
                        // Filter by top 100 rank
                        coin.marketCapRank != null && coin.marketCapRank <= MAX_RANK_FOR_TRENDING
            }
            .sortedByDescending { coin ->
                // Sort by price change percentage (treat null as 0.0)
                coin.priceChangePercentage24h ?: 0.0
            }
            .take(MAX_TRENDING_COINS) // Limit to 10 coins
    }
}

package com.midas.features.home.domain.usecase

import com.midas.features.home.domain.model.Coin
import com.midas.features.home.domain.model.SortOrder
import com.midas.features.home.domain.repository.CoinRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Use case for getting coins with market data
 * Handles business logic for coin fetching, caching, and error handling
 */
class GetCoinsUseCase @Inject constructor(
    private val coinRepository: CoinRepository
) {

    /**
     * Execute the use case to get coins
     *
     * @param params Parameters for fetching coins
     *
     * @return Flow of Result containing list of coins
     */
    operator fun invoke(params: Params = Params()): Flow<Result<List<Coin>>> = flow {
        emit(
            coinRepository.getCoins(
                vsCurrency = params.vsCurrency,
                order = params.sortOrder.name.lowercase(),
                perPage = params.perPage,
                page = params.page,
            ).map { coins -> filterAndSortCoins(coins, params.sortOrder) }
        )
    }.catch { e ->
        emit(Result.failure(e))
    }

    private fun filterAndSortCoins(
        coins: List<Coin>,
        sortOrder: SortOrder?
    ): List<Coin> = coins
        .filter { coin ->
            // Filter out coins with invalid data
            coin.currentPrice != null && coin.currentPrice > 0.0 &&
                    coin.name.isNotBlank() &&
                    coin.symbol.isNotBlank()
        }
        .let { filteredCoins ->
            // Apply additional sorting if needed
            sortCoins(filteredCoins, sortOrder)
        }

    /**
     * Sort coins based on specified criteria
     */
    private fun sortCoins(
        coins: List<Coin>,
        sortOrder: SortOrder?
    ): List<Coin> = when (sortOrder) {
        SortOrder.NAME_ASC -> coins.sortedBy { it.name.lowercase() }
        SortOrder.NAME_DESC -> coins.sortedByDescending { it.name.lowercase() }
        SortOrder.PRICE_DESC -> coins.sortedByDescending { it.currentPrice }
        SortOrder.PRICE_ASC -> coins.sortedBy { it.currentPrice }
        SortOrder.MARKET_CAP_DESC -> coins.sortedByDescending { it.marketCap ?: 0L }
        SortOrder.MARKET_CAP_ASC -> coins.sortedBy { it.marketCap ?: 0L }
        SortOrder.CHANGE_24H_DESC -> coins.sortedByDescending { it.priceChangePercentage24h ?: 0.0 }
        SortOrder.CHANGE_24H_ASC -> coins.sortedBy { it.priceChangePercentage24h ?: 0.0 }
        else -> coins
    }

    data class Params(
        val vsCurrency: String = "usd",
        val sortOrder: SortOrder = SortOrder.MARKET_CAP_DESC,
        val perPage: Int = 20,
        val page: Int = 1,
    )
}

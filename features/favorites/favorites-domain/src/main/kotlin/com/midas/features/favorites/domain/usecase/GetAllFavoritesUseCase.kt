package com.midas.features.favorites.domain.usecase

import com.midas.features.detail.domain.repository.CoinDetailRepository
import com.midas.features.favorites.domain.repository.FavoritesRepository
import com.midas.features.home.domain.model.Coin
import com.midas.features.home.domain.model.SortOrder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

/**
 * Use case for getting favorite coins
 * Handles business logic for favorites management and sorting
 */
class GetAllFavoritesUseCase @Inject constructor(
    private val favoritesRepository: FavoritesRepository,
    private val coinDetailRepository: CoinDetailRepository
) {

    /**
     * Execute the use case to get favorite coins
     *
     * @param params Parameters for fetching favorite coins
     *
     * @return Flow of sorted favorite coins
     */
    operator fun invoke(params: Params): Flow<Result<List<Coin>>> = flow {
        val favoritesResult = favoritesRepository.getFavoriteCoins()

        favoritesResult.onFailure { error ->
            emit(Result.failure(error))
            return@flow
        }

        val favoriteCoins = favoritesResult.getOrThrow()

        val updatedCoins = coroutineScope {
            favoriteCoins.map { coin ->
                async {
                    val detailResult = coinDetailRepository.getCoinDetail(coin.id)

                    detailResult.fold(
                        onSuccess = { detail ->
                            coin.copy(
                                currentPrice = detail.marketData.currentPrice,
                                marketCap = detail.marketData.marketCap,
                                priceChangePercentage24h = detail.marketData.priceChangePercentage24h
                            )
                        },
                        onFailure = {
                            coin
                        }
                    )
                }
            }.awaitAll()
        }

        val sorted = sortFavoriteCoins(updatedCoins, params.sortOrder)

        emit(Result.success(sorted))
    }.catch { e ->
        emit(Result.failure(e))
    }.flowOn(Dispatchers.IO)

    /**
     * Sort favorite coins based on specified criteria
     */
    private fun sortFavoriteCoins(
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
        val sortOrder: SortOrder? = null,
    )
}

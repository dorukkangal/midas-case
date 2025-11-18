package com.midas.features.favorites.domain.usecase

import com.midas.features.favorites.domain.repository.FavoritesRepository
import com.midas.features.home.domain.model.Coin
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Use case for toggling favorite status of a coin
 * Handles business logic for adding/removing favorites with validation
 */
class ToggleFavoriteUseCase @Inject constructor(
    private val favoritesRepository: FavoritesRepository
) {

    /**
     * Execute the use case to toggle favorite status
     *
     * @param params Parameters for the coin to toggle favorite status
     *
     * @return Flow of Result with action performed (ADDED or REMOVED)
     */
    operator fun invoke(params: Params): Flow<Result<FavoriteAction>> = flow {
        val result = favoritesRepository.isFavorite(params.coin.id)
            .fold(
                onSuccess = { isFavorite ->
                    if (isFavorite) {
                        favoritesRepository.removeFromFavorites(params.coin.id)
                            .map { FavoriteAction.REMOVED }
                    } else {
                        favoritesRepository.addToFavorites(params.coin)
                            .map { FavoriteAction.ADDED }
                    }
                },
                onFailure = { throwable ->
                    Result.failure(throwable)
                }
            )
        emit(result)
    }.catch { e ->
        emit(Result.failure(e))
    }

    enum class FavoriteAction {
        ADDED,
        REMOVED
    }

    data class Params(
        val coin: Coin,
    )
}

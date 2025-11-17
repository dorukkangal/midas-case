package com.midas.features.favorites.domain.usecase

import com.midas.features.favorites.domain.repository.FavoritesRepository
import com.midas.features.home.domain.model.Coin
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
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
     * @param params Parameters for fetching the coin to toggle favorite status
     *
     * @return Flow of Result indicating success or failure with message
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    suspend operator fun invoke(params: Params): Flow<Result<FavoriteAction>> {
        return favoritesRepository.isFavorite(params.coin.id)
            .flatMapConcat { isFavResult ->
                isFavResult.fold(
                    onSuccess = { isFavorite ->
                        if (isFavorite) {
                            favoritesRepository.removeFromFavorites(params.coin.id)
                                .map { removeResult ->
                                    removeResult.fold(
                                        onSuccess = { Result.success(FavoriteAction.REMOVED) },
                                        onFailure = { Result.failure(it) }
                                    )
                                }
                        } else {
                            favoritesRepository.addToFavorites(params.coin)
                                .map { addResult ->
                                    addResult.fold(
                                        onSuccess = { Result.success(FavoriteAction.ADDED) },
                                        onFailure = { Result.failure(it) }
                                    )
                                }
                        }
                    },
                    onFailure = { error ->
                        flowOf(Result.failure(error))
                    }
                )
            }
            .catch {
                emit(Result.failure(it))
            }
    }

    enum class FavoriteAction {
        ADDED,
        REMOVED
    }

    data class Params(
        val coin: Coin,
    )
}

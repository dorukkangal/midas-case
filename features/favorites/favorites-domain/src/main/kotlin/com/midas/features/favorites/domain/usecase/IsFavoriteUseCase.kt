package com.midas.features.favorites.domain.usecase

import com.midas.features.favorites.domain.repository.FavoritesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Use case for toggling favorite status of a coin
 * Handles business logic for adding/removing favorites with validation
 */
class IsFavoriteUseCase @Inject constructor(
    private val favoritesRepository: FavoritesRepository
) {

    /**
     * Execute the use case to toggle favorite status
     *
     * @param params Parameters for fetching favorite status
     *
     * @return Result indicating success or failure with message
     */
    operator fun invoke(params: Params): Flow<Result<Boolean>> = flow {
        emit(favoritesRepository.isFavorite(params.coinId))
    }.catch { e ->
        emit(Result.failure(e))
    }

    data class Params(
        val coinId: String,
    )
}

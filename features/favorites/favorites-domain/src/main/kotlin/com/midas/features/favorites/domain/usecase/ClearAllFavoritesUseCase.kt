package com.midas.features.favorites.domain.usecase

import com.midas.features.favorites.domain.repository.FavoritesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Use case for getting favorite coins
 * Handles business logic for favorites management and sorting
 */
class ClearAllFavoritesUseCase @Inject constructor(
    private val favoritesRepository: FavoritesRepository
) {

    /**
     * Execute the use case to get favorite coins
     *
     * @return Flow of sorted favorite coins
     */
    operator fun invoke(): Flow<Result<Unit>> = flow {
        emit(favoritesRepository.clearAllFavorites())
    }
}

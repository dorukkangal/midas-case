package com.midas.features.home.domain.usecase

import com.midas.features.home.domain.model.Coin
import com.midas.features.home.domain.repository.CoinRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Use case for searching coins by name or symbol
 * Handles search logic, validation, and result filtering
 */
class SearchCoinsUseCase @Inject constructor(
    private val coinRepository: CoinRepository
) {

    /**
     * Execute the use case to search coins
     *
     * @param query Search query (minimum 2 characters)
     * @return Flow of Result containing filtered search results
     */
    operator fun invoke(query: String): Flow<Result<List<Coin>>> = flow {
        // Validate search query
        if (query.length < MIN_SEARCH_LENGTH) {
            emit(
                Result.failure(
                    IllegalArgumentException("Search query must be at least $MIN_SEARCH_LENGTH characters")
                )
            )
            return@flow
        }

        val cleanQuery = query.trim().lowercase()

        emit(
            coinRepository.searchCoins(cleanQuery)
                .map { coins -> filterSearchResults(coins, cleanQuery) }
        )
    }

    /**
     * Filter and rank search results based on relevance
     */
    private fun filterSearchResults(coins: List<Coin>, query: String): List<Coin> {
        return coins
            .filter { coin ->
                // Basic validation
                coin.name.isNotBlank() && coin.symbol.isNotBlank()
            }
            .map { coin ->
                // Calculate relevance score for ranking
                val relevanceScore = calculateRelevanceScore(coin, query)
                coin to relevanceScore
            }
            .filter { (_, score) -> score > 0 } // Only include relevant results
            .sortedByDescending { (_, score) -> score } // Sort by relevance
            .take(MAX_SEARCH_RESULTS) // Limit results
            .map { (coin, _) -> coin }
    }

    /**
     * Calculate relevance score for search ranking
     * Higher score = more relevant
     */
    private fun calculateRelevanceScore(coin: Coin, query: String): Int {
        val name = coin.name.lowercase()
        val symbol = coin.symbol.lowercase()

        var score = 0

        // Exact symbol match gets highest score
        if (symbol == query) {
            score += 100
        }

        // Exact name match gets high score
        if (name == query) {
            score += 90
        }

        // Symbol starts with query
        if (symbol.startsWith(query)) {
            score += 80
        }

        // Name starts with query
        if (name.startsWith(query)) {
            score += 70
        }

        // Symbol contains query
        if (symbol.contains(query)) {
            score += 40
        }

        // Name contains query
        if (name.contains(query)) {
            score += 30
        }

        // Boost score for popular coins (higher market cap rank)
        coin.marketCapRank?.let { rank ->
            if (rank <= 10) score += 20
            else if (rank <= 50) score += 10
            else if (rank <= 100) score += 5
        }

        return score
    }

    companion object {
        private const val MIN_SEARCH_LENGTH = 2
        private const val MAX_SEARCH_RESULTS = 20
    }
}

package com.midas.core.ui.navigation

import kotlinx.serialization.Serializable

/**
 * Type-safe navigation routes using Kotlin Serialization
 */
sealed class Routes {

    /**
     * Home screen - Main coin listing with search and trending
     */
    @Serializable
    data object Home : Routes()

    /**
     * Coin detail screen
     *
     * @param coinId The unique identifier of the coin to display
     */
    @Serializable
    data class Detail(
        val coinId: String
    ) : Routes()

    /**
     * Favorites screen - User's saved favorite coins
     */
    @Serializable
    data object Favorites : Routes()
}

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
}

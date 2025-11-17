package com.dorukkangal.midas.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.midas.core.ui.navigation.Routes
import com.midas.features.detail.ui.detail
import com.midas.features.home.ui.home

@Composable
fun MidasNavGraph(
    navController: NavHostController,
    startDestination: Routes = Routes.Home,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        home(navController)
        detail(navController)
    }
}

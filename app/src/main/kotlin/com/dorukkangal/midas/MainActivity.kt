package com.dorukkangal.midas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.dorukkangal.midas.navigation.MidasNavGraph
import com.dorukkangal.midas.ui.theme.MidasCaseTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MidasCaseTheme {
                val navController = rememberNavController()
                MidasNavGraph(
                    navController = navController
                )
            }
        }
    }
}

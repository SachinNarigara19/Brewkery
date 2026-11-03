package com.clickretina.brewkery.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.clickretina.brewkery.ui.navigation.BrewkeryNavGraph
import com.clickretina.brewkery.ui.theme.BrewkeryTheme

@Composable
fun BrewkeryApp() {
    BrewkeryTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val navController = rememberNavController()
            BrewkeryNavGraph(navController = navController)
        }
    }
}

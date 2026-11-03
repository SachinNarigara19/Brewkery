package com.clickretina.brewkery.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.clickretina.brewkery.ui.navigation.BrewkeryNavGraph
import com.clickretina.brewkery.ui.screens.splash.BrewkerySplashScreen
import com.clickretina.brewkery.ui.theme.BrewkeryTheme

@Composable
fun BrewkeryApp() {
    BrewkeryTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            // Splash screen appears only on app cold start, never during navigation
            var showSplash by rememberSaveable { mutableStateOf(true) }

            AnimatedContent(
                targetState = showSplash,
                transitionSpec = {
                    fadeIn(animationSpec = tween(400)) togetherWith fadeOut(animationSpec = tween(400))
                },
                label = "SplashToMenuTransition"
            ) { isSplash ->
                if (isSplash) {
                    BrewkerySplashScreen(
                        onSplashFinished = { showSplash = false }
                    )
                } else {
                    val navController = rememberNavController()
                    BrewkeryNavGraph(navController = navController)
                }
            }
        }
    }
}

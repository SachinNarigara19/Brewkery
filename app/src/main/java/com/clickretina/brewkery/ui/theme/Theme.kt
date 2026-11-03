package com.clickretina.brewkery.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val CoffeeLightColorScheme = lightColorScheme(
    primary = CoffeePrimary,
    onPrimary = WarmSurface,
    primaryContainer = LatteCream,
    onPrimaryContainer = EspressoDark,
    secondary = CoffeeSecondary,
    onSecondary = WarmSurface,
    secondaryContainer = SurfaceSubtle,
    onSecondaryContainer = EspressoDark,
    tertiary = CaramelAccent,
    onTertiary = WarmSurface,
    tertiaryContainer = LatteCream,
    onTertiaryContainer = EspressoDark,
    background = WarmBackground,
    onBackground = DarkRoastText,
    surface = WarmSurface,
    onSurface = DarkRoastText,
    surfaceVariant = SurfaceSubtle,
    onSurfaceVariant = MediumRoastText,
    outline = OutlineWarm,
    outlineVariant = OutlineWarm.copy(alpha = 0.5f),
    error = ErrorWarmRed,
    onError = WarmSurface
)

@Composable
fun BrewkeryTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Brewkery is primarily designed with a warm coffee light aesthetic
    val colorScheme = CoffeeLightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = colorScheme.background.toArgb()
                window.navigationBarColor = colorScheme.background.toArgb()
                val controller = WindowCompat.getInsetsController(window, view)
                controller.isAppearanceLightStatusBars = true
                controller.isAppearanceLightNavigationBars = true
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}


package com.clickretina.brewkery.ui.screens.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.clickretina.brewkery.ui.theme.DarkBrownText
import com.clickretina.brewkery.ui.theme.EspressoBlack
import com.clickretina.brewkery.ui.theme.MutedBrownText
import com.clickretina.brewkery.ui.theme.OrangeAccent
import com.clickretina.brewkery.ui.theme.WarmBackground
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Premium Brewkery Splash Screen.
 *
 * Warm cream background (#FFF9F5), centered circular BK monogram badge,
 * app title "Brewkery", subtitle "Fresh Roast & Bakes", terracotta orange accent (#E84B2C),
 * and subtle coffee steam animation.
 */
@Composable
fun BrewkerySplashScreen(
    onSplashFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Entrance scale & fade animatables
    val contentAlpha = remember { Animatable(0f) }
    val contentScale = remember { Animatable(0.88f) }

    // Infinite transition for subtle coffee cup steam animation
    val infiniteTransition = rememberInfiniteTransition(label = "CoffeeSteam")
    val steamOffset1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -10f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Steam1"
    )
    val steamAlpha1 by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "SteamAlpha1"
    )

    val steamOffset2 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -12f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, delayMillis = 300, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Steam2"
    )
    val steamAlpha2 by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 0.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, delayMillis = 300, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "SteamAlpha2"
    )

    LaunchedEffect(Unit) {
        // Run entrance animations concurrently
        launch {
            contentAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing)
            )
        }
        launch {
            contentScale.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing)
            )
        }

        // Brief, premium display time then smooth transition to Menu screen
        delay(1150)
        onSplashFinished()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(WarmBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .scale(contentScale.value)
                .alpha(contentAlpha.value)
                .padding(24.dp)
        ) {
            // Subtle Coffee Steam Waves
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier.height(20.dp)
            ) {
                // Steam particle 1
                Box(
                    modifier = Modifier
                        .offset(y = steamOffset1.dp)
                        .alpha(steamAlpha1)
                        .size(width = 3.dp, height = 12.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(OrangeAccent)
                )
                // Steam particle 2
                Box(
                    modifier = Modifier
                        .offset(y = steamOffset2.dp)
                        .alpha(steamAlpha2)
                        .size(width = 3.5.dp, height = 16.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(DarkBrownText)
                )
                // Steam particle 3
                Box(
                    modifier = Modifier
                        .offset(y = steamOffset1.dp)
                        .alpha(steamAlpha1)
                        .size(width = 3.dp, height = 10.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(OrangeAccent)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Centered Circular BK Monogram Logo Badge
            Box(
                modifier = Modifier
                    .size(92.dp)
                    .clip(CircleShape)
                    .background(EspressoBlack)
                    .border(width = 3.dp, color = OrangeAccent, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "B",
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.SansSerif
                    )
                    Text(
                        text = "K",
                        color = OrangeAccent,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.SansSerif
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // App Title "Brewkery"
            Text(
                text = "Brewkery",
                color = DarkBrownText,
                fontSize = 34.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Subtitle "Fresh Roast & Bakes" with Terracotta Accent
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .clip(CircleShape)
                        .background(OrangeAccent)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "FRESH ROAST & BAKES",
                    color = OrangeAccent,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.5.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .clip(CircleShape)
                        .background(OrangeAccent)
                )
            }
        }

        // Bottom Brand Aesthetic Pill
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 36.dp)
                .alpha(contentAlpha.value)
        ) {
            Text(
                text = "ARTISANAL COFFEE CRAFT",
                color = MutedBrownText,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 2.sp
            )
        }
    }
}


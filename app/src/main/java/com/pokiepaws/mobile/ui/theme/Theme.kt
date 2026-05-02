package com.pokiepaws.mobile.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val PokiePawsColorScheme =
    lightColorScheme(
        primary = PokieBlue,
        onPrimary = PokieWhite,
        primaryContainer = PokieBlueLight,
        onPrimaryContainer = PokieDarkText,
        secondary = PokieRed,
        onSecondary = PokieWhite,
        background = PokieBlue,
        onBackground = PokieWhite,
        surface = PokieWhite,
        onSurface = PokieDarkText,
        onSurfaceVariant = PokieLightText,
        error = PokieRed,
    )

@Composable
fun PokiePawsTheme(content: @Composable () -> Unit) {
    val colorScheme = PokiePawsColorScheme
    val view = LocalView.current

    SideEffect {
        val window = (view.context as Activity).window
        window.statusBarColor = PokieBlue.toArgb()
        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}

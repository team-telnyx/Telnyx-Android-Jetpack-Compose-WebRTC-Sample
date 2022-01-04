package com.telnyx.webrtc.composevoicesample.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

private val DarkColorPalette = darkColors(
    primary = DarkMatter,
    primaryVariant = Teal200,
    onPrimary = White,
    secondary = TelnyxGreen,
    secondaryVariant = Teal700,
    onSecondary = White
)

private val LightColorPalette = lightColors(
    primary = DarkMatter,
    primaryVariant = Teal200,
    onPrimary = White,
    secondary = TelnyxGreen,
    secondaryVariant = Teal700,
    onSecondary = White

    /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

@Composable
fun ComposeVoiceSampleTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable() () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
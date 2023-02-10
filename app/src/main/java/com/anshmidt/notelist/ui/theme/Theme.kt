package com.anshmidt.notelist.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color



private val LightColorPalette = lightColors(
    primary = Green500,
    primaryVariant = Green700,
    onPrimary = Color.White,
    secondary = Green500,
    secondaryVariant = Green700,
    onSecondary = Color.White,
    background = Color.White
)

private val DarkColorPalette = darkColors(
    primary = Green200,
    primaryVariant = Green500,
    onPrimary = Color.Black,
    secondary = Green200,
    secondaryVariant = Green500,
    onSecondary = Color.Black,
    background = Color.Black
)

@Composable
fun NoteListTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable() () -> Unit) {
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


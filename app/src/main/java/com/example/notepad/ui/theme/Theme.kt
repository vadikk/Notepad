package com.example.notepad.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable

private val DarkColorPalette = darkColors(
    primary = Gray,
    background = White,
    onBackground = Black,
    onPrimary = YellowColor,
    surface = ChipColor
)

@Composable
fun NotepadTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = DarkColorPalette,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
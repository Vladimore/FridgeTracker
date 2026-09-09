package com.example.fridgetracker.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF66796B),
    secondary = Color(0xFF8A9A8E),
    background = Color(0xFFF8F8F5),
    surface = Color(0xFFF8F8F5)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFB7C9BA),
    secondary = Color(0xFFAAB8AC)
)

@Composable
fun FridgeTrackerTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        shapes = Shapes(
            small = androidx.compose.foundation.shape.RoundedCornerShape(12),
            medium = androidx.compose.foundation.shape.RoundedCornerShape(16),
            large = androidx.compose.foundation.shape.RoundedCornerShape(20)
        ),
        content = content
    )
}

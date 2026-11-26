package com.zero.amaraty.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF315E56),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFD4EAE2),
    onPrimaryContainer = Color(0xFF09201B),
    secondary = Color(0xFF59635F),
    background = Color(0xFFF7F8F6),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFE9EFEC),
    error = Color(0xFFBA1A1A),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFA5D1C3),
    onPrimary = Color(0xFF10372F),
    primaryContainer = Color(0xFF245147),
    onPrimaryContainer = Color(0xFFD4EAE2),
    secondary = Color(0xFFBECAC4),
    background = Color(0xFF111714),
    surface = Color(0xFF18201C),
    surfaceVariant = Color(0xFF29342F),
    error = Color(0xFFFFB4AB),
)

@Composable
fun AmaratyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content,
    )
}

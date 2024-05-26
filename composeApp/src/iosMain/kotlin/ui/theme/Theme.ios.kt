package ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
actual fun AppTheme(
    dynamicColor: Boolean,
    useDarkTheme: Boolean,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        useDarkTheme -> DarkColors
        else -> LightColors
    }

    MaterialTheme(colorScheme = colorScheme, content = content)
}
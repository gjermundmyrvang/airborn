package no.uio.ifi.in2000.team18.airborn.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = White,
    secondary = Blue,
    tertiary = LightBlue,
    primaryContainer = DarkBlue,
    secondaryContainer = DarkGrey,
    tertiaryContainer = Disabled,
    onPrimaryContainer = White,
)

private val LightColorScheme = lightColorScheme(
    primary = White,
    secondary = Blue,
    tertiary = LightBlue,
    primaryContainer = DarkBlue,
    secondaryContainer = DarkGrey,
    tertiaryContainer = BlueSecond,
    onPrimaryContainer = White,
    background = Orange,

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun AirbornTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
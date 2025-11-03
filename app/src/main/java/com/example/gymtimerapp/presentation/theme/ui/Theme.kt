package com.example.gymtimerapp.presentation.theme.ui

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80,
    background = Color.DarkGray,
    surface = Color.DarkGray,
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40,

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun GymTimerAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val extendedColors = if (darkTheme) DarkExtendedColors else LightExtendedColors

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
    ) {
        CompositionLocalProvider(LocalExtendedColors provides extendedColors) {
            content()
        }
    }
}

@Immutable
data class ExtendedColors(
    val cardBackground: Color,
    val cancelButtonColor: Color,
    val deleteButtonColor: Color,
)

internal val LocalExtendedColors = staticCompositionLocalOf {
    ExtendedColors(
        cardBackground = Color.Unspecified,
        cancelButtonColor = Color.Unspecified,
        deleteButtonColor = Color.Unspecified,
    )
}

private val LightExtendedColors = ExtendedColors(
    cardBackground = CardBackgroundLightColor,
    cancelButtonColor = CancelButtonColor,
    deleteButtonColor = DeleteButtonColor,
)

private val DarkExtendedColors = ExtendedColors(
    cardBackground = CardBackgroundDarkColor,
    cancelButtonColor = CancelButtonColor,
    deleteButtonColor = DeleteButtonColor,
)

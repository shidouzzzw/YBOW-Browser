package com.ybow.browser.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.ybow.browser.data.ThemeMode

private val OnPrimaryDark = Color(0xFF00201B)
private val OnSecondaryDark = Color(0xFF0F1A24)
private val OnTertiaryDark = Color(0xFF3A1500)
private val OnErrorDark = Color(0xFF690005)

private val LightColors = lightColorScheme(
    primary = Teal700,
    onPrimary = Snow,
    primaryContainer = Teal050,
    onPrimaryContainer = Color(0xFF00201B),
    secondary = CarbonSoft,
    onSecondary = Snow,
    secondaryContainer = Mist100,
    onSecondaryContainer = Carbon,
    tertiary = Ember900,
    onTertiary = Snow,
    tertiaryContainer = Ember050,
    onTertiaryContainer = Ember900,
    background = Mist50,
    onBackground = Carbon,
    surface = Snow,
    onSurface = Carbon,
    surfaceVariant = Mist100,
    onSurfaceVariant = Slate600,
    outline = Mist300,
    outlineVariant = Mist200,
    error = Danger,
    onError = Snow,
)

private val DarkColors = darkColorScheme(
    primary = Teal400,
    onPrimary = OnPrimaryDark,
    primaryContainer = Teal900,
    onPrimaryContainer = Teal050,
    secondary = Slate400,
    onSecondary = OnSecondaryDark,
    secondaryContainer = Ink800,
    onSecondaryContainer = Mist200,
    tertiary = Ember300,
    onTertiary = OnTertiaryDark,
    tertiaryContainer = Ember900,
    onTertiaryContainer = Ember050,
    background = Ink950,
    onBackground = Mist200,
    surface = Ink900,
    onSurface = Mist200,
    surfaceVariant = Ink800,
    onSurfaceVariant = Slate400,
    outline = Ink600,
    outlineVariant = Ink700,
    error = DangerDark,
    onError = OnErrorDark,
)

val YbowShapes = Shapes(
    extraSmall = RoundedCornerShape(10.dp),
    small = RoundedCornerShape(14.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(26.dp),
    extraLarge = RoundedCornerShape(32.dp),
)

@Composable
fun YbowTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    content: @Composable () -> Unit,
) {
    val dark = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            val controller = WindowCompat.getInsetsController(window, view)
            controller.isAppearanceLightStatusBars = !dark
            controller.isAppearanceLightNavigationBars = !dark
        }
    }

    MaterialTheme(
        colorScheme = if (dark) DarkColors else LightColors,
        typography = YbowTypography,
        shapes = YbowShapes,
        content = content,
    )
}

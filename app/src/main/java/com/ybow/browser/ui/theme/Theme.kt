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

private val OnLagoonDark = Color(0xFF03222A)
private val OnPinkDark = Color(0xFF3A1220)
private val OnErrorDark = Color(0xFF690005)

private val LightColors = lightColorScheme(
    primary = LagoonBlue,
    onPrimary = CloudPaper,
    primaryContainer = LagoonSoft,
    onPrimaryContainer = LagoonDeep,
    inversePrimary = LagoonGlow,
    secondary = BubblegumPink,
    onSecondary = Color(0xFF3A1220),
    secondaryContainer = BubblegumSoft,
    onSecondaryContainer = BubblegumDeep,
    tertiary = LagoonDeep,
    onTertiary = CloudPaper,
    tertiaryContainer = LagoonMist,
    onTertiaryContainer = LagoonDeep,
    background = CloudWhite,
    onBackground = Ink,
    surface = CloudPaper,
    onSurface = Ink,
    surfaceVariant = LagoonMist,
    onSurfaceVariant = InkMuted,
    surfaceDim = Color(0xFFEDF4F6),
    surfaceBright = CloudPaper,
    surfaceContainerLowest = CloudPaper,
    surfaceContainerLow = Color(0xFFFBFDFD),
    surfaceContainer = Color(0xFFF3F9FA),
    surfaceContainerHigh = Color(0xFFEEF6F8),
    surfaceContainerHighest = Color(0xFFE9F3F5),
    inverseSurface = Ink,
    inverseOnSurface = CloudWhite,
    outline = Color(0xFFBBD3D9),
    outlineVariant = CloudLine,
    error = Danger,
    onError = CloudPaper,
    errorContainer = Color(0xFFF9DEDC),
    onErrorContainer = Color(0xFF410E0B),
)

private val DarkColors = darkColorScheme(
    primary = LagoonGlow,
    onPrimary = OnLagoonDark,
    primaryContainer = LagoonGlowDeep,
    onPrimaryContainer = Color(0xFFD6F1F8),
    inversePrimary = LagoonBlue,
    secondary = PinkGlow,
    onSecondary = OnPinkDark,
    secondaryContainer = PinkGlowDeep,
    onSecondaryContainer = Color(0xFFFCE0EA),
    tertiary = Color(0xFF8FD7E4),
    onTertiary = OnLagoonDark,
    tertiaryContainer = NightRaised,
    onTertiaryContainer = NightText,
    background = NightDeep,
    onBackground = NightText,
    surface = NightSurface,
    onSurface = NightText,
    surfaceVariant = NightRaised,
    onSurfaceVariant = NightMuted,
    surfaceDim = Color(0xFF051116),
    surfaceBright = NightRaised,
    surfaceContainerLowest = Color(0xFF051116),
    surfaceContainerLow = Color(0xFF0A1D23),
    surfaceContainer = NightSurface,
    surfaceContainerHigh = NightRaised,
    surfaceContainerHighest = Color(0xFF173942),
    inverseSurface = NightText,
    inverseOnSurface = NightSurface,
    outline = Color(0xFF2C5059),
    outlineVariant = NightLine,
    error = DangerDark,
    onError = OnErrorDark,
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
)

val YbowShapes = Shapes(
    extraSmall = RoundedCornerShape(10.dp),
    small = RoundedCornerShape(14.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(30.dp),
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

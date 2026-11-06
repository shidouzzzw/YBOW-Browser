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

private val OnDeepWater = Color(0xFF03222F)
private val OnSand = Color(0xFF2E1A05)
private val OnErrorDark = Color(0xFF690005)

private val LightColors = lightColorScheme(
    primary = Ocean700,
    onPrimary = Snow,
    primaryContainer = ShallowWater,
    onPrimaryContainer = Ocean800,
    inversePrimary = SeaGlow,
    secondary = Surf,
    onSecondary = Snow,
    secondaryContainer = SurfSoft,
    onSecondaryContainer = Color(0xFF0A3B40),
    tertiary = Sand,
    onTertiary = Snow,
    tertiaryContainer = SandSoft,
    onTertiaryContainer = Color(0xFF4A2A0B),
    background = Foam,
    onBackground = HarborInk,
    surface = Paper,
    onSurface = HarborInk,
    surfaceVariant = MistBlue,
    onSurfaceVariant = Tide,
    surfaceDim = Color(0xFFE6F1F7),
    surfaceBright = Paper,
    surfaceContainerLowest = Snow,
    surfaceContainerLow = Color(0xFFFBFDFE),
    surfaceContainer = Color(0xFFF2F8FC),
    surfaceContainerHigh = Color(0xFFEDF5FA),
    surfaceContainerHighest = Color(0xFFE7F1F7),
    inverseSurface = HarborInk,
    inverseOnSurface = Foam,
    outline = Color(0xFFB9D2E1),
    outlineVariant = Ripple,
    error = Danger,
    onError = Snow,
    errorContainer = Color(0xFFF9DEDC),
    onErrorContainer = Color(0xFF410E0B),
)

private val DarkColors = darkColorScheme(
    primary = SeaGlow,
    onPrimary = OnDeepWater,
    primaryContainer = SeaGlowDeep,
    onPrimaryContainer = SeaFoamText,
    inversePrimary = Ocean700,
    secondary = Color(0xFF7FD1C9),
    onSecondary = OnDeepWater,
    secondaryContainer = Color(0xFF123F44),
    onSecondaryContainer = Color(0xFFC7EFEA),
    tertiary = Color(0xFFE7B27A),
    onTertiary = OnSand,
    tertiaryContainer = Color(0xFF4A2A0B),
    onTertiaryContainer = SandSoft,
    background = Abyss,
    onBackground = SeaFoamText,
    surface = DeepSea,
    onSurface = SeaFoamText,
    surfaceVariant = DeepSeaSoft,
    onSurfaceVariant = SeaMist,
    surfaceDim = Color(0xFF04121A),
    surfaceBright = DeepSeaHigh,
    surfaceContainerLowest = Color(0xFF04121A),
    surfaceContainerLow = Color(0xFF081B25),
    surfaceContainer = DeepSea,
    surfaceContainerHigh = DeepSeaSoft,
    surfaceContainerHighest = DeepSeaHigh,
    inverseSurface = SeaFoamText,
    inverseOnSurface = DeepSea,
    outline = Color(0xFF2B5063),
    outlineVariant = SeaLine,
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

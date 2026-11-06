package com.ybow.browser.ui.theme

import android.app.Activity
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.ybow.browser.data.ThemeMode

/** Material colours derived from a palette, so secondary screens stay on brand. */
fun ybowColorScheme(palette: YbowPalette) = darkColorScheme(
    primary = palette.accent,
    onPrimary = palette.bg,
    primaryContainer = palette.card,
    onPrimaryContainer = palette.accent,
    inversePrimary = palette.accent,
    secondary = palette.pink,
    onSecondary = palette.bg,
    secondaryContainer = palette.card,
    onSecondaryContainer = palette.pink,
    tertiary = palette.accent,
    onTertiary = palette.bg,
    tertiaryContainer = palette.card,
    onTertiaryContainer = palette.accent,
    background = palette.bg,
    onBackground = palette.text,
    surface = palette.card,
    onSurface = palette.text,
    surfaceVariant = palette.card,
    onSurfaceVariant = palette.muted,
    surfaceTint = palette.accent,
    inverseSurface = palette.text,
    inverseOnSurface = palette.bg,
    error = palette.error,
    onError = palette.bg,
    errorContainer = palette.card,
    onErrorContainer = palette.error,
    outline = palette.line,
    outlineVariant = palette.line,
    scrim = palette.bg,
    surfaceBright = palette.card,
    surfaceDim = palette.bg,
    surfaceContainer = palette.card,
    surfaceContainerHigh = palette.card,
    surfaceContainerHighest = palette.card,
    surfaceContainerLow = palette.card,
    surfaceContainerLowest = palette.bg,
)

val YbowShapes = Shapes(
    extraSmall = RoundedCornerShape(10.dp),
    small = RoundedCornerShape(14.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(30.dp),
)

@Suppress("UNUSED_PARAMETER")
@Composable
fun YbowTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    palette: YbowPalette = YbowPalette.Night,
    content: @Composable () -> Unit,
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            val controller = WindowCompat.getInsetsController(window, view)
            controller.isAppearanceLightStatusBars = false
            controller.isAppearanceLightNavigationBars = false
        }
    }

    CompositionLocalProvider(
        LocalYbowPalette provides palette,
        LocalTextStyle provides TextStyle(fontFamily = Figtree, fontSize = 15.sp, color = palette.text),
    ) {
        MaterialTheme(
            colorScheme = ybowColorScheme(palette),
            typography = YbowTypography,
            shapes = YbowShapes,
            content = content,
        )
    }
}

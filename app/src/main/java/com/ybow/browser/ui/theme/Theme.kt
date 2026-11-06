package com.ybow.browser.ui.theme

import android.app.Activity
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.ybow.browser.data.ThemeMode

/**
 * One scheme built from the four palette colours. Every Material slot is filled so
 * no default Material colour can leak into the UI.
 */
private val YbowColors = darkColorScheme(
    primary = YbowAccent,
    onPrimary = YbowBackground,
    primaryContainer = YbowCard,
    onPrimaryContainer = YbowAccent,
    inversePrimary = YbowAccent,
    secondary = YbowAccent,
    onSecondary = YbowBackground,
    secondaryContainer = YbowCard,
    onSecondaryContainer = YbowAccent,
    tertiary = YbowAccent,
    onTertiary = YbowBackground,
    tertiaryContainer = YbowCard,
    onTertiaryContainer = YbowAccent,
    background = YbowBackground,
    onBackground = YbowText,
    surface = YbowCard,
    onSurface = YbowText,
    surfaceVariant = YbowCard,
    onSurfaceVariant = YbowText,
    surfaceTint = YbowAccent,
    inverseSurface = YbowText,
    inverseOnSurface = YbowBackground,
    error = YbowAccent,
    onError = YbowBackground,
    errorContainer = YbowCard,
    onErrorContainer = YbowAccent,
    outline = YbowCard,
    outlineVariant = YbowCard,
    scrim = YbowBackground,
    surfaceBright = YbowCard,
    surfaceDim = YbowBackground,
    surfaceContainer = YbowCard,
    surfaceContainerHigh = YbowCard,
    surfaceContainerHighest = YbowCard,
    surfaceContainerLow = YbowCard,
    surfaceContainerLowest = YbowBackground,
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
    content: @Composable () -> Unit,
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            val controller = WindowCompat.getInsetsController(window, view)
            // The canvas is always dark, so the system bars always draw light icons.
            controller.isAppearanceLightStatusBars = false
            controller.isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = YbowColors,
        typography = YbowTypography,
        shapes = YbowShapes,
        content = content,
    )
}

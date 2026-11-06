package com.ybow.browser.ui.theme

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer

/**
 * Every duration, easing curve and scale used by the app's motion lives here, so the
 * whole interface keeps one rhythm.
 */
object YbowMotion {

    // Durations, in milliseconds.
    const val PageTransitionMillis = 260
    const val SearchOverlayMillis = 240
    const val PressInMillis = 90
    const val PressOutMillis = 160
    const val ShortcutInMillis = 220

    /** The page slides by a fraction of its own height: content / divisor. */
    const val PageSlideDivisor = 40
    const val OverlaySlideDivisor = 24

    // Scales.
    const val SearchPressScale = 1.03f
    const val BarPressScale = 0.9f
    const val ShortcutStartScale = 0.86f

    // Easing curves.
    val Enter: Easing = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1f)
    val Exit: Easing = CubicBezierEasing(0.3f, 0f, 0.8f, 0.15f)
    val ShortcutIn: Easing = CubicBezierEasing(0.2f, 0f, 0f, 1f)
}

/**
 * Scales the child while it is pressed and springs back on release. The same
 * interaction source must be handed to the clickable so press state reaches it.
 */
@Composable
fun Modifier.pressScale(
    interactionSource: InteractionSource,
    pressedScale: Float = YbowMotion.BarPressScale,
): Modifier {
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) pressedScale else 1f,
        animationSpec = tween(
            durationMillis = if (pressed) YbowMotion.PressInMillis else YbowMotion.PressOutMillis,
            easing = if (pressed) YbowMotion.Exit else YbowMotion.Enter,
        ),
        label = "pressScale",
    )
    return graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
}

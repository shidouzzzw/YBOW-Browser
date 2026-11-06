package com.ybow.browser.ui.theme

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer

/**
 * Every duration, curve and scale of the interface, tuned for a snappy, premium feel.
 */
object YbowMotion {

    // Press feedback: instant press-in, gentle controlled release.
    const val PressDownMillis = 90
    const val PressUpMillis = 180

    // Durations, in milliseconds.
    const val FastMillis = 160
    const val DurMillis = 220
    const val SlowMillis = 320
    const val RiseMillis = 300
    const val RiseStepMillis = 35
    const val PopMillis = 400
    const val ShakeMillis = 360
    const val LoadMillis = 900
    const val ToastMillis = 2300L
    const val LongPressMillis = 440L
    const val CloseTabMillis = 180L
    const val DeleteShortcutMillis = 200L
    const val FocusDelayMillis = 200L

    // Curves.
    val Ease: Easing = CubicBezierEasing(0.22f, 0.9f, 0.3f, 1f)
    val EaseOut: Easing = CubicBezierEasing(0f, 0f, 0.2f, 1f)
    val Spring: Easing = CubicBezierEasing(0.34f, 1.35f, 0.64f, 1f)

    // Press scales.
    const val PressTile = 0.95f
    const val PressPillButton = 0.93f
    const val PressGo = 0.92f
    const val PressAdd = 0.88f
    const val PressCard = 0.985f
    const val PressTabCard = 0.98f
    const val PressNewTab = 0.97f
    const val PressButton = 0.96f
    const val PressMenu = 0.94f
    const val PressReload = 0.88f
    const val HeldTile = 1.05f
}

/** Scales its child while pressed and springs back on release. */
@Composable
fun Modifier.pressScale(
    interactionSource: InteractionSource,
    pressedScale: Float = YbowMotion.PressButton,
    rotate: Float = 0f,
    highlight: Boolean = false,
): Modifier {
    val pressed by interactionSource.collectIsPressedAsState()
    val progress by animateFloatAsState(
        targetValue = if (pressed) 1f else 0f,
        animationSpec = tween(
            durationMillis = if (pressed) YbowMotion.PressDownMillis else YbowMotion.PressUpMillis,
            easing = if (pressed) YbowMotion.EaseOut else YbowMotion.Spring,
        ),
        label = "pressScale",
    )
    val scale = 1f + (pressedScale - 1f) * progress
    val alphaPress = if (highlight) 1f - 0.15f * progress else 1f
    return graphicsLayer {
        scaleX = scale
        scaleY = scale
        rotationZ = rotate * progress
        alpha = alphaPress
    }
}

/**
 * The staggered "rise" entrance: fade up 10px with a 35ms delay per index.
 * Runs once when the item first appears.
 */
@Composable
fun Modifier.rise(index: Int = 0, enabled: Boolean = true): Modifier {
    var played by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { played = true }
    val progress by animateFloatAsState(
        targetValue = if (enabled && played) 1f else if (enabled) 0f else 1f,
        animationSpec = tween(
            durationMillis = YbowMotion.RiseMillis,
            delayMillis = index * YbowMotion.RiseStepMillis,
            easing = YbowMotion.EaseOut,
        ),
        label = "rise",
    )
    return graphicsLayer {
        alpha = progress
        translationY = (1f - progress) * 10.dp.toPx()
    }
}

private val Int.dp get() = androidx.compose.ui.unit.Dp(this.toFloat())

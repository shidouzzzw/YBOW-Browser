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
 * Every duration, curve and scale of the interface, matching the design tokens
 * (--dur, --dur-slow, --ease, --spring) of the original mock.
 */
object YbowMotion {

    // Durations, in milliseconds.
    const val DurMillis = 280
    const val SlowMillis = 460
    const val RiseMillis = 620
    const val RiseStepMillis = 70
    const val PopMillis = 500
    const val ShakeMillis = 380
    const val LoadMillis = 1100
    const val ToastMillis = 2300L
    const val LongPressMillis = 480L
    const val CloseTabMillis = 200L
    const val DeleteShortcutMillis = 230L
    const val FocusDelayMillis = 320L

    // Curves.
    val Ease: Easing = CubicBezierEasing(0.22f, 0.9f, 0.3f, 1f)
    val Spring: Easing = CubicBezierEasing(0.34f, 1.56f, 0.64f, 1f)

    // Press scales.
    const val PressTile = 0.9f
    const val PressPillButton = 0.86f
    const val PressGo = 0.88f
    const val PressAdd = 0.86f
    const val PressCard = 0.98f
    const val PressTabCard = 0.97f
    const val PressNewTab = 0.96f
    const val PressButton = 0.96f
    const val PressMenu = 0.93f
    const val PressReload = 0.9f
    const val HeldTile = 1.06f
}

/** Scales its child while pressed and springs back on release. */
@Composable
fun Modifier.pressScale(
    interactionSource: InteractionSource,
    pressedScale: Float = YbowMotion.PressButton,
    rotate: Float = 0f,
): Modifier {
    val pressed by interactionSource.collectIsPressedAsState()
    val progress by animateFloatAsState(
        targetValue = if (pressed) 1f else 0f,
        animationSpec = tween(YbowMotion.DurMillis, easing = YbowMotion.Spring),
        label = "pressScale",
    )
    val scale = 1f + (pressedScale - 1f) * progress
    return graphicsLayer {
        scaleX = scale
        scaleY = scale
        rotationZ = rotate * progress
    }
}

/**
 * The staggered "rise" entrance of the design: fade up 14px with a 70ms delay per
 * index. Runs once when the item first appears.
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
            easing = YbowMotion.Ease,
        ),
        label = "rise",
    )
    return graphicsLayer {
        alpha = progress
        translationY = (1f - progress) * 14.dp.toPx()
    }
}

private val Int.dp get() = androidx.compose.ui.unit.Dp(this.toFloat())

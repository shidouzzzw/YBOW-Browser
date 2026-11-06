package com.ybow.browser.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

/**
 * Palette taken from the Ybow design: obsidian stage, elevated cards, electric cyan
 * accent, high-contrast readable text, muted secondary tone and the incognito pink.
 * Every colour in the app comes from a [YbowPalette] instance, so the incognito theme
 * can swap all of them at once.
 */
object YbowColors {
    val Background = Color(0xFF070C0F)
    val Card = Color(0xFF0E171C)
    val CardRaised = Color(0xFF142027)
    val Accent = Color(0xFF3FE0D0)
    val AccentGlow = Color(0x333FE0D0)
    val Text = Color(0xFFE8F3F6)
    val Muted = Color(0xFF97B0B9)
    val Pink = Color(0xFFFF7EB6)
    val Line = Color(0x1CE8F3F6) // subtle hairline border
    val Stage = Color(0xFF05080A)
    val Error = Color(0xFFF07178)
    val LogoBlue = Color(0xFF4DD0E1)
    val LogoPink = Color(0xFFFF7EB6)

    // Incognito variant
    val IncognitoBackground = Color(0xFF110B17)
    val IncognitoCard = Color(0xFF1B1224)
}

/** One complete set of colours; the incognito theme is the same shape, recoloured. */
data class YbowPalette(
    val bg: Color,
    val card: Color,
    val cardRaised: Color = YbowColors.CardRaised,
    val accent: Color,
    val text: Color,
    val muted: Color,
    val pink: Color,
    val line: Color,
    val error: Color,
    val isIncognito: Boolean = false,
) {
    companion object {
        val Night = YbowPalette(
            bg = YbowColors.Background,
            card = YbowColors.Card,
            cardRaised = YbowColors.CardRaised,
            accent = YbowColors.Accent,
            text = YbowColors.Text,
            muted = YbowColors.Muted,
            pink = YbowColors.Pink,
            line = YbowColors.Line,
            error = YbowColors.Error,
        )

        val Incognito = Night.copy(
            bg = YbowColors.IncognitoBackground,
            card = YbowColors.IncognitoCard,
            cardRaised = Color(0xFF24182E),
            accent = YbowColors.Pink,
            isIncognito = true,
        )
    }
}

val LocalYbowPalette = staticCompositionLocalOf { YbowPalette.Night }

/** Deterministic per-site colour: same hash as the HTML mock. */
fun siteHue(host: String): Int {
    var h = 0
    for (char in host) {
        h = (h * 31 + char.code) % 360
    }
    return h
}

fun siteIconBackground(host: String): Color = Color.hsl(siteHue(host).toFloat(), 0.44f, 0.26f)

fun siteIconForeground(host: String): Color = Color.hsl(siteHue(host).toFloat(), 0.90f, 0.88f)

/** Kept for surfaces that need a readable colour on top of an arbitrary fill. */
fun Color.readableOn(): Color =
    if (luminance() > 0.5f) Color(0xFF070C0F) else Color(0xFFE8F3F6)

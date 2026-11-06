package com.ybow.browser.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

/**
 * Palette taken from the Ybow design: dark stage, card, one accent, one text tone,
 * plus a muted label colour and the incognito pink. Every colour in the app comes
 * from a [YbowPalette] instance, so the incognito theme can swap all of them at once.
 */
object YbowColors {
    val Background = Color(0xFF0B1215)
    val Card = Color(0xFF121C21)
    val Accent = Color(0xFF4DD0E1)
    val Text = Color(0xFFE6F1F4)
    val Muted = Color(0xFF8FA6AE)
    val Pink = Color(0xFFE7A3C9)
    val Line = Color(0x17E6F1F4) // rgba(230,241,244,.09)
    val Stage = Color(0xFF060A0C)
    val Error = Color(0xFFF2A0A0)
    val LogoBlue = Color(0xFF5CC8E0)
    val LogoPink = Color(0xFFE3A6C8)

    // Incognito variant
    val IncognitoBackground = Color(0xFF130E16)
    val IncognitoCard = Color(0xFF1D1623)
}

/** One complete set of colours; the incognito theme is the same shape, recoloured. */
data class YbowPalette(
    val bg: Color,
    val card: Color,
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

fun siteIconBackground(host: String): Color = Color.hsl(siteHue(host).toFloat(), 0.42f, 0.28f)

fun siteIconForeground(host: String): Color = Color.hsl(siteHue(host).toFloat(), 0.85f, 0.84f)

/** Kept for surfaces that need a readable colour on top of an arbitrary fill. */
fun Color.readableOn(): Color =
    if (luminance() > 0.5f) Color(0xFF0B1215) else Color(0xFFE6F1F4)

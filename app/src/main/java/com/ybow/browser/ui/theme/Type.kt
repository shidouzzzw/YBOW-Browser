package com.ybow.browser.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.ybow.browser.R

/** Figtree for the interface and Unbounded for the brand, as in the design. */
val Figtree = FontFamily(
    Font(R.font.figtree_var, FontWeight.Light),
    Font(R.font.figtree_var, FontWeight.Normal),
    Font(R.font.figtree_var, FontWeight.Medium),
    Font(R.font.figtree_var, FontWeight.SemiBold),
    Font(R.font.figtree_var, FontWeight.Bold),
)

val Unbounded = FontFamily(
    Font(R.font.unbounded_var, FontWeight.SemiBold),
    Font(R.font.unbounded_var, FontWeight.Bold),
)

val YbowTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = Unbounded,
        fontWeight = FontWeight.Bold,
        fontSize = 38.sp,
        lineHeight = 38.sp,
        letterSpacing = 11.sp,
    ),
    displaySmall = TextStyle(
        fontFamily = Unbounded,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 34.sp,
    ),
    headlineLarge = TextStyle(
        fontFamily = Figtree,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 34.sp,
    ),
    headlineMedium = TextStyle(
        fontFamily = Figtree,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 30.sp,
    ),
    titleLarge = TextStyle(
        fontFamily = Figtree,
        fontWeight = FontWeight.Bold,
        fontSize = 21.sp,
        lineHeight = 27.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = Figtree,
        fontWeight = FontWeight.SemiBold,
        fontSize = 17.sp,
        lineHeight = 23.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = Figtree,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 23.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = Figtree,
        fontWeight = FontWeight.Normal,
        fontSize = 13.5.sp,
        lineHeight = 19.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = Figtree,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.5.sp,
        lineHeight = 19.sp,
    ),
    labelMedium = TextStyle(
        fontFamily = Figtree,
        fontWeight = FontWeight.Medium,
        fontSize = 12.5.sp,
        lineHeight = 17.sp,
    ),
)

/** The wide, letterspaced YBOW wordmark. */
val TextWordmark = TextStyle(
    fontFamily = Unbounded,
    fontWeight = FontWeight.Bold,
    fontSize = 38.sp,
    lineHeight = 38.sp,
    letterSpacing = 11.4.sp,
)

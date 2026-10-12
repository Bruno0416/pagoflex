package com.pagoflex.app.core.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// considerar cambiar fuente por inter (Google Fonts)
private val family = FontFamily.SansSerif

val Typography = Typography(
    // ---- display ----
    displayLarge = TextStyle(
        fontFamily = family,
        fontSize = 57.sp,
        fontWeight = FontWeight.Normal,
    ),
    displayMedium = TextStyle(
        fontFamily = family,
        fontSize = 45.sp,
        fontWeight = FontWeight.Normal,
    ),
    displaySmall = TextStyle(
        fontFamily = family,
        fontSize = 36.sp,
        fontWeight = FontWeight.Normal,
    ),

    // ---- headline ----
    headlineLarge = TextStyle(
        fontFamily = family,
        fontSize = 32.sp,
        fontWeight = FontWeight.Normal,
    ),
    headlineMedium = TextStyle(
        fontFamily = family,
        fontSize = 28.sp,
        fontWeight = FontWeight.Normal,
    ),
    headlineSmall = TextStyle(
        fontFamily = family,
        fontSize = 24.sp,
        fontWeight = FontWeight.Normal,
    ),

    // ---- title ----
    titleLarge = TextStyle(
        fontFamily = family,
        fontSize = 22.sp,
        fontWeight = FontWeight.Normal,
    ),
    titleMedium = TextStyle(
        fontFamily = family,
        fontSize = 16.sp,
        fontWeight = FontWeight.Medium,
    ),
    titleSmall = TextStyle(
        fontFamily = family,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
    ),

    // ---- body ----
    bodyLarge = TextStyle(
        fontFamily = family,
        fontSize = 16.sp,
        fontWeight = FontWeight.Normal,
    ),
    bodyMedium = TextStyle(
        fontFamily = family,
        fontSize = 14.sp,
        fontWeight = FontWeight.Normal,
    ),
    bodySmall = TextStyle(
        fontFamily = family,
        fontSize = 12.sp,
        fontWeight = FontWeight.Normal,
    ),

    // ---- label ----
    labelLarge = TextStyle(
        fontFamily = family,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
    ),
    labelMedium = TextStyle(
        fontFamily = family,
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
    ),
    labelSmall = TextStyle(
        fontFamily = family,
        fontSize = 10.sp,
        fontWeight = FontWeight.Medium,
    ),
)
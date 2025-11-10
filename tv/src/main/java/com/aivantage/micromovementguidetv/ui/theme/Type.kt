package com.aivantage.micromovementguidetv.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Typography

// Base Material typography styles
@OptIn(ExperimentalTvMaterial3Api::class)
private val baseTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp
    ),
    displayMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 45.sp,
        lineHeight = 52.sp,
        letterSpacing = 0.sp
    ),
    displaySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = 0.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    titleSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)

@OptIn(ExperimentalTvMaterial3Api::class)
fun ScaledTypography(fontSizeMultiplier: Float): Typography {
    fun TextStyle.scale(multiplier: Float): TextStyle {
        return this.copy(
            fontSize = if (this.fontSize != TextUnit.Unspecified) this.fontSize * multiplier else this.fontSize,
            lineHeight = if (this.lineHeight != TextUnit.Unspecified) this.lineHeight * multiplier else this.lineHeight
        )
    }

    return Typography(
        displayLarge = baseTypography.displayLarge.scale(fontSizeMultiplier),
        displayMedium = baseTypography.displayMedium.scale(fontSizeMultiplier),
        displaySmall = baseTypography.displaySmall.scale(fontSizeMultiplier),
        headlineLarge = baseTypography.headlineLarge.scale(fontSizeMultiplier),
        headlineMedium = baseTypography.headlineMedium.scale(fontSizeMultiplier),
        headlineSmall = baseTypography.headlineSmall.scale(fontSizeMultiplier),
        titleLarge = baseTypography.titleLarge.scale(fontSizeMultiplier),
        titleMedium = baseTypography.titleMedium.scale(fontSizeMultiplier),
        titleSmall = baseTypography.titleSmall.scale(fontSizeMultiplier),
        bodyLarge = baseTypography.bodyLarge.scale(fontSizeMultiplier),
        bodyMedium = baseTypography.bodyMedium.scale(fontSizeMultiplier),
        bodySmall = baseTypography.bodySmall.scale(fontSizeMultiplier),
        labelLarge = baseTypography.labelLarge.scale(fontSizeMultiplier),
        labelMedium = baseTypography.labelMedium.scale(fontSizeMultiplier),
        labelSmall = baseTypography.labelSmall.scale(fontSizeMultiplier)
    )
}

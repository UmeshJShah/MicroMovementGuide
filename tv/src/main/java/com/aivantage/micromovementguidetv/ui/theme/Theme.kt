package com.aivantage.micromovementguidetv.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.darkColorScheme
import androidx.tv.material3.lightColorScheme
import androidx.tv.material3.ColorScheme
import com.aivantage.micromovementguidetv.AppSettings
import androidx.compose.ui.graphics.Color // <--- ADDED THIS IMPORT

@OptIn(ExperimentalTvMaterial3Api::class)
enum class AppTheme(val themeName: String) {
    DEFAULT("Default") {
        override fun getLightColorScheme(): ColorScheme = lightColorScheme(
            primary = Purple40,
            secondary = PurpleGrey40,
            tertiary = Pink40
        )

        override fun getDarkColorScheme(): ColorScheme = darkColorScheme(
            primary = Purple80,
            secondary = PurpleGrey80,
            tertiary = Pink80
        )
    },
    NEO_SOFT_GRADIENT("Neo-Soft Gradient") {
        override fun getLightColorScheme(): ColorScheme = lightColorScheme(
            primary = NeoSoftPrimaryLight,
            secondary = NeoSoftSecondary,
            tertiary = NeoSoftAccent,
            background = NeoSoftBackgroundLight,
            surface = NeoSoftBackgroundLight,
            onPrimary = Color.White,
            onSecondary = Color.Black,
            onTertiary = Color.Black,
            onBackground = Color.Black,
            onSurface = Color.Black
        )

        override fun getDarkColorScheme(): ColorScheme = darkColorScheme(
            primary = NeoSoftPrimaryDark,
            secondary = NeoSoftSecondary,
            tertiary = NeoSoftAccent,
            background = NeoSoftBackgroundDark,
            surface = NeoSoftBackgroundDark,
            onPrimary = Color.White,
            onSecondary = Color.Black,
            onTertiary = Color.Black,
            onBackground = Color.White,
            onSurface = Color.White
        )
    },
    VIBRANT_ENERGY_BURST("Vibrant Energy Burst") {
        override fun getLightColorScheme(): ColorScheme = darkColorScheme( // This theme is primarily dark
            primary = VibrantEnergyPrimary,
            secondary = VibrantEnergySecondary,
            tertiary = VibrantEnergyAccent,
            background = VibrantEnergyBackgroundDark,
            surface = VibrantEnergyBackgroundDark,
            onPrimary = Color.Black,
            onSecondary = Color.Black,
            onTertiary = Color.Black,
            onBackground = Color.White,
            onSurface = Color.White
        )

        override fun getDarkColorScheme(): ColorScheme = darkColorScheme(
            primary = VibrantEnergyPrimary,
            secondary = VibrantEnergySecondary,
            tertiary = VibrantEnergyAccent,
            background = VibrantEnergyBackgroundDark,
            surface = VibrantEnergyBackgroundDark,
            onPrimary = Color.Black,
            onSecondary = Color.Black,
            onTertiary = Color.Black,
            onBackground = Color.White,
            onSurface = Color.White
        )
    },
    ORGANIC_EARTH_TONES("Organic Earth Tones") {
        override fun getLightColorScheme(): ColorScheme = lightColorScheme(
            primary = OrganicEarthPrimary,
            secondary = OrganicEarthSecondary,
            tertiary = OrganicEarthAccent,
            background = OrganicEarthBackgroundLight,
            surface = OrganicEarthBackgroundLight,
            onPrimary = Color.White,
            onSecondary = Color.Black,
            onTertiary = Color.Black,
            onBackground = Color.Black,
            onSurface = Color.Black
        )

        override fun getDarkColorScheme(): ColorScheme = darkColorScheme(
            primary = OrganicEarthPrimary,
            secondary = OrganicEarthSecondary,
            tertiary = OrganicEarthAccent,
            background = OrganicEarthBackgroundDark,
            surface = OrganicEarthBackgroundDark,
            onPrimary = Color.White,
            onSecondary = Color.Black,
            onTertiary = Color.Black,
            onBackground = Color.White,
            onSurface = Color.White
        )
    },
    CYBER_LUMINANCE("Cyber-Luminance") {
        override fun getLightColorScheme(): ColorScheme = darkColorScheme( // This theme is primarily dark
            primary = CyberLuminancePrimary,
            secondary = CyberLuminanceSecondary,
            tertiary = CyberLuminanceAccent,
            background = CyberLuminanceBackgroundDark,
            surface = CyberLuminanceBackgroundDark,
            onPrimary = Color.White,
            onSecondary = Color.Black,
            onTertiary = Color.Black,
            onBackground = Color.White,
            onSurface = Color.White
        )

        override fun getDarkColorScheme(): ColorScheme = darkColorScheme(
            primary = CyberLuminancePrimary,
            secondary = CyberLuminanceSecondary,
            tertiary = CyberLuminanceAccent,
            background = CyberLuminanceBackgroundDark,
            surface = CyberLuminanceBackgroundDark,
            onPrimary = Color.White,
            onSecondary = Color.Black,
            onTertiary = Color.Black,
            onBackground = Color.White,
            onSurface = Color.White
        )
    },
    RETRO_PLAYFUL_ARCADE("Retro-Playful Arcade") {
        override fun getLightColorScheme(): ColorScheme = darkColorScheme( // This theme is primarily dark
            primary = RetroArcadePrimary,
            secondary = RetroArcadeSecondary,
            tertiary = RetroArcadeAccent,
            background = RetroArcadeBackgroundDark,
            surface = RetroArcadeBackgroundDark,
            onPrimary = Color.White,
            onSecondary = Color.Black,
            onTertiary = Color.Black,
            onBackground = Color.White,
            onSurface = Color.White
        )

        override fun getDarkColorScheme(): ColorScheme = darkColorScheme(
            primary = RetroArcadePrimary,
            secondary = RetroArcadeSecondary,
            tertiary = RetroArcadeAccent,
            background = RetroArcadeBackgroundDark,
            surface = RetroArcadeBackgroundDark,
            onPrimary = Color.White,
            onSecondary = Color.Black,
            onTertiary = Color.Black,
            onBackground = Color.White,
            onSurface = Color.White
        )
    };

    abstract fun getLightColorScheme(): ColorScheme
    abstract fun getDarkColorScheme(): ColorScheme
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun MicroMovementGuideTheme(
    appSettings: AppSettings = AppSettings(),
    isInDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (isInDarkTheme) {
        appSettings.appTheme.getDarkColorScheme()
    } else {
        appSettings.appTheme.getLightColorScheme()
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = ScaledTypography(appSettings.fontSizeMultiplier),
        content = content
    )
}
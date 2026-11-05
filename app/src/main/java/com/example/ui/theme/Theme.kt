package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.example.data.preferences.AppThemeMode

private val DarkColorScheme = darkColorScheme(
    primary = AccentOrange,
    onPrimary = SoftWhite,
    primaryContainer = KeyOperatorTopDark,
    onPrimaryContainer = SoftWhite,
    secondary = ElectricBlue,
    onSecondary = SoftWhite,
    secondaryContainer = KeyNumberTopDark,
    onSecondaryContainer = SoftWhite,
    tertiary = AccentGreen,
    background = CalculatorBodyDark,
    onBackground = SoftWhite,
    surface = DisplayBackgroundDark,
    onSurface = SoftWhite,
    surfaceVariant = KeyNumberTopDark,
    onSurfaceVariant = MutedGray,
    outline = DisplayBorderDark,
    error = AccentRed,
    onError = SoftWhite
)

private val LightColorScheme = lightColorScheme(
    primary = AccentOrange,
    onPrimary = SoftWhite,
    primaryContainer = KeyOperatorTopLight,
    onPrimaryContainer = LightTextPrimary,
    secondary = ElectricBlue,
    onSecondary = SoftWhite,
    secondaryContainer = KeyNumberTopLight,
    onSecondaryContainer = LightTextPrimary,
    tertiary = AccentGreen,
    background = CalculatorBodyLight,
    onBackground = LightTextPrimary,
    surface = DisplayBackgroundLight,
    onSurface = LightTextPrimary,
    surfaceVariant = KeyNumberBottomLight,
    onSurfaceVariant = LightTextSecondary,
    outline = DisplayBorderLight,
    error = AccentRed,
    onError = SoftWhite
)

@Immutable
data class CalculatorColors(
    val calculatorBody: Color,
    val displayBackground: Color,
    val displayBorder: Color,

    val numberTop: Color,
    val numberBottom: Color,
    val numberBevel: Color,

    val operatorTop: Color,
    val operatorBottom: Color,
    val operatorBevel: Color,

    val actionTop: Color,
    val actionBottom: Color,
    val actionBevel: Color,

    val equalsTop: Color,
    val equalsBottom: Color,
    val equalsBevel: Color,

    val scientificTop: Color,
    val scientificBottom: Color,
    val scientificBevel: Color,

    val textPrimary: Color,
    val textSecondary: Color,
    val textOperator: Color,
    val textEquals: Color,
    val borderStroke: Color,
    val activeIndicator: Color
) {
    val midnightBackground: Color get() = calculatorBody
    val numberButtonBg: Color get() = numberBottom
    val operatorButtonBg: Color get() = operatorBottom
    val actionButtonBg: Color get() = actionBottom
    val equalsButtonBg: Color get() = equalsBottom
    val scientificButtonBg: Color get() = scientificBottom
}

val LocalCalculatorColors = staticCompositionLocalOf {
    CalculatorColors(
        calculatorBody = CalculatorBodyDark,
        displayBackground = DisplayBackgroundDark,
        displayBorder = DisplayBorderDark,
        numberTop = KeyNumberTopDark,
        numberBottom = KeyNumberBottomDark,
        numberBevel = KeyNumberBevelDark,
        operatorTop = KeyOperatorTopDark,
        operatorBottom = KeyOperatorBottomDark,
        operatorBevel = KeyOperatorBevelDark,
        actionTop = KeyActionTopDark,
        actionBottom = KeyActionBottomDark,
        actionBevel = KeyActionBevelDark,
        equalsTop = KeyEqualsTopDark,
        equalsBottom = KeyEqualsBottomDark,
        equalsBevel = KeyEqualsBevelDark,
        scientificTop = KeyScientificTopDark,
        scientificBottom = KeyScientificBottomDark,
        scientificBevel = KeyScientificBevelDark,
        textPrimary = SoftWhite,
        textSecondary = MutedGray,
        textOperator = SoftWhite,
        textEquals = SoftWhite,
        borderStroke = DisplayBorderDark,
        activeIndicator = AccentOrange
    )
}

@Composable
fun JzCalculatorTheme(
    themeMode: AppThemeMode = AppThemeMode.DARK,
    content: @Composable () -> Unit
) {
    val isDark = when (themeMode) {
        AppThemeMode.DARK -> true
        AppThemeMode.LIGHT -> false
        AppThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    val colorScheme = if (isDark) DarkColorScheme else LightColorScheme

    val calcColors = if (isDark) {
        CalculatorColors(
            calculatorBody = CalculatorBodyDark,
            displayBackground = DisplayBackgroundDark,
            displayBorder = DisplayBorderDark,
            numberTop = KeyNumberTopDark,
            numberBottom = KeyNumberBottomDark,
            numberBevel = KeyNumberBevelDark,
            operatorTop = KeyOperatorTopDark,
            operatorBottom = KeyOperatorBottomDark,
            operatorBevel = KeyOperatorBevelDark,
            actionTop = KeyActionTopDark,
            actionBottom = KeyActionBottomDark,
            actionBevel = KeyActionBevelDark,
            equalsTop = KeyEqualsTopDark,
            equalsBottom = KeyEqualsBottomDark,
            equalsBevel = KeyEqualsBevelDark,
            scientificTop = KeyScientificTopDark,
            scientificBottom = KeyScientificBottomDark,
            scientificBevel = KeyScientificBevelDark,
            textPrimary = SoftWhite,
            textSecondary = MutedGray,
            textOperator = SoftWhite,
            textEquals = SoftWhite,
            borderStroke = DisplayBorderDark,
            activeIndicator = AccentOrange
        )
    } else {
        CalculatorColors(
            calculatorBody = CalculatorBodyLight,
            displayBackground = DisplayBackgroundLight,
            displayBorder = DisplayBorderLight,
            numberTop = KeyNumberTopLight,
            numberBottom = KeyNumberBottomLight,
            numberBevel = KeyNumberBevelLight,
            operatorTop = KeyOperatorTopLight,
            operatorBottom = KeyOperatorBottomLight,
            operatorBevel = KeyOperatorBevelLight,
            actionTop = KeyActionTopLight,
            actionBottom = KeyActionBottomLight,
            actionBevel = KeyActionBevelLight,
            equalsTop = KeyEqualsTopLight,
            equalsBottom = KeyEqualsBottomLight,
            equalsBevel = KeyEqualsBevelLight,
            scientificTop = KeyScientificTopLight,
            scientificBottom = KeyScientificBottomLight,
            scientificBevel = KeyScientificBevelLight,
            textPrimary = LightTextPrimary,
            textSecondary = LightTextSecondary,
            textOperator = LightTextPrimary,
            textEquals = SoftWhite,
            borderStroke = DisplayBorderLight,
            activeIndicator = AccentOrange
        )
    }

    androidx.compose.runtime.CompositionLocalProvider(
        LocalCalculatorColors provides calcColors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            content = content
        )
    }
}

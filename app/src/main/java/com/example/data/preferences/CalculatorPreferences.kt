package com.example.data.preferences

import android.content.Context
import android.content.SharedPreferences
import com.example.engine.AngleUnit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class AppThemeMode {
    SYSTEM,
    DARK,
    LIGHT
}

enum class NumberFormatStyle(val label: String, val example: String) {
    COMMA_DOT("Standard (1,234.56)", "1,234.56"),
    DOT_COMMA("European (1.234,56)", "1.234,56"),
    SPACE_DOT("Space (1 234.56)", "1 234.56"),
    NONE("Compact (1234.56)", "1234.56")
}

data class UserPreferences(
    val themeMode: AppThemeMode = AppThemeMode.DARK,
    val soundEnabled: Boolean = true,
    val hapticEnabled: Boolean = true,
    val angleUnit: AngleUnit = AngleUnit.DEGREE,
    val decimalPrecision: Int = -1, // -1 for auto
    val formatStyle: NumberFormatStyle = NumberFormatStyle.COMMA_DOT
)

class CalculatorPreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("jz_calc_preferences", Context.MODE_PRIVATE)

    private val _preferences = MutableStateFlow(loadPreferences())
    val preferences: StateFlow<UserPreferences> = _preferences.asStateFlow()

    private fun loadPreferences(): UserPreferences {
        val themeStr = prefs.getString("theme_mode", AppThemeMode.DARK.name) ?: AppThemeMode.DARK.name
        val theme = runCatching { AppThemeMode.valueOf(themeStr) }.getOrDefault(AppThemeMode.DARK)

        val sound = prefs.getBoolean("sound_enabled", true)
        val haptic = prefs.getBoolean("haptic_enabled", true)

        val angleStr = prefs.getString("angle_unit", AngleUnit.DEGREE.name) ?: AngleUnit.DEGREE.name
        val angle = runCatching { AngleUnit.valueOf(angleStr) }.getOrDefault(AngleUnit.DEGREE)

        val precision = prefs.getInt("decimal_precision", -1)

        val formatStr = prefs.getString("format_style", NumberFormatStyle.COMMA_DOT.name)
            ?: NumberFormatStyle.COMMA_DOT.name
        val format = runCatching { NumberFormatStyle.valueOf(formatStr) }.getOrDefault(NumberFormatStyle.COMMA_DOT)

        return UserPreferences(
            themeMode = theme,
            soundEnabled = sound,
            hapticEnabled = haptic,
            angleUnit = angle,
            decimalPrecision = precision,
            formatStyle = format
        )
    }

    fun setThemeMode(mode: AppThemeMode) {
        prefs.edit().putString("theme_mode", mode.name).apply()
        _preferences.value = _preferences.value.copy(themeMode = mode)
    }

    fun setSoundEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("sound_enabled", enabled).apply()
        _preferences.value = _preferences.value.copy(soundEnabled = enabled)
    }

    fun setHapticEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("haptic_enabled", enabled).apply()
        _preferences.value = _preferences.value.copy(hapticEnabled = enabled)
    }

    fun setAngleUnit(unit: AngleUnit) {
        prefs.edit().putString("angle_unit", unit.name).apply()
        _preferences.value = _preferences.value.copy(angleUnit = unit)
    }

    fun setDecimalPrecision(precision: Int) {
        prefs.edit().putInt("decimal_precision", precision).apply()
        _preferences.value = _preferences.value.copy(decimalPrecision = precision)
    }

    fun setFormatStyle(style: NumberFormatStyle) {
        prefs.edit().putString("format_style", style.name).apply()
        _preferences.value = _preferences.value.copy(formatStyle = style)
    }
}

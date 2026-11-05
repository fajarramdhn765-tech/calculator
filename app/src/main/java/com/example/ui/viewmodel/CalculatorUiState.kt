package com.example.ui.viewmodel

import com.example.data.local.CalculationEntity
import com.example.data.preferences.UserPreferences
import com.example.engine.AngleUnit

data class CalculatorUiState(
    val expression: String = "",
    val liveResult: String = "",
    val mainDisplay: String = "0",
    val isEvaluated: Boolean = false,
    val isScientificExpanded: Boolean = false,
    val isInverseTrig: Boolean = false,
    val angleUnit: AngleUnit = AngleUnit.DEGREE,
    val errorMessage: String? = null,
    val history: List<CalculationEntity> = emptyList(),
    val preferences: UserPreferences = UserPreferences(),
    val isHistoryOpen: Boolean = false,
    val isSettingsOpen: Boolean = false,
    val toastMessage: String? = null
)

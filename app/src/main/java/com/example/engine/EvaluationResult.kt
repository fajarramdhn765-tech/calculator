package com.example.engine

sealed class EvaluationResult {
    data class Success(val value: Double, val formatted: String) : EvaluationResult()
    data class Error(val message: String) : EvaluationResult()
}

enum class AngleUnit {
    DEGREE,
    RADIAN
}

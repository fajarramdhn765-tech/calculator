package com.example.ui.viewmodel

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.CalculationEntity
import com.example.data.local.CalculatorDatabase
import com.example.data.preferences.AppThemeMode
import com.example.data.preferences.CalculatorPreferences
import com.example.data.preferences.NumberFormatStyle
import com.example.data.repository.HistoryRepository
import com.example.engine.AngleUnit
import com.example.engine.EvaluationResult
import com.example.engine.ExpressionParser
import com.example.sound.SoundAndHapticHelper
import com.example.sound.SoundType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CalculatorViewModel(application: Application) : AndroidViewModel(application) {

    private val preferences = CalculatorPreferences(application)
    private val database = CalculatorDatabase.getInstance(application)
    private val repository = HistoryRepository(database.historyDao())
    val soundHelper = SoundAndHapticHelper(application)

    private val _uiState = MutableStateFlow(CalculatorUiState())
    val uiState: StateFlow<CalculatorUiState> = _uiState.asStateFlow()

    init {
        // Collect history
        viewModelScope.launch {
            repository.allHistory.collect { list ->
                _uiState.update { it.copy(history = list) }
            }
        }

        // Collect preferences
        viewModelScope.launch {
            preferences.preferences.collect { prefs ->
                _uiState.update { current ->
                    current.copy(
                        preferences = prefs,
                        angleUnit = prefs.angleUnit
                    )
                }
                updateLiveResult(_uiState.value.expression)
            }
        }
    }

    // --- Keypad Input Handlers ---

    fun onNumber(digit: String, view: View? = null) {
        soundHelper.playSound(SoundType.KEY_PRESS, _uiState.value.preferences.soundEnabled)
        soundHelper.triggerHaptic(view, _uiState.value.preferences.hapticEnabled)

        _uiState.update { current ->
            val newExpr = if (current.isEvaluated) {
                digit
            } else {
                if (current.expression == "0" && digit != "00") digit else current.expression + digit
            }
            current.copy(
                expression = newExpr,
                mainDisplay = newExpr,
                isEvaluated = false,
                errorMessage = null
            )
        }
        updateLiveResult(_uiState.value.expression)
    }

    fun onDecimal(view: View? = null) {
        soundHelper.playSound(SoundType.KEY_PRESS, _uiState.value.preferences.soundEnabled)
        soundHelper.triggerHaptic(view, _uiState.value.preferences.hapticEnabled)

        _uiState.update { current ->
            if (current.isEvaluated) {
                return@update current.copy(
                    expression = "0.",
                    mainDisplay = "0.",
                    isEvaluated = false,
                    errorMessage = null
                )
            }

            val expr = current.expression
            if (expr.isEmpty() || isLastCharOperator(expr) || expr.endsWith("(")) {
                val newExpr = expr + "0."
                return@update current.copy(expression = newExpr, mainDisplay = newExpr, errorMessage = null)
            }

            // Check if last number token already contains a dot
            val lastToken = getLastNumberToken(expr)
            if (lastToken.contains(".")) {
                return@update current // already has decimal in current number
            }

            val newExpr = expr + "."
            current.copy(expression = newExpr, mainDisplay = newExpr, errorMessage = null)
        }
        updateLiveResult(_uiState.value.expression)
    }

    fun onOperator(op: String, view: View? = null) {
        soundHelper.playSound(SoundType.OPERATOR_PRESS, _uiState.value.preferences.soundEnabled)
        soundHelper.triggerHaptic(view, _uiState.value.preferences.hapticEnabled)

        _uiState.update { current ->
            var expr = current.expression
            if (current.isEvaluated) {
                // Continue from the previous result
                expr = current.mainDisplay.replace(",", "")
            }

            if (expr.isEmpty()) {
                if (op == "−" || op == "-") {
                    return@update current.copy(expression = "−", mainDisplay = "−", isEvaluated = false)
                }
                return@update current
            }

            // Replace last operator if user taps different operator
            if (isLastCharOperator(expr)) {
                expr = expr.dropLast(1)
            }

            val newExpr = expr + op
            current.copy(
                expression = newExpr,
                mainDisplay = newExpr,
                isEvaluated = false,
                errorMessage = null
            )
        }
        updateLiveResult(_uiState.value.expression)
    }

    fun onParentheses(view: View? = null) {
        soundHelper.playSound(SoundType.OPERATOR_PRESS, _uiState.value.preferences.soundEnabled)
        soundHelper.triggerHaptic(view, _uiState.value.preferences.hapticEnabled)

        _uiState.update { current ->
            val expr = if (current.isEvaluated) "" else current.expression
            val openCount = expr.count { it == '(' }
            val closeCount = expr.count { it == ')' }

            val newExpr = if (expr.isEmpty() || isLastCharOperator(expr) || expr.endsWith("(")) {
                expr + "("
            } else if (openCount > closeCount) {
                expr + ")"
            } else {
                expr + "×("
            }

            current.copy(
                expression = newExpr,
                mainDisplay = newExpr,
                isEvaluated = false,
                errorMessage = null
            )
        }
        updateLiveResult(_uiState.value.expression)
    }

    fun onPlusMinus(view: View? = null) {
        soundHelper.playSound(SoundType.OPERATOR_PRESS, _uiState.value.preferences.soundEnabled)
        soundHelper.triggerHaptic(view, _uiState.value.preferences.hapticEnabled)

        _uiState.update { current ->
            val expr = if (current.isEvaluated) current.mainDisplay else current.expression
            if (expr.isEmpty() || expr == "0") return@update current

            // Toggle sign of last operand
            val newExpr = toggleLastOperandSign(expr)
            current.copy(
                expression = newExpr,
                mainDisplay = newExpr,
                isEvaluated = false,
                errorMessage = null
            )
        }
        updateLiveResult(_uiState.value.expression)
    }

    fun onPercentage(view: View? = null) {
        soundHelper.playSound(SoundType.OPERATOR_PRESS, _uiState.value.preferences.soundEnabled)
        soundHelper.triggerHaptic(view, _uiState.value.preferences.hapticEnabled)

        _uiState.update { current ->
            val expr = if (current.isEvaluated) current.mainDisplay else current.expression
            if (expr.isEmpty() || isLastCharOperator(expr) || expr.endsWith("%") || expr.endsWith("(")) {
                return@update current
            }
            val newExpr = expr + "%"
            current.copy(
                expression = newExpr,
                mainDisplay = newExpr,
                isEvaluated = false,
                errorMessage = null
            )
        }
        updateLiveResult(_uiState.value.expression)
    }

    fun onFunction(funcName: String, view: View? = null) {
        soundHelper.playSound(SoundType.OPERATOR_PRESS, _uiState.value.preferences.soundEnabled)
        soundHelper.triggerHaptic(view, _uiState.value.preferences.hapticEnabled)

        _uiState.update { current ->
            var expr = if (current.isEvaluated) current.mainDisplay else current.expression

            val newExpr = when (funcName) {
                "x²" -> if (expr.isNotEmpty() && !isLastCharOperator(expr)) "$expr^2" else "$expr"
                "xʸ" -> if (expr.isNotEmpty() && !isLastCharOperator(expr)) "$expr^" else "$expr"
                "1/x" -> if (expr.isNotEmpty()) "inv($expr)" else "inv("
                "!" -> if (expr.isNotEmpty() && !isLastCharOperator(expr)) "$expr!" else "$expr"
                "√" -> if (expr.isNotEmpty() && (expr.last().isDigit() || expr.endsWith(")"))) "${expr}×√(" else "$expr√("
                "π" -> if (expr.isNotEmpty() && (expr.last().isDigit() || expr.endsWith(")"))) "${expr}×π" else "${expr}π"
                "e" -> if (expr.isNotEmpty() && (expr.last().isDigit() || expr.endsWith(")"))) "${expr}×e" else "${expr}e"
                else -> {
                    // sin, cos, tan, asin, acos, atan, log, ln
                    if (expr.isNotEmpty() && (expr.last().isDigit() || expr.endsWith(")"))) {
                        "${expr}×$funcName("
                    } else {
                        "$expr$funcName("
                    }
                }
            }

            current.copy(
                expression = newExpr,
                mainDisplay = newExpr,
                isEvaluated = false,
                errorMessage = null
            )
        }
        updateLiveResult(_uiState.value.expression)
    }

    fun onBackspace(view: View? = null) {
        soundHelper.playSound(SoundType.KEY_PRESS, _uiState.value.preferences.soundEnabled)
        soundHelper.triggerHaptic(view, _uiState.value.preferences.hapticEnabled)

        _uiState.update { current ->
            if (current.isEvaluated) {
                return@update current.copy(expression = "", mainDisplay = "0", liveResult = "", isEvaluated = false)
            }
            val expr = current.expression
            if (expr.isEmpty()) return@update current

            // Check if removing a function token like "sin(", "asin(", "sqrt(", "inv("
            val funcs = listOf("asin(", "acos(", "atan(", "sin(", "cos(", "tan(", "log(", "ln(", "inv(", "√(")
            var cutLength = 1
            for (fn in funcs) {
                if (expr.endsWith(fn)) {
                    cutLength = fn.length
                    break
                }
            }

            val newExpr = expr.dropLast(cutLength)
            current.copy(
                expression = newExpr,
                mainDisplay = if (newExpr.isEmpty()) "0" else newExpr,
                errorMessage = null
            )
        }
        updateLiveResult(_uiState.value.expression)
    }

    fun onClear(view: View? = null) {
        soundHelper.playSound(SoundType.ACTION_CLEAR, _uiState.value.preferences.soundEnabled)
        soundHelper.triggerHaptic(view, _uiState.value.preferences.hapticEnabled)

        _uiState.update {
            it.copy(
                expression = "",
                liveResult = "",
                mainDisplay = "0",
                isEvaluated = false,
                errorMessage = null
            )
        }
    }

    fun onEquals(view: View? = null) {
        val expr = _uiState.value.expression
        if (expr.isEmpty()) return

        val parser = ExpressionParser(
            angleUnit = _uiState.value.angleUnit,
            maxDecimals = _uiState.value.preferences.decimalPrecision
        )
        val result = parser.evaluate(expr)

        when (result) {
            is EvaluationResult.Success -> {
                soundHelper.playSound(SoundType.EQUALS, _uiState.value.preferences.soundEnabled)
                soundHelper.triggerHaptic(view, _uiState.value.preferences.hapticEnabled)

                val formattedResult = ExpressionParser.formatResult(
                    value = result.value,
                    maxDecimals = _uiState.value.preferences.decimalPrecision,
                    useEuropeanFormat = _uiState.value.preferences.formatStyle == NumberFormatStyle.DOT_COMMA
                )

                // Save to history
                viewModelScope.launch {
                    repository.addCalculation(expr, formattedResult)
                }

                _uiState.update {
                    it.copy(
                        mainDisplay = formattedResult,
                        liveResult = "",
                        isEvaluated = true,
                        errorMessage = null
                    )
                }
            }
            is EvaluationResult.Error -> {
                soundHelper.playSound(SoundType.ERROR, _uiState.value.preferences.soundEnabled)
                soundHelper.triggerHaptic(view, _uiState.value.preferences.hapticEnabled)

                _uiState.update {
                    it.copy(
                        errorMessage = result.message,
                        isEvaluated = false
                    )
                }
            }
        }
    }

    private fun updateLiveResult(expr: String) {
        if (expr.length < 2 || isLastCharOperator(expr)) {
            _uiState.update { it.copy(liveResult = "") }
            return
        }

        val parser = ExpressionParser(
            angleUnit = _uiState.value.angleUnit,
            maxDecimals = _uiState.value.preferences.decimalPrecision
        )
        val result = parser.evaluate(expr)
        if (result is EvaluationResult.Success) {
            val formatted = ExpressionParser.formatResult(
                value = result.value,
                maxDecimals = _uiState.value.preferences.decimalPrecision,
                useEuropeanFormat = _uiState.value.preferences.formatStyle == NumberFormatStyle.DOT_COMMA
            )
            _uiState.update { it.copy(liveResult = "= $formatted") }
        } else {
            _uiState.update { it.copy(liveResult = "") }
        }
    }

    // --- History Actions ---

    fun onUseHistoryResult(item: CalculationEntity, view: View? = null) {
        soundHelper.playSound(SoundType.KEY_PRESS, _uiState.value.preferences.soundEnabled)
        soundHelper.triggerHaptic(view, _uiState.value.preferences.hapticEnabled)

        val cleanResult = item.result.replace(",", "")
        _uiState.update { current ->
            current.copy(
                expression = cleanResult,
                mainDisplay = cleanResult,
                isEvaluated = false,
                isHistoryOpen = false
            )
        }
        updateLiveResult(cleanResult)
    }

    fun onUseHistoryExpression(item: CalculationEntity, view: View? = null) {
        soundHelper.playSound(SoundType.KEY_PRESS, _uiState.value.preferences.soundEnabled)
        soundHelper.triggerHaptic(view, _uiState.value.preferences.hapticEnabled)

        _uiState.update { current ->
            current.copy(
                expression = item.expression,
                mainDisplay = item.result,
                isEvaluated = true,
                isHistoryOpen = false
            )
        }
    }

    fun onDeleteHistory(id: Long) {
        viewModelScope.launch {
            repository.delete(id)
        }
    }

    fun onClearAllHistory() {
        viewModelScope.launch {
            repository.clearAll()
        }
    }

    fun setHistoryOpen(open: Boolean) {
        _uiState.update { it.copy(isHistoryOpen = open) }
    }

    fun setSettingsOpen(open: Boolean) {
        _uiState.update { it.copy(isSettingsOpen = open) }
    }

    // --- Mode & Features Toggles ---

    fun onToggleScientific() {
        _uiState.update { it.copy(isScientificExpanded = !it.isScientificExpanded) }
    }

    fun onToggleAngleUnit() {
        val nextUnit = if (_uiState.value.angleUnit == AngleUnit.DEGREE) AngleUnit.RADIAN else AngleUnit.DEGREE
        preferences.setAngleUnit(nextUnit)
        _uiState.update { it.copy(angleUnit = nextUnit) }
        updateLiveResult(_uiState.value.expression)
    }

    fun onToggleInverseTrig() {
        _uiState.update { it.copy(isInverseTrig = !it.isInverseTrig) }
    }

    // --- Preferences Actions ---

    fun setThemeMode(mode: AppThemeMode) {
        preferences.setThemeMode(mode)
    }

    fun setSoundEnabled(enabled: Boolean) {
        preferences.setSoundEnabled(enabled)
    }

    fun setHapticEnabled(enabled: Boolean) {
        preferences.setHapticEnabled(enabled)
    }

    fun setDecimalPrecision(precision: Int) {
        preferences.setDecimalPrecision(precision)
        updateLiveResult(_uiState.value.expression)
    }

    fun setFormatStyle(style: NumberFormatStyle) {
        preferences.setFormatStyle(style)
        updateLiveResult(_uiState.value.expression)
    }

    // --- Clipboard ---

    fun onCopyResult(context: Context) {
        val textToCopy = if (_uiState.value.isEvaluated) {
            _uiState.value.mainDisplay
        } else if (_uiState.value.liveResult.isNotEmpty()) {
            _uiState.value.liveResult.removePrefix("= ")
        } else {
            _uiState.value.mainDisplay
        }

        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Calculator Result", textToCopy)
        clipboard.setPrimaryClip(clip)

        _uiState.update { it.copy(toastMessage = "Copied: $textToCopy") }
    }

    fun onPasteFromClipboard(context: Context) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = clipboard.primaryClip
        if (clip != null && clip.itemCount > 0) {
            val pasted = clip.getItemAt(0).text?.toString()?.trim() ?: ""
            // Filter to valid numeric / math symbols
            val cleaned = pasted.filter { it.isDigit() || "+-*/×÷−().,%^".contains(it) }
            if (cleaned.isNotEmpty()) {
                _uiState.update { current ->
                    val newExpr = if (current.isEvaluated) cleaned else current.expression + cleaned
                    current.copy(
                        expression = newExpr,
                        mainDisplay = newExpr,
                        isEvaluated = false,
                        toastMessage = "Pasted: $cleaned"
                    )
                }
                updateLiveResult(_uiState.value.expression)
            } else {
                _uiState.update { it.copy(toastMessage = "No valid math content in clipboard") }
            }
        }
    }

    fun clearToast() {
        _uiState.update { it.copy(toastMessage = null) }
    }

    // --- Helpers ---

    private fun isLastCharOperator(expr: String): Boolean {
        if (expr.isEmpty()) return false
        val last = expr.last()
        return last == '+' || last == '−' || last == '-' || last == '×' || last == '*' || last == '÷' || last == '/' || last == '^'
    }

    private fun getLastNumberToken(expr: String): String {
        var i = expr.length - 1
        while (i >= 0 && (expr[i].isDigit() || expr[i] == '.')) {
            i--
        }
        return expr.substring(i + 1)
    }

    private fun toggleLastOperandSign(expr: String): String {
        // If wrapped in (-X), unwrap it
        val parenNegRegex = Regex("""\(-(\d+(\.\d+)?)\)$""")
        if (parenNegRegex.containsMatchIn(expr)) {
            return parenNegRegex.replace(expr) { match -> match.groupValues[1] }
        }

        // Otherwise find trailing number and wrap as (-X)
        val numRegex = Regex("""(\d+(\.\d+)?)$""")
        if (numRegex.containsMatchIn(expr)) {
            return numRegex.replace(expr) { match -> "(-${match.value})" }
        }

        return expr
    }
}

package com.example.engine

import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.ln
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan

class ExpressionParser(
    private val angleUnit: AngleUnit = AngleUnit.DEGREE,
    private val maxDecimals: Int = -1 // -1 means auto
) {

    fun evaluate(expression: String): EvaluationResult {
        val sanitized = sanitize(expression)
        if (sanitized.isEmpty()) {
            return EvaluationResult.Error("Empty expression")
        }

        return try {
            val parser = InnerParser(sanitized, angleUnit)
            val result = parser.parseExpression()
            if (parser.hasMore()) {
                EvaluationResult.Error("Unexpected symbol at '${parser.peekChar()}'")
            } else if (result.isNaN()) {
                EvaluationResult.Error("Undefined result")
            } else if (result.isInfinite()) {
                EvaluationResult.Error("Cannot divide by 0")
            } else {
                val formatted = formatResult(result, maxDecimals)
                EvaluationResult.Success(result, formatted)
            }
        } catch (e: ArithmeticException) {
            EvaluationResult.Error(e.message ?: "Math error")
        } catch (e: IllegalArgumentException) {
            EvaluationResult.Error(e.message ?: "Invalid expression")
        } catch (e: Exception) {
            EvaluationResult.Error("Invalid expression")
        }
    }

    private fun sanitize(input: String): String {
        return input
            .replace(" ", "")
            .replace("×", "*")
            .replace("÷", "/")
            .replace("−", "-")
            .replace("√", "sqrt")
            .replace("π", "${Math.PI}")
            .replace("e", "${Math.E}")
    }

    private class InnerParser(val src: String, val angleUnit: AngleUnit) {
        var pos = 0

        fun hasMore(): Boolean = pos < src.length
        fun peekChar(): Char = if (hasMore()) src[pos] else '\u0000'

        fun nextChar(): Char {
            val ch = peekChar()
            if (hasMore()) pos++
            return ch
        }

        fun match(expected: Char): Boolean {
            if (hasMore() && src[pos] == expected) {
                pos++
                return true
            }
            return false
        }

        fun parseExpression(): Double {
            var value = parseTerm()
            while (hasMore()) {
                when {
                    match('+') -> {
                        val next = parseTerm()
                        value += next
                    }
                    match('-') -> {
                        val next = parseTerm()
                        value -= next
                    }
                    else -> break
                }
            }
            return value
        }

        fun parseTerm(): Double {
            var value = parsePower()
            while (hasMore()) {
                when {
                    match('*') -> {
                        value *= parsePower()
                    }
                    match('/') -> {
                        val divisor = parsePower()
                        if (divisor == 0.0) {
                            throw ArithmeticException("Cannot divide by 0")
                        }
                        value /= divisor
                    }
                    match('%') -> {
                        // Binary modulo if followed by another factor, or unary percent
                        if (hasMore() && (peekChar().isDigit() || peekChar() == '(' || isFuncStart())) {
                            val mod = parsePower()
                            if (mod == 0.0) throw ArithmeticException("Cannot divide by 0")
                            value %= mod
                        } else {
                            value /= 100.0
                        }
                    }
                    // Handle implied multiplication: e.g. 2(3) or 2sin(30)
                    peekChar() == '(' || isFuncStart() -> {
                        value *= parsePower()
                    }
                    else -> break
                }
            }
            return value
        }

        fun parsePower(): Double {
            var value = parseFactor()
            if (match('^')) {
                val exponent = parsePower() // right-associative
                value = value.pow(exponent)
            }
            return value
        }

        fun parseFactor(): Double {
            // Unary operators
            if (match('+')) {
                return parseFactor()
            }
            if (match('-')) {
                return -parseFactor()
            }

            var value = parsePrimary()

            // Postfix operators: ! (factorial), % (percent)
            while (hasMore()) {
                when {
                    match('!') -> {
                        value = factorial(value)
                    }
                    match('%') -> {
                        // Check if this % is unary postfix (e.g. 50%)
                        value /= 100.0
                    }
                    else -> break
                }
            }

            return value
        }

        fun parsePrimary(): Double {
            if (match('(')) {
                val value = parseExpression()
                if (!match(')')) {
                    // Try to auto-close unclosed parenthesis gracefully
                }
                return value
            }

            // Function calls: sin, cos, tan, asin, acos, atan, log, ln, sqrt
            val funcName = parseFunctionName()
            if (funcName != null) {
                var hasParen = match('(')
                val arg = parseExpression()
                if (hasParen) {
                    match(')')
                }
                return applyFunction(funcName, arg)
            }

            // Numbers
            return parseNumber()
        }

        fun isFuncStart(): Boolean {
            val remaining = src.substring(pos)
            val funcs = listOf("asin", "acos", "atan", "sin", "cos", "tan", "log", "ln", "sqrt", "inv")
            return funcs.any { remaining.startsWith(it) }
        }

        fun parseFunctionName(): String? {
            val remaining = src.substring(pos)
            val funcs = listOf("asin", "acos", "atan", "sin", "cos", "tan", "log", "ln", "sqrt", "inv")
            for (fn in funcs) {
                if (remaining.startsWith(fn)) {
                    pos += fn.length
                    return fn
                }
            }
            return null
        }

        fun applyFunction(name: String, arg: Double): Double {
            return when (name) {
                "sin" -> {
                    val rad = if (angleUnit == AngleUnit.DEGREE) Math.toRadians(arg) else arg
                    val s = sin(rad)
                    if (angleUnit == AngleUnit.DEGREE && (abs(arg % 180.0) < 1e-10)) 0.0 else s
                }
                "cos" -> {
                    val rad = if (angleUnit == AngleUnit.DEGREE) Math.toRadians(arg) else arg
                    val c = cos(rad)
                    if (angleUnit == AngleUnit.DEGREE && abs((arg - 90.0) % 180.0) < 1e-10) 0.0 else c
                }
                "tan" -> {
                    if (angleUnit == AngleUnit.DEGREE && abs((arg - 90.0) % 180.0) < 1e-10) {
                        throw ArithmeticException("Invalid input (tan undefined)")
                    }
                    val rad = if (angleUnit == AngleUnit.DEGREE) Math.toRadians(arg) else arg
                    tan(rad)
                }
                "asin" -> {
                    if (arg < -1.0 || arg > 1.0) throw ArithmeticException("Invalid domain for asin")
                    val rad = asin(arg)
                    if (angleUnit == AngleUnit.DEGREE) Math.toDegrees(rad) else rad
                }
                "acos" -> {
                    if (arg < -1.0 || arg > 1.0) throw ArithmeticException("Invalid domain for acos")
                    val rad = acos(arg)
                    if (angleUnit == AngleUnit.DEGREE) Math.toDegrees(rad) else rad
                }
                "atan" -> {
                    val rad = atan(arg)
                    if (angleUnit == AngleUnit.DEGREE) Math.toDegrees(rad) else rad
                }
                "log" -> {
                    if (arg <= 0) throw ArithmeticException("Invalid domain for log")
                    log10(arg)
                }
                "ln" -> {
                    if (arg <= 0) throw ArithmeticException("Invalid domain for ln")
                    ln(arg)
                }
                "sqrt" -> {
                    if (arg < 0) throw ArithmeticException("Cannot square root negative number")
                    sqrt(arg)
                }
                "inv" -> {
                    if (arg == 0.0) throw ArithmeticException("Cannot divide by 0")
                    1.0 / arg
                }
                else -> throw IllegalArgumentException("Unknown function: $name")
            }
        }

        fun parseNumber(): Double {
            val start = pos
            if (!hasMore()) throw IllegalArgumentException("Expected number at end of expression")

            while (hasMore() && (peekChar().isDigit() || peekChar() == '.')) {
                pos++
            }

            if (start == pos) {
                throw IllegalArgumentException("Expected number at character '${peekChar()}'")
            }

            val numStr = src.substring(start, pos)
            return numStr.toDoubleOrNull() ?: throw IllegalArgumentException("Invalid number: $numStr")
        }

        fun factorial(n: Double): Double {
            if (n < 0 || n != kotlin.math.floor(n)) {
                throw ArithmeticException("Factorial only defined for non-negative integers")
            }
            if (n > 170) {
                throw ArithmeticException("Overflow (n > 170)")
            }
            val intN = n.toInt()
            var res = 1.0
            for (i in 2..intN) {
                res *= i
            }
            return res
        }
    }

    companion object {
        fun formatResult(
            value: Double,
            maxDecimals: Int = -1,
            useEuropeanFormat: Boolean = false
        ): String {
            if (value.isNaN()) return "NaN"
            if (value.isInfinite()) return if (value > 0) "Infinity" else "-Infinity"

            // Handle very close to zero
            val normalized = if (abs(value) < 1e-12) 0.0 else value

            val symbols = DecimalFormatSymbols(Locale.US).apply {
                if (useEuropeanFormat) {
                    groupingSeparator = '.'
                    decimalSeparator = ','
                } else {
                    groupingSeparator = ','
                    decimalSeparator = '.'
                }
            }

            val absVal = abs(normalized)
            // Use scientific notation for extremely large or tiny numbers
            if (absVal != 0.0 && (absVal >= 1e12 || absVal < 1e-6)) {
                val scientificFormat = DecimalFormat("0.######E0", symbols)
                return scientificFormat.format(normalized)
            }

            val bd = BigDecimal.valueOf(normalized)
            val rounded = if (maxDecimals >= 0) {
                bd.setScale(maxDecimals, RoundingMode.HALF_UP).stripTrailingZeros()
            } else {
                bd.round(MathContext(10, RoundingMode.HALF_UP)).stripTrailingZeros()
            }

            val pattern = if (rounded.scale() > 0) "#,##0." + "#".repeat(rounded.scale().coerceAtMost(10)) else "#,##0"
            val df = DecimalFormat(pattern, symbols)
            return df.format(rounded)
        }
    }
}

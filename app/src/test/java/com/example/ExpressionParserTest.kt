package com.example

import com.example.engine.AngleUnit
import com.example.engine.EvaluationResult
import com.example.engine.ExpressionParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExpressionParserTest {

    @Test
    fun testBasicArithmeticAndPrecedence() {
        val parser = ExpressionParser()

        val r1 = parser.evaluate("2 + 3 × 4")
        assertTrue(r1 is EvaluationResult.Success)
        assertEquals(14.0, (r1 as EvaluationResult.Success).value, 1e-6)

        val r2 = parser.evaluate("(2 + 3) × 4")
        assertTrue(r2 is EvaluationResult.Success)
        assertEquals(20.0, (r2 as EvaluationResult.Success).value, 1e-6)

        val r3 = parser.evaluate("100 - 30 ÷ 5 + 4")
        assertTrue(r3 is EvaluationResult.Success)
        assertEquals(98.0, (r3 as EvaluationResult.Success).value, 1e-6)
    }

    @Test
    fun testDivisionByZero() {
        val parser = ExpressionParser()
        val res = parser.evaluate("50 ÷ 0")
        assertTrue(res is EvaluationResult.Error)
        assertEquals("Cannot divide by 0", (res as EvaluationResult.Error).message)
    }

    @Test
    fun testPercentageAndFactorial() {
        val parser = ExpressionParser()

        val r1 = parser.evaluate("50%")
        assertTrue(r1 is EvaluationResult.Success)
        assertEquals(0.5, (r1 as EvaluationResult.Success).value, 1e-6)

        val r2 = parser.evaluate("5!")
        assertTrue(r2 is EvaluationResult.Success)
        assertEquals(120.0, (r2 as EvaluationResult.Success).value, 1e-6)

        val r3 = parser.evaluate("0!")
        assertTrue(r3 is EvaluationResult.Success)
        assertEquals(1.0, (r3 as EvaluationResult.Success).value, 1e-6)
    }

    @Test
    fun testScientificFunctionsInDegree() {
        val parser = ExpressionParser(angleUnit = AngleUnit.DEGREE)

        val rSin = parser.evaluate("sin(30)")
        assertTrue(rSin is EvaluationResult.Success)
        assertEquals(0.5, (rSin as EvaluationResult.Success).value, 1e-6)

        val rCos = parser.evaluate("cos(60)")
        assertTrue(rCos is EvaluationResult.Success)
        assertEquals(0.5, (rCos as EvaluationResult.Success).value, 1e-6)

        val rTan = parser.evaluate("tan(45)")
        assertTrue(rTan is EvaluationResult.Success)
        assertEquals(1.0, (rTan as EvaluationResult.Success).value, 1e-6)

        val rSqrt = parser.evaluate("√(64)")
        assertTrue(rSqrt is EvaluationResult.Success)
        assertEquals(8.0, (rSqrt as EvaluationResult.Success).value, 1e-6)

        val rPower = parser.evaluate("2^8")
        assertTrue(rPower is EvaluationResult.Success)
        assertEquals(256.0, (rPower as EvaluationResult.Success).value, 1e-6)

        val rLog = parser.evaluate("log(1000)")
        assertTrue(rLog is EvaluationResult.Success)
        assertEquals(3.0, (rLog as EvaluationResult.Success).value, 1e-6)
    }

    @Test
    fun testImpliedMultiplication() {
        val parser = ExpressionParser()

        val r1 = parser.evaluate("2(4 + 6)")
        assertTrue(r1 is EvaluationResult.Success)
        assertEquals(20.0, (r1 as EvaluationResult.Success).value, 1e-6)

        val r2 = parser.evaluate("(3)(5)")
        assertTrue(r2 is EvaluationResult.Success)
        assertEquals(15.0, (r2 as EvaluationResult.Success).value, 1e-6)
    }

    @Test
    fun testResultFormatting() {
        val f1 = ExpressionParser.formatResult(1234567.89, -1, false)
        assertEquals("1,234,567.89", f1)

        val f2 = ExpressionParser.formatResult(1234567.89, -1, true)
        assertEquals("1.234.567,89", f2)

        val f3 = ExpressionParser.formatResult(42.000, -1, false)
        assertEquals("42", f3)
    }
}

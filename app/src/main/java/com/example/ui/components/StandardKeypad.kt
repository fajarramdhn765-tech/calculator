package com.example.ui.components

import android.view.View
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun StandardKeypad(
    decimalSeparatorSymbol: String = ".",
    onNumber: (String, View) -> Unit,
    onDecimal: (View) -> Unit,
    onOperator: (String, View) -> Unit,
    onParentheses: (View) -> Unit,
    onPlusMinus: (View) -> Unit,
    onPercentage: (View) -> Unit,
    onClear: (View) -> Unit,
    onEquals: (View) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 2.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Row 1: AC, ( ), %, ÷
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            CalculatorButton(
                symbol = "AC",
                role = ButtonRole.ACTION,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                fontSize = 22.sp,
                onClick = { view -> onClear(view) }
            )
            CalculatorButton(
                symbol = "( )",
                role = ButtonRole.OPERATOR,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                fontSize = 22.sp,
                onClick = { view -> onParentheses(view) }
            )
            CalculatorButton(
                symbol = "%",
                role = ButtonRole.OPERATOR,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                fontSize = 24.sp,
                onClick = { view -> onPercentage(view) }
            )
            CalculatorButton(
                symbol = "÷",
                role = ButtonRole.OPERATOR,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                fontSize = 30.sp,
                onClick = { view -> onOperator("÷", view) }
            )
        }

        // Row 2: 7, 8, 9, ×
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            CalculatorButton(
                symbol = "7",
                role = ButtonRole.NUMBER,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                fontSize = 28.sp,
                onClick = { view -> onNumber("7", view) }
            )
            CalculatorButton(
                symbol = "8",
                role = ButtonRole.NUMBER,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                fontSize = 28.sp,
                onClick = { view -> onNumber("8", view) }
            )
            CalculatorButton(
                symbol = "9",
                role = ButtonRole.NUMBER,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                fontSize = 28.sp,
                onClick = { view -> onNumber("9", view) }
            )
            CalculatorButton(
                symbol = "×",
                role = ButtonRole.OPERATOR,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                fontSize = 30.sp,
                onClick = { view -> onOperator("×", view) }
            )
        }

        // Row 3: 4, 5, 6, −
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            CalculatorButton(
                symbol = "4",
                role = ButtonRole.NUMBER,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                fontSize = 28.sp,
                onClick = { view -> onNumber("4", view) }
            )
            CalculatorButton(
                symbol = "5",
                role = ButtonRole.NUMBER,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                fontSize = 28.sp,
                onClick = { view -> onNumber("5", view) }
            )
            CalculatorButton(
                symbol = "6",
                role = ButtonRole.NUMBER,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                fontSize = 28.sp,
                onClick = { view -> onNumber("6", view) }
            )
            CalculatorButton(
                symbol = "−",
                role = ButtonRole.OPERATOR,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                fontSize = 30.sp,
                onClick = { view -> onOperator("−", view) }
            )
        }

        // Row 4: 1, 2, 3, +
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            CalculatorButton(
                symbol = "1",
                role = ButtonRole.NUMBER,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                fontSize = 28.sp,
                onClick = { view -> onNumber("1", view) }
            )
            CalculatorButton(
                symbol = "2",
                role = ButtonRole.NUMBER,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                fontSize = 28.sp,
                onClick = { view -> onNumber("2", view) }
            )
            CalculatorButton(
                symbol = "3",
                role = ButtonRole.NUMBER,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                fontSize = 28.sp,
                onClick = { view -> onNumber("3", view) }
            )
            CalculatorButton(
                symbol = "+",
                role = ButtonRole.OPERATOR,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                fontSize = 30.sp,
                onClick = { view -> onOperator("+", view) }
            )
        }

        // Row 5: ±, 0, decimal, =
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            CalculatorButton(
                symbol = "±",
                role = ButtonRole.NUMBER,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                fontSize = 24.sp,
                onClick = { view -> onPlusMinus(view) }
            )
            CalculatorButton(
                symbol = "0",
                role = ButtonRole.NUMBER,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                fontSize = 28.sp,
                onClick = { view -> onNumber("0", view) }
            )
            CalculatorButton(
                symbol = decimalSeparatorSymbol,
                role = ButtonRole.NUMBER,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                fontSize = 28.sp,
                onClick = { view -> onDecimal(view) }
            )
            CalculatorButton(
                symbol = "=",
                role = ButtonRole.EQUALS,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                fontSize = 32.sp,
                onClick = { view -> onEquals(view) }
            )
        }
    }
}

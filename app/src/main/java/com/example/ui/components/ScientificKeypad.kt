package com.example.ui.components

import android.view.View
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.AngleUnit

@Composable
fun ScientificKeypad(
    isInverse: Boolean,
    angleUnit: AngleUnit,
    onToggleInverse: (View) -> Unit,
    onToggleAngleUnit: (View) -> Unit,
    onFunction: (String, View) -> Unit,
    onOperator: (String, View) -> Unit,
    modifier: Modifier = Modifier
) {
    val sinLabel = if (isInverse) "asin" else "sin"
    val cosLabel = if (isInverse) "acos" else "cos"
    val tanLabel = if (isInverse) "atan" else "tan"

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 2.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Row 1: 2nd, sin, cos, tan, DEG/RAD
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            CalculatorButton(
                symbol = if (isInverse) "1st" else "2nd",
                role = ButtonRole.SCIENTIFIC,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                fontSize = 14.sp,
                onClick = { view -> onToggleInverse(view) }
            )
            CalculatorButton(
                symbol = sinLabel,
                role = ButtonRole.SCIENTIFIC,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                fontSize = 14.sp,
                onClick = { view -> onFunction(sinLabel, view) }
            )
            CalculatorButton(
                symbol = cosLabel,
                role = ButtonRole.SCIENTIFIC,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                fontSize = 14.sp,
                onClick = { view -> onFunction(cosLabel, view) }
            )
            CalculatorButton(
                symbol = tanLabel,
                role = ButtonRole.SCIENTIFIC,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                fontSize = 14.sp,
                onClick = { view -> onFunction(tanLabel, view) }
            )
            CalculatorButton(
                symbol = if (angleUnit == AngleUnit.DEGREE) "RAD" else "DEG",
                role = ButtonRole.SCIENTIFIC,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                fontSize = 13.sp,
                onClick = { view -> onToggleAngleUnit(view) }
            )
        }

        // Row 2: ln, log, 1/x, e, π
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            CalculatorButton(
                symbol = "ln",
                role = ButtonRole.SCIENTIFIC,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                fontSize = 15.sp,
                onClick = { view -> onFunction("ln", view) }
            )
            CalculatorButton(
                symbol = "log",
                role = ButtonRole.SCIENTIFIC,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                fontSize = 15.sp,
                onClick = { view -> onFunction("log", view) }
            )
            CalculatorButton(
                symbol = "1/x",
                role = ButtonRole.SCIENTIFIC,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                fontSize = 14.sp,
                onClick = { view -> onFunction("1/x", view) }
            )
            CalculatorButton(
                symbol = "e",
                role = ButtonRole.SCIENTIFIC,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                fontSize = 16.sp,
                onClick = { view -> onFunction("e", view) }
            )
            CalculatorButton(
                symbol = "π",
                role = ButtonRole.SCIENTIFIC,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                fontSize = 18.sp,
                onClick = { view -> onFunction("π", view) }
            )
        }

        // Row 3: √, x², xʸ, x!, ^
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            CalculatorButton(
                symbol = "√",
                role = ButtonRole.SCIENTIFIC,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                fontSize = 18.sp,
                onClick = { view -> onFunction("√", view) }
            )
            CalculatorButton(
                symbol = "x²",
                role = ButtonRole.SCIENTIFIC,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                fontSize = 15.sp,
                onClick = { view -> onFunction("x²", view) }
            )
            CalculatorButton(
                symbol = "xʸ",
                role = ButtonRole.SCIENTIFIC,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                fontSize = 15.sp,
                onClick = { view -> onFunction("xʸ", view) }
            )
            CalculatorButton(
                symbol = "x!",
                role = ButtonRole.SCIENTIFIC,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                fontSize = 15.sp,
                onClick = { view -> onFunction("!", view) }
            )
            CalculatorButton(
                symbol = "^",
                role = ButtonRole.SCIENTIFIC,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                fontSize = 18.sp,
                onClick = { view -> onOperator("^", view) }
            )
        }
    }
}

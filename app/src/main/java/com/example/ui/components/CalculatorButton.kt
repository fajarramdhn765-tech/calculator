package com.example.ui.components

import android.view.View
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocalCalculatorColors
import com.example.ui.theme.SpaceGroteskFontFamily

enum class ButtonRole {
    NUMBER,
    OPERATOR,
    ACTION,
    EQUALS,
    SCIENTIFIC
}

@Composable
fun CalculatorButton(
    symbol: String,
    role: ButtonRole = ButtonRole.NUMBER,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 28.sp,
    onClick: (View) -> Unit
) {
    val colors = LocalCalculatorColors.current
    val view = LocalView.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // 3D Press depression physics (mechanical key movement)
    val pressOffsetY by animateDpAsState(
        targetValue = if (isPressed) 4.dp else 0.dp,
        animationSpec = spring(dampingRatio = 0.55f, stiffness = 800f),
        label = "btn_press_y"
    )

    val shadowElevation by animateDpAsState(
        targetValue = if (isPressed) 1.dp else 5.dp,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 800f),
        label = "btn_shadow"
    )

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1.0f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 800f),
        label = "btn_scale"
    )

    val (topColor, bottomColor, bevelColor, textColor) = when (role) {
        ButtonRole.NUMBER -> Quad(
            colors.numberTop,
            colors.numberBottom,
            colors.numberBevel,
            colors.textPrimary
        )
        ButtonRole.OPERATOR -> Quad(
            colors.operatorTop,
            colors.operatorBottom,
            colors.operatorBevel,
            colors.textOperator
        )
        ButtonRole.ACTION -> Quad(
            colors.actionTop,
            colors.actionBottom,
            colors.actionBevel,
            Color.White
        )
        ButtonRole.EQUALS -> Quad(
            colors.equalsTop,
            colors.equalsBottom,
            colors.equalsBevel,
            Color.White
        )
        ButtonRole.SCIENTIFIC -> Quad(
            colors.scientificTop,
            colors.scientificBottom,
            colors.scientificBevel,
            colors.textOperator
        )
    }

    val tag = "calc_btn_" + symbol.replace(" ", "_")
        .replace("+", "plus")
        .replace("−", "minus")
        .replace("×", "mul")
        .replace("÷", "div")
        .replace("=", "equals")
        .replace("%", "percent")
        .replace(".", "dot")
        .replace(",", "comma")

    val buttonShape = RoundedCornerShape(18.dp)

    // Outer 3D Key Container
    Box(
        modifier = modifier
            .testTag(tag)
            .minimumInteractiveComponentSize()
            .scale(scale)
            .padding(horizontal = 3.dp, vertical = 2.dp)
            .shadow(shadowElevation, buttonShape, spotColor = Color.Black.copy(alpha = 0.5f))
            .clip(buttonShape)
            .background(bevelColor) // 3D bottom bevel extrusion
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = { onClick(view) }
            )
    ) {
        // Raised 3D Keycap Face that depresses downward upon touch
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = (5.dp - pressOffsetY).coerceAtLeast(0.dp)) // bevel exposure
                .offset(y = pressOffsetY)
                .clip(buttonShape)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(topColor, bottomColor)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            // Subtle top highlight border for specular reflection
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(1.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = symbol,
                    color = textColor,
                    fontSize = fontSize,
                    fontFamily = SpaceGroteskFontFamily,
                    fontWeight = if (role == ButtonRole.NUMBER || role == ButtonRole.EQUALS || role == ButtonRole.ACTION) {
                        FontWeight.Bold
                    } else {
                        FontWeight.SemiBold
                    },
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

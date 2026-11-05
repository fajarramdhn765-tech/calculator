package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.AngleUnit
import com.example.ui.theme.AccentOrange
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.LocalCalculatorColors
import com.example.ui.theme.MoonlightPurple
import com.example.ui.theme.SpaceGroteskFontFamily

@Composable
fun CalculatorDisplay(
    expression: String,
    liveResult: String,
    mainDisplay: String,
    isEvaluated: Boolean,
    errorMessage: String?,
    angleUnit: AngleUnit,
    isScientificExpanded: Boolean,
    onToggleScientific: () -> Unit,
    onToggleAngleUnit: () -> Unit,
    onCopy: () -> Unit,
    onPaste: () -> Unit,
    onBackspace: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalCalculatorColors.current
    val scrollState = rememberScrollState()

    // Automatically scroll to end as user types
    LaunchedEffect(mainDisplay, expression) {
        scrollState.animateScrollTo(scrollState.maxValue)
    }

    val displayShape = RoundedCornerShape(26.dp)

    // Recessed 3D Beveled Screen Housing
    Box(
        modifier = modifier
            .testTag("calculator_display")
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 6.dp)
            .shadow(6.dp, displayShape, spotColor = Color.Black.copy(alpha = 0.6f))
            .clip(displayShape)
            .background(colors.displayBorder)
            .padding(2.5.dp) // Outer 3D bevel rim
    ) {
        // Inner Display Glass Area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            colors.displayBackground,
                            colors.displayBackground.copy(alpha = 0.95f)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 14.dp),
                horizontalAlignment = Alignment.End
            ) {
                // Top control bar: Mode indicators & Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left Mode Pills
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Angle Mode Pill (DEG / RAD)
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (angleUnit == AngleUnit.DEGREE) AccentOrange.copy(alpha = 0.2f) else ElectricBlue.copy(alpha = 0.2f),
                            border = BorderStroke(
                                1.dp,
                                if (angleUnit == AngleUnit.DEGREE) AccentOrange.copy(alpha = 0.7f) else ElectricBlue.copy(alpha = 0.7f)
                            ),
                            modifier = Modifier
                                .testTag("angle_unit_pill")
                                .clickable { onToggleAngleUnit() }
                        ) {
                            Text(
                                text = if (angleUnit == AngleUnit.DEGREE) "DEG" else "RAD",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = SpaceGroteskFontFamily,
                                color = if (angleUnit == AngleUnit.DEGREE) AccentOrange else ElectricBlue,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }

                        // Scientific Mode Toggle Pill
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isScientificExpanded) AccentOrange.copy(alpha = 0.25f) else colors.numberBottom,
                            border = BorderStroke(
                                1.dp,
                                if (isScientificExpanded) AccentOrange else colors.displayBorder
                            ),
                            modifier = Modifier
                                .testTag("btn_toggle_scientific")
                                .clickable { onToggleScientific() }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Functions,
                                    contentDescription = "Toggle Scientific Mode",
                                    tint = if (isScientificExpanded) AccentOrange else colors.textSecondary,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "3D SCI",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = SpaceGroteskFontFamily,
                                    color = if (isScientificExpanded) AccentOrange else colors.textSecondary
                                )
                            }
                        }
                    }

                    // Right utility buttons (Copy, Paste, Backspace)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onCopy,
                            modifier = Modifier
                                .testTag("btn_copy_result")
                                .size(34.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy Result",
                                tint = colors.textSecondary,
                                modifier = Modifier.size(17.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(2.dp))

                        IconButton(
                            onClick = onPaste,
                            modifier = Modifier
                                .testTag("btn_paste_result")
                                .size(34.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentPaste,
                                contentDescription = "Paste to Calculator",
                                tint = colors.textSecondary,
                                modifier = Modifier.size(17.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(2.dp))

                        IconButton(
                            onClick = onBackspace,
                            modifier = Modifier
                                .testTag("btn_backspace")
                                .size(34.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Backspace,
                                contentDescription = "Delete",
                                tint = colors.textSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Upper context row:
                // - If evaluated: displays previous expression "12 + 5 ="
                // - If typing with live preview: displays live preview "= 17"
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(26.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    if (isEvaluated && expression.isNotEmpty()) {
                        Text(
                            text = "$expression =",
                            color = colors.textSecondary,
                            fontSize = 17.sp,
                            fontFamily = JetBrainsMonoFontFamily,
                            fontWeight = FontWeight.Normal,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.End,
                            modifier = Modifier.testTag("text_expression_history")
                        )
                    } else if (liveResult.isNotEmpty()) {
                        Text(
                            text = liveResult,
                            color = AccentOrange,
                            fontSize = 20.sp,
                            fontFamily = SpaceGroteskFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.End,
                            modifier = Modifier.testTag("text_live_result")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                // Error banner if any
                AnimatedVisibility(
                    visible = errorMessage != null,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    if (errorMessage != null) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .padding(bottom = 6.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.error.copy(alpha = 0.15f))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Error",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = errorMessage,
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = SpaceGroteskFontFamily
                            )
                        }
                    }
                }

                // HERO: 3D High-Contrast Number / Result Display
                val numberText = if (mainDisplay.isEmpty()) "0" else mainDisplay

                val dynamicFontSize = when {
                    numberText.length > 16 -> 28.sp
                    numberText.length > 12 -> 34.sp
                    numberText.length > 8 -> 42.sp
                    else -> 52.sp
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(scrollState),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Text(
                        text = numberText,
                        color = colors.textPrimary,
                        fontSize = dynamicFontSize,
                        fontFamily = SpaceGroteskFontFamily,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        textAlign = TextAlign.End,
                        modifier = Modifier.testTag("text_main_result")
                    )
                }
            }
        }
    }
}

package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.preferences.AppThemeMode
import com.example.data.preferences.NumberFormatStyle
import com.example.data.preferences.UserPreferences
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.LocalCalculatorColors
import com.example.ui.theme.MoonlightPurple
import com.example.ui.theme.SpaceGroteskFontFamily

@Composable
fun SettingsDialog(
    preferences: UserPreferences,
    onDismiss: () -> Unit,
    onThemeChange: (AppThemeMode) -> Unit,
    onSoundToggle: (Boolean) -> Unit,
    onHapticToggle: (Boolean) -> Unit,
    onDecimalPrecisionChange: (Int) -> Unit,
    onFormatStyleChange: (NumberFormatStyle) -> Unit
) {
    val colors = LocalCalculatorColors.current
    val scrollState = rememberScrollState()

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(28.dp),
            color = colors.displayBackground,
            border = BorderStroke(1.dp, colors.borderStroke)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(scrollState)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = ElectricBlue,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Settings",
                            fontFamily = SpaceGroteskFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = colors.textPrimary
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = colors.textSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section 1: Appearance (Theme)
                Text(
                    text = "THEME",
                    fontFamily = SpaceGroteskFontFamily,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MoonlightPurple,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ThemeOptionChip(
                        label = "Dark",
                        icon = Icons.Default.DarkMode,
                        selected = preferences.themeMode == AppThemeMode.DARK,
                        modifier = Modifier.weight(1f),
                        onClick = { onThemeChange(AppThemeMode.DARK) }
                    )
                    ThemeOptionChip(
                        label = "Light",
                        icon = Icons.Default.LightMode,
                        selected = preferences.themeMode == AppThemeMode.LIGHT,
                        modifier = Modifier.weight(1f),
                        onClick = { onThemeChange(AppThemeMode.LIGHT) }
                    )
                    ThemeOptionChip(
                        label = "System",
                        icon = Icons.Default.Settings,
                        selected = preferences.themeMode == AppThemeMode.SYSTEM,
                        modifier = Modifier.weight(1f),
                        onClick = { onThemeChange(AppThemeMode.SYSTEM) }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
                HorizontalDivider(color = colors.borderStroke)
                Spacer(modifier = Modifier.height(16.dp))

                // Section 2: Feedback (Sound & Vibration)
                Text(
                    text = "FEEDBACK",
                    fontFamily = SpaceGroteskFontFamily,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MoonlightPurple,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                SettingSwitchRow(
                    label = "Sound Effects",
                    description = "Futuristic synthesized key tones",
                    icon = Icons.Default.VolumeUp,
                    checked = preferences.soundEnabled,
                    onCheckedChange = onSoundToggle
                )

                Spacer(modifier = Modifier.height(10.dp))

                SettingSwitchRow(
                    label = "Haptic Vibration",
                    description = "Tactile touch response on tap",
                    icon = Icons.Default.Vibration,
                    checked = preferences.hapticEnabled,
                    onCheckedChange = onHapticToggle
                )

                Spacer(modifier = Modifier.height(20.dp))
                HorizontalDivider(color = colors.borderStroke)
                Spacer(modifier = Modifier.height(16.dp))

                // Section 3: Formatting & Calculations
                Text(
                    text = "CALCULATIONS & FORMAT",
                    fontFamily = SpaceGroteskFontFamily,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MoonlightPurple,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Decimal Precision Options
                Text(
                    text = "Decimal Places",
                    fontFamily = SpaceGroteskFontFamily,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = colors.textPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val precisionOptions = listOf(-1 to "Auto", 2 to "2", 4 to "4", 6 to "6", 8 to "8")
                    for ((prec, text) in precisionOptions) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (preferences.decimalPrecision == prec) ElectricBlue else colors.numberButtonBg,
                            border = BorderStroke(1.dp, if (preferences.decimalPrecision == prec) ElectricBlue else colors.borderStroke),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onDecimalPrecisionChange(prec) }
                        ) {
                            Text(
                                text = text,
                                fontFamily = SpaceGroteskFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp,
                                color = if (preferences.decimalPrecision == prec) colors.textEquals else colors.textPrimary,
                                modifier = Modifier.padding(vertical = 8.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Number Separator Style
                Text(
                    text = "Number Format",
                    fontFamily = SpaceGroteskFontFamily,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = colors.textPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    for (style in NumberFormatStyle.entries) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (preferences.formatStyle == style) ElectricBlue.copy(alpha = 0.15f) else colors.numberButtonBg,
                            border = BorderStroke(
                                1.dp,
                                if (preferences.formatStyle == style) ElectricBlue else colors.borderStroke
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onFormatStyleChange(style) }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = style.label,
                                    fontFamily = SpaceGroteskFontFamily,
                                    fontSize = 13.sp,
                                    color = if (preferences.formatStyle == style) ElectricBlue else colors.textPrimary,
                                    fontWeight = if (preferences.formatStyle == style) FontWeight.Bold else FontWeight.Normal
                                )
                                Text(
                                    text = style.example,
                                    fontFamily = SpaceGroteskFontFamily,
                                    fontSize = 12.sp,
                                    color = colors.textSecondary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
                HorizontalDivider(color = colors.borderStroke)
                Spacer(modifier = Modifier.height(16.dp))

                // About section
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = colors.textSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Jz Calculator • Version 1.0",
                        fontFamily = SpaceGroteskFontFamily,
                        fontSize = 12.sp,
                        color = colors.textSecondary
                    )
                }
            }
        }
    }
}

@Composable
private fun ThemeOptionChip(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val colors = LocalCalculatorColors.current
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = if (selected) ElectricBlue else colors.numberButtonBg,
        border = BorderStroke(1.dp, if (selected) ElectricBlue else colors.borderStroke),
        modifier = modifier.clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (selected) colors.textEquals else colors.textPrimary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                fontFamily = SpaceGroteskFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                color = if (selected) colors.textEquals else colors.textPrimary
            )
        }
    }
}

@Composable
private fun SettingSwitchRow(
    label: String,
    description: String,
    icon: ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val colors = LocalCalculatorColors.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = colors.textSecondary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = label,
                    fontFamily = SpaceGroteskFontFamily,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = colors.textPrimary
                )
                Text(
                    text = description,
                    fontFamily = SpaceGroteskFontFamily,
                    fontSize = 11.sp,
                    color = colors.textSecondary
                )
            }
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = colors.textEquals,
                checkedTrackColor = ElectricBlue,
                uncheckedThumbColor = colors.textSecondary,
                uncheckedTrackColor = colors.numberButtonBg
            )
        )
    }
}

package com.example.ui.screens

import android.content.res.Configuration
import android.view.KeyEvent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.focusable
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.preferences.NumberFormatStyle
import com.example.ui.components.CalculatorDisplay
import com.example.ui.components.HistoryBottomSheet
import com.example.ui.components.ScientificKeypad
import com.example.ui.components.SettingsDialog
import com.example.ui.components.StandardKeypad
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.LocalCalculatorColors
import com.example.ui.theme.MoonlightPurple
import com.example.ui.theme.SpaceGroteskFontFamily
import com.example.ui.viewmodel.CalculatorViewModel
import kotlinx.coroutines.delay

@Composable
fun CalculatorScreen(
    viewModel: CalculatorViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val colors = LocalCalculatorColors.current
    val context = LocalContext.current
    val view = LocalView.current
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val focusRequester = remember { FocusRequester() }

    // Request focus for hardware keyboard events
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    // Auto-dismiss toast message
    LaunchedEffect(state.toastMessage) {
        if (state.toastMessage != null) {
            delay(2500)
            viewModel.clearToast()
        }
    }

    val decimalSymbol = if (state.preferences.formatStyle == NumberFormatStyle.DOT_COMMA) "," else "."

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .focusRequester(focusRequester)
            .focusable()
            .onKeyEvent { keyEvent ->
                if (keyEvent.nativeKeyEvent.action == KeyEvent.ACTION_DOWN) {
                    when (keyEvent.nativeKeyEvent.keyCode) {
                        KeyEvent.KEYCODE_0, KeyEvent.KEYCODE_NUMPAD_0 -> { viewModel.onNumber("0", view); true }
                        KeyEvent.KEYCODE_1, KeyEvent.KEYCODE_NUMPAD_1 -> { viewModel.onNumber("1", view); true }
                        KeyEvent.KEYCODE_2, KeyEvent.KEYCODE_NUMPAD_2 -> { viewModel.onNumber("2", view); true }
                        KeyEvent.KEYCODE_3, KeyEvent.KEYCODE_NUMPAD_3 -> { viewModel.onNumber("3", view); true }
                        KeyEvent.KEYCODE_4, KeyEvent.KEYCODE_NUMPAD_4 -> { viewModel.onNumber("4", view); true }
                        KeyEvent.KEYCODE_5, KeyEvent.KEYCODE_NUMPAD_5 -> {
                            if (keyEvent.nativeKeyEvent.isShiftPressed) viewModel.onPercentage(view)
                            else viewModel.onNumber("5", view)
                            true
                        }
                        KeyEvent.KEYCODE_6, KeyEvent.KEYCODE_NUMPAD_6 -> {
                            if (keyEvent.nativeKeyEvent.isShiftPressed) viewModel.onOperator("^", view)
                            else viewModel.onNumber("6", view)
                            true
                        }
                        KeyEvent.KEYCODE_7, KeyEvent.KEYCODE_NUMPAD_7 -> { viewModel.onNumber("7", view); true }
                        KeyEvent.KEYCODE_8, KeyEvent.KEYCODE_NUMPAD_8 -> {
                            if (keyEvent.nativeKeyEvent.isShiftPressed) viewModel.onOperator("×", view)
                            else viewModel.onNumber("8", view)
                            true
                        }
                        KeyEvent.KEYCODE_9, KeyEvent.KEYCODE_NUMPAD_9 -> {
                            if (keyEvent.nativeKeyEvent.isShiftPressed) viewModel.onParentheses(view)
                            else viewModel.onNumber("9", view)
                            true
                        }
                        KeyEvent.KEYCODE_PLUS, KeyEvent.KEYCODE_NUMPAD_ADD -> { viewModel.onOperator("+", view); true }
                        KeyEvent.KEYCODE_MINUS, KeyEvent.KEYCODE_NUMPAD_SUBTRACT -> { viewModel.onOperator("−", view); true }
                        KeyEvent.KEYCODE_SLASH, KeyEvent.KEYCODE_NUMPAD_DIVIDE -> { viewModel.onOperator("÷", view); true }
                        KeyEvent.KEYCODE_STAR, KeyEvent.KEYCODE_NUMPAD_MULTIPLY -> { viewModel.onOperator("×", view); true }
                        KeyEvent.KEYCODE_PERIOD, KeyEvent.KEYCODE_COMMA, KeyEvent.KEYCODE_NUMPAD_DOT -> { viewModel.onDecimal(view); true }
                        KeyEvent.KEYCODE_EQUALS, KeyEvent.KEYCODE_ENTER, KeyEvent.KEYCODE_NUMPAD_ENTER -> { viewModel.onEquals(view); true }
                        KeyEvent.KEYCODE_DEL -> { viewModel.onBackspace(view); true }
                        KeyEvent.KEYCODE_ESCAPE -> { viewModel.onClear(view); true }
                        else -> false
                    }
                } else false
            },
        containerColor = colors.midnightBackground
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            if (isLandscape) {
                // --- Landscape Layout ---
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Left Column: Header + Display + Scientific Panel
                    Column(
                        modifier = Modifier
                            .weight(1.1f)
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        AppHeader(
                            historyCount = state.history.size,
                            onOpenHistory = { viewModel.setHistoryOpen(true) },
                            onOpenSettings = { viewModel.setSettingsOpen(true) }
                        )

                        CalculatorDisplay(
                            expression = state.expression,
                            liveResult = state.liveResult,
                            mainDisplay = state.mainDisplay,
                            isEvaluated = state.isEvaluated,
                            errorMessage = state.errorMessage,
                            angleUnit = state.angleUnit,
                            isScientificExpanded = state.isScientificExpanded,
                            onToggleScientific = { viewModel.onToggleScientific() },
                            onToggleAngleUnit = { viewModel.onToggleAngleUnit() },
                            onCopy = { viewModel.onCopyResult(context) },
                            onPaste = { viewModel.onPasteFromClipboard(context) },
                            onBackspace = { viewModel.onBackspace(view) },
                            modifier = Modifier.weight(1f, fill = false)
                        )

                        ScientificKeypad(
                            isInverse = state.isInverseTrig,
                            angleUnit = state.angleUnit,
                            onToggleInverse = { viewModel.onToggleInverseTrig() },
                            onToggleAngleUnit = { viewModel.onToggleAngleUnit() },
                            onFunction = { fn, v -> viewModel.onFunction(fn, v) },
                            onOperator = { op, v -> viewModel.onOperator(op, v) }
                        )
                    }

                    // Right Column: Standard Keypad
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize()
                    ) {
                        StandardKeypad(
                            decimalSeparatorSymbol = decimalSymbol,
                            onNumber = { num, v -> viewModel.onNumber(num, v) },
                            onDecimal = { v -> viewModel.onDecimal(v) },
                            onOperator = { op, v -> viewModel.onOperator(op, v) },
                            onParentheses = { v -> viewModel.onParentheses(v) },
                            onPlusMinus = { v -> viewModel.onPlusMinus(v) },
                            onPercentage = { v -> viewModel.onPercentage(v) },
                            onClear = { v -> viewModel.onClear(v) },
                            onEquals = { v -> viewModel.onEquals(v) },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            } else {
                // --- Portrait Layout ---
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    AppHeader(
                        historyCount = state.history.size,
                        onOpenHistory = { viewModel.setHistoryOpen(true) },
                        onOpenSettings = { viewModel.setSettingsOpen(true) }
                    )

                    // Hero Display Area
                    CalculatorDisplay(
                        expression = state.expression,
                        liveResult = state.liveResult,
                        mainDisplay = state.mainDisplay,
                        isEvaluated = state.isEvaluated,
                        errorMessage = state.errorMessage,
                        angleUnit = state.angleUnit,
                        isScientificExpanded = state.isScientificExpanded,
                        onToggleScientific = { viewModel.onToggleScientific() },
                        onToggleAngleUnit = { viewModel.onToggleAngleUnit() },
                        onCopy = { viewModel.onCopyResult(context) },
                        onPaste = { viewModel.onPasteFromClipboard(context) },
                        onBackspace = { viewModel.onBackspace(view) }
                    )

                    // Scientific panel (Animated Expansion)
                    AnimatedVisibility(
                        visible = state.isScientificExpanded,
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        ScientificKeypad(
                            isInverse = state.isInverseTrig,
                            angleUnit = state.angleUnit,
                            onToggleInverse = { viewModel.onToggleInverseTrig() },
                            onToggleAngleUnit = { viewModel.onToggleAngleUnit() },
                            onFunction = { fn, v -> viewModel.onFunction(fn, v) },
                            onOperator = { op, v -> viewModel.onOperator(op, v) },
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }

                    // Standard Keypad fills all remaining vertical space proportionally!
                    StandardKeypad(
                        decimalSeparatorSymbol = decimalSymbol,
                        onNumber = { num, v -> viewModel.onNumber(num, v) },
                        onDecimal = { v -> viewModel.onDecimal(v) },
                        onOperator = { op, v -> viewModel.onOperator(op, v) },
                        onParentheses = { v -> viewModel.onParentheses(v) },
                        onPlusMinus = { v -> viewModel.onPlusMinus(v) },
                        onPercentage = { v -> viewModel.onPercentage(v) },
                        onClear = { v -> viewModel.onClear(v) },
                        onEquals = { v -> viewModel.onEquals(v) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(bottom = 6.dp)
                    )
                }
            }

            // Floating Toast notification (e.g. Copied / Pasted)
            AnimatedVisibility(
                visible = state.toastMessage != null,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 70.dp)
            ) {
                state.toastMessage?.let { msg ->
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = ElectricBlue,
                        shadowElevation = 8.dp,
                        border = BorderStroke(1.dp, MoonlightPurple)
                    ) {
                        Text(
                            text = msg,
                            color = colors.textEquals,
                            fontFamily = SpaceGroteskFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp)
                        )
                    }
                }
            }
        }
    }

    // History Sheet
    if (state.isHistoryOpen) {
        HistoryBottomSheet(
            historyList = state.history,
            onDismiss = { viewModel.setHistoryOpen(false) },
            onUseResult = { item -> viewModel.onUseHistoryResult(item, view) },
            onUseExpression = { item -> viewModel.onUseHistoryExpression(item, view) },
            onDeleteSingle = { id -> viewModel.onDeleteHistory(id) },
            onClearAll = { viewModel.onClearAllHistory() }
        )
    }

    // Settings Dialog
    if (state.isSettingsOpen) {
        SettingsDialog(
            preferences = state.preferences,
            onDismiss = { viewModel.setSettingsOpen(false) },
            onThemeChange = { mode -> viewModel.setThemeMode(mode) },
            onSoundToggle = { enabled -> viewModel.setSoundEnabled(enabled) },
            onHapticToggle = { enabled -> viewModel.setHapticEnabled(enabled) },
            onDecimalPrecisionChange = { prec -> viewModel.setDecimalPrecision(prec) },
            onFormatStyleChange = { style -> viewModel.setFormatStyle(style) }
        )
    }
}

@Composable
private fun AppHeader(
    historyCount: Int,
    onOpenHistory: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val colors = LocalCalculatorColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // App Logo & Title
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.testTag("app_header_branding")
        ) {
            Image(
                painter = painterResource(id = com.example.R.drawable.ic_jz_logo),
                contentDescription = "Jz Calculator Logo",
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(
                    text = "Jz Calculator",
                    fontFamily = SpaceGroteskFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = colors.textPrimary
                )
                Text(
                    text = "3D Tactile Edition",
                    fontFamily = SpaceGroteskFontFamily,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = com.example.ui.theme.AccentOrange
                )
            }
        }

        // Action Buttons: History & Settings
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = onOpenHistory,
                modifier = Modifier.testTag("btn_open_history")
            ) {
                if (historyCount > 0) {
                    BadgedBox(
                        badge = {
                            Badge(
                                containerColor = ElectricBlue,
                                contentColor = colors.textEquals
                            ) {
                                Text(
                                    text = if (historyCount > 99) "99+" else "$historyCount",
                                    fontSize = 10.sp,
                                    fontFamily = SpaceGroteskFontFamily
                                )
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "History",
                            tint = colors.textPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                } else {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = "History",
                        tint = colors.textPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            IconButton(
                onClick = onOpenSettings,
                modifier = Modifier.testTag("btn_open_settings")
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = colors.textPrimary,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

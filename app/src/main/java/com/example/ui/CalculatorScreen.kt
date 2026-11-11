package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.util.CalculatorEngine

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(onBack: () -> Unit) {
    var expression by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }
    var isScientific by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Calculator") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { isScientific = !isScientific }) {
                        Icon(if (isScientific) Icons.Default.Functions else Icons.Default.Abc, contentDescription = "Toggle Mode")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Display
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(bottom = 16.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = expression,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = result,
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Keypad
            val buttons = if (isScientific) {
                listOf(
                    listOf("C", "(", ")", "/"),
                    listOf("sin", "cos", "tan", "*"),
                    listOf("log", "ln", "sqrt", "-"),
                    listOf("7", "8", "9", "+"),
                    listOf("4", "5", "6", "^"),
                    listOf("1", "2", "3", "BS"),
                    listOf("0", ".", "%", "=")
                )
            } else {
                listOf(
                    listOf("C", "(", ")", "/"),
                    listOf("7", "8", "9", "*"),
                    listOf("4", "5", "6", "-"),
                    listOf("1", "2", "3", "+"),
                    listOf("0", ".", "BS", "=")
                )
            }

            buttons.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    row.forEach { label ->
                        CalcButton(
                            label = label,
                            modifier = Modifier.weight(1f).fillMaxHeight(),
                            onClick = {
                                when (label) {
                                    "C" -> {
                                        expression = ""
                                        result = ""
                                    }
                                    "BS" -> if (expression.isNotEmpty()) expression = expression.dropLast(1)
                                    "=" -> result = CalculatorEngine.evaluate(expression)
                                    else -> expression += label
                                }
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun CalcButton(label: String, modifier: Modifier, onClick: () -> Unit) {
    val containerColor = when {
        label == "=" -> MaterialTheme.colorScheme.primary
        label in listOf("+", "-", "*", "/", "^", "sqrt", "sin", "cos", "tan", "log", "ln") -> MaterialTheme.colorScheme.secondaryContainer
        label == "C" || label == "BS" -> MaterialTheme.colorScheme.errorContainer
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    val contentColor = when {
        label == "=" -> MaterialTheme.colorScheme.onPrimary
        label == "C" || label == "BS" -> MaterialTheme.colorScheme.onErrorContainer
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(containerColor = containerColor, contentColor = contentColor),
        shape = MaterialTheme.shapes.medium,
        contentPadding = PaddingValues(0.dp)
    ) {
        if (label == "BS") {
            Icon(Icons.AutoMirrored.Filled.Backspace, contentDescription = "Backspace")
        } else {
            Text(text = label, fontSize = 20.sp, fontWeight = FontWeight.Medium)
        }
    }
}

package com.example.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.example.util.FlashlightManager

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun FlashlightScreen(flashlightManager: FlashlightManager, onBack: () -> Unit) {
    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)
    val isTorchOn by flashlightManager.isTorchOn.collectAsState()
    var strobeSpeed by remember { mutableFloatStateOf(0f) }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Flashlight") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        if (cameraPermissionState.status.isGranted) {
            FlashlightContent(
                innerPadding = innerPadding,
                isTorchOn = isTorchOn,
                strobeSpeed = strobeSpeed,
                onStrobeSpeedChange = { strobeSpeed = it },
                onToggleTorch = { flashlightManager.toggleTorch() },
                onSetTorchMode = { flashlightManager.setTorchMode(it) }
            )
        } else {
            Box(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Camera permission is required for the flashlight.")
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { cameraPermissionState.launchPermissionRequest() }) {
                        Text("Grant Permission")
                    }
                }
            }
        }
    }
}

@Composable
fun FlashlightContent(
    innerPadding: PaddingValues,
    isTorchOn: Boolean,
    strobeSpeed: Float,
    onStrobeSpeedChange: (Float) -> Unit,
    onToggleTorch: () -> Unit,
    onSetTorchMode: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Torch Icon with Glow effect when on
        val infiniteTransition = rememberInfiniteTransition()
        val glowScale by infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1.2f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            )
        )

        Box(
            modifier = Modifier
                .size(200.dp)
                .clip(CircleShape)
                .background(
                    if (isTorchOn) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                    else Color.Transparent
                )
                .padding(if (isTorchOn) (20 * (glowScale - 1)).dp else 0.dp),
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                onClick = onToggleTorch,
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(if (isTorchOn) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Icon(
                    imageVector = Icons.Default.PowerSettingsNew,
                    contentDescription = "Toggle Flashlight",
                    modifier = Modifier.size(64.dp),
                    tint = if (isTorchOn) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(64.dp))

        Text("Strobe Mode", style = MaterialTheme.typography.titleMedium)
        Slider(
            value = strobeSpeed,
            onValueChange = onStrobeSpeedChange,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
        Text(
            text = if (strobeSpeed > 0) "Speed: ${(strobeSpeed * 10).toInt()}" else "Off",
            style = MaterialTheme.typography.bodyMedium
        )

        if (strobeSpeed > 0) {
            LaunchedEffect(strobeSpeed) {
                while (true) {
                    onSetTorchMode(true)
                    kotlinx.coroutines.delay((1000 / (strobeSpeed * 10 + 1)).toLong())
                    onSetTorchMode(false)
                    kotlinx.coroutines.delay((1000 / (strobeSpeed * 10 + 1)).toLong())
                }
            }
        }
    }
}

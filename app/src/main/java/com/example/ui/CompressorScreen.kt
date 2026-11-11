package com.example.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompressorScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var originalSize by remember { mutableLongStateOf(0L) }
    var compressedSize by remember { mutableLongStateOf(0L) }
    var quality by remember { mutableFloatStateOf(0.8f) }
    var isCompressing by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        selectedImageUri = uri
        uri?.let {
            context.contentResolver.openAssetFileDescriptor(it, "r")?.use { fd ->
                originalSize = fd.length
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Image Compressor") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = { launcher.launch("image/*") }) {
                Icon(Icons.Default.PhotoLibrary, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Select Image")
            }

            selectedImageUri?.let { uri ->
                Spacer(modifier = Modifier.height(24.dp))
                Text("Original Size: ${formatSize(originalSize)}")
                Spacer(modifier = Modifier.height(16.dp))

                Text("Compression Quality: ${(quality * 100).toInt()}%")
                Slider(
                    value = quality,
                    onValueChange = { quality = it },
                    valueRange = 0.1f..1.0f,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                if (isCompressing) {
                    CircularProgressIndicator()
                } else {
                    Button(onClick = {
                        isCompressing = true
                        val bitmap = BitmapFactory.decodeStream(context.contentResolver.openInputStream(uri))
                        val outputStream = ByteArrayOutputStream()
                        bitmap.compress(Bitmap.CompressFormat.JPEG, (quality * 100).toInt(), outputStream)
                        compressedSize = outputStream.size().toLong()

                        // Save to cache for sharing
                        val compressedFile = File(context.cacheDir, "compressed_image.jpg")
                        FileOutputStream(compressedFile).use { it.write(outputStream.toByteArray()) }

                        isCompressing = false
                    }) {
                        Text("Compress Now")
                    }
                }

                if (compressedSize > 0) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text("Compressed Size: ${formatSize(compressedSize)}")
                    val saved = originalSize - compressedSize
                    Text("Saved: ${formatSize(saved)} (${(saved.toDouble() / originalSize * 100).toInt()}%)", color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

fun formatSize(bytes: Long): String {
    val kb = bytes / 1024.0
    val mb = kb / 1024.0
    return if (mb >= 1) String.format("%.2f MB", mb) else String.format("%.2f KB", kb)
}

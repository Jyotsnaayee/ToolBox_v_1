package com.example.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileReaderScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    var fileContent by remember { mutableStateOf("") }
    var fileName by remember { mutableStateOf("") }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        selectedFileUri = uri
        uri?.let {
            fileName = it.path?.split("/")?.lastOrNull() ?: "File"
            try {
                context.contentResolver.openInputStream(it)?.use { stream ->
                    fileContent = stream.bufferedReader().use { reader -> reader.readText() }
                }
            } catch (e: Exception) {
                fileContent = "Could not read text content (Binary file or error)."
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("File Reader") },
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
                .padding(16.dp)
        ) {
            Button(
                onClick = { launcher.launch(arrayOf("*/*")) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.InsertDriveFile, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Open File")
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (selectedFileUri != null) {
                Text("Viewing: $fileName", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    modifier = Modifier.fillMaxSize().weight(1f),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = MaterialTheme.shapes.medium
                ) {
                    LazyColumn(modifier = Modifier.padding(16.dp)) {
                        item {
                            Text(text = fileContent, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Select a file to view its content")
                }
            }
        }
    }
}

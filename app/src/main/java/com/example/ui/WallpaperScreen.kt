package com.example.ui

import android.app.WallpaperManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.request.SuccessResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WallpaperScreen(onBack: () -> Unit) {
    val categories = listOf("All", "Nature", "Space", "Tech", "Cars", "Abstract", "Animals")
    var selectedCategory by remember { mutableStateOf("All") }
    val wallpaperIds = remember(selectedCategory) { 
        // In a real app, this would fetch filtered results from an API
        (10..60).toList().shuffled() 
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Wallpapers") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { selectedCategory = category },
                        label = { Text(category) }
                    )
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(wallpaperIds) { id ->
                    WallpaperItem(id)
                }
            }
        }
    }
}

@Composable
fun WallpaperItem(id: Int) {
    var showDialog by remember { mutableStateOf(false) }
    var isApplying by remember { mutableStateOf(false) }
    val imageUrl = "https://picsum.photos/id/$id/1080/1920"
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.6f)
            .clickable { showDialog = true },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { if (!isApplying) showDialog = false },
            title = { Text("Set Wallpaper") },
            text = { 
                if (isApplying) {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    Text("Where would you like to apply this wallpaper?")
                }
            },
            confirmButton = {
                if (!isApplying) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                isApplying = true
                                scope.launch {
                                    applyWallpaper(context, imageUrl, WallpaperManager.FLAG_SYSTEM)
                                    isApplying = false
                                    showDialog = false
                                    Toast.makeText(context, "Home screen updated", Toast.LENGTH_SHORT).show()
                                }
                            }
                        ) { Text("Home Screen") }
                        
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                isApplying = true
                                scope.launch {
                                    applyWallpaper(context, imageUrl, WallpaperManager.FLAG_LOCK)
                                    isApplying = false
                                    showDialog = false
                                    Toast.makeText(context, "Lock screen updated", Toast.LENGTH_SHORT).show()
                                }
                            }
                        ) { Text("Lock Screen") }

                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                isApplying = true
                                scope.launch {
                                    applyWallpaper(context, imageUrl, WallpaperManager.FLAG_SYSTEM or WallpaperManager.FLAG_LOCK)
                                    isApplying = false
                                    showDialog = false
                                    Toast.makeText(context, "Wallpaper updated", Toast.LENGTH_SHORT).show()
                                }
                            }
                        ) { Text("Both") }
                    }
                }
            },
            dismissButton = {
                if (!isApplying) {
                    TextButton(onClick = { showDialog = false }) { Text("Cancel") }
                }
            }
        )
    }
}

suspend fun applyWallpaper(context: android.content.Context, url: String, flag: Int) {
    withContext(Dispatchers.IO) {
        val loader = ImageLoader(context)
        val request = ImageRequest.Builder(context)
            .data(url)
            .allowHardware(false) // Required for getting bitmap
            .build()

        val result = (loader.execute(request) as? SuccessResult)?.drawable
        val bitmap = (result as? BitmapDrawable)?.bitmap

        if (bitmap != null) {
            val wallpaperManager = WallpaperManager.getInstance(context)
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    wallpaperManager.setBitmap(bitmap, null, true, flag)
                } else {
                    wallpaperManager.setBitmap(bitmap)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

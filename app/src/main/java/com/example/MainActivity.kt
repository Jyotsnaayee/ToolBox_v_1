package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.data.local.AppDatabase
import com.example.data.repository.AuthRepository
import com.example.data.repository.WeatherRepository
import com.example.ui.*
import com.example.ui.theme.ToolboxTheme
import com.example.util.FlashlightManager
import com.example.util.ViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val authRepository = AuthRepository(this)
        val database = AppDatabase.getDatabase(this)
        val weatherRepository = WeatherRepository()
        val flashlightManager = FlashlightManager(this)

        setContent {
            ToolboxTheme {
                val authViewModel: AuthViewModel = viewModel(factory = ViewModelFactory { AuthViewModel(authRepository) })
                val currentUser by authViewModel.currentUser.collectAsState()
                val isGuestMode by authViewModel.isGuestMode.collectAsState()

                if (currentUser == null && !isGuestMode) {
                    AuthScreen(
                        onSignInClick = { authViewModel.signIn(this@MainActivity) { } },
                        onContinueAsGuest = { authViewModel.setGuestMode(true) }
                    )
                } else {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "home") {
                        composable("home") {
                            HomeScreen(
                                onToolClick = { route -> navController.navigate(route) },
                                onSignOut = { authViewModel.signOut() }
                            )
                        }
                        composable("calculator") {
                            CalculatorScreen(onBack = { navController.popBackStack() })
                        }
                        composable("flashlight") {
                            FlashlightScreen(flashlightManager, onBack = { navController.popBackStack() })
                        }
                        composable("compressor") {
                            CompressorScreen(onBack = { navController.popBackStack() })
                        }
                        composable("file_reader") {
                            FileReaderScreen(onBack = { navController.popBackStack() })
                        }
                        composable("apk_converter") {
                            ApkConverterScreen(onBack = { navController.popBackStack() })
                        }
                        composable("news") {
                            NewsScreen(onBack = { navController.popBackStack() })
                        }
                        composable("weather") {
                            val weatherViewModel: WeatherViewModel = viewModel(factory = ViewModelFactory { WeatherViewModel(weatherRepository) })
                            WeatherScreen(weatherViewModel, onBack = { navController.popBackStack() })
                        }
                        composable("wallpapers") {
                            WallpaperScreen(onBack = { navController.popBackStack() })
                        }
                    }
                }
            }
        }
    }
}

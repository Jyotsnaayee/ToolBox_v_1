package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.AuthRepository
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {
    val currentUser: StateFlow<FirebaseUser?> = repository.currentUser
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _isGuestMode = MutableStateFlow(false)
    val isGuestMode: StateFlow<Boolean> = _isGuestMode.asStateFlow()

    fun setGuestMode(enabled: Boolean) {
        _isGuestMode.value = enabled
        if (enabled) {
            // If entering guest mode, just ensure we are signed out from Firebase
            viewModelScope.launch {
                repository.signOut()
            }
        }
    }

    fun signIn(activity: android.app.Activity, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            val result = repository.signInWithGoogle(activity)
            if (result.isSuccess) {
                _isGuestMode.value = false
            }
            onResult(result)
        }
    }

    fun signOut() {
        viewModelScope.launch {
            repository.signOut()
            _isGuestMode.value = false
        }
    }

    fun attemptAutoSignIn() {
        viewModelScope.launch {
            repository.attemptAutoSignIn()
        }
    }
}

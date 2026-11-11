package com.example.util

import android.content.Context
import android.hardware.camera2.CameraManager
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FlashlightManager(context: Context) {
    private val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    private var cameraId: String? = null

    private val _isTorchOn = MutableStateFlow(false)
    val isTorchOn: StateFlow<Boolean> = _isTorchOn.asStateFlow()

    init {
        try {
            cameraId = cameraManager.cameraIdList.firstOrNull()
            cameraManager.registerTorchCallback(object : CameraManager.TorchCallback() {
                override fun onTorchModeChanged(cameraId: String, enabled: Boolean) {
                    if (this@FlashlightManager.cameraId == cameraId) {
                        _isTorchOn.value = enabled
                    }
                }
            }, null)
        } catch (e: Exception) {
            Log.e("FlashlightManager", "Failed to initialize flashlight", e)
        }
    }

    fun toggleTorch() {
        val id = cameraId ?: return
        try {
            cameraManager.setTorchMode(id, !_isTorchOn.value)
        } catch (e: Exception) {
            Log.e("FlashlightManager", "Failed to toggle torch", e)
        }
    }

    fun setTorchMode(enabled: Boolean) {
        val id = cameraId ?: return
        try {
            cameraManager.setTorchMode(id, enabled)
        } catch (e: Exception) {
            Log.e("FlashlightManager", "Failed to set torch mode", e)
        }
    }
}

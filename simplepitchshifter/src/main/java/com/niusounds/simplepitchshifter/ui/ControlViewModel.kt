package com.niusounds.simplepitchshifter.ui

import android.Manifest
import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.cockos.wdl.SimplePitchShifter
import com.niusounds.simplepitchshifter.service.AudioProcessingService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlin.math.pow

class ControlViewModel(application: Application) : AndroidViewModel(application) {
    private val context: Context get() = getApplication()

    private val _processing = MutableStateFlow(false)
    val processing: LiveData<Boolean> = _processing.asLiveData()

    val minPitch = -24
    val maxPitch = +24

    private val _permissionDialogEvent = MutableSharedFlow<String>()
    val permissionDialogEvent: Flow<String> = _permissionDialogEvent

    private val hasPermission: Boolean get() = AudioProcessingService.hasPermission(context)

    fun togglePlaying() = viewModelScope.launch {
        if (_processing.value) {
            stopService()
        } else {
            if (hasPermission) {
                Log.d("SimplePitchShifter", "${SimplePitchShifter.describeQualities()}") // test
                startService()
            } else {
                _permissionDialogEvent.emit(Manifest.permission.RECORD_AUDIO)
            }
        }
    }

    private fun startService() {
        AudioProcessingService.start(context)
        _processing.value = true
    }

    private fun stopService() {
        AudioProcessingService.stop(context)
        _processing.value = false
    }

    fun onRequestPermissionsResult(permissions: Array<out String>, grantResults: IntArray) {
        // 録音パーミッションの結果を受けた時
        if (permissions.isNotEmpty() && permissions[0] == Manifest.permission.RECORD_AUDIO) {
            if (hasPermission) {
                viewModelScope.launch {
                    startService()
                }
            }
        }
    }

    fun onChangeSlider(semitone: Float) {
        val shift = 2.0.pow(semitone.toDouble() / 12.0)
        AudioProcessingService.configure(context, "shift", shift)
    }
}
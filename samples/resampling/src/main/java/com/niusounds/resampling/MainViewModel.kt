package com.niusounds.resampling

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.niusounds.resampling.audio.AudioEngine
import com.niusounds.resampling.audio.ResamplerProcessor
import kotlinx.coroutines.flow.MutableStateFlow

class MainViewModel : ViewModel() {
    val inputSampleRate = MutableStateFlow(44100)
    val outputSampleRate = MutableStateFlow(48000)

    private val _playing = MutableStateFlow(false)
    val playing: LiveData<Boolean> = _playing.asLiveData()

    private var audioEngine: AudioEngine? = null

    fun togglePlaying() {
        if (_playing.value) {
            _playing.value = false

            audioEngine?.release()
            audioEngine = null
        } else {
            _playing.value = true

            val inRate = inputSampleRate.value
            val outRate = outputSampleRate.value

            val resampler = ResamplerProcessor(inRate, outRate, 1)
            audioEngine = AudioEngine(inRate, outRate, 1, resampler).apply {
                start()
            }
        }
    }
}
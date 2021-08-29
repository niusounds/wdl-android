package com.niusounds.simplepitchshifter.audio

import java.nio.ByteBuffer

interface IAudioProcessor {
    fun init(sampleRate: Int, channels: Int, frameSize: Int)

    fun configure(key: String, value: Any)

    fun process(buffer: FloatArray, bufferSize: Int)
    fun process(buffer: ByteBuffer, bufferSize: Int)

    fun release()
}
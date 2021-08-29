package com.niusounds.simplepitchshifter.audio

import android.util.Log
import com.cockos.wdl.SimplePitchShifter
import java.nio.ByteBuffer

class SimplePitchShifterProcessor : IAudioProcessor {
    private lateinit var pitchShifter: SimplePitchShifter
    private var channels = 0
    private var frameSize = 0
    private lateinit var tempBuffer: FloatArray

    override fun init(sampleRate: Int, channels: Int, frameSize: Int) {
        this.channels = channels
        this.frameSize = frameSize
        this.tempBuffer = FloatArray(frameSize * channels)
        pitchShifter = SimplePitchShifter(frameSize).apply {
            setNch(channels)
            setSrate(sampleRate.toDouble())
            prepare()
        }
    }

    override fun configure(key: String, value: Any) {
        when (key) {
            "shift" -> {
                (value as Double?)?.let { pitchShifter.setShift(it) }
            }
            "qualityParameter" -> {
                (value as Int?)?.let { pitchShifter.setQualityParameter(it) }
            }
        }
    }

    override fun process(buffer: FloatArray, bufferSize: Int) {
        // interleaved to non-interleaved
//        for (ch in 0 until channels) {
//            for (i in 0 until frameSize) {
//                tempBuffer[i + ch * frameSize] = buffer[i * channels + ch]
//            }
//        }
        System.arraycopy(buffer, 0, tempBuffer, 0, buffer.size)

        pitchShifter.write(tempBuffer, bufferSize)
        pitchShifter.bufferDone(bufferSize / channels)
        val samples = pitchShifter.getSamples(tempBuffer, bufferSize)

        // non-interleaved to interleaved
//        for (ch in 0 until channels) {
//            for (i in 0 until frameSize) {
//                buffer[i * channels + ch] = tempBuffer[i + ch * frameSize]
//            }
//        }
        System.arraycopy(tempBuffer, 0, buffer, 0, buffer.size)
        Log.d("SimplePitchShifterProcessor", "samples:$samples")
    }

    override fun process(buffer: ByteBuffer, bufferSize: Int) {
        pitchShifter.write(buffer, bufferSize)
        pitchShifter.bufferDone(bufferSize / channels)
        val samples = pitchShifter.getSamples(buffer, bufferSize)
        Log.d("SimplePitchShifterProcessor", "samples:$samples")
    }

    override fun release() {
        pitchShifter.release()
    }
}
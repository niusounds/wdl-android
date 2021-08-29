package com.niusounds.resampling.audio

import com.cockos.wdl.Resampler
import java.nio.ByteBuffer

class ResamplerProcessor(
    private val inSampleRate: Int,
    private val outSampleRate: Int,
    private val channels: Int
) {
    private val resampler = Resampler()

    init {
        resampler.setRates(inSampleRate.toDouble(), outSampleRate.toDouble())
        resampler.setFeedMode(true)
    }

    fun resample(inBuffer: ByteBuffer, inSamples: Int, outBuffer: ByteBuffer) {
        outBuffer.clear()
        val num = resampler.resamplePrepare(inSamples, channels, inBuffer)
        val out = resampler.resampleOut(
            outBuffer,
            inSamples,
            inSamples * outSampleRate / inSampleRate,
            channels
        )
//        Log.d("resampler", "in:$inSamples num:$num out:$out ${resampler.getCurrentLatency()}")
    }
}
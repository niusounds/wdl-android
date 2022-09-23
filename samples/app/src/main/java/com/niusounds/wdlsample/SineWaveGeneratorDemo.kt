package com.niusounds.wdlsample

import android.content.Context
import com.cockos.wdl.SineWaveGenerator

class SineWaveGeneratorDemo(
    context: Context,
    val volumeGain: Double,
) : AudioDemo(context, useInput = false) {
    private var sineWaveGenerator: SineWaveGenerator? = null

    override fun init(sampleRate: Int, bufferSize: Int) {
        sineWaveGenerator = SineWaveGenerator().apply {
            setFreq(440.0 / sampleRate)
        }
    }

    override fun process(audioData: FloatArray) {
        val sineWaveGenerator = sineWaveGenerator ?: return
        for (i in audioData.indices) {
            audioData[i] = (sineWaveGenerator.gen() * volumeGain).toFloat()
        }
    }

    override fun release() {
        sineWaveGenerator?.release()
        sineWaveGenerator = null
    }
}
package com.niusounds.wdlsample

import android.content.Context
import com.cockos.wdl.SimplePitchShifter

class PitchShift(
    context: Context,
) : AudioDemo(context, useInput = true) {
    private var pitchShifter: SimplePitchShifter? = null

    override fun init(sampleRate: Int, bufferSize: Int) {
        pitchShifter = SimplePitchShifter(bufferSize).apply {
            setNch(1)
//                setTempo(120.0)
            setSrate(sampleRate.toDouble())
            setShift(2.0)
            prepare()
        }
    }

    override fun process(audioData: FloatArray) {
        val pitchShifter = pitchShifter ?: return
        pitchShifter.write(audioData, audioData.size)
        pitchShifter.bufferDone(audioData.size)
        pitchShifter.getSamples(audioData, audioData.size)
    }

    override fun release() {
        pitchShifter?.release()
        pitchShifter = null
    }
}

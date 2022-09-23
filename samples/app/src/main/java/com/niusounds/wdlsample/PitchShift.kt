package com.niusounds.wdlsample

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioRecord
import android.media.AudioTrack
import android.media.MediaRecorder
import android.os.Build
import android.os.Process
import androidx.core.content.getSystemService
import com.cockos.wdl.SimplePitchShifter
import kotlin.concurrent.thread

class PitchShift(
    private val context: Context,
) {
    private var audioThread: Thread? = null

    @SuppressLint("MissingPermission")
    fun startAudio() {
        audioThread?.let { it.interrupt();it.join() }
        val audioManager: AudioManager = context.getSystemService()!!
        val sampleRate = audioManager.getProperty(AudioManager.PROPERTY_OUTPUT_SAMPLE_RATE).toInt()
        audioThread = thread {
            Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO)

            val format = AudioFormat.ENCODING_PCM_FLOAT
            val bufferSize = 4096
//                max(
//                    AudioTrack.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_OUT_MONO, format),
//                    AudioRecord.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_IN_MONO, format),
//                )
            val track = audioTrack {
                setAudioFormat(
                    AudioFormat.Builder()
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .setEncoding(format)
                        .build()
                )
                setBufferSizeInBytes(bufferSize)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    setPerformanceMode(AudioTrack.PERFORMANCE_MODE_LOW_LATENCY)
                }
            }

            val record = AudioRecord.Builder()
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_IN_MONO)
                        .setEncoding(format)
                        .build()
                )
                .setBufferSizeInBytes(bufferSize)
                .setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION)
                .build()

            val buffer = FloatArray(bufferSize)
            track.play()
            record.startRecording()

//            val fft = FFT()
            val pitchShifter = SimplePitchShifter(bufferSize).apply {
                setNch(1)
//                setTempo(120.0)
                setSrate(sampleRate.toDouble())
                setShift(2.0)
                prepare()
            }

            try {
                while (!Thread.interrupted()) {

                    if (!record.fillBuffer(buffer, bufferSize)) break

                    pitchShifter.write(buffer, bufferSize)
                    pitchShifter.bufferDone(bufferSize)
                    val size = pitchShifter.getSamples(buffer, bufferSize)
                    if (pitchShifter.isReset()) {
//                        Log.d(tag, "reset")
                    }

//                fft.realFft(buffer, bufferSize, false)

                    // process buffer[0..bufferSize/2]
//                Log.d(tag, "0:${buffer[0]} 1:${buffer[1]} 2:${buffer[2]}")

                    track.write(buffer, 0, bufferSize, AudioTrack.WRITE_BLOCKING)
                }
            } finally {
                track.stop()
                track.release()
                record.stop()
                record.release()
            }
        }
    }

    fun stopAudio() {
        audioThread?.interrupt()
        audioThread?.join()
        audioThread = null
    }
}

fun audioTrack(block: AudioTrack.Builder.() -> Unit): AudioTrack = AudioTrack.Builder()
    .apply(block)
    .build()

fun AudioRecord.fillBuffer(
    buffer: FloatArray,
    bufferSize: Int,
    mode: Int = AudioRecord.READ_BLOCKING
): Boolean {
    var offset = 0
    while (offset < bufferSize) {
        val readSize = read(buffer, offset, bufferSize - offset, mode)
        if (readSize < 0) {
            return false
        }
        offset += readSize
    }

    return true
}

package com.niusounds.wdlsample

import android.content.Context
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioRecord
import android.media.AudioTrack
import android.media.MediaRecorder
import android.os.Build
import android.os.Process
import androidx.core.content.getSystemService
import kotlin.concurrent.thread

abstract class AudioDemo(
    private val context: Context,
    private val useInput: Boolean,
) : Demo {
    private var audioThread: Thread? = null

    /**
     * Called from audio thread before processing audio samples.
     * Do one time initialization such as instantiating WDL classes.
     */
    abstract fun init(sampleRate: Int, bufferSize: Int)

    /**
     * Called from audio thread.
     * If `useInput = true`, [audioData] will be prefilled with recorded samples.
     * Override this method and process, fill audio samples into [audioData].
     */
    abstract fun process(audioData: FloatArray)

    /**
     * Called from audio thread when [Demo.stop] was called.
     * In this method, release native resources such as WDL classes created in [init].
     */
    abstract fun release()

    override fun start() {
        if (audioThread != null) return

        val audioManager: AudioManager = context.getSystemService()!!
        val sampleRate = audioManager.getProperty(AudioManager.PROPERTY_OUTPUT_SAMPLE_RATE).toInt()
        audioThread = thread {
            Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO)

            val format = AudioFormat.ENCODING_PCM_FLOAT
            val bufferSize = 4096
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

            val record: AudioRecord? = if (useInput) audioRecord {
                setAudioFormat(
                    audioFormat {
                        setSampleRate(sampleRate)
                        setChannelMask(AudioFormat.CHANNEL_IN_MONO)
                        setEncoding(format)
                    }
                )
                setBufferSizeInBytes(bufferSize)
                setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION)
            } else null

            val buffer = FloatArray(bufferSize)
            track.play()
            record?.startRecording()

            try {
                init(sampleRate, bufferSize)

                while (!Thread.interrupted()) {

                    record?.let {
                        if (!it.fillBuffer(buffer, bufferSize)) return@thread
                    }

                    process(buffer)

                    track.write(buffer, 0, bufferSize, AudioTrack.WRITE_BLOCKING)
                }
            } finally {
                release()
                track.stop()
                track.release()
                record?.stop()
                record?.release()
            }
        }
    }

    override fun stop() {
        audioThread?.interrupt()
        audioThread?.join()
        audioThread = null
    }
}

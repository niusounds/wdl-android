package com.niusounds.simplepitchshifter.audio

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.AudioTrack
import android.media.MediaRecorder
import android.os.Build
import android.os.Process
import android.util.Log
import java.nio.ByteBuffer
import kotlin.concurrent.thread

class AudioEngine(private val sampleRate: Int, private val channels: Int) {
    companion object {
        private const val tag = "AudioEngine"
    }

    private var writeThread: Thread? = null

    @SuppressWarnings("MissingPermission")
    fun start(processor: IAudioProcessor) {
        writeThread = thread {
            Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO)

            val format = AudioFormat.ENCODING_PCM_FLOAT
            val bufferSize = 3840

            val track = audioTrack {
                setAudioFormat(
                    AudioFormat.Builder()
                        .setSampleRate(sampleRate)
                        .setChannelMask(
                            when (channels) {
                                1 -> AudioFormat.CHANNEL_OUT_MONO
                                2 -> AudioFormat.CHANNEL_OUT_STEREO
                                else -> throw IllegalStateException("unsupported channels")
                            }
                        )
                        .setEncoding(format)
                        .build()
                )
//                setBufferSizeInBytes(bufferSize)
                setBufferSizeInBytes(
                    AudioTrack.getMinBufferSize(
                        sampleRate,
                        when (channels) {
                            1 -> AudioFormat.CHANNEL_OUT_MONO
                            2 -> AudioFormat.CHANNEL_OUT_STEREO
                            else -> throw IllegalStateException("unsupported channels")
                        },
                        format
                    )
                )

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    setPerformanceMode(AudioTrack.PERFORMANCE_MODE_LOW_LATENCY)
                }
            }

            val record = AudioRecord.Builder()
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setSampleRate(sampleRate)
                        .setChannelMask(
                            when (channels) {
                                1 -> AudioFormat.CHANNEL_IN_MONO
                                2 -> AudioFormat.CHANNEL_IN_STEREO
                                else -> throw IllegalStateException("unsupported channels")
                            }
                        )
                        .setEncoding(format)
                        .build()
                )
//                .setBufferSizeInBytes(bufferSize)
                .setBufferSizeInBytes(
                    AudioRecord.getMinBufferSize(
                        sampleRate,
                        when (channels) {
                            1 -> AudioFormat.CHANNEL_IN_MONO
                            2 -> AudioFormat.CHANNEL_IN_STEREO
                            else -> throw IllegalStateException("unsupported channels")
                        },
                        format
                    )
                )
                .setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION)
                .build()

//            val buffer = FloatArray(bufferSize * channels)
            val buffer = ByteBuffer.allocateDirect(bufferSize * Float.SIZE_BYTES)
            track.play()
            record.startRecording()

            processor.init(sampleRate, channels, bufferSize)

            while (!Thread.interrupted()) {
                if (!record.fillBuffer(buffer, bufferSize)) {
                    Log.e(tag, "cannot fill buffer")
                    break
                }

                processor.process(buffer, bufferSize)

//                track.write(buffer, 0, bufferSize, AudioTrack.WRITE_BLOCKING)
                track.write(buffer, bufferSize * Float.SIZE_BYTES, AudioTrack.WRITE_BLOCKING)
            }

            processor.release()

            track.stop()
            track.release()
            record.stop()
            record.release()
        }
    }

    fun release() {
        writeThread?.interrupt()
        writeThread?.join()
        writeThread = null
    }

    private fun AudioRecord.fillBuffer(
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

        if (offset < bufferSize) {
            return false
        }

        return true
    }

    private fun AudioRecord.fillBuffer(
        buffer: ByteBuffer,
        bufferSize: Int,
        mode: Int = AudioRecord.READ_BLOCKING
    ): Boolean {
        buffer.position(0)
        while (buffer.hasRemaining()) {
            val readSize = read(buffer, buffer.remaining(), mode)
            if (readSize < 0) {
                return false
            }
            buffer.position(readSize)
        }

        buffer.flip()
        return true
    }
}

fun audioTrack(block: AudioTrack.Builder.() -> Unit): AudioTrack = AudioTrack.Builder()
    .apply(block)
    .build()

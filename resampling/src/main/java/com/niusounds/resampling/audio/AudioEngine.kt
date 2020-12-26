package com.niusounds.resampling.audio

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.AudioTrack
import android.media.MediaRecorder
import android.os.Process
import android.util.Log
import java.nio.ByteBuffer
import kotlin.concurrent.thread

class AudioEngine(
    private val inSampleRate: Int,
    private val outSampleRate: Int,
    private val channels: Int,
    private val resampler: ResamplerProcessor,
) {
    companion object {
        private const val tag = "AudioEngine"
    }

    private var writeThread: Thread? = null

    fun start() {
        writeThread = thread {
            Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO)

            val format = AudioFormat.ENCODING_PCM_FLOAT
            val bufferSize = 3840

            val track = AudioTrack.Builder()
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setSampleRate(outSampleRate)
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
//                .setBufferSizeInBytes(bufferSize)
                .setBufferSizeInBytes(
                    AudioTrack.getMinBufferSize(
                        outSampleRate,
                        when (channels) {
                            1 -> AudioFormat.CHANNEL_OUT_MONO
                            2 -> AudioFormat.CHANNEL_OUT_STEREO
                            else -> throw IllegalStateException("unsupported channels")
                        },
                        format
                    )
                )
                .setPerformanceMode(AudioTrack.PERFORMANCE_MODE_LOW_LATENCY)
                .build()

            val record = AudioRecord.Builder()
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setSampleRate(inSampleRate)
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
                        inSampleRate,
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

            val inBuffer = ByteBuffer.allocateDirect(bufferSize * Float.SIZE_BYTES)
            val outBuffer =
                ByteBuffer.allocateDirect(inBuffer.capacity() * outSampleRate / inSampleRate)
            track.play()
            record.startRecording()

            while (!Thread.interrupted()) {
                if (!record.fillBuffer(inBuffer, bufferSize)) {
                    Log.e(tag, "cannot fill buffer")
                    break
                }

                resampler.resample(inBuffer, inBuffer.limit() / Float.SIZE_BYTES, outBuffer)

                track.write(outBuffer, outBuffer.limit(), AudioTrack.WRITE_BLOCKING)
            }

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
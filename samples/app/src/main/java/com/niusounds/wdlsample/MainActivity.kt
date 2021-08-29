package com.niusounds.wdlsample

import android.Manifest
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioRecord
import android.media.AudioTrack
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Process
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.cockos.wdl.SimplePitchShifter
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    companion object {
        private const val tag = "WDLSample"
        private const val requestCode = 1001
    }

    private var audioThread: Thread? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            startAudio()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                requestCode
            )
        }
    }

    override fun onDestroy() {
        stopAudio()
        super.onDestroy()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startAudio()
        }
    }

    private fun startAudio() {
        val audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        val sampleRate = audioManager.getProperty(AudioManager.PROPERTY_OUTPUT_SAMPLE_RATE).toInt()
        audioThread = thread {
            Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO)

            val format = AudioFormat.ENCODING_PCM_FLOAT
            val bufferSize = 4096
//                max(
//                    AudioTrack.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_OUT_MONO, format),
//                    AudioRecord.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_IN_MONO, format),
//                )
            val track = AudioTrack.Builder()
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .setEncoding(format)
                        .build()
                )
                .setBufferSizeInBytes(bufferSize)
                .setPerformanceMode(AudioTrack.PERFORMANCE_MODE_LOW_LATENCY)
                .build()

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

            while (!Thread.interrupted()) {

                if (!record.fillBuffer(buffer, bufferSize)) break

                pitchShifter.write(buffer, bufferSize)
                pitchShifter.bufferDone(bufferSize)
                val size = pitchShifter.getSamples(buffer, bufferSize)
                if (pitchShifter.isReset()) {
                    Log.d(tag, "reset")
                }

//                fft.realFft(buffer, bufferSize, false)

                // process buffer[0..bufferSize/2]
//                Log.d(tag, "0:${buffer[0]} 1:${buffer[1]} 2:${buffer[2]}")

                track.write(buffer, 0, bufferSize, AudioTrack.WRITE_BLOCKING)
            }

            track.stop()
            track.release()
            record.stop()
            record.release()
        }
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
            Log.e(tag, "cannot fill buffer")
            return false
        }

        return true
    }

    private fun stopAudio() {
        audioThread?.interrupt()
        audioThread?.join()
        audioThread = null
    }
}
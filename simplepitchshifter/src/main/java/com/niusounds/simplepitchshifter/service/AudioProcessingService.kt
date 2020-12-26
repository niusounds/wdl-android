package com.niusounds.simplepitchshifter.service

import android.Manifest
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.media.AudioManager
import android.os.IBinder
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import com.niusounds.simplepitchshifter.audio.AudioEngine
import com.niusounds.simplepitchshifter.audio.IAudioProcessor
import com.niusounds.simplepitchshifter.audio.SimplePitchShifterProcessor

class AudioProcessingService : Service() {
    private lateinit var audioEngine: AudioEngine
    private var audioProcessor: IAudioProcessor? = null

    private lateinit var receiver: BroadcastReceiver

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        val audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        audioEngine = AudioEngine(
            sampleRate = audioManager.getProperty(AudioManager.PROPERTY_OUTPUT_SAMPLE_RATE).toInt(),
            channels = 2,
        )

        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val key = intent.getStringExtra("key") ?: return
                val value = intent.extras?.get("value") ?: return
                audioProcessor?.configure(key, value)
            }
        }
        registerReceiver(receiver, IntentFilter(configureAction))
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        audioProcessor = SimplePitchShifterProcessor()
        audioEngine.start(processor = audioProcessor!!)
        return START_STICKY
    }

    override fun onDestroy() {
        unregisterReceiver(receiver)

        audioEngine.release()
        audioProcessor = null
        super.onDestroy()
    }

    companion object {
        private const val configureAction = "com.niusounds.simplepitchshifter.configure"

        fun start(context: Context) {
            context.startService(Intent(context, AudioProcessingService::class.java))
        }

        fun stop(context: Context) {
            context.stopService(Intent(context, AudioProcessingService::class.java))
        }

        fun configure(context: Context, key: String, value: Any) {
            context.sendBroadcast(
                Intent(configureAction).putExtras(
                    bundleOf(
                        "key" to key,
                        "value" to value
                    )
                )
            )
        }

        /**
         * [start]を呼ぶのに適切なパーミッションが付与されているかどうかを返す。
         */
        fun hasPermission(context: Context): Boolean = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }
}
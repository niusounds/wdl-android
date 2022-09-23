package com.niusounds.wdlsample

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.AudioTrack

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

@SuppressLint("MissingPermission")
fun audioRecord(block: AudioRecord.Builder.() -> Unit): AudioRecord = AudioRecord.Builder()
    .apply(block)
    .build()

fun audioTrack(block: AudioTrack.Builder.() -> Unit): AudioTrack = AudioTrack.Builder()
    .apply(block)
    .build()

fun audioFormat(block: AudioFormat.Builder.() -> Unit): AudioFormat = AudioFormat.Builder()
    .apply(block)
    .build()


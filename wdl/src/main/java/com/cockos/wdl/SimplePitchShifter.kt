/*
    Copyright (C) 2020 and later, Yuya Matsuo https://github.com/niusounds

    This software is provided 'as-is', without any express or implied
    warranty.  In no event will the authors be held liable for any damages
    arising from the use of this software.

    Permission is granted to anyone to use this software for any purpose,
    including commercial applications, and to alter it and redistribute it
    freely, subject to the following restrictions:

    1. The origin of this software must not be misrepresented; you must not
       claim that you wrote the original software. If you use this software
       in a product, an acknowledgment in the product documentation would be
       appreciated but is not required.
    2. Altered source versions must be plainly marked as such, and must not be
       misrepresented as being the original software.
    3. This notice may not be removed or altered from any source distribution.
*/
package com.cockos.wdl

import java.nio.ByteBuffer

class SimplePitchShifter(private val bufferSize: Int) {
    private var nativePtr: Long
    private var bufferPointer: Long = 0

    init {
        nativePtr = nativeInit()
    }

    fun release() {
        if (nativePtr != 0L) {
            nativeRelease(nativePtr)
            nativePtr = 0
        }
    }

    fun prepare() {
        bufferPointer = nativeGetBufferPointer(nativePtr, bufferSize)
    }

    fun reset() = nativeReset(nativePtr)
    fun isReset() = nativeIsReset(nativePtr)
    fun setSrate(srate: Double) = nativeSetSrate(nativePtr, srate)
    fun setNch(nch: Int) = nativeSetNch(nativePtr, nch)
    fun setShift(shift: Double) = nativeSetShift(nativePtr, shift)
    fun setTempo(tempo: Double) = nativeSetTempo(nativePtr, tempo)
    fun setFormantShift(shift: Double) = nativeSetFormantShift(nativePtr, shift)
    fun setQualityParameter(quality: Int) = nativeSetQualityParameter(nativePtr, quality)

    fun write(buffer: FloatArray, size: Int) {
        if (bufferPointer == 0L) throw IllegalStateException("prepare first")
        nativeWriteToBuffer(bufferPointer, buffer, size)
    }

    fun write(buffer: ByteBuffer, size: Int) {
        if (bufferPointer == 0L) throw IllegalStateException("prepare first")
        nativeWriteToBufferBB(bufferPointer, buffer, size)
    }

    fun bufferDone(inputFilled: Int) = nativeBufferDone(nativePtr, inputFilled)
    fun getSamples(buffer: FloatArray, size: Int) = nativeGetSamples(nativePtr, buffer, size)
    fun getSamples(buffer: ByteBuffer, size: Int) = nativeGetSamplesBB(nativePtr, buffer, size)

    private external fun nativeInit(): Long
    private external fun nativeRelease(ptr: Long)
    private external fun nativeReset(ptr: Long)
    private external fun nativeIsReset(ptr: Long): Boolean
    private external fun nativeSetSrate(ptr: Long, srate: Double)
    private external fun nativeSetNch(ptr: Long, nch: Int)
    private external fun nativeSetShift(ptr: Long, shift: Double)
    private external fun nativeSetTempo(ptr: Long, tempo: Double)
    private external fun nativeSetFormantShift(ptr: Long, shift: Double)
    private external fun nativeSetQualityParameter(ptr: Long, quality: Int)
    private external fun nativeWriteToBuffer(bufferPointer: Long, buffer: FloatArray, size: Int)
    private external fun nativeWriteToBufferBB(bufferPointer: Long, buffer: ByteBuffer, size: Int)
    private external fun nativeBufferDone(ptr: Long, inputFilled: Int)
    private external fun nativeGetBufferPointer(ptr: Long, size: Int): Long
    private external fun nativeGetSamples(ptr: Long, buffer: FloatArray, size: Int): Int
    private external fun nativeGetSamplesBB(ptr: Long, buffer: ByteBuffer, size: Int): Int

    companion object {
        init {
            System.loadLibrary("wdl")
        }

        /**
         * Get description for qualities.
         * Pass index to [setQualityParameter] later.
         */
        fun describeQualities(): List<String> {
            val qualities = mutableListOf<String>()
            var i = 0
            while (true) {
                qualities.add(nativeEnumQual(i++) ?: break)
            }
            return qualities
        }

        @JvmStatic
        private external fun nativeEnumQual(q: Int): String?
    }
}
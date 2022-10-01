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

class SineWaveGenerator {
    private var nativePtr: Long

    init {
        nativePtr = nativeInit()
    }

    /**
     * Release native resources.
     */
    fun release() {
        if (nativePtr != 0L) {
            nativeRelease(nativePtr)
            nativePtr = 0
        }
    }

    /**
     * Reset internal state.
     */
    fun reset() = nativeReset(nativePtr)

    /**
     * Call this before calling [gen], or on freq change, or after a [reset].
     * [freq] is `frequency / samplerate`. (so 0..1 is valid, though over 0.3 is probably not a good idea)
     */
    fun setFreq(freq: Double) = nativeSetFreq(nativePtr, freq)

    /**
     * Get next sine sample.
     */
    fun gen() = nativeGen(nativePtr)

    /**
     * call BEFORE [gen] if you want the cosine of the next value
     */
    fun getNextCos() = nativeGetNextCos(nativePtr)

    private external fun nativeInit(): Long
    private external fun nativeRelease(ptr: Long)
    private external fun nativeReset(ptr: Long)
    private external fun nativeSetFreq(ptr: Long, freq: Double)
    private external fun nativeGen(ptr: Long): Double
    private external fun nativeGetNextCos(ptr: Long): Double

    companion object {
        init {
            System.loadLibrary("wdl")
        }
    }
}

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

class Resampler {
    private var nativePtr: Long

    init {
        nativePtr = nativeInit()
    }

    fun release() {
        if (nativePtr != 0L) {
            nativeRelease(nativePtr)
            nativePtr = 0
        }
    }

    fun setMode(
        interp: Boolean,
        filtercnt: Int,
        sinc: Boolean,
        sincSize: Int = 64,
        sincInterpsize: Int = 32,
    ) = nativeSetMode(nativePtr, interp, filtercnt, sinc, sincSize, sincInterpsize)

    fun setFilterParms(
        filterpos: Float = 0.693f,
        filterq: Float = 0.707f,
    ) = nativeSetFilterParms(nativePtr, filterpos, filterq)

    fun setFeedMode(wantInputDriven: Boolean) = nativeSetFeedMode(nativePtr, wantInputDriven)
    fun reset(fracpos: Double = 0.0) = nativeReset(nativePtr, fracpos)
    fun setRates(rateIn: Double, rateOut: Double) = nativeSetRates(nativePtr, rateIn, rateOut)
    fun getCurrentLatency() = nativeGetCurrentLatency(nativePtr)
    fun resamplePrepare(reqSamples: Int, nch: Int, buffer: ByteBuffer) =
        nativeResamplePrepare(nativePtr, reqSamples, nch, buffer)

    fun resamplePrepareFloatArray(reqSamples: Int, nch: Int, buffer: FloatArray) =
        nativeResamplePrepareFloatArray(nativePtr, reqSamples, nch, buffer)

    fun resampleOut(
        out: ByteBuffer,
        nsamplesIn: Int,
        nsamplesOut: Int,
        nch: Int
    ) = nativeResampleOut(nativePtr, out, nsamplesIn, nsamplesOut, nch)

    private external fun nativeInit(): Long
    private external fun nativeRelease(ptr: Long)

    private external fun nativeSetMode(
        ptr: Long, interp: Boolean,
        filtercnt: Int,
        sinc: Boolean,
        sincSize: Int,
        sincInterpsize: Int,
    )

    private external fun nativeSetFilterParms(ptr: Long, filterpos: Float, filterq: Float)
    private external fun nativeSetFeedMode(ptr: Long, wantInputDriven: Boolean)
    private external fun nativeReset(ptr: Long, fracpos: Double)
    private external fun nativeSetRates(ptr: Long, rateIn: Double, rateOut: Double)
    private external fun nativeGetCurrentLatency(ptr: Long): Double
    private external fun nativeResamplePrepare(
        ptr: Long,
        reqSamples: Int,
        nch: Int,
        buffer: ByteBuffer,
    ): Int

    private external fun nativeResamplePrepareFloatArray(
        ptr: Long,
        reqSamples: Int,
        nch: Int,
        buffer: FloatArray,
    ): Int

    private external fun nativeResampleOut(
        ptr: Long,
        out: ByteBuffer,
        nsamplesIn: Int,
        nsamplesOut: Int,
        nch: Int,
    ): Int

    companion object {
        init {
            System.loadLibrary("wdl")
        }
    }
}
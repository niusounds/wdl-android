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

#include <jni.h>

#define WDL_RESAMPLE_TYPE float

#include "resample.h"

extern "C" {

JNIEXPORT jlong JNICALL
Java_com_cockos_wdl_Resampler_nativeInit(JNIEnv *env, jobject thiz) {
    return reinterpret_cast<jlong>(new WDL_Resampler());
}

JNIEXPORT void JNICALL
Java_com_cockos_wdl_Resampler_nativeRelease(JNIEnv *env, jobject thiz, jlong ptr) {
    delete reinterpret_cast<WDL_Resampler *>(ptr);
}

JNIEXPORT void JNICALL
Java_com_cockos_wdl_Resampler_nativeSetMode(JNIEnv *env, jobject thiz, jlong ptr, jboolean interp,
                                            jint filtercnt, jboolean sinc, jint sincSize,
                                            jint sincInterpsize) {
    auto resampler = reinterpret_cast<WDL_Resampler *>(ptr);
    resampler->SetMode(interp, filtercnt, sinc, sincSize, sincInterpsize);
}

JNIEXPORT void JNICALL
Java_com_cockos_wdl_Resampler_nativeSetFilterParms(JNIEnv *env, jobject thiz, jlong ptr,
                                                   jfloat filterpos, jfloat filterq) {
    auto resampler = reinterpret_cast<WDL_Resampler *>(ptr);
    resampler->SetFilterParms(filterpos, filterq);
}

JNIEXPORT void JNICALL
Java_com_cockos_wdl_Resampler_nativeSetFeedMode(JNIEnv *env, jobject thiz, jlong ptr,
                                                jboolean wantInputDriven) {
    auto resampler = reinterpret_cast<WDL_Resampler *>(ptr);
    resampler->SetFeedMode(wantInputDriven);
}

JNIEXPORT void JNICALL
Java_com_cockos_wdl_Resampler_nativeReset(JNIEnv *env, jobject thiz, jlong ptr, jdouble fracpos) {
    auto resampler = reinterpret_cast<WDL_Resampler *>(ptr);
    resampler->Reset(fracpos);
}

JNIEXPORT void JNICALL
Java_com_cockos_wdl_Resampler_nativeSetRates(JNIEnv *env, jobject thiz, jlong ptr, jdouble rateIn,
                                             jdouble rateOut) {
    auto resampler = reinterpret_cast<WDL_Resampler *>(ptr);
    resampler->SetRates(rateIn, rateOut);
}

JNIEXPORT jdouble JNICALL
Java_com_cockos_wdl_Resampler_nativeGetCurrentLatency(JNIEnv *env, jobject thiz, jlong ptr) {
    auto resampler = reinterpret_cast<WDL_Resampler *>(ptr);
    return resampler->GetCurrentLatency();
}

JNIEXPORT jint JNICALL
Java_com_cockos_wdl_Resampler_nativeResamplePrepare(JNIEnv *env, jobject thiz, jlong ptr,
                                                    jint reqSamples, jint nch, jobject buffer) {
    auto resampler = reinterpret_cast<WDL_Resampler *>(ptr);
    auto bufferPtr = reinterpret_cast<jfloat *>(env->GetDirectBufferAddress(buffer));
    float *inbuffers;
    int count = resampler->ResamplePrepare(reqSamples, nch, &inbuffers);
    for (int i = 0; i < count; ++i) {
        inbuffers[i] = bufferPtr[i];
    }
    return count;
}

JNIEXPORT jint JNICALL
Java_com_cockos_wdl_Resampler_nativeResamplePrepareFloatArray(JNIEnv *env, jobject thiz, jlong ptr,
                                                              jint reqSamples, jint nch,
                                                              jfloatArray buffer) {
    auto resampler = reinterpret_cast<WDL_Resampler *>(ptr);
    auto bufferPtr = env->GetFloatArrayElements(buffer, nullptr);
    float *inbuffers;
    int count = resampler->ResamplePrepare(reqSamples, nch, &inbuffers);
    for (int i = 0; i < count; ++i) {
        inbuffers[i] = bufferPtr[i];
    }
    env->ReleaseFloatArrayElements(buffer, bufferPtr, 0);
    return count;
}

JNIEXPORT jint JNICALL
Java_com_cockos_wdl_Resampler_nativeResampleOut(JNIEnv *env, jobject thiz, jlong ptr, jobject out,
                                                jint nsamplesIn, jint nsamplesOut, jint nch) {
    auto resampler = reinterpret_cast<WDL_Resampler *>(ptr);
    auto outPtr = reinterpret_cast<jfloat *>(env->GetDirectBufferAddress(out));
    return resampler->ResampleOut(outPtr, nsamplesIn, nsamplesOut, nch);
}

}

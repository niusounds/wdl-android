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
#include <cstring> // for memcpy

#define WDL_SIMPLEPITCHSHIFT_SAMPLETYPE float

#include "simple_pitchshift.h"

extern "C" {
JNIEXPORT jlong JNICALL
Java_com_cockos_wdl_SimplePitchShifter_nativeInit(JNIEnv *env, jobject thiz) {
    return reinterpret_cast<jlong>(new WDL_SimplePitchShifter());
}

JNIEXPORT void JNICALL
Java_com_cockos_wdl_SimplePitchShifter_nativeRelease(JNIEnv *env, jobject thiz, jlong ptr) {
    delete reinterpret_cast<WDL_SimplePitchShifter *>(ptr);
}

JNIEXPORT void JNICALL
Java_com_cockos_wdl_SimplePitchShifter_nativeReset(JNIEnv *env, jobject thiz, jlong ptr) {
    auto shifter = reinterpret_cast<WDL_SimplePitchShifter *>(ptr);
    shifter->Reset();
}

JNIEXPORT jboolean JNICALL
Java_com_cockos_wdl_SimplePitchShifter_nativeIsReset(JNIEnv *env, jobject thiz, jlong ptr) {
    auto shifter = reinterpret_cast<WDL_SimplePitchShifter *>(ptr);
    return shifter->IsReset();
}

JNIEXPORT void JNICALL
Java_com_cockos_wdl_SimplePitchShifter_nativeSetSrate(JNIEnv *env, jobject thiz, jlong ptr,
                                                      double srate) {
    auto shifter = reinterpret_cast<WDL_SimplePitchShifter *>(ptr);
    shifter->set_srate(srate);
}

JNIEXPORT void JNICALL
Java_com_cockos_wdl_SimplePitchShifter_nativeSetNch(JNIEnv *env, jobject thiz, jlong ptr,
                                                    jint nch) {
    auto shifter = reinterpret_cast<WDL_SimplePitchShifter *>(ptr);
    shifter->set_nch(nch);
}

JNIEXPORT void JNICALL
Java_com_cockos_wdl_SimplePitchShifter_nativeSetShift(JNIEnv *env, jobject thiz, jlong ptr,
                                                      jdouble shift) {
    auto shifter = reinterpret_cast<WDL_SimplePitchShifter *>(ptr);
    shifter->set_shift(shift);
}

JNIEXPORT void JNICALL
Java_com_cockos_wdl_SimplePitchShifter_nativeSetTempo(JNIEnv *env, jobject thiz, jlong ptr,
                                                      jdouble tempo) {
    auto shifter = reinterpret_cast<WDL_SimplePitchShifter *>(ptr);
    shifter->set_tempo(tempo);
}

JNIEXPORT void JNICALL
Java_com_cockos_wdl_SimplePitchShifter_nativeSetFormantShift(JNIEnv *env, jobject thiz, jlong ptr,
                                                             jdouble shift) {
    auto shifter = reinterpret_cast<WDL_SimplePitchShifter *>(ptr);
    shifter->set_formant_shift(shift);
}

JNIEXPORT void JNICALL
Java_com_cockos_wdl_SimplePitchShifter_nativeSetQualityParameter(JNIEnv *env, jobject thiz,
                                                                 jlong ptr,
                                                                 jint quality) {
    auto shifter = reinterpret_cast<WDL_SimplePitchShifter *>(ptr);
    shifter->SetQualityParameter(quality);
}

JNIEXPORT void JNICALL
Java_com_cockos_wdl_SimplePitchShifter_nativeWriteToBuffer(JNIEnv *env, jobject thiz, jlong bufPtr,
                                                           jfloatArray buffer, jint size) {
    auto shifterBuf = reinterpret_cast<float *>(bufPtr);
    auto javaBufPtr = env->GetFloatArrayElements(buffer, nullptr);
    for (int i = 0; i < size; ++i) {
        shifterBuf[i] = javaBufPtr[i];
    }
    env->ReleaseFloatArrayElements(buffer, javaBufPtr, 0);
}

JNIEXPORT void JNICALL
Java_com_cockos_wdl_SimplePitchShifter_nativeWriteToBufferBB(JNIEnv *env, jobject thiz,
                                                             jlong bufPtr,
                                                             jobject buffer, jint size) {
    auto shifterBuf = reinterpret_cast<float *>(bufPtr);
    auto javaBufPtr = reinterpret_cast<float *>(env->GetDirectBufferAddress(buffer));
    for (int i = 0; i < size; ++i) {
        shifterBuf[i] = javaBufPtr[i];
    }
}

JNIEXPORT void JNICALL
Java_com_cockos_wdl_SimplePitchShifter_nativeBufferDone(JNIEnv *env, jobject thiz, jlong ptr,
                                                        jint inputFilled) {
    auto shifter = reinterpret_cast<WDL_SimplePitchShifter *>(ptr);
    shifter->BufferDone(inputFilled);
}

JNIEXPORT jlong JNICALL
Java_com_cockos_wdl_SimplePitchShifter_nativeGetBufferPointer(JNIEnv *env, jobject thiz, jlong ptr,
                                                              jint size) {
    auto shifter = reinterpret_cast<WDL_SimplePitchShifter *>(ptr);
    return reinterpret_cast<jlong>(shifter->GetBuffer(size));
}

JNIEXPORT jint JNICALL
Java_com_cockos_wdl_SimplePitchShifter_nativeGetSamples(JNIEnv *env, jobject thiz, jlong ptr,
                                                        jfloatArray buffer, jint size) {
    auto shifter = reinterpret_cast<WDL_SimplePitchShifter *>(ptr);
    auto bufferPtr = env->GetFloatArrayElements(buffer, nullptr);
    auto sampleSize = shifter->GetSamples(size, bufferPtr);
    env->ReleaseFloatArrayElements(buffer, bufferPtr, 0);
    return sampleSize;
}

JNIEXPORT jint JNICALL
Java_com_cockos_wdl_SimplePitchShifter_nativeGetSamplesBB(JNIEnv *env, jobject thiz, jlong ptr,
                                                          jobject buffer, jint size) {
    auto shifter = reinterpret_cast<WDL_SimplePitchShifter *>(ptr);
    auto bufferPtr = reinterpret_cast<float *>(env->GetDirectBufferAddress(buffer));
    return shifter->GetSamples(size, bufferPtr);
}

JNIEXPORT jstring JNICALL
Java_com_cockos_wdl_SimplePitchShifter_nativeEnumQual(JNIEnv *env, jclass thiz, jint q) {
    auto result = WDL_SimplePitchShifter::enumQual(q);
    return env->NewStringUTF(result);
}

}

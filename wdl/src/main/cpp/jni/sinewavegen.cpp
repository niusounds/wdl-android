/*
    Copyright (C) 2022 and later, Yuya Matsuo https://github.com/niusounds

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

#include "sinewavegen.h"

extern "C" {
JNIEXPORT jlong JNICALL
Java_com_cockos_wdl_SineWaveGenerator_nativeInit(JNIEnv *env, jobject thiz) {
    return reinterpret_cast<jlong>(new WDL_SineWaveGenerator());
}

JNIEXPORT void JNICALL
Java_com_cockos_wdl_SineWaveGenerator_nativeRelease(JNIEnv *env, jobject thiz, jlong ptr) {
    delete reinterpret_cast<WDL_SineWaveGenerator *>(ptr);
}

JNIEXPORT void JNICALL
Java_com_cockos_wdl_SineWaveGenerator_nativeReset(JNIEnv *env, jobject thiz, jlong ptr) {
    auto shifter = reinterpret_cast<WDL_SineWaveGenerator *>(ptr);
    shifter->Reset();
}

JNIEXPORT void JNICALL
Java_com_cockos_wdl_SineWaveGenerator_nativeSetFreq(JNIEnv *env, jobject thiz, jlong ptr,
                                                    double srate) {
    auto shifter = reinterpret_cast<WDL_SineWaveGenerator *>(ptr);
    shifter->SetFreq(srate);
}

JNIEXPORT jdouble JNICALL
Java_com_cockos_wdl_SineWaveGenerator_nativeGen(JNIEnv *env, jobject thiz, jlong ptr) {
    auto shifter = reinterpret_cast<WDL_SineWaveGenerator *>(ptr);
    return shifter->Gen();
}

JNIEXPORT jdouble JNICALL
Java_com_cockos_wdl_SineWaveGenerator_nativeGetNextCos(JNIEnv *env, jobject thiz, jlong ptr) {
    auto shifter = reinterpret_cast<WDL_SineWaveGenerator *>(ptr);
    return shifter->GetNextCos();
}

}

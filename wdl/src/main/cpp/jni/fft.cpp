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
#include "fft.h"

extern "C" {
JNIEXPORT void JNICALL
Java_com_cockos_wdl_FFT_nativeInit(JNIEnv *env, jobject thiz) {
    WDL_fft_init();
}
JNIEXPORT void JNICALL
Java_com_cockos_wdl_FFT_realFft(JNIEnv *env, jobject thiz,
                                jfloatArray data,
                                jint len,
                                jboolean isInverse) {
    float *dataPtr = env->GetFloatArrayElements(data, nullptr);
    WDL_real_fft(dataPtr, len, isInverse ? 1 : 0);
    env->ReleaseFloatArrayElements(data, dataPtr, 0);
}
}
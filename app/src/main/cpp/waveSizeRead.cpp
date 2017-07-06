//
// Created by 西澤佳祐 on 2017/03/06.
//

#include "waveSizeRead.h"
#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include "../jni/wave.h"
JNIEXPORT jint JNICALL Java_keisukenishizawa_melodymemo_MainActivity_waveSizeRead(
        JNIEnv *env,
        jobject, /* this */
        //char *waveFile){
        jstring waveFile){
    MONO_PCM pcm0;
    //mono_wave_read(&pcm0, waveFile);
    mono_wave_read(&pcm0,const_cast<char*>(env->GetStringUTFChars(waveFile,0)));
        return static_cast<jint>(pcm0.length);
};
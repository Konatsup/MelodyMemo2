//
// Created by 西澤佳祐 on 2017/05/22.
//

#include "test.h"
#include "jni.h"
extern "C"
JNIEXPORT
jint JNICALL Java_keisukenishizawa_melodymemo_MainActivity_incrementValue(JNIEnv* env, jobject thiz, jint a, jint b){
    //return value + 1;
    int c = a+ b;
    return c;
}
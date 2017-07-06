//
// Created by 西澤佳祐 on 2017/06/02.
//
#include <jni.h>
#include <string>
#include "testC.h"

extern "C"
jstring
Java_keisukenishizawa_melodymemo_MainActivity_testC(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello2 = "AAAAABBBBBB";
    std::string hello3 = "sadlfkls";
    return env->NewStringUTF(hello3.c_str());
}

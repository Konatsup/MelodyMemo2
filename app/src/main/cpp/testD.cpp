//
// Created by 西澤佳祐 on 2017/06/08.
//

#include <jni.h>
#include <string>
//#include "../../../../../../../../../Library/Android/sdk/ndk-bundle/sources/cxx-stl/gnu-libstdc++/4.9/include/string"

extern "C"
jstring
Java_keisukenishizawa_melodymemo_MainActivity_testDD(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello2 = "AAAAABBBBBB";
    std::string hello3 = "sadlfkls";
    return env->NewStringUTF(hello3.c_str());
}

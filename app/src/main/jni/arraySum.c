//
// Created by 西澤佳祐 on 2017/06/02.
//

/*
jint Java_keisukenishizawa_melodymemo_MainActivity_Test(JNIEnv* env, jobject thiz){
    //return value + 1;
    return 1;
}*/


#include "jni.h"

void Java_keisukenishizawa_melodymemo_MainActivity_arraySum(JNIEnv* env,
                                                            jobject thiz,
                                                            jintArray arr){

    jint i;
    jint sum = 0;

    jsize size = (*env)->GetArrayLength(env,arr);

    jint *buf = (*env)->GetIntArrayElements(env, arr, 0);

    for(i = 0; i < size; i++){
        sum += buf[i];
        buf[i] +=1;
    }

    //メモリの解放
    (*env)->ReleaseIntArrayElements(env, arr, buf, JNI_ABORT);

}
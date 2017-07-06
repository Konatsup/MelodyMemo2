//
// Created by 西澤佳祐 on 2017/04/06.
//
#include "jni.h"
//extern "C"
//JNIEXPORT

//jint JNICALL
/*
jint Java_keisukenishizawa_melodymemo_MainActivity_addValues(JNIEnv* env, jobject thiz, jint value1, jint value2){
    //return value + 1;
    return value1 + value2;
}*/


jint Java_keisukenishizawa_melodymemo_MainActivity_Sum(JNIEnv* env, jobject thiz, jint value1, jint value2){
    //return value + 1;
    value1 = 20000;
    return value1;
}

/*
jint Java_keisukenishizawa_melodymemo_MainActivity_Sum(JNIEnv* env, jobject thiz){
    jint a = 1;

    return a;
}*/

/*

jint Java_keisukenishizawa_melodymemo_MainActivity_arraySum(JNIEnv* env, jobject thiz, jintArray arr){

    jint i, sum = 0;

    jsize size = (*env)->GetArrayLength(env, arr);

    jint *buf = (*env)->GetIntArrayElements(env, arr, 0);

    for(i=0; i < size; i++) {
        sum += buf[i];

        buf[i] += i;

    }

    (*env)->ReleaseIntArrayElements(env, arr, buf, JNI_ABORT);

    return 1;
}

*/
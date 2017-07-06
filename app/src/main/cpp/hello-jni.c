//
// Created by 西澤佳祐 on 2017/03/08.
//

#include "hello-jni.h"
#include <string.h>
#include <stdbool.h>
#include <jni.h>
#include <malloc.h>
#include <android/log.h>
#include ""


JavaVM *g_jvm;
jint JNI_OnLoad( JavaVM* vm, void* reserved )
{
    g_jvm = vm;
    return JNI_VERSION_1_6;
}

JNIEnv* getJNIEnv( void )
{
    JNIEnv* env;
    jint ret;

    ret = (*g_jvm)->GetEnv( g_jvm, (void**)&env, JNI_VERSION_1_6);
    if( ret != JNI_OK )
        return NULL;
    return env;
}

void c_to_java(const char* str );

JNIEXPORT void JNICALL Java_com_example_hellojni_CJni_java_1to_1c(JNIEnv *env, jclass thiz, jstring s)
{
    const char *str = (*env)->GetStringUTFChars(env, s, 0);
    __android_log_print(ANDROID_LOG_INFO,"HelloJni","c:%s",str);
    (*env)->ReleaseStringUTFChars(env, s, str);

    // c_to_java
    c_to_java("HelloJni - c_to_java");
}

void c_to_java(const char* str )
{
    __android_log_print(ANDROID_LOG_INFO,"HelloJni","%s",str);

    JNIEnv* env = getJNIEnv();
    // java
    jclass cls = (*env)->FindClass(env,"com/example/hellojni/HelloJni");
    jmethodID mid = (*env)->GetMethodID(env,cls,"<init>","()V");
    jobject obj = (*env)->NewObject(env,cls,mid);

    // Signature: ()V
    jmethodID mid1 = (*env)->GetMethodID(env,cls,"c_to_java1","()V");
    (*env)->CallVoidMethod(env,obj,mid1);

    // Signature: (I)I
    jmethodID mid2 = (*env)->GetMethodID(env,cls,"c_to_java2","(I)I");
    jint n = (*env)->CallIntMethod(env,obj,mid2,1);
    __android_log_print(ANDROID_LOG_INFO,"HelloJni","%d",n);

    // Signature: (Ljava/lang/String;)Ljava/lang/String;
    jmethodID mid3 = (*env)->GetMethodID(env,cls,"c_to_java3","(Ljava/lang/String;)Ljava/lang/String;");
    jstring s = (*env)->CallObjectMethod(env,obj,mid3,(*env)->NewStringUTF(env,str));
    const char* c = (*env)->GetStringUTFChars(env,s,0);
    __android_log_print(ANDROID_LOG_INFO,"HelloJni","%s",c);
    (*env)->ReleaseStringUTFChars(env,s,c);

    // Signature: ([B)[B
    jmethodID mid4 = (*env)->GetMethodID(env,cls,"c_to_java4","([B)[B");
    jbyte by[3];
    int len1 = sizeof( by ) / sizeof( jbyte );
    jbyteArray bary1 = (*env)->NewByteArray(env,len1);

    jbyte *elems1 = (*env)->GetByteArrayElements(env,bary1,NULL);
    int i;
    for( i = 0; i < len1; i++ )
        elems1[i] = (jbyte)i;
    (*env)->SetByteArrayRegion(env,bary1,0,len1,elems1);

    jbyteArray bary2 = (*env)->CallObjectMethod(env,obj,mid4,bary1);
    int len2 = (*env)->GetArrayLength(env,bary2);
    jbyte *elems2 = (*env)->GetByteArrayElements(env,bary2,NULL);
    for( i = 0; i < len2; i++ )
        __android_log_print(ANDROID_LOG_INFO,"HelloJni","%d",elems2[i]);

    (*env)->ReleaseByteArrayElements(env,bary2,elems2,0);
    (*env)->ReleaseByteArrayElements(env,bary1,elems1,0);

    //
    (*env)->DeleteLocalRef(env,obj);
    (*env)->DeleteLocalRef(env,cls);
}
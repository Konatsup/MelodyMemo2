//
// Created by 西澤佳祐 on 2017/03/07.
//

//
// Created by 西澤佳祐 on 2017/03/06.
//
#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <math.h>
#include <android/log.h>
#include <android/log.h>
#include "wave.h"
#include "fft.h"
#include "window_function.h"
#define PAI 3.14159265358979323846264338 // PAI
#define SAMPLE_RATE 44100 //sampling rate
#define FREQUENCY 440 // frequency Hz
#define FFTSIZE 2048
#define THRESHOLD 0.50
#define BPM 120
//#define LOG_TAG "tagname"
//#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
//#define LOGI(...) __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

//extern "C"

jint Java_keisukenishizawa_melodymemo_MainActivity_pitch(
        JNIEnv *env,
        jobject thiz,
        jdoubleArray arr,
        jstring fileName) {

    jint i;
    const char *nativeString = (*env)->GetStringUTFChars(env, fileName, JNI_FALSE);
    MONO_PCM pcm0;

    int n, k, N;
    double *x_real, *x_imag, *w, *powerspec, *rt, *autocorrelation, *NSDF, *peakBin, *pitch_;
   // double *pcm_d;
    double *sample;
    int maxdB = 0;
    int maxN = 0;
    double maxBin = 0.0;
    int peakCnt = 0;
    int *peakTime;
    int overlap = 0;
    int incr = SAMPLE_RATE/960;
    //int incr = 10;
    int dataSize = 30000;
    long a = 1;
    int pitchDataSize = 0;
    char* c;

    jsize size = (*env)->GetArrayLength(env,arr);
    jdouble *buf = (*env)->GetDoubleArrayElements(env, arr, 0);

    //__android_log_print(ANDROID_LOG_ERROR, "log", "not performed");
  // __android_log_print(ANDROID_LOG_ERROR,"LOG","aaaaa");
    // int frame_num = SAMPLE_RATE * FFTSIZE;
   // mono_wave_read(&pcm0, ); /* WAVE?t?@?C?????烂?m?????̉??f?[?^???͂???*/

/*    xr = (*env)->GetDoubleArrayElements(env, xreal, 0);
    xi = (*env)->GetDoubleArrayElements(env, ximag, 0);
    yr = (*env)->GetDoubleArrayElements(env, yreal, 0);
    yi = (*env)->GetDoubleArrayElements(env, yimag, 0);
    or = (*env)->GetDoubleArrayElements(env, outreal, 0);
    oi = (*env)->GetDoubleArrayElements(env, outimag, 0);

*/
//      int frame_num = SAMPLE_RATE*FFTSIZE;
    //__android_log_print(ANDROID_LOG_ERROR, "log", "%s",fileName);
    //  mono_wave_read2(&pcm0, nativeString);
    mono_wave_read(&pcm0,nativeString);

      N = FFTSIZE;
      pitch_ = calloc(pcm0.length+N,sizeof(double));
      peakTime = calloc(N, sizeof(int));
      x_real = calloc(N, sizeof(double));
      x_imag = calloc(N, sizeof(double));
      powerspec = calloc(N, sizeof(double));
      rt = calloc(N, sizeof(double));
      autocorrelation = calloc(N, sizeof(double));
      NSDF = calloc(N, sizeof(double));
      peakBin = calloc(N,sizeof(double));
      w = calloc(N, sizeof(double));
//      pcm_d = calloc(dataSize + 5000, sizeof(double));
      sample = calloc(N, sizeof(double));
      c = calloc(N,sizeof(char));




     // pcm_d = mono_wave_read3(nativeString);


/*
    N = FFTSIZE;
    pitch_ = calloc(pcm0.length+10,sizeof(double));
    peakTime = calloc(N, sizeof(int));
    x_real = calloc(N, sizeof(double));
    x_imag = calloc(N, sizeof(double));
    powerspec = calloc(N, sizeof(double));
    rt = calloc(N, sizeof(double));
    autocorrelation = calloc(N, sizeof(double));
    NSDF = calloc(N, sizeof(double));
    peakBin = calloc(N,sizeof(double));
    w = calloc(N, sizeof(double));


*/
    Hanning_window(w, N);
      for(overlap =0;overlap<pcm0.length;overlap=overlap + incr) {
         // for(overlap =0;overlap < dataSize ;overlap=overlap + incr) {
         //     for(overlap =0;overlap < incr*3 ;overlap=overlap + incr) {
          for (n = 0; n < N; n++){

              x_real[n] = pcm0.s[n + overlap] * w[n];
              ///x_real[n] = pcm_d[n + overlap] * w[n];
             // x_real[n] = pcm_d[n + overlap];
              x_imag[n] = 0.0;
          }


        FFT(x_real, x_imag, FFTSIZE);

        for (n = 0; n < FFTSIZE/2; n++){
          powerspec[n] = sqrt(x_real[n]*x_real[n] + x_imag[n]*x_imag[n]);
        }


        for (n = 0; n < FFTSIZE/2; n++){
          x_real[n] = powerspec[n];
          x_imag[n] = 0.0;
        }

        IFFT(x_real, x_imag, FFTSIZE);


      for (n = 0; n < FFTSIZE/2; n++){
          rt[n] = x_real[0]*x_real[n];
        }


       for (n = 0; n < FFTSIZE/2; n++){
          autocorrelation[n] = x_real[0]*x_real[0] + x_real[n]*x_real[n];
        }

        for(n=0;n<FFTSIZE/2;n++){
          if(autocorrelation[n]){
            NSDF[n] = 2*rt[n]/autocorrelation[n];
          }else{
            NSDF[n]= 0.0;
          }
        }

      for(n=0;n<FFTSIZE/2;n++){
        if(NSDF[n]>=0.0){
          if(NSDF[n]>peakBin[peakCnt]){
            peakBin[peakCnt] = NSDF[n];
            peakTime[peakCnt] = n;
          }
        }else if(NSDF[n-1]>=0.0){
        peakCnt++;
        }
      }

      for(k = 1;k<peakCnt;k++){
        if(peakBin[k]>THRESHOLD){
          break;
        }
      }

      //ここで余裕があれば補間処理を書く

    //ハイCがC5
    //人間の最高音であるF6が1400Hz
      //人間の最低音であるC2が65Hz

     if(peakTime[k]!=0.0 && SAMPLE_RATE/peakTime[k] <1000.0 && SAMPLE_RATE/peakTime[k] > 65.0){
          pitch_[overlap/incr] = SAMPLE_RATE/peakTime[k];
      }else{
        pitch_[overlap/incr] = 0.0;//スレッショルドを超えるものがなかった時
      }

      for(n=0;n<FFTSIZE/2;n++){
        peakBin[n] = 0.0;
        peakTime[n] = 0;
    }

    peakCnt = 0;

    }


   /* for (int i = 0; i < N; i++) {
        sample[i] = (double)i/10.0;

    }
*/

    pitchDataSize = overlap/incr;

      for (int i = 0; i < pitchDataSize; i++) {

          // buf[i] = (jdouble)(overlap/incr);
         //  buf[i] = (jdouble)sample[i];
          buf[i] = (jdouble)pitch_[i];

          //buf[i] = (jdouble)peakTime[i];
//          buf[i] = (jdouble)peakTime[i];
          //buf[i] = (jdouble)NSDF[i];
        //  buf[i] = (jdouble)x_real[i];
         // buf[i] = (jdouble)autocorrelation[i];
        //  buf[i] = (jdouble)NSDF[i];
          ////buf[i] = (jdouble)a;
      }



    (*env)->ReleaseDoubleArrayElements(env, arr, buf, JNI_ABORT);

    free((void*)rt);
    free((void*)powerspec);
    free((void*)autocorrelation);
    free((void*)NSDF);
    free((void*)w);
    free((void*)pitch_);
    free((void*)peakTime);
    free((void*)peakBin);
    free((void*)x_real);
    free((void*)x_imag);
    free((void*)pcm0.s);

    return (jint)pitchDataSize;

}
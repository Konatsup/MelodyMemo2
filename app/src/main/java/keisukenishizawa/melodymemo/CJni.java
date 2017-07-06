/**
 * Created by KeisukeNishizawa on 2017/03/08.
 */

package keisukenishizawa.melodymemo;

public class CJni {

    public static native void java_to_c(String s);
    static {
        System.loadLibrary("hello-jni");
    }
}
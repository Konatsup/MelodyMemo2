package keisukenishizawa.melodymemo;

/**
 * Created by KeisukeNishizawa on 2017/03/08.
 */

import android.app.Activity;

//package com.example.hellojni;
//package keisukenishizawa.melodymemo.helloJni;

        import android.app.Activity;
        import android.util.Log;
        import android.widget.TextView;
        import android.os.Bundle;


public class HelloJni extends Activity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // C/C++
        String s = "HelloJni - java_to_c";
        Log.v("HelloJni", s);
        CJni.java_to_c(s);
    }

    public void c_to_java1()
    {
        Log.v("HelloJni", "java:");
    }
    public int c_to_java2(int n)
    {
        Log.v("HelloJni", "java:"+n);
        return n;
    }
    public String c_to_java3(String s)
    {
        Log.v("HelloJni", "java:"+s);
        return s;
    }
    public byte[] c_to_java4(byte[] b)
    {
        for( int i = 0; i < b.length; i++ )
            Log.v("HelloJni", "java:"+b[i]);
        return b;
    }
}
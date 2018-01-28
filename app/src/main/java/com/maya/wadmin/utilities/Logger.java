package com.maya.wadmin.utilities;

import android.util.Log;

/**
 * Created by Gokul Kalagara on 1/26/2018.
 */

public class Logger
{
    public static boolean logstatus = true;
    public static final String TAG = "Task";

    private Logger() {
    }

    public static void error(String s, Throwable throwable) {
        if (logstatus){
            Log.e(TAG, s, throwable);
        }

    }

    public static void e(String s) {
        if (logstatus){
            Log.e(TAG, "" + s);
        }
    }

    public static void e(String tag, String s) {
        if (logstatus){
            Log.e(tag, "" + s);
        }


    }

    public static void d(String s) {
        if (logstatus){
            Log.d(TAG, "" + s);
        }


    }

    public static void d(String tag, String s){
        if(logstatus){
            Log.d(tag, "" + s);
        }
    }

    public static void w(String s) {
        if (logstatus){
            Log.w(TAG, "" + s);
        }

    }

    public static void w(String tag, String s) {
        if (logstatus){
            Log.w(tag, "" + s);
        }

    }

    public static void i(String s) {
        if (logstatus){
            Log.i(TAG, "" + s);
        }


    }

    public static void i(String tag, String s) {
        if (logstatus){
            Log.w(tag, "" + s);
        }

    }


    public static void v(String s) {
        if (logstatus) {
            Log.v(TAG, "" + s);
        }
    }

    public static void v(String tag, String s) {
        if (logstatus) {
            Log.v(tag, "" + s);
        }
    }

    public static void e(String zup, String s, Throwable e) {
        if(logstatus){
            Log.e(zup,s,e);
        }
    }
}

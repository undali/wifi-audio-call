package com.example.tuhin.ccaudio;

import android.os.Build;

/**
 * Created by Tuhin on 2/28/2017.
 */

public class Utils {
    static String get_device_name(){
        return Build.MANUFACTURER + "_" + Build.MODEL;// + "_" + Build.VERSION.RELEASE;
    }

    static String generate_broadcast_message(boolean enable_end_signal){
        String msg = "";
        msg += enable_end_signal ? "1" : "0";
        return Config.unique_id + ":" + get_device_name() + ":" + msg;
    }

    static String get_unique_id(String msg){
        String[] m = msg.split(":");
        if(m.length > 1){
            //Mouse.log("get_unique_id unique id found:" + m[0]);
            return m[0];
        }
        Mouse.log("no unique id found. original message " + msg);
        return "";
    }

    static boolean verify_message(String msg){
        //Mouse.log("verify_message called with:" + msg);
        return get_unique_id(msg).equals(Config.unique_id);
    }
}

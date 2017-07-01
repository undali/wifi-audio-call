package com.example.tuhin.ccaudio;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Tuhin on 11/22/2016.
 */

public class Mouse {
    Mouse(Context con){
        context = con;
    }
    static Context context = null;
    static long stamp = System.currentTimeMillis();

    public static void log(String tesx){
        Log.i(Config.tag, tesx);
    }

    public static void log_toast(String tesx){
        Log.i(Config.tag, tesx);
        if(context == null){
            log("context isn't set yet.");
            return;
        }
        Toast.makeText(context, tesx, Toast.LENGTH_LONG);
    }

    public static void log_special(String tesx){
        Log.i(Config.tag, (System.currentTimeMillis() - stamp) + " # " + tesx);
    }

    public static void error(String text){
        Log.e(Config.tag, "ERROR ERROR HALT HALT #########################" + text);
    }
}

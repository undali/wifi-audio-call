package com.example.tuhin.ccaudio;

import android.media.AudioFormat;

import java.net.InetAddress;

/**
 * Created by Tuhin on 11/20/2016.
 */

public class Config {
    public static final int AUDIO_SAMPLE_RATE = 16000;
    public static final int AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    public static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    public static final int PLAYER_CHANNELS = AudioFormat.CHANNEL_OUT_MONO;

    public static final String tag = "ccaudio";

    public static final int my_port = 38975;
    public static final String unique_id = "hxdfkwt";

    public static InetAddress my_ip = null;
    //public static int my_port = -1;

    public static InetAddress friend_ip = null;
    public static int friend_port = -1;

    public static final int burning_time = 30;
    public static final int burning_burst_time = 10;


    public static final String message_discover_done = "message_1";
}

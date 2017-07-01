package com.example.tuhin.ccaudio;

import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

/**
 * Created by Tuhin on 11/20/2016.
 */

public class AudioPlayer {

    boolean isPlaying = false, playerStopped = true;
    Thread playingThread = null;
    AudioTrack player;
    int min_buffer_size = 0;
    public void startPlaying(){
        min_buffer_size = AudioTrack.getMinBufferSize(Config.AUDIO_SAMPLE_RATE, Config.PLAYER_CHANNELS, Config.AUDIO_ENCODING);
        //min_buffer_size = 5000;
        player = new AudioTrack(AudioManager.STREAM_VOICE_CALL, Config.AUDIO_SAMPLE_RATE, Config.PLAYER_CHANNELS, Config.AUDIO_ENCODING, min_buffer_size, AudioTrack.MODE_STREAM);
        if (player.getState() != AudioTrack.STATE_INITIALIZED) {
            Log.e(Config.tag, "Failed to initialize audio player!");
            return;
        }

        isPlaying = true;
        playerStopped = false;

        playingThread = new Thread(new Runnable() {
            public void run() {
                processOutputAudio();
            }
        }, "AudioPlayer Thread");

        playingThread.start();
    }

    void processOutputAudio(){
        long timeStart, timeEnds, val;
        boolean play_started = false;
        long written_size = 0;

        /*while(isPlaying){

            timeStart = System.currentTimeMillis();
            Integer t = null;
            try {
                t = CommonData.test.take();
                //t = CommonData.test.poll(1000, java.util.concurrent.TimeUnit.MILLISECONDS);
                if(t==null) continue;
            } catch (Exception e) {
                e.printStackTrace();
            }
            timeEnds = System.currentTimeMillis();
            Mouse.log("Poll takes milliseconds: " + (timeEnds-timeStart) + " Returns: " + t + " Qsize " + CommonData.test.size());
        }*/

        while(isPlaying){

            short[] data = null;
            try {
                data = SharedData.playerQueue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(data == null){
                continue;
            }
            timeStart = System.currentTimeMillis();
            val = player.write(data, 0, data.length);
            String tmp = "";
            int zero = 0;
            /*for(int i = 0;i<data.length;i++){
                tmp += data[i] + " ";
                if(data[i] == 0)
                    zero++;
            }
            Mouse.log("Dump: " + tmp);*/
            //Mouse.log("player write returns:" + val + " datalength:" + data.length + " zerocount:" + zero);
            written_size += val;
            player.play();
            /*if(play_started == false){
                if(written_size > 2000){
                    play_started = true;
                    player.play();
                    Mouse.log("Player started.");
                }else{
                    Mouse.log("### buffer is not filled enough. buffersize:" + written_size + " minbuffersize:" + min_buffer_size + " datalength:" + data.length);
                }
            }*/

            timeEnds = System.currentTimeMillis();
            //Mouse.log("Player play takes milliseconds: " + (timeEnds-timeStart) + " Returns: " + val);
        }

        playerStopped = true;
    }
}

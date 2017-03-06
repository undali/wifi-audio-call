package com.example.tuhin.ccaudio;

import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Tuhin on 11/20/2016.
 */

public class AudioRecorder {

    private AudioRecord recorder = null;
    private Thread recordingThread = null;
    private boolean isRecording = false;
    private boolean recorderStopped = true;

    private int minBufferSize;

    AudioRecorder(){
        minBufferSize = AudioRecord.getMinBufferSize(Config.AUDIO_SAMPLE_RATE,
                Config.RECORDER_CHANNELS, Config.AUDIO_ENCODING);

        Log.i(Config.tag, "Minimum Buffer Size should be: " + minBufferSize);

        if(minBufferSize < 4096) {
            minBufferSize = 6144;
        } else if(minBufferSize == 4096) {
            minBufferSize *= 1;
        }
    }

    public void startRecording(){
        try {
            recorder = new AudioRecord(MediaRecorder.AudioSource.VOICE_COMMUNICATION,
                    Config.AUDIO_SAMPLE_RATE, Config.RECORDER_CHANNELS,
                    Config.AUDIO_ENCODING, minBufferSize);

            if (recorder.getState() != AudioRecord.STATE_INITIALIZED) {
                Log.e(Config.tag, "Failed to initialize audio recorder!");
                return;
            }
            recorder.startRecording();

            isRecording = true;
            recorderStopped = false;

            recordingThread = new Thread(new Runnable() {
                public void run() {
                    processInputAudio();
                }
            }, "AudioRecorder Thread");

            recordingThread.start();
        }catch (Exception e){

        }
    }

    public void stopRecording(){
        isRecording = false;
        recorder.stop();
    }
/*
    byte [] ShortToByte_Twiddle_Method(short [] input)
    {
        int short_index, byte_index;
        int iterations = input.length;

        byte [] buffer = new byte[input.length * 2];

        short_index = byte_index = 0;

        for(; short_index != iterations; )
        {
            buffer[byte_index]     = (byte) (input[short_index] & 0x00FF);
            buffer[byte_index + 1] = (byte) ((input[short_index] & 0xFF00) >> 8);

            ++short_index; byte_index += 2;
        }

        return buffer;
    }*/

    private void processInputAudio(){
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_AUDIO);

        long timeStart, timeEnds;
        int val;
        boolean ret = false;
        Integer counter = 0;

        FileOutputStream os = null;
        try {
            os = new FileOutputStream("/sdcard/benchmark.pcm");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Log.i(Config.tag, "Recording state " + recorder.getRecordingState() + " RecorderState: " + recorder.getState());
        //if(true)return;
        int cnt = 0;
        while (isRecording){
            short[] data = new short[Config.AUDIO_SAMPLE_RATE / 10];
            timeStart = System.currentTimeMillis();
            val = recorder.read(data, 0, data.length);
            timeEnds = System.currentTimeMillis();

            /*try {
                os.write(ShortToByte_Twiddle_Method(data), 0, val * 2);
            } catch (IOException e) {
                e.printStackTrace();
            }*/

            if(val == -3){
                try {
                    Thread.sleep(100);
                    Mouse.log("Recorder isn't ready");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                recorder.startRecording();
            }

            ret = SharedData.recordQueue.add(data);
            //Mouse.log("Recorder read takes milliseconds: " + (timeEnds-timeStart) + " Returns: " + val + " AddReturns:" + ret + " Adding: " + counter);

            //cnt++;if(cnt>60) break;
            //CommonData.test.add(counter++);
            //if(counter>100) break;
            //Log.i(Config.tag, "Recording state " + recorder.getRecordingState() + " RecorderState: " + recorder.getState());
            /*try {
                Thread.sleep(90);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
            //break;
        }
        try {
            os.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        Mouse.log("#################################### Recorder Exited ####################################");
        recorderStopped = true;
    }
}

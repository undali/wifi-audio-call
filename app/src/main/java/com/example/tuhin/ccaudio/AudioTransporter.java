package com.example.tuhin.ccaudio;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static android.content.Context.WIFI_SERVICE;

/**
 * Created by Tuhin on 2/27/2017.
 */

public class AudioTransporter {
    Thread sending_thread, receiving_thread;
    static Context context;
    DatagramSocket socket = null;
    boolean should_run_sender = false;
    boolean should_run_receiver = false;

    AudioTransporter(Context context){
        Mouse.log("audiotransporter started");
        this.context = context;
        initialize_port();
        sending_thread = new Thread(new Runnable() {
            public void run() {
                sending_worker();
            }
        }, "Sender Thread");
        sending_thread.start();

        receiving_thread = new Thread(new Runnable() {
            @Override
            public void run() {
                receiving_worker();
            }
        }, "Receiving Thread");
        receiving_thread.start();
    }

    void initialize_port(){
        try {
            socket = new DatagramSocket(Config.my_port, InetAddress.getByName("0.0.0.0"));
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    private void sending_worker(){
        DatagramPacket packet = null;
        should_run_sender = true;
        while(should_run_sender){
            short[] buffer = null;
            try {
                buffer = SharedData.recordQueue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(buffer == null)
                continue;
            // to turn shorts back to bytes.
            byte[] bytes = new byte[buffer.length * 2];
            ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().put(buffer);

            packet = new DatagramPacket(bytes, bytes.length, Config.friend_ip, Config.friend_port);
            try {
                socket.send(packet);
                Mouse.log("sending audio packet length:" + packet.getLength() + " ------->>>>>> " + Config.friend_ip + ":" + Config.friend_port);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void receiving_worker(){
        byte[] recvBuf = new byte[Config.AUDIO_SAMPLE_RATE/5];
        DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
        should_run_receiver = true;
        while(should_run_receiver){
            try {
                socket.receive(packet);
                byte[] data = packet.getData();
                //Mouse.log("data received with length:" + data.length);

                short[] shorts = new short[data.length/2];

                ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shorts);
                SharedData.playerQueue.add(shorts);
                //Mouse.log("data received short length:" + shorts.length);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

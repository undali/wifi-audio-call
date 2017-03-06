package com.example.tuhin.ccaudio;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

/**
 * Created by Tuhin on 2/27/2017.
 */

public class Peer {
    boolean discovery_should_run;
    boolean discovery_responder_should_run;
    Context context = null;
    Handler handler;
    Message message_class;

    Peer(Context context, Handler handler){
        Mouse.log("peer class initiated.");
        this.context = context;
        this.handler = handler;
        message_class = new Message();

        discovery_should_run = false;
        discovery_responder_should_run = false;

        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                respond_to_discovery();
                //start_discovery();
            }
        }, "peer discovery thread");
        th.start();
    }

    private void set_friend_address(DatagramPacket packet){
        Config.friend_ip = packet.getAddress();
        Config.friend_port = Config.my_port; //Using same port //packet.getPort();
        Mouse.log("friend address found. friend ip:" + Config.friend_ip.toString() + " port:" + Config.friend_port);
    }

    private void respond_to_discovery(){
        Mouse.log("discovery responder started");

        discovery_responder_should_run = true;

        byte[] recvBuf;
        DatagramSocket sock = null;
        DatagramPacket packet = null;
        try {
            recvBuf = new byte[5000];
            sock = new DatagramSocket(Config.my_port, InetAddress.getByName("0.0.0.0"));
            sock.setSoTimeout(200);
            packet = new DatagramPacket(recvBuf, recvBuf.length);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        while(discovery_responder_should_run){
            try {
                sock.receive(packet);
                Mouse.log("data received with length:" + packet.getLength());
                String msg = new String(packet.getData(), "UTF-8");

                if(Utils.verify_message(msg)){
                    Mouse.log("peer broadcat received.");
                    set_friend_address(packet);

                    String message = Utils.generate_broadcast_message(false);
                    packet = new DatagramPacket(message.getBytes(), message.length(), packet.getAddress(), packet.getPort());
                    for(int i=0;i <= Config.burning_time;){
                        sock.send(packet);
                        Thread.sleep(Config.burning_burst_time);
                        i += Config.burning_burst_time;
                    }

                    Mouse.log("happy time. discovering done.");
                    message_class.obj = Config.message_discover_done;
                    handler.sendMessage(message_class);
                    break;
                }else{
                    Mouse.log("unknown broadcast received.");
                }
            }
            catch (SocketTimeoutException e){
                Mouse.log("receive timeout");
            }
            catch (Exception e) {
                Mouse.error("Got Exception. Msg:" + e.getMessage());
            }
        }
        sock.close();
        Mouse.log("discovery responder exit");

        ////////////////////////////FALLBACK/////////////////////////////
        if(discovery_should_run) {
            Mouse.log("switching to active discovery");
            start_discovery();
        }
    }

    public void switch_to_discovery_mode(){
        discovery_should_run = true;
        discovery_responder_should_run = false;
    }

    private void start_discovery(){
        Mouse.log("discovery started.");
        discovery_should_run = true;
        DatagramSocket sock = null;
        DatagramPacket packet = null;
        DatagramPacket recv_packet = null;
        byte[] recvBuf = null;

        String message = Utils.generate_broadcast_message(false);
        try {
            sock = new DatagramSocket();
            sock.setBroadcast(true);
            sock.setSoTimeout(1000);

            packet = new DatagramPacket(message.getBytes(), message.length(), getBroadcastAddress(), Config.my_port);
            recvBuf = new byte[5000];
            recv_packet = new DatagramPacket(recvBuf, recvBuf.length);

        } catch (Exception e) {
            e.printStackTrace();
        }

        while(discovery_should_run){
            try {
                sock.send(packet);
                Log.i(Config.tag, "sending discovery packet ..");

                //Thread.sleep(1000);
                sock.receive(recv_packet);

                if(recv_packet.getLength() > 0){
                    String msg = new String(recv_packet.getData(), "UTF-8");
                    if(Utils.verify_message(msg)){
                        Mouse.log("discovery response received.");

                        set_friend_address(recv_packet);

                        message = Utils.generate_broadcast_message(true);
                        packet = new DatagramPacket(message.getBytes(), message.length(), getBroadcastAddress(), Config.my_port);
                        for(int i=0;i <= Config.burning_time;){
                            sock.send(packet);
                            Thread.sleep(Config.burning_burst_time);
                            i += Config.burning_burst_time;
                        }

                        Mouse.log("happy time. discovering done.");
                        message_class.obj = Config.message_discover_done;
                        handler.sendMessage(message_class);
                        break;
                    }else{
                        Mouse.log("unknown broadcast response received.");
                    }
                }
            }
            catch (SocketTimeoutException e){
                Mouse.log("receive timeout");
            }catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
        sock.close();
        Mouse.log("discovery exit.");
    }

    public int send(short[] data, int length){
        if((Config.friend_ip == null) || (Config.friend_port == -1)){
            Mouse.log("friend address not found.");
            return -1;
        }

        return -1;
    }







    InetAddress getBroadcastAddress() throws IOException {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcp = wifi.getDhcpInfo();
        //Mouse.log("dhcp address:" + dhcp.toString());
        if(dhcp == null){
            Mouse.error("error while getting dhcp info.");
            try {
                Thread.sleep(8000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return InetAddress.getByName("helloworld");
        }

        int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
        byte[] quads = new byte[4];
        for (int k = 0; k < 4; k++)
            quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
        return InetAddress.getByAddress(quads);
    }


}

package com.example.tuhin.ccaudio;

import android.content.Context;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity {

    Button button_start_discovery, button_start_call;
    ToggleButton button_toggle_lspeaker;
    Peer peer;
    Mouse mouse = null;
    AudioTransporter transporter;
    AudioRecorder recorder;
    AudioPlayer player;
    AudioManager audioManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        button_start_discovery = (Button) findViewById(R.id.button2);
        button_start_discovery.setText("connect");
        button_start_discovery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                peer.switch_to_discovery_mode();
            }
        });

        button_start_call = (Button) findViewById(R.id.button);
        button_start_call.setText("start call");
        button_start_call.setEnabled(false);
        button_start_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                transporter = new AudioTransporter(MainActivity.this);
                recorder = new AudioRecorder();
                player = new AudioPlayer();

                recorder.startRecording();
                player.startPlaying();
                button_start_call.setEnabled(false);
            }
        });

        button_toggle_lspeaker = (ToggleButton) findViewById(R.id.toggleButton);
        button_toggle_lspeaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handle_loudspeaker_mode(true);
            }
        });
        handle_loudspeaker_mode(false);

        mouse = new Mouse(this);
        peer = new Peer(this, handler);
    }
    private void handle_loudspeaker_mode(boolean toggle){

        //audioManager.setMode(AudioManager.MODE_IN_CALL);
        if(toggle) {
            audioManager.setSpeakerphoneOn(!audioManager.isSpeakerphoneOn());
        }

        if(audioManager.isSpeakerphoneOn()){
            //button_toggle_lspeaker.setText("speaker on");
            button_toggle_lspeaker.setChecked(true);
        }else{
            //button_toggle_lspeaker.setText("speaker off");
            button_toggle_lspeaker.setChecked(false);
        }
    }

    private Handler handler = new Handler() {

        //@SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message m) {
            String message = (String)m.obj;
            if(message.equals(Config.message_discover_done)) {
                Toast.makeText(getApplicationContext(), "discovery done.", Toast.LENGTH_SHORT).show();
                button_start_call.setEnabled(true);
                button_start_discovery.setEnabled(false);
            }
        }
    };

}

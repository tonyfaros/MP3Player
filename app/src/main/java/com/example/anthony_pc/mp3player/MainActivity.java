package com.example.anthony_pc.mp3player;

import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    Button imageBtnPrevious;
    Button imageBtnPlay_Stop;
    Button imageBtnNext;
    ListView lvSongs;
    final String[] songs = {"Eminem - Cleanin' out my closet","Eminem - The real slim shady",
    "Judah the lion - Take it all back","Metallica - Mama said"};
    final int[] resID = {R.raw.eminemcleaninoutmycloset, R.raw.eminemtherealslimshady, R.raw.judahtheliontakeitallback, R.raw.mamasaidmetallica};
    MediaPlayer mediaPlayer;
    SeekBar advanceSB, volumeSB;


    Handler handler;
    Runnable runnable;
    int currentSong = -1;

    AudioManager audioManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Typeface font = Typeface.createFromAsset( getAssets(), "fontawesome-webfont.ttf" );
        mediaPlayer = MediaPlayer.create(getApplicationContext(), resID[0]);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        volumeSB = findViewById(R.id.volumeSB);
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        volumeSB.setMax(maxVolume);

        volumeSB.setProgress(currentVolume);

        currentSong = 0;
        mediaPlayer.pause();

        imageBtnNext = findViewById(R.id.next);
        imageBtnNext.setTypeface(font);
        imageBtnPrevious = findViewById(R.id.previous);
        imageBtnPrevious.setTypeface(font);
        imageBtnPlay_Stop = findViewById(R.id.play_stop);
        imageBtnPlay_Stop.setTypeface(font);

        handler = new Handler();

        advanceSB = findViewById(R.id.advanceSB);





        advanceSB.setMax(mediaPlayer.getDuration());
        songCycle();


        lvSongs = findViewById(R.id.listViewSongs);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,  android.R.layout.simple_list_item_1, songs);
        lvSongs.setAdapter(adapter);

        lvSongs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mediaPlayer.stop();
                mediaPlayer = MediaPlayer.create(getApplicationContext(), resID[i]);
                currentSong = i;
                advanceSB.setMax(mediaPlayer.getDuration());
                mediaPlayer.start();
                songCycle();
            }
        });

        volumeSB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,i,0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });



        advanceSB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean input) {
                if(input)
                    mediaPlayer.seekTo(progress);


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)){
            volumeSB.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
        }
        return super.onKeyDown(keyCode, event);

    }
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if((keyCode == KeyEvent.KEYCODE_VOLUME_UP)){
            volumeSB.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
        }
        return super.onKeyDown(keyCode, event);

    }


    public void songCycle(){
        advanceSB.setProgress(mediaPlayer.getCurrentPosition());

        if(mediaPlayer.isPlaying()){
            runnable = new Runnable() {
                @Override
                public void run() {
                    songCycle();
                }
            };
            handler.postDelayed(runnable,1000);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mediaPlayer.start();
        songCycle();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mediaPlayer.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
        handler.removeCallbacks(runnable);
    }

    public void clickPlay(View view){

        if(mediaPlayer.isPlaying()){
            mediaPlayer.pause();
            imageBtnPlay_Stop.setText(String.valueOf((char) 0xf28d));
        }else{
            mediaPlayer.start();
            imageBtnPlay_Stop.setText(String.valueOf((char) 0xf144));
            songCycle();
        }
    }

    public void clickNextSong(View view){
        mediaPlayer.stop();
        if(currentSong == resID.length-1) {
            currentSong = 0;
            mediaPlayer = MediaPlayer.create(getApplicationContext(), resID[currentSong]);
        }else{
            mediaPlayer = MediaPlayer.create(getApplicationContext(), resID[currentSong+1]);
            currentSong +=1;
        }
        mediaPlayer.start();
        songCycle();


    }
    public void clickPreviousSong(View view){
        Log.e("id",String.valueOf(currentSong));
        mediaPlayer.stop();
        if(currentSong == 0){
            mediaPlayer = MediaPlayer.create(getApplicationContext(), resID[resID.length-1]);
            currentSong = resID.length-1;
        }else{
            mediaPlayer = MediaPlayer.create(getApplicationContext(), resID[currentSong-1]);
            currentSong-=1;
        }
        mediaPlayer.start();
        songCycle();
    }


}

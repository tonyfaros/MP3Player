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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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

    final String[] songsLyrics = {"cleanin_out_my_closet","the_real_slim_shady",
            "take_it_all_back","mama_said_lyrics"};
    final int[] resID = {R.raw.eminemcleaninoutmycloset, R.raw.eminemtherealslimshady, R.raw.judahtheliontakeitallback, R.raw.mamasaidmetallica};
    MediaPlayer mediaPlayer;
    SeekBar advanceSB, volumeSB;


    Handler handler;
    Runnable runnable;
    int currentSong = 0;

    StringBuilder lyrics = new StringBuilder();
    ScrollView scrollView;

    TextView tvLyrics;

    int lengthSB = 0;
    int lengthSCB = 0;

    AudioManager audioManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Typeface font = Typeface.createFromAsset( getAssets(), "fontawesome-webfont.ttf" );
        tvLyrics = findViewById(R.id.TVLyrics);
        scrollView = findViewById(R.id.scrollViewLyrics);
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


        lengthSB = mediaPlayer.getDuration();
        lengthSCB = scrollView.getChildAt(0).getHeight();


        advanceSB.setMax(mediaPlayer.getDuration());
        songCycle();


        lvSongs = findViewById(R.id.listViewSongs);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,  android.R.layout.simple_list_item_1, songs);
        lvSongs.setAdapter(adapter);

        scrollView.scrollTo(0,300);
        lvSongs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                currentSong = i;
                songNextChange();
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
                if(input) {
                    mediaPlayer.seekTo(progress);
                }
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
        if((keyCode == KeyEvent.KEYCODE_BACK)){
            finish();
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
        int cont = (mediaPlayer.getCurrentPosition() * 100) / lengthSB;
        int cont2 = (cont * lengthSCB) / 100;
        scrollView.scrollTo(0,cont2-400);
        if(mediaPlayer.isPlaying()){
            runnable = new Runnable() {
                @Override
                public void run() {
                    songCycle();
                }
            };
            handler.postDelayed(runnable,1000);
        }
        if(mediaPlayer.getCurrentPosition() == lengthSB){
            clickNextSong(null);

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
            Log.e("playing","playing");
            mediaPlayer.pause();
            imageBtnPlay_Stop.setText(String.valueOf((char) 0xf144));
        }else{
            Log.e("playing","stop");
            mediaPlayer.start();
            imageBtnPlay_Stop.setText(String.valueOf((char) 0xf28d));
            songCycle();
        }
    }

    public void clickNextSong(View view){
        //mediaPlayer.stop();
        if(currentSong == resID.length-1) {
            currentSong = 0;
            songNextChange();
            //mediaPlayer = MediaPlayer.create(getApplicationContext(), resID[currentSong]);
        }else{
            //mediaPlayer = MediaPlayer.create(getApplicationContext(), resID[currentSong+1]);
            currentSong +=1;
            songNextChange();
        }
        //mediaPlayer.start();
       // songCycle();


    }
    public void clickPreviousSong(View view){
        Log.e("id",String.valueOf(currentSong));
        //mediaPlayer.stop();
        if(currentSong == 0){
            //mediaPlayer = MediaPlayer.create(getApplicationContext(), resID[resID.length-1]);

            currentSong = resID.length-1;
            songNextChange();
        }else{
            //mediaPlayer = MediaPlayer.create(getApplicationContext(), resID[currentSong-1]);

            currentSong-=1;
            songNextChange();
        }
        //mediaPlayer.start();
        //songCycle();
    }

    public void songNextChange(){
        mediaPlayer.stop();
        mediaPlayer = MediaPlayer.create(getApplicationContext(), resID[currentSong]);
        //currentSong = i;
        advanceSB.setMax(mediaPlayer.getDuration());
        mediaPlayer.start();
        songCycle();
        imageBtnPlay_Stop.setText(String.valueOf((char) 0xf28d));

        BufferedReader reader = null;
        lyrics = new StringBuilder();


        try {
            reader = new BufferedReader(
                    new InputStreamReader(getAssets().open(songsLyrics[currentSong]+".txt")));

            // do reading, usually loop until end of file reading
            String mLine;
            while ((mLine = reader.readLine()) != null) {
                lyrics.append(mLine);
                lyrics.append('\n');
            }
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(),"Error reading file!",Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    //log the exception
                }
            }
        }

        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.animationfile);
        animation.setDuration(mediaPlayer.getDuration() * 1000);
        tvLyrics.setText(lyrics);
        lengthSB = mediaPlayer.getDuration();
        lengthSCB = scrollView.getChildAt(0).getHeight();
        tvLyrics.startAnimation(animation);
        animation.start();

    }



}

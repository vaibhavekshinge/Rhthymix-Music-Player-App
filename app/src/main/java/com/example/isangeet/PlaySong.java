package com.example.isangeet;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class PlaySong extends AppCompatActivity {

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.release();
        updateSeek.interrupt();
    }

    TextView textView, textView1;
    ImageView play, previous, next, repeat, playlist, lyrics;
    ArrayList<File> songs;
    MediaPlayer mediaPlayer;
    String textContent;
    String textContent2;
    int position;
    SeekBar seekBar;
    Thread updateSeek;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_song);
        textView = findViewById(R.id.textView);
        textView1 = findViewById(R.id.textView2);
        play = findViewById(R.id.play);
        previous = findViewById(R.id.previous);
        next = findViewById(R.id.next);
        seekBar = findViewById(R.id.seekBar);
        repeat = findViewById(R.id.repeat);
        playlist = findViewById(R.id.playlist);
        lyrics = findViewById(R.id.lyrics);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        songs = (ArrayList)bundle.getParcelableArrayList("songList");
        textContent = intent.getStringExtra("currentSong");
        textView.setText(textContent);
        textView.setSelected(true);
        textContent2 = intent.getStringExtra("nextSong");
        textView1.setText("Next:  "+textContent2);
        textView1.setSelected(true);
        position = intent.getIntExtra("position", 0);
        Uri uri = Uri.parse(songs.get(position).toString());
        mediaPlayer = MediaPlayer.create(this, uri);
        mediaPlayer.start();
        seekBar.setMax(mediaPlayer.getDuration());

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });

        updateSeek = new Thread(){
            @Override
            public void run() {
                int currPosition = 0;
                try {
                    while (currPosition<mediaPlayer.getDuration()){
                        currPosition = mediaPlayer.getCurrentPosition();
                        seekBar.setProgress(currPosition);
                        sleep(800);
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        updateSeek.start();

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mediaPlayer.isPlaying()){
                    play.setImageResource(R.drawable.play);
                    mediaPlayer.pause();
                }
                else{
                    play.setImageResource(R.drawable.pause);
                    mediaPlayer.start();
                }
            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.release();
                if(position!=0){
                    position = position-1;
                }
                else {
                    position = songs.size()-1;
                }
                Uri uri = Uri.parse(songs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                mediaPlayer.start();
                play.setImageResource(R.drawable.pause);
                seekBar.setMax(mediaPlayer.getDuration());
                textContent = songs.get(position).getName().toString();
                textView.setText(textContent);
                if(position!=songs.size()-1){
                    textContent2 = songs.get(position+1).getName().toString();
                }
                else {
                    textContent2 = songs.get(0).getName().toString();
                }
                textView1.setText("Next:  "+textContent2);
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.release();
                if(position!=songs.size()-1){
                    position = position+1;
                }
                else {
                    position = 0;
                }
                Uri uri = Uri.parse(songs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                mediaPlayer.start();
                play.setImageResource(R.drawable.pause);
                seekBar.setMax(mediaPlayer.getDuration());
                textContent = songs.get(position).getName().toString();
                textView.setText(textContent);
                if(position!=songs.size()-1){
                    textContent2 = songs.get(position+1).getName().toString();
                }
                else {
                    textContent2 = songs.get(0).getName().toString();
                }
                textView1.setText("Next:  "+textContent2);
            }
        });

        repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.release();
                Uri uri = Uri.parse(songs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                mediaPlayer.start();
                seekBar.setMax(mediaPlayer.getDuration());
            }
        });

        playlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent1);
                mediaPlayer.stop();
                mediaPlayer.release();
            }
        });

        lyrics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String link = "https://www.google.com/search?q=" + textView.getText().toString() + "+lyrics&sca_esv=580203348&sxsrf=AM9HkKl85uBg5WUqfbDlMbz31pDOpg7dHA%3A1699389151589&source=hp&ei=355KZeCdIsmYseMPhJCdkAM&iflsig=AO6bgOgAAAAAZUqs7y-p0k4fqRSRLprEh4l08_7n-8MB&oq=copines+&gs_lp=Egdnd3Mtd2l6Ighjb3BpbmVzICoCCAAyBxAjGIoFGCcyCBAAGIoFGJECMgoQABiABBgUGIcCMggQABiKBRiRAjIIEAAYigUYkQIyBRAuGIAEMggQABiKBRiRAjIFEAAYgAQyBRAAGIAEMgUQLhiABEiMU1CDKljyPnABeACQAQCYAY4BoAHzB6oBAzAuOLgBAcgBAPgBAagCCsICBxAjGOoCGCfCAg4QLhiKBRjHARjRAxiRAsICCxAAGIAEGLEDGIMBwgIREC4YgAQYsQMYgwEYxwEY0QPCAgQQIxgnwgIUEC4YigUYsQMYgwEYxwEY0QMYkQLCAg0QLhiKBRjHARjRAxhDwgIHEAAYigUYQ8ICDRAAGIoFGLEDGIMBGEPCAgoQLhiKBRixAxhDwgIIEAAYgAQYsQPCAg0QLhiKBRixAxiDARhDwgIKEAAYigUYsQMYQ8ICChAuGLEDGIoFGEPCAggQLhixAxiABMICCxAuGLEDGIoFGJEC&sclient=gws-wiz";
                Uri uri = Uri.parse(link);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
    }
}
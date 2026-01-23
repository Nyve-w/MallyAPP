package com.example.mymusique;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class PopActivity extends AppCompatActivity {
    private MediaPlayer mediaPlayer;
    private Button Button,Button2,Retour,Button6,Button7,Button8,Button9;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop);

        Button=findViewById(R.id.button);
        Button2=findViewById(R.id.button2);
        Retour=findViewById(R.id.Retour);
        Button6=findViewById(R.id.button6);
        Button7=findViewById(R.id.button7);
        Button8=findViewById(R.id.button8);
        Button9=findViewById(R.id.button9);

        Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playMusic(R.raw.pop_un);

            }
        });
        Button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playMusic(R.raw.pop_deux);

            }
        });
        Button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
       Button7.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {

           }
       });
       Button8.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {

           }
       });
       Button9.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {

           }
       });
        Retour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();

            }
        });

    }
    private void playMusic(int resId){
        if(mediaPlayer!= null){
            mediaPlayer.release();
        }
        mediaPlayer=MediaPlayer.create(this,resId);
        mediaPlayer.start();
    }
    @Override
    protected void onStop(){
        super.onStop();
        if(mediaPlayer!=null){
            mediaPlayer.release();
            mediaPlayer=null;
        }
    }
}


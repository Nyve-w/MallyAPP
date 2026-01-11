package com.example.mally;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;


public class nosformations extends AppCompatActivity {
    Button buttonop;
    VideoView videoView, videoView2;
    FrameLayout container,container2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nosformations);
        matchID();
        putVideo();
    }
    public void matchID(){

        videoView = findViewById(R.id.videoView);
        videoView2 = findViewById(R.id.videoView2);
        //  buttonop = findViewById(R.id.bt1);
        container = findViewById(R.id.videoContainer);
        container2 = findViewById(R.id.videoContainer2);
    }
    public void putVideo(){
        Uri uri = Uri.parse("android.resource://"
                + getPackageName() + "/" + R.raw.formation);

        videoView.setVideoURI(uri);
        videoView.start();
        videoView2.setVideoURI(uri);
        videoView2.start();
        MediaController controller = new MediaController(this);
        MediaController controller2 = new MediaController(this);
        controller.setAnchorView(container);
        controller2.setAnchorView(container2);
        videoView.setMediaController(controller);
        videoView2.setMediaController(controller2);
    }
    public void Toast(View v){
        Toast.makeText(this, "Telechargement pdf en cours", Toast.LENGTH_SHORT).show();
    }
}
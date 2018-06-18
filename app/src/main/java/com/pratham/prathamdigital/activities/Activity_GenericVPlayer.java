package com.pratham.prathamdigital.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.pratham.prathamdigital.R;

import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.VideoView;

import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.util.PD_Utility;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Activity_GenericVPlayer extends AppCompatActivity implements MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener {

    @BindView(R.id.videoView)
    VideoView myVideoView;
    private String myVideo;
    private String StartTime;
    private String resId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__generic_vplayer);

        ButterKnife.bind(this);
        myVideo = getIntent().getStringExtra("videoPath");
        StartTime = PD_Utility.GetCurrentDateTime();
        resId = getIntent().getStringExtra("resId");

        Play(Uri.parse(myVideo));
        myVideoView.setOnPreparedListener(this);
        myVideoView.setOnCompletionListener(this);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

    }


    public void Play(Uri path) {
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(myVideoView);
        try {
            myVideoView.setVideoURI(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        myVideoView.setMediaController(mediaController);
        myVideoView.requestFocus();
    }


    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        Runtime rs = Runtime.getRuntime();
        rs.freeMemory();
        rs.gc();
        rs.freeMemory();
        this.finish();
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        myVideoView.start();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Runtime rs = Runtime.getRuntime();
        rs.freeMemory();
        rs.gc();
        rs.freeMemory();
        this.finish();
    }

}

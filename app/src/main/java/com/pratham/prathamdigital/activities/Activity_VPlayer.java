package com.pratham.prathamdigital.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.transition.ArcMotion;
import android.util.Log;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.TextView;

import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.custom.morphing.MorphDialogToFab;
import com.pratham.prathamdigital.custom.morphing.MorphFabToDialog;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by HP on 19-08-2017.
 */

public class Activity_VPlayer extends AppCompatActivity /*implements YoutubePlayerView.YouTubeListener */{

    @BindView(R.id.rl_vtitle)
    ViewGroup rl_vtitle;
    @BindView(R.id.v_title)
    TextView v_title;
//    @BindView(R.id.youtubePlayerView)
//    YoutubePlayerView youtubePlayerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vplayer);
        ButterKnife.bind(this);
//        setupEnterTransitions();
//        YTParams params = new YTParams();
//        params.setPlaybackQuality(PlaybackQuality.medium);
//        youtubePlayerView.setAutoPlayerHeight(this);
        Log.d("other::", getIntent().getStringExtra("videoPath"));
        v_title.setText(getIntent().getStringExtra("title"));
//        youtubePlayerView.initialize(getIntent().getStringExtra("videoPath"), params, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                youtubePlayerView.play();
//            }
//        }, 500);
    }

    @Override
    protected void onDestroy() {
//        youtubePlayerView.destroy();
        super.onDestroy();
    }

    private void setupEnterTransitions() {
        ArcMotion arcMotion = new ArcMotion();
        arcMotion.setMinimumHorizontalAngle(50f);
        arcMotion.setMinimumVerticalAngle(50f);

        Interpolator easeInOut = AnimationUtils.loadInterpolator(this, android.R.interpolator.fast_out_slow_in);

        MorphFabToDialog sharedEnter = new MorphFabToDialog();
        sharedEnter.setPathMotion(arcMotion);
        sharedEnter.setInterpolator(easeInOut);

        MorphDialogToFab sharedReturn = new MorphDialogToFab();
        sharedReturn.setPathMotion(arcMotion);
        sharedReturn.setInterpolator(easeInOut);

        if (rl_vtitle != null) {
            sharedEnter.addTarget(rl_vtitle);
            sharedReturn.addTarget(rl_vtitle);
        }
        getWindow().setSharedElementEnterTransition(sharedEnter);
        getWindow().setSharedElementReturnTransition(sharedReturn);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        youtubePlayerView.pause();
    }
//
//    @Override
//    public void onReady() {
//        Log.d("youtubePlayerView::","on ready");
//    }
//
//    @Override
//    public void onStateChange(YoutubePlayerView.STATE state) {
//        Log.d("youtubePlayerView::","on state changed");
//    }
//
//    @Override
//    public void onPlaybackQualityChange(String arg) {
//        Log.d("youtubePlayerView::","on quality change");
//    }
//
//    @Override
//    public void onPlaybackRateChange(String arg) {
//        Log.d("youtubePlayerView::","on playbach rate change");
//    }
//
//    @Override
//    public void onError(String arg) {
//        Log.d("youtubePlayerView::","on Error");
//    }
//
//    @Override
//    public void onApiChange(String arg) {
//        Log.d("youtubePlayerView::","on Api change");
//    }
//
//    @Override
//    public void onCurrentSecond(double second) {
//        Log.d("youtubePlayerView::","on Current Second");
//    }
//
//    @Override
//    public void onDuration(double duration) {
//        Log.d("youtubePlayerView::","on Duration");
//    }
//
//    @Override
//    public void logs(String log) {
//        Log.d("youtubePlayerView::","on Logs");
//    }
}

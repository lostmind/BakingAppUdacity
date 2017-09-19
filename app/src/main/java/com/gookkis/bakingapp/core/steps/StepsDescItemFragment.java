package com.gookkis.bakingapp.core.steps;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.gookkis.bakingapp.R;
import com.gookkis.bakingapp.utils.Const;


/**
 * Created by herikiswanto on 8/22/17.
 */
public class StepsDescItemFragment extends Fragment {

    public SimpleExoPlayerView mPlayerView;
    public TextView tvDescirption;
    public SimpleExoPlayer simpleExoPlayer;

    public static final String PLAYWHENREADY = "PLAYWHENREADY";
    public boolean playWhenReady = true;

    public static final String CURRENTWINDOW = "CURRENTWINDOW";
    public int currentWindow;

    public static final String PLAYBACKPOSITION = "PLAYBACKPOSITION";
    public long playBackPosition;

    public String videoURL;
    public String description;

    public StepsDescItemFragment() {
    }

    public static StepsDescItemFragment newInstance(String videoURL, String description) {
        StepsDescItemFragment f = new StepsDescItemFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Const.VIDEO_URL, videoURL);
        bundle.putString(Const.DESCRIPTION, description);
        f.setArguments(bundle);
        return f;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(PLAYWHENREADY, playWhenReady);
        outState.putInt(CURRENTWINDOW, currentWindow);
        outState.putLong(PLAYBACKPOSITION, playBackPosition);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey(Const.VIDEO_URL) && getArguments().containsKey(Const.DESCRIPTION)) {
            Bundle bundle = getArguments();
            videoURL = bundle.getString(Const.VIDEO_URL);
            description = bundle.getString(Const.DESCRIPTION);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            playWhenReady = savedInstanceState.getBoolean(PLAYWHENREADY);
            currentWindow = savedInstanceState.getInt(CURRENTWINDOW);
            playBackPosition = savedInstanceState.getLong(PLAYBACKPOSITION);
        }

        View rootView = inflater.inflate(R.layout.fragment_steps_desc_item, container, false);
        mPlayerView = rootView.findViewById(R.id.simple_exo_player);
        tvDescirption = rootView.findViewById(R.id.tv_step_detail);
        tvDescirption.setText(description);
        return rootView;
    }


    @Override
    public void onStart() {
        super.onStart();
        hideSystemUI();
        if (Util.SDK_INT > 23) {
            initOrResumePlayer();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        hideSystemUI();
        if (Util.SDK_INT <= 23 || simpleExoPlayer == null) {
            initOrResumePlayer();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }

    private void initOrResumePlayer() {
        if (videoURL == null) return;
        simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(new DefaultRenderersFactory(getActivity())
                , new DefaultTrackSelector(), new DefaultLoadControl());
        mPlayerView.setPlayer(simpleExoPlayer);
        simpleExoPlayer.setPlayWhenReady(playWhenReady);
        simpleExoPlayer.seekTo(currentWindow, playBackPosition);

        Uri uri = Uri.parse(videoURL);
        MediaSource mediaSource = buildMediaSource(uri);
        simpleExoPlayer.prepare(mediaSource);
    }

    private MediaSource buildMediaSource(Uri uri) {
        return new ExtractorMediaSource(uri
                , new DefaultHttpDataSourceFactory("ua")
                , new DefaultExtractorsFactory(), null, null);
    }

    public void releasePlayer() {
        if (simpleExoPlayer != null) {
            playWhenReady = simpleExoPlayer.getPlayWhenReady();
            currentWindow = simpleExoPlayer.getCurrentWindowIndex();
            playBackPosition = simpleExoPlayer.getCurrentPosition();
            simpleExoPlayer.release();
            simpleExoPlayer = null;
        }
    }

    private void hideSystemUI() {
        mPlayerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }


}
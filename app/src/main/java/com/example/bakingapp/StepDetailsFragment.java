package com.example.bakingapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bakingapp.Modal.Step;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;


import java.util.List;
import java.util.Objects;

public class StepDetailsFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = StepDetailsFragment.class.getSimpleName();
    private static final String KEY_EXO_POSITION = "exopostionkey";
    private static final String KEY_EXO_PLAYREADY = "exoplayreadykey";

    private Step mStep;

    private ImageView iv_exo;
    private SimpleExoPlayer mExoPlayer;
    private SimpleExoPlayerView mPlayerView;

    private long exoPosition = 0;
    private boolean exoPlayWhenReady = true;

    // Define a new interface that triggers a callback in the host activity
    StepDetailsFragment.OnNavClickListener mCallback;

    // OnNavClickListener interface, calls a method in the host activity named onNavigate
    public interface OnNavClickListener {
        void onNavigate(int position);
    }

    // Override onAttach to make sure that the container activity has implemented the callback
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the host activity has implemented the callback interface
        // If not, it throws an exception
        try {
            mCallback = (StepDetailsFragment.OnNavClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnNavClickListener");
        }
    }

    // Mandatory empty constructor
    public StepDetailsFragment() {
    }

    @SuppressLint("ValidFragment")
    public StepDetailsFragment(Step step) {
        mStep = step;
    }

    public void setStep(Step step)  {
        mStep = step;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Retain this fragment across configuration changes.
        setRetainInstance(true);

        final View rootView = inflater.inflate(R.layout.fragment_step_details, container, false);
        if(savedInstanceState != null) {
            exoPosition = savedInstanceState.getLong(KEY_EXO_POSITION);
            exoPlayWhenReady = savedInstanceState.getBoolean(KEY_EXO_PLAYREADY);
        }

        iv_exo = (ImageView) rootView.findViewById(R.id.iv_exoplayer);
        mPlayerView = (SimpleExoPlayerView) rootView.findViewById(R.id.v_exoplayer);
        TextView tv_description = rootView.findViewById(R.id.tv_step_description);

        if(rootView.findViewById(R.id.nav_next)!=null) {
            ImageButton nav_prev = rootView.findViewById(R.id.nav_prev);
            ImageButton nav_next = rootView.findViewById(R.id.nav_next);

            nav_prev.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    // Trigger the callback method and pass in the previous id
                    mCallback.onNavigate(mStep.getId() - 1);
                }
            });
            nav_next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    // Trigger the callback method and pass in the next id
                    mCallback.onNavigate(mStep.getId() + 1);
                }
            });
        }

        if(mStep != null) {
            if(tv_description != null) {
                tv_description.setText(mStep.getDescription());
            } else {
                // set full screen programmtically?
            }
        }
        return rootView;

    }

    /**
     * Initialize ExoPlayer.
     * @param mediaUri The URI of the sample to play.
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void initializePlayer(Uri mediaUri) {
        if (mExoPlayer == null) {
            // Create an instance of the ExoPlayer.
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(getActivity(), trackSelector, loadControl);
            mPlayerView.setPlayer(mExoPlayer);
            // Prepare the MediaSource.
            String userAgent = Util.getUserAgent(getActivity(), "BakingApp");
            MediaSource mediaSource = new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(
                    Objects.requireNonNull(getActivity()), userAgent), new DefaultExtractorsFactory(), null, null);
            mExoPlayer.prepare(mediaSource);
            mExoPlayer.seekTo(exoPosition);
            mExoPlayer.setPlayWhenReady(exoPlayWhenReady);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(KEY_EXO_POSITION,exoPosition);
        outState.putBoolean(KEY_EXO_PLAYREADY,exoPlayWhenReady);
    }

    /**
     * Release ExoPlayer.
     */
    private void releasePlayer() {
        mExoPlayer.stop();
        mExoPlayer.release();
        mExoPlayer = null;
    }

    // Release video player on pause
    @Override
    public void onPause() {
        if(mExoPlayer!=null) {
            exoPosition = mExoPlayer.getCurrentPosition() ;
            exoPlayWhenReady = mExoPlayer.getPlayWhenReady();
            releasePlayer();
        }
        super.onPause();
    }

    // initialize video on resume
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onResume() {
        super.onResume();
        if(mStep.getVideoURL().length()>0){
            iv_exo.setVisibility(View.GONE);
            // Initialize the player.
            initializePlayer(Uri.parse(mStep.getVideoURL()));
        }
    }

    @Override
    public void onClick(View view) {}
}

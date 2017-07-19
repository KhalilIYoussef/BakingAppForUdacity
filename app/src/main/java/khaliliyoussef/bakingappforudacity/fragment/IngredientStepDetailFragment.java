package khaliliyoussef.bakingappforudacity.fragment;

import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.mediacodec.MediaCodecRenderer;
import com.google.android.exoplayer2.mediacodec.MediaCodecUtil;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import khaliliyoussef.bakingappforudacity.R;
import khaliliyoussef.bakingappforudacity.adapter.IngredientListAdapter;
import khaliliyoussef.bakingappforudacity.model.Ingredient;
import khaliliyoussef.bakingappforudacity.model.Step;

import static khaliliyoussef.bakingappforudacity.adapter.IngredientStepAdapter.INGREDIENTS;
import static khaliliyoussef.bakingappforudacity.adapter.IngredientStepAdapter.STEPS;
import static khaliliyoussef.bakingappforudacity.activity.IngredientStepActivity.isTabKey;
import static khaliliyoussef.bakingappforudacity.activity.IngredientStepActivity.POSITION;


public class IngredientStepDetailFragment extends Fragment implements ExoPlayer.EventListener {

    public static final String AUTOPLAY = "autoplay";
    public static final String CURRENT_WINDOW_INDEX = "current_window_index";
    public static final String PLAYBACK_POSITION = "playback_position";
    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
    //set it to false at first time running
    private boolean autoPlay = false;
    private  static int currentWindow;
    private  static long playbackPosition;
    private TrackSelector trackSelector;
    

    @BindView(R.id.rv_ingredients) RecyclerView mIngredientsRecyclerView;
    @BindView(R.id.step_video) SimpleExoPlayerView mPlayerView;
    @BindView(R.id.tv_description) TextView mDescription;
    @BindView(R.id.bt_previous) Button mPrevious;
    @BindView(R.id.bt_next) Button mNext;
    @BindView(R.id.step_detail_view) View mStepDetail;
    private SimpleExoPlayer mExoPlayer;
    private ArrayList<Ingredient> mIngredients;
    private ArrayList<Step> mSteps;
    private int mIndex;
    private int mPosition;
    private boolean isTablet;

//mandatory constructor
    public IngredientStepDetailFragment()
    {
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {

        if (savedInstanceState != null)
        {
            mIngredients = savedInstanceState.getParcelableArrayList(INGREDIENTS);
            mSteps = savedInstanceState.getParcelableArrayList(STEPS);
            isTablet = savedInstanceState.getBoolean(isTabKey);
            mPosition = savedInstanceState.getInt(POSITION);

            playbackPosition = savedInstanceState.getLong(PLAYBACK_POSITION);

            currentWindow = savedInstanceState.getInt(CURRENT_WINDOW_INDEX);
            autoPlay = savedInstanceState.getBoolean(AUTOPLAY, false);
        }

        View rootView = inflater.inflate(R.layout.fragment_ingredient_step_detail, container, false);
        //bind the views
        ButterKnife.bind(this,rootView);


        mPrevious.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //clicking the previous button
                //if he hasn't reached the beginning of the steps
                if (mIndex > 0)
                {
                    int index = --mIndex;
                    mDescription.setText(mSteps.get(index).getDescription());
                    playStepVideo(index);
                }
                else
                    {
                    Toast.makeText(getActivity(), R.string.start_of_steps, Toast.LENGTH_LONG).show();
                }


            }
        });
        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //clicking the next button
                //if he hasn't reached the end of the steps
                if (mIndex < mSteps.size() - 1)
                {
                    int index = ++mIndex;
                    mDescription.setText(mSteps.get(index).getDescription());
                    playStepVideo(index);
                }
                else
                    {

                    Toast.makeText(getActivity(), R.string.end_of_steps, Toast.LENGTH_LONG).show();
                }

            }
        });

        //
        mPosition = getArguments().getInt(POSITION);
        mIndex = mPosition - 1;
        isTablet = getArguments().getBoolean(isTabKey);



        return rootView;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onResume()
    {
        super.onResume();
        //Case #1
        if (isTablet && mPosition == 0) {
            //it's a tablet ad he clicked the ingredients
            //#hide the details
            mStepDetail.setVisibility(View.GONE);
            //show the ingredients
            mIngredientsRecyclerView.setVisibility(View.VISIBLE);

            mIngredients = getArguments().getParcelableArrayList(INGREDIENTS);
            IngredientListAdapter adapter = new IngredientListAdapter(mIngredients);
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mIngredientsRecyclerView.getContext(),
                    LinearLayoutManager.VERTICAL);
            mIngredientsRecyclerView.addItemDecoration(dividerItemDecoration);
            mIngredientsRecyclerView.setHasFixedSize(true);
            mIngredientsRecyclerView.setAdapter(adapter);
        }
        //Case #2
        else if (isTablet)
        {
            showStepsForTab();
        }
        //Case #3
        else if (mPosition == 0)
        {
            mStepDetail.setVisibility(View.GONE);
            mIngredientsRecyclerView.setVisibility(View.VISIBLE);
            mIngredients = getArguments().getParcelableArrayList(INGREDIENTS);
            IngredientListAdapter adapter = new IngredientListAdapter(mIngredients);
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mIngredientsRecyclerView.getContext(),
                    LinearLayoutManager.VERTICAL);
            mIngredientsRecyclerView.addItemDecoration(dividerItemDecoration);
            mIngredientsRecyclerView.setHasFixedSize(true);
            mIngredientsRecyclerView.setAdapter(adapter);
        }
        //Case #4
        else
        {
            showStepsForPhone();
        }

//        playbackPosition=mExoPlayer.getCurrentPosition();
//        currentWindow=mExoPlayer.getCurrentWindowIndex();
    }


    public void showStepsForTab()
    {

        mIngredientsRecyclerView.setVisibility(View.GONE);
        mStepDetail.setVisibility(View.VISIBLE);
        mPlayerView.setVisibility(View.VISIBLE);
        mDescription.setVisibility(View.VISIBLE);
        mSteps = getArguments().getParcelableArrayList(STEPS);
        assert mSteps != null;
        mDescription.setText(mSteps.get(mIndex).getDescription());
        playStepVideo(mIndex);
    }


    private void playStepVideo(int index)
    {
        mPlayerView.setVisibility(View.VISIBLE);
        mPlayerView.requestFocus();
        String videoUrl = mSteps.get(index).getVideoUrl();
        String thumbNailUrl = mSteps.get(index).getThumbnailUrl();
        if (!videoUrl.isEmpty())
        {
            initializePlayer(Uri.parse(videoUrl));
        }
        else if (!thumbNailUrl.isEmpty()) {
            initializePlayer(Uri.parse(thumbNailUrl));
        }
        else
            {
            mPlayerView.setVisibility(View.GONE);
        }
    }



    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void showStepsForPhone()
    {
        showStepsForTab();
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
                hideSystemUi();
        mPrevious.setVisibility(View.VISIBLE);
        mNext.setVisibility(View.VISIBLE);
    }

    /* Initialize ExoPlayer.
     * @param mediaUri The URI of the sample to play.
     * where the Uri is the link of the video
     */
    private void initializePlayer(Uri mediaUri) {
        mExoPlayer = null;
        // Create an instance of the ExoPlayer.

        DefaultRenderersFactory renderersFactory = new DefaultRenderersFactory(getActivity(),
                null, DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF);

        TrackSelection.Factory adaptiveTrackSelectionFactory = new AdaptiveTrackSelection.Factory(BANDWIDTH_METER);

        trackSelector = new DefaultTrackSelector(adaptiveTrackSelectionFactory);
        LoadControl loadControl = new DefaultLoadControl();
        mExoPlayer = ExoPlayerFactory.newSimpleInstance(renderersFactory, trackSelector, loadControl);

        // Set the ExoPlayer.EventListener to this activity.
        mExoPlayer.addListener(this);

        mPlayerView.setPlayer(mExoPlayer);
        mExoPlayer.setPlayWhenReady(true);

        mExoPlayer.seekTo(currentWindow, playbackPosition);

        // Prepare the MediaSource.
        String userAgent = Util.getUserAgent(getActivity(), "Baking App");

        MediaSource mediaSource = new ExtractorMediaSource(mediaUri,
                new DefaultDataSourceFactory(getActivity(), BANDWIDTH_METER,
                        new DefaultHttpDataSourceFactory(userAgent, BANDWIDTH_METER)),
                new DefaultExtractorsFactory(), null, null);

        mExoPlayer.prepare(mediaSource);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        releasePlayer();
    }

    //i guess this is better that onStop()
    @Override
    public void onPause()
    {
        super.onPause();
        releasePlayer();
    }


    /**
     * Release ExoPlayer.
     */
    private void releasePlayer()
    {
        if (mExoPlayer != null)
        {
            playbackPosition=mExoPlayer.getCurrentPosition();
            currentWindow=mExoPlayer.getCurrentWindowIndex();
            mExoPlayer.stop();
            mExoPlayer.release();
            mExoPlayer = null;
        }
    }
// only above 4.1
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void hideSystemUi()
    {
        View decorView = getActivity().getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN //Full Screen
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

    private void showSystemUI()
    {
        View decorView = getActivity().getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        //getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        if (mExoPlayer != null)
        {
            //this is because if you rotated the screen while it's playing
            outState.putLong(PLAYBACK_POSITION, playbackPosition);
            outState.putInt(CURRENT_WINDOW_INDEX, currentWindow);

            outState.putBoolean(AUTOPLAY, autoPlay);
        }
        outState.putParcelableArrayList(INGREDIENTS, mIngredients);
        outState.putParcelableArrayList(STEPS, mSteps);
        outState.putBoolean(isTabKey, isTablet);
        outState.putInt(POSITION, mPosition);

    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {
    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading)
    {
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
//            playbackPosition=mExoPlayer.getCurrentPosition();
//            currentWindow=mExoPlayer.getCurrentWindowIndex();
            if((playbackState == ExoPlayer.STATE_READY) && playWhenReady){

            Toast.makeText(getActivity(), "Playing", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onPlayerError(ExoPlaybackException e) {
        String errorString = null;
        if (e.type == ExoPlaybackException.TYPE_RENDERER)
        {
            Exception cause = e.getRendererException();
            if (cause instanceof MediaCodecRenderer.DecoderInitializationException) {
                // Special case for decoder initialization failures.
                MediaCodecRenderer.DecoderInitializationException decoderInitializationException =
                        (MediaCodecRenderer.DecoderInitializationException) cause;
                if (decoderInitializationException.decoderName == null)
                {
                    if (decoderInitializationException.getCause() instanceof MediaCodecUtil.DecoderQueryException)
                    {
                        errorString = getString(R.string.error_querying_decoders);
                    }
                    else if (decoderInitializationException.secureDecoderRequired)
                    {
                        errorString = getString(R.string.error_no_secure_decoder);
                    }
                    else
                        {
                        errorString = getString(R.string.error_no_decoder);
                        }
                }
                else
                    {
                    errorString = getString(R.string.error_instantiating_decoder);
                    }
            }
        }
        if (errorString != null) {
            Toast.makeText(getActivity(), errorString, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onPositionDiscontinuity()
    {
    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters)
    {

    }


}

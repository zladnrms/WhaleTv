package zladnrms.defytech.kim.BroadcastTv.view;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlaybackControlView;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.orhanobut.logger.Logger;

import zladnrms.defytech.kim.BroadcastTv.contract.VideoViewerContract;
import zladnrms.defytech.kim.BroadcastTv.R;
import zladnrms.defytech.kim.BroadcastTv.databinding.ActivityVideoViewerBinding;
import zladnrms.defytech.kim.BroadcastTv.eventbus.RxBus;
import zladnrms.defytech.kim.BroadcastTv.presenter.VideoViewerPresenter;

public class VideoViewerActivity extends AppCompatActivity implements VideoViewerContract.View, SimpleExoPlayer.VideoListener {

    /* Data binding */
    private ActivityVideoViewerBinding binding;

    /* presenter */
    private VideoViewerPresenter presenter;

    private int videoId;
    private String nickname;
    private String videoUrl = "http://52.79.108.8/record/";
    private String subject;
    private String filename;
    private int viewCount;
    private String path;

    /* VideoView */
    private boolean expand = false;

    /* ExoPlayer and ExoPlayer Custom */
    private static final String TAG = "MainActivity";
    private SimpleExoPlayerView simpleExoPlayerView;
    private SimpleExoPlayer player;
    private AspectRatioFrameLayout aspectRatioFrameLayout;
    private PlaybackControlView controlView;
    private Button btnLike, btnOrientation, btnBack;
    private TextView tvViewerCount, tvSubject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_video_viewer);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_video_viewer);

        presenter = new VideoViewerPresenter();
        presenter.attachView(this);

        Intent intent = getIntent();
        videoId = intent.getIntExtra("videoId", 0);
        nickname = intent.getStringExtra("nickname");
        subject = intent.getStringExtra("subject");
        filename = intent.getStringExtra("filename");
        viewCount = intent.getIntExtra("viewCount", 0) + 1; // 기존 viewCount + 현재 자신으로 인한 viewCount 증가를 반영 (서버에는 직후 반영)

        /* ExoPlayer Setting */
        // set your path here
        path = videoUrl + filename + ".mp4";

        /* Set ExoPlayer and Listener */
        initExoPlayer();

        /* ExoPlayer Control By Custom View */
        initCustomExoPlayerUI();

        /* Plus One Viewer Count this video (to server) */
        presenter.upVideoCount(videoId);
    }

    private void initExoPlayer() {
        // 1. Create a default TrackSelector
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector =
                new DefaultTrackSelector(videoTrackSelectionFactory);

        // 2. Create a default LoadControl
        LoadControl loadControl = new DefaultLoadControl();
        // 3. Create the player
        player = ExoPlayerFactory.newSimpleInstance(this, trackSelector, loadControl);

        simpleExoPlayerView = new SimpleExoPlayerView(this);
        simpleExoPlayerView = (SimpleExoPlayerView) findViewById(R.id.exoplayer);
        /* Set media controller */
        simpleExoPlayerView.setUseController(true);
        simpleExoPlayerView.requestFocus();

        /*Bind the player to the view*/
        simpleExoPlayerView.setPlayer(player);
        simpleExoPlayerView.setKeepScreenOn(true);

        // Produces DataSource instances through which media data is loaded.
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "ExoPlayer"));

        // Produces Extractor instances for parsing the media data.
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();

        // This is the MediaSource representing the media to be played.
        MediaSource videoSource = new ExtractorMediaSource(Uri.parse(path),
                dataSourceFactory, extractorsFactory, null, null);

        /* 초기 AspectRatioFrameLayout Height 설정 */
        aspectRatioFrameLayout = simpleExoPlayerView.findViewById(R.id.exo_content_frame);
        ViewGroup.LayoutParams params = aspectRatioFrameLayout.getLayoutParams();
        params.height = presenter.getDeviceHeight(VideoViewerActivity.this) / 3;
        aspectRatioFrameLayout.setLayoutParams(params);

        /* ExoPlayer Listener */
        player.addListener(new Player.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest) {
                Log.v(TAG, "Listener-onTimelineChanged..." + timeline.getPeriodCount());

            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {

            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
                Log.v(TAG, "Listener-onTracksChanged...");

            }

            @Override
            public void onLoadingChanged(boolean isLoading) {
                Log.v(TAG, "Listener-onLoadingChanged...");

            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                Log.v(TAG, "Listener-onPlayerStateChanged..." + playbackState);
            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                Log.v(TAG, "Listener-onPlayerError...");
                player.stop();
                player.setPlayWhenReady(true);

            }

            @Override
            public void onPositionDiscontinuity() {
                Log.v(TAG, "Listener-onPositionDiscontinuity...");

            }
        });

        // Prepare the player with the source.
        player.prepare(videoSource);
        player.setPlayWhenReady(true);
    }

    private void initCustomExoPlayerUI() {
        controlView = simpleExoPlayerView.findViewById(R.id.exo_controller);

        btnLike = controlView.findViewById(R.id.btn_like); /* Orientation Change Button */
        btnLike.setOnClickListener(v-> {
            presenter.like(VideoViewerActivity.this, videoId);
        });

        btnOrientation = controlView.findViewById(R.id.btn_orientation); /* Orientation Change Button */
        btnOrientation.setOnClickListener(v -> {
            presenter.changeMode();
        });

        btnBack = controlView.findViewById(R.id.btn_back); /* Back Button */
        btnBack.setOnClickListener(v-> {
            finish();
        });

        tvViewerCount = controlView.findViewById(R.id.tv_viewer_count); /* Viewer Count */
        tvViewerCount.setText(String.valueOf(viewCount));

        tvSubject = controlView.findViewById(R.id.tv_subject); /* Viewer Count */
        tvSubject.setText(String.valueOf(subject));
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        /* Custom ExoPlayer : ExoPlayer는 화면 전체를 차지하게 하여 비디오 어디든 클릭 시 control view를 나타날 수 있게 처리함 */
        /* 단, Portrait 시 height 값은 화면의 3분의 1 정도로만 차지하도록 하여 보는데 부담이 없도록 처리함 */
        /* like AfreecaTv */
        changeAspectRatioFrameLayout(newConfig.orientation);
    }

    private void changeAspectRatioFrameLayout(int orientation) {
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            simpleExoPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);

            aspectRatioFrameLayout = simpleExoPlayerView.findViewById(R.id.exo_content_frame);
            ViewGroup.LayoutParams params = aspectRatioFrameLayout.getLayoutParams();
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            aspectRatioFrameLayout.setLayoutParams(params);
        } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            simpleExoPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);

            aspectRatioFrameLayout = simpleExoPlayerView.findViewById(R.id.exo_content_frame);
            ViewGroup.LayoutParams params = aspectRatioFrameLayout.getLayoutParams();
            params.height = presenter.getDeviceHeight(VideoViewerActivity.this) / 3;
            aspectRatioFrameLayout.setLayoutParams(params);
        }
    }


    @Override
    public void changeMode() {
        if (expand) {
            expand = false;
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // 세로전환

            btnOrientation.setBackgroundResource(R.drawable.ic_collapse);
        } else {
            expand = true;
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); // 가로전환

            btnOrientation.setBackgroundResource(R.drawable.ic_expand);
        }
    }

    /* Life Cycle */
    @Override
    protected void onDestroy() {
        super.onDestroy();

        player.release();
        RxBus.get().unregister(this);
        presenter.detachView(this);
    }

    @Override
    public void onRenderedFirstFrame() {

    }

    @Override
    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
        Toast.makeText(this, "dqd", Toast.LENGTH_SHORT).show();
        Logger.t("ViewerActivity").d("width : " + width + " , height : "  + height);
    }
}
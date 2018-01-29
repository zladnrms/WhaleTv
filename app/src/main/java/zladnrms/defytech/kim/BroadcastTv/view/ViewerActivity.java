package zladnrms.defytech.kim.BroadcastTv.view;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.google.android.exoplayer2.source.LoopingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlaybackControlView;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.gson.JsonSyntaxException;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.orhanobut.logger.Logger;

import zladnrms.defytech.kim.BroadcastTv.contract.ViewerContract;
import zladnrms.defytech.kim.BroadcastTv.eventbus.BroadcastStatusChangeEvent;
import zladnrms.defytech.kim.BroadcastTv.eventbus.ViewerCountEvent;
import zladnrms.defytech.kim.BroadcastTv.netty.Client.NettyClient;
import zladnrms.defytech.kim.BroadcastTv.networking.CheckNetworkStatus;
import zladnrms.defytech.kim.BroadcastTv.packet.ChatPacket;
import zladnrms.defytech.kim.BroadcastTv.packet.EntryPacket;
import zladnrms.defytech.kim.BroadcastTv.R;
import zladnrms.defytech.kim.BroadcastTv.adapter.ChatListAdapter;
import zladnrms.defytech.kim.BroadcastTv.databinding.ActivityViewerBinding;
import zladnrms.defytech.kim.BroadcastTv.eventbus.BookmarkEvent;
import zladnrms.defytech.kim.BroadcastTv.eventbus.ChangeSubjectEvent;
import zladnrms.defytech.kim.BroadcastTv.eventbus.EndingEvent;
import zladnrms.defytech.kim.BroadcastTv.eventbus.RxBus;
import zladnrms.defytech.kim.BroadcastTv.model.domain.ChatInfo;
import zladnrms.defytech.kim.BroadcastTv.presenter.ViewerPresenter;

public class ViewerActivity extends AppCompatActivity implements ViewerContract.View, SimpleExoPlayer.VideoListener {

    /* Data binding */
    private ActivityViewerBinding binding;

    /* presenter */
    private ViewerPresenter presenter;

    private ChatListAdapter rv_adapter;

    /* RoomInfo Data */
    private int roomId;
    private String viewerList = "시청자";
    private String streamerId = "아이디";
    private String streamerNickname = "별칭";
    private String subject;

    //private String hlsUrl = "http://115.71.238.61/hls/";
    private String hlsUrl = "http://52.79.108.8/hls/";
    private String streamingUrl;

    /* Bookmark */
    private boolean bookmark = false;

    /* VideoView */
    private boolean expand = false;
    private int width, height;

    /* Keyboard Control For Chatting */
    private InputMethodManager imm;

    /* Netty (For Paceket Sending) */
    private NettyClient nc = NettyClient.getInstance();

    /* Connect Flag For EntryPacket Only OneTime Send */
    private boolean connectFlag = false;

    /* ExoPlayer and Custom */
    private static final String TAG = "MainActivity";
    private SimpleExoPlayerView simpleExoPlayerView;
    private SimpleExoPlayer player;
    private AspectRatioFrameLayout aspectRatioFrameLayout;
    private PlaybackControlView controlView;
    private Button btnBookmark, btnLike, btnOrientation, btnBack;
    private TextView tvViewerCount, tvSubject;


    /* Network Change */
    private BroadcastReceiver mReceiver;
    private boolean networkCheck = false; /* 액티비티 첫 시작 시 바로 receiver 작동하는 것 방지 */

    /* Viewer Refresh Timer */
    /* 30초 미만 방송 : 녹화 X, 30초 이상 방송 : 녹화 O */
    private int viewTime = 0;
    private AsyncTask<Void, Void, Void> viewTimerTask;
    private volatile boolean viewTimerRunning = false;

    /* Chat Layout Toggle */
    private boolean toggleFlag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_viewer);

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_viewer);

        presenter = new ViewerPresenter();
        presenter.attachView(this);

        Intent intent = getIntent();
        roomId = intent.getIntExtra("roomId", -1);
        subject = intent.getStringExtra("subject");
        viewerList = intent.getStringExtra("viewer");
        streamerId = intent.getStringExtra("streamerId");
        streamerNickname = intent.getStringExtra("streamerNickname");

        presenter.saveUserRoomId(ViewerActivity.this, roomId);

        /* set Chat Layout */
        initChatLayout();

        /* Set ExoPlayer and Listener */
        initExoPlayer();

        /* ExoPlayer Control By Custom View */
        initCustomExoPlayerUI();

        /* Getting Bookmark Status*/
        presenter.getBookmark(ViewerActivity.this);

        /* Register Event */
        RxBus.get().register(this);

        /* Set Network Status Receiver*/
        setReceiver();
    }

    private void initChatLayout() {
        /* Chatting Listview Adapter */
        rv_adapter = new ChatListAdapter(ViewerActivity.this);
        LinearLayoutManager verticalLayoutmanager = new LinearLayoutManager(ViewerActivity.this, LinearLayoutManager.VERTICAL, false);
        binding.rvChatList.setLayoutManager(verticalLayoutmanager);
        binding.rvChatList.setAdapter(rv_adapter);

        /* Init Chat Layout Height By Device */
        changeChatLayoutHeight(3);

        binding.ivChatToggle.setOnClickListener(v -> {
            if(toggleFlag) {
                toggleFlag = false;
                binding.ivChatToggle.setImageResource(R.drawable.ic_chevron_up_white);
                changeChatLayoutHeight(5);
            } else {
                toggleFlag = true;
                binding.ivChatToggle.setImageResource(R.drawable.ic_chevron_down_white);
                changeChatLayoutHeight(3);
            }
        });

        /* Chatting Button */
        binding.btnChatSend.setOnClickListener(v -> {
                    String chat = binding.etChat.getText().toString();

                    ChatPacket chatPacket = new ChatPacket(roomId, presenter.getUserNickname(ViewerActivity.this), chat);
                    nc.send(2, chatPacket);

                    Logger.d("onBtnSend" + roomId);

                    binding.etChat.setText(null);

                    hideKeyboard(binding.etChat);
                }
        );

        binding.etChat.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_SEND:
                        binding.btnChatSend.performClick();
                        break;
                    default:
                        // 기본 엔터키 동작
                        return false;
                }
                return true;
            }
        });
    }

    private void initExoPlayer() {
        /* 1. Create a default TrackSelector */
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

        /* 2. Create a default LoadControl */
        LoadControl loadControl = new DefaultLoadControl();

        /* 3. Create the player */
        player = ExoPlayerFactory.newSimpleInstance(this, trackSelector, loadControl);

        simpleExoPlayerView = new SimpleExoPlayerView(this);
        simpleExoPlayerView = (SimpleExoPlayerView) findViewById(R.id.exoplayer);
        /* Set media controller */
        simpleExoPlayerView.setUseController(true);
        simpleExoPlayerView.requestFocus();

        /*Bind the player to the view*/
        simpleExoPlayerView.setPlayer(player);
        simpleExoPlayerView.setKeepScreenOn(true);
        //VIDEO FROM SD CARD: ( 2 steps. set up file and streamingUrl, then change videoSource to get the file)
        //        String urimp4 = "streamingUrl/FileName.mp4"; //upload file to device and add streamingUrl/name.mp4
        //        Uri mp4VideoUri = Uri.parse(Environment.getExternalStorageDirectory().getAbsolutePath()+urimp4);

        // building cam live stream link:
        streamingUrl = hlsUrl + streamerId + ".m3u8";
        Uri mp4VideoUri = Uri.parse(streamingUrl);

        // Measures bandwidth during playback. Can be null if not required.
        DefaultBandwidthMeter bandwidthMeterA = new DefaultBandwidthMeter();

        //Produces DataSource instances through which media data is loaded.
        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "exoplayer2example"), bandwidthMeterA);

        //Produces Extractor instances for parsing the media data.
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();

        //FOR SD CARD SOURCE:
        //MediaSource videoSource = new ExtractorMediaSource(mp4VideoUri, dataSourceFactory, extractorsFactory, null, null);
        //
        // FOR LIVESTREAM LINK:
        MediaSource videoSource = new HlsMediaSource(mp4VideoUri, dataSourceFactory, 1, null, null);
        final LoopingMediaSource loopingSource = new LoopingMediaSource(videoSource);

        player.prepare(loopingSource);

        /* 초기 AspectRatioFrameLayout Height 설정 */
        aspectRatioFrameLayout = simpleExoPlayerView.findViewById(R.id.exo_content_frame);
        ViewGroup.LayoutParams params = aspectRatioFrameLayout.getLayoutParams();
        params.height = presenter.getDeviceHeight(ViewerActivity.this) / 3;
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
                player.prepare(loopingSource);
                player.setPlayWhenReady(true);

            }

            @Override
            public void onPositionDiscontinuity() {
                Log.v(TAG, "Listener-onPositionDiscontinuity...");

            }
        });

        player.setPlayWhenReady(true);
    }

    private void initCustomExoPlayerUI() {
        /* ExoPlayer Control By Custom View */
        controlView = simpleExoPlayerView.findViewById(R.id.exo_controller);

        btnBookmark = controlView.findViewById(R.id.btn_bookmark); /* Bookmark button */
        btnBookmark.setOnClickListener(v -> {
            if (bookmark) {
                presenter.delBookmark(ViewerActivity.this, streamerNickname);
            } else {
                presenter.addBookmark(ViewerActivity.this, streamerNickname);
            }
        });

        btnLike = controlView.findViewById(R.id.btn_like); /* Orientation Change Button */
        btnLike.setOnClickListener(v-> {
            presenter.like(ViewerActivity.this, streamerNickname);
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

        tvSubject = controlView.findViewById(R.id.tv_subject); /* Viewer Count */
        tvSubject.setText(String.valueOf(subject));
    }

    private void setReceiver() {
        IntentFilter intentfilter = new IntentFilter();
        intentfilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        //동적 리시버 구현
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (CheckNetworkStatus.isConnectedToNetwork(context)) {
                    showCustomToast("상태 : " + CheckNetworkStatus.isConnectedWifiOrOther(context), Toast.LENGTH_SHORT);
                    if (networkCheck) {
                        finish();
                        startActivity(getIntent());
                    }
                    networkCheck = true;
                } else {
                    showCustomToast("인터넷 연결 상태를 확인해주세요.", Toast.LENGTH_SHORT);
                    if (networkCheck) {
                        finish();
                        startActivity(getIntent());
                    }
                    networkCheck = true;
                }
            }
        };
        registerReceiver(mReceiver, intentfilter);
    }

    @Override
    public void addChat(ChatInfo chatInfo) {
        rv_adapter.add(chatInfo);
    }

    @Override
    public void refresh() {
        rv_adapter.refresh();
    }

    @Override
    public void clear() {
        rv_adapter.clear();
    }

    @Override
    public int getUserRoomId() {
        return presenter.getUserRoomId(ViewerActivity.this);
    }

    @Override
    public String getUserNickname() {
        return presenter.getUserNickname(ViewerActivity.this);
    }

    @Override
    public void bookmarkrefresh() {
        if (bookmark) {
            bookmark = false;
            btnBookmark.setBackgroundResource(R.drawable.ic_bookmark_no);
        } else {
            bookmark = true;
            btnBookmark.setBackgroundResource(R.drawable.ic_bookmark_yes);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        /* Custom ExoPlayer : ExoPlayer는 화면 전체를 차지하게 하여 비디오 어디든 클릭 시 control view를 나타날 수 있게 처리함 */
        /* 단, Portrait 시 height 값은 화면의 3분의 1 정도로만 차지하도록 하여 보는데 부담이 없도록 처리함 */
        /* like AfreecaTv */
        changeAspectRatioFrameLayout(newConfig.orientation);

        /* orientation 변화로 인한 height 변경에 따른 채팅창 높이 변경 */
        changeChatLayoutHeight(3);
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
            params.height = presenter.getDeviceHeight(ViewerActivity.this) / 3;
            aspectRatioFrameLayout.setLayoutParams(params);
        }
    }

    private void changeChatLayoutHeight(int divide) {
        /* Set Height of Chat Layout*/
        ViewGroup.LayoutParams params = binding.layoutChat.getLayoutParams();
        // Changes the height and width to the specified *pixels*
        params.height = presenter.getDeviceHeight(ViewerActivity.this) / divide;
        binding.layoutChat.setLayoutParams(params);
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

    @Subscribe(tags = @Tag("viewer"))
    public void subscribe(Object object) {
        try {
            Logger.d(object);
            if (object instanceof ChatInfo) {
                presenter.addChat((ChatInfo) object);
                presenter.refresh();
                binding.rvChatList.smoothScrollToPosition(rv_adapter.getItemCount());
            } else if (object instanceof BookmarkEvent) { /* 자신의 Bookmark 목록을 가져와 지금의 Streamer 닉네임과 하나하나 비교 */

                Logger.d("클라: " + streamerNickname + ", 서버받아온거 : " +((BookmarkEvent) object).getNickname().equals(streamerNickname));
                if (((BookmarkEvent) object).getNickname().equals(streamerNickname)) {
                    presenter.bookmarkrefresh();
                }
            } else if (object instanceof EndingEvent) {
                if (((EndingEvent) object).getRoomId() == roomId && ((EndingEvent) object).getNickname().equals(streamerNickname)) {
                    showCustomToast("방송이 종료되었습니다", Toast.LENGTH_SHORT);
                    finish();
                }
            } else if (object instanceof ChangeSubjectEvent) { /* 방 제목 변경 */
                ChangeSubjectEvent cse = (ChangeSubjectEvent) object;

                tvSubject.setText(String.valueOf(cse.getSubject())); /* 숫자만 있을수도 있어서 String.valueOf() 로 처리 */
            } else if (object instanceof BroadcastStatusChangeEvent) { /* 방송 중단 or 재개 */
                BroadcastStatusChangeEvent bscEvent = (BroadcastStatusChangeEvent) object;

                switch (bscEvent.getStatus()) {
                    case 0:
                        binding.layoutCastStop.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        binding.layoutCastStop.setVisibility(View.GONE);
                        break;
                }
            } else if (object instanceof ViewerCountEvent) { /* 방송 중단 or 재개 */
                ViewerCountEvent vcEvent = (ViewerCountEvent) object;
                tvViewerCount.setText(Integer.toString(vcEvent.getViewerCount()));
            } else {
                Logger.t("ViewerActivity").d("Have not matched class : " + object.getClass() + ", " + object.toString());
            }
        } catch (JsonSyntaxException e) {
            Logger.d("오류발생");
            e.printStackTrace();
        }
    }

    /* Life Cycle */

    @Override
    protected void onStart() {
        super.onStart();
        Logger.t("ViewerActivity - OnStart").d("Start");

        if (!connectFlag) {
            EntryPacket entryPacket = new EntryPacket(presenter.getUserRoomId(ViewerActivity.this), presenter.getUserNickname(ViewerActivity.this), 0);
            nc.send(0, entryPacket);

            connectFlag = true;
        }

        viewTimerRunning = true;
        viewTimerTask = new viewTimerTask();
        viewTimerTask.execute();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        viewTimerTask.cancel(true);
        viewTimerTask = null;
        System.gc();

        Logger.t("ViewerActivity - OnDestory").d("Destroy");

        player.release();

        if (connectFlag) {
            EntryPacket entryPacket = new EntryPacket(presenter.getUserRoomId(ViewerActivity.this), presenter.getUserNickname(ViewerActivity.this), 1);
            nc.send(1, entryPacket);
        }

        unregisterReceiver(mReceiver);
        RxBus.get().unregister(this);
        presenter.detachView(this);
    }

    /* E.T.C */
    private void showCustomToast(String msg, int duration) {
        //Retrieve the layout inflator
        LayoutInflater inflater = getLayoutInflater();
        //Assign the custom layout to view
        //Parameter 1 - Custom layout XML
        //Parameter 2 - Custom layout ID present in linearlayout tag of XML
        View layout = inflater.inflate(R.layout.custom_toast, (ViewGroup) findViewById(R.id.llayout_custom_toast));
        TextView msgView = (TextView) layout.findViewById(R.id.tv_toast);
        msgView.setText(msg);
        //Return the application context
        Toast toast = new Toast(getApplicationContext());
        ////Set toast gravity to bottom
        //toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        //Set toast duration
        toast.setDuration(duration);
        //Set the custom layout to Toast
        toast.setView(layout);
        //Display toast
        toast.show();
    }

    @Override
    public void onRenderedFirstFrame() {

    }

    @Override
    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
        Logger.t("ViewerActivity").d("width : " + width + " , height : "  + height);
    }

    public void hideKeyboard(EditText et) {
        imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
    }

    /* get Viewer Count per 30 seconds*/
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            viewTime ++;
        }
    };

    private class viewTimerTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {

            while(viewTimerRunning) {
                try {
                    if(isCancelled()) {
                        viewTimerRunning = false;
                    }
                    if(viewTime >= 30) {
                        viewTime = 0;
                        presenter.getViewerCount(ViewerActivity.this, roomId);
                    }
                    handler.sendMessage(handler.obtainMessage());
                    Thread.sleep(1000);
                } catch (Throwable t) {
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aNull) {
            super.onPostExecute(aNull);
        }

        @Override
        protected void onCancelled() {
            Logger.d("취소됨");
            viewTimerRunning = false;
        }
    }
}
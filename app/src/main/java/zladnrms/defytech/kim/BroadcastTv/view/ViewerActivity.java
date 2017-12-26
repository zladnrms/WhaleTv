package zladnrms.defytech.kim.BroadcastTv.view;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.LoopingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveVideoTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.gson.JsonSyntaxException;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.orhanobut.logger.Logger;

import zladnrms.defytech.kim.BroadcastTv.Contract.ViewerContract;
import zladnrms.defytech.kim.BroadcastTv.GlobalApplication;
import zladnrms.defytech.kim.BroadcastTv.Netty.Client.NettyClient;
import zladnrms.defytech.kim.BroadcastTv.Packet.ChangeSubjectPacket;
import zladnrms.defytech.kim.BroadcastTv.Packet.ChatPacket;
import zladnrms.defytech.kim.BroadcastTv.Packet.EntryPacket;
import zladnrms.defytech.kim.BroadcastTv.R;
import zladnrms.defytech.kim.BroadcastTv.adapter.ChatListAdapter;
import zladnrms.defytech.kim.BroadcastTv.databinding.ActivityViewerBinding;
import zladnrms.defytech.kim.BroadcastTv.eventbus.BookmarkEvent;
import zladnrms.defytech.kim.BroadcastTv.eventbus.ChangeSubjectEvent;
import zladnrms.defytech.kim.BroadcastTv.eventbus.EndingEvent;
import zladnrms.defytech.kim.BroadcastTv.eventbus.RxBus;
import zladnrms.defytech.kim.BroadcastTv.model.domain.ChatInfo;
import zladnrms.defytech.kim.BroadcastTv.presenter.ViewerPresenter;

public class ViewerActivity extends AppCompatActivity implements ViewerContract.View {

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
    private boolean expand = true;
    private int width, height;

    /* Keyboard Control For Chatting */
    private InputMethodManager imm;

    /* Netty (For Paceket Sending) */
    private NettyClient nc = NettyClient.getInstance();

    /* Connect Flag For EntryPacket Only OneTime Send */
    private boolean connectFlag = false;

    /* ExoPlayer */

    private static final String TAG = "MainActivity";
    private SimpleExoPlayerView simpleExoPlayerView;
    private SimpleExoPlayer player;
    private ExoPlayer.EventListener exoPlayerEventListener;

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
        binding.tvSubject.setText(subject);

        presenter.saveUserRoomId(ViewerActivity.this, roomId);

        /* Chatting Listview Adapter */
        rv_adapter = new ChatListAdapter(ViewerActivity.this);
        LinearLayoutManager verticalLayoutmanager = new LinearLayoutManager(ViewerActivity.this, LinearLayoutManager.VERTICAL, false);
        binding.rvChatList.setLayoutManager(verticalLayoutmanager);
        binding.rvChatList.setAdapter(rv_adapter);

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

        /* ExoPlayer Setting */

        /* 1. Create a default TrackSelector */
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveVideoTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

        /* 2. Create a default LoadControl */
        LoadControl loadControl = new DefaultLoadControl();

        /* 3. Create the player */
        player = ExoPlayerFactory.newSimpleInstance(this, trackSelector, loadControl);

        simpleExoPlayerView = new SimpleExoPlayerView(this);
        simpleExoPlayerView = (SimpleExoPlayerView) findViewById(R.id.exoplayer);
        /* Set media controller */
        simpleExoPlayerView.setUseController(false);
        simpleExoPlayerView.requestFocus();

        /*Bind the player to the view*/
        simpleExoPlayerView.setPlayer(player);

//VIDEO FROM SD CARD: ( 2 steps. set up file and streamingUrl, then change videoSource to get the file)

//        String urimp4 = "streamingUrl/FileName.mp4"; //upload file to device and add streamingUrl/name.mp4
//        Uri mp4VideoUri = Uri.parse(Environment.getExternalStorageDirectory().getAbsolutePath()+urimp4);


//building cam live stream link:

        streamingUrl = hlsUrl + streamerId + ".m3u8";
        Uri mp4VideoUri = Uri.parse(streamingUrl);

// Measures bandwidth during playback. Can be null if not required.
        DefaultBandwidthMeter bandwidthMeterA = new DefaultBandwidthMeter();
//Produces DataSource instances through which media data is loaded.
        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "exoplayer2example"), bandwidthMeterA);
//Produces Extractor instances for parsing the media data.
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();

//FOR SD CARD SOURCE:
//        MediaSource videoSource = new ExtractorMediaSource(mp4VideoUri, dataSourceFactory, extractorsFactory, null, null);
//
// FOR LIVESTREAM LINK:
        MediaSource videoSource = new HlsMediaSource(mp4VideoUri, dataSourceFactory, 1, null, null);
        final LoopingMediaSource loopingSource = new LoopingMediaSource(videoSource);

        player.prepare(loopingSource);

        player.addListener(new ExoPlayer.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest) {
                Log.v(TAG, "Listener-onTimelineChanged..." + timeline.getPeriodCount());

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

        /* Toggle Bookmark */
        binding.btnBookmark.setOnClickListener(v -> {
            if (bookmark) {
                presenter.delBookmark(ViewerActivity.this, streamerNickname);
            } else {
                presenter.addBookmark(ViewerActivity.this, streamerNickname);
            }
        });

        /* Change Size */
        binding.btnChangesize.setOnClickListener(v -> {
            presenter.changeMode();
        });

        /* Getting Bookmark Status*/
        presenter.getBookmark(ViewerActivity.this);

        /* Register Event */
        RxBus.get().register(this);
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
            binding.btnBookmark.setBackgroundResource(R.drawable.ic_bookmark_no);
        } else {
            bookmark = true;
            binding.btnBookmark.setBackgroundResource(R.drawable.ic_bookmark_yes);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            simpleExoPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            simpleExoPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
        }
    }

    @Override
    public void changeMode() {
        if (expand) {
            expand = false;
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // 세로전환
            binding.btnChangesize.setBackgroundResource(R.drawable.ic_collapse);
        } else {
            expand = true;
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); // 가로전환
            binding.btnChangesize.setBackgroundResource(R.drawable.ic_expand);
        }
    }


    @Subscribe(tags = @Tag("viewer"))
    public void subscribe(Object object) {
        try {
            Logger.d(object);
            if (object instanceof ChatInfo) {
                Logger.d("Subscribe : ViewerActivity : ChatInfo");
                //Logger.d(((ChattingEvent) object).getChatInfo());
                Logger.d(((ChatInfo) object).getChat());
                presenter.addChat((ChatInfo)object);
                presenter.refresh();
                binding.rvChatList.smoothScrollToPosition(rv_adapter.getItemCount());
            } else if (object instanceof BookmarkEvent) { /* 자신의 Bookmark 목록을 가져와 지금의 Streamer 닉네임과 하나하나 비교 */
                Logger.d("Subscribe : ViewerActivity : BookmarkEvent");
                if (((BookmarkEvent) object).getNickname().equals(streamerNickname)) {
                    presenter.bookmarkrefresh();
                }
            } else if (object instanceof EndingEvent) {
                if(((EndingEvent) object).getRoomId() == roomId && ((EndingEvent) object).getNickname().equals(streamerNickname)) {
                    showCustomToast("방송이 종료되었습니다", Toast.LENGTH_SHORT);
                    finish();
                }
            }  else if(object instanceof ChangeSubjectEvent){ /* 방 제목 변경 */
                ChangeSubjectEvent cse = (ChangeSubjectEvent) object;

                binding.tvSubject.setText(String.valueOf(cse.getSubject())); /* 숫자만 있을수도 있어서 String.valueOf() 로 처리 */
            } else {
                Logger.t("ViewerActivity").d("Have not matched class : " + object.getClass() +", " + object.toString());
            }
        }catch (JsonSyntaxException e) {
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Logger.t("ViewerActivity - OnDestory").d("Destroy");

        player.release();

        if (connectFlag) {
            EntryPacket entryPacket = new EntryPacket(presenter.getUserRoomId(ViewerActivity.this), presenter.getUserNickname(ViewerActivity.this), 1);
            nc.send(1, entryPacket);
        }

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

    public void hideKeyboard(EditText et) {
        imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
    }
}
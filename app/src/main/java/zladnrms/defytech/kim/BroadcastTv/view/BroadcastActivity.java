package zladnrms.defytech.kim.BroadcastTv.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.faucamp.simplertmp.RtmpHandler;
import com.google.gson.JsonSyntaxException;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.orhanobut.logger.Logger;
import com.seu.magicfilter.utils.MagicFilterType;

import net.ossrs.yasea.SrsCameraView;
import net.ossrs.yasea.SrsEncodeHandler;
import net.ossrs.yasea.SrsPublisher;
import net.ossrs.yasea.SrsRecordHandler;

import java.io.IOException;
import java.net.SocketException;

import zladnrms.defytech.kim.BroadcastTv.contract.BroadcastContract;
import zladnrms.defytech.kim.BroadcastTv.netty.Client.NettyClient;
import zladnrms.defytech.kim.BroadcastTv.networking.CheckNetworkStatus;
import zladnrms.defytech.kim.BroadcastTv.packet.ChangeSubjectPacket;
import zladnrms.defytech.kim.BroadcastTv.packet.ChatPacket;
import zladnrms.defytech.kim.BroadcastTv.packet.EndingPacket;
import zladnrms.defytech.kim.BroadcastTv.packet.EntryPacket;
import zladnrms.defytech.kim.BroadcastTv.R;
import zladnrms.defytech.kim.BroadcastTv.adapter.ChatListAdapter;
import zladnrms.defytech.kim.BroadcastTv.databinding.ActivityBroadcastBinding;
import zladnrms.defytech.kim.BroadcastTv.eventbus.ChangeSubjectEvent;
import zladnrms.defytech.kim.BroadcastTv.eventbus.RxBus;
import zladnrms.defytech.kim.BroadcastTv.model.domain.ChatInfo;
import zladnrms.defytech.kim.BroadcastTv.packet.StopPacket;
import zladnrms.defytech.kim.BroadcastTv.presenter.BroadcastPresenter;

public class BroadcastActivity extends AppCompatActivity implements BroadcastContract.View, RtmpHandler.RtmpListener,
        SrsRecordHandler.SrsRecordListener, SrsEncodeHandler.SrsEncodeListener {

    /* Data Binding */
    private ActivityBroadcastBinding binding;

    /* presenter */
    private BroadcastPresenter presenter;

    /* View For Chatting Set*/
    private ChatListAdapter rv_adapter;

    private static final String TAG = "BroadcastJavaCvActivity";

    /* Rtmp Variable */
    private boolean recording = false;
    private boolean mute = false;
    private int rotate = 1;
    private String baseUrl = "rtmp://52.79.108.8:1935/live/";
    private String rtmpUrl;
    private String recPath = Environment.getExternalStorageDirectory().getPath() + "/test.mp4";

    private SrsPublisher mPublisher;

    /* RoomInfo Data */
    private int viewerCount = 0;
    private boolean editSubject = false;

    /* Keyboard Control For Chatting */
    private InputMethodManager imm;

    /* Netty (For Paceket Sending) */
    private NettyClient nc = NettyClient.getInstance();

    /* Connect Flag For EntryPacket Only OneTime Send */
    private boolean connectFlag = false;

    /* Network Change */
    private BroadcastReceiver mReceiver;
    private boolean networkCheck = false; /* 액티비티 첫 시작 시 바로 receiver 작동하는 것 방지 */

    /* Video Record Timer */
    /* 30초 미만 방송 : 녹화 X, 30초 이상 방송 : 녹화 O */
    private int castTime = 0;
    private AsyncTask<Void, Void, Void> castTimerTask;
    private volatile boolean castTimerRunning = false;

    /* Broadcast Control */
    /* onStop : */
    private boolean castStop = false;

    public BroadcastActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_broadcast);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR); // response screen rotation event

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); /* 키보드 컨트롤 */

        binding = DataBindingUtil.setContentView(this, R.layout.activity_broadcast); /* Data Binding */

        presenter = new BroadcastPresenter(); /* Set Presenter */
        presenter.attachView(this);

        /* 방 설정 초기화 */
        binding.etSubject.setText(presenter.getUserNickname(BroadcastActivity.this) + "님의 방송");
        rtmpUrl = baseUrl + presenter.getUserId(); /* 송출 할 주소 */

        /* Chatting List */
        rv_adapter = new ChatListAdapter(BroadcastActivity.this);
        LinearLayoutManager verticalLayoutmanager = new LinearLayoutManager(BroadcastActivity.this, LinearLayoutManager.VERTICAL, false);
        binding.rvChatList.setLayoutManager(verticalLayoutmanager);
        binding.rvChatList.setAdapter(rv_adapter);

        /* 채팅 내용 전송 */
        binding.btnChatSend.setOnClickListener(v -> {
                    String chat = binding.etChat.getText().toString();

                    ChatPacket chatPacket = new ChatPacket(presenter.getUserRoomId(BroadcastActivity.this), presenter.getUserNickname(BroadcastActivity.this), chat);
                    nc.send(2, chatPacket);

                    binding.etChat.setText(null);

                    hideKeyboard(binding.etChat);
                }
        );

        /* 엔터 키 -> 보내기 키로 변경 */
        binding.etChat.setOnEditorActionListener((v, actionId, event) ->  {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_SEND:
                        binding.btnChatSend.performClick();
                        break;
                    default:
                        // 기본 엔터키 동작
                        return false;
                }
                return true;
        });

        /* Rtmp Publisher Initialize*/
        mPublisher = new SrsPublisher((SrsCameraView) findViewById(R.id.glsurfaceview_camera));
        mPublisher.setEncodeHandler(new SrsEncodeHandler(this));
        mPublisher.setRtmpHandler(new RtmpHandler(this));
        mPublisher.setRecordHandler(new SrsRecordHandler(this));
        mPublisher.setPreviewResolution(640, 360);
        mPublisher.setOutputResolution(360, 640);
        mPublisher.setVideoHDMode();
        mPublisher.startCamera();

        mPublisher.switchToSoftEncoder();

        /* 방송 버튼 */
        binding.cast.setOnClickListener(v -> {

            binding.btnRotate.setVisibility(View.GONE);
            String subject = binding.etSubject.getText().toString();
            String id = presenter.getUserId();
            String nickname = presenter.getUserNickname(BroadcastActivity.this);

            if (!recording) {
                binding.layoutChat.setVisibility(View.VISIBLE);
                /* 서버에 방 생성 및 방송 시작 */
                presenter.startBroadcast(subject, id, nickname);

                startRecording();
                presenter.pushBookmark(BroadcastActivity.this, presenter.getUserNickname(BroadcastActivity.this) + "님의 방송이 시작되었어요!");
                Log.w(TAG, "Start Button Pushed");
                binding.cast.setBackgroundResource(R.drawable.ic_record_stop);
            } else {
                stopRecording();
                Log.w(TAG, "Stop Button Pushed");
                binding.cast.setBackgroundResource(R.drawable.ic_record_start);
                finish();
            }
        });

        binding.btnSwCam.setOnClickListener(v -> {
            mPublisher.switchCameraFace((mPublisher.getCamraId() + 1) % Camera.getNumberOfCameras());
        });

        binding.btnSwMic.setOnClickListener(v -> {
            if (!mute) {
                mute = true;
                mPublisher.setSendAudioOnly(false);
                mPublisher.setSendVideoOnly(true);
                binding.btnSwMic.setBackgroundResource(R.drawable.ic_mute_on);
            } else {
                mute = false;
                mPublisher.setSendAudioOnly(false);
                mPublisher.setSendVideoOnly(false);
                binding.btnSwMic.setBackgroundResource(R.drawable.ic_mute_off);
            }
        });

        binding.btnRotate.setOnClickListener(v -> {
            int currentOrientation = this.getResources().getConfiguration().orientation;
            if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
                this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                Logger.t("Broadcast").d(currentOrientation);
            } else {
                this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                Logger.t("Broadcast").d(currentOrientation);
            }

            /*
            mPublisher.pauseRecord();
            switch (rotate) {
                case 1:
                    mPublisher.setScreenOrientation(rotate);
                    Logger.t("Broadcast").d(rotate);
                    rotate = 2;
                    break;
                case 2:
                    mPublisher.setScreenOrientation(rotate);
                    Logger.t("Broadcast").d(rotate);
                    rotate = 1;
                    break;
            }
            mPublisher.resumeRecord();
            */
        });

        /* 방 제목 설정 */
        binding.etSubject.setEnabled(false);
        binding.etSubject.setClickable(false);
        binding.etSubject.setFocusable(false);

        binding.btnEditSubject.setOnClickListener(v -> {
            if(!editSubject) {
                etSubjectLock(true);
                binding.etSubject.requestFocus();
                binding.etSubject.setSelection(binding.etSubject.getText().toString().length());
                binding.btnEditSubject.setVisibility(View.GONE);
                binding.btnEditSubjectSubmit.setVisibility(View.VISIBLE);
                showKeyboard(binding.etSubject);
            }
        });

        binding.btnEditSubjectSubmit.setOnClickListener(v-> {
            if(!binding.etSubject.getText().toString().equals("") && editSubject) {
                etSubjectLock(false);
                binding.btnEditSubject.setVisibility(View.VISIBLE);
                binding.btnEditSubjectSubmit.setVisibility(View.GONE);
                String subject = binding.etSubject.getText().toString();
                if (recording) { /* 방송 중일 경우엔 서버에도 전송 */
                    changeSubject(subject);
                }
                hideKeyboard(binding.etSubject);
            } else {
                Toast.makeText(getApplicationContext(), "방 제목을 입력해주세요", Toast.LENGTH_SHORT).show();
            }
        });

        /* 엔터 키 -> 보내기 키로 변경 */
        binding.etSubject.setOnEditorActionListener((v, actionId, event) ->  {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_DONE:
                        binding.btnEditSubjectSubmit.performClick();
                        break;
                    default:
                        // 기본 엔터키 동작
                        return false;
                }
                return true;
        });

        /*
        binding.record.setOnClickListener(v -> {
            if (binding.record.getText().toString().contentEquals("record")) {
                if (mPublisher.startRecord(recPath)) {
                    binding.record.setText("pause");
                }
            } else if (binding.record.getText().toString().contentEquals("pause")) {
                mPublisher.pauseRecord();
                binding.record.setText("resume");
            } else if (binding.record.getText().toString().contentEquals("resume")) {
                mPublisher.resumeRecord();
                binding.record.setText("pause");
            }
        });

        binding.swEnc.setOnClickListener(v -> {
            if (binding.swEnc.getText().toString().contentEquals("soft encoder")) {
                mPublisher.switchToSoftEncoder();
                binding.swEnc.setText("hard encoder");
            } else if (binding.swEnc.getText().toString().contentEquals("hard encoder")) {
                mPublisher.switchToHardEncoder();
                binding.swEnc.setText("soft encoder");
            }
        });
        */

        /* RxBus 설정 */
        RxBus.get().register(this);

         /* Set Network Status Receiver*/
        setReceiver();
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
                    networkCheck = true;
                } else {
                    showCustomToast("인터넷 연결 상태를 확인해주세요.", Toast.LENGTH_SHORT);
                    if (networkCheck) {
                        finish();
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
        return presenter.getUserRoomId(BroadcastActivity.this);
    }

    @Override
    public String getUserNickname() {
        return presenter.getUserNickname(BroadcastActivity.this);
    }

    @Override
    public void changeSubject(String subject) {
        presenter.changeSubject(BroadcastActivity.this, subject);
    }

    @Override
    public void changeSubjectCallback(int roomId, String subject) {
        ChangeSubjectPacket csePacket = new ChangeSubjectPacket(roomId, subject);
        nc.send(10, csePacket);

        binding.etSubject.setText(String.valueOf(subject)); /* 숫자만 있을수도 있어서 String.valueOf() 로 처리 */
    }

    @Override
    public void startBroadcastCallBack(int roomId) {
        presenter.saveUserRoomId(BroadcastActivity.this, roomId);
    }

    @Override
    public void etSubjectLock(boolean lock) {
        binding.etSubject.setEnabled(lock);
        binding.etSubject.setClickable(lock);
        binding.etSubject.setFocusable(lock);
        binding.etSubject.setFocusableInTouchMode(lock);
        editSubject = lock;
    }

    /* Event Bus */
    @Subscribe(tags = @Tag("broadcast"))
    public void subscribe(Object object) {

        if (object instanceof ChatInfo) {
            try {
                Logger.d("Subscribe : BroadcastJavaCvActivity");
                ChatInfo chatInfo = (ChatInfo) object;
                presenter.addChat(chatInfo);
                presenter.refresh();
                binding.rvChatList.smoothScrollToPosition(rv_adapter.getItemCount());

                viewerCount += presenter.setViewerCount(chatInfo.getNickname(), chatInfo.getChat()); /* 앞의 getNickname() = '알림' 포함 */
                binding.tvViewerCount.setText(String.valueOf(viewerCount));
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
            }
        } else if (object instanceof ChangeSubjectEvent) { /* 방 제목 변경 성공 -> EventBus로 받고 시청자들도 변경토록 Packet 전송*/
            ChangeSubjectEvent cse = (ChangeSubjectEvent) object;

            ChangeSubjectPacket csePacket = new ChangeSubjectPacket(cse.getRoomId(), cse.getSubject());
            nc.send(200, csePacket);

            binding.etSubject.setText(String.valueOf(cse.getSubject())); /* 숫자만 있을수도 있어서 String.valueOf() 로 처리 */
        }
    }

    /* Life Cycle */
    @Override
    protected void onStart() {
        super.onStart();

        if (!connectFlag) {
            EntryPacket entryPacket = new EntryPacket(presenter.getUserRoomId(BroadcastActivity.this), presenter.getUserNickname(BroadcastActivity.this), 0);
            nc.send(0, entryPacket);

            connectFlag = true;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        castStop = true;
        /* 방송 중단 패킷 전송 */
        StopPacket stopPacket = new StopPacket(presenter.getUserRoomId(BroadcastActivity.this), 50);
        nc.send(50, stopPacket);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(castStop) {
            StopPacket stopPacket = new StopPacket(presenter.getUserRoomId(BroadcastActivity.this), 51);
            nc.send(51, stopPacket);
        }
        binding.cast.setEnabled(true);
        mPublisher.resumeRecord();
    }

    @Override
    protected void onPause() {
        super.onPause();


        //EndingPacket endingPacket = new EndingPacket(presenter.getUserRoomId(BroadcastActivity.this), presenter.getUserNickname(BroadcastActivity.this), 101);
        //nc.send(101, endingPacket);
        //presenter.delBroadcastRoom(roomId, presenter.getUserId(), presenter.getUserNickname(BroadcastActivity.this));

        mPublisher.pauseRecord();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (connectFlag) {
            EndingPacket endingPacket = new EndingPacket(presenter.getUserRoomId(BroadcastActivity.this), presenter.getUserNickname(BroadcastActivity.this));
            nc.send(101, endingPacket);

            // 만약 방송중이었으면

            EntryPacket entryPacket = new EntryPacket(presenter.getUserRoomId(BroadcastActivity.this), presenter.getUserNickname(BroadcastActivity.this), 1);
            nc.send(1, entryPacket);
        }

        presenter.delBroadcastRoom(BroadcastActivity.this, recording, presenter.getUserRoomId(BroadcastActivity.this), presenter.getUserId(), presenter.getUserNickname(BroadcastActivity.this), castTime);

        recording = false;
        mPublisher.stopPublish();
        mPublisher.stopRecord();

        unregisterReceiver(mReceiver);
        RxBus.get().unregister(this);
        presenter.detachView(this);
    }

    // Start the capture
    public void startRecording() {

        /* 시간 재기 */
        castTimerRunning = true;
        castTimerTask = new castTimerTask();
        castTimerTask.execute();

        /* 방송 시작 */
        mPublisher.startPublish(rtmpUrl);
        mPublisher.startCamera();

        recording = true;

        /* 방송 상태 변경 */
        presenter.updateBroadcastStatus(presenter.getUserRoomId(BroadcastActivity.this));

    }

    public void stopRecording() {

        castTimerTask.cancel(true);
        castTimerTask = null;
        System.gc();

        // This should stop the audio thread from running
        mPublisher.stopPublish();
        mPublisher.stopRecord();

        if (recording) {
            recording = false;
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Quit when back button is pushed
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (recording) {
                stopRecording();
            }
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /* Rtmp Implements Method */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else {
            switch (id) {
                case R.id.cool_filter:
                    mPublisher.switchCameraFilter(MagicFilterType.COOL);
                    break;
                case R.id.beauty_filter:
                    mPublisher.switchCameraFilter(MagicFilterType.BEAUTY);
                    break;
                case R.id.early_bird_filter:
                    mPublisher.switchCameraFilter(MagicFilterType.EARLYBIRD);
                    break;
                case R.id.evergreen_filter:
                    mPublisher.switchCameraFilter(MagicFilterType.EVERGREEN);
                    break;
                case R.id.n1977_filter:
                    mPublisher.switchCameraFilter(MagicFilterType.N1977);
                    break;
                case R.id.nostalgia_filter:
                    mPublisher.switchCameraFilter(MagicFilterType.NOSTALGIA);
                    break;
                case R.id.romance_filter:
                    mPublisher.switchCameraFilter(MagicFilterType.ROMANCE);
                    break;
                case R.id.sunrise_filter:
                    mPublisher.switchCameraFilter(MagicFilterType.SUNRISE);
                    break;
                case R.id.sunset_filter:
                    mPublisher.switchCameraFilter(MagicFilterType.SUNSET);
                    break;
                case R.id.tender_filter:
                    mPublisher.switchCameraFilter(MagicFilterType.TENDER);
                    break;
                case R.id.toast_filter:
                    mPublisher.switchCameraFilter(MagicFilterType.TOASTER2);
                    break;
                case R.id.valencia_filter:
                    mPublisher.switchCameraFilter(MagicFilterType.VALENCIA);
                    break;
                case R.id.walden_filter:
                    mPublisher.switchCameraFilter(MagicFilterType.WALDEN);
                    break;
                case R.id.warm_filter:
                    mPublisher.switchCameraFilter(MagicFilterType.WARM);
                    break;
                case R.id.original_filter:
                default:
                    mPublisher.switchCameraFilter(MagicFilterType.NONE);
                    break;
            }
        }
        setTitle(item.getTitle());

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mPublisher.stopEncode();
        mPublisher.stopRecord();
        //binding.record.setText("record");
        mPublisher.setScreenOrientation(newConfig.orientation);
        if (binding.cast.getText().toString().contentEquals("stop")) {
            mPublisher.startEncode();
        }
        mPublisher.startCamera();
    }

    private void handleException(Exception e) {
        try {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            mPublisher.stopPublish();
            mPublisher.stopRecord();
            binding.cast.setText("publish");
            //binding.record.setText("record");
            //binding.swEnc.setEnabled(true);
        } catch (Exception e1) {
            //
        }
    }

    // Implementation of SrsRtmpListener.

    @Override
    public void onRtmpConnecting(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRtmpConnected(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRtmpVideoStreaming() {
    }

    @Override
    public void onRtmpAudioStreaming() {
    }

    @Override
    public void onRtmpStopped() {
        Toast.makeText(getApplicationContext(), "Stopped", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRtmpDisconnected() {
        Toast.makeText(getApplicationContext(), "Disconnected", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRtmpVideoFpsChanged(double fps) {
        Log.i(TAG, String.format("Output Fps: %f", fps));
    }

    @Override
    public void onRtmpVideoBitrateChanged(double bitrate) {
        int rate = (int) bitrate;
        if (rate / 1000 > 0) {
            Log.i(TAG, String.format("Video bitrate: %f kbps", bitrate / 1000));
        } else {
            Log.i(TAG, String.format("Video bitrate: %d bps", rate));
        }
    }

    @Override
    public void onRtmpAudioBitrateChanged(double bitrate) {
        int rate = (int) bitrate;
        if (rate / 1000 > 0) {
            Log.i(TAG, String.format("Audio bitrate: %f kbps", bitrate / 1000));
        } else {
            Log.i(TAG, String.format("Audio bitrate: %d bps", rate));
        }
    }

    @Override
    public void onRtmpSocketException(SocketException e) {
        handleException(e);
    }

    @Override
    public void onRtmpIOException(IOException e) {
        handleException(e);
    }

    @Override
    public void onRtmpIllegalArgumentException(IllegalArgumentException e) {
        handleException(e);
    }

    @Override
    public void onRtmpIllegalStateException(IllegalStateException e) {
        handleException(e);
    }

    // Implementation of SrsRecordHandler.

    @Override
    public void onRecordPause() {
        Toast.makeText(getApplicationContext(), "Record paused", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRecordResume() {
        Toast.makeText(getApplicationContext(), "Record resumed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRecordStarted(String msg) {
        Toast.makeText(getApplicationContext(), "Recording file: " + msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRecordFinished(String msg) {
        Toast.makeText(getApplicationContext(), "MP4 file saved: " + msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRecordIOException(IOException e) {
        handleException(e);
    }

    @Override
    public void onRecordIllegalArgumentException(IllegalArgumentException e) {
        handleException(e);
    }

    // Implementation of SrsEncodeHandler.

    @Override
    public void onNetworkWeak() {
        Toast.makeText(getApplicationContext(), "Network weak", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNetworkResume() {
        Toast.makeText(getApplicationContext(), "Network resume", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEncodeIllegalArgumentException(IllegalArgumentException e) {
        handleException(e);
    }


    /* Keyboard Control */
    private void hideKeyboard(EditText et) {
        imm.hideSoftInputFromWindow(et.getWindowToken(), 0);

    }

    private void showKeyboard(EditText et) {
        imm.showSoftInput(et, 0);
    }

    /* Back Key Control */
    @Override
    public void onBackPressed() {
        super.onBackPressed();

        showCustomToast("방송 중 백 버튼은 사용하실 수 없습니다", Toast.LENGTH_SHORT);
    }

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

    /* Set Timer */
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            castTime ++;
        }
    };

    private class castTimerTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {

            while(castTimerRunning) {
                try {
                    if(isCancelled()) {
                        castTimerRunning = false;
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
            castTimerRunning = false;
        }
    }
}

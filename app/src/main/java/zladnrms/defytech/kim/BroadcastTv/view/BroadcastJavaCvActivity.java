package zladnrms.defytech.kim.BroadcastTv.view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.nio.ShortBuffer;

import zladnrms.defytech.kim.BroadcastTv.contract.BroadcastJavaCvContract;
import zladnrms.defytech.kim.BroadcastTv.packet.EndingPacket;
import zladnrms.defytech.kim.BroadcastTv.R;
import zladnrms.defytech.kim.BroadcastTv.adapter.ChatListAdapter;
import zladnrms.defytech.kim.BroadcastTv.databinding.ActivityBroadcastJavacvBinding;
import zladnrms.defytech.kim.BroadcastTv.eventbus.RxBus;
import zladnrms.defytech.kim.BroadcastTv.model.domain.ChatInfo;
import zladnrms.defytech.kim.BroadcastTv.netty.Client.NettyClient;
import zladnrms.defytech.kim.BroadcastTv.packet.ChatPacket;
import zladnrms.defytech.kim.BroadcastTv.packet.EntryPacket;
import zladnrms.defytech.kim.BroadcastTv.presenter.BroadcastJavaCvPresenter;

import com.google.gson.JsonSyntaxException;
import com.googlecode.javacv.FFmpegFrameRecorder;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.orhanobut.logger.Logger;

import static com.googlecode.javacv.cpp.opencv_core.*;

public class BroadcastJavaCvActivity extends AppCompatActivity implements BroadcastJavaCvContract.View {

    /* Data Binding */
    private ActivityBroadcastJavacvBinding binding;

    /* presenter */
    private BroadcastJavaCvPresenter presenter;

    private String result_submit;

    /* View For Chatting Set*/
    private ChatListAdapter rv_adapter;

    // TCP/IP 전송 시 필요 객체
    private Handler handler = new Handler();

    /* Rtmp Streaming Variable */
    private final static String LOG_TAG = "MainActivity";

    private PowerManager.WakeLock mWakeLock;

    private String rtmpUrl = "rtmp://52.79.108.8:1935/live/";
    private String streamingUrl;
    //private String streamingUrl = "/mnt/sdcard/new_stream.flv";

    private volatile FFmpegFrameRecorder recorder;
    boolean recording = false;
    long startTime = 0;

    private int sampleAudioRateInHz = 44100;
    private int imageWidth = 2480; /* Width (해상도 Width) */
    private int imageHeight = 1240; /* Height (해상도 Height) */
    private int frameRate = 60;

    private Thread audioThread;
    volatile boolean runAudioThread = true;
    private AudioRecord audioRecord;
    private AudioRecordRunnable audioRecordRunnable;

    private CameraView cameraView;
    private IplImage yuvIplimage = null;

    /* RoomInfo Data */
    private int roomId = 0;
    private String streamerId = "아이디";
    private String streamerNickname = "별칭";
    private String subject;

    /* Keyboard Control For Chatting */
    private InputMethodManager imm;

    /* Netty (For Paceket Sending) */
    private NettyClient nc = NettyClient.getInstance();

    /* Connect Flag For EntryPacket Only OneTime Send */
    private boolean connectFlag = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_broadcast_javacv);

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_broadcast_javacv);

        presenter = new BroadcastJavaCvPresenter();
        presenter.attachView(this);

        Intent intent = getIntent();
        roomId = intent.getIntExtra("roomId", -1);
        subject = intent.getStringExtra("subject");
        streamerId = intent.getStringExtra("streamerId");
        streamerNickname = intent.getStringExtra("streamerNickname");

        presenter.saveUserRoomId(BroadcastJavaCvActivity.this, roomId);

        // Streaming할 주소
        streamingUrl = rtmpUrl + streamerId;

        rv_adapter = new ChatListAdapter(BroadcastJavaCvActivity.this);
        LinearLayoutManager verticalLayoutmanager = new LinearLayoutManager(BroadcastJavaCvActivity.this, LinearLayoutManager.VERTICAL, false);
        binding.rvChatList.setLayoutManager(verticalLayoutmanager);
        binding.rvChatList.setAdapter(rv_adapter);

        binding.btnChatSend.setOnClickListener(v -> {
                    String chat = binding.etChat.getText().toString();

                    ChatPacket chatPacket = new ChatPacket(roomId, presenter.getUserNickname(BroadcastJavaCvActivity.this), chat);
                    nc.send(2, chatPacket);

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

        RxBus.get().register(this);

        initLayout();
        initRecorder();
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
        return presenter.getUserRoomId(BroadcastJavaCvActivity.this);
    }

    @Override
    public String getUserNickname() {
        return presenter.getUserNickname(BroadcastJavaCvActivity.this);
    }

    @Subscribe(tags = @Tag("broadcast"))
    public void subscribe(ChatInfo chatInfo) {
        try {
            Logger.d("Subscribe : BroadcastJavaCvActivity");
            presenter.addChat(chatInfo);
            presenter.refresh();
            binding.rvChatList.smoothScrollToPosition(rv_adapter.getItemCount());
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
    }

    /* Life Cycle */

    @Override
    protected void onStart() {
        super.onStart();

        if (!connectFlag) {
            EntryPacket entryPacket = new EntryPacket(presenter.getUserRoomId(BroadcastJavaCvActivity.this), presenter.getUserNickname(BroadcastJavaCvActivity.this), 0);
            nc.send(0, entryPacket);

            connectFlag = true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mWakeLock == null) {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, LOG_TAG);
            mWakeLock.acquire();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        EndingPacket endingPacket = new EndingPacket(presenter.getUserRoomId(BroadcastJavaCvActivity.this), presenter.getUserNickname(BroadcastJavaCvActivity.this));
        nc.send(101, endingPacket);

        // 만약 방송중이었으면
        presenter.delBroadcastRoom(roomId, presenter.getUserId(), presenter.getUserNickname(BroadcastJavaCvActivity.this));

        if (mWakeLock != null) {
            mWakeLock.release();
            mWakeLock = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        recording = false;

        if (connectFlag) {
            EntryPacket entryPacket = new EntryPacket(presenter.getUserRoomId(BroadcastJavaCvActivity.this), presenter.getUserNickname(BroadcastJavaCvActivity.this), 1);
            nc.send(1, entryPacket);
        }

        RxBus.get().unregister(this);
        presenter.detachView(this);
    }

    /* For Streaming */

    private void initLayout() {

        binding.recorderFlash.setOnClickListener(v -> cameraView.flashOnOff());

        //recordButton.setText("Start");
        binding.recorderButton.setOnClickListener(v -> {

                    AlertDialog.Builder builder = new AlertDialog.Builder(BroadcastJavaCvActivity.this);
                    builder.setTitle("방송");
                    if (recording) {
                        builder.setMessage("종료?");
                    } else {
                        builder.setMessage("시작?");
                    }

                    String positiveText = "확인";
                    builder.setPositiveButton(positiveText,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (!recording) {
                                        presenter.pushBookmark(BroadcastJavaCvActivity.this, presenter.getUserNickname(BroadcastJavaCvActivity.this) + "님의 방송이 시작되었어요!");

                                        startRecording();
                                        Log.w(LOG_TAG, "Start Button Pushed");
                                        binding.recorderButton.setBackgroundResource(R.drawable.ic_record_stop);
                                    } else {
                                        stopRecording();
                                        Log.w(LOG_TAG, "Stop Button Pushed");
                                        binding.recorderButton.setBackgroundResource(R.drawable.ic_record_start);
                                        //recordButton.setText("Start");
                                        finish();
                                    }
                                }
                            });

                    String negativeText = "취소";
                    builder.setNegativeButton(negativeText,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });

                    AlertDialog dialog = builder.create();
                    // display dialog
                    dialog.show();
                }
        );

        cameraView = new CameraView(this);

        /* 화면 크기 구하기 */
        Point p = new Point();
        Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        display.getSize(p);

        int screenWidth = p.x;
        int screenHeight = p.y;

        LinearLayout.LayoutParams layoutParam = new LinearLayout.LayoutParams(screenWidth, screenHeight); // 폰 사이즈에 맞춰서 변경
        binding.recordLayout.addView(cameraView, layoutParam);
        Log.v(LOG_TAG, "added cameraView to mainLayout");
    }

    private void initRecorder() {
        Log.w(LOG_TAG, "initRecorder");

        if (yuvIplimage == null) {
            // Recreated after frame size is set in surface change method
            yuvIplimage = IplImage.create(imageWidth, imageHeight, IPL_DEPTH_8U, 2);
            //yuvIplimage = IplImage.create(imageWidth, imageHeight, IPL_DEPTH_32S, 2);

            Log.v(LOG_TAG, "IplImage.create");
        }

        recorder = new FFmpegFrameRecorder(streamingUrl, imageWidth, imageHeight, 1);
        Log.v(LOG_TAG, "FFmpegFrameRecorder: " + streamingUrl + " imageWidth: " + imageWidth + " imageHeight " + imageHeight);

        recorder.setFormat("flv");
        Log.v(LOG_TAG, "recorder.setFormat(\"flv\")");

        recorder.setSampleRate(sampleAudioRateInHz);
        Log.v(LOG_TAG, "recorder.setSampleRate(sampleAudioRateInHz)");

        // re-set in the surface changed method as well
        recorder.setFrameRate(frameRate);
        Log.v(LOG_TAG, "recorder.setFrameRate(frameRate)");

        // Create audio recording thread
        audioRecordRunnable = new AudioRecordRunnable();
        audioThread = new Thread(audioRecordRunnable);
    }

    // Start the capture
    public void startRecording() {
        try {
            recorder.start();
            startTime = System.currentTimeMillis();
            recording = true;
            audioThread.start();

            presenter.updateBroadcastStatus(presenter.getUserRoomId(BroadcastJavaCvActivity.this));

        } catch (FFmpegFrameRecorder.Exception e) {
            e.printStackTrace();
        }
    }

    public void stopRecording() {
        // This should stop the audio thread from running
        runAudioThread = false;

        if (recorder != null && recording) {
            recording = false;
            Log.v(LOG_TAG, "Finishing recording, calling stop and release on recorder");
            try {
                recorder.stop();
                recorder.release();
            } catch (FFmpegFrameRecorder.Exception e) {
                e.printStackTrace();
            }
            recorder = null;
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

    //---------------------------------------------
    // audio thread, gets and encodes audio data
    //---------------------------------------------
    class AudioRecordRunnable implements Runnable {

        @Override
        public void run() {
            // Set the thread priority
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);

            // Audio
            int bufferSize;
            short[] audioData;
            int bufferReadResult;

            bufferSize = AudioRecord.getMinBufferSize(sampleAudioRateInHz,
                    AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT);
            audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleAudioRateInHz,
                    AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize);

            audioData = new short[bufferSize];

            Log.d(LOG_TAG, "audioRecord.startRecording()");
            audioRecord.startRecording();

            // Audio Capture/Encoding Loop
            while (runAudioThread) {
                // Read from audioRecord
                bufferReadResult = audioRecord.read(audioData, 0, audioData.length);
                if (bufferReadResult > 0) {
                    //Log.v(LOG_TAG,"audioRecord bufferReadResult: " + bufferReadResult);

                    // Changes in this variable may not be picked up despite it being "volatile"
                    if (recording) {
                        try {
                            // Write to FFmpegFrameRecorder
                            recorder.record(ShortBuffer.wrap(audioData, 0, bufferReadResult));
                        } catch (FFmpegFrameRecorder.Exception e) {
                            Log.v(LOG_TAG, e.getMessage());
                            e.printStackTrace();
                        }
                    }
                }
            }
            Log.v(LOG_TAG, "AudioThread Finished");

            /* Capture/Encoding finished, release recorder */
            if (audioRecord != null) {
                audioRecord.stop();
                audioRecord.release();
                audioRecord = null;
                Log.v(LOG_TAG, "audioRecord released");
            }
        }
    }

    class CameraView extends SurfaceView implements SurfaceHolder.Callback, PreviewCallback {

        private boolean previewRunning = false;

        private SurfaceHolder holder;
        private Camera camera;

        private byte[] previewBuffer;

        long videoTimestamp = 0;

        Bitmap bitmap;
        Canvas canvas;

        public CameraView(Context _context) {
            super(_context);

            holder = this.getHolder();
            holder.addCallback(this);
            holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            camera = Camera.open();

            try {
                camera.setPreviewDisplay(holder);
                camera.setPreviewCallback(this);

                Camera.Parameters currentParams = camera.getParameters();
                Log.v(LOG_TAG, "Preview Framerate: " + currentParams.getPreviewFrameRate());
                Log.v(LOG_TAG, "Preview imageWidth: " + currentParams.getPreviewSize().width + " imageHeight: " + currentParams.getPreviewSize().height);

                // Use these values
                imageWidth = currentParams.getPreviewSize().width;
                imageHeight = currentParams.getPreviewSize().height;
                frameRate = currentParams.getPreviewFrameRate();

                bitmap = Bitmap.createBitmap(imageWidth, imageHeight, Bitmap.Config.ALPHA_8);


	        	/*
                Log.v(LOG_TAG,"Creating previewBuffer size: " + imageWidth * imageHeight * ImageFormat.getBitsPerPixel(currentParams.getPreviewFormat())/8);
	        	previewBuffer = new byte[imageWidth * imageHeight * ImageFormat.getBitsPerPixel(currentParams.getPreviewFormat())/8];
				camera.addCallbackBuffer(previewBuffer);
	            camera.setPreviewCallbackWithBuffer(this);
	        	*/

                camera.startPreview();
                previewRunning = true;
            } catch (IOException e) {
                Log.v(LOG_TAG, e.getMessage());
                e.printStackTrace();
            }
        }

        public void itemAdd() {

        }

        // 카메라 플래시 켜고 끄기
        public void flashOnOff() {
            Camera.Parameters mCameraParameter = camera.getParameters();
            String state = mCameraParameter.getFlashMode();
            if (state.equals("off")) {
                mCameraParameter.setFlashMode("torch");
                camera.setParameters(mCameraParameter);
            } else if (state.equals("torch")) {
                mCameraParameter.setFlashMode("off");
                camera.setParameters(mCameraParameter);
            }
        }


        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            Log.v(LOG_TAG, "Surface Changed: width " + width + " height: " + height);

            // We would do this if we want to reset the camera parameters
            /*
            if (!recording) {
    			if (previewRunning){
    				camera.stopPreview();
    			}
    			try {
    				//Camera.Parameters cameraParameters = camera.getParameters();
    				//p.setPreviewSize(imageWidth, imageHeight);
    			    //p.setPreviewFrameRate(frameRate);
    				//camera.setParameters(cameraParameters);

    				camera.setPreviewDisplay(holder);
    				camera.startPreview();
    				previewRunning = true;
    			}
    			catch (IOException e) {
    				Log.e(LOG_TAG,e.getMessage());
    				e.printStackTrace();
    			}
    		}
            */

            // Get the current parameters
            Camera.Parameters currentParams = camera.getParameters();
            Log.v(LOG_TAG, "Preview Framerate: " + currentParams.getPreviewFrameRate());
            Log.v(LOG_TAG, "Preview imageWidth: " + currentParams.getPreviewSize().width + " imageHeight: " + currentParams.getPreviewSize().height);

            // Use these values
            imageWidth = currentParams.getPreviewSize().width;
            imageHeight = currentParams.getPreviewSize().height;
            frameRate = currentParams.getPreviewFrameRate();

            // Create the yuvIplimage if needed
            yuvIplimage = IplImage.create(imageWidth, imageHeight, IPL_DEPTH_8U, 2);
            //yuvIplimage = IplImage.create(imageWidth, imageHeight, IPL_DEPTH_32S, 2);
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            try {
                camera.setPreviewCallback(null);

                previewRunning = false;
                camera.release();

            } catch (RuntimeException e) {
                Log.v(LOG_TAG, e.getMessage());
                e.printStackTrace();
            }
        }

        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {

            if (yuvIplimage != null && recording) {
                videoTimestamp = 1000 * (System.currentTimeMillis() - startTime);

                // Put the camera preview frame right into the yuvIplimage object
                yuvIplimage.getByteBuffer().put(data);

                // FAQ about IplImage:
                // - For custom raw processing of data, getByteBuffer() returns an NIO direct
                //   buffer wrapped around the memory pointed by imageData, and under Android we can
                //   also use that Buffer with Bitmap.copyPixelsFromBuffer() and copyPixelsToBuffer().
                // - To get a BufferedImage from an IplImage, we may call getBufferedImage().
                // - The createFrom() factory method can construct an IplImage from a BufferedImage.
                // - There are also a few copy*() methods for BufferedImage<->IplImage data transfers.

                // Let's try it..
                // This works but only on transparency
                // Need to find the right Bitmap and IplImage matching types


                bitmap.copyPixelsFromBuffer(yuvIplimage.getByteBuffer());
                //bitmap.setPixel(10,10,Color.MAGENTA);

                canvas = new Canvas(bitmap);
                Paint paint = new Paint();
                paint.setColor(Color.GREEN);
                float leftx = 20;
                float topy = 20;
                float rightx = 50;
                float bottomy = 100;
                RectF rectangle = new RectF(leftx, topy, rightx, bottomy);
                canvas.drawRect(rectangle, paint);

                bitmap.copyPixelsToBuffer(yuvIplimage.getByteBuffer());

                //Log.v(LOG_TAG,"Writing Frame");

                try {

                    // Get the correct time
                    recorder.setTimestamp(videoTimestamp);

                    // Record the image into FFmpegFrameRecorder
                    recorder.record(yuvIplimage);

                } catch (FFmpegFrameRecorder.Exception e) {
                    Log.v(LOG_TAG, e.getMessage());
                    e.printStackTrace();
                }
            }
        }
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
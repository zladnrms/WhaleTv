package zladnrms.defytech.kim.BroadcastTv.view;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;

import zladnrms.defytech.kim.BroadcastTv.contract.SplashContract;
import zladnrms.defytech.kim.BroadcastTv.R;
import zladnrms.defytech.kim.BroadcastTv.databinding.ActivitySplashBinding;
import zladnrms.defytech.kim.BroadcastTv.eventbus.LoginEvent;
import zladnrms.defytech.kim.BroadcastTv.eventbus.RxBus;
import zladnrms.defytech.kim.BroadcastTv.networking.CheckNetworkStatus;
import zladnrms.defytech.kim.BroadcastTv.presenter.SplashPresenter;

public class SplashActivity extends AppCompatActivity implements SplashContract.View {

    /*
     권한 설정 및 자동 로그인 액티비티
     */

    /* presenter */
    private SplashPresenter presenter;

    /* Data binding */
    private ActivitySplashBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash);

        presenter = new SplashPresenter();
        presenter.attachView(this);

        checkAndroidVersion();

        RxBus.get().register(this);
    }

    /* Check Network Status For Login */
    private void checkNetworkStatusForLogin() {
        if (!CheckNetworkStatus.isConnectedToNetwork(SplashActivity.this)) {
            Toast.makeText(SplashActivity.this, "인터넷에 연결되어 있지 않습니다. 연결 후 로그인해주세요.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(SplashActivity.this, IntroActivity.class);
            startActivity(intent);
            finish();
        } else {
            presenter.autoLogin(SplashActivity.this);
        }
    }

    /* Check Android Version*/
    private void checkAndroidVersion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            /* Marshmallow 버전 이상에서 권한 전부 설정되어있는지 체크 */
            if (ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    + ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    + ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.CAMERA)
                    + ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.RECORD_AUDIO)
                    != PackageManager.PERMISSION_GRANTED) {
                /* 아닐 경우 권한 설정 페이지로 이동 */
                Intent intent = new Intent(SplashActivity.this, PermissionActivity.class);
                startActivity(intent);
                finish();
            } else {
                checkNetworkStatusForLogin();
            }
        } else {
            checkNetworkStatusForLogin();
        }
    }

    @Override
    public void whenNoLoginData() {
        Intent intent = new Intent(SplashActivity.this, IntroActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        RxBus.get().unregister(this);
        presenter.realmClose();
        presenter.detachView(this);
    }

    @Subscribe(tags = @Tag("splash"))
    public void subscribe(LoginEvent loginEvent) {
        if (loginEvent.getResult().equals("success")) {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            intent.putExtra("nickname", loginEvent.getNickname());
            startActivity(intent);
            finish();
        } else if (loginEvent.getResult().equals("miss_id")) {
            showCustomToast("아이디를 잘못 입력하셨습니다", Toast.LENGTH_SHORT);
            Intent intent = new Intent(SplashActivity.this, IntroActivity.class);
            startActivity(intent);
            finish();
        } else if (loginEvent.getResult().equals("miss_password")) {
            showCustomToast("비밀번호를 잘못 입력하셨습니다", Toast.LENGTH_SHORT);
            Intent intent = new Intent(SplashActivity.this, IntroActivity.class);
            startActivity(intent);
            finish();
        }
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
}

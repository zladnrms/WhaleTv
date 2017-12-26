package zladnrms.defytech.kim.BroadcastTv.view;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.orhanobut.logger.Logger;

import rx.Observable;
import rx.Subscriber;
import zladnrms.defytech.kim.BroadcastTv.Contract.IntroContract;
import zladnrms.defytech.kim.BroadcastTv.R;
import zladnrms.defytech.kim.BroadcastTv.databinding.ActivityIntroBinding;
import zladnrms.defytech.kim.BroadcastTv.eventbus.LoginEvent;
import zladnrms.defytech.kim.BroadcastTv.eventbus.RxBus;
import zladnrms.defytech.kim.BroadcastTv.presenter.IntroPresenter;

public class IntroActivity extends AppCompatActivity implements IntroContract.View {

    /* For Finish By Other Activity */
    public static IntroActivity activity = null;

    /* presenter */
    private IntroPresenter presenter;

    /* Data binding */
    private ActivityIntroBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        activity = this;

        binding = DataBindingUtil.setContentView(this, R.layout.activity_intro);

        presenter = new IntroPresenter();
        presenter.attachView(this);

        binding.btnLogin.setOnClickListener(v -> {
            Intent intent = new Intent(IntroActivity.this, LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.activity_slide_enter_from_bottom, R.anim.activity_not_move);
        });

        binding.btnJoin.setOnClickListener(v -> {
            Intent intent = new Intent(IntroActivity.this, JoinActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.activity_slide_enter_from_bottom, R.anim.activity_not_move);
        });



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        RxBus.get().unregister(this);
        presenter.detachView(this);
    }
}

package zladnrms.defytech.kim.BroadcastTv.view;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.orhanobut.logger.Logger;

import java.util.regex.Pattern;

import zladnrms.defytech.kim.BroadcastTv.Contract.LoginContract;
import zladnrms.defytech.kim.BroadcastTv.R;
import zladnrms.defytech.kim.BroadcastTv.databinding.ActivityLoginBinding;
import zladnrms.defytech.kim.BroadcastTv.eventbus.LoginEvent;
import zladnrms.defytech.kim.BroadcastTv.eventbus.RxBus;
import zladnrms.defytech.kim.BroadcastTv.presenter.LoginPresenter;

public class LoginActivity extends AppCompatActivity implements LoginContract.View {

    /* presenter */
    private LoginContract.Presenter presenter;

    /* Data binding */
    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        presenter = new LoginPresenter();
        presenter.attachView(this);

        binding.toolbar.setNavigationIcon(R.drawable.ic_close);
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        binding.etId.setFilters(new InputFilter[] {filterAlphaNum});

        binding.btnLogin.setOnClickListener(v -> {
            String id = binding.etId.getText().toString();
            String password = binding.etPassword.getText().toString();

            if(id.equals("")) {
                binding.layoutEtPassword.setErrorEnabled(false);
                binding.layoutEtPassword.setError(null);
                binding.layoutEtId.setError("아이디를 입력해주세요");
            } else if(password.equals("")) {
                binding.layoutEtId.setErrorEnabled(false);
                binding.layoutEtId.setError(null);
                binding.layoutEtPassword.setError("비밀번호를 입력해주세요");
            } else {
                presenter.login(LoginActivity.this, id, password);
            }
        });

        RxBus.get().register(this);
    }


    @Subscribe(tags = @Tag("login"))
    public void subscribe(LoginEvent loginEvent) {
        if(loginEvent.getResult().equals("success")) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("nickname", loginEvent.getNickname());
            startActivity(intent);
            finish();

            if (IntroActivity.activity != null) {
                IntroActivity activity = IntroActivity.activity;
                activity.finish();
            }
        } else if (loginEvent.getResult().equals("miss_id")){
            showCustomToast("아이디를 잘못 입력하셨습니다", Toast.LENGTH_SHORT);
        } else if(loginEvent.getResult().equals("miss_password")) {
            showCustomToast("비밀번호를 잘못 입력하셨습니다", Toast.LENGTH_SHORT);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.get().unregister(this);
        presenter.detachView(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.activity_not_move, R.anim.activity_slide_exit_from_top);
    }

    protected InputFilter filterAlphaNum = new InputFilter() {
        public CharSequence filter(CharSequence source, int start, int end,
                                   Spanned dest, int dstart, int dend) {

            Pattern ps = Pattern.compile("^[a-zA-Z0-9]+$");
            if (!ps.matcher(source).matches()) {
                return "";
            }
            return null;
        }
    };

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

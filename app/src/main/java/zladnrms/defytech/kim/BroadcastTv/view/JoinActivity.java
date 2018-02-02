package zladnrms.defytech.kim.BroadcastTv.view;

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

import java.util.regex.Pattern;

import zladnrms.defytech.kim.BroadcastTv.contract.JoinContract;
import zladnrms.defytech.kim.BroadcastTv.R;
import zladnrms.defytech.kim.BroadcastTv.databinding.ActivityJoinBinding;
import zladnrms.defytech.kim.BroadcastTv.eventbus.RxBus;
import zladnrms.defytech.kim.BroadcastTv.presenter.JoinPresenter;

public class JoinActivity extends AppCompatActivity implements JoinContract.View {

    /* presenter */
    private JoinPresenter presenter;

    /* Data binding */
    private ActivityJoinBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_join);

        presenter = new JoinPresenter();
        presenter.attachView(this);

        binding.toolbar.setNavigationIcon(R.drawable.ic_close);
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        binding.etId.setFilters(new InputFilter[] {filterAlphaNum});

        /* 가입 시도 */
        binding.btnSumbit.setOnClickListener(v -> {
            String id = binding.etId.getText().toString();
            String password = binding.etPassword.getText().toString();
            String nickname = binding.etNickname.getText().toString();

            if (id.equals("")) {
                binding.layoutEtPassword.setErrorEnabled(false);
                binding.layoutEtPassword.setError(null);
                binding.layoutEtNickname.setErrorEnabled(false);
                binding.layoutEtNickname.setError(null);
                binding.layoutEtId.setError("아이디를 입력해주세요");
            } else if (password.equals("")) {
                binding.layoutEtId.setErrorEnabled(false);
                binding.layoutEtId.setError(null);
                binding.layoutEtNickname.setErrorEnabled(false);
                binding.layoutEtNickname.setError(null);
                binding.layoutEtPassword.setError("비밀번호를 입력해주세요");
            } else if (nickname.equals("")) {
                binding.layoutEtId.setErrorEnabled(false);
                binding.layoutEtId.setError(null);
                binding.layoutEtPassword.setErrorEnabled(false);
                binding.layoutEtPassword.setError(null);
                binding.layoutEtNickname.setError("닉네임을 입력해주세요");
            } else {
                /* 전송함 */
                presenter.join(JoinActivity.this, id, password, nickname);
            }
        });

        RxBus.get().register(this);
    }


    @Subscribe(tags = @Tag("join"))
    public void subscribe(String result) {
        if(result.equals("success")) {
            showCustomToast("가입에 성공했습니다", Toast.LENGTH_SHORT);
            finish();
        } else if(result.equals("already_id")) {
            showCustomToast("이미 있는 아이디입니다.", Toast.LENGTH_SHORT);
        } else if(result.equals("already_nickname")) {
            showCustomToast("이미 있는 닉네임입니다.", Toast.LENGTH_SHORT);
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

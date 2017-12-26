package zladnrms.defytech.kim.BroadcastTv.view;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import zladnrms.defytech.kim.BroadcastTv.contract.AccountContract;
import zladnrms.defytech.kim.BroadcastTv.R;
import zladnrms.defytech.kim.BroadcastTv.databinding.ActivityAccountBinding;
import zladnrms.defytech.kim.BroadcastTv.eventbus.RxBus;
import zladnrms.defytech.kim.BroadcastTv.presenter.AccountPresenter;

public class AccountActivity extends AppCompatActivity implements AccountContract.View {

    /* presenter */
    private AccountPresenter presenter;

    /* Data binding */
    private ActivityAccountBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_account);
        binding.toolbar.setNavigationIcon(R.drawable.ic_backarrow);
        binding.toolbar.setNavigationOnClickListener(v -> {
            finish();
        });

        binding.ivProfile.setOnClickListener(v -> {

        });

        binding.btnSubmit.setOnClickListener(v -> {

        });

        //binding.accountViewpager

        presenter = new AccountPresenter();
        presenter.attachView(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        RxBus.get().unregister(this);
        presenter.detachView(this);
    }
}

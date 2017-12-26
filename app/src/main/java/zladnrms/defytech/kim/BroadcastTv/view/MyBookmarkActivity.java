package zladnrms.defytech.kim.BroadcastTv.view;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;

import zladnrms.defytech.kim.BroadcastTv.Contract.AccountContract;
import zladnrms.defytech.kim.BroadcastTv.Contract.MyBookmarkContract;
import zladnrms.defytech.kim.BroadcastTv.R;
import zladnrms.defytech.kim.BroadcastTv.adapter.MyBookmarkListAdapter;
import zladnrms.defytech.kim.BroadcastTv.databinding.ActivityAccountBinding;
import zladnrms.defytech.kim.BroadcastTv.databinding.ActivityMyBookmarkBinding;
import zladnrms.defytech.kim.BroadcastTv.eventbus.RxBus;
import zladnrms.defytech.kim.BroadcastTv.presenter.AccountPresenter;
import zladnrms.defytech.kim.BroadcastTv.presenter.MyBookmarkPresenter;

public class MyBookmarkActivity extends AppCompatActivity implements MyBookmarkContract.View {

    /* presenter */
    private MyBookmarkPresenter presenter;

    /* Data binding */
    private ActivityMyBookmarkBinding binding;

    /* RecyclerView Adapter */
    private MyBookmarkListAdapter rv_bookmark_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_bookmark);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_bookmark);
        binding.toolbar.setNavigationIcon(R.drawable.ic_backarrow);
        binding.toolbar.setNavigationOnClickListener(v -> {
            finish();
        });

        presenter = new MyBookmarkPresenter();
        presenter.attachView(this);

        rv_bookmark_adapter = new MyBookmarkListAdapter(this);
        LinearLayoutManager verticalLayoutmanager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        binding.rvBookmarkList.setLayoutManager(verticalLayoutmanager);
        binding.rvBookmarkList.setAdapter(rv_bookmark_adapter);

    }

    @Override
    protected void onResume() {
        super.onResume();

        presenter.clear();
        presenter.getUserBookmarkList(this);
    }

    @Override
    public void getBookmarkData(String nickname) {
        rv_bookmark_adapter.add(nickname);
        presenter.refresh();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        RxBus.get().unregister(this);
        presenter.detachView(this);
    }

    @Override
    public void clear() {
        rv_bookmark_adapter.clear();
    }

    @Override
    public void refresh() {
        rv_bookmark_adapter.refresh();
    }
}

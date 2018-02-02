package zladnrms.defytech.kim.BroadcastTv.view;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;

import java.util.ArrayList;

import zladnrms.defytech.kim.BroadcastTv.contract.MyVideoContract;
import zladnrms.defytech.kim.BroadcastTv.R;
import zladnrms.defytech.kim.BroadcastTv.adapter.MyVideoListAdapter;
import zladnrms.defytech.kim.BroadcastTv.databinding.ActivityMyVideoBinding;
import zladnrms.defytech.kim.BroadcastTv.eventbus.RxBus;
import zladnrms.defytech.kim.BroadcastTv.model.domain.VideoInfo;
import zladnrms.defytech.kim.BroadcastTv.presenter.MyVideoPresenter;

public class MyVideoActivity extends AppCompatActivity implements MyVideoContract.View {

    /* presenter */
    private MyVideoPresenter presenter;

    /* Data binding */
    private ActivityMyVideoBinding binding;

    /* RecyclerView Adapter */
    private MyVideoListAdapter rv_my_video_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_bookmark);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_video);
        binding.toolbar.setNavigationIcon(R.drawable.ic_backarrow);
        binding.toolbar.setNavigationOnClickListener(v -> {
            finish();
        });

        presenter = new MyVideoPresenter();
        presenter.attachView(this);

        rv_my_video_adapter = new MyVideoListAdapter(MyVideoActivity.this);
        LinearLayoutManager verticalLayoutmanager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        binding.rvMyVideoList.setLayoutManager(verticalLayoutmanager);
        binding.rvMyVideoList.setAdapter(rv_my_video_adapter);

        /* refresh list */
        binding.swipeRefreshMyVideoLayout.setOnRefreshListener(() -> {
            presenter.clear();
            presenter.getVideoList(MyVideoActivity.this);
        });
    }

    @Override
    public void getVideoData(ArrayList<VideoInfo> video) {
        for(int i = 0; i < video.size(); i ++) {
            VideoInfo videoInfo = video.get(i);
            rv_my_video_adapter.add(videoInfo);
            presenter.refresh();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        presenter.clear();
        presenter.getVideoList(MyVideoActivity.this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        RxBus.get().unregister(this);
        presenter.detachView(this);
    }

    @Override
    public void clear() {
        rv_my_video_adapter.clear();
    }

    @Override
    public void refresh() {
        rv_my_video_adapter.refresh();
        binding.swipeRefreshMyVideoLayout.setRefreshing(false);
    }
}

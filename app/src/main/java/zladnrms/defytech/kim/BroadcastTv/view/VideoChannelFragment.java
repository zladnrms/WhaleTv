package zladnrms.defytech.kim.BroadcastTv.view;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import zladnrms.defytech.kim.BroadcastTv.Contract.VideoChannelContract;
import zladnrms.defytech.kim.BroadcastTv.R;
import zladnrms.defytech.kim.BroadcastTv.adapter.RecentVideoListAdapter;
import zladnrms.defytech.kim.BroadcastTv.databinding.FragmentVideoChannelBinding;
import zladnrms.defytech.kim.BroadcastTv.model.domain.VideoInfo;
import zladnrms.defytech.kim.BroadcastTv.presenter.VideoChannelPresenter;

public class VideoChannelFragment extends Fragment implements VideoChannelContract.View {

    /* Data binding */
    private FragmentVideoChannelBinding binding;

    /* presenter */
    private VideoChannelPresenter presenter;

    /* RecyclerView Adapter */
    private RecentVideoListAdapter rv_adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_video_channel,container,false);
        View view = binding.getRoot();

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        presenter = new VideoChannelPresenter();
        presenter.attachView(this);

        rv_adapter = new RecentVideoListAdapter(getActivity());
        LinearLayoutManager verticalLayoutmanager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        binding.rvRecentVideoList.setLayoutManager(verticalLayoutmanager);
        binding.rvRecentVideoList.setAdapter(rv_adapter);

        binding.swipeRefreshRecentVideoLayout.setOnRefreshListener(() -> {
            presenter.clear();
            presenter.getVideoList();
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        presenter.clear();
        presenter.getVideoList();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        presenter.detachView(this);
    }

    @Override
    public void getVideoData(ArrayList<VideoInfo> video) {
        for(int i = 0; i < video.size(); i ++) {
            VideoInfo videoInfo = video.get(i);
            rv_adapter.add(videoInfo);
            presenter.refresh();
        }
    }

    @Override
    public void clear() {
        rv_adapter.clear();
    }

    @Override
    public void refresh() {
        rv_adapter.refresh();
        binding.swipeRefreshRecentVideoLayout.setRefreshing(false);
    }
}


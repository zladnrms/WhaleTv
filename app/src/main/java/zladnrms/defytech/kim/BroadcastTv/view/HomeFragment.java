package zladnrms.defytech.kim.BroadcastTv.view;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import zladnrms.defytech.kim.BroadcastTv.contract.HomeContract;
import zladnrms.defytech.kim.BroadcastTv.R;
import zladnrms.defytech.kim.BroadcastTv.adapter.RoomListAdapter;
import zladnrms.defytech.kim.BroadcastTv.databinding.FragmentHomeBinding;
import zladnrms.defytech.kim.BroadcastTv.model.domain.RoomInfo;
import zladnrms.defytech.kim.BroadcastTv.presenter.HomePresenter;

public class HomeFragment extends Fragment implements HomeContract.View {

    /* Data binding */
    private FragmentHomeBinding binding;

    /* presenter */
    private HomePresenter presenter;

    /* RecyclerView Adapter */
    private RoomListAdapter rv_onair_adapter;

    public static final int PERMISSIONS_MULTIPLE_REQUEST = 123;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        View view = binding.getRoot();

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        presenter = new HomePresenter();
        presenter.attachView(this);

        rv_onair_adapter = new RoomListAdapter(getActivity());
        LinearLayoutManager verticalLayoutmanager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        binding.rvStreamingList.setLayoutManager(verticalLayoutmanager);
        binding.rvStreamingList.setAdapter(rv_onair_adapter);

        binding.fab.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), BroadcastActivity.class);
            startActivity(intent);
        });

        binding.swipeRefreshOnairLayout.setOnRefreshListener(() -> {
            presenter.clear();
            presenter.getRoomList();
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        presenter.clear();
        presenter.getRoomList();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        presenter.detachView(this);
    }

    @Override
    public void getRoomData(RoomInfo roomInfo) {
        rv_onair_adapter.add(roomInfo);
        presenter.getRoomViewer(roomInfo.getRoomId());
        presenter.refresh();
    }

    @Override
    public void clear() {
        rv_onair_adapter.clear();
    }

    @Override
    public void refresh() {
        rv_onair_adapter.refresh();
        binding.swipeRefreshOnairLayout.setRefreshing(false);
    }
}


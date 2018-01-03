package zladnrms.defytech.kim.BroadcastTv.view;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.orhanobut.logger.Logger;

import zladnrms.defytech.kim.BroadcastTv.contract.HomeContract;
import zladnrms.defytech.kim.BroadcastTv.R;
import zladnrms.defytech.kim.BroadcastTv.adapter.RoomListAdapter;
import zladnrms.defytech.kim.BroadcastTv.databinding.FragmentHomeBinding;
import zladnrms.defytech.kim.BroadcastTv.model.domain.RoomInfo;
import zladnrms.defytech.kim.BroadcastTv.networking.CheckNetworkStatus;
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

            if (!CheckNetworkStatus.isConnectedToNetwork(getActivity())) {
                Toast.makeText(getActivity(), "인터넷 연결 상태를 확인해주세요.", Toast.LENGTH_SHORT).show();
            } else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                checkPermission();
            } else {
                Intent intent = new Intent(getActivity(), BroadcastActivity.class);
                startActivity(intent);
            }

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


    /* Permission */

    /* Permission Check */
    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                + ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                + ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                + ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                    || ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    || ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.CAMERA)
                    || ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.RECORD_AUDIO)) {

                Snackbar.make(getActivity().findViewById(android.R.id.content),
                        "방송 송출, 시청의 원활한 진행을 위해 권한을 허용해주세요",
                        Snackbar.LENGTH_INDEFINITE).setAction("설정", v->{
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE
                                    , Manifest.permission.WRITE_EXTERNAL_STORAGE
                                    , Manifest.permission.CAMERA
                                    , Manifest.permission.RECORD_AUDIO},
                            PERMISSIONS_MULTIPLE_REQUEST);

                }).show();
            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO},
                        PERMISSIONS_MULTIPLE_REQUEST);
            }
        } else {
            // if permission already granted
            Intent intent = new Intent(getActivity(), BroadcastActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSIONS_MULTIPLE_REQUEST:
                if (grantResults.length > 0) {
                    boolean readExternalFile = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeExternalFile = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean cameraPermission = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    boolean recordAudioPermission = grantResults[3] == PackageManager.PERMISSION_GRANTED;

                    if(readExternalFile && writeExternalFile && cameraPermission && recordAudioPermission)
                    {
                        // if permission granted
                        Intent intent = new Intent(getActivity(), BroadcastActivity.class);
                        startActivity(intent);
                    } else {
                        Snackbar.make(getActivity().findViewById(android.R.id.content),
                                "방송 송출, 시청의 원활한 진행을 위해 권한을 허용해주세요",
                                Snackbar.LENGTH_INDEFINITE).setAction("설정",
                                v->{
                                    ActivityCompat.requestPermissions(getActivity(),
                                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                                    Manifest.permission.CAMERA,
                                                    Manifest.permission.RECORD_AUDIO},
                                            PERMISSIONS_MULTIPLE_REQUEST);
                                }).show();
                    }
                }
                break;
        }
    }
}


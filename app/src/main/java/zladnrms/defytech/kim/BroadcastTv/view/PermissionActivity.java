package zladnrms.defytech.kim.BroadcastTv.view;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import zladnrms.defytech.kim.BroadcastTv.contract.PermissionContract;
import zladnrms.defytech.kim.BroadcastTv.R;
import zladnrms.defytech.kim.BroadcastTv.databinding.ActivityPermissionBinding;
import zladnrms.defytech.kim.BroadcastTv.eventbus.RxBus;
import zladnrms.defytech.kim.BroadcastTv.presenter.PermissionPresenter;

public class PermissionActivity extends AppCompatActivity implements PermissionContract.View {

    /* presenter */
    private PermissionContract.Presenter presenter;

    /* Data binding */
    private ActivityPermissionBinding binding;

    /* collaspe variable */
    private boolean showCamera = true;
    private boolean showRecord = true;
    private boolean showStorage = true;

    public  static final int PERMISSIONS_MULTIPLE_REQUEST = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_permission);

        presenter = new PermissionPresenter();
        presenter.attachView(this);

        binding.toolbar.setNavigationIcon(R.drawable.ic_check);

        binding.layoutCameraCollapse.setOnClickListener(v->{
            if(showCamera) {
                showCamera = false;
                binding.ivCamera.setImageResource(R.drawable.ic_chevron_down);
                binding.layoutCameraDetail.setVisibility(View.GONE);
            } else {
                showCamera = true;
                binding.ivCamera.setImageResource(R.drawable.ic_chevron_up);
                binding.layoutCameraDetail.setVisibility(View.VISIBLE);
            }
        });

        binding.layoutStorageCollapse.setOnClickListener(v->{
            if(showStorage) {
                showStorage = false;
                binding.ivStorage.setImageResource(R.drawable.ic_chevron_down);
                binding.layoutStorageDetail.setVisibility(View.GONE);
            } else {
                showStorage = true;
                binding.ivStorage.setImageResource(R.drawable.ic_chevron_up);
                binding.layoutStorageDetail.setVisibility(View.VISIBLE);
            }
        });

        binding.layoutRecordCollapse.setOnClickListener(v->{
            if(showRecord) {
                showRecord = false;
                binding.ivRecord.setImageResource(R.drawable.ic_chevron_down);
                binding.layoutRecordDetail.setVisibility(View.GONE);
            } else {
                showRecord = true;
                binding.ivRecord.setImageResource(R.drawable.ic_chevron_up);
                binding.layoutRecordDetail.setVisibility(View.VISIBLE);
            }
        });

        binding.btnAccept.setOnClickListener(v -> {
            checkAndroidVersion();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        presenter.detachView(this);
    }

    /* Check Android Version*/
    private void checkAndroidVersion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermission();
        } else {
            Intent intent = new Intent(PermissionActivity.this, SplashActivity.class);
            startActivity(intent);
            finish();
        }
    }

    /* Permission Check */
    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(PermissionActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                + ContextCompat.checkSelfPermission(PermissionActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                + ContextCompat.checkSelfPermission(PermissionActivity.this, Manifest.permission.CAMERA)
                + ContextCompat.checkSelfPermission(PermissionActivity.this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(PermissionActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    || ActivityCompat.shouldShowRequestPermissionRationale(PermissionActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    || ActivityCompat.shouldShowRequestPermissionRationale(PermissionActivity.this, Manifest.permission.CAMERA)
                    || ActivityCompat.shouldShowRequestPermissionRationale(PermissionActivity.this, Manifest.permission.RECORD_AUDIO)) {

                Snackbar.make(PermissionActivity.this.findViewById(android.R.id.content),
                        "방송 송출, 시청의 원활한 진행을 위해 권한을 허용해주세요",
                        Snackbar.LENGTH_INDEFINITE).setAction("설정", v->{
                    ActivityCompat.requestPermissions(PermissionActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE
                                    , Manifest.permission.WRITE_EXTERNAL_STORAGE
                                    , Manifest.permission.CAMERA
                                    , Manifest.permission.RECORD_AUDIO},
                            PERMISSIONS_MULTIPLE_REQUEST);

                }).show();
            } else {
                ActivityCompat.requestPermissions(PermissionActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO},
                        PERMISSIONS_MULTIPLE_REQUEST);
            }
        } else {
            // if permission already granted
            Intent intent = new Intent(PermissionActivity.this, SplashActivity.class);
            startActivity(intent);
            finish();
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
                        Intent intent = new Intent(PermissionActivity.this, SplashActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Snackbar.make(PermissionActivity.this.findViewById(android.R.id.content),
                                "방송 송출, 시청의 원활한 진행을 위해 권한을 허용해주세요",
                                Snackbar.LENGTH_INDEFINITE).setAction("설정",
                                v->{
                                    ActivityCompat.requestPermissions(PermissionActivity.this,
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

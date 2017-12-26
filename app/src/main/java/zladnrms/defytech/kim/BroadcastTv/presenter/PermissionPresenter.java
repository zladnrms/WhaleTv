package zladnrms.defytech.kim.BroadcastTv.presenter;

import zladnrms.defytech.kim.BroadcastTv.Contract.IntroContract;
import zladnrms.defytech.kim.BroadcastTv.Contract.PermissionContract;
import zladnrms.defytech.kim.BroadcastTv.model.LocalDataRepository;
import zladnrms.defytech.kim.BroadcastTv.model.LocalDataRepositoryModel;
import zladnrms.defytech.kim.BroadcastTv.networking.RetrofitClient;

/**
 * Created by kim on 2017-06-22.
 */

public class PermissionPresenter implements PermissionContract.Presenter {

    private PermissionContract.View view;
    private LocalDataRepositoryModel localRepo;

    /* Retrofit2 */
    private RetrofitClient retrofitClient;

    public PermissionPresenter() {
        /* Init Retrofit2 */
        this.retrofitClient = new RetrofitClient();
    }

    @Override
    public void attachView(Object view) {
        this.view = (PermissionContract.View) view;
        this.localRepo = LocalDataRepository.getInstance();
    }

    @Override
    public void detachView(Object view) {
        this.view = null;
    }
}

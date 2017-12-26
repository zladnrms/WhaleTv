package zladnrms.defytech.kim.BroadcastTv.presenter;

import android.content.Context;

import java.util.ArrayList;

import zladnrms.defytech.kim.BroadcastTv.Contract.AccountContract;
import zladnrms.defytech.kim.BroadcastTv.Contract.MyBookmarkContract;
import zladnrms.defytech.kim.BroadcastTv.model.LocalDataRepository;
import zladnrms.defytech.kim.BroadcastTv.model.LocalDataRepositoryModel;
import zladnrms.defytech.kim.BroadcastTv.networking.RetrofitClient;

/**
 * Created by kim on 2017-06-22.
 */

public class AccountPresenter implements AccountContract.Presenter {

    private AccountContract.View view;
    private LocalDataRepositoryModel localRepo;

    /* Retrofit2 */
    private RetrofitClient retrofitClient;

    public AccountPresenter() {
        /* Init Retrofit2 */
        this.retrofitClient = new RetrofitClient();
    }

    @Override
    public void attachView(Object view) {
        this.view = (AccountContract.View) view;
        this.localRepo = LocalDataRepository.getInstance();
    }

    @Override
    public void detachView(Object view) {
        this.view = null;
    }


}

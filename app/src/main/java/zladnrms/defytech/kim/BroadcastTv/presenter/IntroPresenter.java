package zladnrms.defytech.kim.BroadcastTv.presenter;

import android.content.Context;
import android.support.annotation.NonNull;

import com.orhanobut.logger.Logger;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import zladnrms.defytech.kim.BroadcastTv.Contract.IntroContract;
import zladnrms.defytech.kim.BroadcastTv.eventbus.LoginEvent;
import zladnrms.defytech.kim.BroadcastTv.eventbus.RxBus;
import zladnrms.defytech.kim.BroadcastTv.model.LocalDataRepository;
import zladnrms.defytech.kim.BroadcastTv.model.LocalDataRepositoryModel;
import zladnrms.defytech.kim.BroadcastTv.model.domain.LoginData;
import zladnrms.defytech.kim.BroadcastTv.networking.RetrofitClient;
import zladnrms.defytech.kim.BroadcastTv.networking.response.LoginDataRepo;

/**
 * Created by kim on 2017-06-22.
 */

public class IntroPresenter implements IntroContract.Presenter {

    private IntroContract.View view;
    private LocalDataRepositoryModel localRepo;

    /* Retrofit2 */
    private RetrofitClient retrofitClient;

    public IntroPresenter() {
        /* Init Retrofit2 */
        this.retrofitClient = new RetrofitClient();
    }

    @Override
    public void attachView(Object view) {
        this.view = (IntroContract.View) view;
        this.localRepo = LocalDataRepository.getInstance();
    }

    @Override
    public void detachView(Object view) {
        this.view = null;
    }
}

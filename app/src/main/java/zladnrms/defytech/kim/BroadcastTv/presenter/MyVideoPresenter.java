package zladnrms.defytech.kim.BroadcastTv.presenter;

import android.content.Context;

import com.orhanobut.logger.Logger;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import zladnrms.defytech.kim.BroadcastTv.Contract.MyBookmarkContract;
import zladnrms.defytech.kim.BroadcastTv.Contract.MyVideoContract;
import zladnrms.defytech.kim.BroadcastTv.model.LocalDataRepository;
import zladnrms.defytech.kim.BroadcastTv.model.LocalDataRepositoryModel;
import zladnrms.defytech.kim.BroadcastTv.networking.RetrofitClient;
import zladnrms.defytech.kim.BroadcastTv.networking.response.BookmarkListRepo;

/**
 * Created by kim on 2017-06-22.
 */

public class MyVideoPresenter implements MyVideoContract.Presenter {

    private MyVideoContract.View view;
    private LocalDataRepositoryModel localRepo;

    /* Retrofit2 */
    private RetrofitClient retrofitClient;

    public MyVideoPresenter() {
        /* Init Retrofit2 */
        this.retrofitClient = new RetrofitClient();
    }

    @Override
    public void attachView(Object view) {
        this.view = (MyVideoContract.View) view;
        this.localRepo = LocalDataRepository.getInstance();
    }

    @Override
    public void detachView(Object view) {
        this.view = null;
    }

}

package zladnrms.defytech.kim.BroadcastTv.adapter.presenter;

import android.content.Context;
import android.widget.Toast;

import com.orhanobut.logger.Logger;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import zladnrms.defytech.kim.BroadcastTv.adapter.contract.MyBookmarkListAdapterContract;
import zladnrms.defytech.kim.BroadcastTv.contract.IntroContract;
import zladnrms.defytech.kim.BroadcastTv.eventbus.RxBus;
import zladnrms.defytech.kim.BroadcastTv.model.LocalDataRepository;
import zladnrms.defytech.kim.BroadcastTv.model.LocalDataRepositoryModel;
import zladnrms.defytech.kim.BroadcastTv.networking.RetrofitClient;
import zladnrms.defytech.kim.BroadcastTv.networking.response.BookmarkRepo;
import zladnrms.defytech.kim.BroadcastTv.networking.response.ResultRepo;

/**
 * Created by kim on 2017-06-22.
 */

public class MyBookmarkListAdapterPresenter implements MyBookmarkListAdapterContract.Presenter {

    private MyBookmarkListAdapterContract.View view;
    private LocalDataRepositoryModel localRepo;

    /* Retrofit2 */
    private RetrofitClient retrofitClient;

    public MyBookmarkListAdapterPresenter() {
        /* Init Retrofit2 */
        this.retrofitClient = new RetrofitClient();
    }

    @Override
    public void attachView(Object view) {
        this.view = (MyBookmarkListAdapterContract.View) view;
        this.localRepo = LocalDataRepository.getInstance();
    }

    @Override
    public void detachView(Object view) {
        this.view = null;
    }

    @Override
    public void delete(Context context,String streamerNickname) {
        String nickname = localRepo.getUserNickname(context);

        retrofitClient.getApi()
                .delBookmark(nickname, streamerNickname)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResultRepo>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull ResultRepo repo) {
                        for (int i = 0; i < repo.getResponse().size(); i++) {
                                if (repo.getResponse().get(i).getResult() != null) {
                                    Logger.t("ViewerPresenter-onNext").d(repo.getResponse().get(i).getResult());
                                    if (repo.getResponse().get(i).getResult().equals("success")) {
                                        Toast.makeText(context, streamerNickname + "님을 즐겨찾기 제거하였습니다", Toast.LENGTH_SHORT).show();
                                        view.refresh();
                                    }
                                }
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Logger.t("JoinPresenter-onError").d("에러 발생");
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        Logger.t("JoinPresenter-onNext").d("onComplete");

                    }
                });

    }
}

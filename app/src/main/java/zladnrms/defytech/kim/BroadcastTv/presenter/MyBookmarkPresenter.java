package zladnrms.defytech.kim.BroadcastTv.presenter;

import android.content.Context;
import android.widget.Toast;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import zladnrms.defytech.kim.BroadcastTv.contract.MyBookmarkContract;
import zladnrms.defytech.kim.BroadcastTv.model.LocalDataRepository;
import zladnrms.defytech.kim.BroadcastTv.model.LocalDataRepositoryModel;
import zladnrms.defytech.kim.BroadcastTv.networking.RetrofitClient;
import zladnrms.defytech.kim.BroadcastTv.networking.response.BookmarkListRepo;
import zladnrms.defytech.kim.BroadcastTv.networking.response.GetBookmarkRepo;

/**
 * Created by kim on 2017-06-22.
 */

public class MyBookmarkPresenter implements MyBookmarkContract.Presenter {

    private MyBookmarkContract.View view;
    private LocalDataRepositoryModel localRepo;

    /* Retrofit2 */
    private RetrofitClient retrofitClient;

    public MyBookmarkPresenter() {
        /* Init Retrofit2 */
        this.retrofitClient = new RetrofitClient();
    }

    @Override
    public void attachView(Object view) {
        this.view = (MyBookmarkContract.View) view;
        this.localRepo = LocalDataRepository.getInstance();
    }

    @Override
    public void detachView(Object view) {
        this.view = null;
    }

    @Override
    public void clear() {
        view.clear();
    }

    @Override
    public void refresh() {
        view.refresh();
    }

    @Override
    public void getUserBookmarkList(Context context) {
        String nickname = localRepo.getUserNickname(context);

        retrofitClient.getApi()
                .getBookmark(nickname)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GetBookmarkRepo>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull GetBookmarkRepo repo) {
                        for (int i = 0; i < repo.getResponse().size(); i++) {
                            if (repo.getResponse() != null) {
                                String streamerNickname = repo.getResponse().get(i).getStreamerNickname();
                                view.getBookmarkData(streamerNickname);
                            }
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Logger.t("BookmarkListRepo-onError").d("에러 발생");
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        Logger.t("HomePresenter-onNext").d("onComplete");
                    }
                });
    }
}

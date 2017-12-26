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
import zladnrms.defytech.kim.BroadcastTv.model.LocalDataRepository;
import zladnrms.defytech.kim.BroadcastTv.model.LocalDataRepositoryModel;
import zladnrms.defytech.kim.BroadcastTv.networking.RetrofitClient;
import zladnrms.defytech.kim.BroadcastTv.networking.response.BookmarkListRepo;

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
                .getBookmarkList(nickname)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BookmarkListRepo>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull BookmarkListRepo repo) {
                        for (int i = 0; i < repo.getResponse().size(); i++) {
                            if (repo.getResponse().get(i).getResult().equals("yes_data")) {
                                ArrayList<String> bookmarkList = repo.getResponse().get(i).getBookmarklist();
                                Logger.t("BookmarkListRepo-onNext").d(bookmarkList);

                                for(int j = 0; j < bookmarkList.size(); j++ ) {
                                    view.getBookmarkData(bookmarkList.get(j));
                                }
                            } else {
                                Logger.t("BookmarkListRepo-onNext").d(repo.getResponse().get(i).getBookmarklist());

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

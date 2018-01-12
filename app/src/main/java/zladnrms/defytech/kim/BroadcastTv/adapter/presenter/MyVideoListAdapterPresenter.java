package zladnrms.defytech.kim.BroadcastTv.adapter.presenter;

import android.content.Context;
import android.widget.Toast;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import zladnrms.defytech.kim.BroadcastTv.adapter.contract.MyBookmarkListAdapterContract;
import zladnrms.defytech.kim.BroadcastTv.adapter.contract.MyVideoListAdapterContract;
import zladnrms.defytech.kim.BroadcastTv.model.LocalDataRepository;
import zladnrms.defytech.kim.BroadcastTv.model.LocalDataRepositoryModel;
import zladnrms.defytech.kim.BroadcastTv.model.domain.VideoInfo;
import zladnrms.defytech.kim.BroadcastTv.networking.RetrofitClient;
import zladnrms.defytech.kim.BroadcastTv.networking.response.BookmarkRepo;
import zladnrms.defytech.kim.BroadcastTv.networking.response.ResultRepo;
import zladnrms.defytech.kim.BroadcastTv.networking.response.VideoDataRepo;

/**
 * Created by kim on 2017-06-22.
 */

public class MyVideoListAdapterPresenter implements MyVideoListAdapterContract.Presenter {

    private MyVideoListAdapterContract.View view;
    private LocalDataRepositoryModel localRepo;

    /* Retrofit2 */
    private RetrofitClient retrofitClient;

    public MyVideoListAdapterPresenter() {
        /* Init Retrofit2 */
        this.retrofitClient = new RetrofitClient();
    }

    @Override
    public void attachView(Object view) {
        this.view = (MyVideoListAdapterContract.View) view;
        this.localRepo = LocalDataRepository.getInstance();
    }

    @Override
    public void detachView(Object view) {
        this.view = null;
    }

    public void clear() {
        view.clear();
    }

    @Override
    public void refresh() {
        view.refresh();
    }

    @Override
    public void changeStatus(Context context, int videoId, int status) {

        retrofitClient.getApi()
                .changeStatus(videoId, status)
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
                                Logger.t("MyVideoListAdapterPresenter-onNext").d(repo.getResponse().get(i).getResult());
                                if (repo.getResponse().get(i).getResult().equals("success")) {
                                    Toast.makeText(context, "게시 상태를 변경하였습니다..", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Logger.t("MyVideoListAdapterPresenter-onError").d("에러 발생");
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        Logger.t("MyVideoListAdapterPresenter-onNext").d("onComplete");
                        view.refresh();
                    }
                });
    }

    @Override
    public void adjust(Context context, int videoId, String subject) {

        retrofitClient.getApi()
                .adjustVideo(videoId, subject)
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
                                Logger.t("MyVideoListAdapterPresenter-onNext").d(repo.getResponse().get(i).getResult());
                                if (repo.getResponse().get(i).getResult().equals("success")) {
                                    Toast.makeText(context, "제목을 변경하였습니다.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Logger.t("MyVideoListAdapterPresenter-onError").d("에러 발생");
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        Logger.t("MyVideoListAdapterPresenter-onNext").d("onComplete");
                        view.refresh();
                    }
                });
    }

    @Override
    public void delete(Context context, int videoId, String filename) {

        retrofitClient.getApi()
                .deleteVideo(videoId, filename)
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
                                Logger.t("MyVideoListAdapterPresenter-onNext").d(repo.getResponse().get(i).getResult());
                                if (repo.getResponse().get(i).getResult().equals("success")) {
                                    Toast.makeText(context, "성공적으로 삭제하였습니다", Toast.LENGTH_SHORT).show();
                                } else if(repo.getResponse().get(i).getResult().equals("no_video_exist")) {
                                    Toast.makeText(context, "파일이 서버에 존재하지 않습니다", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Logger.t("MyVideoListAdapterPresenter-onError").d("에러 발생");
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        Logger.t("MyVideoListAdapterPresenter-onNext").d("onComplete");
                        view.refresh();
                    }
                });
    }
}

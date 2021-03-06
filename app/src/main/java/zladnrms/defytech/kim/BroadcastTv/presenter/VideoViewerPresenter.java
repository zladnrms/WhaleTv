package zladnrms.defytech.kim.BroadcastTv.presenter;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.widget.Toast;

import com.orhanobut.logger.Logger;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import zladnrms.defytech.kim.BroadcastTv.contract.VideoViewerContract;
import zladnrms.defytech.kim.BroadcastTv.model.LocalDataRepository;
import zladnrms.defytech.kim.BroadcastTv.model.LocalDataRepositoryModel;
import zladnrms.defytech.kim.BroadcastTv.model.ServerDataRepository;
import zladnrms.defytech.kim.BroadcastTv.model.ServerDataRepositoryModel;
import zladnrms.defytech.kim.BroadcastTv.networking.RetrofitClient;
import zladnrms.defytech.kim.BroadcastTv.networking.response.ResultRepo;

/**
 * Created by kim on 2017-06-22.
 */

public class VideoViewerPresenter implements VideoViewerContract.Presenter{

    /* View */
    private VideoViewerContract.View view;

    /* Local DataBase */
    private LocalDataRepositoryModel localRepo;

    /* Server DataBase */
    private ServerDataRepositoryModel serverRepo;

    /* Retrofit2 */
    private RetrofitClient retrofitClient;

    public VideoViewerPresenter() {
        /* Init Retrofit2 */
        this.retrofitClient = new RetrofitClient();
    }

    @Override
    public void attachView(Object view) {
        this.view = (VideoViewerContract.View) view;
        this.localRepo = LocalDataRepository.getInstance();
        this.serverRepo = ServerDataRepository.getInstance();
    }

    @Override
    public void detachView(Object view) {
        this.view = null;
    }

    public void saveUserRoomId(Context context, int roomId) {
        localRepo.saveUserRoomId(context, roomId);
    }

    @Override
    public void changeMode() {
        view.changeMode();
    }

    @Override
    public int getDeviceHeight(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        return height;
    }

    @Override
    public void upVideoCount(int videoId) {
        /* 조회수 1 증가*/
            retrofitClient.getApi()
                    .upVideoCount(videoId)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<ResultRepo>() {
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {

                        }

                        @Override
                        public void onNext(@NonNull ResultRepo repo) {

                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            Logger.t("VideoViewerPresenter-onError").d("에러 발생");
                            e.printStackTrace();
                        }

                        @Override
                        public void onComplete() {
                            Logger.t("VideoViewerPresenter-onNext").d("onComplete");
                        }
                    });
    }

    @Override
    public void like(Context context, int videoId) {
        /* 좋아요 클릭 */
        String nickname = localRepo.getUserNickname(context);

        retrofitClient.getApi()
                .likeVideo(videoId, nickname)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResultRepo>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull ResultRepo repo) {
                        for (int i = 0; i < repo.getResponse().size(); i++) {
                            if (repo.getResponse().get(i).getResult().equals("success")) {
                                Toast.makeText(context, "영상을 추천하였습니다.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "이미 추천하셨습니다.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Logger.t("VideoViewerPresenter-onError").d("에러 발생");
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        Logger.t("VideoViewerPresenter-onNext").d("onComplete");
                    }
                });
    }
}

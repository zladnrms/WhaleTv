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
import zladnrms.defytech.kim.BroadcastTv.Contract.VideoViewerContract;
import zladnrms.defytech.kim.BroadcastTv.Contract.ViewerContract;
import zladnrms.defytech.kim.BroadcastTv.eventbus.BookmarkEvent;
import zladnrms.defytech.kim.BroadcastTv.eventbus.RxBus;
import zladnrms.defytech.kim.BroadcastTv.model.LocalDataRepository;
import zladnrms.defytech.kim.BroadcastTv.model.LocalDataRepositoryModel;
import zladnrms.defytech.kim.BroadcastTv.model.ServerDataRepository;
import zladnrms.defytech.kim.BroadcastTv.model.ServerDataRepositoryModel;
import zladnrms.defytech.kim.BroadcastTv.model.domain.ChatInfo;
import zladnrms.defytech.kim.BroadcastTv.networking.RetrofitClient;
import zladnrms.defytech.kim.BroadcastTv.networking.response.BookmarkRepo;
import zladnrms.defytech.kim.BroadcastTv.networking.response.GetBookmarkRepo;
import zladnrms.defytech.kim.BroadcastTv.networking.response.UpdateDataRepo;

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
    public void upVideoCount(int videoId) {

            retrofitClient.getApi()
                    .upVideoCount(videoId)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<UpdateDataRepo>() {
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {

                        }

                        @Override
                        public void onNext(@NonNull UpdateDataRepo repo) {
                            for (int i = 0; i < repo.getResponse().size(); i++) {

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

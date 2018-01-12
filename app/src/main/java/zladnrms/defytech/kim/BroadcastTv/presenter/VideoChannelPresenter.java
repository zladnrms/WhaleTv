package zladnrms.defytech.kim.BroadcastTv.presenter;

import android.content.Context;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import zladnrms.defytech.kim.BroadcastTv.contract.VideoChannelContract;
import zladnrms.defytech.kim.BroadcastTv.model.LocalDataRepository;
import zladnrms.defytech.kim.BroadcastTv.model.LocalDataRepositoryModel;
import zladnrms.defytech.kim.BroadcastTv.model.domain.VideoInfo;
import zladnrms.defytech.kim.BroadcastTv.networking.RetrofitClient;
import zladnrms.defytech.kim.BroadcastTv.networking.response.VideoDataRepo;

/**
 * Created by kim on 2017-06-22.
 */

public class VideoChannelPresenter implements VideoChannelContract.Presenter{

    /* View */
    private VideoChannelContract.View view;

    /* Local DataBase */
    private LocalDataRepositoryModel localRepo;

    /* Retrofit2 */
    private RetrofitClient retrofitClient;

    public VideoChannelPresenter() {
        /* Init Retrofit2 */
        this.retrofitClient = new RetrofitClient();
    }

    @Override
    public void attachView(Object view) {

        this.view = (VideoChannelContract.View) view;
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
    public int getUserRoomId(Context context) {
        return localRepo.getUserRoomId(context);
    }

    @Override
    public String getUserNickname(Context context) {
        return localRepo.getUserNickname(context);
    }

    @Override
    public void getVideoList() {
        /* Roominfo Array For return */
        ArrayList<VideoInfo> videoArr = new ArrayList<VideoInfo>();

        retrofitClient.getApi()
                .videoData()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<VideoDataRepo>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull VideoDataRepo repo) {
                        for (int i = 0; i < repo.getResponse().size(); i++) {
                            if(repo.getResponse().get(i).getFilename() != null) {
                                    VideoInfo videoInfo = new VideoInfo(repo.getResponse().get(i).getVideoId(), repo.getResponse().get(i).getStreamerId(), repo.getResponse().get(i).getStreamerNickname(), repo.getResponse().get(i).getSubject(), repo.getResponse().get(i).getFilename(), repo.getResponse().get(i).getRecordDate(), repo.getResponse().get(i).getViewCount(), repo.getResponse().get(i).getStatus());
                                    videoArr.add(videoInfo);
                            }
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Logger.t("VideoChannelPresenter-onError").d("에러 발생");
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        Logger.t("VideoChannelPresenter-onNext").d("onComplete");
                        view.getVideoData(videoArr);
                        view.refresh();

                    }
                });
    }

}

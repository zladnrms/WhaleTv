package zladnrms.defytech.kim.BroadcastTv.presenter;

import android.content.Context;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import zladnrms.defytech.kim.BroadcastTv.contract.MyVideoContract;
import zladnrms.defytech.kim.BroadcastTv.model.LocalDataRepository;
import zladnrms.defytech.kim.BroadcastTv.model.LocalDataRepositoryModel;
import zladnrms.defytech.kim.BroadcastTv.model.domain.VideoInfo;
import zladnrms.defytech.kim.BroadcastTv.networking.RetrofitClient;
import zladnrms.defytech.kim.BroadcastTv.networking.response.VideoDataRepo;

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

    @Override
    public void clear() {
        view.clear();
    }

    @Override
    public void refresh() {
        view.refresh();
    }

    @Override
    public void getVideoList(Context context) {
        /* 비디오 목록 가져오기 */
        String nickname = localRepo.getUserNickname(context);

        /* Roominfo Array For return */
        ArrayList<VideoInfo> videoArr = new ArrayList<VideoInfo>();

        retrofitClient.getApi()
                .videoData(nickname)
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

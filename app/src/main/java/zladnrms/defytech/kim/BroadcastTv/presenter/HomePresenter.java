package zladnrms.defytech.kim.BroadcastTv.presenter;

import android.content.Context;

import com.orhanobut.logger.Logger;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import zladnrms.defytech.kim.BroadcastTv.contract.HomeContract;
import zladnrms.defytech.kim.BroadcastTv.model.LocalDataRepository;
import zladnrms.defytech.kim.BroadcastTv.model.LocalDataRepositoryModel;
import zladnrms.defytech.kim.BroadcastTv.model.domain.RoomInfo;
import zladnrms.defytech.kim.BroadcastTv.networking.RetrofitClient;
import zladnrms.defytech.kim.BroadcastTv.networking.response.RoomDataRepo;
import zladnrms.defytech.kim.BroadcastTv.networking.response.ViewerDataRepo;

/**
 * Created by kim on 2017-06-22.
 */

public class HomePresenter implements HomeContract.Presenter {

    /* View */
    private HomeContract.View view;

    /* Local DataBase */
    private LocalDataRepositoryModel localRepo;

    /* Retrofit2 */
    private RetrofitClient retrofitClient;

    public HomePresenter() {
        /* Init Retrofit2 */
        this.retrofitClient = new RetrofitClient();
    }

    @Override
    public void attachView(Object view) {
        this.view = (HomeContract.View) view;
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
    public void getRoomList() {
        /* Roominfo Array For return */
        //ArrayList<RoomInfo> roomArr = new ArrayList<RoomInfo>();

        retrofitClient.getApi()
                .roomData()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<RoomDataRepo>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull RoomDataRepo repo) {
                        for (int i = 0; i < repo.getResponse().size(); i++) {
                            if (repo.getResponse().get(i).getRoomId() != null) {
                                Logger.t("HomePresenter-onNext").d(repo.getResponse().get(i).getRoomId() + "," + repo.getResponse().get(i).getStreamerId() + "," + repo.getResponse().get(i).getStreamerNickname() + "," + repo.getResponse().get(i).getViewer() + "," + repo.getResponse().get(i).getSubject() + "," + repo.getResponse().get(i).getStatus());
                                if (Integer.valueOf(repo.getResponse().get(i).getStatus()) != 0) {
                                    RoomInfo roomInfo = new RoomInfo(Integer.valueOf(repo.getResponse().get(i).getRoomId()), repo.getResponse().get(i).getStreamerId(), repo.getResponse().get(i).getStreamerNickname(), repo.getResponse().get(i).getViewer(), repo.getResponse().get(i).getSubject(), repo.getResponse().get(i).getViewerCount());
                                    view.getRoomData(roomInfo);
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Logger.t("HomePresenter-onError").d("에러 발생");
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        Logger.t("HomePresenter-onNext").d("onComplete");
                        view.refresh();
                    }
                });
    }

    @Override
    public void getRoomViewer(int roomId) {

        retrofitClient.getApi()
                .viewerData(roomId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ViewerDataRepo>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        System.out.println("섭스크라이브");
                    }

                    @Override
                    public void onNext(@NonNull ViewerDataRepo repo) {
                        if (repo.getResponse().get(0) != null) {
                            Logger.t("StreamingListPresenter-addRoom-onNext").d(repo.getResponse().get(0).getViewer());
                        } else {
                            Logger.d("널");
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }
}

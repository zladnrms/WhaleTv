package zladnrms.defytech.kim.BroadcastTv.presenter;

import android.content.Context;

import com.google.firebase.iid.FirebaseInstanceId;
import com.orhanobut.logger.Logger;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import zladnrms.defytech.kim.BroadcastTv.Contract.MainContract;
import zladnrms.defytech.kim.BroadcastTv.model.LocalDataRepository;
import zladnrms.defytech.kim.BroadcastTv.model.LocalDataRepositoryModel;
import zladnrms.defytech.kim.BroadcastTv.networking.RetrofitClient;
import zladnrms.defytech.kim.BroadcastTv.networking.response.FCMTokenRepo;

/**
 * Created by kim on 2017-06-22.
 */

public class MainPresenter implements MainContract.Presenter{

    /* View */
    private MainContract.View view;

    /* Local DataBase */
    private LocalDataRepositoryModel localRepo;

    /* Retrofit2 */
    private RetrofitClient retrofitClient;

    public MainPresenter() {
        /* Init Retrofit2 */
        this.retrofitClient = new RetrofitClient();
    }

    public void attachView(Object view) {
        this.view = (MainContract.View) view;
        this.localRepo = LocalDataRepository.getInstance();
    }

    @Override
    public void detachView(Object view) {
        this.view = null;
    }

    @Override
    public void realmClose() {
        this.localRepo.realmClose();
    }

    @Override
    public void saveUserNickname(Context context, String nickname) {
        localRepo.saveUserNickname(context, nickname);
    }

    @Override
    public void saveUserRoomId(Context context, int roomId) {
        localRepo.saveUserRoomId(context, roomId);
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
    public void sendFCMToken(Context context) {
        String nickname = localRepo.getUserNickname(context);

        String token = FirebaseInstanceId.getInstance().getToken();
        Logger.t("sendFCMToken").d(token);

        retrofitClient.getApi()
                .saveToken(nickname, token)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<FCMTokenRepo>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull FCMTokenRepo repo) {

                        for (int i = 0; i < repo.getResponse().size(); i++) {

                            if (repo.getResponse().get(i).getError() == null) {

                                if (repo.getResponse().get(i).getResult() != null) {
                                    Logger.t("sendFCMToken-onNext").d(repo.getResponse().get(i).getResult());
                                    if (repo.getResponse().get(i).getResult().equals("success")) {
                                        Logger.t("sendFCMToken-onNext").d("FCM 토큰 저장 완료");
                                    }
                                }
                            }

                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Logger.t("MainPresenter-onError").d("에러 발생");
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        Logger.t("sendFCMToken").d("Complete");
                    }
                });
    }

    @Override
    public void logout() {
        localRepo.deleteUserLoginInfo();
    }
}

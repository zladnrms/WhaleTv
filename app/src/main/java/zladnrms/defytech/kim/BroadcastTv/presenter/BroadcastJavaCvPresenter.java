package zladnrms.defytech.kim.BroadcastTv.presenter;

import android.content.Context;
import android.widget.Toast;

import com.orhanobut.logger.Logger;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import zladnrms.defytech.kim.BroadcastTv.contract.BroadcastJavaCvContract;
import zladnrms.defytech.kim.BroadcastTv.model.LocalDataRepository;
import zladnrms.defytech.kim.BroadcastTv.model.LocalDataRepositoryModel;
import zladnrms.defytech.kim.BroadcastTv.model.ServerDataRepository;
import zladnrms.defytech.kim.BroadcastTv.model.ServerDataRepositoryModel;
import zladnrms.defytech.kim.BroadcastTv.model.domain.ChatInfo;
import zladnrms.defytech.kim.BroadcastTv.model.domain.LoginData;
import zladnrms.defytech.kim.BroadcastTv.networking.RetrofitClient;
import zladnrms.defytech.kim.BroadcastTv.networking.response.EndDataRepo;
import zladnrms.defytech.kim.BroadcastTv.networking.response.FCMRepo;
import zladnrms.defytech.kim.BroadcastTv.networking.response.UpdateDataRepo;

/**
 * Created by kim on 2017-06-22.
 */

public class BroadcastJavaCvPresenter implements BroadcastJavaCvContract.Presenter {

    /* View */
    private BroadcastJavaCvContract.View view;

    /* Local DataBase */
    private LocalDataRepositoryModel localRepo;

    /* Server DataBase */
    private ServerDataRepositoryModel serverRepo;

    /* Retrofit2 */
    private RetrofitClient retrofitClient;

    public BroadcastJavaCvPresenter() {
        /* Init Retrofit2 */
        this.retrofitClient = new RetrofitClient();
    }

    @Override
    public void attachView(Object view) {
        this.view = (BroadcastJavaCvContract.View) view;
        this.localRepo = LocalDataRepository.getInstance();
        this.serverRepo = ServerDataRepository.getInstance();
    }

    @Override
    public void detachView(Object view) {
        this.view = null;
    }

    @Override
    public void saveUserRoomId(Context context, int roomId) {
        localRepo.saveUserRoomId(context, roomId);
    }

    @Override
    public void addChat(ChatInfo chatInfo) {
        view.addChat(chatInfo);
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
    public String getUserId() {
        LoginData data = this.localRepo.loadUserLoginInfo();
        return data.getId();
    }

    @Override
    public void updateBroadcastStatus(int roomId) {

        retrofitClient.getApi()
                .updateData(roomId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<UpdateDataRepo>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull UpdateDataRepo repo) {

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Logger.t("BroadcastJavaCvPresenter-onError").d("에러 발생");
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        Logger.t("BroadcastJavaCvPresenter-onNext").d("onComplete");
                    }
                });
    }

    @Override
    public void delBroadcastRoom(int roomId, String id, String nickname) {

        retrofitClient.getApi()
                .endData(roomId, id, nickname)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<EndDataRepo>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull EndDataRepo repo) {

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Logger.t("BroadcastJavaCvPresenter-onError").d("에러 발생");
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        Logger.t("BroadcastJavaCvPresenter-onNext").d("onComplete");
                    }
                });
    }

    @Override
    public void pushBookmark(Context context, String message) {
        retrofitClient.getApi()
                .pushBookmark(localRepo.getUserNickname(context), message)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<FCMRepo>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull FCMRepo repo) {

                        for (int i = 0; i < repo.getResponse().size(); i++) {

                            if (repo.getResponse().get(i).getResult() != null) {

                                Logger.t("BroadcastJavaCvPresenter-onNext").d(repo.getResponse().get(i).getResult());
                                if (repo.getResponse().get(i).getResult().equals("success")) {
                                    Toast.makeText(context, "팬들에게 방송 시작 알림을 보냈습니다", Toast.LENGTH_SHORT);
                                } else {
                                    Toast.makeText(context, "알림을 보낼 팬이 없습니다", Toast.LENGTH_SHORT);
                                }
                            } else {

                            }

                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Logger.t("BroadcastJavaCvPresenter-onError").d("에러 발생");
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}

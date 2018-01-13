package zladnrms.defytech.kim.BroadcastTv.presenter;

import android.content.Context;
import android.widget.Toast;

import com.orhanobut.logger.Logger;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import zladnrms.defytech.kim.BroadcastTv.contract.BroadcastContract;
import zladnrms.defytech.kim.BroadcastTv.model.LocalDataRepository;
import zladnrms.defytech.kim.BroadcastTv.model.LocalDataRepositoryModel;
import zladnrms.defytech.kim.BroadcastTv.model.ServerDataRepository;
import zladnrms.defytech.kim.BroadcastTv.model.ServerDataRepositoryModel;
import zladnrms.defytech.kim.BroadcastTv.model.domain.ChatInfo;
import zladnrms.defytech.kim.BroadcastTv.model.domain.LoginData;
import zladnrms.defytech.kim.BroadcastTv.networking.RetrofitClient;
import zladnrms.defytech.kim.BroadcastTv.networking.response.EndDataRepo;
import zladnrms.defytech.kim.BroadcastTv.networking.response.ResultRepo;
import zladnrms.defytech.kim.BroadcastTv.networking.response.StartDataRepo;

/**
 * Created by kim on 2017-06-22.
 */

public class BroadcastPresenter implements BroadcastContract.Presenter {

    /* View */
    private BroadcastContract.View view;

    /* Local DataBase */
    private LocalDataRepositoryModel localRepo;

    /* Server DataBase */
    private ServerDataRepositoryModel serverRepo;

    /* Retrofit2 */
    private RetrofitClient retrofitClient;

    public BroadcastPresenter() {
        /* Init Retrofit2 */
        this.retrofitClient = new RetrofitClient();
    }

    @Override
    public void attachView(Object view) {
        this.view = (BroadcastContract.View) view;
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
    public int setViewerCount(String type, String chat) {
        /* 방송 중 시청자 수 갱신 (채팅을 받아 처리) */
        if (type.equals("알림") && chat.contains("님이 입장하셨습니다")) { /* 입장 채팅 일 경우 */
            return 1;
        } else if (type.equals("알림") && chat.contains("님이 나가셨습니다")) { /* 퇴장 채팅 일 경우 */
            return -1;
        } else { /* 일반 채팅 일 경우 */
            return 0;
        }
    }

    @Override
    public void startBroadcast(String subject, String id, String nickname) {

        /* index 0 : result, index 1 : roomId */
        int[] result = new int[2];

        retrofitClient.getApi()
                .startData(nickname, id, subject)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<StartDataRepo>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull StartDataRepo repo) {
                        if (repo.getResponse().get(0) != null) {
                            Logger.t("BroadcastPresenter-startBroadcast-onNext").d(repo.getResponse().get(0).getResult() + "," + repo.getResponse().get(0).getRoomId());
                            if (repo.getResponse().get(0).getResult().equals("success")) {
                                result[0] = 1; /* success */
                                result[1] = Integer.valueOf(repo.getResponse().get(0).getRoomId());
                            }
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Logger.t("BroadcastPresenter-startBroadcast-onError").d("에러 발생");
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        if (result[0] == 1) {
                            view.startBroadcastCallBack(result[1]);
                        }
                    }
                });
    }

    @Override
    public void updateBroadcastStatus(int roomId) {

        retrofitClient.getApi()
                .updateData(roomId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResultRepo>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull ResultRepo repo) {
                        Logger.d("BroadcastPresenter-update-onNExt");
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Logger.t("BroadcastPresenter-update-onError").d("에러 발생");
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        Logger.t("BroadcastPresenter-update-onComplete").d("onComplete");
                    }
                });
    }

    @Override
    public void delBroadcastRoom(Context context, boolean recording, int roomId, String id, String nickname, int castTime) {

        retrofitClient.getApi()
                .endData(roomId, id, nickname, castTime)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<EndDataRepo>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull EndDataRepo repo) {
                        for (int i = 0; i < repo.getResponse().size(); i++) {

                            if (repo.getResponse().get(i).getResult() != null) {

                                String result = repo.getResponse().get(i).getResult();
                                int record = repo.getResponse().get(i).getRecord();

                                Logger.t("BroadcastPresenter-onNext").d(repo.getResponse().get(i).getResult());
                                if (recording) {
                                    if (result.equals("success") && record == 1) {
                                        Toast.makeText(context, "방송을 종료합니다. 녹화본이 저장되었습니다.", Toast.LENGTH_SHORT).show();
                                    } else if (result.equals("success") && record == 0) {
                                        Toast.makeText(context, "방송을 종료합니다. 30초 내의 방송은 녹화되지 않습니다..", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }

                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Logger.t("BroadcastPresenter-onError").d("에러 발생");
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        Logger.t("BroadcastPresenter-onNext").d("onComplete");
                    }
                });
    }

    @Override
    public void pushBookmark(Context context, String message) {
        String nickname = localRepo.getUserNickname(context);

        retrofitClient.getApi()
                .pushBookmark(nickname, message)
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

                                Logger.t("BroadcastPresenter-onNext").d(repo.getResponse().get(i).getResult());
                                if (repo.getResponse().get(i).getResult().equals("success")) {
                                    Toast.makeText(context, "팬들에게 방송 시작 알림을 보냈습니다", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, "알림을 보낼 팬이 없습니다", Toast.LENGTH_SHORT).show();
                                }
                            }

                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Logger.t("BroadcastPresenter-onError").d("에러 발생");
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void changeSubject(Context context, String subject) {
        int roomId = localRepo.getUserRoomId(context);

        retrofitClient.getApi()
                .changeSubject(roomId, subject)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResultRepo>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull ResultRepo repo) {
                        Logger.d("BroadcastPresenter-update-onNExt");
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Logger.t("BroadcastPresenter-update-onError").d("에러 발생");
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        Logger.t("BroadcastPresenter-update-onComplete").d("onComplete");
                        view.changeSubjectCallback(roomId, subject);
                    }
                });
    }
}

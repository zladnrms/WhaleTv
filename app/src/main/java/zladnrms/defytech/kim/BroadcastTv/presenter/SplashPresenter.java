package zladnrms.defytech.kim.BroadcastTv.presenter;

import android.content.Context;

import com.orhanobut.logger.Logger;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import zladnrms.defytech.kim.BroadcastTv.contract.SplashContract;
import zladnrms.defytech.kim.BroadcastTv.eventbus.LoginEvent;
import zladnrms.defytech.kim.BroadcastTv.eventbus.RxBus;
import zladnrms.defytech.kim.BroadcastTv.model.LocalDataRepository;
import zladnrms.defytech.kim.BroadcastTv.model.LocalDataRepositoryModel;
import zladnrms.defytech.kim.BroadcastTv.model.domain.LoginData;
import zladnrms.defytech.kim.BroadcastTv.networking.RetrofitClient;
import zladnrms.defytech.kim.BroadcastTv.networking.response.LoginDataRepo;

/**
 * Created by kim on 2017-06-22.
 */

public class SplashPresenter implements SplashContract.Presenter{

    private SplashContract.View view;
    private LocalDataRepositoryModel localRepo;

    /* Retrofit2 */
    private RetrofitClient retrofitClient;

    public SplashPresenter() {
        /* Init Retrofit2 */
        this.retrofitClient = new RetrofitClient();
    }

    @Override
    public void attachView(Object view) {
        this.view = (SplashContract.View) view;
        this.localRepo = LocalDataRepository.getInstance();
    }

    @Override
    public void detachView(Object view) {
        this.view = null;
    }

    public void realmClose() {
        this.localRepo.realmClose();
    }

    public void autoLogin(Context context) {
        /* User Data from local DB*/
        if(this.localRepo.loadUserLoginInfo() == null) {
            /* 자동 로그인 기록 없음 */
            view.whenNoLoginData();
        } else {
            LoginData data = this.localRepo.loadUserLoginInfo();
            login(context, data.getId(), data.getPassword());
        }
    }

    @Override
    public void login(Context context, String id, String password) {
        /* 로그인 */
        retrofitClient.getApi()
                .autoLoginData(id, password)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<LoginDataRepo>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                    }

                    @Override
                    public void onNext(@NonNull LoginDataRepo repo) {
                        for (int i = 0; i < repo.getResponse().size(); i++) {
                            if(repo.getResponse().get(i).getResult() != null) {
                                Logger.t("SplashPresenter-onNext").d(repo.getResponse().get(i).getResult() + "," + repo.getResponse().get(i).getNickname() + "," + repo.getResponse().get(i).getSha256pw());
                                String result = repo.getResponse().get(i).getResult();
                                String nickname = repo.getResponse().get(i).getNickname();

                                if (result.equals("success")) {
                                    // 자동로그인에선 Realm에 저장하지 않음
                                    localRepo.saveUserNickname(context, nickname);
                                    autoLoginResultSend("success", nickname);
                                } else if (result.equals("miss_id")) {
                                    autoLoginResultSend("miss_id", null);
                                } else if (result.equals("miss_password")) {
                                    autoLoginResultSend("miss_password", null);
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Logger.t("SplashPresenter-onError").d("에러 발생");
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        Logger.t("SplashPresenter-onNext").d("onComplete");

                    }
                });
    }

    /* Event Bus */
    @Override
    public void autoLoginResultSend(String result, String nickname) {
        RxBus.get().post("splash", new LoginEvent(result, nickname));
    }
}

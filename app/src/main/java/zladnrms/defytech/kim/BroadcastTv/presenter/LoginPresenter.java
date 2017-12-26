package zladnrms.defytech.kim.BroadcastTv.presenter;

import android.content.Context;
import android.support.annotation.NonNull;

import com.orhanobut.logger.Logger;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import zladnrms.defytech.kim.BroadcastTv.Contract.LoginContract;
import zladnrms.defytech.kim.BroadcastTv.eventbus.LoginEvent;
import zladnrms.defytech.kim.BroadcastTv.eventbus.RxBus;
import zladnrms.defytech.kim.BroadcastTv.model.LocalDataRepository;
import zladnrms.defytech.kim.BroadcastTv.model.LocalDataRepositoryModel;
import zladnrms.defytech.kim.BroadcastTv.networking.RetrofitClient;
import zladnrms.defytech.kim.BroadcastTv.networking.response.LoginDataRepo;

/**
 * Created by kim on 2017-06-22.
 */

public class LoginPresenter implements LoginContract.Presenter {

    private LoginContract.View view;
    private LocalDataRepositoryModel localRepo;

    /* Retrofit2 */
    private RetrofitClient retrofitClient;

    public LoginPresenter() {
        /* Init Retrofit2 */
        this.retrofitClient = new RetrofitClient();
    }

    @Override
    public void attachView(Object view) {
        this.view = (LoginContract.View )view;
        this.localRepo = LocalDataRepository.getInstance();
    }

    @Override
    public void detachView(Object view) {
        this.view = null;
    }

    @Override
    public void login(Context context, String id, String password) {
        retrofitClient.getApi()
                .loginData(id, password)
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
                                Logger.t("LoginFragmentPresenter-onNext").d(repo.getResponse().get(i).getResult() + "," + repo.getResponse().get(i).getNickname() + "," + repo.getResponse().get(i).getSha256pw());
                                String result = repo.getResponse().get(i).getResult();
                                String id = repo.getResponse().get(i).getId();
                                String nickname = repo.getResponse().get(i).getNickname();
                                String sha256pw = repo.getResponse().get(i).getSha256pw();

                                if (result.equals("success")) {
                                    localRepo.saveUserLoginInfo(context, id, sha256pw, nickname);
                                    localRepo.saveUserNickname(context, nickname);
                                    LoginResultSend("success", nickname);
                                } else if (result.equals("miss_id")) {
                                    LoginResultSend("miss_id", null);
                                } else if (result.equals("miss_password")) {
                                    LoginResultSend("miss_password", null);
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Logger.t("LoginPresenter-onError").d("에러 발생");
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        Logger.t("LoginFragmentPresenter-onNext").d("onComplete");

                    }
                });
    }

    /* Event Bus */
    @Override
    public void LoginResultSend(String result, String nickname) {
        RxBus.get().post("login", new LoginEvent(result, nickname));
    }
}

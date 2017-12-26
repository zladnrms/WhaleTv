package zladnrms.defytech.kim.BroadcastTv.presenter;

import android.content.Context;

import com.orhanobut.logger.Logger;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import zladnrms.defytech.kim.BroadcastTv.Contract.JoinContract;
import zladnrms.defytech.kim.BroadcastTv.eventbus.RxBus;
import zladnrms.defytech.kim.BroadcastTv.model.LocalDataRepository;
import zladnrms.defytech.kim.BroadcastTv.model.LocalDataRepositoryModel;
import zladnrms.defytech.kim.BroadcastTv.networking.RetrofitClient;
import zladnrms.defytech.kim.BroadcastTv.networking.response.JoinDataRepo;

/**
 * Created by kim on 2017-06-22.
 */

public class JoinPresenter implements JoinContract.Presenter {

    private JoinContract.View view;
    private LocalDataRepositoryModel localRepo;

    /* Retrofit2 */
    private RetrofitClient retrofitClient;

    public JoinPresenter() {
        /* Init Retrofit2 */
        this.retrofitClient = new RetrofitClient();
    }

    @Override
    public void attachView(Object view) {
        this.view = (JoinContract.View) view;
        this.localRepo = LocalDataRepository.getInstance();
    }

    @Override
    public void detachView(Object view) {
        this.view = null;
    }

    @Override
    public void join(Context context, String id, String password, String nickname) {
        retrofitClient.getApi()
                .joinData(id, password, nickname)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JoinDataRepo>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull JoinDataRepo repo) {
                        for (int i = 0; i < repo.getResponse().size(); i++) {
                            if (repo.getResponse().get(i).getResult() != null) {
                                Logger.t("JoinPresenter-onNext").d(repo.getResponse().get(i).getResult());
                                String result = repo.getResponse().get(i).getResult();

                                if (result.equals("success")) {
                                    JoinResultSend("success");
                                } else if (result.equals("already_id")) {
                                    JoinResultSend("already_id");
                                } else if (result.equals("already_nickname")) {
                                    JoinResultSend("already_nickname");
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Logger.t("JoinPresenter-onError").d("에러 발생");
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        Logger.t("JoinPresenter-onNext").d("onComplete");

                    }
                });

    }

    /* Event Bus */
    @Override
    public void JoinResultSend(String result){
        RxBus.get().post("join", result);
    }
}

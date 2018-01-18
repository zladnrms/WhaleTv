package zladnrms.defytech.kim.BroadcastTv.presenter;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.widget.Toast;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import zladnrms.defytech.kim.BroadcastTv.contract.ViewerContract;
import zladnrms.defytech.kim.BroadcastTv.eventbus.BookmarkEvent;
import zladnrms.defytech.kim.BroadcastTv.eventbus.RxBus;
import zladnrms.defytech.kim.BroadcastTv.eventbus.ViewerCountEvent;
import zladnrms.defytech.kim.BroadcastTv.model.LocalDataRepository;
import zladnrms.defytech.kim.BroadcastTv.model.LocalDataRepositoryModel;
import zladnrms.defytech.kim.BroadcastTv.model.ServerDataRepository;
import zladnrms.defytech.kim.BroadcastTv.model.ServerDataRepositoryModel;
import zladnrms.defytech.kim.BroadcastTv.model.domain.ChatInfo;
import zladnrms.defytech.kim.BroadcastTv.networking.RetrofitClient;
import zladnrms.defytech.kim.BroadcastTv.networking.response.BookmarkRepo;
import zladnrms.defytech.kim.BroadcastTv.networking.response.GetBookmarkRepo;
import zladnrms.defytech.kim.BroadcastTv.networking.response.ViewerCountRepo;

/**
 * Created by kim on 2017-06-22.
 */

public class ViewerPresenter implements ViewerContract.Presenter {

    /* View */
    private ViewerContract.View view;

    /* Local DataBase */
    private LocalDataRepositoryModel localRepo;

    /* Server DataBase */
    private ServerDataRepositoryModel serverRepo;

    /* Retrofit2 */
    private RetrofitClient retrofitClient;

    public ViewerPresenter() {
        /* Init Retrofit2 */
        this.retrofitClient = new RetrofitClient();
    }

    @Override
    public void attachView(Object view) {
        this.view = (ViewerContract.View) view;
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
    public void changeMode() {
        view.changeMode();
    }

    @Override
    public int getDeviceHeight(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        return height;
    }

    @Override
    public void getViewerCount(Context context, int roomId) {
        retrofitClient.getApi()
                .getViewerCount(roomId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ViewerCountRepo>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull ViewerCountRepo repo) {

                        for (int i = 0; i < repo.getResponse().size(); i++) {
                            String result = repo.getResponse().get(i).getResult();
                            int viewerCount = repo.getResponse().get(i).getViewerCount();

                            if (result.equals("success")) {
                                ViewerCountRefreshSend(viewerCount);
                            }
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Logger.t("ViewerPresenter-onError").d("에러 발생");
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void addBookmark(Context context, String streamerNickname) {
        String nickname = localRepo.getUserNickname(context);

        retrofitClient.getApi()
                .addBookmark(nickname, streamerNickname)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BookmarkRepo>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull BookmarkRepo repo) {

                        for (int i = 0; i < repo.getResponse().size(); i++) {

                            if (repo.getResponse().get(i).getError() == null) {

                                if (repo.getResponse().get(i).getResult() != null) {
                                    Logger.t("ViewerPresenter-onNext").d(repo.getResponse().get(i).getResult());
                                    if (repo.getResponse().get(i).getResult().equals("success")) {
                                        Toast.makeText(context, streamerNickname + "님을 즐겨찾기하였습니다", Toast.LENGTH_SHORT).show();
                                        view.bookmarkrefresh();
                                    }
                                }
                            }

                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Logger.t("ViewerPresenter-onError").d("에러 발생");
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void delBookmark(Context context, String streamerNickname) {
        String nickname = localRepo.getUserNickname(context);

        retrofitClient.getApi()
                .delBookmark(nickname, streamerNickname)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BookmarkRepo>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull BookmarkRepo repo) {

                        for (int i = 0; i < repo.getResponse().size(); i++) {

                            if (repo.getResponse().get(i).getError() == null) {

                                if (repo.getResponse().get(i).getResult() != null) {
                                    Logger.t("ViewerPresenter-onNext").d(repo.getResponse().get(i).getResult());
                                    if (repo.getResponse().get(i).getResult().equals("success")) {
                                        Toast.makeText(context, streamerNickname + "님을 즐겨찾기 제거하였습니다", Toast.LENGTH_SHORT).show();
                                        view.bookmarkrefresh();
                                    }
                                }

                            } else {
                                // 에러 났음
                                Logger.t("bookmarktest").d("에러남");
                            }
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Logger.t("ViewerPresenter-onError").d("에러 발생");
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void getBookmark(Context context) {
        String nickname = localRepo.getUserNickname(context);

        retrofitClient.getApi()
                .getBookmark(nickname)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GetBookmarkRepo>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull GetBookmarkRepo repo) {
                        for (int i = 0; i < repo.getResponse().size(); i++) {
                            if (repo.getResponse().get(i).getResult() != null) {
                                if (repo.getResponse().get(i).getResult().equals("success")) {
                                    ArrayList<String> arrList = repo.getResponse().get(i).getBookmark();

                                    for (int j = 0; j < arrList.size(); j++) {
                                        String bookmark_nickname = arrList.get(j);
                                        /* Bookmark 목록에서 하나 씩 뽑아내서 Activity에 보냄 */
                                        BookmarkRefreshSend(bookmark_nickname);
                                    }
                                } else {
                                    Logger.d(repo.getResponse().get(i).getResult() + " Result Failure");
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                        Logger.t("ViewerPresenter-onError").d("에러 발생");
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /* Event Bus */
    @Override
    public void BookmarkRefreshSend(String nickname) {
        RxBus.get().post("viewer", new BookmarkEvent(nickname));
    }

    @Override
    public void bookmarkrefresh() {
        view.bookmarkrefresh();
    }

    @Override
    public void ViewerCountRefreshSend(int viewerCount) {
        RxBus.get().post("viewer", new ViewerCountEvent(viewerCount));
    }
}

package zladnrms.defytech.kim.BroadcastTv.Contract;

import android.content.Context;

public interface SplashContract {
    interface View {
        void whenNoLoginData();
    }

     interface Presenter {
         void attachView(Object view);

         void detachView(Object view);

         void realmClose();

         void autoLogin(Context context);

         void login(Context context, String id, String password);

         void autoLoginResultSend(String result, String nickname);
     }
}
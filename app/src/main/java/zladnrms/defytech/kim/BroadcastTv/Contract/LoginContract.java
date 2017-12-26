package zladnrms.defytech.kim.BroadcastTv.Contract;

import android.content.Context;

public interface LoginContract {
    interface View {

    }

     interface Presenter {
         void attachView(Object view);

         void detachView(Object view);

         void login(Context context, String id, String password);

         void LoginResultSend(String result, String nickname);
     }
}
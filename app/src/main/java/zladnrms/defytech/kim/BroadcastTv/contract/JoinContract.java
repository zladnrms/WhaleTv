package zladnrms.defytech.kim.BroadcastTv.contract;

import android.content.Context;

public interface JoinContract {
    interface View {

    }

     interface Presenter {
         void attachView(Object view);

         void detachView(Object view);

         void join(Context context, String id, String password, String nickname);

         void JoinResultSend(String result);
     }
}
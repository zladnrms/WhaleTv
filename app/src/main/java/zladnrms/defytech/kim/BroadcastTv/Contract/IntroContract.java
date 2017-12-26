package zladnrms.defytech.kim.BroadcastTv.Contract;

import android.content.Context;

public interface IntroContract {
    interface View {

    }

     interface Presenter {
         void attachView(Object view);

         void detachView(Object view);
     }
}
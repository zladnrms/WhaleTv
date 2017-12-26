package zladnrms.defytech.kim.BroadcastTv.Contract;

import android.content.Context;

import java.util.ArrayList;

public interface MyVideoContract {
    interface View {
        /* 방 목록 초기화 */
        void clear();

        /* 방 목록 새로고침 */
        void refresh();
    }

     interface Presenter {
         void attachView(Object view);

         void detachView(Object view);
     }
}
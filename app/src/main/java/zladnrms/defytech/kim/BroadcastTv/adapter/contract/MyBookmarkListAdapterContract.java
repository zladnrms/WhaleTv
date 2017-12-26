package zladnrms.defytech.kim.BroadcastTv.adapter.contract;

import android.content.Context;

public interface MyBookmarkListAdapterContract {
    interface View {
        /* 데이터 갱신 */
        void refresh();
    }

     interface Presenter {
         void attachView(Object view);

         void detachView(Object view);

         void delete(Context context, String streamerNickname);
     }
}
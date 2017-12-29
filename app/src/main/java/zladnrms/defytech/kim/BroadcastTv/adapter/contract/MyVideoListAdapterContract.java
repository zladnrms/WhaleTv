package zladnrms.defytech.kim.BroadcastTv.adapter.contract;

import android.content.Context;

public interface MyVideoListAdapterContract {
    interface View {
        /* 데이터 갱신 */
        void refresh();
    }

     interface Presenter {
         void attachView(Object view);

         void detachView(Object view);

         void delete(Context context, int videoId, String filename);
     }
}
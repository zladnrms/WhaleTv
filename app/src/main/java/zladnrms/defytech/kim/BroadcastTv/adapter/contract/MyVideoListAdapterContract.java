package zladnrms.defytech.kim.BroadcastTv.adapter.contract;

import android.content.Context;

import java.util.ArrayList;

import zladnrms.defytech.kim.BroadcastTv.model.domain.VideoInfo;

public interface MyVideoListAdapterContract {
    interface View {
        /* 데이터 갱신 */
        void refresh();

        void clear();
    }

     interface Presenter {
         void attachView(Object view);

         void detachView(Object view);

         void clear();

         void changeStatus(Context context, int videoId, int status);

         void adjust(Context context, int videoId, String subject);

         void delete(Context context, int videoId, String filename);

         void refresh();

     }
}
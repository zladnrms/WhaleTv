package zladnrms.defytech.kim.BroadcastTv.Contract;

import android.content.Context;

import zladnrms.defytech.kim.BroadcastTv.model.domain.ChatInfo;

public interface VideoViewerContract {
    interface View {
        /* 가로 세로 모드 변경 */
        void changeMode();

    }

     interface Presenter {
         void attachView(Object view);

         void detachView(Object view);

         void changeMode();

         void upVideoCount(int videoId);
     }
}
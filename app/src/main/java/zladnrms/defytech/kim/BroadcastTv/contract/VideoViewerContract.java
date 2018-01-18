package zladnrms.defytech.kim.BroadcastTv.contract;

import android.content.Context;

public interface VideoViewerContract {
    interface View {
        /* 가로 세로 모드 변경 */
        void changeMode();

    }

     interface Presenter {
         void attachView(Object view);

         void detachView(Object view);

         void changeMode();

         int getDeviceHeight(Context context);

         void upVideoCount(int videoId);
     }
}
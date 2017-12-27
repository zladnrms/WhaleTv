package zladnrms.defytech.kim.BroadcastTv.contract;

import android.content.Context;

import java.util.ArrayList;

import zladnrms.defytech.kim.BroadcastTv.model.domain.VideoInfo;

public interface MyVideoContract {
    interface View {
        /* 비디오 목록 초기화 */
        void clear();

        /* 비디오 목록 가져오기 */
        void getVideoData(ArrayList<VideoInfo> video);

        /* 비디오 목록 새로고침 */
        void refresh();
    }

     interface Presenter {
         void attachView(Object view);

         void detachView(Object view);

         void clear();

         void refresh();

         void getVideoList(Context context);
     }
}
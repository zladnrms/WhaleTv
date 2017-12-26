package zladnrms.defytech.kim.BroadcastTv.Contract;

import android.content.Context;

import java.util.ArrayList;

import zladnrms.defytech.kim.BroadcastTv.model.domain.RoomInfo;

public interface MyBookmarkContract {
    interface View {
        /* 즐겨찾기 목록 초기화 */
        void clear();

        /* 즐겨찾기 목록 가져오기 */
        void getBookmarkData(String nickname);

        /* 즐겨찾기 목록 새로고침 */
        void refresh();

    }

     interface Presenter {
         void attachView(Object view);

         void detachView(Object view);

         void clear();

         void refresh();

         void getUserBookmarkList(Context context);
     }
}
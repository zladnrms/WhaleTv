package zladnrms.defytech.kim.BroadcastTv.contract;

import android.content.Context;

import zladnrms.defytech.kim.BroadcastTv.model.domain.RoomInfo;

public interface HomeContract {
        interface View {
            /* 방 목록 초기화 */
            void clear();

            /* 방 목록 가져오기 */
            void getRoomData(RoomInfo roomInfo);

            /* 방 목록 새로고침 */
            void refresh();
    }

     interface Presenter {

         void attachView(Object view);

         void detachView(Object view);

         void clear();

         void refresh();

         int getUserRoomId(Context context);

         String getUserNickname(Context context);

         void getRoomList();
    }
}
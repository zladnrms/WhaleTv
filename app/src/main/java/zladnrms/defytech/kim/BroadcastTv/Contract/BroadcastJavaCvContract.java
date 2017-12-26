package zladnrms.defytech.kim.BroadcastTv.Contract;

import android.content.Context;

import zladnrms.defytech.kim.BroadcastTv.model.domain.ChatInfo;
import zladnrms.defytech.kim.BroadcastTv.model.domain.LoginData;

public interface BroadcastJavaCvContract {
    interface View {
        /* 채팅 목록 초기화 */
        void clear();

        /* 채팅 목록 새로고침 */
        void refresh();

        /* 채팅 리스트에 추가 */
        void addChat(ChatInfo chatInfo);

        /* 네티에서 사용됨 */
        String getUserNickname();

        /* 네티에서 사용됨 */
        int getUserRoomId();
    }

     interface Presenter {

         void attachView(Object view);

         void detachView(Object view);

         void saveUserRoomId(Context context, int roomId);

         void addChat(ChatInfo chatInfo);

         void clear();

         void refresh();

         int getUserRoomId(Context context);

         String getUserNickname(Context context);

         String getUserId();

         void updateBroadcastStatus(int roomId);

         void delBroadcastRoom(int roomId, String Id, String nickname);

         void pushBookmark(Context context, String message);
    }
}
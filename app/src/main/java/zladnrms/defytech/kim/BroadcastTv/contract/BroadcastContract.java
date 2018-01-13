package zladnrms.defytech.kim.BroadcastTv.contract;

import android.content.Context;

import zladnrms.defytech.kim.BroadcastTv.model.domain.ChatInfo;

public interface BroadcastContract {
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

        /* 방 제목 변경*/
        void changeSubject(String subject);

        void startBroadcastCallBack(int roomId);

        void etSubjectLock(boolean lock);

        void changeSubjectCallback(int roomId, String subject);
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

         int setViewerCount(String type, String chat);

         void updateBroadcastStatus(int roomId);

         void delBroadcastRoom(Context context, boolean recording, int roomId, String Id, String nickname, int castTime);

         void pushBookmark(Context context, String message);

         void changeSubject(Context context, String subject);

         void startBroadcast(String subject, String id, String nickname);
    }
}
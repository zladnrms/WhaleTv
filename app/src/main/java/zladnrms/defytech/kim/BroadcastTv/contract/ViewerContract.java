package zladnrms.defytech.kim.BroadcastTv.contract;

import android.content.Context;

import zladnrms.defytech.kim.BroadcastTv.model.domain.ChatInfo;

public interface ViewerContract {
    interface View {
        /* 채팅 목록 초기화 */
        void clear();

        /* 채팅 목록 새로고침 */
        void refresh();

        /* 채팅 리스트에 추가 */
        void addChat(ChatInfo chatInfo);

        /* 사용자 닉네임 가져오기 */
        String getUserNickname();

        /* 사용자 접속 방 번호 가져오기 */
        int getUserRoomId();

        /* 즐겨찾기 refresh */
        void bookmarkrefresh();

        /* 가로 세로 모드 변경 */
        void changeMode();

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

         void changeMode();

         int getDeviceHeight(Context context);

         void addBookmark(Context context, String streamer);

         void delBookmark(Context context, String streamer);

         void getBookmark(Context context);

         void BookmarkRefreshSend(String nickname);

         void bookmarkrefresh();

         void getViewerCount(Context context, int roomId);

         void ViewerCountRefreshSend(int viewerCount);
     }
}
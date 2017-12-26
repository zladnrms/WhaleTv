package zladnrms.defytech.kim.BroadcastTv.Contract;

import android.content.Context;

public interface MainContract {
    interface View {
        int getUserRoomId();

        String getUserNickname();
    }

     interface Presenter {
         void attachView(Object view);

         void detachView(Object view);

         void realmClose();

         void saveUserNickname(Context context, String nickname);

         void saveUserRoomId(Context context, int roomId);

         int getUserRoomId(Context context);

         String getUserNickname(Context context);

         void sendFCMToken(Context context);

         void logout();
    }
}
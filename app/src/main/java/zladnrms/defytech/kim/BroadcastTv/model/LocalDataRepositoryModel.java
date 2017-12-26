package zladnrms.defytech.kim.BroadcastTv.model;

import android.content.Context;

import java.util.ArrayList;

import io.realm.RealmResults;
import zladnrms.defytech.kim.BroadcastTv.model.domain.LoginData;

/**
 * Created by kim on 2017-06-22.
 */

public interface LocalDataRepositoryModel {

    void deleteUserLoginInfo();

    void saveUserLoginInfo(Context context, String id, String password, String nickname);

    LoginData loadUserLoginInfo();

    void saveUserNickname(Context context, String nickname);

    void saveUserRoomId(Context context, int roomId);

    String getUserNickname(Context context);

    int getUserRoomId(Context context);

    void realmClose();
}

package zladnrms.defytech.kim.BroadcastTv.model;

import android.content.Context;
import android.content.SharedPreferences;

import com.orhanobut.logger.Logger;

//import io.realm.Realm;
//import io.realm.RealmResults;
import io.realm.Realm;
import io.realm.RealmResults;
import zladnrms.defytech.kim.BroadcastTv.SQLite.LoginSQLHelper;
import zladnrms.defytech.kim.BroadcastTv.model.domain.LoginData;

/**
 * Created by kim on 2017-06-22.
 */

public class LocalDataRepository implements LocalDataRepositoryModel {

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    private static LocalDataRepository instance;
    private static Realm realm;

    public static LocalDataRepository getInstance() {
        if (instance == null) {
            instance = new LocalDataRepository();
            realm = Realm.getDefaultInstance();
        }
        return instance;
    }

    @Override
    public void deleteUserLoginInfo() {
        if (realm != null && !realm.isClosed()) {
        } else {
            realm = Realm.getDefaultInstance();
        }
        realm.executeTransaction(realm1 -> {
            realm.delete(LoginData.class);
        });

        //LoginSQLHelper loginSQL = new LoginSQLHelper(context.getApplicationContext(), "LoginData.db", null, 1);
        //loginSQL.delete();
    }

    @Override
    public void saveUserLoginInfo(Context context, String id, String password, String nickname) {
        if (realm != null && !realm.isClosed()) {
        } else {
            realm = Realm.getDefaultInstance();
        }

        realm.executeTransaction(realm1 -> {
            Number maxId = realm.where(LoginData.class).max("_id");
            // If there are no rows, currentId is null, so the next id must be 1
            // If currentId is not null, increment it by 1
            int nextId = (maxId == null) ? 1 : maxId.intValue() + 1;

            // User object created with the new Primary key
            LoginData loginData = realm.createObject(LoginData.class, nextId);
            loginData.setId(id);
            loginData.setPassword(password);
        });

        //LoginSQLHelper loginSQL = new LoginSQLHelper(context.getApplicationContext(), "LoginData.db", null, 1);
        //loginSQL.insert(id, password);
    }

    @Override
    public LoginData loadUserLoginInfo() {
        if (realm != null && !realm.isClosed()) {
        } else {
            realm = Realm.getDefaultInstance();
        }

        LoginData result = null;

        realm.beginTransaction();
        long count = realm.where(LoginData.class).count();
        if (count > 0) {
            RealmResults<LoginData> realmResult = realm.where(LoginData.class).findAll();
            Logger.d("User Data In realm: " + realmResult.get(0));
            result = realmResult.get(0);
        } else {
            Logger.d("Nothing in localDB");
            result = null;
        }
        realm.commitTransaction();

        return result;
    }

    @Override
    public String getUserNickname(Context context) {
        pref = context.getSharedPreferences("member", 0);

        return pref.getString("nickname", "NoData");
    }

    @Override
    public int getUserRoomId(Context context) {
        pref = context.getSharedPreferences("member", 0);

        return pref.getInt("roomId", -1);
    }

    @Override
    public void saveUserNickname(Context context, String nickname) {
        pref = context.getSharedPreferences("member", 0);
        editor = pref.edit();

        editor.putString("nickname", nickname);
        editor.commit();
    }

    @Override
    public void saveUserRoomId(Context context, int roomId) {
        pref = context.getSharedPreferences("member", 0);
        editor = pref.edit();

        editor.putInt("roomId", roomId);
        editor.commit();
    }

    @Override
    public void realmClose() {
        realm.close();
    }
}

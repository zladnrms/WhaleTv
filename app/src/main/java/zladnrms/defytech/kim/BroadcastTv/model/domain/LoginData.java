package zladnrms.defytech.kim.BroadcastTv.model.domain;

//import io.realm.RealmObject;
//import io.realm.annotations.PrimaryKey;
//import io.realm.annotations.Required;

import android.support.annotation.RequiresApi;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class LoginData extends RealmObject { // 스트리밍 방  정보 클래스

    @PrimaryKey
    private int _id;

    @Required
    private String id;

    @Required
    private String password;

    public  LoginData() {

    }

    public LoginData(int _id, String id, String password) {
        this._id = _id;
        this.id = id;
        this.password = password;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int get_id() {
        return _id;
    }

    public String getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }

}
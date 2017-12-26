package zladnrms.defytech.kim.BroadcastTv.networking.post;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class JoinData {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("password")
    @Expose
    private String password;

    @SerializedName("nickname")
    @Expose
    private String nickname;

    public JoinData(String id, String password, String nickname) {
        this.id = id;
        this.password = password;
        this.nickname = nickname;
    }

    public String getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }

    public String getNickname() {
        return nickname;
    }

}
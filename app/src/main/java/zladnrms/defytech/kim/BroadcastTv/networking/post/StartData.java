package zladnrms.defytech.kim.BroadcastTv.networking.post;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StartData {

    @SerializedName("nickname")
    @Expose
    private String nickname;

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("subject")
    @Expose
    private String subject;

    public StartData(String nickname, String id, String subject) {
        this.nickname = nickname;
        this.id = id;
        this.subject = subject;
    }

    public String getNickname() {
        return nickname;
    }

    public String getId() {
        return id;
    }

    public String getSubject() {
        return subject;
    }

}
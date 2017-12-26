package zladnrms.defytech.kim.BroadcastTv.networking.post;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SendTokenData {

    @SerializedName("token")
    @Expose
    private String token;

    public SendTokenData(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
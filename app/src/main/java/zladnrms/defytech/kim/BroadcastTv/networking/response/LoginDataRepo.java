package zladnrms.defytech.kim.BroadcastTv.networking.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LoginDataRepo {

    @SerializedName("response")
    List<Obj> response;

    public class Obj {

        public String getId() {
            return id;
        }

        public String getNickname() {
            return nickname;
        }

        public String getResult() {
            return result;
        }

        public String getSha256pw() {
            return sha256pw;
        }

        @SerializedName("result")
        String result;
        @SerializedName("id")
        String id;
        @SerializedName("nickname")
        String nickname;
        @SerializedName("sha256pw")
        String sha256pw;

}

    public List<Obj> getResponse() {
        return response;
    }
}
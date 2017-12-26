package zladnrms.defytech.kim.BroadcastTv.networking.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ApiLoginDataRepo {

    @SerializedName("response")
    List<Obj> response;

    public class Obj {

        public String getNickname() {
            return nickname;
        }

        public String getResult() {
            return result;
        }

        @SerializedName("result")
        String result;
        @SerializedName("nickname")
        String nickname;

}

    public List<Obj> getResponse() {
        return response;
    }
}
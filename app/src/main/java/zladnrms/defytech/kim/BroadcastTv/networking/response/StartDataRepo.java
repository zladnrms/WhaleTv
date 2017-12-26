package zladnrms.defytech.kim.BroadcastTv.networking.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class StartDataRepo {

    @SerializedName("response")
    List<Obj> response;

    public class Obj {

        public String getRoomId() {
            return roomId;
        }

        public String getResult() {
            return result;
        }

        @SerializedName("result")
        String result;
        @SerializedName("roomId")
        String roomId;

}

    public List<Obj> getResponse() {
        return response;
    }
}
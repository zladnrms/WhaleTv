package zladnrms.defytech.kim.BroadcastTv.networking.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BookmarkRepo {

    @SerializedName("response")
    List<Obj> response;

    public class Obj {

        public String getResult() {
            return result;
        }

        public String getError() {
            return error;
        }

        @SerializedName("result")
        String result;

        @SerializedName("error")
        String error;
    }

    public List<Obj> getResponse() {
        return response;
    }
}
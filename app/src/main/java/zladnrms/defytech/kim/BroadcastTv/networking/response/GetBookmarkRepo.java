package zladnrms.defytech.kim.BroadcastTv.networking.response;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class GetBookmarkRepo {

    @SerializedName("response")
    List<Obj> response;

    public class Obj {

        public String getResult() {
            return result;
        }

        public ArrayList<String> getBookmark() {
            return bookmark;
        }

        @SerializedName("result")
        String result;

        @SerializedName("bookmark")
        ArrayList<String> bookmark;
}

    public List<Obj> getResponse() {
        return response;
    }
}
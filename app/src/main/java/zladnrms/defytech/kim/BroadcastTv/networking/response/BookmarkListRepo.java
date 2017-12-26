package zladnrms.defytech.kim.BroadcastTv.networking.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class BookmarkListRepo {

    @SerializedName("response")
    List<Obj> response;

    public class Obj {

        public ArrayList<String> getBookmarklist() {
            return bookmarklist;
        }

        public String getResult() {
            return result;
        }

        @SerializedName("bookmarklist")
        ArrayList<String> bookmarklist;

        @SerializedName("result")
        String result;
    }

    public List<Obj> getResponse() {
        return response;
    }
}
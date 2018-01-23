package zladnrms.defytech.kim.BroadcastTv.networking.response;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class GetBookmarkRepo {

    @SerializedName("response")
    List<Obj> response;

    public class Obj {

        public int getBookmarkId() {
            return bookmarkId;
        }

        public String getStreamerNickname() {
            return streamerNickname;
        }

        @SerializedName("bookmarkId")
        int bookmarkId;

        @SerializedName("streamerNickname")
        String streamerNickname;
}

    public List<Obj> getResponse() {
        return response;
    }
}
package zladnrms.defytech.kim.BroadcastTv.networking.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ViewerCountRepo {

    @SerializedName("response")
    List<Obj> response;

    public class Obj {

        public String getResult() {
            return result;
        }

        public int getViewerCount() {
            return viewerCount;
        }

        @SerializedName("result")
        String result;

        @SerializedName("viewerCount")
        int viewerCount;
}

    public List<Obj> getResponse() {
        return response;
    }
}
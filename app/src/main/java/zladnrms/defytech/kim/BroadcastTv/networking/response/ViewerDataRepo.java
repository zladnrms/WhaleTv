package zladnrms.defytech.kim.BroadcastTv.networking.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ViewerDataRepo {

    @SerializedName("response")
    List<Obj> response;

    public class Obj {

        public String getViewer() {
            return viewer;
        }

        @SerializedName("viewer")
        String viewer;
    }

    public List<Obj> getResponse() {
        return response;
    }
}
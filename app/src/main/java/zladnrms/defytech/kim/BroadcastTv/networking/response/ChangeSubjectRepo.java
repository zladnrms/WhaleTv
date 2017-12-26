package zladnrms.defytech.kim.BroadcastTv.networking.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ChangeSubjectRepo {

    @SerializedName("response")
    List<Obj> response;

    public class Obj {

        public String getResult() {
            return result;
        }

        @SerializedName("result")
        String result;

}

    public List<Obj> getResponse() {
        return response;
    }
}
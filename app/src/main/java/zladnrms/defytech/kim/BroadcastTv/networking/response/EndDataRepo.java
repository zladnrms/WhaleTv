package zladnrms.defytech.kim.BroadcastTv.networking.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class EndDataRepo {

    @SerializedName("response")
    List<Obj> response;

    public class Obj {

        public String getResult() {
            return result;
        }

        public int getRecord() {
            return record;
        }

        @SerializedName("result")
        String result;

        @SerializedName("record")
        int record;
}

    public List<Obj> getResponse() {
        return response;
    }
}
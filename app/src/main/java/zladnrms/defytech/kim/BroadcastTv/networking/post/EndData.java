package zladnrms.defytech.kim.BroadcastTv.networking.post;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EndData {

    @SerializedName("roomId")
    @Expose
    private int roomId;

    @SerializedName("id")
    @Expose
    private String id;

    public EndData(int roomId, String id) {
        this.roomId = roomId;
        this.id = id;
    }

    public int getRoomId() {
        return this.roomId;
    }

    public String getId() {
        return this.id;
    }

}
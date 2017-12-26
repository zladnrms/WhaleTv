package zladnrms.defytech.kim.BroadcastTv.networking.post;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ViewerData {

    @SerializedName("roomId")
    @Expose
    private int roomId;

    public ViewerData(int roomId) {
        this.roomId = roomId;
    }

    public int getRoomId() {
        return roomId;
    }

}
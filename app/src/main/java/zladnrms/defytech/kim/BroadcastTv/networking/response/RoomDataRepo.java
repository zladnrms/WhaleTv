package zladnrms.defytech.kim.BroadcastTv.networking.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RoomDataRepo {

    @SerializedName("response")
    List<Obj> response;

    public class Obj {

        public String getRoomId() {
            return roomId;
        }

        public String getSubject() {
            return subject;
        }

        public String getStreamerId() {
            return streamerId;
        }

        public String getStreamerNickname() {
            return streamerNickname;
        }

        public String getStatus() {
            return status;
        }

        public int getViewerCount() {
            return viewerCount;
        }


        @SerializedName("roomId")
        String roomId;
        @SerializedName("streamerId")
        String streamerId;
        @SerializedName("streamerNickname")
        String streamerNickname;
        @SerializedName("subject")
        String subject;
        @SerializedName("status")
        String status;
        @SerializedName("viewerCount")
        int viewerCount;
    }

    public List<Obj> getResponse() {
        return response;
    }
}
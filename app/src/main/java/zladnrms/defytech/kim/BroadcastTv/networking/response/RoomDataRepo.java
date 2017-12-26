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

        public String getViewer() {
            return viewer;
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

        public int getCount() {
            return viewer_count;
        }


        @SerializedName("roomId")
        String roomId;
        @SerializedName("streamer_id")
        String streamerId;
        @SerializedName("streamer_nickname")
        String streamerNickname;
        @SerializedName("viewer")
        String viewer;
        @SerializedName("subject")
        String subject;
        @SerializedName("status")
        String status;
        @SerializedName("viewer_count")
        int viewer_count;
    }

    public List<Obj> getResponse() {
        return response;
    }
}
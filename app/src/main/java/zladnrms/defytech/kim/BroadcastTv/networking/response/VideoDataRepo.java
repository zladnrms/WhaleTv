package zladnrms.defytech.kim.BroadcastTv.networking.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VideoDataRepo {

    @SerializedName("response")
    List<Obj> response;

    public class Obj {

        public int getVideoId() {
            return videoId;
        }

        public String getStreamerId() {
            return streamerId;
        }

        public String getStreamerNickname() {
            return streamerNickname;
        }

        public String getSubject() { return subject; }

        public String getFilename() {
            return filename;
        }

        public String getRecordDate() {
            return recordDate;
        }

        public int getViewCount() {
            return viewCount;
        }

        public int getStatus() {
            return status;
        }

        @SerializedName("videoId")
        int videoId;

        @SerializedName("streamerId")
        String streamerId;

        @SerializedName("streamerNickname")
        String streamerNickname;

        @SerializedName("subject")
        String subject;

        @SerializedName("filename")
        String filename;

        @SerializedName("recordDate")
        String recordDate;

        @SerializedName("viewCount")
        int viewCount;

        @SerializedName("status")
        int status;
    }

    public List<Obj> getResponse() {
        return response;
    }
}
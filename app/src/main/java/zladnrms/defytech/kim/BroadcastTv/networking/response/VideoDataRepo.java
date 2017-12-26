package zladnrms.defytech.kim.BroadcastTv.networking.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VideoDataRepo {

    @SerializedName("response")
    List<Obj> response;

    public class Obj {

        public int getVideo_id() {
            return video_id;
        }

        public String getStreamer_id() {
            return streamer_id;
        }

        public String getStreamer_nickname() {
            return streamer_nickname;
        }

        public String getFilename() {
            return filename;
        }

        public String getRecord_date() {
            return record_date;
        }

        public int getView_count() {
            return view_count;
        }

        @SerializedName("video_id")
        int video_id;

        @SerializedName("streamer_id")
        String streamer_id;

        @SerializedName("streamer_nickname")
        String streamer_nickname;

        @SerializedName("filename")
        String filename;

        @SerializedName("record_date")
        String record_date;

        @SerializedName("view_count")
        int view_count;
    }

    public List<Obj> getResponse() {
        return response;
    }
}
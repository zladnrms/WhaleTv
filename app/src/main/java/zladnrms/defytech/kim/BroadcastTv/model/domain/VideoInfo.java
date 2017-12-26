package zladnrms.defytech.kim.BroadcastTv.model.domain;

public class VideoInfo { // 녹화 비디오 정보 클래스

    private int videoId;
    private String streamerId;
    private String streamerNickname;
    private String filename;
    private String recordDate;
    private int count;

    public VideoInfo(int _videoId, String _streamerId, String _streamernickname, String _filename, String _recordDate, int _count) {
        this.videoId = _videoId;
        this.streamerId = _streamerId;
        this.streamerNickname = _streamernickname;
        this.filename = _filename;
        this.recordDate = _recordDate;
        this.count = _count;
    }

    public int getVideoId() {
        return videoId;
    }

    public String getFilename() {
        return filename;
    }

    public String getStreamerId() {
        return streamerId;
    }
    public String getStreamerNickname() {
        return streamerNickname;
    }

    public String getRecordDate() {
        return recordDate;
    }

    public int getCount() {
        return count;
    }
}
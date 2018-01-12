package zladnrms.defytech.kim.BroadcastTv.model.domain;

public class VideoInfo { // 녹화 비디오 정보 클래스

    private int videoId;
    private String streamerId;
    private String streamerNickname;
    private String subject;
    private String filename;
    private String recordDate;
    private int count;
    private int status;

    public VideoInfo(int _videoId, String _streamerId, String _streamernickname, String _subject, String _filename, String _recordDate, int _count, int _status) {
        this.videoId = _videoId;
        this.streamerId = _streamerId;
        this.streamerNickname = _streamernickname;
        this.subject = _subject;
        this.filename = _filename;
        this.recordDate = _recordDate;
        this.count = _count;
        this.status = _status;
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

    public String getSubject() { return subject; }

    public String getRecordDate() {
        return recordDate;
    }

    public int getCount() {
        return count;
    }

    public int getStatus() {
        return status;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }


    public void setStatus(int status) {
        this.status = status;
    }
}
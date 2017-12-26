package zladnrms.defytech.kim.BroadcastTv.model.domain;

public class RoomInfo{ // 스트리밍 방  정보 클래스

    private int roomId;
    private String streamerId;
    private String streamerNickname;
    private String viewer;
    private String subject;
    private int count;

    public RoomInfo(int _roomId, String _id, String _nickname, String _viewer, String _subject, int _count) {
        this.roomId = _roomId;
        this.streamerId = _id;
        this.streamerNickname = _nickname;
        this.viewer = _viewer;
        this.subject = _subject;
        this.count = _count;
    }

    public int getRoomId() {
        return roomId;
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

    public String getSubject() {
        return subject;
    }

    public int getCount() {
        return count;
    }
}
package zladnrms.defytech.kim.BroadcastTv.model.domain;

public class ChatInfo{ // 채팅 정보 클래스

    private int roomId;
    private String nickname;
    private String chat;

    public ChatInfo(int _roomId, String _nickname, String _chat) {
        this.roomId = _roomId;
        this.nickname = _nickname;
        this.chat = _chat;
    }

    public int getRoomId() {
        return roomId;
    }

    public String getNickname() {
        return nickname;
    }

    public String getChat() {
        return chat;
    }
}
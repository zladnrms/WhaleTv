package zladnrms.defytech.kim.BroadcastTv.packet;

import java.io.Serializable;

/**
 * Created by Administrator on 2016-11-28.
 */

public class ChatPacket extends HeaderPacket implements Serializable {

    private static final long serialVersionUID = 402;

    private int roomId;
    private String nickname;
    private String chat;

    public ChatPacket(int roomId, String nickname, String chat) { // 방 고유번호, 유저명, 채팅 내용
        super((byte) 2); // 2 = 채팅 프로토콜
        this.roomId = roomId;
        this.nickname = nickname;
        this.chat = chat;
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

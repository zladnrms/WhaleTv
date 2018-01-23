package zladnrms.defytech.kim.BroadcastTv.packet;

import java.io.Serializable;

/**
 * Created by Administrator on 2016-11-28.
 */

public class EntryPacket extends HeaderPacket implements Serializable {

    private static final long serialVersionUID = 401;

    private int roomId;
    private String nickname;

    public EntryPacket(int roomId, String nickname, int kinds) { // 방 고유번호, 유저id, 유저명, 프로토콜 종류
        super((byte) kinds); // 0 = 입장 프로토콜, 1 = 퇴장 프로토콜
        this.roomId = roomId;
        this.nickname = nickname;
    }

    public int getRoomId() {
        return roomId;
    }

    public String getNickname() {
        return nickname;
    }

}

package zladnrms.defytech.kim.BroadcastTv.packet;

import java.io.Serializable;

/**
 * Created by Administrator on 2016-11-28.
 */

public class EndingPacket extends HeaderPacket implements Serializable {

    private static final long serialVersionUID = 601;

    private int roomId;
    private String nickname;

    public EndingPacket(int roomId, String nickname) { // 방 고유번호, 유저명, 프로토콜 종류
        super((byte) 101); // 101 = 방송 종료 알림 프로토콜
        this.roomId =  roomId;
        this.nickname = nickname;
    }

    public int getRoomId() {
        return roomId;
    }

    public String getNickname() {
        return nickname;
    }

}

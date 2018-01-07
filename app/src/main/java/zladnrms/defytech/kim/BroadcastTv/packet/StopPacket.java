package zladnrms.defytech.kim.BroadcastTv.packet;

import java.io.Serializable;

/**
 * Created by Administrator on 2016-11-28.
 */

public class StopPacket extends HeaderPacket implements Serializable {

    private static final long serialVersionUID = 800;

    private int roomId;

    public StopPacket(int roomId, int kinds) { // 방 고유번호, 닉네임, 프로토콜 종류
        super((byte) kinds); // 50 = 방송 중단 알림 프로토콜, 51 = 방송 재개 알림 프로토콜
        this.roomId =  roomId;
    }

    public int getRoomId() {
        return roomId;
    }

}

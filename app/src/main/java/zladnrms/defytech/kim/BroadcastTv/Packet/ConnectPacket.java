package zladnrms.defytech.kim.BroadcastTv.Packet;

import java.io.Serializable;

/**
 * Created by Administrator on 2016-11-28.
 */

public class ConnectPacket extends HeaderPacket implements Serializable {

    private static final long serialVersionUID = 501;

    private String nickname;

    public ConnectPacket(int roomId, String nickname) { // 방 고유번호, 유저명, 채팅 내용, 데이터 길이, 연결 (100)
        super((byte) 100); // 100 = 최초 연결 프로토콜
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }

}

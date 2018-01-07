package zladnrms.defytech.kim.BroadcastTv.packet;

import java.io.Serializable;

/**
 * Created by Administrator on 2016-11-28.
 */

public class ConnectPacket extends HeaderPacket implements Serializable {

    private static final long serialVersionUID = 501;

    private String nickname;

    public ConnectPacket(int roomId, String nickname) { // 방 고유번호, 유저명
        super((byte) 100); // 100 = 최초 연결 프로토콜
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }

}

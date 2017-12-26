package zladnrms.defytech.kim.BroadcastTv.packet;

import java.io.Serializable;

/**
 * Created by Administrator on 2016-11-28.
 */

public class ChangeSubjectPacket extends HeaderPacket implements Serializable {

    private static final long serialVersionUID = 700;

    private int roomId;
    private String subject;

    public ChangeSubjectPacket(int roomId, String subject) {
        super((byte) 10); // 10 = 방송 제목 변경
        this.roomId =  roomId;
        this.subject = subject;
    }

    public int getRoomId() {
        return roomId;
    }

    public String getSubject() {
        return subject;
    }

}

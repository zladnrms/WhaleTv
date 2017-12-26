package zladnrms.defytech.kim.BroadcastTv.eventbus;

/**
 * Created by kim on 2017-07-01.
 */

public class EndingEvent {

    private int roomId;
    private String nickname;

    public EndingEvent(int roomId, String nickname) {
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

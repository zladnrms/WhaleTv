package zladnrms.defytech.kim.BroadcastTv.eventbus;

/**
 * Created by kim on 2017-07-01.
 */

public class ChangeSubjectEvent {
    private int roomId;
    private String subject;

    public ChangeSubjectEvent(int roomId, String subject) {
        this.subject = subject;
    }

    public int getRoomId() {
        return roomId;
    }
    public String getSubject() {
        return subject;
    }
}

package zladnrms.defytech.kim.BroadcastTv.eventbus;

/**
 * Created by kim on 2017-07-01.
 */

public class BroadcastStatusChangeEvent {
    private int status;

    public BroadcastStatusChangeEvent(int status) {
        this.status = status;
    }

    public int getStatus(){
        return status;

    }
}

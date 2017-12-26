package zladnrms.defytech.kim.BroadcastTv.eventbus;

/**
 * Created by kim on 2017-07-01.
 */

public class DataSendEvent {
    private String data;

    public DataSendEvent(String data) {
        this.data = data;
    }

    public String getData(){
        return data;

    }
}

package zladnrms.defytech.kim.BroadcastTv.eventbus;

import com.hwangjr.rxbus.Bus;

public final class RxBus {
    private static Bus sBus;

    public static synchronized Bus get() {
        if (sBus == null) {
            sBus = new Bus();
        }
        return sBus;
    }

}

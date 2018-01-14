package zladnrms.defytech.kim.BroadcastTv.eventbus;

/**
 * Created by kim on 2017-07-01.
 */

public class ViewerCountEvent {
    private int viewerCount;

    public ViewerCountEvent(int viewerCount) {
        this.viewerCount = viewerCount;
    }

    public int getViewerCount() {
        return viewerCount;
    }
}

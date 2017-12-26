package zladnrms.defytech.kim.BroadcastTv.eventbus;

/**
 * Created by kim on 2017-07-01.
 */

public class BookmarkEvent {
    private String nickname;

    public BookmarkEvent(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }
}

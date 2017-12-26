package zladnrms.defytech.kim.BroadcastTv.eventbus;

import zladnrms.defytech.kim.BroadcastTv.model.domain.ChatInfo;

/**
 * Created by kim on 2017-07-01.
 */

public class LoginEvent {
    private String result;
    private String nickname;

    public LoginEvent(String result, String nickname) {
        this.result = result;
        this.nickname = nickname;
    }

    public String getResult(){
        return result;
    }

    public String getNickname() {
        return nickname;
    }
}

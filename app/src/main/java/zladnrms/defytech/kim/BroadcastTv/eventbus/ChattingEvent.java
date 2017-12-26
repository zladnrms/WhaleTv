package zladnrms.defytech.kim.BroadcastTv.eventbus;

import zladnrms.defytech.kim.BroadcastTv.model.domain.ChatInfo;

/**
 * Created by kim on 2017-07-01.
 */

public class ChattingEvent {
    private ChatInfo chatInfo;

    public ChattingEvent(ChatInfo chatInfo) {
        this.chatInfo = chatInfo;
    }

    public ChatInfo getChatInfo(){
        return chatInfo;

    }
}

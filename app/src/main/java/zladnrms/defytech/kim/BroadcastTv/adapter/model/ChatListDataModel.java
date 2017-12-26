package zladnrms.defytech.kim.BroadcastTv.adapter.model;

import zladnrms.defytech.kim.BroadcastTv.model.domain.ChatInfo;

public interface ChatListDataModel {
    /* 데이터 추가 */
    void add(ChatInfo chatInfo);
    /* 데이터 제거 */
    void remove(int position);
    /* 데이터 초기화*/
    void clear();
}
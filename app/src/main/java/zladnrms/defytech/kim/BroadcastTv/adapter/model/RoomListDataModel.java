package zladnrms.defytech.kim.BroadcastTv.adapter.model;

import java.util.ArrayList;

import zladnrms.defytech.kim.BroadcastTv.model.domain.RoomInfo;

public interface RoomListDataModel {
    /* 데이터 추가 */
    void add(RoomInfo roomInfo);
    /* 데이터 제거 */
    void remove(int position);
    /* 데이터 초기화*/
    void clear();
}
package zladnrms.defytech.kim.BroadcastTv.adapter.model;

import zladnrms.defytech.kim.BroadcastTv.model.domain.VideoInfo;

public interface MyVideoListDataModel {
    /* 데이터 추가 */
    void add(VideoInfo videoInfo);
    /* 데이터 제거 */
    void remove(int position);
    /* 데이터 초기화*/
    void clear();
}
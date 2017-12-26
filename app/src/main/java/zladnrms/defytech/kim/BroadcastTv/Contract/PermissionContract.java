package zladnrms.defytech.kim.BroadcastTv.Contract;

import android.content.Context;

import zladnrms.defytech.kim.BroadcastTv.model.domain.ChatInfo;

public interface PermissionContract {
    interface View {

    }

     interface Presenter {

         void attachView(Object view);

         void detachView(Object view);

    }
}
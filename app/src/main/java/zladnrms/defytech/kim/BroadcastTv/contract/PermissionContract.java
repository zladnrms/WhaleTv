package zladnrms.defytech.kim.BroadcastTv.contract;

public interface PermissionContract {
    interface View {

    }

     interface Presenter {

         void attachView(Object view);

         void detachView(Object view);

    }
}
package zladnrms.defytech.kim.BroadcastTv.Contract;

public interface AccountContract {
    interface View {

    }

     interface Presenter {
         void attachView(Object view);

         void detachView(Object view);
     }
}
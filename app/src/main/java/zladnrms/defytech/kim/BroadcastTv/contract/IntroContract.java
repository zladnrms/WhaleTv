package zladnrms.defytech.kim.BroadcastTv.contract;

public interface IntroContract {
    interface View {

    }

     interface Presenter {
         void attachView(Object view);

         void detachView(Object view);
     }
}
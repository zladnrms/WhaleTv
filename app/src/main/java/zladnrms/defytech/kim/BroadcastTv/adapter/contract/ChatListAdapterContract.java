package zladnrms.defytech.kim.BroadcastTv.adapter.contract;

public interface ChatListAdapterContract {
    interface View {
        /* 데이터 갱신 */
        void refresh();
    }

     interface Presenter {
         void attachView(Object view);

         void detachView(Object view);
     }
}
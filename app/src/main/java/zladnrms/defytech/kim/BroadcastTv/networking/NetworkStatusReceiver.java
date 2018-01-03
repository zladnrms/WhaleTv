package zladnrms.defytech.kim.BroadcastTv.networking;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.orhanobut.logger.Logger;

public class NetworkStatusReceiver extends BroadcastReceiver {
 
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
         
        // 네트웍에 변경이 일어났을때 발생하는 부분
        if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
            NetworkInfo mobNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            Logger.t("receiver").d("Active Network Type : " + activeNetInfo.getTypeName());
            Logger.t("receiver").d("Mobile Network Type : " + mobNetInfo.getTypeName() );
        }
    }
}
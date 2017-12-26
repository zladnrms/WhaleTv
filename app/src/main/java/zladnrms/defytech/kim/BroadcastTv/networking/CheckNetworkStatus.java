package zladnrms.defytech.kim.BroadcastTv.networking;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class CheckNetworkStatus {

    public static boolean isConnectedToNetwork(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }


    public static int isConnectedWifiOrOther(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        int networkType = networkInfo.getType();
        if(networkType== ConnectivityManager.TYPE_WIFI){
            //Wifi  연결된 상태
            networkType = ConnectivityManager.TYPE_WIFI;
        } else if(networkType == ConnectivityManager.TYPE_MOBILE){
            //3G  연결된 상태
            networkType = ConnectivityManager.TYPE_MOBILE;
        }
        return networkType;
    }
}
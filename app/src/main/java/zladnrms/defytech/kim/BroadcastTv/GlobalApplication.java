package zladnrms.defytech.kim.BroadcastTv;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

//import io.realm.Realm;
import io.realm.Realm;
import zladnrms.defytech.kim.BroadcastTv.view.BroadcastJavaCvActivity;
import zladnrms.defytech.kim.BroadcastTv.view.MainActivity;
import zladnrms.defytech.kim.BroadcastTv.view.ViewerActivity;

/**
 * Created by kim on 2017-05-16.
 */

public class GlobalApplication extends Application {

    private BroadcastJavaCvActivity broadcastJavaCvActivity;
    private MainActivity mainActivity;
    private ViewerActivity viewerActivity;

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
    }

    /* For MultiDex */
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}

package zladnrms.defytech.kim.BroadcastTv.model;

import com.orhanobut.logger.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import zladnrms.defytech.kim.BroadcastTv.model.domain.RoomInfo;
import zladnrms.defytech.kim.BroadcastTv.networking.RetrofitClient;
import zladnrms.defytech.kim.BroadcastTv.networking.post.StartData;
import zladnrms.defytech.kim.BroadcastTv.networking.response.RoomDataRepo;
import zladnrms.defytech.kim.BroadcastTv.networking.response.StartDataRepo;

/**
 * Created by kim on 2017-06-22.
 */

public class ServerDataRepository implements ServerDataRepositoryModel {

    private static ServerDataRepository instance;
    private RetrofitClient retrofitClient;

    public ServerDataRepository() {
        retrofitClient = new RetrofitClient();
    }

    public static ServerDataRepository getInstance() {
        if (instance == null) {
            instance = new ServerDataRepository();
        }
        return instance;
    }

}

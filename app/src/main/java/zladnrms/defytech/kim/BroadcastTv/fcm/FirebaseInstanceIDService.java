package zladnrms.defytech.kim.BroadcastTv.fcm;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.orhanobut.logger.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final String URLlink = "http://52.79.108.8";
    private OkHttpClient okhttp = new OkHttpClient();

    private static final String TAG = "FirebaseIIDService";

    private String result_fcm;

    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String token = FirebaseInstanceId.getInstance().getToken();
        Logger.d(TAG, "Refreshed token: " + token);


        System.out.println("리프레시토큰");

        // 생성등록된 토큰을 개인 앱서버에 보내 저장해 두었다가 추가 뭔가를 하고 싶으면 할 수 있도록 한다.
        //sendRegistrationToServer(URLlink + "/forcoupon/member/fcm/register.php",token, "a");
    }

    private void sendRegistrationToServer(String url, String token, String input_nickname) {
        System.out.println("센드");

        RequestBody body = new FormBody.Builder()
                .add("token", token)
                .add("nickname", input_nickname)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        okhttp.newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        System.out.println("네트워크 연결 상태를 확인해주세요");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        result_fcm = response.body().string();

                        try{
                            JSONObject jsonObj = new JSONObject(result_fcm);
                            JSONArray jsonArr = jsonObj.getJSONArray("result");

                            for(int i = 0; i < jsonArr.length(); i ++) {
                                JSONObject c = jsonArr.getJSONObject(i);
                                String js_error, js_result;

                                if(!c.isNull("error")) {
                                    js_error = c.getString("error");

                                    switch (js_error) {
                                        case "01":
                                            System.out.println("서버 상황이 좋지 않습니다. 잠시후 다시 시도해주세요");
                                            break;
                                    }
                                } else {
                                    if(!c.isNull("result")) {
                                        js_result = c.getString("result");

                                        com.orhanobut.logger.Logger.t("RESULT-FCM").d(js_result);

                                        switch (js_result) {
                                            case "success":
                                                System.out.println("FCM 성공");
                                                break;
                                        }
                                    }
                                }
                            }
                        } catch (JSONException e) {

                        }
                    }
                });
    }
}
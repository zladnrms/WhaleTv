package zladnrms.defytech.kim.BroadcastTv.networking;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AddHeaderInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {

        Request.Builder builder = chain.request().newBuilder();
        builder.addHeader("Authorization", "MyauthHeaderContent");

        return chain.proceed(builder.build());
    }
}
package zladnrms.defytech.kim.BroadcastTv.networking;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import zladnrms.defytech.kim.BroadcastTv.BuildConfig;

public class RetrofitClient {

    private static final String BASE_URL = BuildConfig.SERVER_URL;

    private ApiInterface apiInterface;

    public RetrofitClient() {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Response response = chain.proceed(chain.request());

                        return response;
                    }
                })
                .addNetworkInterceptor(new AddHeaderInterceptor()).build();

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        apiInterface = retrofit.create(ApiInterface.class);
    }

    public ApiInterface getApi() {
        return apiInterface;
    }
}

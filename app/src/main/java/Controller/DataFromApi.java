package Controller;

import java.util.concurrent.TimeUnit;

import Controller.Api;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DataFromApi {
    public static Api getApi(){
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Api.HOST)
//                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(Api.gson))
                .build();

        return retrofit.create(Api.class);
    }
}

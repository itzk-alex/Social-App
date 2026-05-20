package com.example.a24520085_buihotrucanh.network;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class ApiClient {
    private static final String BASE_URL = "http://blackntt.net:8111/";

    private static Retrofit retrofit;

    private ApiClient() {}

    public static Retrofit retrofit() {
        if (retrofit != null) return retrofit;

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttp = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttp)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit;
    }

    public static ApiService api() {
        return retrofit().create(ApiService.class);
    }
}


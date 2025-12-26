package com.dillon.lw.http;

import com.dillon.lw.store.AppStore;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class TokenInterceptor implements Interceptor {


    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();

        // 为请求添加 Authorization Header
        Request newRequest = originalRequest.newBuilder()
                .header("Authorization", "Bearer " + AppStore.getAccessToken())
//                .header("Authorization", "Bearer f1a4a5018c7a4d12a14e7ddd745f52b3")
//                .header("tenant-id", "1")
                .build();

        System.err.println(AppStore.getAccessToken());
        return chain.proceed(newRequest);
    }

}
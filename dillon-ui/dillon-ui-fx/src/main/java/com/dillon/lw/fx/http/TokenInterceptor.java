package com.dillon.lw.fx.http;

import com.dillon.lw.fx.store.AppStore;
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
                .header("Authorization", "Bearer " + AppStore.getToken())
                .header("tenant-id", "1")
                .build();

        return chain.proceed(newRequest);
    }

}
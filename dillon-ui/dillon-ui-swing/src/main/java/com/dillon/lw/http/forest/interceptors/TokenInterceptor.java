package com.dillon.lw.http.forest.interceptors;

import com.dillon.lw.store.AppStore;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.interceptor.ForestInterceptor;

public class TokenInterceptor implements ForestInterceptor {

    @Override
    public boolean beforeExecute(ForestRequest request) {
        request.addHeader("Authorization", "Bearer " + AppStore.getAccessToken());  // 添加Header
        return true;  // 继续执行请求返回true
    }
}

package com.dillon.lw.fx.http.forest.interceptors;

import com.dillon.lw.fx.store.AppStore;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.interceptor.ForestInterceptor;

public class TokenInterceptor implements ForestInterceptor {

    @Override
    public boolean beforeExecute(ForestRequest request) {
        request.addHeader("Authorization", "Bearer " + AppStore.getToken());  // 添加Header
        return true;  // 继续执行请求返回true
    }
}

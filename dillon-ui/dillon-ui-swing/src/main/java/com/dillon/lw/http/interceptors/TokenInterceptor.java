package com.dillon.lw.http.interceptors;

import com.dillon.lw.store.AppStore;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.interceptor.ForestInterceptor;

public class TokenInterceptor implements ForestInterceptor {
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    public boolean beforeExecute(ForestRequest request) {
        // 兼容后端鉴权方案：始终附加 Bearer 前缀，token 为空时由服务端统一判定未登录。
        request.addHeader(AUTHORIZATION_HEADER, BEARER_PREFIX + AppStore.getAccessToken());
        return true;
    }
}

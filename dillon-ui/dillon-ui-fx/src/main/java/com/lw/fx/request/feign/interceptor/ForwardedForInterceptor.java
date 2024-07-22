package com.lw.fx.request.feign.interceptor;

import com.lw.fx.store.AppStore;
import feign.RequestInterceptor;
import feign.RequestTemplate;

public class ForwardedForInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {

        template.header("Authorization", AppStore.getToken());
    }
}
package com.lw.fx.request.interceptor;

import cn.hutool.core.util.ObjectUtil;
import com.lw.fx.store.AppStore;
import feign.RequestInterceptor;
import feign.RequestTemplate;

public class ForwardedForInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {

        template.header("Authorization", "Bearer " + AppStore.getToken());
        template.uri("/admin-api" + template.url());
        if (ObjectUtil.isNotEmpty("1")) {
            template.header("tenant-id", "1");
        }else {
            template.header("tenant-id", "1");
        }
//        template.header("tenantName", "DKY-源码");


    }
}
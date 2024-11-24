package com.lw.swing.request.interceptor;

import cn.hutool.core.util.ObjectUtil;
import com.lw.swing.store.AppStore;
import feign.RequestInterceptor;
import feign.RequestTemplate;

public class ForwardedForInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {

        template.header("Authorization", "Bearer " + AppStore.getAccessToken());
        template.uri(System.getProperty("app.server.url.prefix") + template.url());
        if (ObjectUtil.isNotEmpty("1")) {
            template.header("tenant-id", "1");
        }else {
            template.header("tenant-id", "1");
        }
//        template.header("tenantName", "DKY-源码");


    }
}
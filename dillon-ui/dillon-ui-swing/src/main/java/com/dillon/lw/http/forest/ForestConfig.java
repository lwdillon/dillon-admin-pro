package com.dillon.lw.http.forest;

import com.dillon.lw.forest.interceptors.ErrorInterceptor;
import com.dillon.lw.forest.interceptors.HASmartSource;
import com.dillon.lw.http.forest.interceptors.TokenInterceptor;
import com.dtflys.forest.Forest;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.interceptor.ForestInterceptor;
import com.dtflys.forest.interceptor.Interceptor;

import java.util.ArrayList;
import java.util.List;

public class ForestConfig {

    // 状态标记
    public static volatile boolean isMasterAlive = true;

    public static void init() {
        ForestConfiguration configuration = Forest.config();
//        configuration.setVariableValue("baseUrl", "/admin-api/");

        List<Class<? extends Interceptor>> interceptors = new ArrayList<>();
        interceptors.add(ErrorInterceptor.class);
        interceptors.add(TokenInterceptor.class);
        configuration.setInterceptors(interceptors);
        configuration.setBaseAddressSourceClass(HASmartSource.class);
    }
}

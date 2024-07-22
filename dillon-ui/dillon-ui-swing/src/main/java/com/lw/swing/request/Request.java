package com.lw.swing.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lw.dillon.admin.framework.common.pojo.CommonResult;
import com.lw.dillon.admin.module.system.controller.admin.auth.vo.AuthLoginReqVO;
import com.lw.swing.request.interceptor.ForwardedForInterceptor;
import com.lw.swing.request.interceptor.OkHttpInterceptor;
import com.lw.ui.request.api.system.AuthFeign;
import com.lw.ui.request.gson.LocalDateTimeTypeAdapter;
import com.lw.ui.request.gson.LocalDateTypeAdapter;
import com.lw.ui.request.gson.ZonedDateTimeTypeAdapter;
import feign.Client;
import feign.Feign;
import feign.Logger;
import feign.Retryer;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import feign.querymap.BeanQueryMapEncoder;
import okhttp3.ConnectionPool;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

public class Request {

    private final static String SERVICE_LIST = "http://127.0.0.1:48080/";
    private static Client feignClient;
    private static ObjectMapper objectMapper;
    private static GsonDecoder gsonDecoder;
    private static GsonEncoder gsonEncoder;
    static {

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        feignClient = new feign.okhttp.OkHttpClient(createOkHttpClient());
    }

    public static <T> T buildApiClient(Class<T> apiInterface) {

        return Feign.builder().queryMapEncoder(new BeanQueryMapEncoder())
                .decoder(getGsonDecoder())
                .encoder(getGsonEncoder())
                .logger(new Logger.JavaLogger(apiInterface.getSimpleName() + ".Logger")
                        .appendToFile("logs/http.log"))
                .logLevel(Logger.Level.FULL)
                .client(feignClient)
                .requestInterceptor(new ForwardedForInterceptor())
                .retryer(new Retryer.Default()) // 默认重试策略

                .target(apiInterface, SERVICE_LIST);
    }

    private static okhttp3.OkHttpClient createOkHttpClient() {
        return new okhttp3.OkHttpClient.Builder().connectionPool(new ConnectionPool())
                .addInterceptor(new OkHttpInterceptor())
                .build();
    }

    public static GsonDecoder getGsonDecoder() {
        if (gsonDecoder == null) {
            Gson gson = new GsonBuilder().setPrettyPrinting()
                    .registerTypeAdapter(LocalDate.class, new LocalDateTypeAdapter())
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                    .registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeTypeAdapter())
                    .setDateFormat("yyyy-MM-dd HH:mm:ss").create();
            gsonDecoder = new GsonDecoder(gson);
        }
        return gsonDecoder;
    }

    public static GsonEncoder getGsonEncoder() {
        if (gsonEncoder == null) {
            Gson gson = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(LocalDate.class, new LocalDateTypeAdapter())
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                    .registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeTypeAdapter()).setDateFormat("yyyy-MM-dd HH:mm:ss").create();

            gsonEncoder = new GsonEncoder(gson);
        }

        return gsonEncoder;
    }
    public static void main(String[] args) {

//        Map<String, Object> map = new HashMap<>();
//        map.put("tenantName", "DKY-源码");
//        map.put("username", "admin");
//        map.put("password", "admin123");
//        map.put("rememberMe", true);

        AuthLoginReqVO authLoginReqVO = new AuthLoginReqVO();
        authLoginReqVO.setUsername("admin");
        authLoginReqVO.setPassword("admin123");
        CommonResult commonResult = Request.buildApiClient(AuthFeign.class).login(authLoginReqVO);
        System.err.println(commonResult);
    }
}

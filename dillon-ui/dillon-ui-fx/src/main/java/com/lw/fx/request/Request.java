package com.lw.fx.request;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lw.fx.request.feign.decoder.FeignErrorDecoder;
import com.lw.fx.request.feign.interceptor.ForwardedForInterceptor;
import com.lw.fx.request.feign.interceptor.OkHttpInterceptor;
import com.lw.ui.request.api.BaseFeignApi;
import com.lw.ui.request.gson.LocalDateTimeTypeAdapter;
import com.lw.ui.request.gson.LocalDateTypeAdapter;
import com.lw.ui.request.gson.ZonedDateTimeTypeAdapter;
import feign.Feign;
import feign.Logger;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import feign.okhttp.OkHttpClient;
import feign.querymap.BeanQueryMapEncoder;
import feign.slf4j.Slf4jLogger;
import okhttp3.ConnectionPool;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class Request {

    private static final Map<String, BaseFeignApi> CONNECTORS = new ConcurrentHashMap<>();

    private static String API_URL = "http://127.0.0.1:48080/";
    private final static int CONNECT_TIME_OUT_MILLIS = 3000;
    private final static int READ_TIME_OUT_MILLIS = 90000;
    private static GsonDecoder gsonDecoder;
    private static GsonEncoder gsonEncoder;

    /**
     * @Description:
     * @param: [connectorClass, readTimeOut->设置超时时间]
     * @return: T
     * @auther: liwen
     * @date: 2019-06-05 14:33
     */
    public static <T extends BaseFeignApi> T connector(Class<T> connectorClass, int readTimeOut) {
        final String commandConfigKey = connectorClass.getSimpleName() + readTimeOut;

        return (T) CONNECTORS.computeIfAbsent(commandConfigKey, k -> {
            return Feign.builder().queryMapEncoder(new BeanQueryMapEncoder())
                    .client(new OkHttpClient(createOkHttpClient()))
                    .decoder(getGsonDecoder())
                    .encoder(getGsonEncoder())
                    .errorDecoder(new FeignErrorDecoder(new GsonDecoder()))
                    .requestInterceptor(new ForwardedForInterceptor())
                    .logger(new Slf4jLogger())
                    .logLevel(Logger.Level.BASIC)
                    .target(connectorClass, API_URL);

        });

    }


    public static <T extends BaseFeignApi> T connector(Class<T> connectorClass) {
        return connector(connectorClass, READ_TIME_OUT_MILLIS);

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
}

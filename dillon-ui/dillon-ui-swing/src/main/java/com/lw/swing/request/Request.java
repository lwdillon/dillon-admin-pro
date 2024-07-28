package com.lw.swing.request;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lw.swing.request.interceptor.ForwardedForInterceptor;
import com.lw.swing.request.interceptor.OkHttpInterceptor;
import com.lw.ui.request.api.BaseFeignApi;
import com.lw.ui.request.gson.LocalDateTimeTypeAdapter;
import com.lw.ui.request.gson.LocalDateTypeAdapter;
import com.lw.ui.request.gson.ZonedDateTimeTypeAdapter;
import feign.AsyncFeign;
import feign.Feign;
import feign.Logger;
import feign.Retryer;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import feign.jackson.JacksonDecoder;
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
    private static final int READ_TIME_OUT_MILLIS = 90000;

    // Gson 实例和解码器、编码器
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDate.class, new LocalDateTypeAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
            .registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeTypeAdapter())
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .create();

    private static final Decoder GSON_DECODER = new GsonDecoder(GSON);
    private static final Encoder GSON_ENCODER = new GsonEncoder(GSON);

    private static final okhttp3.OkHttpClient OK_HTTP_CLIENT = createOkHttpClient();
    private static final Feign.Builder BUILDER = createFeignBuilder();
    private static final Feign.Builder FILE_BUILDER = createFileFeignBuilder();

    private static final AsyncFeign.AsyncBuilder ASYNC_BUILDER = createAsyncFeignBuilder();

    private Request() {
        // 防止实例化
    }

    public static <T extends BaseFeignApi> T connector(Class<T> connectorClass, int readTimeOut) {
        final String key = connectorClass.getSimpleName() + readTimeOut;
        return (T) CONNECTORS.computeIfAbsent(key, k -> BUILDER.target(connectorClass, System.getProperty("app.server.url")));
    }

    public static <T extends BaseFeignApi> T connector(Class<T> connectorClass) {
        return connector(connectorClass, READ_TIME_OUT_MILLIS);
    }


    public static <T extends BaseFeignApi> T asyncConnector(Class<T> connectorClass, int readTimeOut) {
        final String key = connectorClass.getSimpleName() + readTimeOut;
        return (T) CONNECTORS.computeIfAbsent(key, k -> (BaseFeignApi) ASYNC_BUILDER.target(connectorClass, System.getProperty("app.server.url")));
    }

    public static <T extends BaseFeignApi> T asyncConnector(Class<T> connectorClass) {
        return asyncConnector(connectorClass, READ_TIME_OUT_MILLIS);
    }

    private static okhttp3.OkHttpClient createOkHttpClient() {
        return new okhttp3.OkHttpClient.Builder()
                .connectionPool(new ConnectionPool())
                .addInterceptor(new OkHttpInterceptor())
                .build();
    }

    private static Feign.Builder createFeignBuilder() {
        return Feign.builder()
                .queryMapEncoder(new BeanQueryMapEncoder())
                .decoder(GSON_DECODER)
                .encoder(GSON_ENCODER)
                .logger(new Slf4jLogger())
                .logLevel(Logger.Level.BASIC)
                .client(new OkHttpClient(OK_HTTP_CLIENT))
                .requestInterceptor(new ForwardedForInterceptor())
                .retryer(new Retryer.Default()); // 默认重试策略
    }

    private static AsyncFeign.AsyncBuilder createAsyncFeignBuilder() {
        return AsyncFeign.builder()
                .queryMapEncoder(new BeanQueryMapEncoder())
                .decoder(GSON_DECODER)
                .encoder(GSON_ENCODER)
                .logger(new Slf4jLogger())
                .logLevel(Logger.Level.FULL)
                .client(new OkHttpClient(OK_HTTP_CLIENT))
                .requestInterceptor(new ForwardedForInterceptor())
                .retryer(new Retryer.Default()); // 默认重试策略
    }


    public static <T extends BaseFeignApi> T fileConnector(Class<T> connectorClass) {
        return FILE_BUILDER.target(connectorClass, System.getProperty("app.server.url"));
    }

    private static Feign.Builder createFileFeignBuilder() {


        return Feign.builder()
                .encoder(new SpringFormEncoder())
                .decoder(new JacksonDecoder())
                .requestInterceptor(new ForwardedForInterceptor());

    }
}

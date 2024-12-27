package com.lw.fx.request;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lw.fx.request.interceptor.ForwardedForInterceptor;
import com.lw.fx.request.interceptor.OkHttpInterceptor;
import com.lw.fx.request.loadbalancer.PrimaryBackupRule;
import com.lw.ui.request.api.BaseFeignApi;
import com.lw.ui.request.gson.LocalDateTimeTypeAdapter;
import com.lw.ui.request.gson.LocalDateTypeAdapter;
import com.lw.ui.request.gson.ZonedDateTimeTypeAdapter;
import com.netflix.client.ClientFactory;
import com.netflix.client.config.IClientConfig;
import com.netflix.config.ConfigurationManager;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.ZoneAwareLoadBalancer;
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
import feign.ribbon.LBClient;
import feign.ribbon.LBClientFactory;
import feign.ribbon.RibbonClient;
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

    static {
        // 防止实例化
        try {
            // 手动设置 Ribbon 配置

            ConfigurationManager.loadPropertiesFromResources("myService.properties");

        } catch (Exception e) {
            throw new RuntimeException("Failed to configure Ribbon", e);
        }
    }

    private Request() {

    }

    public static <T extends BaseFeignApi> T connector(Class<T> connectorClass, int readTimeOut) {
        final String key = connectorClass.getSimpleName() + readTimeOut;
        return (T) CONNECTORS.computeIfAbsent(key, k -> BUILDER.target(connectorClass, "http://myService"));
    }

    public static <T extends BaseFeignApi> T connector(Class<T> connectorClass) {
        return connector(connectorClass, READ_TIME_OUT_MILLIS);
    }


    public static <T extends BaseFeignApi> T asyncConnector(Class<T> connectorClass, int readTimeOut) {
        final String key = connectorClass.getSimpleName() + readTimeOut;
        return (T) CONNECTORS.computeIfAbsent(key, k -> (BaseFeignApi) ASYNC_BUILDER.target(connectorClass, "http://myService"));
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
                .client(RibbonClient.builder().delegate(new OkHttpClient(OK_HTTP_CLIENT)).lbClientFactory(new LBClientFactory() {
                    @Override
                    public LBClient create(String clientName) {
                        IClientConfig config = ClientFactory.getNamedConfig(clientName);

                        ILoadBalancer lb = ClientFactory.getNamedLoadBalancer(clientName);
                        ZoneAwareLoadBalancer zb = (ZoneAwareLoadBalancer) lb;
                        // 设置规则：使用 AvailabilityFilteringRule 和 ZoneAvoidanceRule 进行主备切换
                        zb.setRule(new PrimaryBackupRule());
                        LBClient lbClient = LBClient.create(lb, config);
                        return lbClient;
                    }
                }).build())
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
        return FILE_BUILDER.target(connectorClass, "http://myService");
    }

    private static Feign.Builder createFileFeignBuilder() {


        return Feign.builder()
                .encoder(new SpringFormEncoder())
                .decoder(new JacksonDecoder())
                .client(RibbonClient.builder().delegate(new OkHttpClient(OK_HTTP_CLIENT)).lbClientFactory(new LBClientFactory() {
                    @Override
                    public LBClient create(String clientName) {
                        IClientConfig config = ClientFactory.getNamedConfig(clientName);

                        ILoadBalancer lb = ClientFactory.getNamedLoadBalancer(clientName);
                        ZoneAwareLoadBalancer zb = (ZoneAwareLoadBalancer) lb;
                        // 设置规则：使用 AvailabilityFilteringRule 和 ZoneAvoidanceRule 进行主备切换
                        zb.setRule(new PrimaryBackupRule());
                        LBClient lbClient = LBClient.create(lb, config);
                        return lbClient;
                    }
                }).build())
                .requestInterceptor(new ForwardedForInterceptor());

    }
}

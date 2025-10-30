package com.lw.swing.http;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lw.ui.request.gson.LocalDateTimeTypeAdapter;
import com.lw.ui.request.gson.LocalDateTypeAdapter;
import com.lw.ui.request.gson.ZonedDateTimeTypeAdapter;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by zhouwei on 16/11/9.
 */

public class RetrofitServiceManager {
    private static final int DEFAULT_TIME_OUT = 50;
    private static final int DEFAULT_READ_TIME_OUT = 100;

    private static final String PRIMARY_BASE_URL = System.getProperty("app.server.primary_base_url");
    private static final String BACKUP_BASE_URL = System.getProperty("app.server.backup_base_url");

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDate.class, new LocalDateTypeAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
            .registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeTypeAdapter())
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .create();

    private Retrofit mRetrofit;
    private OkHttpClient client;
    private AtomicBoolean isUsingPrimary = new AtomicBoolean(true);
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private RetrofitServiceManager() {
        client = createOkHttpClient();
        mRetrofit = createRetrofit(PRIMARY_BASE_URL);
        startPrimaryHealthCheck();
    }

    private OkHttpClient createOkHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(DEFAULT_TIME_OUT, TimeUnit.SECONDS)
                .writeTimeout(DEFAULT_READ_TIME_OUT, TimeUnit.SECONDS)
                .readTimeout(DEFAULT_READ_TIME_OUT, TimeUnit.SECONDS);

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
        builder.addInterceptor(new TokenInterceptor());
        builder.addInterceptor(loggingInterceptor);

        builder.addInterceptor(chain -> {
            Response response;
            try {
                response = chain.proceed(chain.request());
            } catch (IOException e) {
                if (isUsingPrimary.get()) {
                    isUsingPrimary.set(false);
                    synchronized (this) {
                        mRetrofit = createRetrofit(BACKUP_BASE_URL);
                    }
                }
                throw e;
            }
            return response;
        });

        return builder.build();
    }

    private Retrofit createRetrofit(String baseUrl) {
        return new Retrofit.Builder()
                .client(client)
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(GSON))
                .baseUrl(baseUrl)
                .build();
    }

    private void startPrimaryHealthCheck() {
        scheduler.scheduleAtFixedRate(() -> {
            if (!isUsingPrimary.get()) {
                try {
                    Request request = new Request.Builder()
                            .url(PRIMARY_BASE_URL + "system/auth/health")
                            .build();
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        synchronized (this) {
                            mRetrofit = createRetrofit(PRIMARY_BASE_URL);
                            isUsingPrimary.set(true);
                        }
                    }
                } catch (IOException ignored) {
                }
            }
        }, 10, 30, TimeUnit.SECONDS);
    }

    private static class SingletonHolder {
        private static final RetrofitServiceManager INSTANCE = new RetrofitServiceManager();
    }

    public static RetrofitServiceManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public <T> T create(Class<T> service) {
        return mRetrofit.create(service);
    }
}


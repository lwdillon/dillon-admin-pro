package http;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lw.ui.request.gson.LocalDateTimeTypeAdapter;
import com.lw.ui.request.gson.LocalDateTypeAdapter;
import com.lw.ui.request.gson.ZonedDateTimeTypeAdapter;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhouwei on 16/11/9.
 */

public class RetrofitServiceManager {
    private static final int DEFAULT_TIME_OUT = 5;//超时时间 5s
    private static final int DEFAULT_READ_TIME_OUT = 10;
    // Gson 实例和解码器、编码器
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDate.class, new LocalDateTypeAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
            .registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeTypeAdapter())
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .create();


    private static TokenInterceptor tokenInterceptor;
    private Retrofit mRetrofit;

    private RetrofitServiceManager() {
        // 创建 OKHttpClient
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        builder.connectTimeout(DEFAULT_TIME_OUT, TimeUnit.SECONDS);//连接超时时间
        builder.writeTimeout(DEFAULT_READ_TIME_OUT, TimeUnit.SECONDS);//写操作 超时时间
        builder.readTimeout(DEFAULT_READ_TIME_OUT, TimeUnit.SECONDS);//读操作超时时间

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.addInterceptor(tokenInterceptor = new TokenInterceptor(""));
        builder.addInterceptor(loggingInterceptor);


        // 创建Retrofit
        mRetrofit = new Retrofit.Builder()
                .client(builder.build())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(GSON))
                .baseUrl("http://127.0.0.1:48080/admin-api/")
                .build();
    }

    private static class SingletonHolder {
        private static final RetrofitServiceManager INSTANCE = new RetrofitServiceManager();
    }

    /**
     * 获取RetrofitServiceManager
     *
     * @return
     */
    public static RetrofitServiceManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * 获取对应的Service
     *
     * @param service Service 的 class
     * @param <T>
     * @return
     */
    public <T> T create(Class<T> service) {
        return mRetrofit.create(service);
    }


    public static void updateToken(String newToken) {
        if (tokenInterceptor != null) {
            tokenInterceptor.setToken(newToken); // 更新 Token
        }
    }
}

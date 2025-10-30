package http;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class TokenInterceptor implements Interceptor {
    private String token;

    public TokenInterceptor(String token) {
        this.token = token;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();

        // 为请求添加 Authorization Header
        Request newRequest = originalRequest.newBuilder()
                .header("Authorization", "Bearer " + token)
                .header("tenant-id", "1")
                .build();

        return chain.proceed(newRequest);
    }

    public void setToken(String token) {
        this.token = token;
    }
}
package com.lw.swing.request.interceptor;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.lw.dillon.admin.framework.common.exception.enums.GlobalErrorCodeConstants;
import com.lw.dillon.admin.framework.common.pojo.CommonResult;
import com.lw.swing.components.notice.WMessage;
import com.lw.swing.view.MainFrame;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.IOException;
import java.nio.charset.Charset;


public class OkHttpInterceptor implements Interceptor {
    private static final Charset UTF8 = Charset.forName("UTF-8");

    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        Request originalRequest = chain.request();

        Response response = null;
        try {
            response = chain.proceed(originalRequest);
        } catch (IOException e) {
            messageProcess(CommonResult.error(404, e.getMessage()));

            throw new RuntimeException(e);
        }

        MediaType mediaType = response.body().contentType();
        String content = null;
        try {
            content = response.body().string();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        JSONObject jsonObject = JSONUtil.parseObj(content);
        CommonResult commonResult = JSONUtil.toBean(jsonObject, CommonResult.class);

        Response responseNew = response.newBuilder().body(ResponseBody.create(mediaType, content)).build();
        if (!"GET".equalsIgnoreCase(originalRequest.method())) {
            messageProcess(commonResult);
        } else {
            if (commonResult.isError()) {
                messageProcess(commonResult);
            }
        }
        //生成新的response返回，网络请求的response如果取出之后，直接返回将会抛出异常
        return responseNew;
    }

    public OkHttpInterceptor() {
    }

    /**
     * @Description: 下面的注释为通过response自定义code来标示请求状态，当code返回如下情况为权限有问题，登出并返回到登录页
     * * 如通过xmlhttprequest 状态码标识 逻辑可写在下面error中
     * @param: [res]
     * @return: void
     * @auther: liwen
     * @date: 2018/11/6 12:59 PM
     */
    private void messageProcess(CommonResult commonResult) throws IOException {


        if (commonResult.isError()) {

            SwingUtilities.invokeLater(() -> {

                if (NumberUtil.equals(commonResult.getCode(), GlobalErrorCodeConstants.UNAUTHORIZED.getCode())) {

                    MainFrame.getInstance().showLogin();
                }
                System.err.println("-----------------"+commonResult.getMsg());
                WMessage.showMessageError(MainFrame.getInstance(), commonResult.getMsg());


            });
        }


    }


}
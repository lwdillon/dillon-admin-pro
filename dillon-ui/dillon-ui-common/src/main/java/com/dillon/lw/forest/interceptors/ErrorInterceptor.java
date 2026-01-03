package com.dillon.lw.forest.interceptors;

import com.dillon.lw.forest.config.HAStatusManager;
import com.dillon.lw.framework.common.exception.ServiceException;
import com.dillon.lw.framework.common.pojo.CommonResult;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.interceptor.ForestInterceptor;
import com.dtflys.forest.interceptor.ResponseResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.UnknownHostException;

public class ErrorInterceptor implements ForestInterceptor {

    private static final Logger log =
            LoggerFactory.getLogger(ErrorInterceptor.class);

    @Override
    public void onError(ForestRuntimeException ex,
                        ForestRequest request,
                        ForestResponse response) {

        // ========= ① 协议级错误 =========
        if (response != null) {
            int status = response.getStatusCode();
            String url = request.getUrl();

            if(status==401){
                throw new ServiceException(
                        401,
                        "账号未登录\n" + url,
                        ex
                );
            }
            if (status == 404) {
                throw new ServiceException(
                        404,
                        "接口不存在（404）\n" + url,
                        ex
                );
            }

            if (status == 405) {
                throw new ServiceException(
                        405,
                        "接口方法不匹配（405）\n" + url,
                        ex
                );
            }

            if (status == 400 || status == 415) {
                throw new ServiceException(
                        400,
                        "请求参数或 Content-Type 错误（" + status + "）",
                        ex
                );
            }
        }

        // ========= ② 网络级错误（主备切换） =========
        Throwable cause = ex.getCause();
        boolean isNetworkError =
                        (cause instanceof ConnectException ||
                                cause instanceof SocketTimeoutException ||
                                cause instanceof UnknownHostException);

        if (isNetworkError && isRequestToMaster(request)) {
            log.warn("主机不可达，切换到备机，原因：{}", cause.toString());
            HAStatusManager.markMasterFail();
        }

        // 原异常继续抛
        throw ex;
    }

    @Override
    public ResponseResult onResponse(ForestRequest request,
                                     ForestResponse response) {

        // SSE 放行
        if (request.isSSE()) {
            return proceed();
        }

        // HTTP 错误 → onError
        if (response.isError()) {
            return error(response.getException());
        }

        // 仅处理 JSON 业务返回
        CommonResult<?> ct = response.get(CommonResult.class);
        if (ct != null) {
            CommonResult<?> result = response.get(CommonResult.class);
            if (result != null && result.isError()) {
                throw new ServiceException(
                        result.getCode(),
                        result.getMsg()
                );
            }
        }

        return proceed();
    }

    private boolean isRequestToMaster(ForestRequest request) {
        try {
            URI uri = URI.create(request.getUrl());
            return HAStatusManager.isMasterAlive()
                    && HAStatusManager.MASTER_IP.equals(uri.getHost());
        } catch (Exception e) {
            return false;
        }
    }
}
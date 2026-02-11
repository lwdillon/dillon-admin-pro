package com.dillon.lw.exception;

import com.dillon.lw.components.notice.WMessage;
import com.dillon.lw.framework.common.exception.ServiceException;
import com.dillon.lw.view.frame.MainFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public final class SwingExceptionHandler {
    private static final int UNAUTHORIZED_CODE = 401;
    private static final String DEFAULT_ERROR_PREFIX = "系统异常：";

    private static final Logger log = LoggerFactory.getLogger(SwingExceptionHandler.class);

    private SwingExceptionHandler() {}

    /**
     * 统一异常入口：
     * 1. 网络异常（连接失败、超时、DNS 等）
     * 2. 业务异常（ServiceException）
     * 3. 未知技术异常（兜底）
     */
    public static void handle(Throwable e) {
        log.error("客户端异常", e);

        Throwable root = findRootCause(e);
        if (tryHandleNetworkException(root)) {
            return;
        }

        if (tryHandleBusinessException(e)) {
            return;
        }

        showError(buildFallbackMessage(root));
    }

    private static boolean tryHandleNetworkException(Throwable root) {
        if (!isNetworkException(root)) {
            return false;
        }
        showError(getNetworkErrorMessage(root));
        return true;
    }

    private static boolean tryHandleBusinessException(Throwable e) {
        ServiceException serviceException = findServiceException(e);
        if (serviceException == null) {
            return false;
        }
        if (serviceException.getCode() == UNAUTHORIZED_CODE) {
            MainFrame.getInstance().showLogin(false);
        }
        showError(serviceException.getMessage());
        return true;
    }

    private static String buildFallbackMessage(Throwable root) {
        return DEFAULT_ERROR_PREFIX
                + root.getClass().getSimpleName()
                + (root.getMessage() != null ? "\n" + root.getMessage() : "");
    }

    private static boolean isNetworkException(Throwable t) {
        return t instanceof ConnectException
                || t instanceof SocketTimeoutException
                || t instanceof UnknownHostException
                || t instanceof SocketException
                || t instanceof IOException;
    }

    private static String getNetworkErrorMessage(Throwable t) {
        if (t instanceof ConnectException) {
            return "网络异常：无法连接到服务器";
        } else if (t instanceof SocketTimeoutException) {
            return "网络异常：请求超时";
        } else if (t instanceof UnknownHostException) {
            return "网络异常：无法解析服务器地址";
        } else if (t instanceof SocketException) {
            return "网络异常：连接被重置或中断";
        } else {
            return "网络异常：" + t.getMessage();
        }
    }

    private static ServiceException findServiceException(Throwable e) {
        Throwable cur = e;
        while (cur != null) {
            if (cur instanceof ServiceException) {
                return (ServiceException) cur;
            }
            cur = cur.getCause();
        }
        return null;
    }

    private static Throwable findRootCause(Throwable e) {
        Throwable cur = e;
        while (cur != null && cur.getCause() != null && cur.getCause() != cur) {
            cur = cur.getCause();
        }
        return cur != null ? cur : e;
    }

    private static void showError(String msg) {
        SwingUtilities.invokeLater(() ->
                WMessage.showMessageWarning(MainFrame.getInstance(), msg)
        );
    }
}

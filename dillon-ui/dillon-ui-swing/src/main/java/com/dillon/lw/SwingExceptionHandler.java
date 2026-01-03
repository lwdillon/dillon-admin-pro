package com.dillon.lw;

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

    private static final Logger log = LoggerFactory.getLogger(SwingExceptionHandler.class);

    private SwingExceptionHandler() {}

    public static void handle(Throwable e) {
        log.error("客户端异常", e);

        // ① 优先处理网络异常
        Throwable root = findRootCause(e);
        if (isNetworkException(root)) {
            showError(getNetworkErrorMessage(root));
            return;
        }

        // ② 查找业务异常
        ServiceException se = findServiceException(e);
        if (se != null) {

            if (se.getCode() == 401) {
                MainFrame.getInstance().showLogin(false);
            }
            showError(se.getMessage());
            return;
        }

        // ③ 兜底技术异常
        showError("系统异常："
                + root.getClass().getSimpleName()
                + (root.getMessage() != null ? "\n" + root.getMessage() : ""));
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
package com.dillon.lw.components.notice;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ContainerAdapter;
import java.awt.event.ContainerEvent;

/**
 * @version： 0.0.1
 * @description:
 * @className: WMessage
 * @author: liwen
 * @date: 2021/11/4 09:25
 */
public class WMessage {
    private static final String LISTENER_ATTACHED_KEY = "notice.listener.attached";

    private static final int DURATION = 3000;
    private static final int OFFSET = 80;
    public static final int INFO = 0;
    /**
     * @deprecated 历史拼写，建议使用 {@link #INFO}。
     */
    @Deprecated
    public static final int INFOR = INFO;
    public static final int SUCCESS = 1;
    public static final int WARNING = 2;
    public static final int ERROR = 3;

    public static void showMessageInfo(JFrame frame, String message) {
        showMessage(frame, message, DURATION, true, OFFSET, INFO);
    }

    /**
     * @deprecated 历史拼写，建议改用 {@link #showMessageInfo(JFrame, String)}。
     */
    @Deprecated
    public static void showMessageInfor(JFrame frame, String message) {
        showMessageInfo(frame, message);
    }

    public static void showMessageSuccess(JFrame frame, String message) {
        showMessage(frame, message, DURATION, true, OFFSET, SUCCESS);
    }

    public static void showMessageWarning(JFrame frame, String message) {
        showMessage(frame, message, DURATION, true, OFFSET, WARNING);
    }

    public static void showMessageError(JFrame frame, String message) {
        showMessage(frame, message, DURATION, true, OFFSET, ERROR);
    }

    public static void showMessage(JFrame frame, String message, int duration, boolean showClose, int offset, int type) {
        NoticeContainerPane noticeContainerPane = getOrCreateNoticeContainerPane(frame);
        noticeContainerPane.setOffset(offset);
        ensureGlassPaneVisible(frame);

        WMessagePane messagePane = new WMessagePane(message, duration, showClose, type);
        noticeContainerPane.addMessage(messagePane);
        attachAutoHideListener(frame, noticeContainerPane);
    }

    private static NoticeContainerPane getOrCreateNoticeContainerPane(JFrame frame) {
        Component glassPane = frame.getGlassPane();
        if (glassPane instanceof NoticeContainerPane) {
            return (NoticeContainerPane) glassPane;
        }
        NoticeContainerPane pane = new NoticeContainerPane();
        frame.setGlassPane(pane);
        return pane;
    }

    private static void ensureGlassPaneVisible(JFrame frame) {
        if (!frame.getGlassPane().isVisible()) {
            frame.getGlassPane().setVisible(true);
        }
    }

    private static void attachAutoHideListener(JFrame frame, NoticeContainerPane pane) {
        if (Boolean.TRUE.equals(pane.getClientProperty(LISTENER_ATTACHED_KEY))) {
            return;
        }

        pane.addContainerListener(new ContainerAdapter() {
            @Override
            public void componentRemoved(ContainerEvent e) {
                super.componentRemoved(e);
                if (pane.getComponentCount() == 0) {
                    frame.getGlassPane().setVisible(false);
                }
            }
        });
        pane.putClientProperty(LISTENER_ATTACHED_KEY, Boolean.TRUE);
    }
}

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

    private final static int DURATION=3000;
    private final static int OFFSET=80;
    public final static int INFOR = 0;
    public final static int SUCCESS = 1;
    public final static int WARNING = 2;
    public final static int ERROR = 3;

    public static void showMessageInfor(JFrame frame, String message){
        showMessage(frame, message, DURATION, true, OFFSET, INFOR);
    }
    public static void showMessageSuccess(JFrame frame, String message){
        showMessage(frame, message, DURATION, true, OFFSET, SUCCESS);
    }
    public static void showMessageWarning(JFrame frame, String message){
        showMessage(frame, message, DURATION, true, OFFSET, WARNING);
    }
    public static void showMessageError(JFrame frame, String message){
        showMessage(frame, message, DURATION, true, OFFSET, ERROR);
    }

    public static void showMessage(JFrame frame, String message, int duration, boolean showClose, int offset, int type) {

        Component glassPane = frame.getGlassPane();
        NoticeContainerPane noticeContainerPane = null;
        if (glassPane == null || (glassPane instanceof NoticeContainerPane) == false) {
            noticeContainerPane = new NoticeContainerPane();

            frame.setGlassPane(noticeContainerPane);
        } else {

            noticeContainerPane = (NoticeContainerPane) glassPane;
        }
        noticeContainerPane.setOffset(offset);
        if (frame.getGlassPane().isVisible() == false) {
            frame.getGlassPane().setVisible(true);
        }

        WMessagePane wMessagePane = new WMessagePane(message, duration, showClose, type);
        noticeContainerPane.addMessage(wMessagePane);
        NoticeContainerPane finalNoticeContainerPane = noticeContainerPane;
        noticeContainerPane.addContainerListener(new ContainerAdapter() {
            @Override
            public void componentRemoved(ContainerEvent e) {
                super.componentRemoved(e);

                if (finalNoticeContainerPane.getComponentCount() == 0) {
                    frame.getGlassPane().setVisible(false);
                }
            }
        });
    }


}

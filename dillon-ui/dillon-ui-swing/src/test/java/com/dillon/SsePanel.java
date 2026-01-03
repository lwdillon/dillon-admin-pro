/*
 * Created by JFormDesigner on Tue Dec 23 09:27:26 CST 2025
 */

package com.dillon;

import com.dillon.lw.api.sse.CalSseClient;
import com.dillon.lw.api.system.AuthApi;
import com.dillon.lw.framework.common.pojo.CommonResult;
import com.dillon.lw.module.system.api.sse.SseMessage;
import com.dillon.lw.module.system.controller.admin.auth.vo.AuthLoginReqVO;
import com.dillon.lw.module.system.controller.admin.auth.vo.AuthLoginRespVO;
import com.dillon.lw.module.system.controller.admin.auth.vo.AuthPermissionInfoRespVO;
import com.dillon.lw.store.AppStore;
import com.dtflys.forest.Forest;
import com.dtflys.forest.http.ForestSSE;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.event.HierarchyEvent;


/**
 * @author wenli
 */
public class SsePanel extends JPanel {
    private ForestSSE forestSSE;

    public SsePanel() {
        initComponents();

        testBut.addActionListener(e -> {
            if (forestSSE != null) {
                return;
            }
            testBut.setEnabled(false);
            startSse();
        });
        loginOutBut.setEnabled(false);
        loginBut.addActionListener(e -> login());
        loginOutBut.addActionListener(e -> loginOut());

        // 当 Panel 被移除 / 窗口关闭时，自动关闭 SSE（非常重要）
        addHierarchyListener(e -> {
            if ((e.getChangeFlags() & HierarchyEvent.DISPLAYABILITY_CHANGED) != 0) {
                if (!isDisplayable()) {
                    stopSse();
                }
            }
        });
    }

    private void loginOut() {
        SwingWorker<CommonResult<Boolean>, Void> swingWorker = new SwingWorker<CommonResult<Boolean>, Void>() {
            @Override
            protected CommonResult<Boolean> doInBackground() throws Exception {
                CommonResult<Boolean> commonResult = Forest.client(AuthApi.class).logout();

                return commonResult;
            }

            @Override
            protected void done() {
                try {
                    if (get().isSuccess()) {
                        loginBut.setEnabled(true);
                        loginOutBut.setEnabled(false);
                        AppStore.setAuthLoginRespVO(null);

                    } else {
                        loginOutBut.setEnabled(true);
                    }

                } catch (Exception e) {
                    loginOutBut.setEnabled(true);
                    throw new RuntimeException(e);
                }
            }
        };
        loginOutBut.setEnabled(false);
        swingWorker.execute();
    }

    private void login() {
        AuthLoginReqVO loginReqVO = new AuthLoginReqVO();
        loginReqVO.setUsername(userNameTx.getText());
        loginReqVO.setPassword(passwordTx.getText());
        SwingWorker<CommonResult<AuthLoginRespVO>, Void> swingWorker = new SwingWorker<CommonResult<AuthLoginRespVO>, Void>() {
            @Override
            protected CommonResult<AuthLoginRespVO> doInBackground() throws Exception {
                CommonResult<AuthLoginRespVO> commonResult = Forest.client(AuthApi.class).login(loginReqVO);

                return commonResult;
            }

            @Override
            protected void done() {
                try {
                    if (get().isSuccess()) {
                        loginOutBut.setEnabled(true);
                        AppStore.setAuthLoginRespVO(get().getCheckedData());

                        CommonResult<AuthPermissionInfoRespVO> commonResult=  Forest.client(AuthApi.class).getPermissionInfo();
                        System.err.println();
                    } else {
                        loginBut.setEnabled(true);
                    }

                } catch (Exception e) {
                    loginBut.setEnabled(true);
                    throw new RuntimeException(e);
                }
            }
        };
        loginOutBut.setEnabled(false);
        loginBut.setEnabled(false);
        swingWorker.execute();
    }

    /**
     * 启动 SSE
     */
    private void startSse() {
        CalSseClient calSseClient = Forest.client(CalSseClient.class);

        forestSSE = calSseClient
                .testSSE("TASK_A", "bbbb", "8888")
                .setOnOpen(event -> {
                    SwingUtilities.invokeLater(() -> {
                        msgTrea.append("SSE connected\n");
                        testBut.setEnabled(false);
                    });
                })
                .setOnMessage(event -> {
                    SseMessage msg = event.data(SseMessage.class);
                    if (msg == null) {
                        return;
                    }
                    SwingUtilities.invokeLater(() ->
                            msgTrea.append(String.valueOf(msg.getPayload()) + "\n")
                    );
                })
                .setOnClose(event -> {
                    SwingUtilities.invokeLater(() -> {
                        msgTrea.append("SSE closed\n");
                        testBut.setEnabled(true);
                    });
                    forestSSE = null;
                });

        SwingWorker swingWorker = new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                forestSSE.listen();
                return null;
            }

            @Override
            protected void done() {
                super.done();
            }
        };
        swingWorker.execute();

    }


    /**
     * 主动关闭 SSE（窗口 / Tab 关闭时调用）
     */
    public void stopSse() {
        if (forestSSE != null) {
            forestSSE.close();
            forestSSE = null;
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner non-commercial license
        userNameTx = new JTextField();
        passwordTx = new JTextField();
        loginBut = new JButton();
        loginOutBut = new JButton();
        scrollPane1 = new JScrollPane();
        msgTrea = new JTextArea();
        testBut = new JButton();

        //======== this ========
        setLayout(new MigLayout(
            "fill,hidemode 3,aligny top",
            // columns
            "[grow,fill]" +
            "[fill]",
            // rows
            "[]" +
            "[grow]"));

        //---- userNameTx ----
        userNameTx.setText("admin");
        add(userNameTx, "cell 0 0");

        //---- passwordTx ----
        passwordTx.setText("admin123");
        add(passwordTx, "cell 0 0");

        //---- loginBut ----
        loginBut.setText("\u767b\u5f55");
        add(loginBut, "cell 1 0");

        //---- loginOutBut ----
        loginOutBut.setText("\u9000\u51fa");
        add(loginOutBut, "cell 1 0");

        //======== scrollPane1 ========
        {

            //---- msgTrea ----
            msgTrea.setLineWrap(true);
            scrollPane1.setViewportView(msgTrea);
        }
        add(scrollPane1, "cell 0 1,growy");

        //---- testBut ----
        testBut.setText("test");
        add(testBut, "cell 1 1");
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner non-commercial license
    private JTextField userNameTx;
    private JTextField passwordTx;
    private JButton loginBut;
    private JButton loginOutBut;
    private JScrollPane scrollPane1;
    private JTextArea msgTrea;
    private JButton testBut;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}

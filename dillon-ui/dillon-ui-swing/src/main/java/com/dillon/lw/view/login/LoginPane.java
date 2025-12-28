/*
 * Created by JFormDesigner on Fri Jan 24 09:32:20 CST 2025
 */

package com.dillon.lw.view.login;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.dillon.lw.module.system.controller.admin.auth.vo.AuthLoginReqVO;
import com.dillon.lw.module.system.controller.admin.auth.vo.AuthLoginRespVO;
import com.dillon.lw.module.system.controller.admin.auth.vo.AuthPermissionInfoRespVO;
import com.dillon.lw.SwingExceptionHandler;
import com.dillon.lw.store.AppStore;
import com.dillon.lw.view.frame.MainFrame;
import com.dillon.lw.api.system.AuthApi;
import com.dtflys.forest.Forest;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author wenli
 */
public class LoginPane extends JPanel {
    private List<FunctionType> functions = new ArrayList<>();
    private Timer timer;

    public LoginPane() {
        initComponents();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner non-commercial license
        infoPane = new JPanel();
        contentPane = new JPanel();
        logoLabel = new JLabel();
        titleLabel = new JLabel();
        subTitleLabel = new JLabel();
        progressBar = new JProgressBar();
        userNameField = new JTextField("admin");
        passwordField = new JPasswordField("admin123");
        rememberCheckBox = new JCheckBox();
        msgLabel = new JLabel();
        loginButton = new JButton();
        corporationText = new JLabel();

        //======== this ========
        setOpaque(false);
        setPreferredSize(new Dimension(900, 580));
        setLayout(new MigLayout(
            "fill,hidemode 3",
            // columns
            "0[fill]0" +
            "[380!,fill]0",
            // rows
            "0[]0"));

        //======== infoPane ========
        {
            infoPane.setOpaque(false);
            infoPane.setBorder(new EmptyBorder(20, 40, 20, 40));
            infoPane.setLayout(new CardLayout());
        }
        add(infoPane, "cell 0 0,grow");

        //======== contentPane ========
        {
            contentPane.setOpaque(false);
            contentPane.setBorder(new EmptyBorder(5, 30, 5, 45));
            contentPane.setLayout(new MigLayout(
                "fill,hidemode 3",
                // columns
                "[fill]",
                // rows
                "[fill]" +
                "[45!]0" +
                "[30!]0" +
                "[10!]0" +
                "[50!]" +
                "[50!]" +
                "[20!]0" +
                "[30!,grow,fill]0" +
                "[50!]" +
                "[]"));

            //---- logoLabel ----
            logoLabel.setText("text");
            contentPane.add(logoLabel, "cell 0 0,align center bottom,grow 0 0");

            //---- titleLabel ----
            titleLabel.setText("Dillon-Admin-Pro");
            titleLabel.setFont(new Font(".AppleSystemUIFont", Font.BOLD, 30));
            contentPane.add(titleLabel, "cell 0 1,align center bottom,grow 0 0");

            //---- subTitleLabel ----
            subTitleLabel.setText("\u6b22\u8fce\u767b\u5f55");
            contentPane.add(subTitleLabel, "cell 0 2,align center top,grow 0 0");
            contentPane.add(progressBar, "cell 0 3");
            contentPane.add(userNameField, "cell 0 4,growy");
            contentPane.add(passwordField, "cell 0 5,growy");

            //---- rememberCheckBox ----
            rememberCheckBox.setText("\u8bb0\u4f4f\u5bc6\u7801");
            contentPane.add(rememberCheckBox, "cell 0 6,alignx right,growx 0");

            //---- msgLabel ----
            msgLabel.setForeground(new Color(0xff0066));
            contentPane.add(msgLabel, "cell 0 7,alignx center,grow 0 100");

            //---- loginButton ----
            loginButton.setText("\u767b\u5f55");
            loginButton.setMinimumSize(new Dimension(49, 34));
            contentPane.add(loginButton, "cell 0 8,growy");

            //---- corporationText ----
            corporationText.setText("@liwen");
            contentPane.add(corporationText, "cell 0 9,align center bottom,grow 0 0");
        }
        add(contentPane, "cell 1 0,growy");
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on

        // 添加功能
        loadSvg();

        initCustomComponents();
        intListeners();
    }

    private void loadSvg() {
        functions.add(new FunctionType("用户管理", "用户管理功能允许管理员轻松管理系统中的用户", "icons/user-manage.svg"));
        functions.add(new FunctionType("数据分析与报表", "数据分析与报表功能提供了系统内部数据的分析和可视化工具", "icons/baobiaofenxi.svg"));
        functions.add(new FunctionType("权限管理", "权限管理功能允许管理员灵活设置系统内各项功能的访问权限", "icons/quanxian.svg"));
        functions.add(new FunctionType("日志审计与监控", "日志审计与监控功能记录和跟踪系统内的操作日志", "icons/jiankong.svg"));
    }

    private void initCustomComponents() {
        logoLabel.setText("");
        logoLabel.setIcon(new FlatSVGIcon("icons/guanli.svg", 60, 60));
        subTitleLabel.putClientProperty("FlatLaf.style", "font: bold 20");

        progressBar.setVisible(false);
        userNameField.setFont(UIManager.getFont("h3.font"));
        userNameField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "用户名");
        userNameField.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON, new FlatSVGIcon("icons/icon_username.svg", 35, 35));
        userNameField.putClientProperty(FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON, true);

        passwordField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "用户密码");
        passwordField.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON, new FlatSVGIcon("icons/mima.svg", 35, 35));
        passwordField.putClientProperty(FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON, true);
        passwordField.setFont(UIManager.getFont("h3.font"));

        msgLabel.setVisible(false);
        loginButton.setForeground(new Color(0xffffff));
        loginButton.setBackground(UIManager.getColor("App.accentColor"));


        // 创建面板数组
        for (FunctionType function : functions) {
            JPanel panel = new JPanel(new BorderLayout(20, 30));
            panel.setOpaque(false);
            JPanel panel2 = new JPanel(new BorderLayout(10, 0));
            panel2.setOpaque(false);

            JLabel label = new JLabel(new FlatSVGIcon(function.getIcon(), 400, 400));
            JLabel mainLabel = new JLabel(function.getTitle());
            mainLabel.setFont(UIManager.getFont("h0.font"));
            mainLabel.setHorizontalAlignment(SwingConstants.CENTER);
            JLabel subLabel = new JLabel("<html>" + function.getDescription() + "</html>");
            subLabel.setFont(UIManager.getFont("h3.font"));
            subLabel.setHorizontalAlignment(SwingConstants.CENTER);
            label.setHorizontalAlignment(SwingConstants.CENTER);

            panel2.add(mainLabel, BorderLayout.NORTH);
            panel2.add(subLabel, BorderLayout.CENTER);

            panel.add(label, BorderLayout.NORTH);
            panel.add(panel2, BorderLayout.CENTER);

            infoPane.add(panel);

        }

    }

    private void intListeners() {
        loginButton.addActionListener(e -> login());
        timer = new Timer(3000, e -> {
            CardLayout cardLayout = (CardLayout) infoPane.getLayout();
            cardLayout.next(infoPane);
        });
        timer.start();
    }

    private void login() {

        AuthLoginReqVO authLoginReqVO = new AuthLoginReqVO();
        authLoginReqVO.setUsername(userNameField.getText());
        authLoginReqVO.setPassword(new String(passwordField.getPassword()));

        updateUiBeforeRequest();
        CompletableFuture.supplyAsync(() -> {
            AuthApi authApi = Forest.client(AuthApi.class);
            AuthLoginRespVO authLoginRespVO = authApi.login(authLoginReqVO).getCheckedData();
            // 更新 Token
            AppStore.setAuthLoginRespVO(authLoginRespVO);
            // 发起权限信息请求
            return authApi.getPermissionInfo().getCheckedData();
        }).thenAcceptAsync(authPermissionInfo -> {
            handleSuccess(authPermissionInfo);
        }, SwingUtilities::invokeLater).whenComplete((unused, throwable) -> {
            SwingUtilities.invokeLater(() -> {
                resetUiAfterRequest();
                if (throwable != null) {
                    handleError(throwable);
                    SwingExceptionHandler.handle(throwable);
                }
            });
        });

    }

    private void updateUiBeforeRequest() {
        msgLabel.setVisible(false);
        loginButton.setText("正在请求登录...");
        loginButton.setEnabled(false);
        progressBar.setIndeterminate(true);
        progressBar.setVisible(true);

    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setComposite(AlphaComposite.SrcOver.derive(0.95f));

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        GradientPaint gradientPaint = new GradientPaint(0, 0, new Color(0x63A1FA), 0, getHeight(), new Color(0x97A7ED));
        g2.setPaint(gradientPaint);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
        g2.dispose();
    }

    private void resetUiAfterRequest() {
        loginButton.setEnabled(true);
        loginButton.setText("登录");
        progressBar.setVisible(false);
        progressBar.setIndeterminate(false);
    }

    private void handleSuccess(AuthPermissionInfoRespVO authPermissionInfo) {
        msgLabel.setVisible(false);
        msgLabel.setText("");
        AppStore.setAuthPermissionInfoRespVO(authPermissionInfo);
        timer.stop();
        MainFrame.getInstance().showMain();
        AppStore.loadDictData();
    }

    private void handleError(Throwable throwable) {
        msgLabel.setVisible(true);
//        msgLabel.setText(throwable.getMessage());
        loginButton.setEnabled(true);
        loginButton.setText("登录");
    }

    class FunctionType {
        private String title;
        private String description;
        private String icon;

        public FunctionType(String title, String description, String icon) {
            this.title = title;
            this.description = description;
            this.icon = icon;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        public String getIcon() {
            return icon;
        }
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner non-commercial license
    private JPanel infoPane;
    private JPanel contentPane;
    private JLabel logoLabel;
    private JLabel titleLabel;
    private JLabel subTitleLabel;
    private JProgressBar progressBar;
    private JTextField userNameField;
    private JPasswordField passwordField;
    private JCheckBox rememberCheckBox;
    private JLabel msgLabel;
    private JButton loginButton;
    private JLabel corporationText;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}

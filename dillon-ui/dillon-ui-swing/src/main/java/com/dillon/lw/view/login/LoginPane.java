/*
 * Created by JFormDesigner on Fri Jan 24 09:32:20 CST 2025
 */

package com.dillon.lw.view.login;

import com.dillon.lw.api.system.AuthApi;
import com.dillon.lw.components.AlphaPanel;
import com.dillon.lw.components.AutoCompleteField;
import com.dillon.lw.config.UserHistory;
import com.dillon.lw.config.UserHistoryService;
import com.dillon.lw.eventbus.EventBusCenter;
import com.dillon.lw.eventbus.event.LoginEvent;
import com.dillon.lw.module.system.controller.admin.auth.vo.AuthLoginReqVO;
import com.dillon.lw.module.system.controller.admin.auth.vo.AuthLoginRespVO;
import com.dillon.lw.module.system.controller.admin.auth.vo.AuthPermissionInfoRespVO;
import com.dillon.lw.store.AppStore;
import com.dillon.lw.utils.ExecuteUtils;
import com.dtflys.forest.Forest;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.extras.components.FlatButton;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wenli
 */
public class LoginPane extends JPanel {
    private List<FunctionType> functions = new ArrayList<>();
    private Timer timer;

    public LoginPane() {
        initComponents();
        initData();
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
        userNameField = new AutoCompleteField();
        passwordField = new JPasswordField();
        panel1 = new JPanel();
        delButton = new FlatButton();
        rememberCheckBox = new JCheckBox();
        loginButton = new JButton();
        corporationText = new JLabel();

        //======== this ========
        setPreferredSize(new Dimension(900, 580));
        setLayout(new MigLayout(
            "fill,hidemode 3",
            // columns
            "0[grow,fill]0" +
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
                "[50!]0" +
                "[40!,grow,center]0" +
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

            //---- userNameField ----
            userNameField.setText("admin");
            contentPane.add(userNameField, "cell 0 4,growy");

            //---- passwordField ----
            passwordField.setText("admin123");
            contentPane.add(passwordField, "cell 0 5,growy");

            //======== panel1 ========
            {
                panel1.setOpaque(false);
                panel1.setLayout(new MigLayout(
                    "fill,hidemode 3",
                    // columns
                    "[fill]",
                    // rows
                    "0[40!,grow,center]0"));

                //---- delButton ----
                delButton.setText("\u79fb\u9664\u5e10\u53f7");
                panel1.add(delButton, "cell 0 0");

                //---- rememberCheckBox ----
                rememberCheckBox.setText("\u8bb0\u4f4f\u5bc6\u7801");
                panel1.add(rememberCheckBox, "cell 0 0");
            }
            contentPane.add(panel1, "cell 0 6,alignx right,growx 0");

            //---- loginButton ----
            loginButton.setText("\u767b\u5f55");
            loginButton.setMinimumSize(new Dimension(49, 34));
            contentPane.add(loginButton, "cell 0 7,growy");

            //---- corporationText ----
            corporationText.setText("@liwen");
            corporationText.setForeground(new Color(0xcecece));
            contentPane.add(corporationText, "cell 0 8,align center bottom,grow 0 0");
        }
        add(contentPane, "cell 1 0,growy");
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
// 2. 设置自定义渲染器 (示例：让文字变蓝色)
        delButton.setButtonType(FlatButton.ButtonType.toolBarButton);
        delButton.setFocusable(false);
        delButton.putClientProperty("FlatLaf.internal.testing.ignore", true);
        userNameField.setCellRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = new JLabel(value.toString());

            if (value instanceof UserHistory) {
                label.setText(value.getUsername());
            }
            label.setOpaque(true);
            if (isSelected) {
                label.setForeground(list.getSelectionForeground());
                label.setBackground(list.getSelectionBackground());
            } else {
                label.setForeground(list.getForeground());
                label.setBackground(list.getBackground());
            }
            return label;
        });
        // 配置：告诉组件通过 User 的 name 属性搜索
        userNameField.setMapper(user -> user.getUsername());
        userNameField.getList().setFixedCellHeight(40);
        userNameField.getList().addListSelectionListener(e -> {

        });
        // 添加功能
        loadSvg();

        initCustomComponents();
        intListeners();
    }


    private void initData() {

        userNameField.setDataList(UserHistoryService.loadUsers());
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

        this.setBackground(new Color(0x32BAF6));
        progressBar.setVisible(false);
        userNameField.setFont(UIManager.getFont("h3.font"));
        userNameField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "用户名");
        userNameField.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON, new FlatSVGIcon("icons/icon_username.svg", 35, 35));
        userNameField.putClientProperty(FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON, true);

        passwordField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "用户密码");
        passwordField.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON, new FlatSVGIcon("icons/mima.svg", 35, 35));
        passwordField.putClientProperty(FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON, true);
        passwordField.setFont(UIManager.getFont("h3.font"));

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

        userNameField.getList().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;

            UserHistory u = userNameField.getSelectedValue();
            if (u == null) return;

            // 仅用于 UI 预览，不要提交
            passwordField.setText(u.getPasswrod());
        });

        delButton.addActionListener(e -> {
            if (userNameField.getSelectedValue() != null) {
                UserHistoryService.removeUser(userNameField.getSelectedValue().getUserId() + "");
                userNameField.setText("");
                passwordField.setText("");
                userNameField.setDataList(UserHistoryService.loadUsers());
            }
        });
    }

    private void login() {

        AuthLoginReqVO authLoginReqVO = new AuthLoginReqVO();
        authLoginReqVO.setUsername(userNameField.getText());
        authLoginReqVO.setPassword(new String(passwordField.getPassword()));

        updateUiBeforeRequest();
        // 2. 使用封装的 ExecuteUtils 发起异步请求
        ExecuteUtils.execute(
                () -> {
                    // 【后台线程】执行耗时操作
                    AuthApi authApi = Forest.client(AuthApi.class);
                    AuthLoginRespVO authLoginRespVO = authApi.login(authLoginReqVO).getCheckedData();

                    // 存储 Token 等登录信息
                    AppStore.setAuthLoginRespVO(authLoginRespVO);

                    // 获取用户权限和个人信息
                    return authApi.getPermissionInfo().getCheckedData();
                },
                authPermissionInfo -> {
                    // 【EDT 线程】请求成功后处理业务逻辑（跳转主界面等）
                    handleSuccess(authPermissionInfo);
                    if (rememberCheckBox.isSelected()) {
                        UserHistoryService.recordLogin(
                                new UserHistory(authPermissionInfo.getUser().getId()+"", authPermissionInfo.getUser().getUsername(), authLoginReqVO.getPassword()),
                                true
                        );
                    }

                },
                () -> {
                    // 【EDT 线程】无论成功失败，重置 UI 状态（恢复按钮点击、隐藏动画）
                    resetUiAfterRequest();
                }
        );

    }

    private void updateUiBeforeRequest() {
        loginButton.setText("正在请求登录...");
        loginButton.setEnabled(false);
        progressBar.setIndeterminate(true);
        progressBar.setVisible(true);

    }


    private void resetUiAfterRequest() {
        loginButton.setEnabled(true);
        loginButton.setText("登录");
        progressBar.setVisible(false);
        progressBar.setIndeterminate(false);
    }

    private void handleSuccess(AuthPermissionInfoRespVO authPermissionInfo) {
        AppStore.setAuthPermissionInfoRespVO(authPermissionInfo);
        timer.stop();
        AppStore.loadDictData();

        EventBusCenter.get().post(new LoginEvent(0));
    }

    private void handleError(Throwable throwable) {
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

    @Override
    public void removeNotify() {
        timer.stop();
        super.removeNotify();
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner non-commercial license
    private JPanel infoPane;
    private JPanel contentPane;
    private JLabel logoLabel;
    private JLabel titleLabel;
    private JLabel subTitleLabel;
    private JProgressBar progressBar;
    private AutoCompleteField<UserHistory> userNameField;
    private JPasswordField passwordField;
    private JPanel panel1;
    private FlatButton delButton;
    private JCheckBox rememberCheckBox;
    private JButton loginButton;
    private JLabel corporationText;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}

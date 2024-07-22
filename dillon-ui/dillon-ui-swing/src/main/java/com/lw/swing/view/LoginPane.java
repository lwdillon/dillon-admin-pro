/*
 * Created by JFormDesigner on Thu May 09 14:58:09 CST 2024
 */

package com.lw.swing.view;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.lw.dillon.admin.framework.common.pojo.CommonResult;
import com.lw.dillon.admin.module.system.controller.admin.auth.vo.AuthLoginReqVO;
import com.lw.dillon.admin.module.system.controller.admin.auth.vo.AuthLoginRespVO;
import com.lw.dillon.admin.module.system.controller.admin.auth.vo.AuthPermissionInfoRespVO;
import com.lw.swing.request.Request;
import com.lw.swing.store.AppStore;
import com.lw.ui.request.api.system.AuthFeign;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.lw.swing.view.MainPrefs.KEY_USER;
import static com.lw.swing.view.MainPrefs.KEY_USER_CUR;

/**
 * @author wenli
 */
public class LoginPane extends JPanel {
    private JPanel infoPane;
    private JPanel contentPane;
    private JLabel titleLabel;
    private JLabel subTitleLabel;
    private JProgressBar progressBar;
    private JTextField usernameTextFiled;
    private JPasswordField passwordTextField;
    private JCheckBox rememberPwd;
    private JButton loginBut;
    private JSeparator separator1;
    private JLabel label2;
    private JLabel label3;
    private JLabel msgLabel;
    private JLabel msg1Label;
    private JLabel msg2Label;
    private List<FunctionType> functions = new ArrayList<>();
    private Timer timer;

    private CardLayout cardLayout;

    public LoginPane() {


        // 添加功能
        functions.add(new FunctionType("用户管理", "用户管理功能允许管理员轻松管理系统中的用户...", "icons/user-manage.svg"));
        functions.add(new FunctionType("数据分析与报表", "数据分析与报表功能提供了系统内部数据的分析和可视化工具...", "icons/baobiaofenxi.svg"));
        functions.add(new FunctionType("权限管理", "权限管理功能允许管理员灵活设置系统内各项功能的访问权限...", "icons/quanxian.svg"));
        functions.add(new FunctionType("日志审计与监控", "日志审计与监控功能记录和跟踪系统内的操作日志...", "icons/jiankong.svg"));

        initComponents();
        initListeners();
        startTimer();
    }

    private void initListeners() {
        loginBut.addActionListener(e -> {
            login();

        });

        // 创建 DocumentListener
        usernameTextFiled.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateMessage();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateMessage();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateMessage();
            }

            private void updateMessage() {
                if (usernameTextFiled.getText().isEmpty()) {
                    usernameTextFiled.putClientProperty("JComponent.outline", "error");

                    msg1Label.setVisible(true);
                    msg1Label.setText("请输入用户名");
                } else {
                    usernameTextFiled.putClientProperty("JComponent.outline", null);

                    msg1Label.setVisible(false);
                    msg1Label.setText("");
                }
            }
        });
        passwordTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateMessage();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateMessage();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateMessage();
            }

            private void updateMessage() {
                if (passwordTextField.getText().isEmpty()) {
                    passwordTextField.putClientProperty("JComponent.outline", "error");

                    msg2Label.setVisible(true);
                    msg2Label.setText("请输入密码");
                } else {
                    passwordTextField.putClientProperty("JComponent.outline", null);

                    msg2Label.setVisible(false);
                    msg2Label.setText("");
                }
            }
        });
    }

    private void initComponents() {

        infoPane = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setComposite(AlphaComposite.SrcOver.derive(0.95f));

                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gradientPaint = new GradientPaint(0, 0, new Color(0x63A1FA), 0, getHeight(), new Color(0x97A7ED));
                g2.setPaint(gradientPaint);
                g2.fillRoundRect(0, 0, getWidth() + 80, getHeight(), 40, 40);
                g2.dispose();
            }
        };
        contentPane = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
//                g2.setComposite(AlphaComposite.SrcOver.derive(0.95f));

                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(UIManager.getColor("App.titleBarBackground"));
                g2.fillRoundRect(-80, 0, getWidth() + 80, getHeight(), 40, 40);
                g2.dispose();
            }
        };
        titleLabel = new JLabel();
        subTitleLabel = new JLabel();
        progressBar = new JProgressBar();
        usernameTextFiled = new JTextField("admin");
        passwordTextField = new JPasswordField("admin123");
        rememberPwd = new JCheckBox();
        loginBut = new JButton();
        separator1 = new JSeparator();
        label2 = new JLabel();
        label3 = new JLabel();
        msgLabel = new JLabel();

        //======== this ========
        setOpaque(false);
        setLayout(new BorderLayout());

        progressBar.setVisible(false);


        //======== infoPane ========
        {
            infoPane.setPreferredSize(new Dimension(520, 0));
            infoPane.setOpaque(false);
            infoPane.setLayout(cardLayout = new CardLayout());
            infoPane.setBorder(new EmptyBorder(70, 70, 70, 70));

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

                label.setHorizontalAlignment(SwingConstants.CENTER);

                panel2.add(mainLabel, BorderLayout.NORTH);
                panel2.add(subLabel, BorderLayout.CENTER);

                panel.add(label, BorderLayout.NORTH);
                panel.add(panel2, BorderLayout.CENTER);

                infoPane.add(panel);

            }
        }
        add(infoPane, BorderLayout.CENTER);


        //======== contentPane ========
        {
            contentPane.setBorder(new EmptyBorder(70, 70, 70, 70));
            contentPane.setPreferredSize(new Dimension(432, 346));
            contentPane.setOpaque(false);
            contentPane.setLayout(new MigLayout("insets 0,hidemode 3,aligny center,gap 0 0",
                    // columns
                    "[grow,left]",
                    // rows
                    "[fill]10[fill]10[fill]30[fill]5[fill]15[45!]5[fill]15[fill]15[fill]15[45!]15[20!]15[fill]20[fill]"));

            //---- titleLabel ----
            titleLabel.setText("登录");
            titleLabel.setIcon(new FlatSVGIcon("icons/guanli.svg", 30, 30));
            titleLabel.putClientProperty("FlatLaf.style", "font: bold 30");
            contentPane.add(titleLabel, "cell 0 0,alignx center,growx 0");
            //---- subTitleLabel ----
            subTitleLabel.setText("Dillon-admin-pro");
            subTitleLabel.putClientProperty("FlatLaf.style", "font: bold 20");
            contentPane.add(subTitleLabel, "cell 0 1,alignx center,growx 0");
            contentPane.add(progressBar, "cell 0 2,grow");

            //---- usernameTextFiled ----
            usernameTextFiled.setPreferredSize(new Dimension(49, 45));
            usernameTextFiled.setFont(UIManager.getFont("h3.font"));
            usernameTextFiled.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "用户名");
            usernameTextFiled.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON, new FlatSVGIcon("icons/icon_username.svg", 35, 35));
            usernameTextFiled.putClientProperty(FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON, true);

            contentPane.add(usernameTextFiled, "cell 0 3,grow");
            msg1Label = new JLabel();
            msg1Label.setVisible(false);
            msg1Label.setForeground(new Color(0xf81d22));
            contentPane.add(msg1Label, "cell 0 4,grow");

            //---- passwordTextField ----
            passwordTextField.setPreferredSize(new Dimension(49, 45));
            passwordTextField.setPreferredSize(new Dimension(49, 45));
            passwordTextField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "用户密码");
            passwordTextField.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON, new FlatSVGIcon("icons/mima.svg", 35, 35));
            passwordTextField.putClientProperty(FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON, true);
            passwordTextField.setFont(UIManager.getFont("h3.font"));
            contentPane.add(passwordTextField, "cell 0 5,grow");
            msg2Label = new JLabel();
            msg2Label.setVisible(false);
            msg2Label.setForeground(new Color(0xf81d22));
            contentPane.add(msg2Label, "cell 0 6,grow");

            //---- checkBox1 ----
            rememberPwd.setText("记住密码");
            contentPane.add(rememberPwd, "cell 0 7,alignx trailing,growx 0");
            //---- msgLabel ----
            msgLabel.setVisible(false);
            msgLabel.setForeground(new Color(0xf81d22));
            contentPane.add(msgLabel, "cell 0 8,alignx center,growx 0");
            //---- button1 ----
            loginBut.setText("登 录");
            loginBut.setForeground(new Color(0xffffff));
            loginBut.setBackground(UIManager.getColor("App.accentColor"));
            loginBut.setPreferredSize(new Dimension(49, 45));
            contentPane.add(loginBut, "cell 0 9,aligny center,grow 100 0");

            //---- separator1 ----
            separator1.setBorder(new TitledBorder(null, "or", TitledBorder.CENTER, TitledBorder.TOP));
            contentPane.add(separator1, "cell 0 10,aligny center,grow 100 0");

            //---- label2 ----
            label2.setText("lw-dillon");
            label2.setFont(UIManager.getFont("h3.font"));
            label2.setIcon(new FlatSVGIcon("icons/weixin.svg", 20, 20));
            contentPane.add(label2, "cell 0 11,alignx center,growx 0");

            //---- label3 ----
            label3.setText("421058391");
            label3.setFont(UIManager.getFont("h3.font"));
            label3.setIcon(new FlatSVGIcon("icons/qq.svg", 20, 20));
            contentPane.add(label3, "cell 0 12,alignx center,growx 0");
        }
        add(contentPane, BorderLayout.EAST);


        // 创建计时器
        timer = new Timer(3000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switchPanels();
            }
        });
    }

    public void startTimer() {
        timer.start();
    }

    private void switchPanels() {
        cardLayout.next(infoPane);
    }


    /**
     * 登录
     */
    private void login() {
        final String username = usernameTextFiled.getText();
        final String password = passwordTextField.getText();

        if (StrUtil.isBlank(username)) {
            usernameTextFiled.putClientProperty("JComponent.outline", "error");
            msgLabel.setVisible(true);
            msgLabel.setText("请输入用户名");
            return;
        } else if (StrUtil.isBlank(password)) {
            passwordTextField.putClientProperty("JComponent.outline", "error");
            msgLabel.setVisible(true);
            msgLabel.setText("请输入密码");
            return;
        } else {
            msgLabel.setText("");
            msgLabel.setVisible(false);
        }

        AuthLoginReqVO authLoginReqVO = new AuthLoginReqVO();
        authLoginReqVO.setUsername(username);
        authLoginReqVO.setPassword(password);
        SwingWorker<CommonResult, String> worker = new SwingWorker<CommonResult, String>() {
            @Override
            protected CommonResult doInBackground() throws Exception {

                CommonResult<AuthLoginRespVO> commonResult = Request.buildApiClient(AuthFeign.class).login(authLoginReqVO);

                if (commonResult.isSuccess()) {
                    AppStore.setAuthLoginRespVO(commonResult.getData());

                    CommonResult<AuthPermissionInfoRespVO> permissionInfoRespVOCommonResult = Request.buildApiClient(AuthFeign.class).getPermissionInfo();

                    if (permissionInfoRespVOCommonResult.isSuccess()) {
                        AppStore.setAuthPermissionInfoRespVO(permissionInfoRespVOCommonResult.getData());
                    }

                    AppStore.loadDictData();
                }
                return commonResult;
            }

            @Override
            protected void done() {

                try {
                    if (get().isSuccess()) {
                        msgLabel.setVisible(false);
                        msgLabel.setText("");
                        if (rememberPwd.isSelected()) {
                            Map<String, Object> userMap = new HashMap<>();
                            userMap.put("username", username);
                            userMap.put("password", password);
                            String json = JSONUtil.toJsonStr(userMap);
                            MainPrefs.getState().put(KEY_USER + username, json);
                            MainPrefs.getState().put(KEY_USER_CUR, json);
                        }

                        MainFrame.getInstance().showMainPane();

                    } else {
                        usernameTextFiled.setRequestFocusEnabled(true);
                        msgLabel.setVisible(true);
                        msgLabel.setText(get().getMsg());
                    }
                } catch (Exception e) {
                    msgLabel.setText("无法连接服务器，请检查服务器是否启动。");
                    e.printStackTrace();
                } finally {
                    setLoginStatus(false);
                }

            }

        };

        setLoginStatus(true);

        worker.execute();
    }

    /**
     * 设置登录状态
     *
     * @param bool 保龄球
     */
    private void setLoginStatus(boolean bool) {
        usernameTextFiled.requestFocusInWindow();
        progressBar.setIndeterminate(bool);
        passwordTextField.setEnabled(!bool);
        usernameTextFiled.setEnabled(!bool);
        loginBut.setEnabled(!bool);
        loginBut.setText(bool ? "正在登录..." : "登录");
        progressBar.setVisible(bool);
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
}

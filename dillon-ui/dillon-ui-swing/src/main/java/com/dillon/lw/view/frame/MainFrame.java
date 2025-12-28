package com.dillon.lw.view.frame;

import com.formdev.flatlaf.FlatClientProperties;
import com.jidesoft.swing.JideTabbedPane;
import com.dillon.lw.view.login.LoginPane;
import com.dillon.lw.view.mainpane.MainPane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.RoundRectangle2D;

public class MainFrame extends JFrame {
    private MainPane mainPane;
    private static MainFrame instance;
    private int cornerRadius = 20;

    // 私有构造函数，防止外部直接创建实例
    private MainFrame() {
        super(System.getProperty("app.name"));
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                super.windowClosed(e);
            }
        });

        // 监听组件大小变化，动态更新圆角形状
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateWindowShape();
            }
        });
    }

    private void updateWindowShape() {
        if (getExtendedState() == JFrame.MAXIMIZED_BOTH) {
            setShape(null);
        } else {
            setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius));
        }
    }

    // 提供单例实例的静态方法
    public static MainFrame getInstance() {
        if (instance == null) {
            synchronized (MainFrame.class) {
                if (instance == null) {
                    instance = new MainFrame();
                }
            }
        }
        return instance;
    }

    // 显示登录面板
    public void showLogin() {


        this.setSize(900, 600);
        this.setResizable(false);
        updateWindowShape();
        rootPane.setWindowDecorationStyle(JRootPane.PLAIN_DIALOG);
        rootPane.putClientProperty(FlatClientProperties.FULL_WINDOW_CONTENT, true);
        rootPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        this.setJMenuBar(null);
        this.setContentPane(new LoginPane());
        this.revalidate();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    // 显示主界面
    public void showMain() {
        updateWindowShape();
        rootPane.putClientProperty(FlatClientProperties.FULL_WINDOW_CONTENT, false);
        rootPane.putClientProperty(FlatClientProperties.TITLE_BAR_SHOW_TITLE, false);
        rootPane.putClientProperty(FlatClientProperties.TITLE_BAR_BACKGROUND, UIManager.getColor("App.titleBarBackground"));
        rootPane.putClientProperty(FlatClientProperties.TITLE_BAR_FOREGROUND, UIManager.getColor("App.titleBarForeground"));

        rootPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rootPane.setWindowDecorationStyle(JRootPane.FRAME);
        mainPane = new MainPane();
        this.setResizable(true);
        this.setBackground(null);
        this.setJMenuBar(mainPane.getTitleMenuBar());
        this.setContentPane(mainPane);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.revalidate();
    }


    public JideTabbedPane getTabbedPane() {

        if (mainPane != null) {
            return mainPane.getTabbedPane();
        }
        return null;
    }
}

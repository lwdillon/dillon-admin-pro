
package com.lw.swing.view;

import cn.hutool.core.util.StrUtil;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.extras.FlatAnimatedLafChange;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.util.LoggingFacade;
import com.jidesoft.swing.JideTabbedPane;
import com.lw.dillon.admin.framework.common.pojo.CommonResult;
import com.lw.dillon.admin.module.system.controller.admin.auth.vo.AuthPermissionInfoRespVO;
import com.lw.swing.components.ReflectionRepaintManager;
import com.lw.swing.components.ui.MainTabbedPaneUI;
import com.lw.swing.request.Request;
import com.lw.swing.store.AppStore;
import com.lw.swing.theme.DarkTheme;
import com.lw.swing.theme.GlazzedTheme;
import com.lw.swing.theme.LightTheme;
import com.lw.swing.utils.IconLoader;
import com.lw.swing.view.system.user.PersonalCenterPanel;
import com.lw.ui.request.api.system.AuthFeign;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutionException;

/**
 * @author wenli
 * @date 2024/05/09
 */
public class MainFrame extends JFrame {

    private static MainFrame instance;
    private static final String PREFS_ROOT_PATH = "/dillon-admin-pro-ui";
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private BufferedImage backgroundImageDark;
    private BufferedImage backgroundImageLight;

    private JMenuBar titleMenuBar;
    private SidePane sidePane;
    private JLabel titleLabel;

    private LoginPane loginPane;
    private JPanel content;
    private JToolBar statusBar;
    private JideTabbedPane tabbedPane;
    private JToggleButton menuBut;
    private JToolBar tabLeadingBar;
    private JToolBar tabTrailingBar;
    private JButton themeBut;
    private JButton refreshBut;


    private MainFrame() {
        MainPrefs.init(PREFS_ROOT_PATH);
        setUndecorated(true);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);


        rootPane.putClientProperty(FlatClientProperties.TITLE_BAR_SHOW_TITLE, false);
        rootPane.putClientProperty(FlatClientProperties.TITLE_BAR_BACKGROUND, UIManager.getColor("App.titleBarBackground"));
        rootPane.putClientProperty(FlatClientProperties.TITLE_BAR_FOREGROUND, UIManager.getColor("App.titleBarForeground"));


        installRepaintManager();

    }


    private void installRepaintManager() {
        ReflectionRepaintManager manager = new ReflectionRepaintManager();
        RepaintManager.setCurrentManager(manager);
    }

    public static MainFrame getInstance() {
        if (instance == null) {
            instance = new MainFrame();
        }
        return instance;
    }


    @Override
    public void setBackground(Color bgColor) {
        super.setBackground(bgColor);
    }

    /**
     * 显示登录
     */
    public void showLogin() {

        this.showMenuBar(false);
        this.setContentPane(loginPane = new LoginPane());
        this.setResizable(false);
        rootPane.setWindowDecorationStyle(JRootPane.PLAIN_DIALOG);
        rootPane.putClientProperty(FlatClientProperties.FULL_WINDOW_CONTENT, true);
        rootPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        this.setBackground(new Color(0, 0, 0, 0));

        this.setSize(960, 650);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    /**
     *
     */
    public void showMainPane() {
        this.showMenuBar(true);
        rootPane.putClientProperty(FlatClientProperties.FULL_WINDOW_CONTENT, false);

        this.setBackground(null);
        rootPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rootPane.setWindowDecorationStyle(JRootPane.FRAME);

        content = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                if (StrUtil.equals(UIManager.getLookAndFeel().getName(), DarkTheme.NAME)) {
                  g2.setColor(UIManager.getColor("App.background"));
                  g2.fillRect(0,0,getWidth(),getHeight());
                } else if (StrUtil.equals(UIManager.getLookAndFeel().getName(), LightTheme.NAME)) {
                    g2.drawImage(getBackgroundImageLight(), 0, 0, getWidth(), getHeight(), this);
                } else if (StrUtil.equals(UIManager.getLookAndFeel().getName(), GlazzedTheme.NAME)) {
                    g2.drawImage(getBackgroundImageDark(), 0, 0, getWidth(), getHeight(), this);
                }
                g2.dispose();
            }
        };
        content.add(getSidePane(), BorderLayout.WEST);
        content.add(getStatusBar(), BorderLayout.SOUTH);
        content.add(getTabbedPane(), BorderLayout.CENTER);
        this.setContentPane(content);

        this.setResizable(true);

        this.setLocationRelativeTo(null);
        this.setVisible(true);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);

    }


    public JToolBar getTabLeadingBar() {
        if (tabLeadingBar == null) {
            tabLeadingBar = new JToolBar() {
                @Override
                public Dimension getPreferredSize() {
                    return new Dimension(super.getPreferredSize().width, 60);
                }
            };
            tabLeadingBar.setOpaque(false);
            tabLeadingBar.add(menuBut = new JToggleButton(new FlatSVGIcon("icons/bars.svg", 25, 25)));
            tabLeadingBar.add(refreshBut = new JButton(new FlatSVGIcon("icons/refresh.svg", 25, 25)));
            menuBut.setSelected(true);
            menuBut.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    sidePane.setVisible(menuBut.isSelected());
                }
            });
        }
        return tabLeadingBar;
    }

    public JToolBar getTabTrailingBar() {
        if (tabTrailingBar == null) {
            tabTrailingBar = new JToolBar() {
                @Override
                public Dimension getPreferredSize() {
                    return new Dimension(super.getPreferredSize().width, 60);
                }
            };
        }
        return tabTrailingBar;
    }

    public BufferedImage getBackgroundImageDark() {
        if (backgroundImageDark == null) {
            backgroundImageDark = IconLoader.image("/images/bg2.jpg");
        }
        return backgroundImageDark;
    }

    public BufferedImage getBackgroundImageLight() {
        if (backgroundImageLight == null) {
            backgroundImageLight = IconLoader.image("/images/mainBack.png");
        }
        return backgroundImageLight;
    }

    public JideTabbedPane getTabbedPane() {
        if (tabbedPane == null) {
            tabbedPane = new JideTabbedPane() {
                @Override
                public void updateUI() {
                    super.updateUI();
                    setUI(new MainTabbedPaneUI());
                }
            };
            tabbedPane.setShowCloseButton(true);
            tabbedPane.setShowCloseButtonOnTab(true);
            tabbedPane.setShowCloseButtonOnMouseOver(true);
            tabbedPane.addTab("主页", new FlatSVGIcon("icons/home.svg", 25, 25), new JLabel());
            tabbedPane.setTabClosableAt(0, false);
            tabbedPane.setTabLeadingComponent(getTabLeadingBar());
            tabbedPane.setTabTrailingComponent(getTabTrailingBar());
            tabbedPane.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    tabForegroundChanged();
                }
            });
//            tabbedPane.setBoldActiveTab(true);

            tabbedPane.setUI(new MainTabbedPaneUI());
            tabbedPane.setFont(UIManager.getFont("Label.font").deriveFont(16f));
            tabForegroundChanged();

        }
        return tabbedPane;
    }

    public void addTab(AuthPermissionInfoRespVO.MenuVO menuVO) {
        if (getTabbedPane().indexOfTab(menuVO.getName()) == -1) {
            FlatSVGIcon icon = IconLoader.getSvgIcon("icons/menu/" + menuVO.getIcon().split(":")[1] + ".svg", 25, 25);

            getTabbedPane().addTab(menuVO.getName(), icon, AppStore.getNavigatonPanel(menuVO.getComponentSwing()));
        }
        getTabbedPane().setSelectedIndex(getTabbedPane().indexOfTab(menuVO.getName()));
        tabForegroundChanged();


    }


    private void tabForegroundChanged() {
        int tabCount = tabbedPane.getTabCount();
        int selectCount = tabbedPane.getSelectedIndex();
        for (int i = 0; i < tabCount; i++) {
            FlatSVGIcon icon = (FlatSVGIcon) tabbedPane.getIconAt(i);
            if (icon == null) {
                continue;
            }
            Color color = null;

            if (selectCount == i) {
                color = UIManager.getColor("TabbedPane.selectedForeground");

            } else {
                color = UIManager.getColor("Label.foreground");
            }
            tabbedPane.setForegroundAt(i, color);
            tabbedPane.setBackground(UIManager.getColor("TabbedPane.background"));
            Color finalColor = color;
            icon.setColorFilter(new FlatSVGIcon.ColorFilter(c -> {
                return finalColor;
            }));

        }
    }

    public JMenuBar getTitleMenuBar() {
        if (titleMenuBar == null) {
            titleMenuBar = new JMenuBar();
            // left
            titleMenuBar.add(getTitleLabel());
            titleMenuBar.add(Box.createGlue());
            // right
            titleMenuBar.add(getThemeBut());
            titleMenuBar.add(getUserBut());
        }
        return titleMenuBar;
    }

    public JButton getThemeBut() {
        if (themeBut == null) {


            themeBut = new JButton();
            themeBut.putClientProperty("JButton.buttonType", "toolBarButton");

            themeBut.setIcon(new FlatSVGIcon("icons/skin.svg", 26, 26));
            themeBut.addActionListener(e -> showPopupSkinButtonActionPerformed(e));
        }
        return themeBut;
    }

    public JLabel getTitleLabel() {
        if (titleLabel == null) {
            titleLabel = new JLabel("Dillon-Pro-管理系统");
            titleLabel.putClientProperty("FlatLaf.styleClass", "h1");
            titleLabel.setIcon(new FlatSVGIcon("icons/guanli.svg", 45, 45));
            titleLabel.setBorder(BorderFactory.createEmptyBorder(7, 3, 7, 7));

        }
        return titleLabel;
    }

    public JButton getUserBut() {

        // 欢迎标签  TODO: 从登录信息中获取用户名

        FlatSVGIcon svgIcon = new FlatSVGIcon("icons/user.svg", 25, 25);
//        svgIcon.setColorFilter(new FlatSVGIcon.ColorFilter(color -> {
//            return new Color(0xffffff);
//        }));
        JButton userBut = new JButton(AppStore.getAuthPermissionInfoRespVO().getUser().getNickname());
        userBut.setIcon(svgIcon);
        userBut.putClientProperty("JButton.buttonType", "toolBarButton");
        userBut.setFocusable(false);
        userBut.setFont(userBut.getFont().deriveFont(18f));
        userBut.addActionListener(e -> showPopupMenuButtonActionPerformed((Component) e.getSource()));
        return userBut;
    }


    public SidePane getSidePane() {
        if (sidePane == null) {
            sidePane = new SidePane();
            sidePane.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, UIManager.getColor("App.borderColor")));

        }
        return sidePane;
    }

    public JToolBar getStatusBar() {
        if (statusBar == null) {
            statusBar = new JToolBar();
            statusBar.setFloatable(false);
            statusBar.setPreferredSize(new Dimension(0, 45));
            statusBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UIManager.getColor("App.borderColor")));

            statusBar.add(Box.createGlue());
            JLabel label = new JLabel("管理后台");
            statusBar.add(label);
            statusBar.add(Box.createGlue());
            // 时间标签
            JLabel timeLabel = new JLabel(TIME_FORMATTER.format(LocalDateTime.now()), SwingConstants.CENTER);
//            timeLabel.setFont(timeLabel.getFont().deriveFont(18f));
            Timer timer = new Timer(1000, event -> timeLabel.setText(TIME_FORMATTER.format(LocalDateTime.now())));
            timer.start();
            statusBar.add(timeLabel);
            statusBar.add(Box.createHorizontalStrut(15));


        }
        return statusBar;
    }

    /**
     * 皮肤显示弹出按钮执行操作
     *
     * @param e e
     */
    private void showPopupSkinButtonActionPerformed(ActionEvent e) {
        Component invoker = (Component) e.getSource();
        JPopupMenu themePopupMenu = new JPopupMenu();

        ButtonGroup group = new ButtonGroup();

        JCheckBoxMenuItem lighterMenuItem = new JCheckBoxMenuItem("白色");
        group.add(lighterMenuItem);
        themePopupMenu.add(lighterMenuItem);
        lighterMenuItem.addActionListener(e1 -> theme("白色"));


        JCheckBoxMenuItem darkMenuItem = new JCheckBoxMenuItem("深色");
        darkMenuItem.addActionListener(e1 -> theme("深色"));
        themePopupMenu.add(darkMenuItem);
        group.add(darkMenuItem);

        JCheckBoxMenuItem glazzedMenuItem = new JCheckBoxMenuItem("玻璃");
        glazzedMenuItem.addActionListener(e1 -> theme("玻璃"));
        themePopupMenu.add(glazzedMenuItem);
        group.add(glazzedMenuItem);


        themePopupMenu.show(invoker, 0, invoker.getHeight());

    }

    /**
     * 主题
     */
    private void theme(String theme) {

        EventQueue.invokeLater(() -> {
            FlatAnimatedLafChange.showSnapshot();


            try {
                switch (theme) {

                    case "深色": {

                        UIManager.setLookAndFeel(DarkTheme.class.getName());
                        break;
                    }
                    case "白色": {

                        UIManager.setLookAndFeel(LightTheme.class.getName());
                        break;
                    }
                    case "玻璃": {
                        UIManager.setLookAndFeel(GlazzedTheme.class.getName());
                        break;

                    }
                    default: {
                         UIManager.setLookAndFeel(LightTheme.class.getName());
                        break;

                    }

                }

                sidePane.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, UIManager.getColor("App.borderColor")));
                statusBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UIManager.getColor("App.borderColor")));

            } catch (Exception ex) {
                LoggingFacade.INSTANCE.logSevere(null, ex);
            }
            titleMenuBar.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
            rootPane.putClientProperty(FlatClientProperties.TITLE_BAR_BACKGROUND, UIManager.getColor("App.titleBarBackground"));
            rootPane.putClientProperty(FlatClientProperties.TITLE_BAR_FOREGROUND, UIManager.getColor("App.titleBarForeground"));
            content.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UIManager.getColor("App.borderColor")));
            FlatLaf.updateUI();


            FlatSVGIcon.ColorFilter.getInstance()
                    .add(new Color(0x6e6e6e), new Color(0x6e6e6e), new Color(0xffffff));
            tabForegroundChanged();

            FlatAnimatedLafChange.hideSnapshotWithAnimation();

        });
    }


    private void showMenuBar(boolean isShow) {
        setJMenuBar(isShow ? getTitleMenuBar() : null);
        this.revalidate();
        this.repaint();
    }

    /**
     * 显示弹出菜单按钮执行操作
     */
    private void showPopupMenuButtonActionPerformed(Component invoker) {
        JPopupMenu popupMenu = new JPopupMenu();

        JPanel infoPanel = new JPanel(new BorderLayout());
        JLabel label = new JLabel("", JLabel.CENTER);
        label.setIcon(new FlatSVGIcon("icons/user.svg", 80, 80));
        label.setText("admin");
        label.setVerticalTextPosition(SwingConstants.BOTTOM);  //必须设置文字树直方向位置
        label.setHorizontalTextPosition(SwingConstants.CENTER);
        infoPanel.add(label, BorderLayout.CENTER);
        infoPanel.add(new JLabel("系统管理员", JLabel.CENTER), BorderLayout.SOUTH);
        label.setPreferredSize(new Dimension(240, 100));
        JMenuItem menuItem9 = new JMenuItem("个人信息");
        menuItem9.setIcon(new FlatSVGIcon("icons/gerenxinxi.svg", 25, 25));
        JMenuItem menuItem11 = new JMenuItem("退出");
        menuItem11.setIcon(new FlatSVGIcon("icons/logout.svg", 25, 25));

        menuItem11.addActionListener(e1 -> loginOut());
        menuItem9.addActionListener(e1 -> {

            int tabIndex = MainFrame.getInstance().getTabbedPane().indexOfTab("个人信息");
            PersonalCenterPanel personalCenterPanel;
            if (tabIndex == -1) {
                personalCenterPanel = new PersonalCenterPanel();
                MainFrame.getInstance().getTabbedPane().addTab("个人信息", personalCenterPanel);
            } else {
                personalCenterPanel = (PersonalCenterPanel) MainFrame.getInstance().getTabbedPane().getComponentAt(tabIndex);
            }
            MainFrame.getInstance().getTabbedPane().setSelectedIndex(MainFrame.getInstance().getTabbedPane().indexOfTab("个人信息"));
            personalCenterPanel.updateData();


        });

        popupMenu.add(infoPanel);
        popupMenu.addSeparator();
        popupMenu.add(menuItem9);
        popupMenu.addSeparator();
        popupMenu.add(menuItem11);
        popupMenu.show(invoker, 0, invoker.getHeight());

    }

    @Override
    public void dispose() {
        super.dispose();
    }

    @Override
    public void setTitle(String title) {
        super.setTitle(title);
        getTitleLabel().setText(title);
    }

    public void loginOut(){
        SwingWorker<CommonResult<Boolean>,Object> swingWorker=new SwingWorker< CommonResult<Boolean>, Object>() {
            @Override
            protected  CommonResult<Boolean> doInBackground() throws Exception {
                return Request.buildApiClient(AuthFeign.class).logout();

            }

            @Override
            protected void done() {
                try {
                    if (get().isSuccess()) {
                        showLogin();
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        swingWorker.execute();
    }
}

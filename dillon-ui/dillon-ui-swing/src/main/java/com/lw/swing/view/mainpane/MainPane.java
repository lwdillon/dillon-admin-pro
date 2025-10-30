/*
 * Created by JFormDesigner on Fri Jan 24 10:02:57 CST 2025
 */

package com.lw.swing.view.mainpane;

import cn.hutool.core.util.StrUtil;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.extras.FlatAnimatedLafChange;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.util.LoggingFacade;
import com.jidesoft.swing.JideTabbedPane;
import com.lw.dillon.admin.module.infra.controller.admin.config.vo.ConfigSaveReqVO;
import com.lw.dillon.admin.module.system.controller.admin.auth.vo.AuthPermissionInfoRespVO;
import com.lw.swing.http.PayLoad;
import com.lw.swing.http.RetrofitServiceManager;
import com.lw.swing.store.AppStore;
import com.lw.swing.theme.DarkTheme;
import com.lw.swing.theme.GlazzedTheme;
import com.lw.swing.theme.LightTheme;
import com.lw.swing.utils.IconLoader;
import com.lw.swing.view.frame.MainFrame;
import com.lw.swing.view.system.notice.MyNotifyMessagePane;
import com.lw.swing.view.system.user.PersonalCenterPanel;
import com.lw.ui.api.config.ConfigApi;
import io.reactivex.rxjava3.schedulers.Schedulers;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.treetable.AbstractTreeTableModel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * @author wenli
 */
public class MainPane extends JPanel {
    private JToggleButton menuBut;
    private JLabel titleLabel;
    private JToolBar tabLeadingBar;
    private JToolBar tabTrailingBar;
    private JButton themeBut;
    private JButton refreshBut;
    private JButton noticeBut;
    private JPopupMenu themePopupMenu;
    private JPopupMenu personalPopupMenu;
    private JMenuBar titleMenuBar;

    public MainPane() {
        initComponents();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner non-commercial license
        navBarPane = new JPanel();
        scrollPane1 = new JScrollPane();
        navBarTreeTable = new JXTreeTable();
        tabbedPane = new JideTabbedPane();
        statusPane = new JToolBar();

        //======== this ========
        setLayout(new BorderLayout());

        //======== navBarPane ========
        {
            navBarPane.setPreferredSize(new Dimension(320, 376));
            navBarPane.setLayout(new BorderLayout());

            //======== scrollPane1 ========
            {
                scrollPane1.setViewportView(navBarTreeTable);
            }
            navBarPane.add(scrollPane1, BorderLayout.CENTER);
        }
        add(navBarPane, BorderLayout.WEST);
        add(tabbedPane, BorderLayout.CENTER);

        //======== statusPane ========
        {
            statusPane.setFloatable(false);
        }
        add(statusPane, BorderLayout.SOUTH);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
        initCustomComponents();
        initListeners();
        updateTreeTableRoot();
    }

    private void initCustomComponents() {

        scrollPane1.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

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

        tabbedPane.setFont(UIManager.getFont("Label.font").deriveFont(16f));
        tabForegroundChanged();

        tabbedPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                if (SwingUtilities.isRightMouseButton(e)) {
                    showPopupMenu(e);
                }
            }
        });
        navBarPane.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, UIManager.getColor("App.borderColor")));
        navBarTreeTable.setTreeTableModel(new MenuModel());
        navBarTreeTable.setShowHorizontalLines(true);
        navBarTreeTable.setOpaque(false);
        navBarTreeTable.setIntercellSpacing(new Dimension(1, 1));
        // 设置表头高度
        JTableHeader header = new JTableHeader() {
            @Override
            public Dimension getMaximumSize() {
                return new Dimension(super.getMaximumSize().width, 30);
            }
        };
        navBarTreeTable.setTableHeader(header);
        navBarTreeTable.setRowHeight(45);
        navBarTreeTable.setHighlighters(new ColorHighlighter(HighlightPredicate.ROLLOVER_ROW, UIManager.getColor("App.hoverBackground"), null));

        navBarTreeTable.setTreeCellRenderer(new DefaultTreeCellRenderer() {
            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                Component component = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
                if (component instanceof JLabel) {
                    if (value instanceof AuthPermissionInfoRespVO.MenuVO) {
                        AuthPermissionInfoRespVO.MenuVO menuVO = (AuthPermissionInfoRespVO.MenuVO) value;
                        ((JLabel) component).setText(menuVO.getName());
                        String icon = menuVO.getIcon();
                        if (StrUtil.isBlank(menuVO.getIcon())) {
                            icon = "icons/item.svg";
                        } else if (StrUtil.contains(icon, ":")) {
                            icon = "icons/menu/" + icon.split(":")[1] + ".svg";
                        }
                        FlatSVGIcon svgIcon = IconLoader.getSvgIcon(icon, 25, 25);


                        ((JLabel) component).setIcon(svgIcon);
                        ((JLabel) component).setIconTextGap(7);

                    }


                }
                return component;
            }
        });
        navBarTreeTable.getColumnExt(0).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (component instanceof JLabel) {
                    ((JLabel) component).setIcon(new FlatSVGIcon("icons/item.svg", 25, 25));
                }
                return component;
            }
        });
    }

    private void initListeners() {
        navBarTreeTable.addTreeSelectionListener(e -> {
            if (e.getNewLeadSelectionPath() == null) {
                return;
            }
            Object object = e.getNewLeadSelectionPath().getLastPathComponent();

            if (object instanceof AuthPermissionInfoRespVO.MenuVO) {
                if (StrUtil.isNotBlank(((AuthPermissionInfoRespVO.MenuVO) object).getComponentSwing())) {
                    addTab((AuthPermissionInfoRespVO.MenuVO) object);
                }
            }
        });
    }

    public void addTab(AuthPermissionInfoRespVO.MenuVO menuVO) {
        if (tabbedPane.indexOfTab(menuVO.getName()) == -1) {
            FlatSVGIcon icon = IconLoader.getSvgIcon("icons/menu/" + menuVO.getIcon().split(":")[1] + ".svg", 25, 25);

            tabbedPane.addTab(menuVO.getName(), icon, AppStore.getNavigatonPanel(menuVO.getComponentSwing()));
        }
        tabbedPane.setSelectedIndex(tabbedPane.indexOfTab(menuVO.getName()));
        tabForegroundChanged();


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
                    navBarPane.setVisible(menuBut.isSelected());
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

    public JMenuBar getTitleMenuBar() {
        if (titleMenuBar == null) {
            titleMenuBar = new JMenuBar() {
                @Override
                public void updateUI() {
                    super.updateUI();
                    if (themePopupMenu != null) {
                        SwingUtilities.updateComponentTreeUI(themePopupMenu);
                    }
                    if (personalPopupMenu != null) {
                        SwingUtilities.updateComponentTreeUI(personalPopupMenu);
                    }
                }
            };
            // left
            titleMenuBar.add(getTitleLabel());
            titleMenuBar.add(Box.createGlue());
            // right
            titleMenuBar.add(getThemeBut());
            titleMenuBar.add(getNoticeBut());
            titleMenuBar.add(getUserBut());
            titleMenuBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UIManager.getColor("App.borderColor")));

        }
        return titleMenuBar;
    }

    public JButton getNoticeBut() {
        if (noticeBut == null) {
            noticeBut = new JButton();
            noticeBut.setIcon(new FlatSVGIcon("icons/bell.svg", 25, 25));
            noticeBut.putClientProperty("JButton.buttonType", "toolBarButton");
            noticeBut.setFocusable(false);
            noticeBut.addActionListener(e -> {
                int tabIndex = tabbedPane.indexOfTab("我的消息");
                MyNotifyMessagePane myNotifyMessagePane;
                if (tabIndex == -1) {
                    myNotifyMessagePane = new MyNotifyMessagePane();
                    tabbedPane.addTab("我的消息", new FlatSVGIcon("icons/bell.svg", 25, 25), myNotifyMessagePane);
                } else {
                    myNotifyMessagePane = (MyNotifyMessagePane) tabbedPane.getComponentAt(tabIndex);
                }
                tabbedPane.setSelectedIndex(tabbedPane.indexOfTab("我的消息"));
                myNotifyMessagePane.updateData();

            });

        }
        return noticeBut;
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

    /**
     * 皮肤显示弹出按钮执行操作
     *
     * @param e e
     */
    private void showPopupSkinButtonActionPerformed(ActionEvent e) {
        Component invoker = (Component) e.getSource();
        if (themePopupMenu == null) {

            themePopupMenu = new JPopupMenu();
            ButtonGroup group = new ButtonGroup();

            JCheckBoxMenuItem lighterMenuItem = new JCheckBoxMenuItem("白色");
            group.add(lighterMenuItem);
            themePopupMenu.add(lighterMenuItem);
            lighterMenuItem.addActionListener(e1 -> {
                theme("白色");
                updateUserTheme("白色");
            });


            JCheckBoxMenuItem darkMenuItem = new JCheckBoxMenuItem("深色");
            darkMenuItem.addActionListener(e1 -> {
                theme("深色");
                updateUserTheme("深色");
            });
            themePopupMenu.add(darkMenuItem);
            group.add(darkMenuItem);

            JCheckBoxMenuItem glazzedMenuItem = new JCheckBoxMenuItem("玻璃");
            glazzedMenuItem.addActionListener(e1 -> {
                theme("玻璃");
                updateUserTheme("玻璃");
            });
            themePopupMenu.add(glazzedMenuItem);
            group.add(glazzedMenuItem);

        }


        themePopupMenu.show(invoker, 0, invoker.getHeight());

    }

    /**
     * 主题
     */
    public void theme(String theme) {

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

                navBarPane.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, UIManager.getColor("App.borderColor")));
                statusPane.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UIManager.getColor("App.borderColor")));


            } catch (Exception ex) {
                LoggingFacade.INSTANCE.logSevere(null, ex);
            }
            MainFrame.getInstance().getRootPane().putClientProperty(FlatClientProperties.TITLE_BAR_BACKGROUND, UIManager.getColor("App.titleBarBackground"));
            MainFrame.getInstance().getRootPane().putClientProperty(FlatClientProperties.TITLE_BAR_FOREGROUND, UIManager.getColor("App.titleBarForeground"));
            titleMenuBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UIManager.getColor("App.borderColor")));
            FlatLaf.updateUI();


            FlatSVGIcon.ColorFilter.getInstance()
                    .add(new Color(0x6e6e6e), new Color(0x6e6e6e), new Color(0xffffff));
            tabForegroundChanged();

            FlatAnimatedLafChange.hideSnapshotWithAnimation();

        });


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

    public void updateUserTheme(String theme) {


        String finalUserTheme = theme;
        String key = "swing.theme.userid." + AppStore.getAuthPermissionInfoRespVO().getUser().getId();
        ConfigApi configApi=  RetrofitServiceManager.getInstance().create(ConfigApi.class);

        // 使用 flatMap 按顺序执行第一个请求后，执行第二个请求
        configApi.getConfig(key) .map(new PayLoad<>()) // 第一个请求
                .flatMap(configRespVO -> {
                    // 第二个请求依赖于第一个请求的结果
                    if (configRespVO == null) {
                        ConfigSaveReqVO saveReqVO = new ConfigSaveReqVO();
                        saveReqVO.setKey(key);
                        saveReqVO.setValue(finalUserTheme);
                        saveReqVO.setVisible(true);
                        saveReqVO.setCategory("ui");
                        saveReqVO.setName("用户主题");
                        return configApi.createConfig(saveReqVO);
                    } else {
                        ConfigSaveReqVO saveReqVO = new ConfigSaveReqVO();
                        saveReqVO.setId(configRespVO.getId());
                        saveReqVO.setName(configRespVO.getName());
                        saveReqVO.setCategory(configRespVO.getCategory());
                        saveReqVO.setValue(finalUserTheme);
                        saveReqVO.setKey(configRespVO.getKey());
                        saveReqVO.setVisible(true);
                        return configApi.updateConfig(saveReqVO);
                    }
                })
                .subscribeOn(Schedulers.io())  // 在IO线程中执行请求
                .observeOn(Schedulers.from(SwingUtilities::invokeLater))  // 在主线程处理结果
                .subscribe(
                        result -> {

                        },
                        throwable -> {
                            // 错误处理
                            throwable.printStackTrace();
                        }
                );




    }


    /**
     * 显示弹出菜单按钮执行操作
     */
    private void showPopupMenuButtonActionPerformed(Component invoker) {
        if (personalPopupMenu == null) {
            personalPopupMenu = new JPopupMenu();
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

            menuItem11.addActionListener(e1 -> {
                MainFrame.getInstance().showLogin();
            });
            menuItem9.addActionListener(e1 -> {

                int tabIndex = tabbedPane.indexOfTab("个人信息");
                PersonalCenterPanel personalCenterPanel;
                if (tabIndex == -1) {
                    personalCenterPanel = new PersonalCenterPanel();
                    tabbedPane.addTab("个人信息", personalCenterPanel);
                } else {
                    personalCenterPanel = (PersonalCenterPanel) tabbedPane.getComponentAt(tabIndex);
                }
                tabbedPane.setSelectedIndex(tabbedPane.indexOfTab("个人信息"));
                personalCenterPanel.updateData();


            });

            personalPopupMenu.add(infoPanel);
            personalPopupMenu.addSeparator();
            personalPopupMenu.add(menuItem9);
            personalPopupMenu.addSeparator();
            personalPopupMenu.add(menuItem11);
        }


        personalPopupMenu.show(invoker, 0, invoker.getHeight());

    }

    public JLabel getTitleLabel() {
        if (titleLabel == null) {
            titleLabel = new JLabel("后台管理系统");
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

    private void updateTreeTableRoot() {
        AuthPermissionInfoRespVO.MenuVO rootNode = new AuthPermissionInfoRespVO.MenuVO();
        rootNode.setName("root");
        rootNode.setChildren(AppStore.getMenus());
        navBarTreeTable.setTreeTableModel(new MenuModel(rootNode));

    }

    private void showPopupMenu(final MouseEvent event) {

        // 如果当前事件与右键菜单有关（单击右键），则弹出菜单
        final int index = ((JTabbedPane) event.getComponent()).getUI().tabForCoordinate(tabbedPane, event.getX(), event.getY());
        final int count = ((JTabbedPane) event.getComponent()).getTabCount();
        final String title = tabbedPane.getTitleAt(index);

        if (index == -1) {
            return;
        }
        JPopupMenu pop = new JPopupMenu();
        JMenuItem closeCurrent = new JMenuItem("关闭当前");
        closeCurrent.setPreferredSize(new Dimension(140, 30));

        closeCurrent.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (tabbedPane.isTabClosableAt(index)) {
                    tabbedPane.removeTabAt(index);
                }
            }
        });
        pop.add(closeCurrent);
        pop.addSeparator();

        JMenuItem closeLeft = new JMenuItem("关闭左侧标签");
        closeLeft.setPreferredSize(new Dimension(140, 30));

        closeLeft.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                for (int j = (index - 1); j >= 0; j--) {
                    if (tabbedPane.isTabClosableAt(j)) {
                        tabbedPane.removeTabAt(j);
                    }
                }
            }
        });
        pop.add(closeLeft);

        JMenuItem closeRight = new JMenuItem("关闭右侧标签");
        closeRight.setPreferredSize(new Dimension(140, 30));

        closeRight.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                for (int j = (count - 1); j > index; j--) {
                    if (tabbedPane.isTabClosableAt(j)) {
                        tabbedPane.removeTabAt(j);
                    }
                }
            }
        });
        closeRight.setPreferredSize(new Dimension(140, 30));
        pop.add(closeRight);

        JMenuItem other = new JMenuItem("关闭其它标签");
        other.setPreferredSize(new Dimension(140, 30));

        other.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                int tabCount = count;
                while (tabCount-- > 0) {
                    if (title.equals(tabbedPane.getTitleAt(tabCount))) {
                        continue;
                    }
                    if (tabbedPane.isTabClosableAt(tabCount)) {
                        tabbedPane.removeTabAt(tabCount);
                    }
                }
            }
        });
        pop.add(other);
        pop.addSeparator();

        JMenuItem all = new JMenuItem("关闭所有标签");
        all.setPreferredSize(new Dimension(140, 30));

        all.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                int tabCount = count;
                // We invoke removeTabAt for each tab, otherwise we may end up
                // removing Components added by the UI.
                while (tabCount-- > 0) {

                    if (tabbedPane.isTabClosableAt(tabCount)) {
                        tabbedPane.removeTabAt(tabCount);
                    }
                }
            }
        });
        pop.add(all);

        pop.show(event.getComponent(), event.getX(), event.getY());

    }

    class MenuModel extends AbstractTreeTableModel {

        public MenuModel() {

        }


        public MenuModel(Object root) {
            super(root);
        }

        @Override
        public Object getChild(Object parent, int index) {
            AuthPermissionInfoRespVO.MenuVO parentFile = (AuthPermissionInfoRespVO.MenuVO) parent;
            java.util.List<AuthPermissionInfoRespVO.MenuVO> children = parentFile.getChildren();
            return children != null ? children.get(index) : null;

        }

        @Override
        public int getChildCount(Object parent) {
            if (parent instanceof AuthPermissionInfoRespVO.MenuVO) {
                java.util.List<AuthPermissionInfoRespVO.MenuVO> children = ((AuthPermissionInfoRespVO.MenuVO) parent).getChildren();
                if (children != null) {
                    return children.size();
                }
            }

            return 0;
        }

        @Override
        public Class<?> getColumnClass(int column) {
            switch (column) {
                case 0:
                    return String.class;
                default:
                    return super.getColumnClass(column);
            }
        }

        @Override
        public int getColumnCount() {
            return 1;
        }

        @Override
        public String getColumnName(int column) {
            return "Name";
        }

        @Override
        public Object getValueAt(Object node, int column) {
            if (node instanceof AuthPermissionInfoRespVO.MenuVO) {
                AuthPermissionInfoRespVO.MenuVO menuVO = (AuthPermissionInfoRespVO.MenuVO) node;
                switch (column) {
                    case 0:
                        return menuVO.getName();

                }
            }

            return null;
        }


        public void setRoot(Object root) {
            this.root = root;
            this.modelSupport.fireNewRoot();
        }

        @Override
        public boolean isLeaf(Object node) {
            if (node instanceof AuthPermissionInfoRespVO.MenuVO) {
                return ((AuthPermissionInfoRespVO.MenuVO) node).getChildren() == null;
            } else {
                return true;
            }
        }

        @Override
        public int getIndexOfChild(Object parent, Object child) {
            if (parent instanceof AuthPermissionInfoRespVO.MenuVO && child instanceof AuthPermissionInfoRespVO.MenuVO) {
                AuthPermissionInfoRespVO.MenuVO parentFile = (AuthPermissionInfoRespVO.MenuVO) parent;
                List<AuthPermissionInfoRespVO.MenuVO> files = parentFile.getChildren();
                int i = 0;

                for (int len = files.size(); i < len; ++i) {
                    if (files.get(i).equals(child)) {
                        return i;
                    }
                }
            }

            return -1;
        }
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner non-commercial license
    private JPanel navBarPane;
    private JScrollPane scrollPane1;
    private JXTreeTable navBarTreeTable;
    private JideTabbedPane tabbedPane;
    private JToolBar statusPane;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on


    public JideTabbedPane getTabbedPane() {
        return tabbedPane;
    }
}

package com.dillon.lw.view.mainpane;

import cn.hutool.core.util.StrUtil;
import com.dillon.lw.components.WCardPanel;
import com.dillon.lw.eventbus.EventBusCenter;
import com.dillon.lw.eventbus.event.AddMainTabEvent;
import com.dillon.lw.module.system.controller.admin.auth.vo.AuthPermissionInfoRespVO;
import com.dillon.lw.store.AppStore;
import com.dillon.lw.utils.IconLoader;
import com.dillon.lw.view.frame.MainFrame;
import com.dillon.lw.view.system.user.PersonalCenterPanel;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.google.common.eventbus.Subscribe;
import com.jidesoft.swing.JideTabbedPane;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.treetable.AbstractTreeTableModel;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * 主应用界面面板 (MainPane) - 兼容 Java 8
 * 负责整体布局、导航栏切换 (展开/折叠)、标签页管理、主题切换和用户操作处理。
 */
public class MainPane extends JPanel {
    // JFormDesigner 自动生成的组件声明 (保留)
    private JPanel navBarPane;
    private JScrollPane navBarTreeTablePane;
    private JXTreeTable navBarTreeTable;
    private JideTabbedPane tabbedPane;
    private JToolBar statusPane;

    // 自定义组件和状态变量
    private JToggleButton menuBut;
    private JLabel titleLabel;
    private JToolBar tabLeadingBar;
    private JToolBar tabTrailingBar;
    private JButton refreshBut;


    // 导航栏状态变量
    private int navBarExpandedWidth; // 导航栏展开时的宽度 (320px)
    private JPanel collapsedIconPanel; // 折叠状态下只显示图标的面板 (60px)

    /**
     * 构造函数。初始化组件和布局。
     */
    public MainPane() {
        initComponents();
        initCustomComponents();
        initListeners();
        EventBusCenter.get().register(this);
    }

    // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
    private void initComponents() {
        // ... (JFormDesigner Generated Code remains unchanged for structure)
        navBarPane = new WCardPanel();
        navBarPane.setBorder(BorderFactory.createEmptyBorder(10,10,10,0));
        navBarTreeTablePane = new JScrollPane();
        navBarTreeTablePane.setOpaque(false);
        navBarTreeTablePane.getViewport().setOpaque(false);
        navBarTreeTablePane.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
        navBarTreeTable = new JXTreeTable();
// TreeTable 本身
        navBarTreeTable.setOpaque(false);
        navBarTreeTable.setBackground(new Color(0, 0, 0, 0));

        tabbedPane = new JideTabbedPane();
        tabbedPane.setOpaque(false);
        statusPane = new JToolBar();

        //======== this ========
        setLayout(new BorderLayout(7,7));
        setBorder(BorderFactory.createEmptyBorder(7,7,0,7));
        //======== navBarPane ========
        {
            navBarPane.setPreferredSize(new Dimension(320, 376));
        }
        navBarTreeTablePane.setViewportView(navBarTreeTable);
        add(navBarPane, BorderLayout.WEST);
        WCardPanel wPanel=     new WCardPanel();
        wPanel.setLayout(new BorderLayout());
        wPanel.setBorder(BorderFactory.createEmptyBorder(0,10,10,10));
        wPanel.add(tabbedPane);
        add(wPanel, BorderLayout.CENTER);

        //======== statusPane ========
        {
            statusPane.setFloatable(false);
        }
        add(statusPane, BorderLayout.SOUTH);
    }
    // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on

    /**
     * 初始化自定义组件和布局逻辑。
     * 主要设置导航栏的 CardLayout 切换机制。
     */
    private void initCustomComponents() {

        // 记录导航栏展开时的宽度
        navBarExpandedWidth = navBarPane.getPreferredSize().width;
        // 1. 设置 navBarPane 使用 CardLayout 来切换视图
        navBarPane.setLayout(new CardLayout());

        // 2. 将展开菜单 (JTreeTable) 添加到 CardLayout
//        navBarTreeTablePane.setBorder(new FlatScrollPaneBorder());
        navBarPane.add(navBarTreeTablePane, "ExpandedMenu");

        // 3. 初始化并添加折叠图标面板 (CollapsedMenu)
        initCollapsedIconPanel();
        navBarPane.add(collapsedIconPanel, "CollapsedMenu");

        // 确保初始显示展开菜单
        ((CardLayout) navBarPane.getLayout()).show(navBarPane, "ExpandedMenu");

        // 4. 配置 JideTabbedPane
        tabbedPane.setShowCloseButton(true);
        tabbedPane.setShowCloseButtonOnTab(true);
        tabbedPane.setShowCloseButtonOnMouseOver(true);
        tabbedPane.putClientProperty("TabbedPane.tabHeight", 60);


        // 添加默认主页标签
        tabbedPane.addTab("主页", new FlatSVGIcon("icons/home.svg", 25, 25), new JPanel());
        tabbedPane.setTabClosableAt(0, false);

        // 设置标签页两侧的工具栏
        tabbedPane.setTabLeadingComponent(getTabLeadingBar());
        tabbedPane.setTabTrailingComponent(getTabTrailingBar());
        // 使用 Lambda 表达式 (Java 8 兼容)
        tabbedPane.addChangeListener(e -> tabForegroundChanged());

        tabbedPane.setFont(UIManager.getFont("Label.font").deriveFont(16f));
        tabForegroundChanged();

        tabbedPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                if (SwingUtilities.isRightMouseButton(e)) {
                    showPopupMenu(e); // 标签页右键菜单
                }
            }
        });

        // 5. 配置 JXTreeTable 样式和渲染器
        configureTreeTable();
    }

    /**
     * 配置导航菜单树表 (JXTreeTable) 的样式和单元格渲染器。
     */
    private void configureTreeTable() {
        navBarTreeTable.setTreeTableModel(new MenuModel());
//        navBarTreeTable.setShowHorizontalLines(true);
        navBarTreeTable.setOpaque(false);
//        navBarTreeTable.setIntercellSpacing(new Dimension(1, 1));

        // 设置表头高度
        JTableHeader header = new JTableHeader() {
            @Override
            public Dimension getMaximumSize() {
                return new Dimension(super.getMaximumSize().width, 30);
            }
        };
        navBarTreeTable.setTableHeader(header);
        navBarTreeTable.setRowHeight(45);

        navBarTreeTable.setHighlighters(new ColorHighlighter(HighlightPredicate.ROLLOVER_ROW,
                UIManager.getColor("App.hoverBackground"),
                null));
        // 设置树节点渲染器：用于显示菜单名称和图标 (Java 8 兼容)
        navBarTreeTable.setTreeCellRenderer(new DefaultTreeCellRenderer() {
            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                Component component = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
                // 使用传统 instanceof 检查和强制转换 (Java 8 兼容)
                if (component instanceof JLabel && value instanceof AuthPermissionInfoRespVO.MenuVO) {
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
                return component;
            }
        });

        // 设置表格列渲染器 
        navBarTreeTable.getColumnExt(0).setCellRenderer(new DefaultTableCellRenderer());
    }

    /**
     * 初始化事件监听器。
     */
    private void initListeners() {
        // 导航菜单点击事件
        navBarTreeTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = navBarTreeTable.rowAtPoint(e.getPoint());
                if (row < 0) return;

                Object obj = navBarTreeTable.getPathForRow(row).getLastPathComponent();

                // 使用传统 instanceof 检查和强制转换 (Java 8 兼容)
                if (obj instanceof AuthPermissionInfoRespVO.MenuVO) {
                    AuthPermissionInfoRespVO.MenuVO menuVO = (AuthPermissionInfoRespVO.MenuVO) obj;
                    // 如果是叶子节点 (具有组件路径)，则打开标签页
                    if (StrUtil.isNotBlank(menuVO.getComponentSwing())) {
                        addTab(menuVO);
                    }
                }
            }
        });
    }

    /**
     * 切换导航栏的展开/折叠状态。
     * 使用 FlatAnimatedLafChange 实现平滑过渡。
     *
     * @param isExpanded true 为展开 (宽度 navBarExpandedWidth)，false 为折叠 (宽度 60px)。
     */
    private void toggleNavBarState(boolean isExpanded) {
        CardLayout cl = (CardLayout) navBarPane.getLayout();


        if (isExpanded) {
            // 展开状态
            navBarPane.setPreferredSize(new Dimension(navBarExpandedWidth, navBarPane.getHeight()));
            cl.show(navBarPane, "ExpandedMenu");
            // 修复点：在切换到展开视图时，确保根节点展开
            navBarTreeTable.expandAll();
        } else {
            // 折叠状态
            navBarPane.setPreferredSize(new Dimension(60, navBarPane.getHeight()));
            cl.show(navBarPane, "CollapsedMenu");
        }

        revalidate();
        repaint();

    }

    /**
     * 初始化或重新初始化折叠状态下只显示图标的面板。
     * 创建一级菜单图标按钮，并为其添加鼠标悬停弹出菜单的逻辑。
     */
    private void initCollapsedIconPanel() {
        // 确保面板被创建和正确设置布局
        if (collapsedIconPanel == null) {
            collapsedIconPanel = new JPanel();
            collapsedIconPanel.setOpaque(false);
            collapsedIconPanel.setLayout(new BoxLayout(collapsedIconPanel, BoxLayout.Y_AXIS));
            collapsedIconPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 10));
        } else {
            // 清空旧组件，准备重新加载菜单图标
            collapsedIconPanel.removeAll();
        }

        List<AuthPermissionInfoRespVO.MenuVO> topLevelMenus = AppStore.getMenus();

        if (topLevelMenus != null) {
            for (AuthPermissionInfoRespVO.MenuVO menu : topLevelMenus) {
                JButton iconBut = createIconMenuButton(menu);
                collapsedIconPanel.add(iconBut);
                collapsedIconPanel.add(Box.createVerticalStrut(5)); // 添加垂直间距

                // 添加鼠标悬停监听器：实现弹出浮动菜单
                iconBut.addMouseListener(new MouseAdapter() {
                    private JPopupMenu popupMenu = null;

                    @Override
                    public void mouseEntered(MouseEvent e) {
                        // 仅在导航栏折叠时触发悬停弹出
                        if (!menuBut.isSelected()) {
                            // 检查是否有子菜单
                            if (menu.getChildren() != null && !menu.getChildren().isEmpty()) {

                                if (popupMenu == null) {
                                    popupMenu = createFloatingMenu(menu.getChildren());
                                }

                                if (popupMenu != null) {
                                    JButton source = (JButton) e.getSource();
                                    // 在按钮右侧弹出菜单，稍微偏移，模拟 Web 效果
                                    popupMenu.show(source, source.getWidth() - 5, 0);
                                }
                            }
                            // 折叠状态下的叶子节点不添加悬停打开，避免误操作
                        }
                    }
                });
            }
        }
    }

    /**
     * 创建折叠状态下显示的一级菜单图标按钮。
     *
     * @param menuVO 菜单数据对象。
     * @return 配置好的 JButton。
     */
    private JButton createIconMenuButton(AuthPermissionInfoRespVO.MenuVO menuVO) {
        String icon = menuVO.getIcon();
        if (StrUtil.isBlank(menuVO.getIcon())) {
            icon = "icons/item.svg";
        } else if (StrUtil.contains(icon, ":")) {
            icon = "icons/menu/" + icon.split(":")[1] + ".svg";
        }
        FlatSVGIcon svgIcon = IconLoader.getSvgIcon(icon, 25, 25);

        JButton but = new JButton(svgIcon);
        but.setToolTipText(menuVO.getName()); // 悬停提示菜单名称
        but.putClientProperty("JButton.buttonType", "toolBarButton");
        but.setAlignmentX(Component.CENTER_ALIGNMENT);
        but.setFocusable(false);

        // 尺寸设置为 50x45，居中于 60px 的面板
        but.setPreferredSize(new Dimension(50, 45));
        but.setMinimumSize(new Dimension(50, 45));
        but.setMaximumSize(new Dimension(50, 45));

        // 点击行为：直接打开标签页
        if (StrUtil.isNotBlank(menuVO.getComponentSwing())) {
            but.addActionListener(e -> addTab(menuVO));
        }

        return but;
    }

    /**
     * 递归创建浮动的 JPopupMenu，用于在导航栏折叠时显示菜单层级。
     *
     * @param children 当前菜单层级的子菜单列表。
     * @return 包含子菜单项的 JPopupMenu。
     */
    private JPopupMenu createFloatingMenu(List<AuthPermissionInfoRespVO.MenuVO> children) {
        if (children == null || children.isEmpty()) {
            return null;
        }

        JPopupMenu popup = new JPopupMenu();
        popup.setBorderPainted(true);
        popup.setOpaque(true);

        for (AuthPermissionInfoRespVO.MenuVO menu : children) {

            // 解析图标路径
            String iconPath = menu.getIcon();
            String resolvedIcon = "icons/item.svg";
            if (StrUtil.contains(iconPath, ":")) {
                resolvedIcon = "icons/menu/" + iconPath.split(":")[1] + ".svg";
            } else if (StrUtil.isNotBlank(iconPath)) {
                resolvedIcon = iconPath;
            }

            if (menu.getChildren() != null && !menu.getChildren().isEmpty()) {
                // 有子菜单：创建 JMenu
                JMenu subMenu = new JMenu(menu.getName());
                subMenu.setIcon(IconLoader.getSvgIcon(resolvedIcon, 25, 25));
                subMenu.setBorder(BorderFactory.createEmptyBorder(7, 7, 7, 7));

                // 递归创建子菜单的子项
                JPopupMenu subPopup = createFloatingMenu(menu.getChildren());
                if (subPopup != null) {
                    for (Component comp : subPopup.getComponents()) {
                        if (comp instanceof JMenuItem) {
                            JMenuItem menuItem = (JMenuItem) comp;
                            menuItem.setBorder(BorderFactory.createEmptyBorder(7, 7, 7, 7));
                            subMenu.add(menuItem);
                        }
                    }
                }
                popup.add(subMenu);
            } else {
                // 无子菜单：创建 JMenuItem
                JMenuItem item = new JMenuItem(menu.getName());
                item.setBorder(BorderFactory.createEmptyBorder(7, 7, 7, 7));
                item.setIcon(IconLoader.getSvgIcon(resolvedIcon, 25, 25));

                // 点击行为：打开新标签页
                if (StrUtil.isNotBlank(menu.getComponentSwing())) {
                    item.addActionListener(e -> addTab(menu));
                }
                popup.add(item);
            }
        }
        return popup;
    }

    /**
     * 向标签页中添加一个新的面板。
     * 如果标签页已存在，则选中它。
     *
     * @param menuVO 菜单数据对象，包含名称和组件路径。
     */
    public void addTab(AuthPermissionInfoRespVO.MenuVO menuVO) {
        if (tabbedPane.indexOfTab(menuVO.getName()) == -1) {
            String iconPath = menuVO.getIcon();
            String resolvedIcon = "icons/item.svg";
            if (StrUtil.contains(iconPath, ":")) {
                resolvedIcon = "icons/menu/" + iconPath.split(":")[1] + ".svg";
            } else if (StrUtil.isNotBlank(iconPath)) {
                resolvedIcon = iconPath;
            }

            FlatSVGIcon icon = IconLoader.getSvgIcon(resolvedIcon, 25, 25);
            tabbedPane.addTab(menuVO.getName(), icon, AppStore.getNavigatonPanel(menuVO.getComponentSwing()));
        }
        tabbedPane.setSelectedIndex(tabbedPane.indexOfTab(menuVO.getName()));
        tabForegroundChanged();
    }

    /**
     * 获取标签页前导工具栏（包含菜单切换按钮和刷新按钮）。
     */
    public JToolBar getTabLeadingBar() {
        if (tabLeadingBar == null) {
            tabLeadingBar = new JToolBar() {
                @Override
                public Dimension getPreferredSize() {
                    return new Dimension(super.getPreferredSize().width, 60);
                }
            };
            tabLeadingBar.setOpaque(false);

            // 菜单切换按钮
            tabLeadingBar.add(menuBut = new JToggleButton(new FlatSVGIcon("icons/bars.svg", 25, 25)));
            tabLeadingBar.add(refreshBut = new JButton(new FlatSVGIcon("icons/refresh.svg", 25, 25)));
            menuBut.setSelected(true); // 初始为展开状态

            // 添加状态监听器来切换导航栏
            menuBut.addItemListener(e -> toggleNavBarState(menuBut.isSelected()));
        }
        return tabLeadingBar;
    }

    /**
     * 获取标签页尾随工具栏（目前为空）。
     */
    public JToolBar getTabTrailingBar() {
        if (tabTrailingBar == null) {
            tabTrailingBar = new JToolBar() {
                @Override
                public Dimension getPreferredSize() {
                    return new Dimension(super.getPreferredSize().width, 60);
                }
            };
            tabTrailingBar.setOpaque(false);
        }
        return tabTrailingBar;
    }


    /**
     * 根据当前选中的标签页，更新标签页的前景颜色和图标颜色。
     */
    private void tabForegroundChanged() {
        int tabCount = tabbedPane.getTabCount();
        int selectCount = tabbedPane.getSelectedIndex();
        Color selectedColor = UIManager.getColor("TabbedPane.selectedForeground");
        Color defaultColor = UIManager.getColor("Label.foreground");

        for (int i = 0; i < tabCount; i++) {
            FlatSVGIcon icon = (FlatSVGIcon) tabbedPane.getIconAt(i);
            if (icon == null) continue;

            Color color = (selectCount == i) ? selectedColor : defaultColor;

            tabbedPane.setForegroundAt(i, color);
            tabbedPane.setBackground(UIManager.getColor("TabbedPane.background"));

            // 使用 ColorFilter 更新 SVG 图标的颜色
//            Color finalColor = color;
//            icon.setColorFilter(new FlatSVGIcon.ColorFilter(c -> finalColor));
        }
    }


    /**
     * 创建个人信息弹出菜单的内容。
     */
    private JPopupMenu createPersonalPopupMenu() {
        JPopupMenu popupMenu = new JPopupMenu();

        // 顶部用户信息面板
        JPanel infoPanel = new JPanel(new BorderLayout());
        JLabel label = new JLabel(AppStore.getAuthPermissionInfoRespVO().getUser().getNickname(), JLabel.CENTER);
        label.setIcon(new FlatSVGIcon("icons/user.svg", 80, 80));
        label.setVerticalTextPosition(SwingConstants.BOTTOM);
        label.setHorizontalTextPosition(SwingConstants.CENTER);
        infoPanel.add(label, BorderLayout.CENTER);
        infoPanel.add(new JLabel("系统管理员", JLabel.CENTER), BorderLayout.SOUTH);
        label.setPreferredSize(new Dimension(240, 100));
        popupMenu.add(infoPanel);
        popupMenu.addSeparator();

        // 个人信息项
        JMenuItem personalInfoItem = new JMenuItem("个人信息");
        personalInfoItem.setIcon(new FlatSVGIcon("icons/gerenxinxi.svg", 25, 25));
        personalInfoItem.addActionListener(e -> {
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
        popupMenu.add(personalInfoItem);
        popupMenu.addSeparator();

        // 退出登录项
        JMenuItem logoutItem = new JMenuItem("退出");
        logoutItem.setIcon(new FlatSVGIcon("icons/logout.svg", 25, 25));
        logoutItem.addActionListener(e -> MainFrame.getInstance().showLogin(false));
        popupMenu.add(logoutItem);

        return popupMenu;
    }

    /**
     * 获取应用主标题标签。
     */
    public JLabel getTitleLabel() {
        if (titleLabel == null) {
            titleLabel = new JLabel("后台管理系统");
            titleLabel.putClientProperty("FlatLaf.styleClass", "h1");
            titleLabel.setIcon(new FlatSVGIcon("icons/guanli.svg", 45, 45));
            titleLabel.setBorder(BorderFactory.createEmptyBorder(7, 3, 7, 7));
        }
        return titleLabel;
    }

    /**
     * 更新导航菜单的根节点数据，并在数据变更时刷新展开和折叠视图。
     */
    public void updateTreeTableRoot() {
        // 创建一个虚拟的根节点来包裹一级菜单
        AuthPermissionInfoRespVO.MenuVO rootNode = new AuthPermissionInfoRespVO.MenuVO();
        rootNode.setName("root");
        rootNode.setChildren(AppStore.getMenus());

        // 1. 更新展开状态的 TreeTable 数据模型
        MenuModel model = new MenuModel(rootNode);
        navBarTreeTable.setTreeTableModel(model);

        // ******************** 核心修复 ********************
        // JXTreeTable 默认只显示根节点，必须展开才能看到子菜单。
        navBarTreeTable.expandAll();
        // *************************************************

        // 2. 更新折叠状态的 Icon Panel 图标
        if (collapsedIconPanel != null) {
            initCollapsedIconPanel();
            collapsedIconPanel.revalidate();
            collapsedIconPanel.repaint();
        }

        // 如果当前处于折叠状态，强制刷新布局以确保图标显示正常
        if (menuBut != null && !menuBut.isSelected()) {
            navBarPane.revalidate();
            navBarPane.repaint();
        }
    }

    /**
     * 显示标签页的右键菜单。
     */
    private void showPopupMenu(final MouseEvent event) {
        final int index = tabbedPane.getUI().tabForCoordinate(tabbedPane, event.getX(), event.getY());
        final int count = tabbedPane.getTabCount();

        if (index == -1) return;

        JPopupMenu pop = new JPopupMenu();

        // 菜单项配置
        JMenuItem closeCurrent = createMenuItem("关闭当前", 140, () -> {
            if (tabbedPane.isTabClosableAt(index)) tabbedPane.removeTabAt(index);
        });

        JMenuItem closeLeft = createMenuItem("关闭左侧标签", 140, () -> {
            for (int j = (index - 1); j >= 0; j--) {
                if (tabbedPane.isTabClosableAt(j)) tabbedPane.removeTabAt(j);
            }
        });

        JMenuItem closeRight = createMenuItem("关闭右侧标签", 140, () -> {
            for (int j = (count - 1); j > index; j--) {
                if (tabbedPane.isTabClosableAt(j)) tabbedPane.removeTabAt(j);
            }
        });

        JMenuItem other = createMenuItem("关闭其它标签", 140, () -> {
            String currentTitle = tabbedPane.getTitleAt(index);
            for (int k = tabbedPane.getTabCount() - 1; k >= 0; k--) {
                if (!currentTitle.equals(tabbedPane.getTitleAt(k)) && tabbedPane.isTabClosableAt(k)) {
                    tabbedPane.removeTabAt(k);
                }
            }
        });

        JMenuItem all = createMenuItem("关闭所有标签", 140, () -> {
            for (int k = tabbedPane.getTabCount() - 1; k >= 0; k--) {
                if (tabbedPane.isTabClosableAt(k)) tabbedPane.removeTabAt(k);
            }
        });

        pop.add(closeCurrent);
        pop.addSeparator();
        pop.add(closeLeft);
        pop.add(closeRight);
        pop.add(other);
        pop.addSeparator();
        pop.add(all);

        pop.show(event.getComponent(), event.getX(), event.getY());
    }

    /**
     * 辅助方法：创建 JMenuItem 并设置尺寸和操作。
     */
    private JMenuItem createMenuItem(String text, int width, Runnable action) {
        JMenuItem item = new JMenuItem(text);
        item.setPreferredSize(new Dimension(width, 30));
        // 使用 Lambda 表达式 (Java 8 兼容)
        item.addActionListener(e -> action.run());
        return item;
    }


    /**
     * JXTreeTable 的数据模型。
     */
    class MenuModel extends AbstractTreeTableModel {

        public MenuModel() {
        }

        public MenuModel(Object root) {
            super(root);
        }

        @Override
        public Object getChild(Object parent, int index) {
            AuthPermissionInfoRespVO.MenuVO parentMenu = (AuthPermissionInfoRespVO.MenuVO) parent;
            List<AuthPermissionInfoRespVO.MenuVO> children = parentMenu.getChildren();
            return (children != null && index >= 0 && index < children.size()) ? children.get(index) : null;
        }

        @Override
        public int getChildCount(Object parent) {
            // 使用传统 instanceof 检查和强制转换 (Java 8 兼容)
            if (parent instanceof AuthPermissionInfoRespVO.MenuVO) {
                AuthPermissionInfoRespVO.MenuVO menuVO = (AuthPermissionInfoRespVO.MenuVO) parent;
                List<AuthPermissionInfoRespVO.MenuVO> children = menuVO.getChildren();
                return (children != null) ? children.size() : 0;
            }
            return 0;
        }

        @Override
        public int getColumnCount() {
            return 1; // 仅一列用于显示菜单名称和树结构
        }

        @Override
        public String getColumnName(int column) {
            return "菜单";
        }

        @Override
        public Object getValueAt(Object node, int column) {
            // 使用传统 instanceof 检查和强制转换 (Java 8 兼容)
            if (node instanceof AuthPermissionInfoRespVO.MenuVO) {
                AuthPermissionInfoRespVO.MenuVO menuVO = (AuthPermissionInfoRespVO.MenuVO) node;
                if (column == 0) {
                    return menuVO.getName();
                }
            }
            return null;
        }

        @Override
        public boolean isLeaf(Object node) {
            // 使用传统 instanceof 检查和强制转换 (Java 8 兼容)
            if (node instanceof AuthPermissionInfoRespVO.MenuVO) {
                AuthPermissionInfoRespVO.MenuVO menuVO = (AuthPermissionInfoRespVO.MenuVO) node;
                return menuVO.getChildren() == null || menuVO.getChildren().isEmpty();
            } else {
                return true;
            }
        }

        @Override
        public int getIndexOfChild(Object parent, Object child) {
            // 使用传统 instanceof 检查和强制转换 (Java 8 兼容)
            if (parent instanceof AuthPermissionInfoRespVO.MenuVO && child instanceof AuthPermissionInfoRespVO.MenuVO) {
                AuthPermissionInfoRespVO.MenuVO parentMenu = (AuthPermissionInfoRespVO.MenuVO) parent;
                List<AuthPermissionInfoRespVO.MenuVO> files = parentMenu.getChildren();
                return (files != null) ? files.indexOf(child) : -1;
            }
            return -1;
        }
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // ... (JFormDesigner Variables)
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on


    public JideTabbedPane getTabbedPane() {
        return tabbedPane;
    }

    @Override
    protected void paintComponent(Graphics g) {
//        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setPaint(UIManager.getColor("App.background"));
//        g2.setPaint(Color.RED);
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.dispose();
    }

    @Subscribe
    private void onAddTab(AddMainTabEvent addMainTabEvent) {

        SwingUtilities.invokeLater(() -> {
            String tabName = addMainTabEvent.getTabName();
            String icon = addMainTabEvent.getIcon();
            JComponent component = addMainTabEvent.getTabContent();
            int tabIndex = tabbedPane.indexOfTab(tabName);
            if (tabIndex == -1) {
                tabbedPane.addTab(tabName, new FlatSVGIcon(icon, 25, 25), component);
            }
            tabbedPane.setSelectedIndex(tabbedPane.indexOfTab(tabName));
        });


    }

    @Override
    public void updateUI() {
        super.updateUI();
        if (navBarTreeTable != null) {
            // 添加鼠标悬停高亮
            navBarTreeTable.setHighlighters(new ColorHighlighter(HighlightPredicate.ROLLOVER_ROW,
                    UIManager.getColor("App.hoverBackground"),
                    null));
        }

    }


    @Override
    public void removeNotify() {
        EventBusCenter.get().unregister(this);
        super.removeNotify();

    }
}
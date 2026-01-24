package com.dillon.lw.view.mainpane;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.pinyin.PinyinUtil;
import com.dillon.lw.api.system.AuthApi;
import com.dillon.lw.components.WPanel;
import com.dillon.lw.eventbus.EventBusCenter;
import com.dillon.lw.eventbus.event.AddMainTabEvent;
import com.dillon.lw.eventbus.event.MenuRefrestEvent;
import com.dillon.lw.eventbus.event.RefrestEvent;
import com.dillon.lw.exception.SwingExceptionHandler;
import com.dillon.lw.module.system.controller.admin.auth.vo.AuthPermissionInfoRespVO;
import com.dillon.lw.store.AppStore;
import com.dillon.lw.utils.IconLoader;
import com.dtflys.forest.Forest;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.google.common.eventbus.Subscribe;
import com.jidesoft.swing.JideTabbedPane;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.treetable.AbstractTreeTableModel;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.JTableHeader;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 主应用界面面板 (MainPane)
 * 采用 BorderLayout 布局：
 * - 西部 (WEST)：侧边导航栏（支持展开/折叠）
 * - 中部 (CENTER)：多标签页容器 (JideTabbedPane)
 * - 南部 (SOUTH)：状态栏
 */
public class MainPane extends JPanel {

    // --- 核心 UI 组件 ---
    private JPanel navBarPane;            // 侧边栏容器 (CardLayout)
    private JXTreeTable navBarTreeTable;  // 导航树表
    private JideTabbedPane tabbedPane;    // 标签页
    private JToolBar statusPane;          // 底部状态栏
    private JToggleButton menuToggleBut;  // 菜单切换按钮
    private JPanel collapsedIconPanel;    // 折叠后的图标面板

    // --- 配置属性 ---
    private int navBarExpandedWidth = 300;

    public MainPane() {
        initComponents();
        initListeners();
        // 注册 EventBus 监听
        EventBusCenter.get().register(this);
    }

    /**
     * 初始化界面组件结构
     */
    private void initComponents() {
        setLayout(new BorderLayout(7, 7));
        setBorder(BorderFactory.createEmptyBorder(0, 7, 0, 7));

        // 1. 初始化左侧导航容器 (使用 CardLayout 实现展开/收起切换)
        navBarPane = new WPanel();
        navBarPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 0));
        navBarPane.setPreferredSize(new Dimension(320, 376));
        navBarPane.setLayout(new CardLayout());

        // --- 展开状态面板 ---
        JPanel expandedPanel = new JPanel(new BorderLayout(10, 10));
        expandedPanel.setOpaque(false);
        setupTreeTable(); // 配置树表样式与模型

        JScrollPane treeScroll = new JScrollPane(navBarTreeTable);
        treeScroll.setOpaque(false);
        treeScroll.getViewport().setOpaque(false);
        treeScroll.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        expandedPanel.add(setupSearchBar(), BorderLayout.NORTH);
        expandedPanel.add(treeScroll, BorderLayout.CENTER);

        // --- 折叠状态面板 ---
        initCollapsedIconPanel();

        navBarPane.add(expandedPanel, "ExpandedMenu");
        navBarPane.add(collapsedIconPanel, "CollapsedMenu");
        navBarExpandedWidth = navBarPane.getPreferredSize().width;

        // 2. 初始化中央标签页
        setupTabbedPane();

        // 3. 底部状态栏
        statusPane = new JToolBar();
        statusPane.setFloatable(false);
        statusPane.setOpaque(false);

        // 组装主框架
        add(navBarPane, BorderLayout.WEST);
        add(tabbedPane, BorderLayout.CENTER);
        add(statusPane, BorderLayout.SOUTH);

        // 默认显示展开状态
        ((CardLayout) navBarPane.getLayout()).show(navBarPane, "ExpandedMenu");
    }

    // ===================================================================================
    // UI 构建辅助方法
    // ===================================================================================

    /**
     * 配置搜索工具栏
     */
    private JToolBar setupSearchBar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setOpaque(false);

        JTextField searchField = new JTextField();
        searchField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "搜索菜单...");
        searchField.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON, new FlatSVGIcon("icons/sousuo.svg", 18, 18));
        searchField.putClientProperty(FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON, true);

        // 搜索框实时过滤监听
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) { filter(); }
            public void insertUpdate(DocumentEvent e) { filter(); }
            public void removeUpdate(DocumentEvent e) { filter(); }
            private void filter() { applyTreeFilter(searchField.getText().trim()); }
        });

        toolBar.add(searchField);
        toolBar.add(createToolbarButton("icons/expand-up-down-line.svg", e -> navBarTreeTable.expandAll()));
        toolBar.add(createToolbarButton("icons/contract-up-down-line.svg", e -> navBarTreeTable.collapseAll()));
        return toolBar;
    }

    /**
     * 配置导航树表样式
     */
    private void setupTreeTable() {
        navBarTreeTable = new JXTreeTable();
        navBarTreeTable.setOpaque(false);
        navBarTreeTable.setBackground(new Color(0, 0, 0, 0));
        navBarTreeTable.setRowHeight(45);
        // 设置鼠标悬停高亮
        navBarTreeTable.setHighlighters(new ColorHighlighter(HighlightPredicate.ROLLOVER_ROW,
                UIManager.getColor("App.hoverBackground"), null));

        // 隐藏表格头
        navBarTreeTable.setTableHeader(new JTableHeader() {
            @Override public Dimension getPreferredSize() { return new Dimension(super.getPreferredSize().width, 0); }
        });

        // 自定义树节点渲染
        navBarTreeTable.setTreeCellRenderer(new DefaultTreeCellRenderer() {
            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                JLabel label = (JLabel) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
                if (value instanceof AuthPermissionInfoRespVO.MenuVO) {
                    AuthPermissionInfoRespVO.MenuVO menu = (AuthPermissionInfoRespVO.MenuVO) value;
                    label.setText(menu.getName());
                    label.setIcon(getMenuIcon(menu.getIcon(), 20));
                    label.setIconTextGap(10);
                }
                return label;
            }
        });
    }

    /**
     * 配置中央标签页及 UI 装饰
     */
    private void setupTabbedPane() {
        tabbedPane = new JideTabbedPane() {
            @Override
            protected void paintComponent(Graphics g) {
                // 自定义绘制：实现圆角背景和主内容区阴影感
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int arc = Convert.toInt(UIManager.getInt("App.arc"), 21);
                g2.setColor(UIManager.getColor("App.baseBackground"));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);

                g2.setPaint(UIManager.getColor("App.mainTabbedPaneBackground"));
                g2.fillRoundRect(5, 50, getWidth() - 10, getHeight() - 50, arc, arc);
                g2.fillRect(5, 50, getWidth() - 10, arc); // 衔接部分
                g2.dispose();
            }
        };

        tabbedPane.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
        tabbedPane.setShowCloseButton(true);
        tabbedPane.setShowCloseButtonOnTab(true);
        tabbedPane.setShowCloseButtonOnMouseOver(true);
        tabbedPane.setFont(UIManager.getFont("Label.font").deriveFont(16f));

        // 默认主页
        tabbedPane.addTab("主页", new FlatSVGIcon("icons/home.svg", 20, 20), new JPanel());
        tabbedPane.setTabClosableAt(0, false);

        // 设置标签栏辅助组件
        tabbedPane.setTabLeadingComponent(createTabLeadingBar());
        tabbedPane.setTabTrailingComponent(new JToolBar() {{ setOpaque(false); }});

        tabbedPane.addChangeListener(e -> updateTabStyles());
    }

    // ===================================================================================
    // 业务逻辑与事件
    // ===================================================================================

    /**
     * 树表搜索过滤核心逻辑
     */
    private void applyTreeFilter(String keyword) {
        List<AuthPermissionInfoRespVO.MenuVO> allMenus = AppStore.getMenus();
        if (StrUtil.isBlank(keyword)) {
            updateTreeTableRoot(allMenus);
            return;
        }

        // 递归过滤菜单
        List<AuthPermissionInfoRespVO.MenuVO> filtered = filterMenuItems(allMenus, keyword.toLowerCase());
        updateTreeTableRoot(filtered);
        navBarTreeTable.expandAll();
    }

    private List<AuthPermissionInfoRespVO.MenuVO> filterMenuItems(List<AuthPermissionInfoRespVO.MenuVO> nodes, String query) {
        List<AuthPermissionInfoRespVO.MenuVO> result = new ArrayList<>();
        if (nodes == null) return result;

        for (AuthPermissionInfoRespVO.MenuVO node : nodes) {
            List<AuthPermissionInfoRespVO.MenuVO> filteredChildren = filterMenuItems(node.getChildren(), query);

            // 匹配规则：名称包含、拼音包含、或子项有匹配
            boolean matches = node.getName().toLowerCase().contains(query)
                    || PinyinUtil.getPinyin(node.getName(), "").toLowerCase().contains(query);

            if (matches || !filteredChildren.isEmpty()) {
                AuthPermissionInfoRespVO.MenuVO newNode = new AuthPermissionInfoRespVO.MenuVO();
                newNode.setName(node.getName());
                newNode.setIcon(node.getIcon());
                newNode.setComponentSwing(node.getComponentSwing());
                newNode.setChildren(filteredChildren);
                result.add(newNode);
            }
        }
        return result;
    }

    /**
     * 切换导航栏展开/折叠状态
     */
    private void toggleNavBarState(boolean isExpanded) {
        CardLayout cl = (CardLayout) navBarPane.getLayout();
        if (isExpanded) {
            navBarPane.setPreferredSize(new Dimension(navBarExpandedWidth, navBarPane.getHeight()));
            cl.show(navBarPane, "ExpandedMenu");
        } else {
            navBarPane.setPreferredSize(new Dimension(60, navBarPane.getHeight()));
            cl.show(navBarPane, "CollapsedMenu");
        }
        revalidate();
        repaint();
    }

    /**
     * 刷新菜单数据 (异步获取)
     */
    public void updateNavBarData() {
        CompletableFuture.supplyAsync(() -> Forest.client(AuthApi.class).getPermissionInfo().getCheckedData())
                .thenAccept(result -> SwingUtilities.invokeLater(() -> {
                    AppStore.setAuthPermissionInfoRespVO(result);
                    updateTreeTableRoot(AppStore.getMenus());
                }))
                .exceptionally(ex -> {
                    SwingUtilities.invokeLater(() -> SwingExceptionHandler.handle(ex));
                    return null;
                });
    }

    /**
     * 更新树表显示的数据源
     */
    public void updateTreeTableRoot(List<AuthPermissionInfoRespVO.MenuVO> menuList) {
        AuthPermissionInfoRespVO.MenuVO root = new AuthPermissionInfoRespVO.MenuVO();
        root.setName("root");
        root.setChildren(menuList);
        navBarTreeTable.setTreeTableModel(new MenuModel(root));

        // 同时刷新折叠后的图标面板
        initCollapsedIconPanel();
        collapsedIconPanel.revalidate();
        collapsedIconPanel.repaint();
    }

    /**
     * 打开新标签页逻辑
     */
    public void addTab(AuthPermissionInfoRespVO.MenuVO menuVO) {
        int index = tabbedPane.indexOfTab(menuVO.getName());
        if (index == -1) {
            tabbedPane.addTab(menuVO.getName(), getMenuIcon(menuVO.getIcon(), 20),
                    AppStore.getNavigatonPanel(menuVO.getComponentSwing()));
            index = tabbedPane.getTabCount() - 1;
        }
        tabbedPane.setSelectedIndex(index);
    }

    // ===================================================================================
    // 内部组件与监听
    // ===================================================================================

    private void initListeners() {
        // 树表点击监听
        navBarTreeTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = navBarTreeTable.rowAtPoint(e.getPoint());
                if (row < 0) return;
                Object obj = navBarTreeTable.getPathForRow(row).getLastPathComponent();
                if (obj instanceof AuthPermissionInfoRespVO.MenuVO) {
                    AuthPermissionInfoRespVO.MenuVO menu = (AuthPermissionInfoRespVO.MenuVO) obj;
                    if (StrUtil.isNotBlank(menu.getComponentSwing())) addTab(menu);
                }
            }
        });

        // 标签页右键菜单
        tabbedPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) showPopupMenu(e);
            }
        });
    }

    /**
     * 初始化侧边栏折叠后的纯图标列
     */
    private void initCollapsedIconPanel() {
        if (collapsedIconPanel == null) {
            collapsedIconPanel = new JPanel();
            collapsedIconPanel.setOpaque(false);
            collapsedIconPanel.setLayout(new BoxLayout(collapsedIconPanel, BoxLayout.Y_AXIS));
            collapsedIconPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 10));
        } else {
            collapsedIconPanel.removeAll();
        }

        List<AuthPermissionInfoRespVO.MenuVO> menus = AppStore.getMenus();
        if (menus != null) {
            for (AuthPermissionInfoRespVO.MenuVO menu : menus) {
                JButton btn = createIconMenuButton(menu);
                collapsedIconPanel.add(btn);
                collapsedIconPanel.add(Box.createVerticalStrut(5));
            }
        }
    }

    private JButton createIconMenuButton(AuthPermissionInfoRespVO.MenuVO menu) {
        JButton btn = new JButton(getMenuIcon(menu.getIcon(), 25));
        btn.setToolTipText(menu.getName());
        btn.putClientProperty("JButton.buttonType", "toolBarButton");
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setPreferredSize(new Dimension(50, 45));
        btn.setMaximumSize(new Dimension(50, 45));

        // 折叠状态下，悬停显示子菜单
        btn.addMouseListener(new MouseAdapter() {
            private JPopupMenu popup;
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!menuToggleBut.isSelected() && menu.getChildren() != null && !menu.getChildren().isEmpty()) {
                    if (popup == null) popup = createFloatingMenu(menu.getChildren());
                    popup.show(btn, btn.getWidth(), 0);
                }
            }
        });

        if (StrUtil.isNotBlank(menu.getComponentSwing())) {
            btn.addActionListener(e -> addTab(menu));
        }
        return btn;
    }

    public JideTabbedPane getTabbedPane() {
        return tabbedPane;
    }

    private JPopupMenu createFloatingMenu(List<AuthPermissionInfoRespVO.MenuVO> children) {
        JPopupMenu popup = new JPopupMenu();
        for (AuthPermissionInfoRespVO.MenuVO menu : children) {
            if (menu.getChildren() != null && !menu.getChildren().isEmpty()) {
                JMenu sub = new JMenu(menu.getName());
                sub.setIcon(getMenuIcon(menu.getIcon(), 20));
                JPopupMenu subPopup = createFloatingMenu(menu.getChildren());
                for (Component c : subPopup.getComponents()) sub.add(c);
                popup.add(sub);
            } else {
                JMenuItem item = new JMenuItem(menu.getName(), getMenuIcon(menu.getIcon(), 20));
                item.addActionListener(e -> addTab(menu));
                popup.add(item);
            }
        }
        return popup;
    }

    private JToolBar createTabLeadingBar() {
        JToolBar bar = new JToolBar() {
            @Override public Dimension getPreferredSize() { return new Dimension(super.getPreferredSize().width, 50); }
        };
        bar.setOpaque(false);
        menuToggleBut = new JToggleButton(new FlatSVGIcon("icons/bars.svg", 25, 25));
        menuToggleBut.setSelected(true);
        menuToggleBut.addItemListener(e -> toggleNavBarState(menuToggleBut.isSelected()));

        JButton refreshBut = createToolbarButton("icons/refresh.svg", e -> EventBusCenter.get().post(new RefrestEvent()));

        bar.add(menuToggleBut);
        bar.add(refreshBut);
        return bar;
    }

    private void showPopupMenu(MouseEvent e) {
        int index = tabbedPane.getUI().tabForCoordinate(tabbedPane, e.getX(), e.getY());
        if (index == -1) return;

        JPopupMenu menu = new JPopupMenu();
        menu.add(createMenuItem("关闭当前", () -> { if (tabbedPane.isTabClosableAt(index)) tabbedPane.removeTabAt(index); }));
        menu.addSeparator();
        menu.add(createMenuItem("关闭其它", () -> {
            String title = tabbedPane.getTitleAt(index);
            for (int i = tabbedPane.getTabCount() - 1; i >= 0; i--) {
                if (tabbedPane.isTabClosableAt(i) && !tabbedPane.getTitleAt(i).equals(title)) tabbedPane.removeTabAt(i);
            }
        }));
        menu.add(createMenuItem("关闭所有", () -> {
            for (int i = tabbedPane.getTabCount() - 1; i >= 0; i--) {
                if (tabbedPane.isTabClosableAt(i)) tabbedPane.removeTabAt(i);
            }
        }));
        menu.show(e.getComponent(), e.getX(), e.getY());
    }

    // ===================================================================================
    // 通用工具方法
    // ===================================================================================

    private JButton createToolbarButton(String iconPath, java.awt.event.ActionListener listener) {
        JButton btn = new JButton(new FlatSVGIcon(iconPath, 22, 22));
        btn.putClientProperty("JButton.buttonType", "toolBarButton");
        btn.addActionListener(listener);
        return btn;
    }

    private JMenuItem createMenuItem(String text, Runnable action) {
        JMenuItem item = new JMenuItem(text);
        item.addActionListener(e -> action.run());
        return item;
    }

    private FlatSVGIcon getMenuIcon(String iconPath, int size) {
        String path = "icons/item.svg";
        if (StrUtil.isNotBlank(iconPath)) {
            path = iconPath.contains(":") ? "icons/menu/" + iconPath.split(":")[1] + ".svg" : iconPath;
        }
        return IconLoader.getSvgIcon(path, size, size);
    }

    private void updateTabStyles() {
        int selected = tabbedPane.getSelectedIndex();
        Color selCol = UIManager.getColor("TabbedPane.selectedForeground");
        Color defCol = UIManager.getColor("Label.foreground");
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            tabbedPane.setForegroundAt(i, (i == selected) ? selCol : defCol);
        }
    }

    // ===================================================================================
    // EventBus 订阅处理
    // ===================================================================================

    @Subscribe
    private void onMenuRefresh(MenuRefrestEvent event) {
        updateNavBarData();
    }

    @Subscribe
    private void onAddTab(AddMainTabEvent event) {
        SwingUtilities.invokeLater(() -> {
            AuthPermissionInfoRespVO.MenuVO vo = new AuthPermissionInfoRespVO.MenuVO();
            vo.setName(event.getTabName());
            vo.setIcon(event.getIcon());
            vo.setComponentSwing(event.getTabContent().getClass().getName());
            addTab(vo);
        });
    }

    /**
     * TreeTable 内部模型类
     */
    class MenuModel extends AbstractTreeTableModel {
        public MenuModel(Object root) { super(root); }
        @Override public int getColumnCount() { return 1; }
        @Override public String getColumnName(int column) { return "导航菜单"; }
        @Override public Object getValueAt(Object node, int column) {
            return (node instanceof AuthPermissionInfoRespVO.MenuVO) ? ((AuthPermissionInfoRespVO.MenuVO) node).getName() : null;
        }
        @Override public Object getChild(Object parent, int index) {
            List<AuthPermissionInfoRespVO.MenuVO> children = ((AuthPermissionInfoRespVO.MenuVO) parent).getChildren();
            return (children != null) ? children.get(index) : null;
        }
        @Override public int getChildCount(Object parent) {
            List<AuthPermissionInfoRespVO.MenuVO> children = ((AuthPermissionInfoRespVO.MenuVO) parent).getChildren();
            return (children != null) ? children.size() : 0;
        }
        @Override public int getIndexOfChild(Object parent, Object child) {
            List<AuthPermissionInfoRespVO.MenuVO> children = ((AuthPermissionInfoRespVO.MenuVO) parent).getChildren();
            return (children != null) ? children.indexOf(child) : -1;
        }
        @Override public boolean isLeaf(Object node) { return getChildCount(node) == 0; }
    }

    @Override
    public void removeNotify() {
        EventBusCenter.get().unregister(this);
        super.removeNotify();
    }
}
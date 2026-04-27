package com.dillon.lw.view.mainpane;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.pinyin.PinyinUtil;
import com.dillon.lw.api.system.AuthApi;
import com.dillon.lw.components.ToolDescriptor;
import com.dillon.lw.components.WPanel;
import com.dillon.lw.components.WToolTabbedPane;
import com.dillon.lw.eventbus.EventBusCenter;
import com.dillon.lw.eventbus.event.AddMainTabEvent;
import com.dillon.lw.eventbus.event.MenuRefreshEvent;
import com.dillon.lw.eventbus.event.RefreshDataEvent;
import com.dillon.lw.exception.SwingExceptionHandler;
import com.dillon.lw.module.system.controller.admin.auth.vo.AuthPermissionInfoRespVO;
import com.dillon.lw.store.AppStore;
import com.dillon.lw.utils.ColorUtils;
import com.dillon.lw.utils.IconLoader;
import com.dillon.lw.view.home.HomePanel;
import com.dillon.lw.view.system.menu.MenuManagementPanel;
import com.dtflys.forest.Forest;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.extras.components.FlatTabbedPane;
import com.google.common.eventbus.Subscribe;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.treetable.AbstractTreeTableModel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.JTableHeader;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import com.dillon.lw.swing.rx.SwingSchedulers;

/**
 * 主应用界面面板 (MainPane)
 * 采用 BorderLayout 布局：
 * - 西部 (WEST)：侧边导航栏（支持展开/折叠）
 * - 中部 (CENTER)：多标签页容器 (JideTabbedPane)
 * - 南部 (SOUTH)：状态栏
 */
public class MainPane extends JPanel {
    private static final String CARD_EXPANDED_MENU = "ExpandedMenu";
    private static final String CARD_COLLAPSED_MENU = "CollapsedMenu";
    private static final String HOME_TOOL_ID = "home";
    private static final int NAV_BAR_COLLAPSED_WIDTH = 60;
    private static final int MENU_ICON_SIZE = 20;
    private static final int COLLAPSED_MENU_ICON_SIZE = 25;
    private static final int TOOLBAR_ICON_SIZE = 22;
    private static final DateTimeFormatter STATUS_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // --- 核心 UI 组件 ---
    private JPanel navBarPane;            // 侧边栏容器 (CardLayout)
    private JXTreeTable navBarTreeTable;  // 导航树表
    private WToolTabbedPane toolTabbedPane;    // 标签页
    private JToolBar statusPane;          // 底部状态栏
    private JButton menuToggleBut;  // 菜单切换按钮
    private JPanel collapsedIconPanel;    // 折叠后的图标面板

    // 状态栏组件
    private JLabel timeLabel;
    private JLabel copyrightLabel;
    private Timer timeTimer;
    // --- 配置属性 ---
    private int navBarExpandedWidth = 300;

    private boolean isExpanded = true;
    private boolean syncingTreeSelection;

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
        setLayout(new BorderLayout(7, 0));
        setBorder(BorderFactory.createEmptyBorder(0, 7, 0, 7));

        // 1. 初始化左侧导航容器 (使用 CardLayout 实现展开/收起切换)
        navBarPane = new WPanel();
        navBarPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
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
        treeScroll.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
        treeScroll.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));

        expandedPanel.add(setupSearchBar(), BorderLayout.NORTH);
        expandedPanel.add(treeScroll, BorderLayout.CENTER);

        // --- 折叠状态面板 ---
        initCollapsedIconPanel();

        navBarPane.add(expandedPanel, CARD_EXPANDED_MENU);
        navBarPane.add(collapsedIconPanel, CARD_COLLAPSED_MENU);
        navBarExpandedWidth = navBarPane.getPreferredSize().width;

        // 2. 初始化中央标签页
        setupTabbedPane();

        // 3. 底部状态栏
        statusPane = new JToolBar();
        statusPane.setFloatable(false);
        statusPane.setOpaque(false);
        setupStatusPane();

        // 组装主框架
        add(navBarPane, BorderLayout.WEST);
        add(createWorkspaceSurface(), BorderLayout.CENTER);
        add(statusPane, BorderLayout.SOUTH);

        // 默认显示展开状态
        ((CardLayout) navBarPane.getLayout()).show(navBarPane, CARD_EXPANDED_MENU);
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
        searchField.getDocument().addDocumentListener(createDocumentListener(() ->
                applyTreeFilter(searchField.getText().trim())));

        toolBar.add(searchField);
        toolBar.add(createToolbarButton("icons/expand-up-down-line.svg", "展开菜单", e -> navBarTreeTable.expandAll()));
        toolBar.add(createToolbarButton("icons/contract-up-down-line.svg", "折叠菜单", e -> navBarTreeTable.collapseAll()));
        return toolBar;
    }

    /**
     * 配置导航树表样式
     */
    private void setupTreeTable() {
        navBarTreeTable = new JXTreeTable();
        navBarTreeTable.setRowHeight(45);
        applyTreeHoverHighlighter();

        // 隐藏表格头
        navBarTreeTable.setTableHeader(createZeroHeightTableHeader());

        // 自定义树节点渲染
        navBarTreeTable.setTreeCellRenderer(new DefaultTreeCellRenderer() {
            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                JLabel label = (JLabel) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
                if (value instanceof AuthPermissionInfoRespVO.MenuVO) {
                    AuthPermissionInfoRespVO.MenuVO menu = (AuthPermissionInfoRespVO.MenuVO) value;
                    label.setText(menu.getName());
                    label.setIcon(getMenuIcon(menu.getIcon(), MENU_ICON_SIZE));
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
        toolTabbedPane = new WToolTabbedPane();
        toolTabbedPane.setLeadingComponent(createTabLeadingBar());
        toolTabbedPane.setTrailingComponent(new JToolBar() {{
            setOpaque(false);
        }});
        toolTabbedPane.setSelectionListener(descriptor ->
                syncTreeSelectionToToolId(descriptor == null ? null : descriptor.getId()));
        toolTabbedPane.openTool(buildHomeToolDescriptor());
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
            String menuName = node.getName() == null ? "" : node.getName();

            // 匹配规则：名称包含、拼音包含、或子项有匹配
            boolean matches = menuName.toLowerCase().contains(query)
                    || PinyinUtil.getPinyin(menuName, "").toLowerCase().contains(query)
                    || PinyinUtil.getFirstLetter(menuName, "").toLowerCase().contains(query);

            if (matches || !filteredChildren.isEmpty()) {
                AuthPermissionInfoRespVO.MenuVO newNode = copyMenuNode(node);
                newNode.setChildren(filteredChildren);
                result.add(newNode);
            }
        }
        return result;
    }

    /**
     * 切换导航栏展开/折叠状态
     */
    private void toggleNavBarState() {
        CardLayout cl = (CardLayout) navBarPane.getLayout();
        isExpanded = !isExpanded;
        if (isExpanded) {
            navBarPane.setPreferredSize(new Dimension(navBarExpandedWidth, navBarPane.getHeight()));
            cl.show(navBarPane, CARD_EXPANDED_MENU);
        } else {
            navBarPane.setPreferredSize(new Dimension(NAV_BAR_COLLAPSED_WIDTH, navBarPane.getHeight()));
            cl.show(navBarPane, CARD_COLLAPSED_MENU);
        }
        revalidate();
        repaint();
    }

    /**
     * 刷新菜单数据 (异步获取)
     */
    public void updateNavBarData() {
        Single
                /*
                 * 权限信息接口仍然是同步调用，这里用 fromCallable 包装成懒执行任务，
                 * 让真正的网络请求在订阅时才发生，并切到 IO 线程执行。
                 */
                .fromCallable(() -> Forest.client(AuthApi.class).getPermissionInfo().getCheckedData())
                .subscribeOn(Schedulers.io())
                /*
                 * refreshMenuTree 会更新树表和折叠菜单，属于标准 Swing UI 操作，
                 * 因此在进入消费逻辑前统一切回 EDT，避免后台线程直接碰组件。
                 */
                .observeOn(SwingSchedulers.edt())
                .subscribe(this::refreshMenuTree, SwingExceptionHandler::handle);
    }

    private void refreshMenuTree(AuthPermissionInfoRespVO authPermissionInfo) {
        AppStore.setAuthPermissionInfoRespVO(authPermissionInfo);
        updateTreeTableRoot(AppStore.getMenus());
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
        syncTreeSelectionToToolId(toolTabbedPane.getSelectedToolId());
    }

    /**
     * 打开新标签页逻辑
     */
    public void addTab(AuthPermissionInfoRespVO.MenuVO menuVO) {
        if (menuVO == null || StrUtil.isBlank(menuVO.getComponentSwing())) {
            return;
        }
        toolTabbedPane.openTool(buildMenuToolDescriptor(menuVO));
    }

    // ===================================================================================
    // 内部组件与监听
    // ===================================================================================

    private void initListeners() {
        navBarTreeTable.addTreeSelectionListener(e -> {
            if (syncingTreeSelection || e.getNewLeadSelectionPath() == null) {
                return;
            }
            Object obj = e.getNewLeadSelectionPath().getLastPathComponent();
            if (obj instanceof AuthPermissionInfoRespVO.MenuVO) {
                AuthPermissionInfoRespVO.MenuVO menu = (AuthPermissionInfoRespVO.MenuVO) obj;
                if (StrUtil.isNotBlank(menu.getComponentSwing())) {
                    addTab(menu);
                }
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
            collapsedIconPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
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

    /**
     * 配置状态栏内部组件
     */
    private void setupStatusPane() {
        // A. 服务器地址 (从配置或 Store 获取)
        copyrightLabel = new JLabel("© liwen");

        JLabel versionLabel = new JLabel("V" + System.getProperty("app.version"));

        // B. 服务器时间
        timeLabel = new JLabel();
        timeLabel.setIcon(new FlatSVGIcon("icons/shijian.svg", 16, 16));
        // 放入状态栏
        statusPane.add(new JButton("127.0.0.1:8080", new FlatSVGIcon("icons/server.svg", 16, 16)));
        statusPane.add(Box.createHorizontalGlue());
        statusPane.add(timeLabel);
        statusPane.add(Box.createHorizontalStrut(10));
        statusPane.add(versionLabel);
        statusPane.add(Box.createHorizontalStrut(10));
        statusPane.add(copyrightLabel);
        initTimeTask();
    }

    private JButton createIconMenuButton(AuthPermissionInfoRespVO.MenuVO menu) {
        JButton btn = new JButton(getMenuIcon(menu.getIcon(), COLLAPSED_MENU_ICON_SIZE));
        btn.setToolTipText(menu.getName());
        btn.putClientProperty("JButton.buttonType", "toolBarButton");
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setPreferredSize(new Dimension(50, 45));
        btn.setMaximumSize(new Dimension(50, 45));

        // 折叠状态下，悬停显示子菜单
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!menuToggleBut.isSelected() && menu.getChildren() != null && !menu.getChildren().isEmpty()) {
                    JPopupMenu popup = createFloatingMenu(menu.getChildren());
                    popup.show(btn, btn.getWidth(), 0);
                }
            }
        });

        if (StrUtil.isNotBlank(menu.getComponentSwing())) {
            btn.addActionListener(e -> addTab(menu));
        }
        return btn;
    }

    public FlatTabbedPane getTabbedPane() {
        return toolTabbedPane.getTabbedPane();
    }

    private JPopupMenu createFloatingMenu(List<AuthPermissionInfoRespVO.MenuVO> children) {
        JPopupMenu popup = new JPopupMenu();
        for (AuthPermissionInfoRespVO.MenuVO menu : children) {
            if (menu.getChildren() != null && !menu.getChildren().isEmpty()) {
                JMenu sub = new JMenu(menu.getName());
                sub.setIcon(getMenuIcon(menu.getIcon(), MENU_ICON_SIZE));
                JPopupMenu subPopup = createFloatingMenu(menu.getChildren());
                for (Component c : subPopup.getComponents()) sub.add(c);
                popup.add(sub);
            } else {
                JMenuItem item = new JMenuItem(menu.getName(), getMenuIcon(menu.getIcon(), MENU_ICON_SIZE));
                item.addActionListener(e -> addTab(menu));
                popup.add(item);
            }
        }
        return popup;
    }

    private JToolBar createTabLeadingBar() {
        JToolBar bar = new JToolBar() {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(super.getPreferredSize().width, 50);
            }
        };
        bar.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
        bar.setOpaque(false);
        menuToggleBut = createToolbarButton("icons/bars.svg", "折叠/展开菜单栏", e -> toggleNavBarState());

        JButton refreshBut = createToolbarButton("icons/refresh.svg", "刷新", e -> EventBusCenter.get().post(new RefreshDataEvent()));

        bar.add(menuToggleBut);
        bar.add(refreshBut);

        return bar;
    }

    private void initTimeTask() {
        if (timeTimer == null) {
            // 创建定时器，每 1000 毫秒（1秒）触发一次
            timeTimer = new Timer(1000, e -> {
                // 更新标签文本
                timeLabel.setText(" " + LocalDateTime.now().format(STATUS_TIME_FORMATTER));
            });
        }

        timeTimer.start();
    }

    public void closeAllTab() {
        toolTabbedPane.closeAllTabs();
    }

    // ===================================================================================
    // 通用工具方法
    // ===================================================================================

    private JButton createToolbarButton(String iconPath, String toolTip, java.awt.event.ActionListener listener) {
        JButton btn = new JButton(IconLoader.getSvgIcon(iconPath, TOOLBAR_ICON_SIZE, TOOLBAR_ICON_SIZE));
        btn.putClientProperty("JButton.buttonType", "toolBarButton");
        btn.addActionListener(listener);
        btn.setToolTipText(toolTip);
        return btn;
    }

    private DocumentListener createDocumentListener(Runnable action) {
        return new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                action.run();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                action.run();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                action.run();
            }
        };
    }

    private JTableHeader createZeroHeightTableHeader() {
        return new JTableHeader() {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(super.getPreferredSize().width, 0);
            }
        };
    }

    private void applyTreeHoverHighlighter() {
        if (navBarTreeTable == null) {
            return;
        }
        navBarTreeTable.setHighlighters(new ColorHighlighter(
                HighlightPredicate.ROLLOVER_ROW,
                UIManager.getColor("App.hoverBackground"),
                null
        ));
    }

    private AuthPermissionInfoRespVO.MenuVO copyMenuNode(AuthPermissionInfoRespVO.MenuVO source) {
        AuthPermissionInfoRespVO.MenuVO copy = new AuthPermissionInfoRespVO.MenuVO();
        copy.setId(source.getId());
        copy.setParentId(source.getParentId());
        copy.setName(source.getName());
        copy.setIcon(source.getIcon());
        copy.setComponentSwing(source.getComponentSwing());
        return copy;
    }

    private FlatSVGIcon getMenuIcon(String iconPath, int size) {
        String path = "icons/item.svg";
        if (StrUtil.isNotBlank(iconPath)) {
            path = iconPath.contains(":") ? "icons/menu/" + iconPath.split(":")[1] + ".svg" : iconPath;
        }
        return IconLoader.getSvgIcon(path, size, size);
    }

    // ===================================================================================
    // EventBus 订阅处理
    // ===================================================================================

    @Subscribe
    private void onMenuRefresh(MenuRefreshEvent event) {
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

    @Override
    public void updateUI() {
        super.updateUI();
        applyTreeHoverHighlighter();
    }

    /**
     * TreeTable 内部模型类
     */
    private static class MenuModel extends AbstractTreeTableModel {
        public MenuModel(Object root) {
            super(root);
        }

        @Override
        public int getColumnCount() {
            return 1;
        }

        @Override
        public String getColumnName(int column) {
            return "导航菜单";
        }

        @Override
        public Object getValueAt(Object node, int column) {
            return (node instanceof AuthPermissionInfoRespVO.MenuVO) ? ((AuthPermissionInfoRespVO.MenuVO) node).getName() : null;
        }

        @Override
        public Object getChild(Object parent, int index) {
            List<AuthPermissionInfoRespVO.MenuVO> children = ((AuthPermissionInfoRespVO.MenuVO) parent).getChildren();
            return (children != null) ? children.get(index) : null;
        }

        @Override
        public int getChildCount(Object parent) {
            List<AuthPermissionInfoRespVO.MenuVO> children = ((AuthPermissionInfoRespVO.MenuVO) parent).getChildren();
            return (children != null) ? children.size() : 0;
        }

        @Override
        public int getIndexOfChild(Object parent, Object child) {
            List<AuthPermissionInfoRespVO.MenuVO> children = ((AuthPermissionInfoRespVO.MenuVO) parent).getChildren();
            return (children != null) ? children.indexOf(child) : -1;
        }

        @Override
        public boolean isLeaf(Object node) {
            return getChildCount(node) == 0;
        }
    }

    @Override
    public void removeNotify() {
        EventBusCenter.get().unregister(this);
        if (timeTimer != null && timeTimer.isRunning()) {
            timeTimer.stop(); // 停止定时器
        }
        super.removeNotify();
    }

    private ToolDescriptor buildHomeToolDescriptor() {
        return new ToolDescriptor(
                HOME_TOOL_ID,
                "主页",
                "系统",
                "主页",
                IconLoader.getSvgIcon("icons/menu/zhuye.svg", MENU_ICON_SIZE, MENU_ICON_SIZE),
                false,
                false,
                () -> (JComponent) AppStore.getNavigationPanel(HomePanel.class.getName())
        );
    }

    private ToolDescriptor buildMenuToolDescriptor(AuthPermissionInfoRespVO.MenuVO menuVO) {
        String componentClass = StrUtil.equals(menuVO.getName(), "菜单管理")
                ? MenuManagementPanel.class.getName()
                : menuVO.getComponentSwing();
        return new ToolDescriptor(
                buildToolId(menuVO),
                menuVO.getName(),
                "菜单",
                menuVO.getName(),
                getMenuIcon(menuVO.getIcon(), MENU_ICON_SIZE),
                false,
                true,
                () -> (JComponent) AppStore.getNavigationPanel(componentClass)
        );
    }

    private String buildToolId(AuthPermissionInfoRespVO.MenuVO menuVO) {
        if (menuVO == null) {
            return null;
        }
        if (menuVO.getId() != null) {
            return "menu:" + menuVO.getId();
        }
        if (StrUtil.isNotBlank(menuVO.getComponentSwing())) {
            return "menu:" + menuVO.getComponentSwing();
        }
        return "menu:" + menuVO.getName();
    }

    private void syncTreeSelectionToToolId(String toolId) {
        syncingTreeSelection = true;
        try {
            if (StrUtil.isBlank(toolId) || StrUtil.equals(toolId, HOME_TOOL_ID)) {
                navBarTreeTable.clearSelection();
                return;
            }
            TreePath path = findTreePathForToolId(toolId);
            if (path == null) {
                navBarTreeTable.clearSelection();
                return;
            }
            navBarTreeTable.expandPath(path);
            navBarTreeTable.scrollPathToVisible(path);
            navBarTreeTable.getTreeSelectionModel().setSelectionPath(path);
            int row = navBarTreeTable.getRowForPath(path);
            if (row >= 0) {
                navBarTreeTable.setRowSelectionInterval(row, row);
            }
        } finally {
            syncingTreeSelection = false;
        }
    }

    private TreePath findTreePathForToolId(String toolId) {
        if (StrUtil.isBlank(toolId) || navBarTreeTable == null || navBarTreeTable.getTreeTableModel() == null) {
            return null;
        }
        Object root = navBarTreeTable.getTreeTableModel().getRoot();
        if (!(root instanceof AuthPermissionInfoRespVO.MenuVO)) {
            return null;
        }
        return findTreePath((AuthPermissionInfoRespVO.MenuVO) root, new TreePath(root), toolId);
    }

    private TreePath findTreePath(AuthPermissionInfoRespVO.MenuVO current, TreePath currentPath, String toolId) {
        if (StrUtil.equals(toolId, buildToolId(current))) {
            return currentPath;
        }
        List<AuthPermissionInfoRespVO.MenuVO> children = current.getChildren();
        if (children == null) {
            return null;
        }
        for (AuthPermissionInfoRespVO.MenuVO child : children) {
            TreePath childPath = findTreePath(child, currentPath.pathByAddingChild(child), toolId);
            if (childPath != null) {
                return childPath;
            }
        }
        return null;
    }


    private JPanel createWorkspaceSurface() {
        RoundedPanel workspaceSurface = new RoundedPanel();
        workspaceSurface.setLayout(new BorderLayout());
        workspaceSurface.setBorder(new EmptyBorder(0, 12, 10, 12));

        workspaceSurface.add(toolTabbedPane, BorderLayout.CENTER);
        return workspaceSurface;
    }

    private static class RoundedPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color panelBackground = UIManager.getColor("Panel.background");
                if (panelBackground == null) {
                    panelBackground = getBackground();
                }
                g2.setColor(panelBackground);
                int arc = UIManager.getInt("App.arc");
                if (arc <= 0) {
                    arc = 21;
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);

                g2.setColor(UIManager.getColor("App.mainTabbedPaneBackground"));
                g2.fillRoundRect(0, 50, getWidth(), Math.max(0, getHeight()- 50 ), arc, arc);
                g2.fillRect(0, 50, getWidth(), 50);
//                g2.fillRect(0, 25, getWidth(), arc);
            } finally {
                g2.dispose();
            }
            super.paintComponent(g);
        }

        @Override
        public boolean isOpaque() {
            return false;
        }
    }
}

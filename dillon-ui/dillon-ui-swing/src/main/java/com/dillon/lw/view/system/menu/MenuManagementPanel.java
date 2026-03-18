package com.dillon.lw.view.system.menu;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNodeConfig;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.dillon.lw.api.system.MenuApi;
import com.dillon.lw.components.AbstractRefreshablePanel;
import com.dillon.lw.components.WButton;
import com.dillon.lw.components.WFormDialog;
import com.dillon.lw.components.WPanel;
import com.dillon.lw.components.WaitPane;
import com.dillon.lw.components.notice.WMessage;
import com.dillon.lw.components.table.renderer.OptButtonTableCellEditor;
import com.dillon.lw.components.table.renderer.OptButtonTableCellRenderer;
import com.dillon.lw.eventbus.EventBusCenter;
import com.dillon.lw.eventbus.event.MenuRefreshEvent;
import com.dillon.lw.exception.SwingExceptionHandler;
import com.dillon.lw.module.system.controller.admin.permission.vo.menu.MenuListReqVO;
import com.dillon.lw.module.system.controller.admin.permission.vo.menu.MenuRespVO;
import com.dillon.lw.module.system.controller.admin.permission.vo.menu.MenuSaveVO;
import com.dillon.lw.swing.rx.SwingSchedulers;
import com.dillon.lw.utils.IconLoader;
import com.dillon.lw.view.frame.MainFrame;
import com.dtflys.forest.Forest;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.treetable.AbstractTreeTableModel;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static javax.swing.JOptionPane.*;

/**
 * 菜单管理面板
 *
 * @author liwen
 * @date 2022/07/17
 */
public class MenuManagementPanel extends AbstractRefreshablePanel {
    private static final String[] COLUMN_NAMES = {"菜单名称", "图标", "排序", "权限标识", "组件路径", "组件名称", "状态", "操作"};
    private static final String COL_ACTION = "操作";
    private static final String COL_ICON = "图标";
    private static final String COL_SORT = "排序";
    private static final String COL_COMPONENT = "组件路径";

    private JXTreeTable treeTable;
    private JTextField nameTextField;
    private JComboBox statusCombo;
    private JToggleButton exButton;
    private JButton searchButton;

    private WaitPane waitPane;
    /**
     * 菜单列表加载请求版本号。
     * <p>
     * 即使旧订阅被 dispose，底层 HTTP 调用也未必立刻中断。
     * 因此这里额外保留版本号，确保“后发起的请求”永远只接受自己的返回结果。
     * </p>
     */
    private final AtomicInteger loadRequestVersion = new AtomicInteger();
    /**
     * 当前列表加载订阅。
     */
    private final AtomicReference<Disposable> currentLoadDisposable = new AtomicReference<Disposable>();
    /**
     * 当前删除操作订阅。
     */
    private final AtomicReference<Disposable> currentDeleteDisposable = new AtomicReference<Disposable>();

    public MenuManagementPanel() {

        initComponents();
        updateData();
    }

    @Override
    protected void doRefresh() {
        updateData();
    }

    private void initComponents() {

        JPanel toolBar = new WPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        toolBar.add(new JLabel("菜单名称"));
        toolBar.add(nameTextField = createTextField("请输入菜单名称"));

        nameTextField.setColumns(20);
        toolBar.add(new JLabel("状态"));
        toolBar.add(statusCombo = new JComboBox(new String[]{"全部", "正常", "停用"}));


        JButton restButton = new WButton("重置");
        restButton.setBackground(new Color(0xf35857));
        toolBar.add(restButton);
        restButton.addActionListener(e -> {
            statusCombo.setSelectedIndex(0);
        });
        searchButton = new WButton("搜索");
        searchButton.setBackground(new Color(0xff9c4b));
        searchButton.addActionListener(e -> updateData());
        toolBar.add(searchButton);

        JButton addButton = new WButton("新增");
        toolBar.add(addButton);
        addButton.addActionListener(e -> {
            showMenuAddDialog(null);
        });
        addButton.setBackground(new Color(0x1c7dfa));

        exButton = new JToggleButton("展开/折叠");
        toolBar.add(exButton);
        exButton.addActionListener(e -> {
            if (exButton.isSelected()) {
                treeTable.expandAll();
            } else {
                treeTable.collapseAll();
            }
        });


        treeTable = new JXTreeTable(new MenuTreeTableModel(null));
        treeTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        treeTable.setRowHeight(50);
        treeTable.setLeafIcon(null);
        treeTable.setClosedIcon(null);
        treeTable.setOpenIcon(null);
        treeTable.setShowHorizontalLines(true);
        treeTable.setShowVerticalLines(false);
        treeTable.setIntercellSpacing(new Dimension(0, 1));
        treeTable.setOpaque(false);
        treeTable.setHighlighters();
        // JXTable
        ColorHighlighter rollover = new ColorHighlighter(HighlightPredicate.ROLLOVER_ROW, UIManager.getColor("App.hoverBackground"), null);
        treeTable.setHighlighters(rollover);
        treeTable.putClientProperty(JXTreeTable.USE_DTCR_COLORMEMORY_HACK, Boolean.FALSE);

        JScrollPane tsp = new JScrollPane(treeTable);
        tsp.setBorder(BorderFactory.createEmptyBorder());

        JPanel cantenPane = new JPanel(new BorderLayout(0, 10));
//        cantenPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        cantenPane.setOpaque(false);
        cantenPane.add(toolBar, BorderLayout.NORTH);
        cantenPane.add(tsp, BorderLayout.CENTER);
        waitPane = new WaitPane(cantenPane);

        this.setLayout(new BorderLayout());
        this.add(waitPane, BorderLayout.CENTER);


    }

    @Override
    public void updateUI() {
        SwingUtilities.invokeLater(() -> {
            if (treeTable != null) {
                ColorHighlighter rollover = new ColorHighlighter(HighlightPredicate.ROLLOVER_ROW, UIManager.getColor("App.hoverBackground"), null);
                treeTable.setHighlighters(rollover);
                treeTable.setIntercellSpacing(new Dimension(0, 1));
            }
        });
        super.updateUI();

    }

    private JToolBar createActionBar() {
        JToolBar optBar = new JToolBar();

        JButton edit = new JButton("修改");
        edit.setForeground(UIManager.getColor("App.accent.color"));
        edit.setIcon(new FlatSVGIcon("icons/xiugai.svg", 15, 15));

        edit.addActionListener(e -> showMenuEditDialog());
        edit.setForeground(UIManager.getColor("App.accent.color"));

        JButton del = new JButton("删除");
        del.setIcon(new FlatSVGIcon("icons/delte.svg", 15, 15));
        del.addActionListener(e -> delMenu());
        del.setForeground(UIManager.getColor("App.danger.color"));
        JButton add = new JButton("新增");
        add.addActionListener(e -> {
            showMenuAddDialog(getSelectedMenuId());
        });
        add.setForeground(UIManager.getColor("App.accent.color"));
        add.setIcon(new FlatSVGIcon("icons/xinzeng.svg", 15, 15));
        optBar.add(Box.createGlue());
        optBar.add(edit);
        optBar.add(add);
        optBar.add(del);
        optBar.add(Box.createGlue());
        return optBar;

    }


    private void showMenuAddDialog(Long id) {
        MenuEditPane formPane = new MenuEditPane();
        formPane.updateData(id, true);

        WFormDialog<MenuSaveVO> dialog = new WFormDialog<>(
                MainFrame.getInstance(), "添加菜单", formPane);

        dialog.showDialogWithAsyncSubmit(
                formPane::validates,
                formPane::getMenuRespVO,
                data -> {
                    Forest.client(MenuApi.class).createMenu(data).getCheckedData();
                    return true;
                },
                () -> {
                    updateData();
                    postMenuRefreshEvent();
                },
                "添加菜单成功"
        );
    }


    private void showMenuEditDialog() {
        Long menuId = getSelectedMenuId();

        MenuEditPane formPane = new MenuEditPane();
        formPane.updateData(menuId, false);

        WFormDialog<MenuSaveVO> dialog = new WFormDialog<>(
                MainFrame.getInstance(), "修改菜单", formPane);

        dialog.showDialogWithAsyncSubmit(
                formPane::validates,
                formPane::getMenuRespVO,
                data -> {
                    Forest.client(MenuApi.class).updateMenu(data).getCheckedData();
                    return true;
                },
                () -> {
                    updateData();
                    postMenuRefreshEvent();
                },
                "修改菜单成功"
        );
    }

    /**
     * 从筛选条件构建查询参数并刷新树表。
     * <p>
     * 这里统一走 RxJava：
     * 1. 请求在 IO 线程执行；
     * 2. `observeOn(SwingSchedulers.edt())` 后的树表刷新、提示消息都固定回到 EDT；
     * 3. 新请求会主动替换旧订阅，并通过 requestId 屏蔽迟到结果。
     * </p>
     */
    private void updateData() {
        final Map<String, Object> queryMap = createQueryMap();
        final int requestId = loadRequestVersion.incrementAndGet();
        disposeTrackedDisposable(currentLoadDisposable);

        final AtomicReference<Disposable> loadDisposableRef = new AtomicReference<Disposable>();
        Single
                /*
                 * 菜单查询接口是同步调用，这里直接包成 Single，
                 * 让 subscribeOn(io) 负责把它切到后台线程执行。
                 */
                .fromCallable(() -> Forest.client(MenuApi.class).getMenuList(queryMap).getCheckedData())
                .subscribeOn(Schedulers.io())
                .observeOn(SwingSchedulers.edt())
                .doOnSubscribe(disposable -> {
                    loadDisposableRef.set(disposable);
                    currentLoadDisposable.set(disposable);
                    /*
                     * doOnSubscribe 不保证运行在 EDT。
                     * 这里把等待遮罩和搜索按钮置灰都显式投递回 Swing 线程，
                     * 这样用户在查询返回前无法重复点击“搜索”。
                     */
                    SwingSchedulers.runOnEdt(() -> {
                        searchButton.setEnabled(false);
                        showWaitMessage("菜单加载中...");
                    });
                })
                .doFinally(() -> {
                    clearTrackedDisposable(currentLoadDisposable, loadDisposableRef.get());
                    if (requestId == loadRequestVersion.get()) {
                        /*
                         * 只有最后一次仍然有效的查询才允许恢复 UI 状态，
                         * 避免旧请求结束时把新请求的加载态提前清掉。
                         */
                        SwingSchedulers.runOnEdt(() -> {
                            searchButton.setEnabled(true);
                            hideWaitMessage();
                        });
                    }
                })
                .subscribe(
                        data -> {
                            /*
                             * 即使旧订阅被 dispose，底层请求也可能已经发出。
                             * 这里再用 requestId 做一次兜底，保证只有最后一次查询能刷新树表。
                             */
                            if (requestId != loadRequestVersion.get()) {
                                return;
                            }

                            Tree<Long> treeRoot = buildMenuTree(data);
                            updateTreeTableRoot(treeRoot);
                            if (exButton.isSelected()) {
                                treeTable.expandAll();
                            }
                            WMessage.showMessageSuccess(MainFrame.getInstance(), "查询成功！");
                        },
                        SwingExceptionHandler::handle
                );
    }

    private Map<String, Object> createQueryMap() {
        MenuListReqVO reqVO = new MenuListReqVO();
        reqVO.setName(nameTextField.getText());
        int selectIndex = statusCombo.getSelectedIndex();
        if (selectIndex != 0) {
            reqVO.setStatus(selectIndex == 1 ? 0 : 1);
        }
        return BeanUtil.beanToMap(reqVO, false, true);
    }

    private Tree<Long> buildMenuTree(List<MenuRespVO> data) {
        long minParentId = data.stream().mapToLong(value -> value.getParentId()).min().orElse(0L);
        TreeNodeConfig config = new TreeNodeConfig();
        config.setWeightKey("orderNum");
        config.setParentIdKey("parentId");
        config.setNameKey("menuName");
        return TreeUtil.buildSingle(data, minParentId, config, (object, treeNode) -> {
            treeNode.setId(object.getId());
            treeNode.setParentId(object.getParentId());
            treeNode.setName(object.getName());
            treeNode.putExtra("icon", object.getIcon());
            treeNode.putExtra("component", object.getComponentSwing());
            treeNode.putExtra("orderNum", object.getSort());
            treeNode.putExtra("status", object.getStatus());
            treeNode.putExtra("perms", object.getPermission());
            treeNode.putExtra("menuType", object.getType());
            treeNode.putExtra("componentName", object.getComponentName());
        });
    }

    private void updateTreeTableRoot(Object root) {

        treeTable.setTreeTableModel(new MenuTreeTableModel(root));
        treeTable.getColumnExt(COL_ACTION).setMinWidth(240);
        treeTable.getColumnExt(COL_ACTION).setMaxWidth(240);
        treeTable.getColumnExt(COL_ICON).setMinWidth(60);
        treeTable.getColumnExt(COL_ICON).setMaxWidth(60);
        treeTable.getColumnExt(COL_SORT).setWidth(80);
        treeTable.getColumnExt(COL_SORT).setMaxWidth(80);

        treeTable.getColumnExt(COL_COMPONENT).setMinWidth(300);

        treeTable.getColumnExt(COL_ICON).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                JLabel label = new JLabel("", JLabel.CENTER);

                String icon = (String) value;

                if (StrUtil.contains(icon, ":")) {
                    String iconPath = "icons/menu/" + icon.split(":")[1] + ".svg";

                    if (StrUtil.isBlank(iconPath) || IconLoader.class.getResourceAsStream("/" + iconPath) == null) {
                        label.setIcon(null);
                        label.setText("");
                    } else {
                        label.setIcon(new FlatSVGIcon(iconPath, 25, 25));
                        label.setText("");
                    }

                } else {
                    label.setIcon(null);
                    label.setText(String.valueOf(value));
                }

                label.setHorizontalTextPosition(CENTER);
                JPanel panel = new JPanel(new BorderLayout());
                panel.add(label);
                panel.setBackground(component.getBackground());
                return panel;
            }
        });

        treeTable.getColumnExt("状态").setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
                JLabel label = new JLabel(ObjectUtil.equals(value, 0) ? "开启" : "停用");
                label.setForeground(ObjectUtil.equals(value, 0) ? new Color(96, 197, 104) : new Color(0xf56c6c));
                FlatSVGIcon icon = new FlatSVGIcon("icons/yuan.svg", 10, 10);
                icon.setColorFilter(new FlatSVGIcon.ColorFilter(color -> {
                    return label.getForeground();
                }));
                label.setIcon(icon);
                panel.add(label);
                panel.setBackground(component.getBackground());
                return panel;
            }
        });
        treeTable.getColumnExt(COL_ACTION).setCellRenderer(new OptButtonTableCellRenderer(createActionBar()));
        treeTable.getColumnExt(COL_ACTION).setCellEditor(new OptButtonTableCellEditor(createActionBar()));
        treeTable.getColumn(COL_SORT).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                JLabel label = new JLabel(value + "", JLabel.CENTER);
                JPanel panel = new JPanel(new BorderLayout());
                panel.add(label);
                panel.setBackground(component.getBackground());
                return panel;
            }
        });
    }


    private JTextField createTextField(String placeholderText) {
        JTextField textField = new JTextField();
        textField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, placeholderText);
        textField.putClientProperty(FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON, true);
        return textField;
    }


    private void delMenu() {
        Tree selectedMenu = getSelectedMenuNode();
        if (selectedMenu == null) {
            return;
        }
        Long menuId = (Long) selectedMenu.get("id");
        String menuName = selectedMenu.getName().toString();

        int opt = JOptionPane.showOptionDialog(this, "是否确定删除[" + menuName + "]？", "提示", OK_CANCEL_OPTION, WARNING_MESSAGE, null, null, null);

        if (opt != 0) {
            return;
        }

        final Long finalMenuId = menuId;
        disposeTrackedDisposable(currentDeleteDisposable);

        final AtomicReference<Disposable> deleteDisposableRef = new AtomicReference<Disposable>();
        Completable
                /*
                 * 删除接口没有业务返回值，因此用 Completable 表达“只关心完成/失败”会更直观。
                 */
                .fromAction(() -> Forest.client(MenuApi.class).deleteMenu(finalMenuId).getCheckedData())
                .subscribeOn(Schedulers.io())
                .observeOn(SwingSchedulers.edt())
                .doOnSubscribe(disposable -> {
                    deleteDisposableRef.set(disposable);
                    currentDeleteDisposable.set(disposable);
                    showWaitMessage("菜单删除中...");
                })
                .doFinally(() -> {
                    clearTrackedDisposable(currentDeleteDisposable, deleteDisposableRef.get());
                    hideWaitMessage();
                })
                .subscribe(
                        () -> {
                            WMessage.showMessageSuccess(MainFrame.getInstance(), "删除菜单成功");
                            postMenuRefreshEvent();
                            /*
                             * 这里故意放到下一轮 EDT 再触发刷新，
                             * 避免本次删除链的 doFinally 把下一次“菜单加载中...”遮罩提前关掉。
                             */
                            SwingSchedulers.postOnEdt(this::updateData);
                        },
                        SwingExceptionHandler::handle
                );
    }

    /**
     * 统一显示等待遮罩。
     * <p>
     * 即使未来调用方不在 EDT，也可以安全复用。
     * </p>
     */
    private void showWaitMessage(final String message) {
        SwingSchedulers.runOnEdt(() -> waitPane.showMessageLayer(message));
    }

    /**
     * 统一隐藏等待遮罩。
     */
    private void hideWaitMessage() {
        SwingSchedulers.runOnEdt(waitPane::hideMessageLayer);
    }

    /**
     * 替换式 dispose。
     * <p>
     * 发起新请求前先 dispose 旧订阅，避免旧链继续占用页面资源。
     * </p>
     */
    private void disposeTrackedDisposable(AtomicReference<Disposable> disposableRef) {
        Disposable disposable = disposableRef.getAndSet(null);
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    /**
     * 仅在句柄仍然是“当前活跃订阅”时才清空引用。
     * 这样可以防止旧请求结束时把新请求的句柄误覆盖掉。
     */
    private void clearTrackedDisposable(AtomicReference<Disposable> disposableRef, Disposable disposable) {
        disposableRef.compareAndSet(disposable, null);
    }

    private Tree getSelectedMenuNode() {
        int selectedRow = treeTable.getSelectedRow();
        if (selectedRow < 0) {
            return null;
        }
        Object obj = treeTable.getPathForRow(selectedRow).getLastPathComponent();
        return obj instanceof Tree ? (Tree) obj : null;
    }

    private Long getSelectedMenuId() {
        Tree selectedNode = getSelectedMenuNode();
        return selectedNode == null ? null : (Long) selectedNode.get("id");
    }

    private void postMenuRefreshEvent() {
        EventBusCenter.get().post(new MenuRefreshEvent());
    }

    @Override
    public void removeNotify() {
        disposeTrackedDisposable(currentLoadDisposable);
        disposeTrackedDisposable(currentDeleteDisposable);
        super.removeNotify();
    }


    private static class MenuTreeTableModel extends AbstractTreeTableModel {


        public MenuTreeTableModel() {
        }

        public MenuTreeTableModel(Object root) {
            super(root);
        }


        public void setRoot(Object root) {
            this.root = root;
            modelSupport.fireNewRoot();
        }

        @Override
        public boolean isCellEditable(Object node, int column) {
            if (column == 7) {
                return true;
            }
            return super.isCellEditable(node, column);
        }

        @Override
        public Object getChild(Object parent, int index) {

            Tree parentTree = (Tree) parent;

            return parentTree.getChildren().get(index);
        }

        @Override
        public int getChildCount(Object parent) {
            if (parent instanceof Tree) {
                List<Tree> children = ((Tree) parent).getChildren();

                if (children != null) {
                    return children.size();
                }
            }
            return 0;
        }

        @Override
        public int getIndexOfChild(Object parent, Object child) {
            if (parent instanceof Tree && child instanceof Tree) {
                Tree parentFile = (Tree) parent;
                List<Tree> treeList = parentFile.getChildren();

                for (int i = 0, len = treeList.size(); i < len; i++) {
                    if (treeList.get(i).equals(child)) {
                        return i;
                    }
                }
            }
            return -1;
        }

        @Override
        public Class<?> getColumnClass(int column) {
            switch (column) {
                case 0:
                    return String.class;
                case 1:
                    return String.class;
                case 2:
                    return String.class;
                case 3:
                    return String.class;
                case 4:
                    return String.class;
                default:
                    return super.getColumnClass(column);
            }
        }

        @Override
        public String getColumnName(int column) {
            return COLUMN_NAMES[column];
        }


        @Override
        public int getColumnCount() {
            return COLUMN_NAMES.length;
        }

        @Override
        public Object getValueAt(Object node, int column) {
            if (node instanceof Tree) {
                Tree tree = (Tree) node;
                switch (column) {
                    case 0:
                        return tree.getName();
                    case 1:
                        return tree.get("icon");
                    case 2:
                        return tree.get("orderNum");
                    case 3:
                        return tree.get("perms");
                    case 4:
                        return tree.get("component");
                    case 5:
                        return tree.get("componentName");
                    case 6:
                        return tree.get("status");


                }
            }

            return null;
        }

    }


}

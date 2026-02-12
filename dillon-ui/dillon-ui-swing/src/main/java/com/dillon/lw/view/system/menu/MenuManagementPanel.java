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
import com.dillon.lw.utils.ExecuteUtils;
import com.dillon.lw.utils.IconLoader;
import com.dillon.lw.view.frame.MainFrame;
import com.dtflys.forest.Forest;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.treetable.AbstractTreeTableModel;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

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

    private WaitPane waitPane;

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
        JButton searchButton = new WButton("搜索");
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
     */
    private void updateData() {
        MenuListReqVO reqVO = new MenuListReqVO();
        reqVO.setName(nameTextField.getText());
        int selectIndex = statusCombo.getSelectedIndex();
        if (selectIndex != 0) {
            reqVO.setStatus(selectIndex == 1 ? 0 : 1);
        }
        Map<String, Object> queryMap = BeanUtil.beanToMap(reqVO, false, true);

        ExecuteUtils.execute(
                () -> Forest.client(MenuApi.class).getMenuList(queryMap).getCheckedData(),
                data -> {
                    Tree<Long> treeRoot = buildMenuTree(data);
                    updateTreeTableRoot(treeRoot);
                    if (exButton.isSelected()) {
                        treeTable.expandAll();
                    }
                },
                () -> {
                },
                "查询成功！",
                waitPane
        );

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

        Long finalMenuId = menuId;
        CompletableFuture.runAsync(() -> {
            Forest.client(MenuApi.class).deleteMenu(finalMenuId).getCheckedData();
        }).thenAcceptAsync(unused -> {
            WMessage.showMessageSuccess(MainFrame.getInstance(), "删除菜单成功");
            updateData();
            postMenuRefreshEvent();
        }, SwingUtilities::invokeLater).exceptionally(throwable -> {
            SwingUtilities.invokeLater(() -> {
                SwingExceptionHandler.handle(throwable);
            });
            return null;
        });


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

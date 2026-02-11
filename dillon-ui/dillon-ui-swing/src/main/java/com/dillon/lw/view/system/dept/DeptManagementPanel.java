package com.dillon.lw.view.system.dept;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNodeConfig;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.util.ObjectUtil;
import com.dillon.lw.api.system.DeptApi;
import com.dillon.lw.components.*;
import com.dillon.lw.components.notice.WMessage;
import com.dillon.lw.components.table.renderer.OptButtonTableCellEditor;
import com.dillon.lw.components.table.renderer.OptButtonTableCellRenderer;
import com.dillon.lw.exception.SwingExceptionHandler;
import com.dillon.lw.module.system.controller.admin.dept.vo.dept.DeptListReqVO;
import com.dillon.lw.module.system.controller.admin.dept.vo.dept.DeptSaveReqVO;
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
import java.util.function.Consumer;
import java.util.function.Supplier;

import static javax.swing.JOptionPane.*;

/**
 * 部门管理面板
 *
 * @author liwen
 * @date 2022/07/17
 */
public class DeptManagementPanel extends AbstractRefreshablePanel {
    private static final String[] COLUMN_ID = {"部门名称", "负责人", "排序", "状态", "创建时间", "操作"};
    private static final String SUCCESS_DELETE = "删除成功！";
    private static final String WARN_SELECT_DEPT_FIRST = "请先选择部门！";

    private JXTreeTable treeTable;

    private JTextField nameTextField;
    private JComboBox statusCombo;


    private WaitPane waitPane;

    public DeptManagementPanel() {

        initComponents();
        updateData();
    }

    @Override
    protected void doRefresh() {
        updateData();
    }

    private void initComponents() {

        JPanel toolBar = new WPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        toolBar.add(new JLabel("部门名称"));
        toolBar.add(nameTextField = createTextField("请输入部门名称"));

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
            showDeptAddDialog(null);
        });
        addButton.setBackground(new Color(0x1c7dfa));

        JToggleButton exButton = new JToggleButton("展开/折叠");
        toolBar.add(exButton);
        exButton.addActionListener(e -> {
            if (exButton.isSelected()) {
                treeTable.expandAll();
            } else {
                treeTable.collapseAll();
            }
        });


        treeTable = new JXTreeTable(new DeptTreeTableModel(null));
        treeTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        treeTable.setRowHeight(50);
        treeTable.setLeafIcon(null);
        treeTable.setClosedIcon(null);
        treeTable.setOpenIcon(null);
        treeTable.setShowHorizontalLines(true);
        treeTable.setIntercellSpacing(new Dimension(0, 1));
        treeTable.setOpaque(false);
        treeTable.setHighlighters();
        ColorHighlighter rollover = new ColorHighlighter(HighlightPredicate.ROLLOVER_ROW, UIManager.getColor("App.hoverBackground"), null);
        treeTable.setHighlighters(rollover);
        treeTable.putClientProperty(JXTreeTable.USE_DTCR_COLORMEMORY_HACK, Boolean.FALSE);
        JScrollPane tsp = new WScrollPane(treeTable);
        JPanel panel = new WPanel();
        panel.setLayout(new BorderLayout());
        panel.add(tsp);
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        this.setLayout(new BorderLayout(0, 10));
        this.add(toolBar, BorderLayout.NORTH);
        this.add(panel);


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

    /**
     * @deprecated 历史命名，建议改用 {@link #createActionBar()}。
     */
    @Deprecated
    private JToolBar creatBar() {
        return createActionBar();
    }

    /**
     * 创建“操作列”工具栏，包含修改/新增/删除。
     */
    private JToolBar createActionBar() {
        JToolBar optBar = new JToolBar();
        optBar.setOpaque(false);
        JButton edit = new JButton("修改");
        edit.setForeground(UIManager.getColor("App.accent.color"));
        edit.setIcon(new FlatSVGIcon("icons/xiugai.svg", 15, 15));

        edit.addActionListener(e -> showDeptEditDialog());
        edit.setForeground(UIManager.getColor("App.accent.color"));

        JButton del = new JButton("删除");
        del.setIcon(new FlatSVGIcon("icons/delte.svg", 15, 15));
        del.addActionListener(e -> del());
        del.setForeground(UIManager.getColor("App.danger.color"));
        JButton add = new JButton("新增");
        add.addActionListener(e -> {
            showDeptAddDialog(getSelectedDeptId());
        });
        add.setForeground(UIManager.getColor("App.accent.color"));
        add.setIcon(new FlatSVGIcon("icons/xinzeng.svg", 15, 15));
        optBar.add(Box.createGlue());
        optBar.add(edit);
        optBar.add(add);
        optBar.add(del);
        optBar.add(Box.createGlue());
        optBar.setPreferredSize(new Dimension(210, 45));
        return optBar;

    }


    private void showDeptAddDialog(Long id) {
        DeptEditPane formPane = new DeptEditPane();
        formPane.updateData(id, true);

        WFormDialog<DeptSaveReqVO> dialog = new WFormDialog<>(
                MainFrame.getInstance(), "添加", formPane);

        dialog.showDialogWithAsyncSubmit(
                formPane::validates,
                formPane::getValue,
                data -> Forest.client(DeptApi.class).createDept(data).getCheckedData(),
                this::updateData,
                "添加成功！"
        );
    }


    private void showDeptEditDialog() {
        Long deptId = getSelectedDeptId();
        if (deptId == null) {
            WMessage.showMessageWarning(MainFrame.getInstance(), WARN_SELECT_DEPT_FIRST);
            return;
        }

        DeptEditPane formPane = new DeptEditPane();
        formPane.updateData(deptId, false);

        WFormDialog<DeptSaveReqVO> dialog = new WFormDialog<>(
                MainFrame.getInstance(), "修改", formPane);

        dialog.showDialogWithAsyncSubmit(
                formPane::validates,
                formPane::getValue,
                data -> Forest.client(DeptApi.class).updateDept(data).getCheckedData(),
                this::updateData,
                "修改成功！"
        );
    }

    private void updateData() {
        // 1) 组装筛选条件；2) 查询部门列表；3) 构建树并刷新表格。
        DeptListReqVO deptListReqVO = new DeptListReqVO();
        deptListReqVO.setName(nameTextField.getText());
        int selectIndex = statusCombo.getSelectedIndex();
        if (selectIndex != 0) {
            deptListReqVO.setStatus(selectIndex == 1 ? 0 : 1);

        }
        Map<String, Object> queryMap = BeanUtil.beanToMap(deptListReqVO, false, true);
        executeAsync(() -> Forest.client(DeptApi.class).getDeptList(queryMap).getCheckedData(), deptRespVOList -> {
            long min = deptRespVOList.stream().mapToLong(value -> value.getParentId()).min().orElse(0L);
            TreeNodeConfig config = new TreeNodeConfig();
            config.setWeightKey("sort");
            config.setParentIdKey("parentId");
            config.setNameKey("name");
            Tree<Long> treeList = TreeUtil.buildSingle(deptRespVOList, min, config, ((object, treeNode) -> {
                treeNode.setId(object.getId());
                treeNode.setParentId(object.getParentId());
                treeNode.setName(object.getName());
                treeNode.putExtra("sort", object.getSort());
                treeNode.putExtra("status", object.getStatus());
                if (object.getLeaderUserId() != null) {
                    treeNode.putExtra("leaderUserId", object.getLeaderUserId());
                }
                treeNode.putExtra("createTime", DateUtil.format(object.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
            }));
            updateTreeTableRoot(treeList);
        });


    }

    private void updateTreeTableRoot(Object root) {

        treeTable.setTreeTableModel(new DeptTreeTableModel(root));
        treeTable.getColumnExt("操作").setMinWidth(240);
        treeTable.getColumnExt("操作").setMaxWidth(240);
        treeTable.getColumnExt("排序").setWidth(80);
        treeTable.getColumnExt("排序").setMaxWidth(80);

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
        treeTable.getColumnExt("操作").setCellRenderer(new OptButtonTableCellRenderer(createActionBar()));
        treeTable.getColumnExt("操作").setCellEditor(new OptButtonTableCellEditor(createActionBar()));
        treeTable.getColumn("排序").setCellRenderer(new DefaultTableCellRenderer() {
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


    private void del() {
        Tree selectedDept = getSelectedDeptNode();
        if (selectedDept == null) {
            WMessage.showMessageWarning(MainFrame.getInstance(), WARN_SELECT_DEPT_FIRST);
            return;
        }
        Long deptId = (Long) selectedDept.get("id");
        String deptName = selectedDept.getName().toString();

        int opt = JOptionPane.showOptionDialog(this, "是否确定删除[" + deptName + "]？", "提示", OK_CANCEL_OPTION, WARNING_MESSAGE, null, null, null);

        if (opt != 0) {
            return;
        }

        executeAsync(() -> Forest.client(DeptApi.class).deleteDept(deptId).getCheckedData(), aBoolean -> {
            WMessage.showMessageSuccess(MainFrame.getInstance(), SUCCESS_DELETE);
            updateData();
        });

    }

    private Tree getSelectedDeptNode() {
        // TreeTable 的每一行对应一个 Tree 节点；未选中时返回 null。
        int selectedRow = treeTable.getSelectedRow();
        if (selectedRow < 0) {
            return null;
        }
        Object obj = treeTable.getPathForRow(selectedRow).getLastPathComponent();
        return obj instanceof Tree ? (Tree) obj : null;
    }

    private Long getSelectedDeptId() {
        // 统一“未选中返回 null”的语义，便于上层做显式校验。
        Tree selectedNode = getSelectedDeptNode();
        if (selectedNode == null) {
            return null;
        }
        return (Long) selectedNode.get("id");
    }

    private <T> void executeAsync(Supplier<T> request, Consumer<T> onSuccess) {
        // 统一异步执行模板：后台请求 + EDT 更新 + 全局异常处理。
        CompletableFuture
                .supplyAsync(request)
                .thenAcceptAsync(onSuccess, SwingUtilities::invokeLater)
                .exceptionally(throwable -> {
                    SwingUtilities.invokeLater(() -> SwingExceptionHandler.handle(throwable));
                    return null;
                });
    }

    private static class DeptTreeTableModel extends AbstractTreeTableModel {


        public DeptTreeTableModel() {
        }

        public DeptTreeTableModel(Object root) {
            super(root);
        }


        public void setRoot(Object root) {
            this.root = root;
            modelSupport.fireNewRoot();
        }

        @Override
        public boolean isCellEditable(Object node, int column) {
            if (column == 5) {
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
            return COLUMN_ID[column];
        }


        @Override
        public int getColumnCount() {
            return COLUMN_ID.length;
        }

        @Override
        public Object getValueAt(Object node, int column) {
            if (node instanceof Tree) {
                Tree tree = (Tree) node;
                switch (column) {
                    case 0:
                        return tree.getName();
                    case 1:
                        return tree.get("leaderUserId");
                    case 2:
                        return tree.get("sort");
                    case 3:
                        return tree.get("status");
                    case 4:
                        return tree.get("createTime");
                }
            }

            return null;
        }

    }


}

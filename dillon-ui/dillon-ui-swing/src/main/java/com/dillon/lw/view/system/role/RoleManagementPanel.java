/*
 * Created by JFormDesigner on Thu Jun 13 19:52:21 CST 2024
 */

package com.dillon.lw.view.system.role;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.dillon.lw.api.system.PermissionApi;
import com.dillon.lw.api.system.RoleApi;
import com.dillon.lw.components.*;
import com.dillon.lw.components.notice.WMessage;
import com.dillon.lw.components.table.renderer.OptButtonTableCellEditor;
import com.dillon.lw.components.table.renderer.OptButtonTableCellRenderer;
import com.dillon.lw.exception.SwingExceptionHandler;
import com.dillon.lw.module.system.controller.admin.permission.vo.permission.PermissionAssignRoleDataScopeReqVO;
import com.dillon.lw.module.system.controller.admin.permission.vo.permission.PermissionAssignRoleMenuReqVO;
import com.dillon.lw.module.system.controller.admin.permission.vo.role.RoleRespVO;
import com.dillon.lw.module.system.controller.admin.permission.vo.role.RoleSaveReqVO;
import com.dillon.lw.view.frame.MainFrame;
import com.dtflys.forest.Forest;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import net.miginfocom.swing.MigLayout;
import org.jdesktop.swingx.JXTable;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;

import static javax.swing.JOptionPane.*;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import com.dillon.lw.swing.rx.SwingSchedulers;
import com.dillon.lw.swing.rx.SwingRx;

/**
 * 角色管理主面板。
 * <p>
 * 负责角色列表查询、分页、增删改，以及角色菜单/数据权限分配。
 * </p>
 *
 * @author wenli
 */
public class RoleManagementPanel extends com.dillon.lw.components.AbstractDisposablePanel {
    private static final String[] COLUMN_ID = {"角色编号", "角色名称", "角色类型", "角色标识", "显示顺序", "备注", "状态", "创建时间", "操作"};
    private static final int COL_ROLE_ID = 0;
    private static final int COL_ROLE_NAME = 1;
    private static final int COL_ROLE_OBJECT = 8;
    private static final String SUCCESS_ASSIGN = "分配成功！";
    private static final String WARN_SELECT_ROLE_FIRST = "请先选择角色！";

    private DefaultTableModel tableModel;

    private WPaginationPane paginationPane;

    public RoleManagementPanel() {
        initComponents();
        initListeners();
        loadTableData();
    }

    private void initComponents() {
        textField = new JTextField();
        scrollPane1 = new WScrollPane();
        centerPane = new JPanel();
        scrollPane2 = new WScrollPane();
        table = new JXTable(tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return "操作".equals(getColumnName(column));
            }
        });
        toolPane = new WPanel();
        label7 = new JLabel();
        nameTextField = new JTextField();
        label8 = new JLabel();
        codeTextField = new JTextField();
        label9 = new JLabel();
        stautsComboBox = new JComboBox();
        label10 = new JLabel();
        startDateTextField = new WLocalDateCombo();
        label11 = new JLabel();
        endDateTextField = new WLocalDateCombo();
        searchBut = new JButton();
        reseBut = new JButton();
        newBut = new JButton();

        //======== this ========
        setOpaque(false);
        setLayout(new BorderLayout(10, 10));


        //======== centerPane ========
        {
            centerPane.setOpaque(false);
            centerPane.setLayout(new BorderLayout(10, 10));

            //======== scrollPane2 ========
            {
                tableModel.setColumnIdentifiers(COLUMN_ID);
                scrollPane2.setViewportView(table);
            }

            JPanel panel = new WPanel();
            panel.setLayout(new BorderLayout());
            panel.add(scrollPane2);
            paginationPane = new WPaginationPane() {
                @Override
                public void setPageIndex(long pageIndex) {
                    super.setPageIndex(pageIndex);
                    loadTableData();
                }
            };
            paginationPane.setOpaque(false);
            panel.add(paginationPane, BorderLayout.SOUTH);
            centerPane.add(panel, BorderLayout.CENTER);

            //======== toolPane ========
            {
                toolPane.setLayout(new MigLayout(
                        "fill,insets 0,hidemode 3",
                        // columns
                        "[left]",
                        // rows
                        "[]"));
                toolPane.setBorder(new EmptyBorder(10, 10, 10, 10));
                //---- label7 ----
                label7.setText("角色名称");
                toolPane.add(label7, "cell 0 0");

                //---- userNameTextField ----
                nameTextField.setColumns(15);
                toolPane.add(nameTextField, "cell 0 0");

                //---- label8 ----
                label8.setText("角色标识");
                toolPane.add(label8, "cell 0 0");

                //---- phoneTextField ----
                codeTextField.setColumns(15);
                toolPane.add(codeTextField, "cell 0 0");

                //---- label9 ----
                label9.setText("状态");
                toolPane.add(label9, "cell 0 0");
                toolPane.add(stautsComboBox, "cell 0 0");

                //---- label10 ----
                label10.setText("创建时间");
                toolPane.add(label10, "cell 0 0");

                //---- startDateTextField ----
                toolPane.add(startDateTextField, "cell 0 0");

                //---- label11 ----
                label11.setText("-");
                toolPane.add(label11, "cell 0 0");

                //---- endDateTextField ----
                toolPane.add(endDateTextField, "cell 0 0");

                //---- button1 ----
                searchBut.setText("\u641c\u7d22");
                toolPane.add(searchBut, "cell 0 0");

                //---- reseBut ----
                reseBut.setText("\u91cd\u7f6e");
                toolPane.add(reseBut, "cell 0 0");

                //---- newBut ----
                newBut.setText("\u65b0\u589e");
                toolPane.add(newBut, "cell 0 0");
            }
            centerPane.add(toolPane, BorderLayout.NORTH);
        }
        add(centerPane, BorderLayout.CENTER);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on

        table.setRowHeight(40);


        stautsComboBox.addItem("全部");
        stautsComboBox.addItem("开启");
        stautsComboBox.addItem("关闭");

        startDateTextField.setValue(null);
        endDateTextField.setValue(null);


        table.setDefaultRenderer(Object.class, new CenterTableCellRenderer());


    }

    /**
     * @deprecated 历史命名，建议改用 {@link #createActionBar()}。
     */
    @Deprecated
    private JToolBar creatBar() {
        return createActionBar();
    }

    /**
     * 创建“操作列”按钮工具栏。
     */
    private JToolBar createActionBar() {
        JToolBar optBar = new JToolBar();
        optBar.setOpaque(false);
        JButton edit = new JButton("修改");
        edit.setForeground(UIManager.getColor("App.accent.color"));
        edit.setIcon(new FlatSVGIcon("icons/xiugai.svg", 15, 15));
        edit.addActionListener(e -> showEditDialog());
        edit.setForeground(UIManager.getColor("App.accent.color"));

        JButton del = new JButton("删除");
        del.setIcon(new FlatSVGIcon("icons/delte.svg", 15, 15));
        del.addActionListener(e -> del());
        del.setForeground(UIManager.getColor("App.danger.color"));
        JButton menuPermissionBut = new JButton("菜单权限");
        menuPermissionBut.addActionListener(e -> showRoleAssignMenuDialog());
        menuPermissionBut.setForeground(UIManager.getColor("App.accent.color"));
        menuPermissionBut.setIcon(new FlatSVGIcon("icons/xinzeng.svg", 15, 15));

        JButton dataPermissionBut = new JButton("数据权限");
        dataPermissionBut.addActionListener(e -> showDataPermissionPaneDialog());
        dataPermissionBut.setForeground(UIManager.getColor("App.accent.color"));
        dataPermissionBut.setIcon(new FlatSVGIcon("icons/xinzeng.svg", 15, 15));


        optBar.add(edit);
        optBar.add(menuPermissionBut);
        optBar.add(dataPermissionBut);
        optBar.add(del);
        optBar.setPreferredSize(new Dimension(210, 45));
        return optBar;

    }

    private void initListeners() {

        reseBut.addActionListener(e -> reset());
        searchBut.addActionListener(e -> loadTableData());
        newBut.addActionListener(e -> showAddDialog(null));
    }

    private void reset() {
        nameTextField.setText("");
        codeTextField.setText("");
        stautsComboBox.setSelectedIndex(0);
        startDateTextField.setValue(null);
        endDateTextField.setValue(null);
    }

    private void showAddDialog(Long id) {
        RoleEditPane formPane = new RoleEditPane();
        formPane.updateData(id);

        WFormDialog<RoleSaveReqVO> dialog = new WFormDialog<>(
                MainFrame.getInstance(), "添加", formPane);

        dialog.showDialogWithAsyncSubmit(
                formPane::validates,
                formPane::getValue,
                data -> Forest.client(RoleApi.class).createRole(data).getCheckedData(),
                this::loadTableData,
                "添加成功！"
        );
    }

    private void showEditDialog() {
        Long roleId = getSelectedRoleId();
        if (roleId == null) {
            WMessage.showMessageWarning(MainFrame.getInstance(), WARN_SELECT_ROLE_FIRST);
            return;
        }

        RoleEditPane formPane = new RoleEditPane();
        formPane.updateData(roleId);

        WFormDialog<RoleSaveReqVO> dialog = new WFormDialog<>(
                MainFrame.getInstance(), "修改", formPane);

        dialog.showDialogWithAsyncSubmit(
                formPane::validates,
                formPane::getValue,
                data -> Forest.client(RoleApi.class).updateRole(data).getCheckedData(),
                this::loadTableData,
                "修改成功！"
        );
    }

    private void showDataPermissionPaneDialog() {
        RoleRespVO roleRespVO = getSelectedRole();
        if (roleRespVO.getId() == null) {
            WMessage.showMessageWarning(MainFrame.getInstance(), WARN_SELECT_ROLE_FIRST);
            return;
        }

        DataPermissionPane dataPermissionPane = new DataPermissionPane();
        dataPermissionPane.updateData(roleRespVO);
        int opt = JOptionPane.showOptionDialog(null, dataPermissionPane, "数据权限", OK_CANCEL_OPTION, PLAIN_MESSAGE, null, new Object[]{"确定", "取消"}, "确定");
        if (opt == 0) {
            assignRoleDataScope(dataPermissionPane.getValue());
        }
    }

    private void showRoleAssignMenuDialog() {
        RoleRespVO roleRespVO = getSelectedRole();
        if (roleRespVO.getId() == null) {
            WMessage.showMessageWarning(MainFrame.getInstance(), WARN_SELECT_ROLE_FIRST);
            return;
        }

        RoleAssignMenuPane roleAssignMenuPane = new RoleAssignMenuPane();
        roleAssignMenuPane.updateData(roleRespVO);
        int opt = JOptionPane.showOptionDialog(null, roleAssignMenuPane, "菜单权限", OK_CANCEL_OPTION, PLAIN_MESSAGE, null, new Object[]{"确定", "取消"}, "确定");
        if (opt == 0) {
            assignRoleMenu(roleAssignMenuPane.getValue());
        }
    }

    @Override
    public void updateUI() {
        super.updateUI();
        if (table != null) {
            table.setDefaultRenderer(Object.class, new CenterTableCellRenderer());
        }
    }


    private void del() {
        Long id = getSelectedRoleId();
        String roleName = getSelectedRoleName();
        if (id == null) {
            WMessage.showMessageWarning(MainFrame.getInstance(), WARN_SELECT_ROLE_FIRST);
            return;
        }

        int opt = JOptionPane.showOptionDialog(this, "是否确定删除[" + roleName + "]？", "提示", OK_CANCEL_OPTION, WARNING_MESSAGE, null, null, null);

        if (opt != 0) {
            return;
        }

        Single
                /*
                 * 同步接口先通过 fromCallable 包装成懒执行的 RxJava 任务，
                 * 请求放到 IO 线程执行，成功结果再切回 EDT 更新 Swing 组件。
                 */
                .fromCallable(() -> Forest.client(RoleApi.class).deleteRole(id).getCheckedData())
                .subscribeOn(Schedulers.io())
                .observeOn(SwingSchedulers.edt())
                .compose(SwingRx.bindTo(this))
                .subscribe(result -> {
                    WMessage.showMessageSuccess(MainFrame.getInstance(), "删除成功！");
                    loadTableData();
                }, SwingExceptionHandler::handle);
    }

    /**
     * 提交角色菜单权限分配。
     */
    private void assignRoleMenu(PermissionAssignRoleMenuReqVO permissionAssignRoleMenuReqVO) {
        Single
                /*
                 * 同步接口先通过 fromCallable 包装成懒执行的 RxJava 任务，
                 * 请求放到 IO 线程执行，成功结果再切回 EDT 更新 Swing 组件。
                 */
                .fromCallable(() -> Forest.client(PermissionApi.class).assignRoleMenu(permissionAssignRoleMenuReqVO).getCheckedData())
                .subscribeOn(Schedulers.io())
                .observeOn(SwingSchedulers.edt())
                .compose(SwingRx.bindTo(this))
                .subscribe(result -> {
                    WMessage.showMessageSuccess(MainFrame.getInstance(), SUCCESS_ASSIGN);
                    loadTableData();
                }, SwingExceptionHandler::handle);
    }

    /**
     * 提交角色数据权限分配。
     */
    private void assignRoleDataScope(PermissionAssignRoleDataScopeReqVO permissionAssignRoleDataScopeReqVO) {
        Single
                /*
                 * 同步接口先通过 fromCallable 包装成懒执行的 RxJava 任务，
                 * 请求放到 IO 线程执行，成功结果再切回 EDT 更新 Swing 组件。
                 */
                .fromCallable(() -> Forest.client(PermissionApi.class).assignRoleDataScope(permissionAssignRoleDataScopeReqVO).getCheckedData())
                .subscribeOn(Schedulers.io())
                .observeOn(SwingSchedulers.edt())
                .compose(SwingRx.bindTo(this))
                .subscribe(result -> {
                    WMessage.showMessageSuccess(MainFrame.getInstance(), SUCCESS_ASSIGN);
                    loadTableData();
                }, SwingExceptionHandler::handle);
    }


    /**
     * 按当前筛选条件查询角色分页数据，并刷新表格与分页器。
     */
    public void loadTableData() {

        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("pageNo", paginationPane.getPageIndex());
        queryMap.put("pageSize", paginationPane.getPageSize());

        if (StrUtil.isNotBlank(nameTextField.getText())) {
            queryMap.put("name", nameTextField.getText());
        }
        if (StrUtil.isNotBlank(codeTextField.getText())) {
            queryMap.put("code", codeTextField.getText());
        }
        queryMap.put("status", stautsComboBox.getSelectedIndex() == 0 ? null : (stautsComboBox.getSelectedIndex() == 1 ? 0 : 1));

        if (ObjectUtil.isAllNotEmpty(startDateTextField.getValue(), endDateTextField.getValue())) {
            java.lang.String sd = DateUtil.format(startDateTextField.getValue().atTime(0, 0, 0), "yyyy-MM-dd HH:mm:ss");
            java.lang.String ed = DateUtil.format(endDateTextField.getValue().atTime(23, 59, 59), "yyyy-MM-dd HH:mm:ss");
            queryMap.put("createTime", ListUtil.of(sd, ed));
        }
        queryMap.values().removeIf(Objects::isNull);

        Single
                /*
                 * 同步接口先通过 fromCallable 包装成懒执行的 RxJava 任务，
                 * 请求放到 IO 线程执行，成功结果再切回 EDT 更新 Swing 组件。
                 */
                .fromCallable(() -> Forest.client(RoleApi.class).getRolePage(queryMap).getCheckedData())
                .subscribeOn(Schedulers.io())
                .observeOn(SwingSchedulers.edt())
                .doOnSubscribe(disposable -> {
                    /*
                     * 角色查询期间先禁用搜索按钮，避免用户连续点击触发多个相同分页请求。
                     * doOnSubscribe 不保证运行在 EDT，因此按钮状态切换显式回到 Swing 线程。
                     */
                    SwingSchedulers.runOnEdt(() -> searchBut.setEnabled(false));
                })
                .doFinally(() -> {
                    /*
                     * 查询链结束后恢复按钮，不让异常或取消场景把搜索入口一直锁住。
                     * doFinally 同样通过 EDT 执行，确保 Swing 组件访问线程安全。
                     */
                    SwingSchedulers.runOnEdt(() -> searchBut.setEnabled(true));
                })
                .compose(SwingRx.bindTo(this))
                .subscribe(result -> {
                    Vector<Vector> tableData = new Vector<>();
                    result.getList().forEach(roleRespVO -> {
                        Vector rowV = new Vector();
                        rowV.add(roleRespVO.getId());
                        rowV.add(roleRespVO.getName());
                        rowV.add(roleRespVO.getType());
                        rowV.add(roleRespVO.getCode());
                        rowV.add(roleRespVO.getSort());
                        rowV.add(roleRespVO.getRemark());
                        rowV.add(roleRespVO.getStatus());
                        rowV.add(DateUtil.format(roleRespVO.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
                        rowV.add(roleRespVO);
                        tableData.add(rowV);
                    });
                    tableModel.setDataVector(tableData, new Vector<>(Arrays.asList(COLUMN_ID)));
                    table.getColumn("操作").setMinWidth(240);
                    table.getColumn("操作").setCellRenderer(new OptButtonTableCellRenderer(createActionBar()));
                    table.getColumn("操作").setCellEditor(new OptButtonTableCellEditor(createActionBar()));

                    table.getColumn("状态").setCellRenderer(new DefaultTableCellRenderer() {
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
                            panel.setOpaque(isSelected);
                            return panel;
                        }
                    });
                    paginationPane.setTotal(result.getTotal());
                }, SwingExceptionHandler::handle);
    }

    private Long getSelectedRoleId() {
        // 表格未选择行时返回 null，调用方需做前置校验并给出提示。
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            return null;
        }
        return Convert.toLong(table.getValueAt(selectedRow, COL_ROLE_ID));
    }

    private String getSelectedRoleName() {
        // 与 getSelectedRoleId 保持一致的“未选中即 null”语义。
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            return null;
        }
        return Convert.toStr(table.getValueAt(selectedRow, COL_ROLE_NAME));
    }

    private RoleRespVO getSelectedRole() {
        // 返回空对象以避免调用方出现 NPE；调用方按 id 是否为空判断是否有选择。
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            return new RoleRespVO();
        }
        return (RoleRespVO) table.getValueAt(selectedRow, COL_ROLE_OBJECT);
    }


    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner non-commercial license
    private JTextField textField;
    private JScrollPane scrollPane1;
    private JPanel centerPane;
    private JScrollPane scrollPane2;
    private JXTable table;
    private JPanel toolPane;
    private JLabel label7;
    private JTextField nameTextField;
    private JLabel label8;
    private JTextField codeTextField;
    private JLabel label9;
    private JComboBox stautsComboBox;
    private JLabel label10;
    private WLocalDateCombo startDateTextField;
    private JLabel label11;
    private WLocalDateCombo endDateTextField;
    private JButton searchBut;
    private JButton reseBut;
    private JButton newBut;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}

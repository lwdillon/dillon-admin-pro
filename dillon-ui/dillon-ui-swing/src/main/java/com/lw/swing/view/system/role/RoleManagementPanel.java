/*
 * Created by JFormDesigner on Thu Jun 13 19:52:21 CST 2024
 */

package com.lw.swing.view.system.role;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.lw.dillon.admin.framework.common.pojo.CommonResult;
import com.lw.dillon.admin.framework.common.pojo.PageResult;
import com.lw.dillon.admin.module.system.controller.admin.permission.vo.permission.PermissionAssignRoleDataScopeReqVO;
import com.lw.dillon.admin.module.system.controller.admin.permission.vo.permission.PermissionAssignRoleMenuReqVO;
import com.lw.dillon.admin.module.system.controller.admin.permission.vo.role.RoleRespVO;
import com.lw.dillon.admin.module.system.controller.admin.permission.vo.role.RoleSaveReqVO;
import com.lw.swing.components.*;
import com.lw.swing.components.notice.WMessage;
import com.lw.swing.components.table.renderer.OptButtonTableCellEditor;
import com.lw.swing.components.table.renderer.OptButtonTableCellRenderer;
import com.lw.swing.request.Request;
import com.lw.swing.view.MainFrame;
import com.lw.ui.request.api.system.PermissionFeign;
import com.lw.ui.request.api.system.RoleFeign;
import net.miginfocom.swing.MigLayout;
import org.jdesktop.swingx.JXTable;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.concurrent.ExecutionException;

import static javax.swing.JOptionPane.*;

/**
 * @author wenli
 */
public class RoleManagementPanel extends JPanel {
    private String[] COLUMN_ID = {"角色编号", "角色名称", "角色类型", "角色标识", "显示顺序", "备注", "状态", "创建时间", "操作"};

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
        table = new JXTable(tableModel = new DefaultTableModel());
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

    private JToolBar creatBar() {
        JToolBar optBar = new JToolBar();
        optBar.setOpaque(false);
        JButton edit = new JButton("修改");
        edit.setForeground(UIManager.getColor("App.accentColor"));
        edit.setIcon(new FlatSVGIcon("icons/xiugai.svg", 15, 15));
        edit.addActionListener(e -> showEditDialog());
        edit.setForeground(UIManager.getColor("App.accentColor"));

        JButton del = new JButton("删除");
        del.setIcon(new FlatSVGIcon("icons/delte.svg", 15, 15));
        del.addActionListener(e -> del());
        del.setForeground(UIManager.getColor("app-error-color-5"));

        JButton menuPermissionBut = new JButton("菜单权限");
        menuPermissionBut.addActionListener(e -> showRoleAssignMenuDialog());
        menuPermissionBut.setForeground(UIManager.getColor("App.accentColor"));
        menuPermissionBut.setIcon(new FlatSVGIcon("icons/xinzeng.svg", 15, 15));

        JButton dataPermissionBut = new JButton("数据权限");
        dataPermissionBut.addActionListener(e -> showDataPermissionPaneDialog());
        dataPermissionBut.setForeground(UIManager.getColor("App.accentColor"));
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
        RoleEditPane roleEditPane = new RoleEditPane();
        roleEditPane.updateData(id);
        int opt = WOptionPane.showOptionDialog(null, roleEditPane, "添加", OK_CANCEL_OPTION, PLAIN_MESSAGE, null, new Object[]{"确定", "取消"}, "确定");
        if (opt == 0) {
            add(roleEditPane.getValue());
        }
    }

    private void showEditDialog() {


        int selRow = table.getSelectedRow();
        Long userId = null;
        if (selRow != -1) {
            userId = Convert.toLong(table.getValueAt(selRow, 0));
        }

        RoleEditPane roleEditPane = new RoleEditPane();
        roleEditPane.updateData(userId);
        int opt = WOptionPane.showOptionDialog(null, roleEditPane, "修改", OK_CANCEL_OPTION, PLAIN_MESSAGE, null, new Object[]{"确定", "取消"}, "确定");
        if (opt == 0) {
            edit(roleEditPane.getValue());
        }
    }

    private void showDataPermissionPaneDialog() {
        int selRow = table.getSelectedRow();
        RoleRespVO roleRespVO = new RoleRespVO();
        if (selRow != -1) {
            roleRespVO = (RoleRespVO) table.getValueAt(selRow, 8);
        }

        DataPermissionPane dataPermissionPane = new DataPermissionPane();
        dataPermissionPane.updateData(roleRespVO);
        int opt = WOptionPane.showOptionDialog(null, dataPermissionPane, "数据权限", OK_CANCEL_OPTION, PLAIN_MESSAGE, null, new Object[]{"确定", "取消"}, "确定");
        if (opt == 0) {
            assignRoleDataScope(dataPermissionPane.getValue());
        }
    }

    private void showRoleAssignMenuDialog() {
        int selRow = table.getSelectedRow();
        RoleRespVO roleRespVO = new RoleRespVO();
        if (selRow != -1) {
            roleRespVO = (RoleRespVO) table.getValueAt(selRow, 8);
        }

        RoleAssignMenuPane roleAssignMenuPane = new RoleAssignMenuPane();
        roleAssignMenuPane.updateData(roleRespVO);
        int opt = WOptionPane.showOptionDialog(null, roleAssignMenuPane, "菜单权限", OK_CANCEL_OPTION, PLAIN_MESSAGE, null, new Object[]{"确定", "取消"}, "确定");
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


    /**
     * 添加
     */
    private void add(RoleSaveReqVO roleSaveReqVO) {

        SwingWorker<CommonResult<Long>, Object> swingWorker = new SwingWorker<CommonResult<Long>, Object>() {
            @Override
            protected CommonResult<Long> doInBackground() throws Exception {
                return Request.connector(RoleFeign.class).createRole(roleSaveReqVO);
            }

            @Override
            protected void done() {
                try {
                    if (get().isSuccess()) {
                        WMessage.showMessageSuccess(MainFrame.getInstance(), "添加成功！");
                        loadTableData();
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
        swingWorker.execute();

    }

    private void edit(RoleSaveReqVO roleSaveReqVO) {


        SwingWorker<CommonResult<Boolean>, Object> swingWorker = new SwingWorker<CommonResult<Boolean>, Object>() {
            @Override
            protected CommonResult<Boolean> doInBackground() throws Exception {
                return Request.connector(RoleFeign.class).updateRole(roleSaveReqVO);
            }

            @Override
            protected void done() {
                try {
                    if (get().isSuccess()) {
                        WMessage.showMessageSuccess(MainFrame.getInstance(), "修改成功！");
                        loadTableData();
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

    private void del() {
        Long id = null;
        String userName = null;

        int selRow = table.getSelectedRow();
        if (selRow != -1) {
            id = Convert.toLong(table.getValueAt(selRow, 0));
            userName = Convert.toStr(table.getValueAt(selRow, 1));
        }

        int opt = WOptionPane.showOptionDialog(this, "是否确定删除[" + userName + "]？", "提示", OK_CANCEL_OPTION, WARNING_MESSAGE, null, null, null);

        if (opt != 0) {
            return;
        }
        Long finalId = id;
        SwingWorker<CommonResult<Boolean>, Object> swingWorker = new SwingWorker<CommonResult<Boolean>, Object>() {
            @Override
            protected CommonResult<Boolean> doInBackground() throws Exception {
                return Request.connector(RoleFeign.class).deleteRole(finalId);
            }

            @Override
            protected void done() {
                try {
                    if (get().isSuccess()) {
                        WMessage.showMessageSuccess(MainFrame.getInstance(), "删除成功！");
                        loadTableData();
                    }
                } catch (Exception e) {

                    throw new RuntimeException(e);
                }
            }
        };
        swingWorker.execute();

    }

    /**
     * 重置pwd
     */
    private void assignRoleMenu(PermissionAssignRoleMenuReqVO permissionAssignRoleMenuReqVO) {
        SwingWorker<CommonResult<Boolean>, Object> swingWorker = new SwingWorker<CommonResult<Boolean>, Object>() {
            @Override
            protected CommonResult<Boolean> doInBackground() throws Exception {
                return Request.connector(PermissionFeign.class).assignRoleMenu(permissionAssignRoleMenuReqVO);
            }

            @Override
            protected void done() {
                try {
                    if (get().isSuccess()) {
                        WMessage.showMessageSuccess(MainFrame.getInstance(), "添加成功！");

                        loadTableData();
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

    private void assignRoleDataScope(PermissionAssignRoleDataScopeReqVO permissionAssignRoleDataScopeReqVO) {

        SwingWorker<CommonResult<Boolean>, Object> swingWorker = new SwingWorker<CommonResult<Boolean>, Object>() {
            @Override
            protected CommonResult<Boolean> doInBackground() throws Exception {
                return Request.connector(PermissionFeign.class).assignRoleDataScope(permissionAssignRoleDataScopeReqVO);
            }

            @Override
            protected void done() {
                try {
                    if (get().isSuccess()) {
                        WMessage.showMessageSuccess(MainFrame.getInstance(), "添加成功！");
                        loadTableData();
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
            String[] dateTimes = new String[2];
            dateTimes[0] = DateUtil.format(startDateTextField.getValue().atTime(0, 0, 0), "yyyy-MM-dd HH:mm:ss");
            dateTimes[1] = DateUtil.format(endDateTextField.getValue().atTime(23, 59, 59), "yyyy-MM-dd HH:mm:ss");
            queryMap.put("createTime", dateTimes);
        }

        SwingWorker<Vector<Vector>, Long> swingWorker = new SwingWorker<Vector<Vector>, Long>() {
            @Override
            protected Vector<Vector> doInBackground() throws Exception {
                CommonResult<PageResult<RoleRespVO>> result = Request.connector(RoleFeign.class).getRolePage(queryMap);

                Vector<Vector> tableData = new Vector<>();


                if (result.isSuccess()) {

                    result.getData().getList().forEach(roleRespVO -> {
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

                    publish(result.getData().getTotal());
                }
                return tableData;
            }


            @Override
            protected void process(List<Long> chunks) {
                chunks.forEach(total -> paginationPane.setTotal(total));
            }

            @Override
            protected void done() {
                try {


                    tableModel.setDataVector(get(), new Vector<>(Arrays.asList(COLUMN_ID)));
                    table.getColumn("操作").setMinWidth(240);
                    table.getColumn("操作").setCellRenderer(new OptButtonTableCellRenderer(creatBar()));
                    table.getColumn("操作").setCellEditor(new OptButtonTableCellEditor(creatBar()));

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
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                }

            }
        };
        swingWorker.execute();

    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner non-commercial license
    private JTextField textField;
    private JScrollPane scrollPane1;
    private JPanel centerPane;
    private JScrollPane scrollPane2;
    private JTable table;
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

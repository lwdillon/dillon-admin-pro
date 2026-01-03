/*
 * Created by JFormDesigner on Thu Jun 13 19:52:21 CST 2024
 */

package com.dillon.lw.view.system.user;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.dillon.lw.SwingExceptionHandler;
import com.dillon.lw.api.system.DeptApi;
import com.dillon.lw.api.system.PermissionApi;
import com.dillon.lw.api.system.UserApi;
import com.dillon.lw.components.*;
import com.dillon.lw.components.notice.WMessage;
import com.dillon.lw.components.table.renderer.OptButtonTableCellEditor;
import com.dillon.lw.components.table.renderer.OptButtonTableCellRenderer;
import com.dillon.lw.framework.common.pojo.PageResult;
import com.dillon.lw.module.system.controller.admin.dept.vo.dept.DeptSimpleRespVO;
import com.dillon.lw.module.system.controller.admin.permission.vo.permission.PermissionAssignUserRoleReqVO;
import com.dillon.lw.module.system.controller.admin.user.vo.user.UserRespVO;
import com.dillon.lw.module.system.controller.admin.user.vo.user.UserSaveReqVO;
import com.dillon.lw.module.system.controller.admin.user.vo.user.UserUpdatePasswordReqVO;
import com.dillon.lw.view.frame.MainFrame;
import com.dtflys.forest.Forest;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.lang3.StringUtils;
import org.jdesktop.swingx.JXTable;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static javax.swing.JOptionPane.*;

/**
 * @author wenli
 */
public class UserManagementPanel extends JPanel {
    private String[] COLUMN_ID = {"用户编号", "用户名称", "用户昵称", "部门", "手机号", "状态", "创建时间", "操作"};

    private DefaultTableModel tableModel;

    private WPaginationPane paginationPane;

    public UserManagementPanel() {
        initComponents();
        initListeners();
        loadTreeData();
        loadTableData();
    }

    private void initComponents() {
        treePane = new WPanel();
        textField = new JTextField();
        scrollPane1 = new WScrollPane();
        tree = new JTree();
        centerPane = new JPanel();
        scrollPane2 = new WScrollPane();
        table = new JXTable(tableModel = new DefaultTableModel());
        toolPane = new WPanel();
        label7 = new JLabel();
        userNameTextField = new JTextField();
        label8 = new JLabel();
        phoneTextField = new JTextField();
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

        //======== treePane ========
        {
            treePane.setPreferredSize(new Dimension(300, 528));
            treePane.setBorder(new EmptyBorder(10, 10, 10, 10));
            treePane.setLayout(new BorderLayout(10, 10));
            treePane.add(textField, BorderLayout.NORTH);

            //======== scrollPane1 ========
            {
                tree.setOpaque(false);
                scrollPane1.setViewportView(tree);
            }
            treePane.add(scrollPane1, BorderLayout.CENTER);
        }
        add(treePane, BorderLayout.WEST);

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
                label7.setText("\u7528\u6237\u540d\u79f0");
                toolPane.add(label7, "cell 0 0");

                //---- userNameTextField ----
                userNameTextField.setColumns(15);
                toolPane.add(userNameTextField, "cell 0 0");

                //---- label8 ----
                label8.setText("\u624b\u673a\u53f7\u7801");
                toolPane.add(label8, "cell 0 0");

                //---- phoneTextField ----
                phoneTextField.setColumns(15);
                toolPane.add(phoneTextField, "cell 0 0");

                //---- label9 ----
                label9.setText("\u72b6\u6001");
                toolPane.add(label9, "cell 0 0");
                toolPane.add(stautsComboBox, "cell 0 0");

                //---- label10 ----
                label10.setText("\u521b\u5efa\u65f6\u95f4");
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
        tree.setRowHeight(40);
        tree.setCellRenderer(new DefaultTreeCellRenderer() {
            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                if (value instanceof DefaultMutableTreeNode) {
                    Object userObject = ((DefaultMutableTreeNode) value).getUserObject();

                    if (userObject instanceof DeptSimpleRespVO) {
                        value = ((DeptSimpleRespVO) userObject).getName();
                    }
                }
                return super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
            }
        });

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
        edit.setForeground(UIManager.getColor("App.accent.color"));
        edit.setIcon(new FlatSVGIcon("icons/xiugai.svg", 15, 15));
        edit.addActionListener(e -> showEditDialog());
        edit.setForeground(UIManager.getColor("App.accent.color"));

        JButton del = new JButton("删除");
        del.setIcon(new FlatSVGIcon("icons/delte.svg", 15, 15));
        del.addActionListener(e -> delMenu());
        del.setForeground(UIManager.getColor("App.danger.color"));
        JButton restBut = new JButton("重置密码");
        restBut.addActionListener(e -> resetPwd());
        restBut.setForeground(UIManager.getColor("App.accent.color"));
        restBut.setIcon(new FlatSVGIcon("icons/xinzeng.svg", 15, 15));

        JButton roleBut = new JButton("分配角色");
        roleBut.addActionListener(e -> showPermissionAssignUserRoleDialog());
        roleBut.setForeground(UIManager.getColor("App.accent.color"));
        roleBut.setIcon(new FlatSVGIcon("icons/xinzeng.svg", 15, 15));


        optBar.add(edit);
        optBar.add(del);
        optBar.add(restBut);
        optBar.add(roleBut);
        optBar.setPreferredSize(new Dimension(210, 45));
        return optBar;

    }

    private void initListeners() {
        tree.addTreeSelectionListener(e -> {
            loadTableData();
        });

        reseBut.addActionListener(e -> reset());
        searchBut.addActionListener(e -> loadTableData());
        newBut.addActionListener(e -> showAddDialog(null));
    }

    private void reset() {
        userNameTextField.setText("");
        phoneTextField.setText("");
        stautsComboBox.setSelectedIndex(0);
        startDateTextField.setValue(null);
        endDateTextField.setValue(null);
    }

    private void showAddDialog(Long id) {
        UserEditPane userEditPane = new UserEditPane();
        userEditPane.updateData(id);
        int opt = WOptionPane.showOptionDialog(null, userEditPane, "添加", OK_CANCEL_OPTION, PLAIN_MESSAGE, null, new Object[]{"确定", "取消"}, "确定");
        if (opt == 0) {
            add(userEditPane.getValue());
        }
    }

    private void showEditDialog() {


        int selRow = table.getSelectedRow();
        Long userId = null;
        if (selRow != -1) {
            userId = Convert.toLong(table.getValueAt(selRow, 0));
        }

        UserEditPane userEditPane = new UserEditPane();
        userEditPane.updateData(userId);
        int opt = WOptionPane.showOptionDialog(null, userEditPane, "修改", OK_CANCEL_OPTION, PLAIN_MESSAGE, null, new Object[]{"确定", "取消"}, "确定");
        if (opt == 0) {
            edit(userEditPane.getValue());
        }
    }

    @Override
    public void updateUI() {
        super.updateUI();
        if (table != null) {
            table.setDefaultRenderer(Object.class, new CenterTableCellRenderer());
        }
    }

    private void showPermissionAssignUserRoleDialog() {
        int selRow = table.getSelectedRow();
        Long userId = null;
        if (selRow != -1) {
            userId = Convert.toLong(table.getValueAt(selRow, 0));
        }

        AssignRolesPane assignRolesPane = new AssignRolesPane();
        assignRolesPane.updateData(Convert.toStr(table.getValueAt(selRow, 1)), Convert.toStr(table.getValueAt(selRow, 2)), userId);
        int opt = WOptionPane.showOptionDialog(null, assignRolesPane, "分配角色", OK_CANCEL_OPTION, PLAIN_MESSAGE, null, new Object[]{"确定", "取消"}, "确定");
        if (opt == 0) {
            permissionAssignUserRole(assignRolesPane.getValue());
        }
    }

    /**
     * 添加
     */
    private void add(UserSaveReqVO userSaveReqVO) {
        CompletableFuture.runAsync(() -> {
            Forest.client(UserApi.class).createUser(userSaveReqVO).getCheckedData();
        }).thenAcceptAsync(unused -> {
            WMessage.showMessageSuccess(MainFrame.getInstance(), "添加用户成功");
            loadTableData();
        }, SwingUtilities::invokeLater).exceptionally(throwable -> {
            SwingUtilities.invokeLater(() -> {
                SwingExceptionHandler.handle(throwable);
            });
            return null;
        });
    }

    private void edit(UserSaveReqVO userSaveReqVO) {
        CompletableFuture.runAsync(() -> {
            Forest.client(UserApi.class).updateUser(userSaveReqVO).getCheckedData();
        }).thenAcceptAsync(unused -> {
            WMessage.showMessageSuccess(MainFrame.getInstance(), "修改用户成功");
            loadTableData();
        }, SwingUtilities::invokeLater).exceptionally(throwable -> {
            SwingUtilities.invokeLater(() -> {
                SwingExceptionHandler.handle(throwable);
            });
            return null;
        });
    }

    private void delMenu() {
        Long userId = null;
        String userName = null;

        int selRow = table.getSelectedRow();
        if (selRow != -1) {
            userId = Convert.toLong(table.getValueAt(selRow, 0));
            userName = Convert.toStr(table.getValueAt(selRow, 1));
        }

        int opt = WOptionPane.showOptionDialog(this, "是否确定删除[" + userName + "]？", "提示", OK_CANCEL_OPTION, WARNING_MESSAGE, null, null, null);

        if (opt != 0) {
            return;
        }
        Long finalUserId = userId;
        CompletableFuture.runAsync(() -> {
            Forest.client(UserApi.class).deleteUser(finalUserId).getCheckedData();
        }).thenAcceptAsync(unused -> {
            WMessage.showMessageSuccess(MainFrame.getInstance(), "删除用户成功");
            loadTableData();
        }, SwingUtilities::invokeLater).exceptionally(throwable -> {
            SwingUtilities.invokeLater(() -> {
                SwingExceptionHandler.handle(throwable);
            });
            return null;
        });
    }

    /**
     * 重置pwd
     */
    private void resetPwd() {
        Long id = Convert.toLong(table.getValueAt(table.getSelectedRow(), 0));
        String pwd = WOptionPane.showInputDialog(this, "请输入【" + table.getValueAt(table.getSelectedRow(), 2) + "】的密码", "重置密码", INFORMATION_MESSAGE);
        if (StringUtils.isBlank(pwd)) {
            return;
        }
        UserUpdatePasswordReqVO userUpdatePasswordReqVO = new UserUpdatePasswordReqVO();
        userUpdatePasswordReqVO.setId(id);
        userUpdatePasswordReqVO.setPassword(pwd);

        CompletableFuture.runAsync(() -> {
            Forest.client(UserApi.class).updateUserPassword(userUpdatePasswordReqVO).getCheckedData();
        }).thenAcceptAsync(unused -> {
            WMessage.showMessageSuccess(MainFrame.getInstance(), "重置密码成功");
        }, SwingUtilities::invokeLater).exceptionally(throwable -> {
            SwingUtilities.invokeLater(() -> {
                SwingExceptionHandler.handle(throwable);
            });
            return null;
        });
    }

    private void permissionAssignUserRole(PermissionAssignUserRoleReqVO permissionAssignUserRoleReqVO) {
        CompletableFuture.runAsync(() -> {
            Forest.client(PermissionApi.class).assignUserRole(permissionAssignUserRoleReqVO).getCheckedData();
        }).thenAcceptAsync(unused -> {
            WMessage.showMessageSuccess(MainFrame.getInstance(), "分配角色成功");
            loadTableData();
        }, SwingUtilities::invokeLater).exceptionally(throwable -> {
            SwingUtilities.invokeLater(() -> {
                SwingExceptionHandler.handle(throwable);
            });
            return null;
        });
    }

    public void loadTreeData() {
        CompletableFuture.supplyAsync(() -> {
            return Forest.client(DeptApi.class).getSimpleDeptList().getCheckedData();
        }).thenAcceptAsync(deptSimpleRespVOS -> {
            updateTreeUI(deptSimpleRespVOS);
        }, SwingUtilities::invokeLater).exceptionally(throwable -> {
            SwingUtilities.invokeLater(() -> {
                SwingExceptionHandler.handle(throwable);
            });
            return null;
        });
    }

    @NotNull
    private void updateTreeUI(List<DeptSimpleRespVO> result) {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("主类目");
        // Build the tree
        Map<Long, DefaultMutableTreeNode> nodeMap = new HashMap<>();
        nodeMap.put(0L, root); // Root node


        for (DeptSimpleRespVO menu : result) {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(menu);

            nodeMap.put(menu.getId(), node);
        }

        result.forEach(menuSimpleRespVO -> {
            DefaultMutableTreeNode parentNode = nodeMap.get(menuSimpleRespVO.getParentId());
            DefaultMutableTreeNode childNode = nodeMap.get(menuSimpleRespVO.getId());
            if (parentNode != null) {

                parentNode.add(childNode);
            }
        });

        tree.setModel(new DefaultTreeModel(root));
    }

    public void loadTableData() {
        // 获取选中的节点
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

        Long deptId = null;
        if (selectedNode != null) {
            // 获取节点的数据
            Object nodeInfo = selectedNode.getUserObject();
            if (nodeInfo instanceof DeptSimpleRespVO) {
                deptId = ((DeptSimpleRespVO) nodeInfo).getId();
            }
        }
        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("pageNo", paginationPane.getPageIndex());
        queryMap.put("pageSize", paginationPane.getPageSize());

        if (ObjectUtil.isNotEmpty(deptId)) {
            queryMap.put("deptId", deptId);
        }

        if (StrUtil.isNotBlank(userNameTextField.getText())) {
            queryMap.put("username", userNameTextField.getText());
        }
        if (StrUtil.isNotBlank(phoneTextField.getText())) {
            queryMap.put("mobile", phoneTextField.getText());
        }
        queryMap.put("status", stautsComboBox.getSelectedIndex() == 0 ? null : (stautsComboBox.getSelectedIndex() == 1 ? 0 : 1));

        if (ObjectUtil.isAllNotEmpty(startDateTextField.getValue(), endDateTextField.getValue())) {
            String[] dateTimes = new String[2];
            dateTimes[0] = DateUtil.format(startDateTextField.getValue().atTime(0, 0, 0), "yyyy-MM-dd HH:mm:ss");
            dateTimes[1] = DateUtil.format(endDateTextField.getValue().atTime(23, 59, 59), "yyyy-MM-dd HH:mm:ss");
            queryMap.put("createTime", dateTimes);
        }

        // 过滤掉 null 值
        queryMap.entrySet().removeIf(entry -> entry.getValue() == null);
        CompletableFuture.supplyAsync(() -> {
            return Forest.client(UserApi.class).getUserPage(queryMap).getCheckedData();
        }).thenAcceptAsync(result -> {
            updateTableDataUI(result);
        }, SwingUtilities::invokeLater).exceptionally(throwable -> {
            SwingUtilities.invokeLater(() -> {
                SwingExceptionHandler.handle(throwable);
            });
            return null;
        });

    }

    private void updateTableDataUI(PageResult<UserRespVO> pageResult) {
        Vector<Vector> tableData = new Vector<>();
        paginationPane.setTotal(pageResult.getTotal());
        pageResult.getList().forEach(userRespVO -> {
            Vector rowV = new Vector();
            rowV.add(userRespVO.getId());
            rowV.add(userRespVO.getUsername());
            rowV.add(userRespVO.getNickname());
            rowV.add(userRespVO.getDeptName());
            rowV.add(userRespVO.getMobile());
            rowV.add(userRespVO.getStatus());
            rowV.add(DateUtil.format(userRespVO.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
            rowV.add(userRespVO);
            tableData.add(rowV);
        });
        tableModel.setDataVector(tableData, new Vector<>(Arrays.asList(COLUMN_ID)));
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

    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner non-commercial license
    private JPanel treePane;
    private JTextField textField;
    private JScrollPane scrollPane1;
    private JTree tree;
    private JPanel centerPane;
    private JScrollPane scrollPane2;
    private JTable table;
    private JPanel toolPane;
    private JLabel label7;
    private JTextField userNameTextField;
    private JLabel label8;
    private JTextField phoneTextField;
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

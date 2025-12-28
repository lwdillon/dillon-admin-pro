/*
 * Created by JFormDesigner on Fri Jun 14 15:12:51 CST 2024
 */

package com.dillon.lw.view.system.role;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.dillon.lw.api.system.DeptApi;
import com.dillon.lw.SwingExceptionHandler;
import com.dillon.lw.module.system.controller.admin.dept.vo.dept.DeptSimpleRespVO;
import com.dillon.lw.module.system.controller.admin.dict.vo.data.DictDataSimpleRespVO;
import com.dillon.lw.module.system.controller.admin.permission.vo.permission.PermissionAssignRoleDataScopeReqVO;
import com.dillon.lw.module.system.controller.admin.permission.vo.role.RoleRespVO;
import com.dillon.lw.module.system.enums.permission.DataScopeEnum;
import com.dillon.lw.store.AppStore;
import com.dillon.lw.utils.DictTypeEnum;
import com.dtflys.forest.Forest;
import com.jidesoft.swing.CheckBoxTree;
import com.jidesoft.tree.TreeUtils;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.List;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * @author wenli
 */
public class DataPermissionPane extends JPanel {
    private Long id;

    public DataPermissionPane() {
        initComponents();
        initListeners();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner non-commercial license
        label1 = new JLabel();
        nameTextField = new JTextField();
        label2 = new JLabel();
        codeTextField = new JTextField();
        label3 = new JLabel();
        perComboBox = new JComboBox();
        treePane = new JPanel();
        toolBar = new JToolBar();
        allCheckBox = new JCheckBox();
        unfoldCheckBox = new JCheckBox();
        scrollPane1 = new JScrollPane();
        deptTree = new CheckBoxTree();

        //======== this ========
        setLayout(new MigLayout(
            "fill,hidemode 3",
            // columns
            "[right]" +
            "[380:n,grow,fill]",
            // rows
            "[]" +
            "[]" +
            "[]" +
            "[320:320,grow]"));

        //---- label1 ----
        label1.setText("\u89d2\u8272\u540d\u79f0");
        add(label1, "cell 0 0");

        //---- nameTextField ----
        nameTextField.setEditable(false);
        add(nameTextField, "cell 1 0");

        //---- label2 ----
        label2.setText("\u89d2\u8272\u6807\u8bc6");
        add(label2, "cell 0 1");

        //---- codeTextField ----
        codeTextField.setEditable(false);
        add(codeTextField, "cell 1 1");

        //---- label3 ----
        label3.setText("\u6743\u9650\u8303\u56f4");
        add(label3, "cell 0 2");
        add(perComboBox, "cell 1 2");

        //======== treePane ========
        {
            treePane.setLayout(new BorderLayout());

            //======== toolBar ========
            {
                toolBar.setFloatable(false);

                //---- allCheckBox ----
                allCheckBox.setText("\u5168\u9009/\u5168\u4e0d\u9009");
                toolBar.add(allCheckBox);

                //---- unfoldCheckBox ----
                unfoldCheckBox.setText("\u5168\u90e8\u5c55\u5f00/\u6298\u53e0");
                toolBar.add(unfoldCheckBox);
            }
            treePane.add(toolBar, BorderLayout.NORTH);

            //======== scrollPane1 ========
            {
                scrollPane1.setViewportView(deptTree);
            }
            treePane.add(scrollPane1, BorderLayout.CENTER);
        }
        add(treePane, "cell 1 3,grow");
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on

        deptTree.setCellRenderer(new DefaultTreeCellRenderer() {
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
        List<DictDataSimpleRespVO> dictDataSimpleRespVOList = AppStore.getDictDataList(DictTypeEnum.SYSTEM_DATA_SCOPE);
        treePane.setVisible(false);
        perComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                if (value instanceof DictDataSimpleRespVO) {
                    value = ((DictDataSimpleRespVO) value).getLabel();
                }
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        });
        for (DictDataSimpleRespVO dictDataSimpleRespVO : dictDataSimpleRespVOList) {
            perComboBox.addItem(dictDataSimpleRespVO);
        }

    }


    private void initListeners() {

        allCheckBox.addActionListener(e -> {
            if (deptTree.getCheckBoxTreeSelectionModel().isDigIn()) {
                if (allCheckBox.isSelected()) {
                    deptTree.getCheckBoxTreeSelectionModel().setSelectionPath(new TreePath(deptTree.getModel().getRoot()));
                } else {
                    deptTree.getCheckBoxTreeSelectionModel().clearSelection();
                }

            }
        });
        unfoldCheckBox.addActionListener(e -> {
            if (unfoldCheckBox.isSelected()) {
                TreeUtils.expandAll(deptTree);
            } else {
                for (int i = deptTree.getRowCount() - 1; i >= 0; i--) {
                    deptTree.collapseRow(i);
                }
            }

        });
        perComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {

                if (perComboBox.getSelectedItem() instanceof DictDataSimpleRespVO) {

                    if (StrUtil.equals(((DictDataSimpleRespVO) perComboBox.getSelectedItem()).getValue(), DataScopeEnum.DEPT_CUSTOM.getScope() + "")) {
                        treePane.setVisible(true);
                    } else {
                        treePane.setVisible(false);
                    }
                }
            }
        });
    }


    public PermissionAssignRoleDataScopeReqVO getValue() {
        PermissionAssignRoleDataScopeReqVO permissionAssignRoleDataScopeReqVO = new PermissionAssignRoleDataScopeReqVO();
        permissionAssignRoleDataScopeReqVO.setRoleId(id);
        permissionAssignRoleDataScopeReqVO.setDataScope(Convert.toInt(((DictDataSimpleRespVO) perComboBox.getSelectedItem()).getValue(), 0));
        if (perComboBox.getSelectedIndex() == 1) {

            TreePath[] treePaths = deptTree.getCheckBoxTreeSelectionModel().getSelectionPaths();
            Set<Long> deptIdlist = new HashSet<>();


            if (treePaths != null) {
                for (TreePath path : treePaths) {

                    Object obj = path.getLastPathComponent();
                    if (obj instanceof DefaultMutableTreeNode) {
                        DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) obj;
                        treeDataScopeDeptIds(treeNode, deptIdlist);
                    }
                }
                permissionAssignRoleDataScopeReqVO.setDataScopeDeptIds(deptIdlist);
            }
        }

        return permissionAssignRoleDataScopeReqVO;
    }


    private void treeDataScopeDeptIds(DefaultMutableTreeNode treeNode, Set<Long> deptIdlist) {


        addDeptIdIfApplicable(treeNode, deptIdlist);


        // 处理子节点
        for (int i = 0; i < treeNode.getChildCount(); i++) {
            DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) treeNode.getChildAt(i);
            treeDataScopeDeptIds(childNode, deptIdlist);
        }

        // 处理父节点
        while (treeNode.getParent() != null) {
            treeNode = (DefaultMutableTreeNode) treeNode.getParent();
            addDeptIdIfApplicable(treeNode, deptIdlist);
        }
    }

    // 将添加菜单ID的逻辑提取成一个方法
    private void addDeptIdIfApplicable(DefaultMutableTreeNode treeNode, Set<Long> deptIdList) {
        if (treeNode.getUserObject() instanceof DeptSimpleRespVO) {
            DeptSimpleRespVO respVO = (DeptSimpleRespVO) treeNode.getUserObject();
            deptIdList.add(respVO.getId());
        }
    }

    public void updateData(RoleRespVO roleRespVO) {

        this.id = roleRespVO.getId();

        nameTextField.setText(roleRespVO.getName());
        codeTextField.setText(roleRespVO.getCode());
        List<DictDataSimpleRespVO> dictDataSimpleRespVOList = AppStore.getDictDataList(DictTypeEnum.SYSTEM_DATA_SCOPE);

        for (DictDataSimpleRespVO dictDataSimpleRespVO : dictDataSimpleRespVOList) {
            if (StrUtil.equals(dictDataSimpleRespVO.getValue(), roleRespVO.getDataScope() + "")) {
                perComboBox.setSelectedItem(dictDataSimpleRespVO);
                break;
            }

        }

        CompletableFuture.supplyAsync(() -> {
            return Forest.client(DeptApi.class).getSimpleDeptList().getCheckedData();
        }).thenAcceptAsync(deptResult -> {
            DefaultMutableTreeNode deptRoot = new DefaultMutableTreeNode("全部");
            // Build the tree
            Map<Long, DefaultMutableTreeNode> nodeMap = new HashMap<>();
            nodeMap.put(0L, deptRoot); // Root node

            List<DefaultMutableTreeNode> selNodes = new ArrayList<>();
            for (DeptSimpleRespVO simpleRespVO : deptResult) {
                DefaultMutableTreeNode node = new DefaultMutableTreeNode(simpleRespVO);
                nodeMap.put(simpleRespVO.getId(), node);
                if (roleRespVO.getDataScopeDeptIds().contains(simpleRespVO.getId())) {
                    selNodes.add(node);
                }
            }

            deptResult.forEach(deptSimpleRespVO -> {
                DefaultMutableTreeNode parentNode = nodeMap.get(deptSimpleRespVO.getParentId());
                DefaultMutableTreeNode childNode = nodeMap.get(deptSimpleRespVO.getId());
                if (parentNode != null) {
                    parentNode.add(childNode);
                }
            });
            if (selNodes != null) {
                for (DefaultMutableTreeNode node : selNodes) {
                    if (node.isLeaf()) {
                        deptTree.getCheckBoxTreeSelectionModel().addSelectionPath(new TreePath(node.getPath()));
                    }

                }
            }
            TreeUtils.expandAll(deptTree);
            TreeUtils.expandAll(deptTree);

        }, SwingUtilities::invokeLater).exceptionally(throwable -> {
            SwingUtilities.invokeLater(() -> {
                SwingExceptionHandler.handle(throwable);
            });
            return null;
        });


    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner non-commercial license
    private JLabel label1;
    private JTextField nameTextField;
    private JLabel label2;
    private JTextField codeTextField;
    private JLabel label3;
    private JComboBox perComboBox;
    private JPanel treePane;
    private JToolBar toolBar;
    private JCheckBox allCheckBox;
    private JCheckBox unfoldCheckBox;
    private JScrollPane scrollPane1;
    private CheckBoxTree deptTree;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}

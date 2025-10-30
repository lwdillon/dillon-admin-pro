/*
 * Created by JFormDesigner on Sun Jun 16 12:53:18 CST 2024
 */

package com.lw.swing.view.system.role;

import com.jidesoft.swing.CheckBoxTree;
import com.jidesoft.tree.TreeUtils;
import com.lw.dillon.admin.module.system.controller.admin.permission.vo.menu.MenuSimpleRespVO;
import com.lw.dillon.admin.module.system.controller.admin.permission.vo.permission.PermissionAssignRoleMenuReqVO;
import com.lw.dillon.admin.module.system.controller.admin.permission.vo.role.RoleRespVO;
import com.lw.swing.http.PayLoad;
import com.lw.swing.http.RetrofitServiceManager;
import com.lw.ui.api.system.MenuApi;
import com.lw.ui.api.system.PermissionApi;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * @author wenli
 */
public class RoleAssignMenuPane extends JPanel {
    private Long id;

    public RoleAssignMenuPane() {
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
        treePane = new JPanel();
        toolBar = new JToolBar();
        allCheckBox = new JCheckBox();
        unfoldCheckBox = new JCheckBox();
        scrollPane1 = new JScrollPane();
        menuTree = new CheckBoxTree();

        //======== this ========
        setLayout(new MigLayout(
            "fill,hidemode 3",
            // columns
            "[fill]" +
            "[320:320,grow,shrink 0,fill]",
            // rows
            "[]" +
            "[]" +
            "[480:480,grow,shrink 0]"));

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
        label3.setText("\u83dc\u5355\u6743\u9650 ");
        add(label3, "cell 0 2,aligny top,growy 0");

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
                scrollPane1.setViewportView(menuTree);
            }
            treePane.add(scrollPane1, BorderLayout.CENTER);
        }
        add(treePane, "cell 1 2,grow");
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
        menuTree.setCellRenderer(new DefaultTreeCellRenderer() {
            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                if (value instanceof DefaultMutableTreeNode) {
                    Object userObject = ((DefaultMutableTreeNode) value).getUserObject();

                    if (userObject instanceof MenuSimpleRespVO) {
                        value = ((MenuSimpleRespVO) userObject).getName();
                    }
                }
                return super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
            }
        });
    }


    private void initListeners() {

        allCheckBox.addActionListener(e -> {
            if (menuTree.getCheckBoxTreeSelectionModel().isDigIn()) {
                if (allCheckBox.isSelected()) {
                    menuTree.getCheckBoxTreeSelectionModel().setSelectionPath(new TreePath(menuTree.getModel().getRoot()));
                } else {
                    menuTree.getCheckBoxTreeSelectionModel().clearSelection();
                }

            }
        });
        unfoldCheckBox.addActionListener(e -> {
            if (unfoldCheckBox.isSelected()) {
                TreeUtils.expandAll(menuTree);
            } else {
                for (int i = menuTree.getRowCount() - 1; i >= 0; i--) {
                    menuTree.collapseRow(i);
                }
            }

        });

    }

    public PermissionAssignRoleMenuReqVO getValue() {
        PermissionAssignRoleMenuReqVO permissionAssignRoleMenuReqVO = new PermissionAssignRoleMenuReqVO();
        permissionAssignRoleMenuReqVO.setRoleId(id);
        TreePath[] treePaths = menuTree.getCheckBoxTreeSelectionModel().getSelectionPaths();
        Set<Long> menuIdlist = new HashSet<>();


        if (treePaths != null) {
            for (TreePath path : treePaths) {

                Object obj = path.getLastPathComponent();
                if (obj instanceof DefaultMutableTreeNode) {
                    DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) obj;
                    treeDataScopeDeptIds(treeNode, menuIdlist);
                }
            }
            permissionAssignRoleMenuReqVO.setMenuIds(menuIdlist);
        }

        return permissionAssignRoleMenuReqVO;
    }

    private void treeDataScopeDeptIds(DefaultMutableTreeNode treeNode, Set<Long> menuIdlist) {


        // 处理当前节点
        addMenuIdIfApplicable(treeNode, menuIdlist);

        // 处理子节点
        for (int i = 0; i < treeNode.getChildCount(); i++) {
            DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) treeNode.getChildAt(i);
            treeDataScopeDeptIds(childNode, menuIdlist);
        }

        // 处理父节点
        while (treeNode.getParent() != null) {
            treeNode = (DefaultMutableTreeNode) treeNode.getParent();
            addMenuIdIfApplicable(treeNode, menuIdlist);
        }
    }
    // 将添加菜单ID的逻辑提取成一个方法
    private void addMenuIdIfApplicable(DefaultMutableTreeNode treeNode, Set<Long> menuIdList) {
        if (treeNode.getUserObject() instanceof MenuSimpleRespVO) {
            MenuSimpleRespVO menuSimpleRespVO = (MenuSimpleRespVO) treeNode.getUserObject();
            menuIdList.add(menuSimpleRespVO.getId());
        }
    }

    public void updateData(RoleRespVO roleRespVO) {

        this.id = roleRespVO.getId();

        nameTextField.setText(roleRespVO.getName());
        codeTextField.setText(roleRespVO.getCode());

        MenuApi menuApi=   RetrofitServiceManager.getInstance().create(MenuApi.class);
        PermissionApi permissionApi=   RetrofitServiceManager.getInstance().create(PermissionApi.class);


        Observable.zip(
                        permissionApi.getRoleMenuList(id).map(new PayLoad<>()),
                        menuApi.getSimpleMenuList().map(new PayLoad<>()),
                        (permissionSet, menuList) -> {
                            // 使用 LinkedHashMap 保持顺序
                            Map<String, Object> resultMap = new LinkedHashMap<>();
                            resultMap.put("permissionSet", permissionSet);
                            resultMap.put("menuList", menuList);
                            return resultMap;
                        }
                )
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.from(SwingUtilities::invokeLater))
                .subscribe(resultMap -> {
                    // 直接获取数据，无需强转
                    Set<Long> permissionSet = (Set<Long>) resultMap.get("permissionSet");
                    List<MenuSimpleRespVO> menuList = (List<MenuSimpleRespVO>) resultMap.get("menuList");

                    DefaultMutableTreeNode menuRoot = new DefaultMutableTreeNode("全部");
                    // Build the tree
                    Map<Long, DefaultMutableTreeNode> nodeMap = new HashMap<>();
                    nodeMap.put(0L, menuRoot); // Root node

                    java.util.List<DefaultMutableTreeNode> selNodes = new ArrayList<>();
                    for (MenuSimpleRespVO simpleRespVO :menuList) {

                        DefaultMutableTreeNode node = new DefaultMutableTreeNode(simpleRespVO);
                        nodeMap.put(simpleRespVO.getId(), node);

                        if (permissionSet.contains(simpleRespVO.getId())) {
                            selNodes.add(node);
                        }
                    }

                    menuList.forEach(menuSimpleRespVO -> {

                        DefaultMutableTreeNode parentNode = nodeMap.get(menuSimpleRespVO.getParentId());
                        DefaultMutableTreeNode childNode = nodeMap.get(menuSimpleRespVO.getId());
                        if (parentNode != null) {
                            if (childNode != null) {
                                parentNode.add(childNode);
                            }
                        }
                    });

                    menuTree.setModel(new DefaultTreeModel(menuRoot));
                    if (selNodes != null) {
                        for (DefaultMutableTreeNode node : selNodes) {
                            if (node.isLeaf()) {
                                menuTree.getCheckBoxTreeSelectionModel().addSelectionPath(new TreePath(node.getPath()));
                            }

                        }
                    }
                    TreeUtils.expandAll(menuTree);

                }, Throwable::printStackTrace);


    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner non-commercial license
    private JLabel label1;
    private JTextField nameTextField;
    private JLabel label2;
    private JTextField codeTextField;
    private JLabel label3;
    private JPanel treePane;
    private JToolBar toolBar;
    private JCheckBox allCheckBox;
    private JCheckBox unfoldCheckBox;
    private JScrollPane scrollPane1;
    private CheckBoxTree menuTree;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}

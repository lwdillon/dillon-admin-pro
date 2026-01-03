/*
 * Created by JFormDesigner on Sat Jun 08 00:09:10 CST 2024
 */

package com.dillon.lw.view.system.menu;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import com.dillon.lw.SwingExceptionHandler;
import com.dillon.lw.api.system.MenuApi;
import com.dillon.lw.module.system.controller.admin.permission.vo.menu.MenuRespVO;
import com.dillon.lw.module.system.controller.admin.permission.vo.menu.MenuSaveVO;
import com.dillon.lw.module.system.controller.admin.permission.vo.menu.MenuSimpleRespVO;
import com.dtflys.forest.Forest;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import net.miginfocom.swing.MigLayout;
import org.jdesktop.swingx.JXTree;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * @author wenli
 */
public class MenuEditPane extends JPanel {
    private JXTree menuTree;
    private JPopupMenu popupMenu;

    private Long id = null;
    private Long parentId = null;

    public MenuEditPane() {
        initComponents();
        initListeners();
        setOpaque(false);
    }

    private void initComponents() {
        menuTree = new JXTree();
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
        popupMenu = new JPopupMenu();
        popupMenu.add(new JScrollPane(menuTree));

        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner non-commercial license
        label1 = new JLabel();
        parentIdTextField = new JTextField();
        label2 = new JLabel();
        nameTextField = new JTextField();
        label3 = new JLabel();
        panel6 = new JPanel();
        typeDirRadioButton = new JRadioButton();
        typeDirRadioButton.setSelected(true);
        typeMenuRadioButton = new JRadioButton();
        typeButRadioButton = new JRadioButton();
        label4 = new JLabel();
        iconTextField = new JTextField();
        label5 = new JLabel();
        label14 = new JLabel();
        pathTextField = new JTextField();
        label6 = new JLabel();
        componentTextField = new JTextField();
        label7 = new JLabel();
        componentNameTextField = new JTextField();
        label8 = new JLabel();
        label15 = new JLabel();
        permissionTextField = new JTextField();
        label9 = new JLabel();
        sortSpinner = new JSpinner();
        label10 = new JLabel();
        statusCheckBox = new JCheckBox();
        label11 = new JLabel();
        label16 = new JLabel();
        visibleCheckBox = new JCheckBox();
        label12 = new JLabel();
        label17 = new JLabel();
        alwaysShowCheckBox = new JCheckBox();
        label13 = new JLabel();
        label18 = new JLabel();
        keepAliveCheckBox = new JCheckBox();

        //======== this ========
        setMinimumSize(new Dimension(600, 558));
        setLayout(new MigLayout(
            "fill,hidemode 3",
            // columns
            "[right]" +
            "[400:400,grow,shrink 0,fill]",
            // rows
            "[sizegroup 1]" +
            "[sizegroup 1]" +
            "[sizegroup 1]" +
            "[sizegroup 1]" +
            "[sizegroup 1]" +
            "[sizegroup 1]" +
            "[sizegroup 1]" +
            "[sizegroup 1]" +
            "[sizegroup 1]" +
            "[sizegroup 1]" +
            "[sizegroup 1]" +
            "[sizegroup 1]" +
            "[sizegroup 1]"));

        //---- label1 ----
        label1.setText("\u4e0a\u7ea7\u83dc\u5355");
        add(label1, "cell 0 0");
        add(parentIdTextField, "cell 1 0");

        //---- label2 ----
        label2.setText("*\u83dc\u5355\u540d\u79f0");
        add(label2, "cell 0 1");
        add(nameTextField, "cell 1 1");

        //---- label3 ----
        label3.setText("\u83dc\u5355\u7c7b\u578b");
        add(label3, "cell 0 2");

        //======== panel6 ========
        {
            panel6.setLayout(new MigLayout(
                "fill,insets 0,hidemode 3",
                // columns
                "[fill]",
                // rows
                "[sizegroup 1]"));

            //---- typeDirRadioButton ----
            typeDirRadioButton.setText("\u76ee\u5f55");
            panel6.add(typeDirRadioButton, "cell 0 0");

            //---- typeMenuRadioButton ----
            typeMenuRadioButton.setText("\u83dc\u5355");
            panel6.add(typeMenuRadioButton, "cell 0 0");

            //---- typeButRadioButton ----
            typeButRadioButton.setText("\u6309\u94ae");
            panel6.add(typeButRadioButton, "cell 0 0");
        }
        add(panel6, "cell 1 2,alignx left,growx 0");

        //---- label4 ----
        label4.setText("\u83dc\u5355\u56fe\u6807");
        add(label4, "cell 0 3");
        add(iconTextField, "cell 1 3");

        //---- label5 ----
        label5.setText("*\u8def\u7531\u5730\u5740");
        add(label5, "cell 0 4");

        //---- label14 ----
        label14.setIcon(new FlatSVGIcon("icons/tishi.svg",16,16));
        label14.setToolTipText("\u8bbf\u95ee\u7684\u8def\u7531\u5730\u5740\uff0c\u5982\uff1a`user`\u3002\u5982\u9700\u5916\u7f51\u5730\u5740\u65f6\uff0c\u5219\u4ee5 `http(s)://` \u5f00\u5934");
        add(label14, "cell 0 4,aligny top,growy 0");
        add(pathTextField, "cell 1 4");

        //---- label6 ----
        label6.setText("\u7ec4\u4ef6\u5730\u5740");
        add(label6, "cell 0 5");
        add(componentTextField, "cell 1 5");

        //---- label7 ----
        label7.setText("\u7ec4\u4ef6\u540d\u5b57");
        add(label7, "cell 0 6");
        add(componentNameTextField, "cell 1 6");

        //---- label8 ----
        label8.setText("\u6743\u9650\u6807\u8bc6");
        add(label8, "cell 0 7");

        //---- label15 ----
        label15.setIcon(new FlatSVGIcon("icons/tishi.svg",16,16));
        label15.setToolTipText("Controller \u65b9\u6cd5\u4e0a\u7684\u6743\u9650\u5b57\u7b26\uff0c\u5982\uff1a@PreAuthorize(`@ss.hasPermission('system:user:list')`)");
        add(label15, "cell 0 7,aligny top,growy 0");
        add(permissionTextField, "cell 1 7");

        //---- label9 ----
        label9.setText("*\u663e\u793a\u6392\u5e8f");
        add(label9, "cell 0 8");
        add(sortSpinner, "cell 1 8");

        //---- label10 ----
        label10.setText("*\u83dc\u5355\u72b6\u6001");
        add(label10, "cell 0 9");

        //---- statusCheckBox ----
        statusCheckBox.setText("\u5f00\u542f");
        add(statusCheckBox, "cell 1 9,alignx left,growx 0");

        //---- label11 ----
        label11.setText("\u663e\u793a\u72b6\u6001");
        add(label11, "cell 0 10");

        //---- label16 ----
        label16.setIcon(new FlatSVGIcon("icons/tishi.svg",16,16));
        label16.setToolTipText("\u9009\u62e9\u9690\u85cf\u65f6\uff0c\u8def\u7531\u5c06\u4e0d\u4f1a\u51fa\u73b0\u5728\u4fa7\u8fb9\u680f\uff0c\u4f46\u4ecd\u7136\u53ef\u4ee5\u8bbf\u95ee");
        add(label16, "cell 0 10,aligny top,growy 0");

        //---- visibleCheckBox ----
        visibleCheckBox.setText("\u663e\u793a");
        add(visibleCheckBox, "cell 1 10,alignx left,growx 0");

        //---- label12 ----
        label12.setText("\u603b\u662f\u663e\u793a ");
        add(label12, "cell 0 11");

        //---- label17 ----
        label17.setIcon(new FlatSVGIcon("icons/tishi.svg",16,16));
        label17.setToolTipText("\u9009\u62e9\u4e0d\u662f\u65f6\uff0c\u5f53\u8be5\u83dc\u5355\u53ea\u6709\u4e00\u4e2a\u5b50\u83dc\u5355\u65f6\uff0c\u4e0d\u5c55\u793a\u81ea\u5df1\uff0c\u76f4\u63a5\u5c55\u793a\u5b50\u83dc\u5355");
        add(label17, "cell 0 11,aligny top,growy 0");

        //---- alwaysShowCheckBox ----
        alwaysShowCheckBox.setText("\u603b\u662f");
        add(alwaysShowCheckBox, "cell 1 11,alignx left,growx 0");

        //---- label13 ----
        label13.setText("\u7f13\u5b58\u72b6\u6001");
        add(label13, "cell 0 12");

        //---- label18 ----
        label18.setIcon(new FlatSVGIcon("icons/tishi.svg",16,16));
        add(label18, "cell 0 12,aligny top,growy 0");

        //---- keepAliveCheckBox ----
        keepAliveCheckBox.setText("\u7f13\u5b58");
        add(keepAliveCheckBox, "cell 1 12,alignx left,growx 0");

        //---- buttonGroup1 ----
        ButtonGroup buttonGroup1 = new ButtonGroup();
        buttonGroup1.add(typeDirRadioButton);
        buttonGroup1.add(typeMenuRadioButton);
        buttonGroup1.add(typeButRadioButton);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
        parentIdTextField.setEditable(false);
    }

    private void initListeners() {
        parentIdTextField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    showPopupMenu();
                }
            }
        });
        menuTree.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) menuTree.getLastSelectedPathComponent();
            if (selectedNode != null) {
                Object nodeInfo = selectedNode.getUserObject();

                if (nodeInfo instanceof MenuSimpleRespVO) {
                    parentId = ((MenuSimpleRespVO) nodeInfo).getId();
                    parentIdTextField.setText(((MenuSimpleRespVO) nodeInfo).getName());
                } else {
                    parentId = 0L;
                    parentIdTextField.setText(nodeInfo + "");
                }
                popupMenu.setVisible(false);

            }

        });


    }

    private void showPopupMenu() {
        popupMenu.setPopupSize(parentIdTextField.getWidth(), 400);
        popupMenu.show(parentIdTextField, 0, parentIdTextField.getHeight());
    }

    public MenuSaveVO getMenuRespVO() {
        MenuSaveVO menuSaveVO = new MenuSaveVO();
        menuSaveVO.setParentId(parentId);
        menuSaveVO.setId(id);
        menuSaveVO.setIcon(iconTextField.getText());
        menuSaveVO.setName(nameTextField.getText());
        menuSaveVO.setStatus(statusCheckBox.isSelected() ? 0 : 1);
        menuSaveVO.setAlwaysShow(alwaysShowCheckBox.isSelected());
        menuSaveVO.setKeepAlive(keepAliveCheckBox.isSelected());
        menuSaveVO.setComponentSwing(componentTextField.getText());
        menuSaveVO.setComponentName(componentNameTextField.getText());
        menuSaveVO.setPath(pathTextField.getText());
        menuSaveVO.setVisible(visibleCheckBox.isSelected());
        menuSaveVO.setPermission(permissionTextField.getText());
        menuSaveVO.setSort(Convert.toInt(sortSpinner.getValue(), 0));
        menuSaveVO.setType(typeDirRadioButton.isSelected() ? 1 : (typeMenuRadioButton.isSelected() ? 2 : 3));
        return menuSaveVO;
    }

    public void setMenuRespVO(MenuRespVO menuRespVO) {
        if (menuRespVO == null) {
            return;
        }
        nameTextField.setText(menuRespVO.getName());
        int menuType = ObjectUtil.defaultIfNull(menuRespVO.getType(),0);
        if (menuType == 1) {
            typeDirRadioButton.setSelected(true);
        } else if (menuType == 2) {
            typeMenuRadioButton.setSelected(true);
        } else {
            typeButRadioButton.setSelected(true);
        }
        iconTextField.setText(menuRespVO.getIcon());
        pathTextField.setText(menuRespVO.getPath());
        componentTextField.setText(menuRespVO.getComponentSwing());
        componentNameTextField.setText(menuRespVO.getComponentName());
        permissionTextField.setText(menuRespVO.getPermission());
        sortSpinner.setValue(ObjectUtil.defaultIfNull(menuRespVO.getSort(),0));
        statusCheckBox.setSelected(ObjectUtil.equals(menuRespVO.getStatus(), 0));
        visibleCheckBox.setSelected(ObjectUtil.defaultIfNull(menuRespVO.getVisible(), true));
        alwaysShowCheckBox.setSelected(ObjectUtil.defaultIfNull(menuRespVO.getAlwaysShow(),true));
        keepAliveCheckBox.setSelected(ObjectUtil.defaultIfNull(menuRespVO.getKeepAlive(),true));
    }


    public void updateData(Long id, boolean isAdd) {

        if (isAdd) {
            this.id = null;
        } else {
            this.id = id;
        }

        // 获取 API 实例
        MenuApi menuApi = Forest.client(MenuApi.class);

        CompletableFuture<MenuRespVO> menuFuture = CompletableFuture.supplyAsync(() -> {
            if (id == null) {
                return new MenuRespVO();
            }
            return menuApi.getMenu(id).getCheckedData();
        });

        CompletableFuture<List<MenuSimpleRespVO>> menuListFuture = CompletableFuture.supplyAsync(() -> {
            return menuApi.getSimpleMenuList().getCheckedData();
        });

        CompletableFuture.allOf(menuFuture, menuListFuture).thenAcceptAsync(v -> {
            try {
                MenuRespVO menuRespVO = menuFuture.get();
                List<MenuSimpleRespVO> menuList = menuListFuture.get();

                setMenuRespVO(menuRespVO);

                DefaultMutableTreeNode root = new DefaultMutableTreeNode("主类目");
                Map<Long, DefaultMutableTreeNode> nodeMap = new HashMap<>();
                for (MenuSimpleRespVO menu : menuList) {
                    nodeMap.put(menu.getId(), new DefaultMutableTreeNode(menu));
                }

                for (MenuSimpleRespVO menu : menuList) {
                    DefaultMutableTreeNode node = nodeMap.get(menu.getId());
                    if (menu.getParentId() == null || menu.getParentId() == 0) {
                        root.add(node);
                    } else {
                        DefaultMutableTreeNode parentNode = nodeMap.get(menu.getParentId());
                        if (parentNode != null) {
                            parentNode.add(node);
                        } else {
                            root.add(node);
                        }
                    }
                }

                menuTree.setModel(new DefaultTreeModel(root));

                if (menuRespVO.getParentId() != null && menuRespVO.getParentId() != 0) {
                    parentId = menuRespVO.getParentId();
                    for (MenuSimpleRespVO menu : menuList) {
                        if (menu.getId().equals(parentId)) {
                            parentIdTextField.setText(menu.getName());
                            break;
                        }
                    }
                } else {
                    parentId = 0L;
                    parentIdTextField.setText("主类目");
                }
            } catch (Exception e) {
                SwingExceptionHandler.handle(e);
            }
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
    private JTextField parentIdTextField;
    private JLabel label2;
    private JTextField nameTextField;
    private JLabel label3;
    private JPanel panel6;
    private JRadioButton typeDirRadioButton;
    private JRadioButton typeMenuRadioButton;
    private JRadioButton typeButRadioButton;
    private JLabel label4;
    private JTextField iconTextField;
    private JLabel label5;
    private JLabel label14;
    private JTextField pathTextField;
    private JLabel label6;
    private JTextField componentTextField;
    private JLabel label7;
    private JTextField componentNameTextField;
    private JLabel label8;
    private JLabel label15;
    private JTextField permissionTextField;
    private JLabel label9;
    private JSpinner sortSpinner;
    private JLabel label10;
    private JCheckBox statusCheckBox;
    private JLabel label11;
    private JLabel label16;
    private JCheckBox visibleCheckBox;
    private JLabel label12;
    private JLabel label17;
    private JCheckBox alwaysShowCheckBox;
    private JLabel label13;
    private JLabel label18;
    private JCheckBox keepAliveCheckBox;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}

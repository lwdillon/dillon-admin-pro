/*
 * Created by JFormDesigner on Sun Jun 16 14:10:58 CST 2024
 */

package com.dillon.lw.view.system.dept;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import com.dillon.lw.module.system.controller.admin.dept.vo.dept.DeptRespVO;
import com.dillon.lw.module.system.controller.admin.dept.vo.dept.DeptSaveReqVO;
import com.dillon.lw.module.system.controller.admin.dept.vo.dept.DeptSimpleRespVO;
import com.dillon.lw.module.system.controller.admin.user.vo.user.UserSimpleRespVO;
import com.dillon.lw.http.PayLoad;
import com.dillon.lw.http.RetrofitServiceManager;
import com.dillon.lw.utils.TreeUtils;
import com.dillon.lw.api.system.DeptApi;
import com.dillon.lw.api.system.UserApi;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import net.miginfocom.swing.MigLayout;
import org.jdesktop.swingx.JXTree;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;

/**
 * @author wenli
 */
public class DeptEditPane extends JPanel {
    private JXTree deptTree;
    private JPopupMenu popupMenu;
    private Long id = null;
    private Long parentId = null;
    private Long leaderUserId = null;

    public DeptEditPane() {
        initComponents();
        initListeners();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner non-commercial license
        label1 = new JLabel();
        parentTextField = new JTextField();
        label2 = new JLabel();
        nameTextField = new JTextField();
        label3 = new JLabel();
        sortSpinner = new JSpinner();
        label4 = new JLabel();
        leaderUserIdComboBox = new JComboBox();
        label5 = new JLabel();
        phoneTextField = new JTextField();
        label6 = new JLabel();
        emailTextField = new JTextField();
        label7 = new JLabel();
        statusComboBox = new JComboBox();

        //======== this ========
        setLayout(new MigLayout(
            "fill,hidemode 3",
            // columns
            "[right]" +
            "[320:n,grow,fill]",
            // rows
            "[]" +
            "[]" +
            "[]" +
            "[]" +
            "[]" +
            "[]" +
            "[]"));

        //---- label1 ----
        label1.setText("*\u4e0a\u7ea7\u90e8\u95e8");
        add(label1, "cell 0 0");
        add(parentTextField, "cell 1 0");

        //---- label2 ----
        label2.setText("*\u90e8\u95e8\u540d\u79f0");
        add(label2, "cell 0 1");
        add(nameTextField, "cell 1 1");

        //---- label3 ----
        label3.setText("*\u663e\u793a\u6392\u5e8f");
        add(label3, "cell 0 2");
        add(sortSpinner, "cell 1 2");

        //---- label4 ----
        label4.setText("\u8d1f\u8d23\u4eba");
        add(label4, "cell 0 3");
        add(leaderUserIdComboBox, "cell 1 3");

        //---- label5 ----
        label5.setText("\u8054\u7cfb\u7535\u8bdd");
        add(label5, "cell 0 4");
        add(phoneTextField, "cell 1 4");

        //---- label6 ----
        label6.setText("\u90ae\u7bb1");
        add(label6, "cell 0 5");
        add(emailTextField, "cell 1 5");

        //---- label7 ----
        label7.setText("*\u72b6\u6001");
        add(label7, "cell 0 6");
        add(statusComboBox, "cell 1 6");
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on

        deptTree = new JXTree();
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
        popupMenu = new JPopupMenu();
        popupMenu.add(new JScrollPane(deptTree));

        leaderUserIdComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                if (value instanceof UserSimpleRespVO) {
                    value = ((UserSimpleRespVO) value).getNickname();
                }
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        });
        statusComboBox.addItem("开启");
        statusComboBox.addItem("关闭");
    }

    private void initListeners() {
        parentTextField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    showPopupMenu();
                }
            }
        });
        deptTree.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) deptTree.getLastSelectedPathComponent();
            if (selectedNode != null) {
                Object nodeInfo = selectedNode.getUserObject();

                if (nodeInfo instanceof DeptSimpleRespVO) {
                    parentId = ((DeptSimpleRespVO) nodeInfo).getId();
                    parentTextField.setText(((DeptSimpleRespVO) nodeInfo).getName());
                } else {
                    parentId = 0L;
                    parentTextField.setText(nodeInfo + "");
                }
                popupMenu.setVisible(false);

            }

        });

    }

    private void showPopupMenu() {
        popupMenu.setPopupSize(parentTextField.getWidth(), 400);
        popupMenu.show(parentTextField, 0, parentTextField.getHeight());
    }

    public DeptSaveReqVO getValue() {
        DeptSaveReqVO saveVO = new DeptSaveReqVO();
        saveVO.setParentId(parentId);
        saveVO.setId(id);
        saveVO.setLeaderUserId(leaderUserIdComboBox.getSelectedItem() instanceof UserSimpleRespVO ? ((UserSimpleRespVO) leaderUserIdComboBox.getSelectedItem()).getId() : null);
        saveVO.setName(nameTextField.getText());
        saveVO.setStatus(statusComboBox.getSelectedIndex());
        saveVO.setSort(Convert.toInt(sortSpinner.getValue(), 0));
        saveVO.setEmail(emailTextField.getText());
        saveVO.setPhone(phoneTextField.getText());

        return saveVO;
    }

    private void setDeptRespVO(DeptRespVO deptRespVO) {

        if (deptRespVO == null) {
            return;
        }

        statusComboBox.setSelectedIndex(ObjectUtil.defaultIfNull(deptRespVO.getStatus(),0));
        sortSpinner.setValue(ObjectUtil.defaultIfNull(deptRespVO.getSort(),0));
        nameTextField.setText(deptRespVO.getName());
        emailTextField.setText(deptRespVO.getEmail());
        phoneTextField.setText(deptRespVO.getPhone());

    }

    private void updateData(Map<String, Object> resultMap, boolean isAdd) {
        DeptRespVO deptRespVO = (DeptRespVO) resultMap.get("deptRespVO");
        List<DeptSimpleRespVO> deptList = (List<DeptSimpleRespVO>) resultMap.get("deptList");
        List<UserSimpleRespVO> userList = (List<UserSimpleRespVO>) resultMap.get("userList");

        setDeptRespVO(deptRespVO);

        DefaultMutableTreeNode selectDept = null;
        Vector leaderUsers = new Vector();
        leaderUsers.add(null);
        UserSimpleRespVO leaderUserSel = null;
        DefaultMutableTreeNode deptRootNode = new DefaultMutableTreeNode("主类目");
        // Build the tree
        Map<Long, DefaultMutableTreeNode> nodeMap = new HashMap<>();
        nodeMap.put(0L, deptRootNode); // Root node


        for (UserSimpleRespVO respVO : userList) {
            leaderUsers.add(respVO);

            if (deptRespVO != null && Objects.equals(deptRespVO.getLeaderUserId(), respVO.getId())) {
                leaderUserSel = respVO;
            }
        }


        for (DeptSimpleRespVO dept : deptList) {

            DefaultMutableTreeNode node = new DefaultMutableTreeNode(dept);
            nodeMap.put(dept.getId(), node);
        }

        deptList.forEach(deptSimpleRespVO -> {

            DefaultMutableTreeNode parentNode = nodeMap.get(deptSimpleRespVO.getParentId());
            DefaultMutableTreeNode childNode = nodeMap.get(deptSimpleRespVO.getId());
            if (parentNode != null) {
                parentNode.add(childNode);
            }


        });


        if (deptRespVO != null) {
            selectDept = nodeMap.get(isAdd ? deptRespVO.getId() : deptRespVO.getParentId());
        }


        deptTree.setModel(new DefaultTreeModel(deptRootNode));
        if (selectDept != null) {
            TreePath path = new TreePath(selectDept.getPath());
            deptTree.setSelectionPath(path);
            deptTree.scrollPathToVisible(path);
            TreeUtils.expandTreeNode(deptTree, selectDept);
        }

        leaderUserIdComboBox.setModel(new DefaultComboBoxModel(leaderUsers));

        if (leaderUserSel != null) {
            leaderUserIdComboBox.setSelectedItem(leaderUserSel);
        }
    }


    public void updateData(Long id, boolean isAdd) {

        if (isAdd) {
            this.id = null;
        } else {
            this.id = id;
        }

        // 获取 API 实例（避免重复调用 getInstance()）
        DeptApi deptApi = RetrofitServiceManager.getInstance().create(DeptApi.class);
        UserApi userApi = RetrofitServiceManager.getInstance().create(UserApi.class);

        Observable.zip(
                        Observable.defer(() -> {
                            if (id == null) {
                                return Observable.just(new DeptRespVO());// 返回一个默认的空对象
                            }
                            return deptApi.getDept(id).map(new PayLoad<>());
                        }),
                        deptApi.getSimpleDeptList().map(new PayLoad<>()),
                        userApi.getSimpleUserList().map(new PayLoad<>()),
                        (deptRespVO, deptList, userList) -> {
                            // 使用 LinkedHashMap 保持顺序
                            Map<String, Object> resultMap = new LinkedHashMap<>();
                            resultMap.put("deptRespVO", deptRespVO);
                            resultMap.put("deptList", deptList);
                            resultMap.put("userList", userList);
                            return resultMap;
                        }
                )
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.from(SwingUtilities::invokeLater))
                .subscribe(resultMap -> {
                    // 直接获取数据，无需强转
                    updateData(resultMap, isAdd);
                }, Throwable::printStackTrace);


    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner non-commercial license
    private JLabel label1;
    private JTextField parentTextField;
    private JLabel label2;
    private JTextField nameTextField;
    private JLabel label3;
    private JSpinner sortSpinner;
    private JLabel label4;
    private JComboBox leaderUserIdComboBox;
    private JLabel label5;
    private JTextField phoneTextField;
    private JLabel label6;
    private JTextField emailTextField;
    private JLabel label7;
    private JComboBox statusComboBox;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}

/*
 * Created by JFormDesigner on Fri Jun 14 09:37:13 CST 2024
 */

package com.lw.swing.view.system.user;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Editor;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import com.jidesoft.swing.CheckBoxList;
import com.lw.dillon.admin.module.system.controller.admin.dept.vo.dept.DeptSimpleRespVO;
import com.lw.dillon.admin.module.system.controller.admin.dept.vo.post.PostSimpleRespVO;
import com.lw.dillon.admin.module.system.controller.admin.user.vo.user.UserRespVO;
import com.lw.dillon.admin.module.system.controller.admin.user.vo.user.UserSaveReqVO;
import com.lw.swing.components.WScrollPane;
import com.lw.swing.http.PayLoad;
import com.lw.swing.http.RetrofitServiceManager;
import com.lw.ui.api.system.DeptApi;
import com.lw.ui.api.system.PostApi;
import com.lw.ui.api.system.UserApi;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
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
public class UserEditPane extends JPanel {

    private Long id;
    private Long deptId = null;
    private JTree deptTree;
    private JPopupMenu deptPopupMenu;
    private JPopupMenu postPopupMenu;
    private CheckBoxList postCheckBoxList;

    public UserEditPane() {
        initComponents();
        initListeners();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner non-commercial license
        contentPanel = new JPanel();
        label1 = new JLabel();
        nicknameTextField = new JTextField();
        label2 = new JLabel();
        deptTextField = new JTextField();
        label3 = new JLabel();
        mobileTextField = new JTextField();
        label4 = new JLabel();
        emailTextField = new JTextField();
        label5 = new JLabel();
        usernameTextField = new JTextField();
        label6 = new JLabel();
        passwordField = new JPasswordField();
        label7 = new JLabel();
        sexComboBox = new JComboBox();
        label8 = new JLabel();
        postTextField = new JTextField();
        label9 = new JLabel();
        scrollPane1 = new JScrollPane();
        remarkTextArea = new JTextArea();

        //======== this ========
        setLayout(new BorderLayout());

        //======== contentPanel ========
        {
            contentPanel.setLayout(new MigLayout(
                "fill,insets dialog,hidemode 3",
                // columns
                "[right]" +
                "[grow,shrink 0,sizegroup 1,fill]15" +
                "[right]" +
                "[180,grow,shrink 0,sizegroup 1,fill]",
                // rows
                "[]" +
                "[]" +
                "[]" +
                "[45!]" +
                "[180,grow,shrink 0]"));

            //---- label1 ----
            label1.setText("*\u7528\u6237\u6635\u79f0");
            contentPanel.add(label1, "cell 0 0");
            contentPanel.add(nicknameTextField, "cell 1 0");

            //---- label2 ----
            label2.setText("\u5f52\u5c5e\u90e8\u95e8");
            contentPanel.add(label2, "cell 2 0");
            contentPanel.add(deptTextField, "cell 3 0");

            //---- label3 ----
            label3.setText("\u624b\u673a\u53f7\u7801");
            contentPanel.add(label3, "cell 0 1");
            contentPanel.add(mobileTextField, "cell 1 1");

            //---- label4 ----
            label4.setText("\u90ae\u7bb1");
            contentPanel.add(label4, "cell 2 1");
            contentPanel.add(emailTextField, "cell 3 1");

            //---- label5 ----
            label5.setText("*\u7528\u6237\u540d\u79f0");
            contentPanel.add(label5, "cell 0 2");
            contentPanel.add(usernameTextField, "cell 1 2");

            //---- label6 ----
            label6.setText("*\u7528\u6237\u5bc6\u7801");
            contentPanel.add(label6, "cell 2 2");
            contentPanel.add(passwordField, "cell 3 2");

            //---- label7 ----
            label7.setText("\u7528\u6237\u6027\u522b");
            contentPanel.add(label7, "cell 0 3");
            contentPanel.add(sexComboBox, "cell 1 3");

            //---- label8 ----
            label8.setText("\u5c97\u4f4d");
            contentPanel.add(label8, "cell 2 3");
            contentPanel.add(postTextField, "cell 3 3");

            //---- label9 ----
            label9.setText("\u5907\u6ce8");
            contentPanel.add(label9, "cell 0 4,aligny top,growy 0");

            //======== scrollPane1 ========
            {
                scrollPane1.setViewportView(remarkTextArea);
            }
            contentPanel.add(scrollPane1, "cell 1 4 3 1,growy");
        }
        add(contentPanel, BorderLayout.CENTER);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on

        deptTree = new JTree();
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
        deptPopupMenu = new JPopupMenu();
        postPopupMenu = new JPopupMenu();
        postCheckBoxList = new CheckBoxList();
        postCheckBoxList.setClickInCheckBoxOnly(false);
        postCheckBoxList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                if (value instanceof PostSimpleRespVO) {
                    value = ((PostSimpleRespVO) value).getName();
                }
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        });
        postCheckBoxList.setFixedCellHeight(35);
        postPopupMenu.add(new WScrollPane(postCheckBoxList));
        deptPopupMenu.add(new WScrollPane(deptTree));
        deptTextField.setEditable(false);
        postTextField.setEditable(false);

        sexComboBox.addItem("男");
        sexComboBox.addItem("女");


    }

    private void initListeners() {
        deptTextField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    showDeptPopupMenu();
                }
            }
        });
        postTextField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    showPostPopupMenu();
                }
            }
        });

        deptTree.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) deptTree.getLastSelectedPathComponent();
            if (selectedNode != null) {
                Object nodeInfo = selectedNode.getUserObject();

                if (nodeInfo instanceof DeptSimpleRespVO) {
                    deptId = ((DeptSimpleRespVO) nodeInfo).getId();
                    deptTextField.setText(((DeptSimpleRespVO) nodeInfo).getName());
                } else {
                    deptId = 0L;
                    deptTextField.setText(nodeInfo + "");
                }
                deptPopupMenu.setVisible(false);
            }
        });


        postCheckBoxList.getCheckBoxListSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    Object[] objects = postCheckBoxList.getCheckBoxListSelectedValues();
                    postTextField.setText(ArrayUtil.join(objects, ",", new Editor<Object>() {
                        @Override
                        public Object edit(Object o) {
                            if (o instanceof PostSimpleRespVO) {
                                return ((PostSimpleRespVO) o).getName();
                            }
                            return o;
                        }
                    }));
                }
            }
        });
    }

    private void showDeptPopupMenu() {
        deptPopupMenu.setPopupSize(deptTextField.getWidth(), 400);
        deptPopupMenu.show(deptTextField, 0, deptTextField.getHeight());
    }

    private void showPostPopupMenu() {
        postPopupMenu.setPopupSize(postTextField.getWidth(), 400);
        postPopupMenu.show(postTextField, 0, postTextField.getHeight());
    }

    public void updateData(Long id) {

        this.id = id;
        if (id != null) {
            label5.setVisible(false);
            label6.setVisible(false);
            usernameTextField.setVisible(false);
            passwordField.setVisible(false);
        } else {
            label5.setVisible(true);
            label6.setVisible(true);
            usernameTextField.setVisible(true);
            passwordField.setVisible(true);
        }

        // 获取 API 实例（避免重复调用 getInstance()）
        UserApi userApi = RetrofitServiceManager.getInstance().create(UserApi.class);
        DeptApi deptApi = RetrofitServiceManager.getInstance().create(DeptApi.class);
        PostApi postApi = RetrofitServiceManager.getInstance().create(PostApi.class);

        Observable.zip(
                        Observable.defer(() -> {
                            if (id == null) {
                                return Observable.just(new UserRespVO());// 返回一个默认的空对象
                            }
                            return userApi.getUser(id).map(new PayLoad<>());
                        }),
                        deptApi.getSimpleDeptList().map(new PayLoad<>()),
                        postApi.getSimplePostList().map(new PayLoad<>()),
                        (userInfo, deptList, postList) -> {
                            // 使用 LinkedHashMap 保持顺序
                            Map<String, Object> resultMap = new LinkedHashMap<>();
                            resultMap.put("userInfo", userInfo);
                            resultMap.put("deptList", deptList);
                            resultMap.put("postList", postList);
                            return resultMap;
                        }
                )
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.from(SwingUtilities::invokeLater))
                .subscribe(resultMap -> {
                    // 直接获取数据，无需强转
                    updateData(resultMap);
                }, Throwable::printStackTrace);


    }

    private void updateData(Map<String, Object> resultMap) {
        UserRespVO userInfo = (UserRespVO) resultMap.get("userInfo");
        List<DeptSimpleRespVO> deptList = (List<DeptSimpleRespVO>) resultMap.get("deptList");
        List<PostSimpleRespVO> postList = (List<PostSimpleRespVO>) resultMap.get("postList");

        DefaultMutableTreeNode deptRoot = new DefaultMutableTreeNode("主类目");
        DefaultMutableTreeNode selectNode = null;
        // Build the tree
        Map<Long, DefaultMutableTreeNode> nodeMap = new HashMap<>();
        nodeMap.put(0L, deptRoot); // Root node


        for (DeptSimpleRespVO simpleRespVO : deptList) {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(simpleRespVO);
            nodeMap.put(simpleRespVO.getId(), node);

            if (userInfo != null) {
                if (ObjectUtil.equals(simpleRespVO.getId(), userInfo.getDeptId())) {
                    selectNode = node;
                }
            }
        }
        deptList.forEach(deptSimpleRespVO -> {
            DefaultMutableTreeNode parentNode = nodeMap.get(deptSimpleRespVO.getParentId());
            DefaultMutableTreeNode childNode = nodeMap.get(deptSimpleRespVO.getId());
            if (parentNode != null) {
                parentNode.add(childNode);
            }
        });

        Vector<Object> selPost = new Vector<>();
        DefaultListModel listModel = new DefaultListModel();

        for (PostSimpleRespVO postSimpleRespVO : postList) {

            listModel.addElement(postSimpleRespVO);

            if (userInfo != null&& CollUtil.isNotEmpty(userInfo.getPostIds())) {

                if (userInfo.getPostIds().contains(postSimpleRespVO.getId())) {
                    selPost.add(postSimpleRespVO);
                }
            }
        }
        setUserRespVO(userInfo);
        deptTree.setModel(new DefaultTreeModel(deptRoot));

        if (selectNode != null) {
            TreePath path = new TreePath(selectNode.getPath());
            deptTree.setSelectionPath(path);
            deptTree.scrollPathToVisible(path);
        }

        postCheckBoxList.setModel(listModel);
        if (selPost != null) {
            postCheckBoxList.setSelectedObjects(selPost);

        }
    }

    private void setUserRespVO(UserRespVO userRespVO) {
        usernameTextField.setText(userRespVO.getUsername());
        mobileTextField.setText(userRespVO.getMobile());
        emailTextField.setText(userRespVO.getEmail());
        nicknameTextField.setText(userRespVO.getNickname());
        remarkTextArea.setText(userRespVO.getRemark());
        if(userRespVO.getDeptId()!=null){
            deptId = userRespVO.getDeptId();
        }
        if (userRespVO.getSex() != null) {
            sexComboBox.setSelectedIndex(userRespVO.getSex() - 1);

        }

    }

    public UserSaveReqVO getValue() {
        UserSaveReqVO reqVO = new UserSaveReqVO();
        reqVO.setId(id);
        reqVO.setDeptId(deptId);
        reqVO.setMobile(mobileTextField.getText());
        reqVO.setEmail(emailTextField.getText());
        reqVO.setNickname(nicknameTextField.getText());
        reqVO.setUsername(usernameTextField.getText());
        reqVO.setRemark(remarkTextArea.getText());
        if (id == null) {

            reqVO.setPassword(passwordField.getText());
        }

        reqVO.setSex(sexComboBox.getSelectedIndex() + 1);

        Object[] selPost = postCheckBoxList.getCheckBoxListSelectedValues();
        Set<Long> selPostId = new HashSet<>();
        for (Object obj : selPost) {
            if (obj instanceof PostSimpleRespVO) {
                selPostId.add(((PostSimpleRespVO) obj).getId());
            }
        }
        reqVO.setPostIds(selPostId);

        return reqVO;
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner non-commercial license
    private JPanel contentPanel;
    private JLabel label1;
    private JTextField nicknameTextField;
    private JLabel label2;
    private JTextField deptTextField;
    private JLabel label3;
    private JTextField mobileTextField;
    private JLabel label4;
    private JTextField emailTextField;
    private JLabel label5;
    private JTextField usernameTextField;
    private JLabel label6;
    private JPasswordField passwordField;
    private JLabel label7;
    private JComboBox sexComboBox;
    private JLabel label8;
    private JTextField postTextField;
    private JLabel label9;
    private JScrollPane scrollPane1;
    private JTextArea remarkTextArea;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}

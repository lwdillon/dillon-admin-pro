/*
 * Created by JFormDesigner on Fri Jun 14 15:12:51 CST 2024
 */

package com.lw.swing.view.system.user;

import cn.hutool.core.lang.Editor;
import cn.hutool.core.util.ArrayUtil;
import com.jidesoft.swing.CheckBoxList;
import com.lw.dillon.admin.framework.common.pojo.CommonResult;
import com.lw.dillon.admin.module.system.controller.admin.permission.vo.permission.PermissionAssignUserRoleReqVO;
import com.lw.dillon.admin.module.system.controller.admin.permission.vo.role.RoleRespVO;
import com.lw.swing.components.WScrollPane;
import com.lw.swing.request.Request;
import com.lw.ui.request.api.system.PermissionFeign;
import com.lw.ui.request.api.system.RoleFeign;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * @author wenli
 */
public class AssignRolesPane extends JPanel {
    private Long id;
    private JPopupMenu rolePopupMenu;
    private CheckBoxList roleCheckBoxList;

    public AssignRolesPane() {
        initComponents();
        initListeners();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner non-commercial license
        label1 = new JLabel();
        usernameTextField = new JTextField();
        label2 = new JLabel();
        nickTextField = new JTextField();
        label3 = new JLabel();
        rolesTextField = new JTextField();

        //======== this ========
        setLayout(new MigLayout(
            "fill,hidemode 3",
            // columns
            "[right]" +
            "[380:n,grow,fill]",
            // rows
            "[]" +
            "[]" +
            "[]"));

        //---- label1 ----
        label1.setText("\u7528\u6237\u540d\u79f0");
        add(label1, "cell 0 0");

        //---- usernameTextField ----
        usernameTextField.setEditable(false);
        add(usernameTextField, "cell 1 0");

        //---- label2 ----
        label2.setText("\u7528\u6237\u6635\u79f0");
        add(label2, "cell 0 1");

        //---- nickTextField ----
        nickTextField.setEditable(false);
        add(nickTextField, "cell 1 1");

        //---- label3 ----
        label3.setText("\u89d2\u8272");
        add(label3, "cell 0 2");

        //---- rolesTextField ----
        rolesTextField.setEditable(false);
        add(rolesTextField, "cell 1 2");
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on

        rolePopupMenu = new JPopupMenu();
        roleCheckBoxList = new CheckBoxList();
        roleCheckBoxList.setClickInCheckBoxOnly(false);
        rolePopupMenu.add(new WScrollPane(roleCheckBoxList));
        roleCheckBoxList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                if (value instanceof RoleRespVO) {
                    value = ((RoleRespVO) value).getName();
                }
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        });
    }


    private void initListeners() {
        rolesTextField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    showRolePopupMenu();
                }
            }
        });
        roleCheckBoxList.getCheckBoxListSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    Object[] objects = roleCheckBoxList.getCheckBoxListSelectedValues();
                    rolesTextField.setText(ArrayUtil.join(objects, ",", new Editor<Object>() {
                        @Override
                        public Object edit(Object o) {
                            if (o instanceof RoleRespVO) {
                                return ((RoleRespVO) o).getName();
                            }
                            return o;
                        }
                    }));
                }
            }
        });
    }

    private void showRolePopupMenu() {
        rolePopupMenu.setPopupSize(rolesTextField.getWidth(), 400);
        rolePopupMenu.show(rolesTextField, 0, rolesTextField.getHeight());
    }


    public PermissionAssignUserRoleReqVO getValue() {

        Object[] selRole = roleCheckBoxList.getCheckBoxListSelectedValues();
        Set<Long> selRoleId = new HashSet<>();
        for (Object obj : selRole) {
            if (obj instanceof RoleRespVO) {
                selRoleId.add(((RoleRespVO) obj).getId());
            }
        }
        PermissionAssignUserRoleReqVO permissionAssignUserRoleReqVO = new PermissionAssignUserRoleReqVO();
        permissionAssignUserRoleReqVO.setUserId(id);
        permissionAssignUserRoleReqVO.setRoleIds(selRoleId);

        return permissionAssignUserRoleReqVO;
    }

    public void updateData(String userName, String nickName, Long id) {

        this.id = id;
        usernameTextField.setText(userName);
        nickTextField.setText(nickName);


        SwingWorker<Map<String, Object>, RoleRespVO> swingWorker = new SwingWorker<Map<String, Object>, RoleRespVO>() {
            @Override
            protected Map<String, Object> doInBackground() throws Exception {
                CommonResult<List<RoleRespVO>> roleResult = Request.connector(RoleFeign.class).getSimpleRoleList();
                CommonResult<Set<Long>> listAdminRoleResult = Request.connector(PermissionFeign.class).listAdminRoles(id);

                Vector<Object> selRoles = new Vector<>();
                DefaultListModel listModel = new DefaultListModel();

                for (RoleRespVO roleRespVO : roleResult.getData()) {

                    listModel.addElement(roleRespVO);

                    if (listAdminRoleResult.getData().contains(roleRespVO.getId())) {
                        selRoles.add(roleRespVO);
                    }
                }

                Map<String, Object> reslutMap = new HashMap<>();
                reslutMap.put("listModel", listModel);
                reslutMap.put("selRoles", selRoles);


                return reslutMap;
            }


            @Override
            protected void done() {
                try {
                    roleCheckBoxList.setModel((ListModel) get().get("listModel"));
                    Vector selRoles = (Vector) get().get("selRoles");
                    if (selRoles != null) {
                        roleCheckBoxList.setSelectedObjects((Vector<?>) get().get("selRoles"));
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

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner non-commercial license
    private JLabel label1;
    private JTextField usernameTextField;
    private JLabel label2;
    private JTextField nickTextField;
    private JLabel label3;
    private JTextField rolesTextField;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}

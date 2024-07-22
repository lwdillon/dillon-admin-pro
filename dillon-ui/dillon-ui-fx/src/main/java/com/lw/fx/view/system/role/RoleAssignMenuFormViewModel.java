package com.lw.fx.view.system.role;

import com.lw.dillon.admin.framework.common.pojo.CommonResult;
import com.lw.dillon.admin.module.system.controller.admin.permission.vo.menu.MenuSimpleRespVO;
import com.lw.dillon.admin.module.system.controller.admin.permission.vo.permission.PermissionAssignRoleMenuReqVO;
import com.lw.dillon.admin.module.system.controller.admin.permission.vo.role.RoleRespVO;
import com.lw.fx.request.Request;
import com.lw.ui.request.api.system.MenuFeign;
import com.lw.ui.request.api.system.PermissionFeign;
import de.saxsys.mvvmfx.ViewModel;
import io.datafx.core.concurrent.ProcessChain;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.TreeItem;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RoleAssignMenuFormViewModel implements ViewModel {
    private StringProperty name = new SimpleStringProperty();
    private StringProperty code = new SimpleStringProperty();
    private LongProperty roleId = new SimpleLongProperty();
    private ObservableSet<Long> menuIds = FXCollections.observableSet();
    private ObjectProperty<CheckBoxTreeItem<MenuSimpleRespVO>> menuTreeRoot = new SimpleObjectProperty<>();


    public void getSimpleMenuList(RoleRespVO roleRespVO) {
        name.set(roleRespVO.getName());
        code.set(roleRespVO.getCode());
        roleId.set(roleRespVO.getId());
        ProcessChain.create().addSupplierInExecutor(() -> Request.connector(PermissionFeign.class).getRoleMenuList(roleRespVO.getId()))
                .addConsumerInPlatformThread(setCommonResult -> {
                    menuIds.clear();
                    menuIds.addAll(setCommonResult.getData());
                })
                .addSupplierInExecutor(() -> Request.connector(MenuFeign.class).getSimpleMenuList())
                .addConsumerInPlatformThread(listCommonResult -> {
                    MenuSimpleRespVO respVO = new MenuSimpleRespVO();
                    respVO.setId(0L);
                    respVO.setName("主类目");

                    CheckBoxTreeItem<MenuSimpleRespVO> root = new CheckBoxTreeItem<>(respVO);
                    root.setExpanded(true);
                    // Build the treeExpanded
                    Map<Long, CheckBoxTreeItem<MenuSimpleRespVO>> nodeMap = new HashMap<>();
                    nodeMap.put(0l, root); // Root node


                    if (listCommonResult.isSuccess()) {
                        for (MenuSimpleRespVO menu : listCommonResult.getData()) {

                            CheckBoxTreeItem<MenuSimpleRespVO> item = new CheckBoxTreeItem<MenuSimpleRespVO>(menu);
                            nodeMap.put(menu.getId(), item);

                        }

                        listCommonResult.getData().forEach(menuSimpleRespVO -> {
                            CheckBoxTreeItem<MenuSimpleRespVO> parentNode = nodeMap.get(menuSimpleRespVO.getParentId());
                            CheckBoxTreeItem<MenuSimpleRespVO> childNode = nodeMap.get(menuSimpleRespVO.getId());
                            if (parentNode != null) {
                                parentNode.getChildren().add(childNode);
                            }
                            childNode.setSelected(menuIds.contains(menuSimpleRespVO.getId()));

                        });

                        menuTreeRoot.set(root);
                    }
                })
                .run();
    }

    public CommonResult<Boolean> assignRoleMenu() {
        PermissionAssignRoleMenuReqVO permissionAssignRoleMenuReqVO = new PermissionAssignRoleMenuReqVO();
        permissionAssignRoleMenuReqVO.setRoleId(roleId.get());

        Set<Long> selMenuIds = new HashSet<>();
        findSelectedItems(menuTreeRoot.get(),selMenuIds);
        permissionAssignRoleMenuReqVO.setMenuIds(selMenuIds);
        return Request.connector(PermissionFeign.class).assignRoleMenu(permissionAssignRoleMenuReqVO);
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public String getCode() {
        return code.get();
    }

    public StringProperty codeProperty() {
        return code;
    }

    public CheckBoxTreeItem<MenuSimpleRespVO> getMenuTreeRoot() {
        return menuTreeRoot.get();
    }

    public ObjectProperty<CheckBoxTreeItem<MenuSimpleRespVO>> menuTreeRootProperty() {
        return menuTreeRoot;
    }

    public ObservableSet<Long> getMenuIds() {
        return menuIds;
    }

    private void findSelectedItems(CheckBoxTreeItem<MenuSimpleRespVO> item, Set<Long> selMenuIds) {
        if (item.isSelected()) {
            selMenuIds.add(item.getValue().getId());
        }

        for (TreeItem<MenuSimpleRespVO> child : item.getChildren()) {
            findSelectedItems((CheckBoxTreeItem<MenuSimpleRespVO>) child,selMenuIds);
        }
    }





}

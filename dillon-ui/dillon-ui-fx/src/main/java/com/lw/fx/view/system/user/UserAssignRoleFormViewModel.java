package com.lw.fx.view.system.user;

import com.lw.dillon.admin.framework.common.pojo.CommonResult;
import com.lw.dillon.admin.module.system.controller.admin.permission.vo.permission.PermissionAssignUserRoleReqVO;
import com.lw.dillon.admin.module.system.controller.admin.permission.vo.role.RoleRespVO;
import com.lw.dillon.admin.module.system.controller.admin.user.vo.user.UserRespVO;
import com.lw.fx.request.Request;
import com.lw.ui.request.api.system.PermissionFeign;
import com.lw.ui.request.api.system.RoleFeign;
import de.saxsys.mvvmfx.SceneLifecycle;
import de.saxsys.mvvmfx.ViewModel;
import io.datafx.core.concurrent.ProcessChain;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;

import java.util.HashSet;
import java.util.Set;

public class UserAssignRoleFormViewModel implements ViewModel, SceneLifecycle {

    private LongProperty userId = new SimpleLongProperty();
    private StringProperty username = new SimpleStringProperty();
    private StringProperty nickname = new SimpleStringProperty();
    private ObjectProperty<ObservableList<RoleRespVO>> roleItems = new SimpleObjectProperty<>();
    private ObservableSet<Long> selRoleIdItems = FXCollections.emptyObservableSet();
    private ObservableList<RoleRespVO> selRoleItems = FXCollections.observableArrayList();


    public void listUserRoles(UserRespVO userRespVO) {
        userId.set(userRespVO.getId());
        username.set(userRespVO.getUsername());
        nickname.set(userRespVO.getNickname());
        ProcessChain.create()
                .addSupplierInExecutor(() -> Request.connector(PermissionFeign.class).listAdminRoles(userRespVO.getId()).getData())
                .addConsumerInPlatformThread(rel -> {
                    selRoleIdItems = FXCollections.observableSet(rel);
                })
                .addSupplierInExecutor(() -> Request.connector(RoleFeign.class).getSimpleRoleList().getData())
                .addConsumerInPlatformThread(roleRespVOS -> {
                    ObservableList<RoleRespVO> list = FXCollections.observableArrayList();
                    list.addAll(roleRespVOS);
                    for (RoleRespVO respVO : roleRespVOS) {
                        if (selRoleIdItems != null) {
                            if (selRoleIdItems.contains(respVO.getId())) {
                                selRoleItems.add(respVO);
                            }
                        }

                    }
                    roleItems.set(list);

                })
                .onException(e -> e.printStackTrace())
                .run();
    }

    public String getUsername() {
        return username.get();
    }

    public StringProperty usernameProperty() {
        return username;
    }

    public String getNickname() {
        return nickname.get();
    }

    public StringProperty nicknameProperty() {
        return nickname;
    }

    public ObservableList<RoleRespVO> getRoleItems() {
        return roleItems.get();
    }

    public ObjectProperty<ObservableList<RoleRespVO>> roleItemsProperty() {
        return roleItems;
    }

    public ObservableList<RoleRespVO> getSelRoleItems() {
        return selRoleItems;
    }

    public CommonResult assignUserRole() {

        Set<Long> roleIds = new HashSet<>();
        for (RoleRespVO respVO : selRoleItems) {
            roleIds.add(respVO.getId());
        }

        PermissionAssignUserRoleReqVO permissionAssignUserRoleReqVO = new PermissionAssignUserRoleReqVO();
        permissionAssignUserRoleReqVO.setUserId(userId.getValue());
        if (roleIds.isEmpty() == false) {
            permissionAssignUserRoleReqVO.setRoleIds(roleIds);
        }
        return Request.connector(PermissionFeign.class).assignUserRole(permissionAssignUserRoleReqVO);
    }

    @Override
    public void onViewAdded() {

    }

    @Override
    public void onViewRemoved() {

    }
}

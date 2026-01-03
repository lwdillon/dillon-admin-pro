package com.dillon.lw.fx.view.system.user;

import com.dillon.lw.api.system.PermissionApi;
import com.dillon.lw.api.system.RoleApi;
import com.dillon.lw.fx.eventbus.EventBusCenter;
import com.dillon.lw.fx.eventbus.event.MessageEvent;
import com.dillon.lw.fx.eventbus.event.UpdateDataEvent;
import com.dillon.lw.fx.http.PayLoad;
import com.dillon.lw.fx.mvvm.base.BaseViewModel;
import com.dillon.lw.fx.utils.MessageType;
import com.dillon.lw.fx.view.layout.ConfirmDialog;
import com.dillon.lw.module.system.controller.admin.permission.vo.permission.PermissionAssignUserRoleReqVO;
import com.dillon.lw.module.system.controller.admin.permission.vo.role.RoleRespVO;
import com.dillon.lw.module.system.controller.admin.user.vo.user.UserRespVO;
import com.dtflys.forest.Forest;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class UserAssignRoleFormViewModel extends BaseViewModel {

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


        CompletableFuture.supplyAsync(() -> {
            return new PayLoad<Set<Long>>().apply(Forest.client(PermissionApi.class).listAdminRoles(userRespVO.getId()));
        }).thenAcceptAsync(roleIds -> {
            selRoleIdItems = FXCollections.observableSet(roleIds);
        }, Platform::runLater).thenComposeAsync(v -> {
            return CompletableFuture.supplyAsync(() -> {
                return new PayLoad<List<RoleRespVO>>().apply(Forest.client(RoleApi.class).getSimpleRoleList());
            });
        }).thenAcceptAsync(roleRespVOS -> {
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
        }, Platform::runLater).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });
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

    public void assignUserRole(ConfirmDialog confirmDialog) {

        Set<Long> roleIds = new HashSet<>();
        for (RoleRespVO respVO : selRoleItems) {
            roleIds.add(respVO.getId());
        }

        PermissionAssignUserRoleReqVO permissionAssignUserRoleReqVO = new PermissionAssignUserRoleReqVO();
        permissionAssignUserRoleReqVO.setUserId(userId.getValue());
        if (roleIds.isEmpty() == false) {
            permissionAssignUserRoleReqVO.setRoleIds(roleIds);
        }

        CompletableFuture.supplyAsync(() -> {
            return new PayLoad<Boolean>().apply(Forest.client(PermissionApi.class).assignUserRole(permissionAssignUserRoleReqVO));
        }).thenAcceptAsync(commonResult -> {
            EventBusCenter.get().post(new MessageEvent("分配角色成功", MessageType.SUCCESS));
            EventBusCenter.get().post(new UpdateDataEvent("更新用户列表"));
            confirmDialog.close();
        }, Platform::runLater).exceptionally(throwable -> {
            throwable.printStackTrace();
            EventBusCenter.get().post(new MessageEvent("分配角色失败", MessageType.DANGER));
            return null;
        });
    }


}

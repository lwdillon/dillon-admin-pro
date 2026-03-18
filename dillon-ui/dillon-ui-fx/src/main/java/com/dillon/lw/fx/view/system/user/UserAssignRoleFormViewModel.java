package com.dillon.lw.fx.view.system.user;

import com.dillon.lw.api.system.PermissionApi;
import com.dillon.lw.api.system.RoleApi;
import com.dillon.lw.fx.DefaultExceptionHandler;
import com.dillon.lw.fx.eventbus.EventBusCenter;
import com.dillon.lw.fx.eventbus.event.MessageEvent;
import com.dillon.lw.fx.eventbus.event.UpdateDataEvent;
import com.dillon.lw.fx.mvvm.base.BaseViewModel;
import com.dillon.lw.fx.rx.FxSchedulers;
import com.dillon.lw.fx.rx.FxRx;
import com.dillon.lw.fx.utils.MessageType;
import com.dillon.lw.fx.view.layout.ConfirmDialog;
import com.dillon.lw.module.system.controller.admin.permission.vo.permission.PermissionAssignUserRoleReqVO;
import com.dillon.lw.module.system.controller.admin.permission.vo.role.RoleRespVO;
import com.dillon.lw.module.system.controller.admin.user.vo.user.UserRespVO;
import com.dtflys.forest.Forest;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.util.HashSet;
import java.util.Set;

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

        Single
                /*
                 * 用户已分配角色和全部角色列表可以并行获取，
                 * 用 zip 组合后一次性回到 JavaFX UI 线程刷新候选列表与已选中项。
                 */
                .zip(
                        Single.fromCallable(() -> Forest.client(PermissionApi.class).listAdminRoles(userRespVO.getId()).getCheckedData()),
                        Single.fromCallable(() -> Forest.client(RoleApi.class).getSimpleRoleList().getCheckedData()),
                        (roleIds, roleRespVOS) -> {
                            java.util.Map<String, Object> result = new java.util.HashMap<String, Object>();
                            result.put("roleIds", roleIds);
                            result.put("roles", roleRespVOS);
                            return result;
                        }
                )
                .subscribeOn(Schedulers.io())
                .observeOn(FxSchedulers.fx())
                .compose(FxRx.bindTo(this))
                .subscribe(result -> {
                    selRoleIdItems = FXCollections.observableSet((java.util.Set<Long>) result.get("roleIds"));
                    java.util.List<RoleRespVO> roleRespVOS = (java.util.List<RoleRespVO>) result.get("roles");
                    ObservableList<RoleRespVO> list = FXCollections.observableArrayList();
                    list.addAll(roleRespVOS);
                    selRoleItems.clear();
                    for (RoleRespVO respVO : roleRespVOS) {
                        if (selRoleIdItems != null && selRoleIdItems.contains(respVO.getId())) {
                            selRoleItems.add(respVO);
                        }
                    }
                    roleItems.set(list);
                }, DefaultExceptionHandler::handle);
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

        Single
                .fromCallable(() -> Forest.client(PermissionApi.class).assignUserRole(permissionAssignUserRoleReqVO).getCheckedData())
                .subscribeOn(Schedulers.io())
                .observeOn(FxSchedulers.fx())
                .compose(FxRx.bindTo(this))
                .subscribe(commonResult -> {
                    EventBusCenter.get().post(new MessageEvent("分配角色成功", MessageType.SUCCESS));
                    EventBusCenter.get().post(new UpdateDataEvent("更新用户列表"));
                    confirmDialog.close();
                }, DefaultExceptionHandler::handle);
    }


}

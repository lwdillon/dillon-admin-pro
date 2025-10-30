package com.dillon.lw.fx.view.system.role;

import cn.hutool.core.bean.BeanUtil;
import com.dillon.lw.module.system.controller.admin.permission.vo.role.RoleSaveReqVO;
import com.dillon.lw.api.system.RoleApi;
import com.dillon.lw.fx.eventbus.EventBusCenter;
import com.dillon.lw.fx.eventbus.event.MessageEvent;
import com.dillon.lw.fx.eventbus.event.UpdateDataEvent;
import com.dillon.lw.fx.http.PayLoad;
import com.dillon.lw.fx.http.Request;
import com.dillon.lw.fx.mvvm.base.BaseViewModel;
import com.dillon.lw.fx.mvvm.mapping.ModelWrapper;
import com.dillon.lw.fx.utils.MessageType;
import com.dillon.lw.fx.view.layout.ConfirmDialog;
import io.reactivex.rxjava3.schedulers.Schedulers;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;

public class RoleFormViewModel extends BaseViewModel {

    private ModelWrapper<RoleSaveReqVO> wrapper = new ModelWrapper<>();


    public RoleFormViewModel() {
    }

    public RoleSaveReqVO getUserSaveReqVO() {
        wrapper.commit();
        return wrapper.get();
    }


    public void query(Long id) {

        if (id == null) {
            setRole(new RoleSaveReqVO());
            return;
        }
        Request.getInstance().create(RoleApi.class).getRole(id)
                .subscribeOn(Schedulers.io())
                .map(new PayLoad<>())
                .observeOn(Schedulers.from(Platform::runLater))
                .subscribe(data -> {
                    RoleSaveReqVO roleRespVO = new RoleSaveReqVO();
                    BeanUtil.copyProperties(data, roleRespVO);
                    setRole(roleRespVO);
                }, e -> {
                    e.printStackTrace();
                });
    }

    /**
     * 系统设置菜单
     */
    public void setRole(RoleSaveReqVO roleRespVO) {

        wrapper.set(roleRespVO);
        wrapper.reload();
    }


    public void addRole(ConfirmDialog confirmDialog) {
        Request.getInstance().create(RoleApi.class).createRole(getUserSaveReqVO())
                .subscribeOn(Schedulers.io())
                .map(new PayLoad<>())
                .observeOn(Schedulers.from(Platform::runLater))
                .subscribe(data -> {
                    EventBusCenter.get().post(new UpdateDataEvent("更新角色列表"));
                    EventBusCenter.get().post(new MessageEvent("保存成功", MessageType.SUCCESS));
                    confirmDialog.close();
                }, e -> {
                    e.printStackTrace();
                });
    }

    public void updateRole(ConfirmDialog confirmDialog) {
        Request.getInstance().create(RoleApi.class).updateRole(getUserSaveReqVO())
                .subscribeOn(Schedulers.io())
                .map(new PayLoad<>())
                .observeOn(Schedulers.from(Platform::runLater))
                .subscribe(data -> {
                    EventBusCenter.get().post(new UpdateDataEvent("更新角色列表"));
                    EventBusCenter.get().post(new MessageEvent("更新成功", MessageType.SUCCESS));
                    confirmDialog.close();
                }, e -> {
                    e.printStackTrace();
                });
    }

    public StringProperty nameProperty() {
        return wrapper.field("name", RoleSaveReqVO::getName, RoleSaveReqVO::setName, "");
    }

    public StringProperty codeProperty() {
        return wrapper.field("code", RoleSaveReqVO::getCode, RoleSaveReqVO::setCode, "");
    }

    public IntegerProperty sortProperty() {
        return wrapper.field("sort", RoleSaveReqVO::getSort, RoleSaveReqVO::setSort);
    }

    public IntegerProperty statusProperty() {
        return wrapper.field("status", RoleSaveReqVO::getStatus, RoleSaveReqVO::setStatus);
    }

    public StringProperty remarkProperty() {
        return wrapper.field("remark", RoleSaveReqVO::getRemark, RoleSaveReqVO::setRemark);
    }

}


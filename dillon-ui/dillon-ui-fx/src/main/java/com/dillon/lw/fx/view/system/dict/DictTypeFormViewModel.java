package com.dillon.lw.fx.view.system.dict;

import cn.hutool.core.bean.BeanUtil;
import com.dillon.lw.api.system.DictTypeApi;
import com.dillon.lw.fx.eventbus.EventBusCenter;
import com.dillon.lw.fx.eventbus.event.MessageEvent;
import com.dillon.lw.fx.eventbus.event.UpdateDataEvent;
import com.dillon.lw.fx.http.PayLoad;
import com.dillon.lw.fx.mvvm.base.BaseViewModel;
import com.dillon.lw.fx.mvvm.mapping.ModelWrapper;
import com.dillon.lw.fx.utils.MessageType;
import com.dillon.lw.fx.view.layout.ConfirmDialog;
import com.dillon.lw.module.system.controller.admin.dict.vo.type.DictTypeRespVO;
import com.dillon.lw.module.system.controller.admin.dict.vo.type.DictTypeSaveReqVO;
import com.dtflys.forest.Forest;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.StringProperty;

import java.util.concurrent.CompletableFuture;

public class DictTypeFormViewModel extends BaseViewModel {

    private ModelWrapper<DictTypeSaveReqVO> wrapper = new ModelWrapper<>();

    private BooleanProperty edit = new SimpleBooleanProperty(false);


    public void query(Long id) {

        if (id == null) {
            setDictType(new DictTypeSaveReqVO());
            return;
        }
        CompletableFuture.supplyAsync(() -> {
            return new PayLoad<DictTypeRespVO>().apply(Forest.client(DictTypeApi.class).getDictType(id));
        }).thenAcceptAsync(data -> {
            DictTypeSaveReqVO dictTypeSaveReqVO = new DictTypeSaveReqVO();
            BeanUtil.copyProperties(data, dictTypeSaveReqVO);
            setDictType(dictTypeSaveReqVO);
        }, Platform::runLater).exceptionally(e -> {
            e.printStackTrace();
            return null;
        });
    }

    /**
     * 系统设置菜单
     */
    public void setDictType(DictTypeSaveReqVO roleRespVO) {

        wrapper.set(roleRespVO);
        wrapper.reload();
    }

    public DictTypeSaveReqVO getDictType() {
        wrapper.commit();
        return wrapper.get();

    }


    public void addDictType(ConfirmDialog confirmDialog) {

        CompletableFuture.supplyAsync(() -> {
            return new PayLoad<Long>().apply(Forest.client(DictTypeApi.class).createDictType(getDictType()));
        }).thenAcceptAsync(data -> {
            EventBusCenter.get().post(new UpdateDataEvent("更新字典类型列表"));
            EventBusCenter.get().post(new MessageEvent("保存成功", MessageType.SUCCESS));
            confirmDialog.close();
        }, Platform::runLater).exceptionally(e -> {
            e.printStackTrace();
            return null;
        });


    }

    public void updateDictType(ConfirmDialog confirmDialog) {
        CompletableFuture.supplyAsync(() -> {
            return new PayLoad<Boolean>().apply(Forest.client(DictTypeApi.class).updateDictType(getDictType()));
        }).thenAcceptAsync(data -> {
            EventBusCenter.get().post(new UpdateDataEvent("更新字典类型列表"));
            EventBusCenter.get().post(new MessageEvent("更新成功", MessageType.SUCCESS));
            confirmDialog.close();
        }, Platform::runLater).exceptionally(e -> {
            e.printStackTrace();
            return null;
        });
    }

    public StringProperty nameProperty() {
        return wrapper.field("name", DictTypeSaveReqVO::getName, DictTypeSaveReqVO::setName, "");
    }

    public StringProperty typeProperty() {
        return wrapper.field("type", DictTypeSaveReqVO::getType, DictTypeSaveReqVO::setType, "");
    }


    public IntegerProperty statusProperty() {
        return wrapper.field("status", DictTypeSaveReqVO::getStatus, DictTypeSaveReqVO::setStatus);
    }

    public StringProperty remartProperty() {
        return wrapper.field("remark", DictTypeSaveReqVO::getRemark, DictTypeSaveReqVO::setRemark);
    }

    public boolean isEdit() {
        return edit.get();
    }

    public BooleanProperty editProperty() {
        return edit;
    }
}


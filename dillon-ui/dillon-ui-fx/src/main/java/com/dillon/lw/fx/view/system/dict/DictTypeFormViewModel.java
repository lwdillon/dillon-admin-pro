package com.dillon.lw.fx.view.system.dict;

import cn.hutool.core.bean.BeanUtil;
import com.dillon.lw.module.system.controller.admin.dict.vo.type.DictTypeSaveReqVO;
import com.dillon.lw.api.system.DictTypeApi;
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
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.StringProperty;

public class DictTypeFormViewModel extends BaseViewModel {

    private ModelWrapper<DictTypeSaveReqVO> wrapper = new ModelWrapper<>();

    private BooleanProperty edit = new SimpleBooleanProperty(false);


    public void query(Long id) {

        if (id == null) {
            setDictType(new DictTypeSaveReqVO());
            return;
        }
        Request.getInstance().create(DictTypeApi.class).getDictType(id)
                .subscribeOn(Schedulers.io())
                .map(new PayLoad<>())
                .observeOn(Schedulers.from(Platform::runLater))
                .subscribe(data -> {
                    DictTypeSaveReqVO dictTypeSaveReqVO = new DictTypeSaveReqVO();
                    BeanUtil.copyProperties(data, dictTypeSaveReqVO);
                    setDictType(dictTypeSaveReqVO);
                }, e -> {
                    e.printStackTrace();
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

        Request.getInstance().create(DictTypeApi.class).createDictType(getDictType())
                .subscribeOn(Schedulers.io())
                .map(new PayLoad<>())
                .observeOn(Schedulers.from(Platform::runLater))
                .subscribe(data -> {
                    EventBusCenter.get().post(new UpdateDataEvent("更新字典类型列表"));
                    EventBusCenter.get().post(new MessageEvent("保存成功", MessageType.SUCCESS));
                    confirmDialog.close();
                }, e -> {
                    e.printStackTrace();
                });


    }

    public void updateDictType(ConfirmDialog confirmDialog) {
        Request.getInstance().create(DictTypeApi.class).updateDictType(getDictType())
                .subscribeOn(Schedulers.io())
                .map(new PayLoad<>())
                .observeOn(Schedulers.from(Platform::runLater))
                .subscribe(data -> {
                    EventBusCenter.get().post(new UpdateDataEvent("更新字典类型列表"));
                    EventBusCenter.get().post(new MessageEvent("更新成功", MessageType.SUCCESS));
                    confirmDialog.close();
                }, e -> {
                    e.printStackTrace();
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


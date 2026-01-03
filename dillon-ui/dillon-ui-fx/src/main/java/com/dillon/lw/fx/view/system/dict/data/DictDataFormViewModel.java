package com.dillon.lw.fx.view.system.dict.data;

import cn.hutool.core.bean.BeanUtil;
import com.dillon.lw.api.system.DictDataApi;
import com.dillon.lw.fx.eventbus.EventBusCenter;
import com.dillon.lw.fx.eventbus.event.MessageEvent;
import com.dillon.lw.fx.eventbus.event.UpdateDataEvent;
import com.dillon.lw.fx.http.PayLoad;
import com.dillon.lw.fx.mvvm.base.BaseViewModel;
import com.dillon.lw.fx.mvvm.mapping.ModelWrapper;
import com.dillon.lw.fx.utils.MessageType;
import com.dillon.lw.fx.view.layout.ConfirmDialog;
import com.dillon.lw.module.system.controller.admin.dict.vo.data.DictDataRespVO;
import com.dillon.lw.module.system.controller.admin.dict.vo.data.DictDataSaveReqVO;
import com.dillon.lw.module.system.controller.admin.dict.vo.data.DictDataSimpleRespVO;
import com.dtflys.forest.Forest;
import javafx.application.Platform;
import javafx.beans.property.*;

import java.util.concurrent.CompletableFuture;

public class DictDataFormViewModel extends BaseViewModel {

    private ModelWrapper<DictDataSaveReqVO> wrapper = new ModelWrapper<>();

    private BooleanProperty edit = new SimpleBooleanProperty(false);

    private StringProperty selDictType = new SimpleStringProperty();

    private ObjectProperty<DictDataSimpleRespVO> selDictDataSimpleRespVO = new SimpleObjectProperty<>();

    public DictDataFormViewModel() {
    }

    public DictDataSaveReqVO getDictDataSave() {
        colorTypeProperty().set(selDictDataSimpleRespVO.getValue().getValue());
        wrapper.commit();
        return wrapper.get();
    }


    public void query(Long id) {

        if (id == null) {
            if (id == null) {
                DictDataSaveReqVO dictDataSaveReqVO = new DictDataSaveReqVO();
                dictDataSaveReqVO.setDictType(selDictType.get());
                setDictData(dictDataSaveReqVO);
                return;
            }
        }

        CompletableFuture.supplyAsync(() -> {
            return new PayLoad<DictDataRespVO>().apply(Forest.client(DictDataApi.class).getDictData(id));
        }).thenAcceptAsync(data -> {
            DictDataSaveReqVO dictDataSaveReqVO = new DictDataSaveReqVO();
            BeanUtil.copyProperties(data, dictDataSaveReqVO);
            setDictData(dictDataSaveReqVO);
        }, Platform::runLater).exceptionally(e -> {
            e.printStackTrace();
            return null;
        });
    }

    /**
     * 系统设置菜单
     */
    public void setDictData(DictDataSaveReqVO dictDataRespVO) {

        wrapper.set(dictDataRespVO);
        wrapper.reload();
    }


    public void addDictData(ConfirmDialog confirmDialog) {

        CompletableFuture.supplyAsync(() -> {
            return new PayLoad<Long>().apply(Forest.client(DictDataApi.class).createDictData(getDictDataSave()));
        }).thenAcceptAsync(data -> {
            EventBusCenter.get().post(new UpdateDataEvent("更新字典数据列表"));
            EventBusCenter.get().post(new MessageEvent("保存成功", MessageType.SUCCESS));
            confirmDialog.close();
        }, Platform::runLater).exceptionally(e -> {
            e.printStackTrace();
            return null;
        });
    }

    public void updateDictData(ConfirmDialog confirmDialog) {
        CompletableFuture.supplyAsync(() -> {
            return new PayLoad<Boolean>().apply(Forest.client(DictDataApi.class).updateDictData(getDictDataSave()));
        }).thenAcceptAsync(data -> {
            EventBusCenter.get().post(new UpdateDataEvent("更新字典数据列表"));
            EventBusCenter.get().post(new MessageEvent("更新成功", MessageType.SUCCESS));
            confirmDialog.close();
        }, Platform::runLater).exceptionally(e -> {
            e.printStackTrace();
            return null;
        });
    }

    public LongProperty idProperty() {
        return wrapper.field("id", DictDataSaveReqVO::getId, DictDataSaveReqVO::setId);
    }

    public StringProperty labelProperty() {
        return wrapper.field("label", DictDataSaveReqVO::getLabel, DictDataSaveReqVO::setLabel);
    }

    public StringProperty dictTypeProperty() {
        return wrapper.field("dictType", DictDataSaveReqVO::getDictType, DictDataSaveReqVO::setDictType);
    }

    public StringProperty colorTypeProperty() {
        return wrapper.field("colorType", DictDataSaveReqVO::getColorType, DictDataSaveReqVO::setColorType);
    }

    public StringProperty cssClassProperty() {
        return wrapper.field("cssClass", DictDataSaveReqVO::getCssClass, DictDataSaveReqVO::setCssClass);
    }

    public StringProperty valueProperty() {
        return wrapper.field("value", DictDataSaveReqVO::getValue, DictDataSaveReqVO::setValue, "");
    }

    public IntegerProperty sortProperty() {
        return wrapper.field("sort", DictDataSaveReqVO::getSort, DictDataSaveReqVO::setSort);
    }

    public IntegerProperty statusProperty() {
        return wrapper.field("status", DictDataSaveReqVO::getStatus, DictDataSaveReqVO::setStatus);
    }

    public StringProperty remartProperty() {
        return wrapper.field("remark", DictDataSaveReqVO::getRemark, DictDataSaveReqVO::setRemark);
    }

    public DictDataSimpleRespVO getSelDictDataSimpleRespVO() {
        return selDictDataSimpleRespVO.get();
    }

    public ObjectProperty<DictDataSimpleRespVO> selDictDataSimpleRespVOProperty() {
        return selDictDataSimpleRespVO;
    }

    public boolean isEdit() {
        return edit.get();
    }

    public BooleanProperty editProperty() {
        return edit;
    }

    public String getSelDictType() {
        return selDictType.get();
    }

    public StringProperty selDictTypeProperty() {
        return selDictType;
    }


}


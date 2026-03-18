package com.dillon.lw.fx.view.infra.config;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.Convert;
import com.dillon.lw.api.infra.ConfigApi;
import com.dillon.lw.fx.DefaultExceptionHandler;
import com.dillon.lw.fx.eventbus.EventBusCenter;
import com.dillon.lw.fx.eventbus.event.MessageEvent;
import com.dillon.lw.fx.eventbus.event.UpdateDataEvent;
import com.dillon.lw.fx.mvvm.base.BaseViewModel;
import com.dillon.lw.fx.mvvm.mapping.ModelWrapper;
import com.dillon.lw.fx.rx.FxSchedulers;
import com.dillon.lw.fx.rx.FxRx;
import com.dillon.lw.fx.store.AppStore;
import com.dillon.lw.fx.utils.MessageType;
import com.dillon.lw.fx.view.layout.ConfirmDialog;
import com.dillon.lw.module.infra.controller.admin.config.vo.ConfigSaveReqVO;
import com.dillon.lw.module.system.controller.admin.dict.vo.data.DictDataSimpleRespVO;
import com.dtflys.forest.Forest;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.util.Optional;

import static com.dillon.lw.utils.DictTypeEnum.INFRA_BOOLEAN_STRING;


public class ConfigFormViewModel extends BaseViewModel {

    private ModelWrapper<ConfigSaveReqVO> wrapper = new ModelWrapper<>();
    private ObjectProperty<DictDataSimpleRespVO> visbleSel = new SimpleObjectProperty<>();


    public ConfigFormViewModel() {
    }

    public ConfigSaveReqVO getSaveReqVO() {
        visibleProperty().set(Convert.toBool(visbleSel.get().getValue(), false));
        wrapper.commit();
        return wrapper.get();
    }


    public void query(Long id) {

        if (id == null) {
            setValue(new ConfigSaveReqVO());
            return;
        }
        Single
                .fromCallable(() -> Forest.client(ConfigApi.class).getConfig(id).getCheckedData())
                .subscribeOn(Schedulers.io())
                .observeOn(FxSchedulers.fx())
                .compose(FxRx.bindTo(this))
                .subscribe(r -> {
                    ConfigSaveReqVO reqVO = new ConfigSaveReqVO();
                    BeanUtil.copyProperties(r, reqVO);
                    setValue(reqVO);
                }, DefaultExceptionHandler::handle);

    }

    /**
     * 系统设置菜单
     */
    public void setValue(ConfigSaveReqVO roleRespVO) {
        DictDataSimpleRespVO sel = AppStore.getDictDataValueMap(INFRA_BOOLEAN_STRING).get(Optional.ofNullable(roleRespVO).map(ConfigSaveReqVO::getVisible).orElse(false) + "");
        visbleSel.set(sel);
        wrapper.set(roleRespVO);
        wrapper.reload();
    }


    public void createConfig(ConfirmDialog confirmDialog) {

        Single
                .fromCallable(() -> Forest.client(ConfigApi.class).createConfig(getSaveReqVO()).getCheckedData())
                .subscribeOn(Schedulers.io())
                .observeOn(FxSchedulers.fx())
                .compose(FxRx.bindTo(this))
                .subscribe(r -> {
                    confirmDialog.close();
                    EventBusCenter.get().post(new UpdateDataEvent("更新参数配置列表"));
                    EventBusCenter.get().post(new MessageEvent("保存成功", MessageType.SUCCESS));
                }, DefaultExceptionHandler::handle);
    }

    public void updateConfig(ConfirmDialog confirmDialog) {
        Single
                .fromCallable(() -> Forest.client(ConfigApi.class).updateConfig(getSaveReqVO()).getCheckedData())
                .subscribeOn(Schedulers.io())
                .observeOn(FxSchedulers.fx())
                .compose(FxRx.bindTo(this))
                .subscribe(r -> {
                    confirmDialog.close();
                    EventBusCenter.get().post(new UpdateDataEvent("更新参数配置列表"));
                    EventBusCenter.get().post(new MessageEvent("保存成功", MessageType.SUCCESS));
                }, DefaultExceptionHandler::handle);
    }

    public StringProperty nameProperty() {
        return wrapper.field("name", ConfigSaveReqVO::getName, ConfigSaveReqVO::setName, "");
    }

    public StringProperty categoryProperty() {
        return wrapper.field("category", ConfigSaveReqVO::getCategory, ConfigSaveReqVO::setCategory, "");
    }

    public StringProperty keyProperty() {
        return wrapper.field("key", ConfigSaveReqVO::getKey, ConfigSaveReqVO::setKey);
    }

    public StringProperty valueProperty() {
        return wrapper.field("value", ConfigSaveReqVO::getValue, ConfigSaveReqVO::setValue);
    }

    public BooleanProperty visibleProperty() {
        return wrapper.field("visible", ConfigSaveReqVO::getVisible, ConfigSaveReqVO::setVisible);
    }


    public StringProperty remartProperty() {
        return wrapper.field("remark", ConfigSaveReqVO::getRemark, ConfigSaveReqVO::setRemark);
    }

    public DictDataSimpleRespVO getVisbleSel() {
        return visbleSel.get();
    }

    public ObjectProperty<DictDataSimpleRespVO> visbleSelProperty() {
        return visbleSel;
    }

    public void setVisbleSel(DictDataSimpleRespVO visbleSel) {
        this.visbleSel.set(visbleSel);
    }
}

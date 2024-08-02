package com.lw.fx.view.infra.config;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.Convert;
import com.lw.dillon.admin.framework.common.pojo.CommonResult;
import com.lw.dillon.admin.module.infra.controller.admin.config.vo.ConfigSaveReqVO;
import com.lw.dillon.admin.module.system.controller.admin.dict.vo.data.DictDataSimpleRespVO;
import com.lw.fx.request.Request;
import com.lw.fx.store.AppStore;
import com.lw.ui.request.api.config.ConfigFeign;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.mapping.ModelWrapper;
import io.datafx.core.concurrent.ProcessChain;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;

import java.util.Optional;

import static com.lw.ui.utils.DictTypeEnum.INFRA_BOOLEAN_STRING;

public class ConfigFormViewModel implements ViewModel {

    private ModelWrapper<ConfigSaveReqVO> wrapper = new ModelWrapper<>();
    private ObjectProperty<DictDataSimpleRespVO> visbleSel = new SimpleObjectProperty<>();


    public ConfigFormViewModel() {
    }

    public ConfigSaveReqVO getSaveReqVO() {
        visibleProperty().set(Convert.toBool(visbleSel.get().getValue(),false));
        wrapper.commit();
        return wrapper.get();
    }


    public void query(Long id) {

        ProcessChain.create()
                .addSupplierInExecutor(() -> {
                    if (id == null) {
                        return new ConfigSaveReqVO();
                    }
                    return Request.connector(ConfigFeign.class).getConfig(id).getData();
                })
                .addConsumerInPlatformThread(r -> {
                    ConfigSaveReqVO reqVO = new ConfigSaveReqVO();
                    BeanUtil.copyProperties(r, reqVO);
                    setValue(reqVO);

                })
                .onException(e -> e.printStackTrace())
                .run();
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

    public CommonResult save(boolean isAdd) {

        if (isAdd) {
            return Request.connector(ConfigFeign.class).createConfig(getSaveReqVO());
        } else {
            return Request.connector(ConfigFeign.class).updateConfig(getSaveReqVO());
        }
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


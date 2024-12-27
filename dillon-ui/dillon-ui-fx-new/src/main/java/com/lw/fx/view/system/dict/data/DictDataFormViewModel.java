package com.lw.fx.view.system.dict.data;

import cn.hutool.core.bean.BeanUtil;
import com.lw.dillon.admin.framework.common.pojo.CommonResult;
import com.lw.dillon.admin.module.system.controller.admin.dict.vo.data.DictDataSaveReqVO;
import com.lw.dillon.admin.module.system.controller.admin.dict.vo.data.DictDataSimpleRespVO;
import com.lw.fx.request.Request;
import com.lw.ui.request.api.system.DictDataFeign;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.mapping.ModelWrapper;
import io.datafx.core.concurrent.ProcessChain;
import javafx.beans.property.*;

public class DictDataFormViewModel implements ViewModel {

    private ModelWrapper<DictDataSaveReqVO> wrapper = new ModelWrapper<>();

    private BooleanProperty edit = new SimpleBooleanProperty(false);

    private StringProperty selDictType = new SimpleStringProperty();

    private ObjectProperty<DictDataSimpleRespVO> selDictDataSimpleRespVO = new SimpleObjectProperty<>();

    public DictDataFormViewModel() {
    }

    public DictDataSaveReqVO getUserSaveReqVO() {
        colorTypeProperty().set(selDictDataSimpleRespVO.getValue().getValue());
        wrapper.commit();
        return wrapper.get();
    }


    public void query(Long id) {

        ProcessChain.create()
                .addSupplierInExecutor(() -> {

                    edit.set(id != null);
                    if (id == null) {
                        DictDataSaveReqVO dictDataSaveReqVO=   new DictDataSaveReqVO();
                        dictDataSaveReqVO.setDictType(selDictType.get());
                        return dictDataSaveReqVO;
                    }
                    return Request.connector(DictDataFeign.class).getDictData(id).getData();
                })
                .addConsumerInPlatformThread(r -> {
                    DictDataSaveReqVO reqVO = new DictDataSaveReqVO();
                    BeanUtil.copyProperties(r, reqVO);
                    setDictData(reqVO);

                })
                .onException(e -> e.printStackTrace())
                .run();
    }

    /**
     * 系统设置菜单
     */
    public void setDictData(DictDataSaveReqVO dictDataRespVO) {

        wrapper.set(dictDataRespVO);
        wrapper.reload();
    }

    public CommonResult save(boolean isAdd) {

        if (isAdd) {
            return Request.connector(DictDataFeign.class).createDictData(getUserSaveReqVO());
        } else {
            return Request.connector(DictDataFeign.class).updateDictData(getUserSaveReqVO());
        }
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


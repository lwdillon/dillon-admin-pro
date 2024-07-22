package com.lw.fx.view.system.dict;

import cn.hutool.core.bean.BeanUtil;
import com.lw.dillon.admin.framework.common.pojo.CommonResult;
import com.lw.dillon.admin.module.system.controller.admin.dict.vo.type.DictTypeSaveReqVO;
import com.lw.fx.request.Request;
import com.lw.ui.request.api.system.DictTypeFeign;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.mapping.ModelWrapper;
import io.datafx.core.concurrent.ProcessChain;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.StringProperty;

public class DictTypeFormViewModel implements ViewModel {

    private ModelWrapper<DictTypeSaveReqVO> wrapper = new ModelWrapper<>();

    private BooleanProperty edit = new SimpleBooleanProperty(false);

    public DictTypeFormViewModel() {
    }

    public DictTypeSaveReqVO getUserSaveReqVO() {
        wrapper.commit();
        return wrapper.get();
    }


    public void query(Long id) {

        ProcessChain.create()
                .addSupplierInExecutor(() -> {
                    edit.set(id != null);
                    if (id == null) {
                        return new DictTypeSaveReqVO();
                    }
                    return Request.connector(DictTypeFeign.class).getDictType(id).getData();
                })
                .addConsumerInPlatformThread(r -> {
                    DictTypeSaveReqVO reqVO = new DictTypeSaveReqVO();
                    BeanUtil.copyProperties(r, reqVO);
                    setDictType(reqVO);

                })
                .onException(e -> e.printStackTrace())
                .run();
    }

    /**
     * 系统设置菜单
     */
    public void setDictType(DictTypeSaveReqVO roleRespVO) {

        wrapper.set(roleRespVO);
        wrapper.reload();
    }

    public CommonResult saveUser(boolean isAdd) {

        if (isAdd) {
            return Request.connector(DictTypeFeign.class).createDictType(getUserSaveReqVO());
        } else {
            return Request.connector(DictTypeFeign.class).updateDictType(getUserSaveReqVO());
        }
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


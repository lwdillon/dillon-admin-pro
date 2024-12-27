package com.lw.fx.view.system.role;

import cn.hutool.core.bean.BeanUtil;
import com.lw.dillon.admin.framework.common.pojo.CommonResult;
import com.lw.dillon.admin.module.system.controller.admin.permission.vo.role.RoleSaveReqVO;
import com.lw.fx.request.Request;
import com.lw.ui.request.api.system.RoleFeign;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.mapping.ModelWrapper;
import io.datafx.core.concurrent.ProcessChain;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;

public class RoleFormViewModel implements ViewModel {

    private ModelWrapper<RoleSaveReqVO> wrapper = new ModelWrapper<>();


    public RoleFormViewModel() {
    }

    public RoleSaveReqVO getUserSaveReqVO() {
        wrapper.commit();
        return wrapper.get();
    }


    public void query(Long id) {

        ProcessChain.create()
                .addSupplierInExecutor(() -> {
                    if (id == null) {
                        return new RoleSaveReqVO();
                    }
                    return Request.connector(RoleFeign.class).getRole(id).getData();
                })
                .addConsumerInPlatformThread(r -> {
                    RoleSaveReqVO reqVO = new RoleSaveReqVO();
                    BeanUtil.copyProperties(r, reqVO);
                    setRole(reqVO);

                })
                .onException(e -> e.printStackTrace())
                .run();
    }
    /**
     * 系统设置菜单
     */
    public void setRole(RoleSaveReqVO roleRespVO) {

        wrapper.set(roleRespVO);
        wrapper.reload();
    }

    public CommonResult save(boolean isAdd) {

        if (isAdd) {
            return Request.connector(RoleFeign.class).createRole(getUserSaveReqVO());
        } else {
            return Request.connector(RoleFeign.class).updateRole(getUserSaveReqVO());
        }
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


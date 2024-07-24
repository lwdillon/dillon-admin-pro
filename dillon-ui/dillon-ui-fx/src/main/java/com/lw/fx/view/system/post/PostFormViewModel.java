package com.lw.fx.view.system.post;

import cn.hutool.core.bean.BeanUtil;
import com.lw.dillon.admin.framework.common.pojo.CommonResult;
import com.lw.dillon.admin.module.system.controller.admin.dept.vo.post.PostSaveReqVO;
import com.lw.fx.request.Request;
import com.lw.ui.request.api.system.PostFeign;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.mapping.ModelWrapper;
import io.datafx.core.concurrent.ProcessChain;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;

public class PostFormViewModel implements ViewModel {

    private ModelWrapper<PostSaveReqVO> wrapper = new ModelWrapper<>();


    public PostFormViewModel() {
    }

    public PostSaveReqVO getUserSaveReqVO() {
        wrapper.commit();
        return wrapper.get();
    }


    public void query(Long id) {

        ProcessChain.create()
                .addSupplierInExecutor(() -> {
                    if (id == null) {
                        return new PostSaveReqVO();
                    }
                    return Request.connector(PostFeign.class).getPost(id).getData();
                })
                .addConsumerInPlatformThread(r -> {
                    PostSaveReqVO reqVO = new PostSaveReqVO();
                    BeanUtil.copyProperties(r, reqVO);
                    setPost(reqVO);

                })
                .onException(e -> e.printStackTrace())
                .run();
    }
    /**
     * 系统设置菜单
     */
    public void setPost(PostSaveReqVO roleRespVO) {

        wrapper.set(roleRespVO);
        wrapper.reload();
    }

    public CommonResult save(boolean isAdd) {

        if (isAdd) {
            return Request.connector(PostFeign.class).createPost(getUserSaveReqVO());
        } else {
            return Request.connector(PostFeign.class).updatePost(getUserSaveReqVO());
        }
    }

    public StringProperty nameProperty() {
        return wrapper.field("name", PostSaveReqVO::getName, PostSaveReqVO::setName, "");
    }

    public StringProperty codeProperty() {
        return wrapper.field("code", PostSaveReqVO::getCode, PostSaveReqVO::setCode, "");
    }

    public IntegerProperty sortProperty() {
        return wrapper.field("sort", PostSaveReqVO::getSort, PostSaveReqVO::setSort);
    }

    public IntegerProperty statusProperty() {
        return wrapper.field("status", PostSaveReqVO::getStatus, PostSaveReqVO::setStatus);
    }

    public StringProperty remartProperty() {
        return wrapper.field("remark", PostSaveReqVO::getRemark, PostSaveReqVO::setRemark);
    }

}


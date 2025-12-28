package com.dillon.lw.fx.view.system.post;

import cn.hutool.core.bean.BeanUtil;
import com.dillon.lw.module.system.controller.admin.dept.vo.post.PostSaveReqVO;
import com.dillon.lw.api.system.PostApi;
import com.dillon.lw.fx.eventbus.EventBusCenter;
import com.dillon.lw.fx.eventbus.event.MessageEvent;
import com.dillon.lw.fx.eventbus.event.UpdateDataEvent;
import com.dillon.lw.fx.http.PayLoad;
import com.dillon.lw.fx.mvvm.base.BaseViewModel;
import com.dillon.lw.fx.mvvm.mapping.ModelWrapper;
import com.dillon.lw.fx.utils.MessageType;
import com.dillon.lw.fx.view.layout.ConfirmDialog;
import com.dtflys.forest.Forest;
import com.dillon.lw.module.system.controller.admin.dept.vo.post.PostRespVO;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.StringProperty;

import java.util.concurrent.CompletableFuture;

public class PostFormViewModel extends BaseViewModel {


    private ModelWrapper<PostSaveReqVO> wrapper = new ModelWrapper<>();

    private StringProperty name = wrapper.field(PostSaveReqVO::getName, PostSaveReqVO::setName, "");
    private StringProperty code = wrapper.field(PostSaveReqVO::getCode, PostSaveReqVO::setCode, "");
    private StringProperty remark = wrapper.field(PostSaveReqVO::getRemark, PostSaveReqVO::setRemark, "");
    private IntegerProperty status = wrapper.field(PostSaveReqVO::getStatus, PostSaveReqVO::setStatus, 0);
    private IntegerProperty sort = wrapper.field(PostSaveReqVO::getSort, PostSaveReqVO::setSort, 0);
    private LongProperty id = wrapper.field(PostSaveReqVO::getId, PostSaveReqVO::setId, 0L);

    public PostFormViewModel() {
    }

    public PostSaveReqVO getUserSaveReqVO() {
        wrapper.commit();
        return wrapper.get();
    }


    public void query(Long id) {

        if (id == null) {
            return;
        }
        CompletableFuture.supplyAsync(() -> {
            return new PayLoad<PostRespVO>().apply(Forest.client(PostApi.class).getPost(id));
        }).thenAcceptAsync(result -> {
            PostSaveReqVO reqVO = new PostSaveReqVO();
            BeanUtil.copyProperties(result, reqVO);
            setPost(reqVO);
        }, Platform::runLater).exceptionally(throwable -> {
            System.err.println("查询岗位异常：" + throwable.getMessage());
            return null;
        });
    }

    /**
     * 系统设置菜单
     */
    public void setPost(PostSaveReqVO roleRespVO) {

        wrapper.set(roleRespVO);
        wrapper.reload();
    }


    public void updatePost(ConfirmDialog dialog) {

        CompletableFuture.supplyAsync(() -> {
            return new PayLoad<Boolean>().apply(Forest.client(PostApi.class).updatePost(getUserSaveReqVO()));
        }).thenAcceptAsync(result -> {
            EventBusCenter.get().post(new MessageEvent("更新岗位成功", MessageType.SUCCESS));
            EventBusCenter.get().post(new UpdateDataEvent("更新岗位列表"));
            dialog.close();
        }, Platform::runLater).exceptionally(throwable -> {
            System.err.println("更新岗位异常：" + throwable.getMessage());
            return null;
        });
    }

    public void createPost(ConfirmDialog dialog) {

        CompletableFuture.supplyAsync(() -> {
            return new PayLoad<Long>().apply(Forest.client(PostApi.class).createPost(getUserSaveReqVO()));
        }).thenAcceptAsync(result -> {
            EventBusCenter.get().post(new MessageEvent("创建岗位成功", MessageType.SUCCESS));
            EventBusCenter.get().post(new UpdateDataEvent("更新岗位列表"));
            dialog.close();
        }, Platform::runLater).exceptionally(throwable -> {
            System.err.println("创建岗位异常：" + throwable.getMessage());
            return null;
        });
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public String getCode() {
        return code.get();
    }

    public StringProperty codeProperty() {
        return code;
    }

    public String getRemark() {
        return remark.get();
    }

    public StringProperty remarkProperty() {
        return remark;
    }

    public int getStatus() {
        return status.get();
    }

    public IntegerProperty statusProperty() {
        return status;
    }

    public int getSort() {
        return sort.get();
    }

    public IntegerProperty sortProperty() {
        return sort;
    }

    public long getId() {
        return id.get();
    }

    public LongProperty idProperty() {
        return id;
    }
}


package com.dillon.lw.fx.view.infra.job;

import cn.hutool.core.bean.BeanUtil;
import com.dillon.lw.module.infra.controller.admin.job.vo.job.JobRespVO;
import com.dillon.lw.module.infra.controller.admin.job.vo.job.JobSaveReqVO;
import com.dillon.lw.api.infra.JobApi;
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
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.StringProperty;

public class JobFormViewModel extends BaseViewModel {

    private Long id = null;
    private ModelWrapper<JobRespVO> wrapper = new ModelWrapper<>();


    public void updateData(Long id) {

        if (id == null) {
            setWrapper(new JobRespVO());
            return;
        }

        this.id = id;

        Request.getInstance().create(JobApi.class).getJob(id)
                .subscribeOn(Schedulers.io())
                .map(new PayLoad<>())
                .observeOn(Schedulers.from(Platform::runLater))
                .subscribe(data -> {
                    setWrapper(data);
                }, e -> {
                    e.printStackTrace();
                });

    }

    public void setWrapper(JobRespVO respVO) {

        wrapper.set(respVO);
        wrapper.reload();
    }


    public void createJob(ConfirmDialog confirmDialog) {
        Request.getInstance().create(JobApi.class).createJob(getSaveReqVO())
                .subscribeOn(Schedulers.io())
                .map(new PayLoad<>())
                .observeOn(Schedulers.from(Platform::runLater))
                .subscribe(data -> {
                    confirmDialog.close();
                    confirmDialog.close();
                    EventBusCenter.get().post(new UpdateDataEvent("更新job配置列表"));
                    EventBusCenter.get().post(new MessageEvent("保存成功", MessageType.SUCCESS));
                }, e -> {
                    e.printStackTrace();
                    confirmDialog.close();
                    EventBusCenter.get().post(new MessageEvent("保存失败", MessageType.DANGER));
                });
    }
    public void updateJob(ConfirmDialog confirmDialog) {
        Request.getInstance().create(JobApi.class).updateJob(getSaveReqVO())
                .subscribeOn(Schedulers.io())
                .map(new PayLoad<>())
                .observeOn(Schedulers.from(Platform::runLater))
                .subscribe(data -> {
                    confirmDialog.close();
                    EventBusCenter.get().post(new UpdateDataEvent("更新job配置列表"));
                    EventBusCenter.get().post(new MessageEvent("更新成功", MessageType.SUCCESS));
                }, e -> {
                    e.printStackTrace();
                    confirmDialog.close();
                    EventBusCenter.get().post(new MessageEvent("更新失败", MessageType.DANGER));
                });
    }

    public JobSaveReqVO getSaveReqVO() {
        wrapper.commit();
        JobRespVO jobRespVO = wrapper.get();
        JobSaveReqVO saveVo = new JobSaveReqVO();
        BeanUtil.copyProperties(jobRespVO, saveVo);
        return saveVo;
    }


    public LongProperty idProperty() {
        return wrapper.field("id", JobRespVO::getId, JobRespVO::setId);
    }

    public StringProperty nameProperty() {
        return wrapper.field("name", JobRespVO::getName, JobRespVO::setName, "");
    }

    public StringProperty handlerNameProperty() {
        return wrapper.field("handlerName", JobRespVO::getHandlerName, JobRespVO::setHandlerName, "");
    }

    public StringProperty handlerParamProperty() {
        return wrapper.field("handlerParam", JobRespVO::getHandlerParam, JobRespVO::setHandlerParam, "");
    }

    public StringProperty cronExpressionProperty() {
        return wrapper.field("cronExpression", JobRespVO::getCronExpression, JobRespVO::setCronExpression, "");
    }

    public IntegerProperty statusProperty() {
        return wrapper.field("status", JobRespVO::getStatus, JobRespVO::setStatus);
    }

    public IntegerProperty retryCountProperty() {
        return wrapper.field("retryCount", JobRespVO::getRetryCount, JobRespVO::setRetryCount);
    }

    public IntegerProperty retryIntervalProperty() {
        return wrapper.field("retryInterval", JobRespVO::getRetryInterval, JobRespVO::setRetryInterval);
    }

    public IntegerProperty monitorTimeoutProperty() {
        return wrapper.field("monitorTimeout", JobRespVO::getMonitorTimeout, JobRespVO::setMonitorTimeout);
    }


}

package com.dillon.lw.fx.view.infra.job;

import cn.hutool.core.bean.BeanUtil;
import com.dillon.lw.api.infra.JobApi;
import com.dillon.lw.fx.DefaultExceptionHandler;
import com.dillon.lw.fx.eventbus.EventBusCenter;
import com.dillon.lw.fx.eventbus.event.MessageEvent;
import com.dillon.lw.fx.eventbus.event.UpdateDataEvent;
import com.dillon.lw.fx.mvvm.base.BaseViewModel;
import com.dillon.lw.fx.mvvm.mapping.ModelWrapper;
import com.dillon.lw.fx.rx.FxSchedulers;
import com.dillon.lw.fx.rx.FxRx;
import com.dillon.lw.fx.utils.MessageType;
import com.dillon.lw.fx.view.layout.ConfirmDialog;
import com.dillon.lw.module.infra.controller.admin.job.vo.job.JobRespVO;
import com.dillon.lw.module.infra.controller.admin.job.vo.job.JobSaveReqVO;
import com.dtflys.forest.Forest;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.StringProperty;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class JobFormViewModel extends BaseViewModel {

    private Long id = null;
    private ModelWrapper<JobRespVO> wrapper = new ModelWrapper<>();


    public void updateData(Long id) {

        if (id == null) {
            setWrapper(new JobRespVO());
            return;
        }

        this.id = id;

        Single
                .fromCallable(() -> Forest.client(JobApi.class).getJob(id).getCheckedData())
                .subscribeOn(Schedulers.io())
                .observeOn(FxSchedulers.fx())
                .compose(FxRx.bindTo(this))
                .subscribe(this::setWrapper, DefaultExceptionHandler::handle);

    }

    public void setWrapper(JobRespVO respVO) {

        wrapper.set(respVO);
        wrapper.reload();
    }


    public void createJob(ConfirmDialog confirmDialog) {
        Single
                .fromCallable(() -> Forest.client(JobApi.class).createJob(getSaveReqVO()).getCheckedData())
                .subscribeOn(Schedulers.io())
                .observeOn(FxSchedulers.fx())
                .compose(FxRx.bindTo(this))
                .subscribe(data -> {
                    confirmDialog.close();
                    EventBusCenter.get().post(new UpdateDataEvent("更新job配置列表"));
                    EventBusCenter.get().post(new MessageEvent("保存成功", MessageType.SUCCESS));
                }, e -> {
                    DefaultExceptionHandler.handle(e);
                    confirmDialog.close();
                });
    }

    public void updateJob(ConfirmDialog confirmDialog) {
        Single
                .fromCallable(() -> Forest.client(JobApi.class).updateJob(getSaveReqVO()).getCheckedData())
                .subscribeOn(Schedulers.io())
                .observeOn(FxSchedulers.fx())
                .compose(FxRx.bindTo(this))
                .subscribe(data -> {
                    confirmDialog.close();
                    EventBusCenter.get().post(new UpdateDataEvent("更新job配置列表"));
                    EventBusCenter.get().post(new MessageEvent("更新成功", MessageType.SUCCESS));
                }, e -> {
                    DefaultExceptionHandler.handle(e);
                    confirmDialog.close();
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

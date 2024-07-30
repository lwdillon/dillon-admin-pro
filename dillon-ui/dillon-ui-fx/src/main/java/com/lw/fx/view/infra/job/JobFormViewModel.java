package com.lw.fx.view.infra.job;

import cn.hutool.core.bean.BeanUtil;
import com.lw.dillon.admin.framework.common.pojo.CommonResult;
import com.lw.dillon.admin.module.infra.controller.admin.job.vo.job.JobRespVO;
import com.lw.dillon.admin.module.infra.controller.admin.job.vo.job.JobSaveReqVO;
import com.lw.dillon.admin.module.infra.controller.admin.job.vo.job.JobSaveReqVO;
import com.lw.dillon.admin.module.system.controller.admin.permission.vo.role.RoleSaveReqVO;
import com.lw.fx.request.Request;
import com.lw.ui.request.api.job.JobFeign;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.mapping.ModelWrapper;
import io.datafx.core.concurrent.ProcessChain;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.StringProperty;

public class JobFormViewModel implements ViewModel {

    private Long id = null;
    private ModelWrapper<JobRespVO> wrapper = new ModelWrapper<>();


    public void updateData(Long id, boolean isAdd) {

        this.id = id;
        ProcessChain.create()
                .addSupplierInExecutor(() -> {
                    if (id == null) {
                        return new JobRespVO();
                    }
                    return Request.connector(JobFeign.class).getJob(id).getData();

                })
                .addConsumerInPlatformThread(rel -> {

                    setWrapper(rel);

                })

                .run();

    }

    public void setWrapper(JobRespVO respVO) {

        wrapper.set(respVO);
        wrapper.reload();
    }

    public CommonResult save(boolean isAdd) {

        if (isAdd) {
            return Request.connector(JobFeign.class).createJob(getSaveReqVO());
        } else {
            return Request.connector(JobFeign.class).updateJob(getSaveReqVO());
        }
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

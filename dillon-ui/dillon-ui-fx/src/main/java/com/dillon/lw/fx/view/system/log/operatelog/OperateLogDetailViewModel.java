package com.dillon.lw.fx.view.system.log.operatelog;

import com.dillon.lw.module.system.controller.admin.logger.vo.operatelog.OperateLogRespVO;
import com.dillon.lw.fx.mvvm.base.BaseViewModel;
import com.dillon.lw.fx.mvvm.mapping.ModelWrapper;
import javafx.beans.property.*;

import java.time.LocalDateTime;

public class OperateLogDetailViewModel extends BaseViewModel {

    private ModelWrapper<OperateLogRespVO> wrapper = new ModelWrapper<>();

    private BooleanProperty edit = new SimpleBooleanProperty(false);

    public OperateLogDetailViewModel() {
    }


    public void query(OperateLogRespVO operateLogRespVO) {

        wrapper.set(operateLogRespVO);
    }


    public LongProperty idProperty() {
        return wrapper.field("id", OperateLogRespVO::getId, OperateLogRespVO::setId);
    }

    public LongProperty userIdProperty() {
        return wrapper.field("userId", OperateLogRespVO::getUserId, OperateLogRespVO::setUserId);
    }

    public LongProperty bizIdProperty() {
        return wrapper.field("bizId", OperateLogRespVO::getBizId, OperateLogRespVO::setBizId);
    }

    public StringProperty traceIdProperty() {
        return wrapper.field("traceId", OperateLogRespVO::getTraceId, OperateLogRespVO::setTraceId, "");
    }

    public StringProperty userNameProperty() {
        return wrapper.field("userName", OperateLogRespVO::getUserName, OperateLogRespVO::setUserName);
    }

    public StringProperty typeProperty() {
        return wrapper.field("type", OperateLogRespVO::getType, OperateLogRespVO::setType);
    }

    public StringProperty subTypeProperty() {
        return wrapper.field("subType", OperateLogRespVO::getSubType, OperateLogRespVO::setSubType);
    }

    public StringProperty actionProperty() {
        return wrapper.field("action", OperateLogRespVO::getAction, OperateLogRespVO::setAction);
    }

    public StringProperty extraProperty() {
        return wrapper.field("extra", OperateLogRespVO::getExtra, OperateLogRespVO::setExtra);
    }

    public StringProperty requestMethodProperty() {
        return wrapper.field("requestMethod", OperateLogRespVO::getRequestMethod, OperateLogRespVO::setRequestMethod);
    }

    public StringProperty requestUrlProperty() {
        return wrapper.field("requestUrl", OperateLogRespVO::getRequestUrl, OperateLogRespVO::setRequestUrl);
    }

    public StringProperty userIpProperty() {
        return wrapper.field("userIp", OperateLogRespVO::getUserIp, OperateLogRespVO::setUserIp);
    }

    public StringProperty userAgentProperty() {
        return wrapper.field("userAgent", OperateLogRespVO::getUserAgent, OperateLogRespVO::setUserAgent);
    }

    public ObjectProperty<LocalDateTime> createTimeProperty() {
        return wrapper.field("createTime", OperateLogRespVO::getCreateTime, OperateLogRespVO::setCreateTime);
    }

    public boolean isEdit() {
        return edit.get();
    }

    public BooleanProperty editProperty() {
        return edit;
    }
}


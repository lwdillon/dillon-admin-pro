package com.lw.fx.view.system.loginlog;

import com.lw.dillon.admin.module.system.controller.admin.logger.vo.loginlog.LoginLogRespVO;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.mapping.ModelWrapper;
import javafx.beans.property.*;

import java.time.LocalDateTime;

public class LoginLogDetailViewModel implements ViewModel {

    private ModelWrapper<LoginLogRespVO> wrapper = new ModelWrapper<>();

    private BooleanProperty edit = new SimpleBooleanProperty(false);

    public LoginLogDetailViewModel() {
    }


    public void queryDictType(LoginLogRespVO operateLogRespVO) {

        wrapper.set(operateLogRespVO);
    }


    public LongProperty idProperty() {
        return wrapper.field("id", LoginLogRespVO::getId, LoginLogRespVO::setId);
    }

    public LongProperty userIdProperty() {
        return wrapper.field("userId", LoginLogRespVO::getUserId, LoginLogRespVO::setUserId);
    }


    public StringProperty traceIdProperty() {
        return wrapper.field("traceId", LoginLogRespVO::getTraceId, LoginLogRespVO::setTraceId, "");
    }

    public StringProperty userNameProperty() {
        return wrapper.field("username", LoginLogRespVO::getUsername, LoginLogRespVO::setUsername);
    }

    public IntegerProperty logTypeProperty() {
        return wrapper.field("logType", LoginLogRespVO::getLogType, LoginLogRespVO::setLogType,-1);
    }


    public IntegerProperty resultProperty() {
        return wrapper.field("result", LoginLogRespVO::getResult, LoginLogRespVO::setResult);
    }


    public StringProperty userIpProperty() {
        return wrapper.field("userIp", LoginLogRespVO::getUserIp, LoginLogRespVO::setUserIp);
    }

    public StringProperty userAgentProperty() {
        return wrapper.field("userAgent", LoginLogRespVO::getUserAgent, LoginLogRespVO::setUserAgent);
    }

    public ObjectProperty<LocalDateTime> createTimeProperty() {
        return wrapper.field("createTime", LoginLogRespVO::getCreateTime, LoginLogRespVO::setCreateTime);
    }

    public boolean isEdit() {
        return edit.get();
    }

    public BooleanProperty editProperty() {
        return edit;
    }
}


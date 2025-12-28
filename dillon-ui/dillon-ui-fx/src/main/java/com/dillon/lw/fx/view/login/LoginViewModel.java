package com.dillon.lw.fx.view.login;


import com.dillon.lw.module.system.controller.admin.auth.vo.AuthLoginReqVO;
import com.dillon.lw.api.system.AuthApi;
import com.dillon.lw.fx.eventbus.EventBusCenter;
import com.dillon.lw.fx.eventbus.event.LoginSuccessEvent;
import com.dillon.lw.fx.http.PayLoad;
import com.dillon.lw.fx.mvvm.base.BaseViewModel;
import com.dillon.lw.fx.store.AppStore;
import com.dtflys.forest.Forest;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.concurrent.CompletableFuture;

public class LoginViewModel extends BaseViewModel {

    private StringProperty username = new SimpleStringProperty();
    private StringProperty password = new SimpleStringProperty();
    private StringProperty msg = new SimpleStringProperty();
    private BooleanProperty progressbarVisible = new SimpleBooleanProperty(false);
    private BooleanProperty loginButDisable = new SimpleBooleanProperty(false);

    public void login() {

        AuthLoginReqVO loginReqVO = new AuthLoginReqVO();
        loginReqVO.setUsername(getUsername());
        loginReqVO.setPassword(getPassword());

        progressbarVisible.set(true);
        loginButDisable.setValue(true);

        CompletableFuture.supplyAsync(() -> {
            return new PayLoad<com.dillon.lw.module.system.controller.admin.auth.vo.AuthLoginRespVO>().apply(Forest.client(AuthApi.class).login(loginReqVO));
        }).thenAcceptAsync(data -> {
            AppStore.setToken(data.getAccessToken());
            AppStore.loadDictData();
            EventBusCenter.get().post(new LoginSuccessEvent());
            msg.set("");
        }, Platform::runLater).whenCompleteAsync((unused, throwable) -> {
            progressbarVisible.setValue(false);
            loginButDisable.set(false);
            if (throwable != null) {
                msg.set(throwable.getCause() != null ? throwable.getCause().getMessage() : throwable.getMessage());
            }
        }, Platform::runLater);

    }

    public String getUsername() {
        return username.get();
    }

    public StringProperty usernameProperty() {
        return username;
    }

    public String getPassword() {
        return password.get();
    }

    public StringProperty passwordProperty() {
        return password;
    }

    public String getMsg() {
        return msg.get();
    }

    public StringProperty msgProperty() {
        return msg;
    }

    public boolean isProgressbarVisible() {
        return progressbarVisible.get();
    }

    public BooleanProperty progressbarVisibleProperty() {
        return progressbarVisible;
    }

    public boolean isLoginButDisable() {
        return loginButDisable.get();
    }

    public BooleanProperty loginButDisableProperty() {
        return loginButDisable;
    }
}

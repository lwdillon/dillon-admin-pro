package com.dillon.lw.fx.view.login;


import com.dillon.lw.module.system.controller.admin.auth.vo.AuthLoginReqVO;
import com.dillon.lw.api.system.AuthApi;
import com.dillon.lw.fx.eventbus.EventBusCenter;
import com.dillon.lw.fx.eventbus.event.LoginSuccessEvent;
import com.dillon.lw.fx.http.PayLoad;
import com.dillon.lw.fx.http.Request;
import com.dillon.lw.fx.mvvm.base.BaseViewModel;
import com.dillon.lw.fx.store.AppStore;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

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

        Observable.just(1)
                .observeOn(Schedulers.from(Platform::runLater)) // 1️⃣ 先到 UI 线程
                .doOnNext(ignore -> {
                    // 更新 UI：loading 开始
                    progressbarVisible.set(true);
                    loginButDisable.setValue(true);
                })
                .observeOn(Schedulers.io()) // 2️⃣ 切到 IO 线程执行 login()
                .flatMap(ignore -> {

                            return Request.getInstance().create(AuthApi.class).login(loginReqVO);
                        }
                ).map(new PayLoad<>())
                .observeOn(Schedulers.from(Platform::runLater)) // 3️⃣ 再回 UI 线程更新结果
                .doFinally(() -> {
                    // loading 结束
                    progressbarVisible.setValue(false);
                    loginButDisable.set(false);
                })
                .subscribe(
                        data -> {
                            AppStore.setToken(data.getAccessToken());
                            AppStore.loadDictData();
                            EventBusCenter.get().post(new LoginSuccessEvent());
                            msg.set("");
                        },
                        throwable -> {
                            // 登录失败
                            msg.set(throwable.getMessage());
                        }
                );

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

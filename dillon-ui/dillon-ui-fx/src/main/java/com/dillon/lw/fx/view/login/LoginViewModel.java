package com.dillon.lw.fx.view.login;


import com.dillon.lw.api.system.AuthApi;
import com.dillon.lw.fx.DefaultExceptionHandler;
import com.dillon.lw.fx.eventbus.EventBusCenter;
import com.dillon.lw.fx.eventbus.event.LoginSuccessEvent;
import com.dillon.lw.fx.mvvm.base.BaseViewModel;
import com.dillon.lw.fx.rx.FxSchedulers;
import com.dillon.lw.fx.rx.FxRx;
import com.dillon.lw.fx.store.AppStore;
import com.dillon.lw.module.system.controller.admin.auth.vo.AuthLoginReqVO;
import com.dtflys.forest.Forest;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
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
        final AuthLoginReqVO loginReqVO = new AuthLoginReqVO();
        loginReqVO.setUsername(getUsername());
        loginReqVO.setPassword(getPassword());

        Single
                /*
                 * 登录请求和 Swing 模块保持同样的 RxJava 写法：
                 * 同步接口在 IO 线程执行，登录成功后的状态回填和界面切换回到 JavaFX UI 线程。
                 */
                .fromCallable(() -> Forest.client(AuthApi.class).login(loginReqVO).getCheckedData())
                .subscribeOn(Schedulers.io())
                .observeOn(FxSchedulers.fx())
                .doOnSubscribe(disposable -> {
                    /*
                     * doOnSubscribe 不保证天然运行在 UI 线程，
                     * 所以显式切回 JavaFX UI 线程去控制进度条和登录按钮状态。
                     */
                    FxSchedulers.runOnFx(() -> {
                        progressbarVisible.set(true);
                        loginButDisable.set(true);
                    });
                })
                .doFinally(() -> {
                    /*
                     * 不论登录成功还是失败，都要恢复按钮和进度条。
                     * doFinally 同样通过 JavaFX UI 线程执行，避免跨线程改属性。
                     */
                    FxSchedulers.runOnFx(() -> {
                        progressbarVisible.set(false);
                        loginButDisable.set(false);
                    });
                })
                .compose(FxRx.bindTo(this))
                .subscribe(data -> {
                    AppStore.setToken(data.getAccessToken());
                    AppStore.loadDictData();
                    EventBusCenter.get().post(new LoginSuccessEvent());
                    msg.set("");
                }, DefaultExceptionHandler::handle);
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

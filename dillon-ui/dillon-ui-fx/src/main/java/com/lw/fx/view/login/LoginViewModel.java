package com.lw.fx.view.login;

import com.lw.dillon.admin.module.system.controller.admin.auth.vo.AuthLoginReqVO;
import com.lw.fx.request.Request;
import com.lw.fx.store.AppStore;
import com.lw.ui.request.api.system.AuthFeign;
import de.saxsys.mvvmfx.MvvmFX;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.mapping.ModelWrapper;
import io.datafx.core.concurrent.ProcessChain;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class LoginViewModel implements ViewModel {

    private ModelWrapper<AuthLoginReqVO> authLoginReqVOModelWrapper = new ModelWrapper<>();
    private BooleanProperty loginStautsProperty = new SimpleBooleanProperty(false);
    private StringProperty msgProperty = new SimpleStringProperty("");



    public LoginViewModel() {
        authLoginReqVOModelWrapper.set(new AuthLoginReqVO());
        authLoginReqVOModelWrapper.reload();
    }


    public void login() {

        ProcessChain.create().addRunnableInPlatformThread(() -> {
                    loginStautsProperty.set(true);
                })
                .addSupplierInExecutor(() -> {
                    authLoginReqVOModelWrapper.commit();
                    return Request.connector(AuthFeign.class).login(authLoginReqVOModelWrapper.get());
                })

                .addConsumerInPlatformThread(r -> {

                    if (r.isSuccess()) {
                        AppStore.setToken(r.getData().getAccessToken());
                        MvvmFX.getNotificationCenter().publish("showMainView", "显示主界面");
                        msgProperty.set("");
                        AppStore.loadDictData();
                    } else {
                        msgProperty.set(r.getMsg());
                    }
                })
                .onException(e -> {
                    msgProperty.set(e.getLocalizedMessage());
                    e.printStackTrace();})
                .withFinal(() -> {
                    loginStautsProperty.set(false);
                })
                .run();
    }




    public BooleanProperty loginStautsPropertyProperty() {
        return loginStautsProperty;
    }


    public StringProperty msgPropertyProperty() {
        return msgProperty;
    }

    public StringProperty usernameProperty() {
        return authLoginReqVOModelWrapper.field("username", AuthLoginReqVO::getUsername, AuthLoginReqVO::setUsername, "admin");
    }

    public StringProperty passwordProperty() {
        return authLoginReqVOModelWrapper.field("password", AuthLoginReqVO::getPassword, AuthLoginReqVO::setPassword, "admin123");
    }



}

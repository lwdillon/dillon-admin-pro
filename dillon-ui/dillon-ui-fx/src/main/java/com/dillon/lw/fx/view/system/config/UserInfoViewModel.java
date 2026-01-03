package com.dillon.lw.fx.view.system.config;

import com.dillon.lw.api.system.UserProfileApi;
import com.dillon.lw.fx.eventbus.EventBusCenter;
import com.dillon.lw.fx.eventbus.event.MessageEvent;
import com.dillon.lw.fx.http.PayLoad;
import com.dillon.lw.fx.mvvm.base.BaseViewModel;
import com.dillon.lw.fx.mvvm.mapping.ModelWrapper;
import com.dillon.lw.fx.utils.MessageType;
import com.dillon.lw.module.system.controller.admin.dept.vo.dept.DeptSimpleRespVO;
import com.dillon.lw.module.system.controller.admin.dept.vo.post.PostSimpleRespVO;
import com.dillon.lw.module.system.controller.admin.permission.vo.role.RoleSimpleRespVO;
import com.dillon.lw.module.system.controller.admin.user.vo.profile.UserProfileRespVO;
import com.dillon.lw.module.system.controller.admin.user.vo.profile.UserProfileUpdatePasswordReqVO;
import com.dillon.lw.module.system.controller.admin.user.vo.profile.UserProfileUpdateReqVO;
import com.dtflys.forest.Forest;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

public class UserInfoViewModel extends BaseViewModel {

    private ModelWrapper<UserProfileRespVO> wrapper = new ModelWrapper<>();

    public UserInfoViewModel() {
        initData();

    }

    public void setUserProfile(UserProfileRespVO userProfile) {
        wrapper.set(userProfile);
        wrapper.reload();
    }

    public void initData() {


        CompletableFuture.supplyAsync(() -> {
            return new PayLoad<UserProfileRespVO>().apply(Forest.client(UserProfileApi.class).getUserProfile());
        }).thenAcceptAsync(data -> {
            setUserProfile(data);
        }, Platform::runLater).exceptionally(e -> {
            e.printStackTrace();
            return null;
        });

    }

    public void updateUserProfile(UserProfileUpdateReqVO userProfileUpdateReqVO) {


        CompletableFuture.supplyAsync(() -> {
            return new PayLoad<Boolean>().apply(Forest.client(UserProfileApi.class).updateUserProfile(userProfileUpdateReqVO));
        }).thenAcceptAsync(data -> {
            EventBusCenter.get().post(new MessageEvent("保存成功！", MessageType.SUCCESS));
            initData();
        }, Platform::runLater).exceptionally(e -> {
            e.printStackTrace();
            return null;
        });

    }

    public void updateUserProfilePassword(UserProfileUpdatePasswordReqVO userProfileUpdatePasswordReqVO) {


        CompletableFuture.supplyAsync(() -> {
            return new PayLoad<Boolean>().apply(Forest.client(UserProfileApi.class).updateUserProfilePassword(userProfileUpdatePasswordReqVO));
        }).thenAcceptAsync(data -> {
            EventBusCenter.get().post(new MessageEvent("保存成功！", MessageType.SUCCESS));
        }, Platform::runLater).exceptionally(e -> {
            e.printStackTrace();
            return null;
        });

    }

    public StringProperty userNameProperty() {
        return wrapper.field("username", UserProfileRespVO::getUsername, UserProfileRespVO::setUsername, "");
    }

    public StringProperty phonenumberProperty() {
        return wrapper.field("mobile", UserProfileRespVO::getMobile, UserProfileRespVO::setMobile, "");
    }

    public StringProperty emailProperty() {
        return wrapper.field("email", UserProfileRespVO::getEmail, UserProfileRespVO::setEmail, "");
    }

    public StringProperty nicknameProperty() {
        return wrapper.field("nickname", UserProfileRespVO::getNickname, UserProfileRespVO::setNickname, "");
    }


    public ObjectProperty<LocalDateTime> createTimeProperty() {
        return wrapper.field("createTime", UserProfileRespVO::getCreateTime, UserProfileRespVO::setCreateTime, null);
    }

    public ListProperty<RoleSimpleRespVO> rolesProperty() {
        return wrapper.field("roles", UserProfileRespVO::getRoles, UserProfileRespVO::setRoles);
    }

    public ListProperty<PostSimpleRespVO> postsProperty() {
        return wrapper.field("posts", UserProfileRespVO::getPosts, UserProfileRespVO::setPosts);
    }

    public ObjectProperty<DeptSimpleRespVO> deptProperty() {
        return wrapper.field("dept", UserProfileRespVO::getDept, UserProfileRespVO::setDept, new DeptSimpleRespVO());
    }

    public IntegerProperty sexProperty() {
        return wrapper.field("sex", UserProfileRespVO::getSex, UserProfileRespVO::setSex,1);
    }


}

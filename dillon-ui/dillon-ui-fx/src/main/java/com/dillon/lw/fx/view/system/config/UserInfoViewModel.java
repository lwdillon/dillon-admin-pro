package com.dillon.lw.fx.view.system.config;

import com.dillon.lw.api.system.UserProfileApi;
import com.dillon.lw.fx.DefaultExceptionHandler;
import com.dillon.lw.fx.eventbus.EventBusCenter;
import com.dillon.lw.fx.eventbus.event.MessageEvent;
import com.dillon.lw.fx.mvvm.base.BaseViewModel;
import com.dillon.lw.fx.mvvm.mapping.ModelWrapper;
import com.dillon.lw.fx.rx.FxSchedulers;
import com.dillon.lw.fx.rx.FxRx;
import com.dillon.lw.fx.utils.MessageType;
import com.dillon.lw.module.system.controller.admin.dept.vo.dept.DeptSimpleRespVO;
import com.dillon.lw.module.system.controller.admin.dept.vo.post.PostSimpleRespVO;
import com.dillon.lw.module.system.controller.admin.permission.vo.role.RoleSimpleRespVO;
import com.dillon.lw.module.system.controller.admin.user.vo.profile.UserProfileRespVO;
import com.dillon.lw.module.system.controller.admin.user.vo.profile.UserProfileUpdatePasswordReqVO;
import com.dillon.lw.module.system.controller.admin.user.vo.profile.UserProfileUpdateReqVO;
import com.dtflys.forest.Forest;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.time.LocalDateTime;

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


        Single
                .fromCallable(() -> Forest.client(UserProfileApi.class).getUserProfile().getCheckedData())
                .subscribeOn(Schedulers.io())
                .observeOn(FxSchedulers.fx())
                .compose(FxRx.bindTo(this))
                .subscribe(this::setUserProfile, DefaultExceptionHandler::handle);

    }

    public void updateUserProfile(UserProfileUpdateReqVO userProfileUpdateReqVO) {


        Single
                .fromCallable(() -> Forest.client(UserProfileApi.class).updateUserProfile(userProfileUpdateReqVO).getCheckedData())
                .subscribeOn(Schedulers.io())
                .observeOn(FxSchedulers.fx())
                .compose(FxRx.bindTo(this))
                .subscribe(data -> {
                    EventBusCenter.get().post(new MessageEvent("保存成功！", MessageType.SUCCESS));
                    initData();
                }, DefaultExceptionHandler::handle);

    }

    public void updateUserProfilePassword(UserProfileUpdatePasswordReqVO userProfileUpdatePasswordReqVO) {


        Single
                .fromCallable(() -> Forest.client(UserProfileApi.class).updateUserProfilePassword(userProfileUpdatePasswordReqVO).getCheckedData())
                .subscribeOn(Schedulers.io())
                .observeOn(FxSchedulers.fx())
                .compose(FxRx.bindTo(this))
                .subscribe(data -> EventBusCenter.get().post(new MessageEvent("保存成功！", MessageType.SUCCESS)), DefaultExceptionHandler::handle);

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
        return wrapper.field("sex", UserProfileRespVO::getSex, UserProfileRespVO::setSex, 1);
    }


}

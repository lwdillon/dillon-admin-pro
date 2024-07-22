package com.lw.fx.view.system.config;

import com.lw.dillon.admin.module.system.controller.admin.dept.vo.dept.DeptSimpleRespVO;
import com.lw.dillon.admin.module.system.controller.admin.dept.vo.post.PostSimpleRespVO;
import com.lw.dillon.admin.module.system.controller.admin.permission.vo.role.RoleSimpleRespVO;
import com.lw.dillon.admin.module.system.controller.admin.user.vo.profile.UserProfileRespVO;
import com.lw.dillon.admin.module.system.controller.admin.user.vo.profile.UserProfileUpdatePasswordReqVO;
import com.lw.dillon.admin.module.system.controller.admin.user.vo.profile.UserProfileUpdateReqVO;
import com.lw.fx.request.Request;
import com.lw.fx.util.MessageType;
import com.lw.ui.request.api.system.UserProfileFeign;
import de.saxsys.mvvmfx.MvvmFX;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.mapping.ModelWrapper;
import io.datafx.core.concurrent.ProcessChain;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDateTime;

public class UserInfoViewModel implements ViewModel {

    private ModelWrapper<UserProfileRespVO> wrapper = new ModelWrapper<>();

    public UserInfoViewModel() {
        initData();

    }

    public void setUserProfile(UserProfileRespVO userProfile) {
        wrapper.set(userProfile);
        wrapper.reload();
    }

    public void initData() {
        ProcessChain.create()

                .addSupplierInExecutor(() -> {
                    return Request.connector(UserProfileFeign.class).getUserProfile().getCheckedData();
                })
                .addConsumerInPlatformThread(respVO -> setUserProfile(respVO))

                .onException(e -> e.printStackTrace())
                .run();

    }

    public void updateUserProfile(UserProfileUpdateReqVO userProfileUpdateReqVO) {
        ProcessChain.create()

                .addSupplierInExecutor(() -> {
                    return Request.connector(UserProfileFeign.class).updateUserProfile(userProfileUpdateReqVO);
                }).addConsumerInPlatformThread(rel->{
                    if (rel.isSuccess()) {
                        MvvmFX.getNotificationCenter().publish("message", "保存成功", MessageType.SUCCESS);
                    }
                })

                .onException(e -> e.printStackTrace())
                .run();

    }

    public void updateUserProfilePassword(UserProfileUpdatePasswordReqVO userProfileUpdatePasswordReqVO) {
        ProcessChain.create()

                .addSupplierInExecutor(() -> {
                    return Request.connector(UserProfileFeign.class).updateUserProfilePassword(userProfileUpdatePasswordReqVO);
                }).addConsumerInPlatformThread(booleanCommonResult -> {
                    if (booleanCommonResult.isSuccess()) {
                        MvvmFX.getNotificationCenter().publish("message", "保存成功", MessageType.SUCCESS);
                    }
                })

                .onException(e -> e.printStackTrace())
                .run();

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

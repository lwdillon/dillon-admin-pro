package com.dillon.lw.api.system;

import com.dillon.lw.api.BaseApi;
import com.dillon.lw.framework.common.pojo.CommonResult;
import com.dillon.lw.module.system.controller.admin.user.vo.profile.UserProfileRespVO;
import com.dillon.lw.module.system.controller.admin.user.vo.profile.UserProfileUpdatePasswordReqVO;
import com.dillon.lw.module.system.controller.admin.user.vo.profile.UserProfileUpdateReqVO;
import com.dtflys.forest.annotation.Body;
import com.dtflys.forest.annotation.DataFile;
import com.dtflys.forest.annotation.Get;
import com.dtflys.forest.annotation.Post;
import com.dtflys.forest.annotation.Put;

import java.io.File;


public interface UserProfileApi extends BaseApi {

    @Get("system/user/profile/get")
    CommonResult<UserProfileRespVO> getUserProfile();

    @Put("system/user/profile/update")
    CommonResult<Boolean> updateUserProfile(@Body UserProfileUpdateReqVO reqVO);

    @Put("system/user/profile/update-password")
    CommonResult<Boolean> updateUserProfilePassword(@Body UserProfileUpdatePasswordReqVO reqVO);

    @Post("system/user/profile/update-avatar")
    CommonResult<String> updateUserAvatar(@DataFile("file") File file) throws Exception;

}

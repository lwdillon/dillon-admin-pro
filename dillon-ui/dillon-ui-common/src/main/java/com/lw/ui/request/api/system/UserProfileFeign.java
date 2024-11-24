package com.lw.ui.request.api.system;

import com.lw.dillon.admin.framework.common.pojo.CommonResult;
import com.lw.dillon.admin.module.system.controller.admin.user.vo.profile.UserProfileRespVO;
import com.lw.dillon.admin.module.system.controller.admin.user.vo.profile.UserProfileUpdatePasswordReqVO;
import com.lw.dillon.admin.module.system.controller.admin.user.vo.profile.UserProfileUpdateReqVO;
import com.lw.ui.request.api.BaseFeignApi;
import feign.RequestLine;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.multipart.MultipartFile;


public interface UserProfileFeign extends BaseFeignApi {


    @RequestLine("GET /system/user/profile/get")
    CommonResult<UserProfileRespVO> getUserProfile();

    @RequestLine("PUT /system/user/profile/update")
    CommonResult<Boolean> updateUserProfile(UserProfileUpdateReqVO reqVO);

    @PutMapping("/update-password")
    @RequestLine("PUT /system/user/profile/update-password")
    CommonResult<Boolean> updateUserProfilePassword(UserProfileUpdatePasswordReqVO reqVO);


    @RequestLine("POST /system/user/profile/update-avatar")
    CommonResult<String> updateUserAvatar(MultipartFile file) throws Exception;

}

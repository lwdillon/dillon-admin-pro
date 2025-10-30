package com.lw.ui.api.system;

import com.lw.dillon.admin.framework.common.pojo.CommonResult;
import com.lw.dillon.admin.module.system.controller.admin.user.vo.profile.UserProfileRespVO;
import com.lw.dillon.admin.module.system.controller.admin.user.vo.profile.UserProfileUpdatePasswordReqVO;
import com.lw.dillon.admin.module.system.controller.admin.user.vo.profile.UserProfileUpdateReqVO;
import com.lw.ui.request.api.BaseFeignApi;
import io.reactivex.rxjava3.core.Observable;
import org.springframework.web.multipart.MultipartFile;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;


public interface UserProfileApi extends BaseFeignApi {

    @GET("system/user/profile/get")
    Observable<CommonResult<UserProfileRespVO>> getUserProfile();

    @PUT("system/user/profile/update")
    Observable<CommonResult<Boolean>> updateUserProfile(@Body UserProfileUpdateReqVO reqVO);

    @PUT("system/user/profile/update-password")
    Observable<CommonResult<Boolean>> updateUserProfilePassword(@Body UserProfileUpdatePasswordReqVO reqVO);

    @POST("system/user/profile/update-avatar")
    Observable<CommonResult<String>> updateUserAvatar(@Body MultipartFile file) throws Exception;

}

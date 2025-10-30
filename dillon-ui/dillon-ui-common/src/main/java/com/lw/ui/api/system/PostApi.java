package com.lw.ui.api.system;

import com.lw.dillon.admin.framework.common.pojo.CommonResult;
import com.lw.dillon.admin.framework.common.pojo.PageResult;
import com.lw.dillon.admin.module.system.controller.admin.dept.vo.post.PostRespVO;
import com.lw.dillon.admin.module.system.controller.admin.dept.vo.post.PostSaveReqVO;
import com.lw.dillon.admin.module.system.controller.admin.dept.vo.post.PostSimpleRespVO;
import com.lw.ui.request.api.BaseFeignApi;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.*;

import java.util.List;
import java.util.Map;

public interface PostApi extends BaseFeignApi {


    @POST("system/post/create")
    Observable<CommonResult<Long>> createPost(@Body PostSaveReqVO createReqVO);

    @PUT("system/post/update")
    Observable<CommonResult<Boolean>> updatePost(@Body PostSaveReqVO updateReqVO);

    @DELETE("system/post/delete?id={id}")
    Observable<CommonResult<Boolean>> deletePost(@Path("id") Long id);

    @GET("system/post/get?id={id}")
    Observable<CommonResult<PostRespVO>> getPost(@Path("id") Long id);

    @GET("system/post/simple-list")
    Observable<CommonResult<List<PostSimpleRespVO>>> getSimplePostList();

    @GET("system/post/page")
    Observable<CommonResult<PageResult<PostRespVO>>> getPostPage(@QueryMap Map<String, Object> queryMap);


}

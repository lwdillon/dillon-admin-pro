package com.dillon.lw.forest.api.system;

import com.dillon.lw.framework.common.pojo.CommonResult;
import com.dillon.lw.framework.common.pojo.PageResult;
import com.dillon.lw.module.system.controller.admin.dept.vo.post.PostRespVO;
import com.dillon.lw.module.system.controller.admin.dept.vo.post.PostSaveReqVO;
import com.dillon.lw.module.system.controller.admin.dept.vo.post.PostSimpleRespVO;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.*;

import java.util.List;
import java.util.Map;

public interface PostApi {


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

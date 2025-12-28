package com.dillon.lw.api.system;

import com.dillon.lw.api.BaseApi;
import com.dillon.lw.framework.common.pojo.CommonResult;
import com.dillon.lw.framework.common.pojo.PageResult;
import com.dillon.lw.module.system.controller.admin.dept.vo.post.PostRespVO;
import com.dillon.lw.module.system.controller.admin.dept.vo.post.PostSaveReqVO;
import com.dillon.lw.module.system.controller.admin.dept.vo.post.PostSimpleRespVO;
import com.dtflys.forest.annotation.*;

import java.util.List;
import java.util.Map;

public interface PostApi extends BaseApi {


    @Post("system/post/create")
    CommonResult<Long> createPost(@Body PostSaveReqVO createReqVO);

    @Put("system/post/update")
    CommonResult<Boolean> updatePost(@Body PostSaveReqVO updateReqVO);

    @Delete("system/post/delete")
    CommonResult<Boolean> deletePost(@Query("id") Long id);

    @Get("system/post/get")
    CommonResult<PostRespVO> getPost(@Query("id") Long id);

    @Get("system/post/simple-list")
    CommonResult<List<PostSimpleRespVO>> getSimplePostList();

    @Get("system/post/page")
    CommonResult<PageResult<PostRespVO>> getPostPage(@Query Map<String, Object> queryMap);


}

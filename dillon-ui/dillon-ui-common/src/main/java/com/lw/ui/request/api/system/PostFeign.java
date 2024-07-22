package com.lw.ui.request.api.system;

import com.lw.dillon.admin.framework.common.pojo.CommonResult;
import com.lw.dillon.admin.framework.common.pojo.PageResult;
import com.lw.dillon.admin.module.system.controller.admin.dept.vo.post.PostRespVO;
import com.lw.dillon.admin.module.system.controller.admin.dept.vo.post.PostSaveReqVO;
import com.lw.dillon.admin.module.system.controller.admin.dept.vo.post.PostSimpleRespVO;
import com.lw.ui.request.api.BaseFeignApi;
import feign.Param;
import feign.QueryMap;
import feign.RequestLine;

import java.util.List;
import java.util.Map;

public interface PostFeign extends BaseFeignApi {


    @RequestLine("POST /admin-api/system/post/create")
    public CommonResult<Long> createPost(PostSaveReqVO createReqVO) ;

    @RequestLine("PUT /admin-api/system/post/update")
    public CommonResult<Boolean> updatePost( PostSaveReqVO updateReqVO) ;

    @RequestLine("DELETE /admin-api/system/post/delete?id={id}")
    public CommonResult<Boolean> deletePost(@Param("id") Long id);

    @RequestLine("GET /admin-api/system/post/get?id={id}")
    public CommonResult<PostRespVO> getPost(@Param("id") Long id);

    @RequestLine("GET /admin-api/system/post/simple-list")
    public CommonResult<List<PostSimpleRespVO>> getSimplePostList() ;

    @RequestLine("GET /admin-api/system/post/page")
    public CommonResult<PageResult<PostRespVO>> getPostPage(@QueryMap Map<String,Object> queryMap);

 

}

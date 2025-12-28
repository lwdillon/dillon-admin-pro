package com.dillon.lw.api.sse;

import com.dillon.lw.api.BaseApi;
import com.dtflys.forest.annotation.Get;
import com.dtflys.forest.annotation.Query;
import com.dtflys.forest.http.ForestSSE;

public interface CalSseClient extends BaseApi {

    // ForestSSE 为 Forest 的 SSE 控制器类
    // 只要以此类作为方法的返回值类型，该方法就自动成为 SSE 请求方法
    @Get(url = "system/sse/connect", contentType = "text/event-stream")
    ForestSSE testSSE(@Query("taskId") String taskId, @Query("userId") String userId, @Query("clientId") String clientId);
}

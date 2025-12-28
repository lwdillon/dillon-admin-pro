package com.dillon.lw.api.ai;

import com.dillon.lw.api.BaseApi;
import com.dtflys.forest.annotation.Body;
import com.dtflys.forest.annotation.Post;
import com.dtflys.forest.http.ForestSSE;

public interface AiChatMessageApi extends BaseApi {

    @Post(value = "ai/chat/message/send-stream", contentType = "application/json")
    ForestSSE sendChatMessageStream(@Body AiChatMessageSendReqVO sendReqVO);
}

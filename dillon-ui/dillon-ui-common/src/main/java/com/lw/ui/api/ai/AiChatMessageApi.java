package com.lw.ui.api.ai;

import io.reactivex.rxjava3.core.Flowable;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface AiChatMessageApi {

    @Headers("Content-Type: application/json")
    @POST(value = "ai/chat/message/send-stream")
    Flowable<ResponseBody> sendChatMessageStream(@Body AiChatMessageSendReqVO sendReqVO);
}

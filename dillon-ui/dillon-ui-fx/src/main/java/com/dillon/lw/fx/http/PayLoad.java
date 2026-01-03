package com.dillon.lw.fx.http;

import cn.hutool.core.util.StrUtil;
import com.dillon.lw.framework.common.pojo.CommonResult;
import com.dillon.lw.fx.eventbus.EventBusCenter;
import com.dillon.lw.fx.eventbus.event.LogoutEvent;
import com.dillon.lw.fx.eventbus.event.MessageEvent;
import com.dillon.lw.fx.utils.MessageType;

import java.util.function.Function;

/**
 * 剥离 最终数据
 * Created by zhouwei on 16/11/10.
 */

public class PayLoad<T> implements Function<CommonResult<T>, T> {


    @Override
    public T apply(CommonResult<T> tCommonResult) {
        if (!tCommonResult.isSuccess()) {

            if (tCommonResult.getCode() == 401) {
                // 401 未授权
                EventBusCenter.get().post(new LogoutEvent());

            } else {
                showErrorMessage(tCommonResult.getMsg());
            }
            throw new Fault(tCommonResult.getCode(), tCommonResult.getMsg());
        } else {
            if(StrUtil.isNotBlank(tCommonResult.getMsg())) {
                EventBusCenter.get().post(new MessageEvent(tCommonResult.getMsg(), MessageType.SUCCESS));
            }

        }
        return tCommonResult.getCheckedData();
    }

    private void showErrorMessage(String message) {
        EventBusCenter.get().post(new MessageEvent(message, MessageType.DANGER));
    }
}

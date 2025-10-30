package com.lw.swing.http;

import com.lw.dillon.admin.framework.common.pojo.CommonResult;
import com.lw.swing.components.notice.WMessage;
import com.lw.swing.view.frame.MainFrame;
import io.reactivex.rxjava3.functions.Function;

import javax.swing.*;

/**
 * 剥离 最终数据
 * Created by zhouwei on 16/11/10.
 */

public class PayLoad<T> implements Function<CommonResult<T>, T> {


    @Override
    public T apply(CommonResult<T> tCommonResult) throws Throwable {
        if (!tCommonResult.isSuccess()) {

            if (tCommonResult.getCode() == 401) {
                // 401 未授权
                MainFrame.getInstance().showLogin();
            }
            showErrorMessage(tCommonResult.getMsg());
            throw new Fault(tCommonResult.getCode(), tCommonResult.getMsg());
        }
        return tCommonResult.getCheckedData();
    }

    private void showErrorMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            WMessage.showMessageWarning(MainFrame.getInstance(), message);
        });
    }
}

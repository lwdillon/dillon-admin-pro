package http;

import com.lw.dillon.admin.framework.common.pojo.CommonResult;
import io.reactivex.rxjava3.functions.Function;

/**
 * 剥离 最终数据
 * Created by zhouwei on 16/11/10.
 */

public class PayLoad<T> implements Function<CommonResult<T>, T> {


    @Override
    public T apply(CommonResult<T> tCommonResult) throws Throwable {
        if (!tCommonResult.isSuccess()) {
            throw new Fault(tCommonResult.getCode(), tCommonResult.getMsg());
        }
        return tCommonResult.getCheckedData();
    }
}

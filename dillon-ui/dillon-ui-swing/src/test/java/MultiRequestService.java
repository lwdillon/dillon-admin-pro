import com.lw.dillon.admin.framework.common.pojo.CommonResult;
import com.lw.dillon.admin.module.system.controller.admin.permission.vo.menu.MenuRespVO;
import com.lw.dillon.admin.module.system.controller.admin.permission.vo.menu.MenuSimpleRespVO;
import com.lw.swing.http.RetrofitServiceManager;
import com.lw.ui.api.system.MenuApi;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import javafx.application.Platform;

import java.util.List;

public class MultiRequestService {
    private MenuApi menuApi = RetrofitServiceManager.getInstance().create(MenuApi.class);

    public void fetchData() {
        Observable<CommonResult<List<MenuSimpleRespVO>>> request1 = menuApi.getSimpleMenuList();
        Observable<CommonResult<MenuRespVO>> request2 = menuApi.getMenu(1L);

        Observable.merge(request1, request2) // 合并两个请求
                .subscribeOn(Schedulers.io()) // 在 IO 线程执行网络请求
                .observeOn(Schedulers.from(Platform::runLater)) // 在单线程处理结果
                .subscribe(response -> {
                    // 处理 response，例如更新 JavaFX 控件
                    System.out.println(response);
                }, throwable -> {
                    // 错误处理
                    System.err.println("Error: " + throwable.getMessage());
                });
    }
}
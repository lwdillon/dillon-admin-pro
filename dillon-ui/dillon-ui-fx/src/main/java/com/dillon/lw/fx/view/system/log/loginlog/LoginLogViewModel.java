package com.dillon.lw.fx.view.system.log.loginlog;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import com.dillon.lw.api.system.LoginLogApi;
import com.dillon.lw.fx.DefaultExceptionHandler;
import com.dillon.lw.fx.eventbus.EventBusCenter;
import com.dillon.lw.fx.eventbus.event.MessageEvent;
import com.dillon.lw.fx.eventbus.event.RefreshEvent;
import com.dillon.lw.fx.eventbus.event.UpdateDataEvent;
import com.dillon.lw.fx.mvvm.base.BaseViewModel;
import com.dillon.lw.fx.rx.FxSchedulers;
import com.dillon.lw.fx.rx.FxRx;
import com.dillon.lw.fx.utils.MessageType;
import com.dillon.lw.fx.view.layout.ConfirmDialog;
import com.dillon.lw.module.system.controller.admin.logger.vo.loginlog.LoginLogRespVO;
import com.dtflys.forest.Forest;
import com.google.common.eventbus.Subscribe;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class LoginLogViewModel extends BaseViewModel {

    private SimpleIntegerProperty total = new SimpleIntegerProperty(0);
    private IntegerProperty pageNum = new SimpleIntegerProperty(0);
    private IntegerProperty pageSize = new SimpleIntegerProperty(10);

    private ObjectProperty<ObservableList<LoginLogRespVO>> tableItems = new SimpleObjectProperty<>();


    private ObjectProperty<LocalDate> beginDate = new SimpleObjectProperty<>();
    private ObjectProperty<LocalDate> endDate = new SimpleObjectProperty<>();
    private StringProperty userIp = new SimpleStringProperty();
    private StringProperty userName = new SimpleStringProperty();
    private StringProperty status = new SimpleStringProperty();

    public LoginLogViewModel() {
        loadTableData();
    }


    public void loadTableData() {

        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("pageNo", pageNum.get() + 1);
        queryMap.put("pageSize", pageSize.get());

        queryMap.put("userIp", userIp.get());
        queryMap.put("username", userName.get());
        queryMap.put("status", status.get());

        if (ObjectUtil.isAllNotEmpty(getBeginDate(), getEndDate())) {
            String sd = getBeginDate().atTime(0, 0, 0).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String ed = getEndDate().atTime(23, 59, 59).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            queryMap.put("createTime", ListUtil.of(sd, ed));
        }
        queryMap.values().removeAll(Collections.singleton(null));

        Single
                /*
                 * 登录日志分页查询走固定的 RxJava 模式：
                 * 后台线程拉数据，JavaFX UI 线程负责刷新表格和分页属性。
                 */
                .fromCallable(() -> Forest.client(LoginLogApi.class).getLoginLogPage(queryMap).getCheckedData())
                .subscribeOn(Schedulers.io())
                .observeOn(FxSchedulers.fx())
                .compose(FxRx.bindTo(this))
                .subscribe(listCommonResult -> {
                    ObservableList<LoginLogRespVO> userRespVOS = FXCollections.observableArrayList();
                    userRespVOS.addAll(listCommonResult.getList());
                    tableItems.set(userRespVOS);
                    totalProperty().set(listCommonResult.getTotal().intValue());
                }, DefaultExceptionHandler::handle);


    }

    public void deleteLoginLog(Long id, ConfirmDialog dialog) {
        if (id == null) {
            return;
        }
        Single
                /*
                 * 删除登录日志成功后需要立刻通知页面刷新，
                 * 因此成功回调统一切回 JavaFX UI 线程处理事件派发。
                 */
                .fromCallable(() -> Forest.client(LoginLogApi.class).deleteLoginLog(id).getCheckedData())
                .subscribeOn(Schedulers.io())
                .observeOn(FxSchedulers.fx())
                .compose(FxRx.bindTo(this))
                .subscribe(commonResult -> {
                    EventBusCenter.get().post(new MessageEvent("删除成功", MessageType.SUCCESS));
                    EventBusCenter.get().post(new UpdateDataEvent("更新登录日志列表"));
                }, DefaultExceptionHandler::handle);
    }

    @Subscribe
    private void refresh(RefreshEvent event) {
        FxSchedulers.runOnFx(() -> loadTableData());
    }

    @Subscribe
    private void updateData(UpdateDataEvent menuEvent) {
        FxSchedulers.runOnFx(() -> {
            if ("更新登录日志列表".equals(menuEvent.getMessage())) {
                loadTableData();
            }
        });
    }


    public ObjectProperty<ObservableList<LoginLogRespVO>> tableItemsProperty() {
        return tableItems;
    }


    public String getUserName() {
        return userName.get();
    }

    public StringProperty userNameProperty() {
        return userName;
    }

    public LocalDate getBeginDate() {
        return beginDate.get();
    }

    public ObjectProperty<LocalDate> beginDateProperty() {
        return beginDate;
    }

    public LocalDate getEndDate() {
        return endDate.get();
    }

    public ObjectProperty<LocalDate> endDateProperty() {
        return endDate;
    }


    public int getTotal() {
        return total.get();
    }

    public SimpleIntegerProperty totalProperty() {
        return total;
    }

    public int getPageNum() {
        return pageNum.get();
    }

    public IntegerProperty pageNumProperty() {
        return pageNum;
    }

    public int getPageSize() {
        return pageSize.get();
    }

    public IntegerProperty pageSizeProperty() {
        return pageSize;
    }

    public String getUserIp() {
        return userIp.get();
    }

    public StringProperty userIpProperty() {
        return userIp;
    }


}

package com.dillon.lw.fx.view.system.log.loginlog;

import cn.hutool.core.util.ObjectUtil;
import com.dillon.lw.module.system.controller.admin.logger.vo.loginlog.LoginLogRespVO;
import com.google.common.eventbus.Subscribe;
import com.dillon.lw.api.system.LoginLogApi;
import com.dillon.lw.fx.eventbus.EventBusCenter;
import com.dillon.lw.fx.eventbus.event.MessageEvent;
import com.dillon.lw.fx.eventbus.event.RefreshEvent;
import com.dillon.lw.fx.eventbus.event.UpdateDataEvent;
import com.dillon.lw.fx.http.PayLoad;
import com.dillon.lw.fx.http.Request;
import com.dillon.lw.fx.mvvm.base.BaseViewModel;
import com.dillon.lw.fx.utils.MessageType;
import com.dillon.lw.fx.view.layout.ConfirmDialog;
import io.reactivex.rxjava3.schedulers.Schedulers;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

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
            queryMap.put("createTime", new String[]{sd, ed});
        }
        queryMap.values().removeAll(Collections.singleton(null));

        Request.getInstance().create(LoginLogApi.class).getLoginLogPage(queryMap)
                .subscribeOn(Schedulers.io())
                .map(new PayLoad<>())
                .observeOn(Schedulers.from(Platform::runLater))
                .subscribe(listCommonResult -> {

                    ObservableList<LoginLogRespVO> userRespVOS = FXCollections.observableArrayList();
                    userRespVOS.addAll(listCommonResult.getList());
                    tableItems.set(userRespVOS);
                    totalProperty().set(listCommonResult.getTotal().intValue());

                });


    }

    public void deleteLoginLog(Long id, ConfirmDialog dialog) {
        if (id == null) {
            return;
        }
        Request.getInstance().create(LoginLogApi.class).deleteLoginLog(id)
                .subscribeOn(Schedulers.io())
                .map(new PayLoad<>())
                .observeOn(Schedulers.from(Platform::runLater))
                .subscribe(commonResult -> {
                    // 删除成功后重新加载数据
                    EventBusCenter.get().post(new MessageEvent("删除成功", MessageType.SUCCESS));
                    EventBusCenter.get().post(new UpdateDataEvent("更新登录日志列表"));
                });
    }

    @Subscribe
    private void refresh(RefreshEvent event) {
        Platform.runLater(() -> loadTableData());
    }

    @Subscribe
    private void updateData(UpdateDataEvent menuEvent) {
        Platform.runLater(() -> {
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

package com.lw.fx.view.system.loginlog;

import cn.hutool.core.util.ObjectUtil;
import com.lw.dillon.admin.module.system.controller.admin.logger.vo.loginlog.LoginLogRespVO;
import com.lw.fx.request.Request;
import com.lw.ui.request.api.system.LoginLogFeign;
import de.saxsys.mvvmfx.SceneLifecycle;
import de.saxsys.mvvmfx.ViewModel;
import io.datafx.core.concurrent.ProcessChain;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class LoginLogViewModel implements ViewModel, SceneLifecycle {

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

        ProcessChain.create()

                .addSupplierInExecutor(() -> Request.connector(LoginLogFeign.class).getLoginLogPage(queryMap))
                .addConsumerInPlatformThread(listCommonResult -> {
                    if (listCommonResult.isSuccess()) {
                        ObservableList<LoginLogRespVO> userRespVOS = FXCollections.observableArrayList();
                        userRespVOS.addAll(listCommonResult.getData().getList());
                        tableItems.set(userRespVOS);
                        totalProperty().set(listCommonResult.getData().getTotal().intValue());
                    }
                })
                .run();


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





    @Override
    public void onViewAdded() {

    }

    @Override
    public void onViewRemoved() {

    }
}

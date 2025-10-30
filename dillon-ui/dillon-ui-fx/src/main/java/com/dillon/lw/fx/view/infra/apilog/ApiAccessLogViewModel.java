package com.dillon.lw.fx.view.infra.apilog;

import cn.hutool.core.util.ObjectUtil;
import com.dillon.lw.module.infra.controller.admin.logger.vo.apiaccesslog.ApiAccessLogRespVO;
import com.dillon.lw.module.system.controller.admin.dict.vo.data.DictDataSimpleRespVO;
import com.dillon.lw.api.infra.ApiAccessLogApi;
import com.dillon.lw.fx.http.PayLoad;
import com.dillon.lw.fx.http.Request;
import com.dillon.lw.fx.mvvm.base.BaseViewModel;
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
import java.util.Optional;

public class ApiAccessLogViewModel extends BaseViewModel {

    private SimpleIntegerProperty total = new SimpleIntegerProperty(0);
    private IntegerProperty pageNum = new SimpleIntegerProperty(0);
    private IntegerProperty pageSize = new SimpleIntegerProperty(10);

    private ObjectProperty<ObservableList<ApiAccessLogRespVO>> tableItems = new SimpleObjectProperty<>();


    private ObjectProperty<LocalDate> beginDate = new SimpleObjectProperty<>();
    private ObjectProperty<LocalDate> endDate = new SimpleObjectProperty<>();
    private StringProperty userId = new SimpleStringProperty();
    private StringProperty applicationName = new SimpleStringProperty();
    private StringProperty duration = new SimpleStringProperty();
    private StringProperty resultCode = new SimpleStringProperty();

    private ObjectProperty<DictDataSimpleRespVO> selUserType = new SimpleObjectProperty<>();


    public ApiAccessLogViewModel() {
        loadTableData();
    }


    public void loadTableData() {

        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("pageNo", pageNum.get() + 1);
        queryMap.put("pageSize", pageSize.get());

        queryMap.put("userId", userId.get());
        queryMap.put("userType", Optional.ofNullable(selUserType.get()).map(DictDataSimpleRespVO::getValue).orElse(null));
        queryMap.put("applicationName", applicationName.get());
        queryMap.put("duration", duration.get());
        queryMap.put("resultCode", resultCode.get());

        if (ObjectUtil.isAllNotEmpty(getBeginDate(), getEndDate())) {
            String sd = getBeginDate().atTime(0, 0, 0).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String ed = getEndDate().atTime(23, 59, 59).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            queryMap.put("beginTime", new String[]{sd, ed});
        }

        queryMap.values().removeAll(Collections.singleton(null));
        Request.getInstance().create(ApiAccessLogApi.class).getApiAccessLogPage(queryMap)
                .subscribeOn(Schedulers.io())
                .map(new PayLoad<>())
                .observeOn(Schedulers.from(Platform::runLater))
                .subscribe(data -> {
                    ObservableList<ApiAccessLogRespVO> respVOS = FXCollections.observableArrayList();
                    respVOS.addAll(data.getList());
                    tableItems.set(respVOS);
                    totalProperty().set(data.getTotal().intValue());
                }, throwable -> {
                    throwable.printStackTrace();
                });


    }


    public ObjectProperty<ObservableList<ApiAccessLogRespVO>> tableItemsProperty() {
        return tableItems;
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

    public String getUserId() {
        return userId.get();
    }

    public StringProperty userIdProperty() {
        return userId;
    }

    public String getApplicationName() {
        return applicationName.get();
    }

    public StringProperty applicationNameProperty() {
        return applicationName;
    }

    public DictDataSimpleRespVO getSelUserType() {
        return selUserType.get();
    }

    public ObjectProperty<DictDataSimpleRespVO> selUserTypeProperty() {
        return selUserType;
    }

    public String getDuration() {
        return duration.get();
    }

    public StringProperty durationProperty() {
        return duration;
    }

    public String getResultCode() {
        return resultCode.get();
    }

    public StringProperty resultCodeProperty() {
        return resultCode;
    }

}

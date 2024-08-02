package com.lw.fx.view.infra.apilog;

import cn.hutool.core.util.ObjectUtil;
import com.lw.dillon.admin.module.infra.controller.admin.logger.vo.apierrorlog.ApiErrorLogRespVO;
import com.lw.dillon.admin.module.system.controller.admin.dict.vo.data.DictDataSimpleRespVO;
import com.lw.fx.request.Request;
import com.lw.ui.request.api.apilog.ApiErrorLogFeign;
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
import java.util.Optional;

public class ApiErrorLogViewModel implements ViewModel, SceneLifecycle {

    private SimpleIntegerProperty total = new SimpleIntegerProperty(0);
    private IntegerProperty pageNum = new SimpleIntegerProperty(0);
    private IntegerProperty pageSize = new SimpleIntegerProperty(10);

    private ObjectProperty<ObservableList<ApiErrorLogRespVO>> tableItems = new SimpleObjectProperty<>();


    private ObjectProperty<LocalDate> beginDate = new SimpleObjectProperty<>();
    private ObjectProperty<LocalDate> endDate = new SimpleObjectProperty<>();
    private StringProperty userId = new SimpleStringProperty();
    private StringProperty applicationName = new SimpleStringProperty();

    private ObjectProperty<DictDataSimpleRespVO> selUserType = new SimpleObjectProperty<>();
    private ObjectProperty<DictDataSimpleRespVO> processStatus = new SimpleObjectProperty<>();


    public ApiErrorLogViewModel() {
        loadTableData();
    }


    public void loadTableData() {

        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("pageNo", pageNum.get() + 1);
        queryMap.put("pageSize", pageSize.get());

        queryMap.put("userId", userId.get());
        queryMap.put("userType", Optional.ofNullable(selUserType.get()).map(DictDataSimpleRespVO::getValue).orElse(null));
        queryMap.put("applicationName", applicationName.get());
        queryMap.put("processStatus", Optional.ofNullable(processStatus.get()).map(DictDataSimpleRespVO::getValue).orElse(null));

        if (ObjectUtil.isAllNotEmpty(getBeginDate(), getEndDate())) {
            String sd = getBeginDate().atTime(0, 0, 0).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String ed = getEndDate().atTime(23, 59, 59).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            queryMap.put("exceptionTime", new String[]{sd, ed});
        }

        ProcessChain.create()

                .addSupplierInExecutor(() -> Request.connector(ApiErrorLogFeign.class).getApiErrorLogPage(queryMap))
                .addConsumerInPlatformThread(listCommonResult -> {
                    if (listCommonResult.isSuccess()) {
                        ObservableList<ApiErrorLogRespVO> respVOS = FXCollections.observableArrayList();
                        respVOS.addAll(listCommonResult.getData().getList());
                        tableItems.set(respVOS);
                        totalProperty().set(listCommonResult.getData().getTotal().intValue());
                    }
                })
                .run();


    }


    public ObjectProperty<ObservableList<ApiErrorLogRespVO>> tableItemsProperty() {
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

    public DictDataSimpleRespVO getProcessStatus() {
        return processStatus.get();
    }

    public ObjectProperty<DictDataSimpleRespVO> processStatusProperty() {
        return processStatus;
    }

    @Override
    public void onViewAdded() {

    }

    @Override
    public void onViewRemoved() {

    }
}

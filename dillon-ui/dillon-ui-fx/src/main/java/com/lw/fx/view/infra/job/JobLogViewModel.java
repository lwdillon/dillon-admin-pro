package com.lw.fx.view.infra.job;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import com.lw.dillon.admin.module.infra.controller.admin.job.vo.log.JobLogRespVO;
import com.lw.dillon.admin.module.system.controller.admin.dict.vo.data.DictDataSimpleRespVO;
import com.lw.fx.request.Request;
import com.lw.ui.request.api.job.JobLogFeign;
import de.saxsys.mvvmfx.ViewModel;
import io.datafx.core.concurrent.ProcessChain;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class JobLogViewModel implements ViewModel {
    private SimpleIntegerProperty total = new SimpleIntegerProperty(0);
    private IntegerProperty pageNum = new SimpleIntegerProperty(0);
    private IntegerProperty pageSize = new SimpleIntegerProperty(10);

    private StringProperty name = new SimpleStringProperty();
    private StringProperty handlerName = new SimpleStringProperty();
    private ObjectProperty<DictDataSimpleRespVO> status = new SimpleObjectProperty<>();

    private ObservableList<JobLogRespVO> tableItems = FXCollections.observableArrayList();
    private ObjectProperty<LocalDate> beginDate = new SimpleObjectProperty<>();
    private ObjectProperty<LocalDate> endDate = new SimpleObjectProperty<>();

    public JobLogViewModel() {

    }


    public void loadTableData() {

        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("pageNo", pageNum.get() + 1);
        queryMap.put("pageSize", pageSize.get());

        queryMap.put("handlerName", handlerName.get());
        if (status.get() != null) {
            queryMap.put("status", Convert.toInt(status.get().getValue(), null));
        }

        if (ObjectUtil.isAllNotEmpty(beginDate.get(), endDate.get())) {
            String sd = beginDate.get().atTime(0, 0, 0).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String ed = endDate.get().atTime(23, 59, 59).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            queryMap.put("beginTime",sd);
            queryMap.put("endTime",ed);
        }

        ProcessChain.create()
                .addRunnableInPlatformThread(() -> tableItems.clear())
                .addSupplierInExecutor(() -> Request.connector(JobLogFeign.class).getJobLogPage(queryMap))
                .addConsumerInPlatformThread(listCommonResult -> {
                    if (listCommonResult.isSuccess()) {
                        tableItems.setAll(listCommonResult.getCheckedData().getList());
                        totalProperty().set(listCommonResult.getData().getTotal().intValue());
                    }
                }).onException(e -> e.printStackTrace())
                .run();


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



    public ObservableList<JobLogRespVO> getTableItems() {
        return tableItems;
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public String getHandlerName() {
        return handlerName.get();
    }

    public StringProperty handlerNameProperty() {
        return handlerName;
    }

    public DictDataSimpleRespVO getStatus() {
        return status.get();
    }

    public ObjectProperty<DictDataSimpleRespVO> statusProperty() {
        return status;
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
}

package com.lw.fx.view.infra.file;

import cn.hutool.core.util.ObjectUtil;
import com.lw.dillon.admin.module.infra.controller.admin.file.vo.file.FileRespVO;
import com.lw.fx.request.Request;
import com.lw.ui.request.api.file.FileFeign;
import de.saxsys.mvvmfx.ViewModel;
import io.datafx.core.concurrent.ProcessChain;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class FileListViewModel implements ViewModel {
    private SimpleIntegerProperty total = new SimpleIntegerProperty(0);
    private IntegerProperty pageNum = new SimpleIntegerProperty(0);
    private IntegerProperty pageSize = new SimpleIntegerProperty(10);
    private ObjectProperty<LocalDate> beginDate = new SimpleObjectProperty<>();
    private ObjectProperty<LocalDate> endDate = new SimpleObjectProperty<>();

    private StringProperty path = new SimpleStringProperty();
    private StringProperty type = new SimpleStringProperty();

    private ObservableList<FileRespVO> tableItems = FXCollections.observableArrayList();

    public FileListViewModel() {
        loadTableData();
    }


    public void loadTableData() {

        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("pageNo", pageNum.get() + 1);
        queryMap.put("pageSize", pageSize.get());

        queryMap.put("path", path.get());
        queryMap.put("type", type.get());

        if (ObjectUtil.isAllNotEmpty(getBeginDate(), getEndDate())) {
            String sd = getBeginDate().atTime(0, 0, 0).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String ed = getEndDate().atTime(23, 59, 59).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            queryMap.put("createTime", new String[]{sd, ed});
        }

        ProcessChain.create()
                .addRunnableInPlatformThread(() -> tableItems.clear())
                .addSupplierInExecutor(() -> Request.connector(FileFeign.class).getFilePage(queryMap))
                .addConsumerInPlatformThread(listCommonResult -> {
                    if (listCommonResult.isSuccess()) {
                        tableItems.setAll(listCommonResult.getData().getList());
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

    public ObservableList<FileRespVO> getTableItems() {
        return tableItems;
    }

    public String getPath() {
        return path.get();
    }

    public StringProperty pathProperty() {
        return path;
    }

    public String getType() {
        return type.get();
    }

    public StringProperty typeProperty() {
        return type;
    }
}

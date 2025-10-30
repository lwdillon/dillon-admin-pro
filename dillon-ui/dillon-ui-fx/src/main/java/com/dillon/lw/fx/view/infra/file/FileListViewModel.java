package com.dillon.lw.fx.view.infra.file;

import cn.hutool.core.util.ObjectUtil;
import com.dillon.lw.module.infra.controller.admin.file.vo.file.FileRespVO;
import com.dillon.lw.api.infra.FileApi;
import com.dillon.lw.fx.eventbus.EventBusCenter;
import com.dillon.lw.fx.eventbus.event.MessageEvent;
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

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class FileListViewModel extends BaseViewModel {
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
        queryMap.values().removeAll(Collections.singleton(null));

        Request.getInstance().create(FileApi.class).getFilePage(queryMap)
                .subscribeOn(Schedulers.io())
                .map(new PayLoad<>())
                .observeOn(Schedulers.from(Platform::runLater))
                .subscribe(data -> {
                    tableItems.setAll(data.getList());
                    total.set(data.getTotal().intValue());
                }, e -> {
                    e.printStackTrace();
                });


    }

    public void uploadFile(String path, File file) {

        Request.getInstance().create(FileApi.class).uploadFile(path, file)
                .subscribeOn(Schedulers.io())
                .map(new PayLoad<>())
                .observeOn(Schedulers.from(Platform::runLater))
                .subscribe(data -> {
                    EventBusCenter.get().post(new UpdateDataEvent("更新文件列表"));
                    EventBusCenter.get().post(new MessageEvent("上传成功", MessageType.SUCCESS));
                    loadTableData();
                }, e -> {
                    e.printStackTrace();
                });
    }

    public void deleteFile(Long id, ConfirmDialog confirmDialog) {
        Request.getInstance().create(FileApi.class).deleteFile(id)
                .subscribeOn(Schedulers.io())
                .map(new PayLoad<>())
                .observeOn(Schedulers.from(Platform::runLater))
                .subscribe(data -> {
                    EventBusCenter.get().post(new UpdateDataEvent("更新文件列表"));
                    EventBusCenter.get().post(new MessageEvent("删除成功", MessageType.SUCCESS));
                    confirmDialog.close();
                    loadTableData();
                }, e -> {
                    e.printStackTrace();
                });
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

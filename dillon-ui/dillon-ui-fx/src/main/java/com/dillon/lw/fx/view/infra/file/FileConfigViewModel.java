package com.dillon.lw.fx.view.infra.file;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import com.dillon.lw.module.infra.controller.admin.file.vo.config.FileConfigRespVO;
import com.dillon.lw.module.system.controller.admin.dict.vo.data.DictDataSimpleRespVO;
import com.google.common.eventbus.Subscribe;
import com.google.gson.JsonObject;
import com.dillon.lw.api.infra.FileConfigApi;
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
import java.util.List;
import java.util.Map;

public class FileConfigViewModel extends BaseViewModel {
    private SimpleIntegerProperty total = new SimpleIntegerProperty(0);
    private IntegerProperty pageNum = new SimpleIntegerProperty(0);
    private IntegerProperty pageSize = new SimpleIntegerProperty(10);
    private ObjectProperty<LocalDate> beginDate = new SimpleObjectProperty<>();
    private ObjectProperty<LocalDate> endDate = new SimpleObjectProperty<>();

    private StringProperty name = new SimpleStringProperty();
    private ObjectProperty<DictDataSimpleRespVO> storage = new SimpleObjectProperty<>();

    private ObservableList<FileConfigRespVO> tableItems = FXCollections.observableArrayList();

    public FileConfigViewModel() {
        loadTableData();
    }


    public void loadTableData() {

        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("pageNo", pageNum.get() + 1);
        queryMap.put("pageSize", pageSize.get());

        queryMap.put("name", name.get());
        if (storage.get() != null) {
            queryMap.put("storage", Convert.toInt(storage.get().getValue(), null));
        }

        if (ObjectUtil.isAllNotEmpty(getBeginDate(), getEndDate())) {
            String sd = getBeginDate().atTime(0, 0, 0).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String ed = getEndDate().atTime(23, 59, 59).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            queryMap.put("createTime", new String[]{sd, ed});
        }

        queryMap.values().removeAll(Collections.singleton(null));
        tableItems.clear();


        Request.getInstance().create(FileConfigApi.class).getFileConfigPage(queryMap)
                .subscribeOn(Schedulers.io())
                .map(new PayLoad<>())
                .observeOn(Schedulers.from(Platform::runLater))
                .subscribe(data -> {
                    List<JsonObject> list = data.getList();
                    for (JsonObject jsonObject : list) {
                        FileConfigRespVO fileConfigRespVO = new FileConfigRespVO();
                        fileConfigRespVO.setId(jsonObject.get("id").getAsLong());
                        fileConfigRespVO.setName(jsonObject.get("name").getAsString());
                        fileConfigRespVO.setStorage(jsonObject.get("storage").getAsInt());
                        fileConfigRespVO.setRemark(jsonObject.get("remark").getAsString());
                        fileConfigRespVO.setMaster(jsonObject.get("master").getAsBoolean());
                        fileConfigRespVO.setCreateTime(Convert.toLocalDateTime(jsonObject.get("createTime")));
                        tableItems.add(fileConfigRespVO);
                    }
                    totalProperty().set(data.getTotal().intValue());
                }, e -> {
                    e.printStackTrace();
                    total.set(0);
                });

    }

    public void updateFileConfigMaster(Long id, ConfirmDialog confirmDialog) {
        Request.getInstance().create(FileConfigApi.class).updateFileConfigMaster(id)
                .subscribeOn(Schedulers.io())
                .map(new PayLoad<>())
                .observeOn(Schedulers.from(Platform::runLater))
                .subscribe(data -> {
                    confirmDialog.close();
                    EventBusCenter.get().post(new UpdateDataEvent("更新文件配置列表"));
                    EventBusCenter.get().post(new MessageEvent("更新成功", MessageType.SUCCESS));
                }, e -> {
                    e.printStackTrace();
                    EventBusCenter.get().post(new MessageEvent("更新失败", MessageType.DANGER));
                });
    }

    public void deleteFileConfig(Long id, ConfirmDialog confirmDialog) {
        Request.getInstance().create(FileConfigApi.class).deleteFileConfig(id)
                .subscribeOn(Schedulers.io())
                .map(new PayLoad<>())
                .observeOn(Schedulers.from(Platform::runLater))
                .subscribe(data -> {
                    confirmDialog.close();
                    EventBusCenter.get().post(new UpdateDataEvent("更新文件配置列表"));
                    EventBusCenter.get().post(new MessageEvent("删除成功", MessageType.SUCCESS));
                }, e -> {
                    e.printStackTrace();
                    EventBusCenter.get().post(new MessageEvent("删除失败", MessageType.DANGER));
                });
    }

    @Subscribe
    private void updateData(UpdateDataEvent menuEvent) {
        Platform.runLater(() -> {
            if ("更新文件配置列表".equals(menuEvent.getMessage())) {
                loadTableData();
            }
        });
    }


    @Subscribe
    private void refresh(RefreshEvent event) {
        Platform.runLater(() -> loadTableData());
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

    public ObservableList<FileConfigRespVO> getTableItems() {
        return tableItems;
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public DictDataSimpleRespVO getStorage() {
        return storage.get();
    }

    public ObjectProperty<DictDataSimpleRespVO> storageProperty() {
        return storage;
    }
}

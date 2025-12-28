package com.dillon.lw.fx.view.infra.config;

import cn.hutool.core.util.ObjectUtil;
import com.dillon.lw.framework.common.pojo.PageResult;
import com.dillon.lw.module.infra.controller.admin.config.vo.ConfigRespVO;
import com.dillon.lw.module.system.controller.admin.dict.vo.data.DictDataSimpleRespVO;
import com.dtflys.forest.Forest;
import com.google.common.eventbus.Subscribe;
import com.dillon.lw.api.infra.ConfigApi;
import com.dillon.lw.fx.eventbus.EventBusCenter;
import com.dillon.lw.fx.eventbus.event.MessageEvent;
import com.dillon.lw.fx.eventbus.event.RefreshEvent;
import com.dillon.lw.fx.eventbus.event.UpdateDataEvent;
import com.dillon.lw.fx.http.PayLoad;
import com.dillon.lw.fx.mvvm.base.BaseViewModel;
import com.dillon.lw.fx.utils.MessageType;
import com.dillon.lw.fx.view.layout.ConfirmDialog;
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
import java.util.concurrent.CompletableFuture;

public class ConfigViewModel extends BaseViewModel {
    private SimpleIntegerProperty total = new SimpleIntegerProperty(0);
    private IntegerProperty pageNum = new SimpleIntegerProperty(0);
    private IntegerProperty pageSize = new SimpleIntegerProperty(10);

    private ObjectProperty<ObservableList<ConfigRespVO>> tableItems = new SimpleObjectProperty<>();

    private ObjectProperty<LocalDate> beginDate = new SimpleObjectProperty<>();
    private ObjectProperty<LocalDate> endDate = new SimpleObjectProperty<>();
    private StringProperty name = new SimpleStringProperty();
    private StringProperty key = new SimpleStringProperty();
    private ObjectProperty<DictDataSimpleRespVO> type = new SimpleObjectProperty<>();

    public ConfigViewModel() {

        loadTableData();
    }


    public void loadTableData() {

        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("pageNo", pageNum.get() + 1);
        queryMap.put("pageSize", pageSize.get());

        queryMap.put("name", name.get());
        queryMap.put("key", key.get());
        queryMap.put("type", Optional.ofNullable(type.get()).map(DictDataSimpleRespVO::getValue).orElse(null));


        if (ObjectUtil.isAllNotEmpty(getBeginDate(), getEndDate())) {
            String sd = getBeginDate().atTime(0, 0, 0).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String ed = getEndDate().atTime(23, 59, 59).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            queryMap.put("createTime", new String[]{sd, ed});
        }
        queryMap.values().removeAll(Collections.singleton(null));


        CompletableFuture.supplyAsync(() -> {
            return new PayLoad<PageResult<ConfigRespVO>>().apply(Forest.client(ConfigApi.class).getConfigPage(queryMap));
        }).thenAcceptAsync(data -> {
            ObservableList<ConfigRespVO> userRespVOS = FXCollections.observableArrayList();
            userRespVOS.addAll(data.getList());
            tableItems.set(userRespVOS);
            totalProperty().set(data.getTotal().intValue());
        }, Platform::runLater).exceptionally(e -> {
            e.printStackTrace();
            return null;
        });


    }

    public void deleteConfig(long id, ConfirmDialog confirmDialog) {
        CompletableFuture.supplyAsync(() -> {
            return new PayLoad<Boolean>().apply(Forest.client(ConfigApi.class).deleteConfig(id));
        }).thenAcceptAsync(r -> {
            confirmDialog.close();
            EventBusCenter.get().post(new UpdateDataEvent("更新参数配置列表"));
            EventBusCenter.get().post(new MessageEvent("删除成功", MessageType.SUCCESS));
        }, Platform::runLater).exceptionally(e -> {
            e.printStackTrace();
            return null;
        });
    }

    @Subscribe
    private void updateData(UpdateDataEvent menuEvent) {
        Platform.runLater(() -> {
            if ("更新参数配置列表".equals(menuEvent.getMessage())) {
                loadTableData();
            }
        });
    }


    @Subscribe
    private void refresh(RefreshEvent event) {
        Platform.runLater(() -> loadTableData());
    }

    public ObjectProperty<ObservableList<ConfigRespVO>> tableItemsProperty() {
        return tableItems;
    }

    public DictDataSimpleRespVO getType() {
        return type.get();
    }

    public ObjectProperty<DictDataSimpleRespVO> typeProperty() {
        return type;
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public String getKey() {
        return key.get();
    }

    public StringProperty keyProperty() {
        return key;
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

}

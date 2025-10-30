package com.dillon.lw.fx.view.infra.job;

import cn.hutool.core.convert.Convert;
import com.dillon.lw.module.infra.controller.admin.job.vo.job.JobRespVO;
import com.dillon.lw.module.system.controller.admin.dict.vo.data.DictDataSimpleRespVO;
import com.google.common.eventbus.Subscribe;
import com.dillon.lw.api.infra.JobApi;
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

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class JobViewModel extends BaseViewModel {
    private SimpleIntegerProperty total = new SimpleIntegerProperty(0);
    private IntegerProperty pageNum = new SimpleIntegerProperty(0);
    private IntegerProperty pageSize = new SimpleIntegerProperty(10);

    private StringProperty name = new SimpleStringProperty();
    private StringProperty handlerName = new SimpleStringProperty();
    private ObjectProperty<DictDataSimpleRespVO> status = new SimpleObjectProperty<>();

    private ObservableList<JobRespVO> tableItems = FXCollections.observableArrayList();
    private ObservableList<LocalDateTime> nextDateTimes = FXCollections.observableArrayList();


    public JobViewModel() {
        loadTableData();
    }


    public void loadTableData() {

        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("pageNo", pageNum.get() + 1);
        queryMap.put("pageSize", pageSize.get());

        queryMap.put("name", name.get());
        queryMap.put("handlerName", handlerName.get());
        if (status.get() != null) {
            queryMap.put("status", Convert.toInt(status.get().getValue(), null));
        }
        queryMap.values().removeAll(Collections.singleton(null));


        Request.getInstance().create(JobApi.class).getJobPage(queryMap)
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

    public void getJobNextTimes(Long id, Integer count) {
        nextDateTimes.clear();
        Request.getInstance().create(JobApi.class).getJobNextTimes(id, count)
                .subscribeOn(Schedulers.io())
                .map(new PayLoad<>())
                .observeOn(Schedulers.from(Platform::runLater))
                .subscribe(data -> {
                    nextDateTimes.setAll(data);
                }, e -> {
                    e.printStackTrace();
                });
    }

    public void deleteJob(Long id, ConfirmDialog confirmDialog) {
        Request.getInstance().create(JobApi.class).deleteJob(id)
                .subscribeOn(Schedulers.io())
                .map(new PayLoad<>())
                .observeOn(Schedulers.from(Platform::runLater))
                .subscribe(data -> {
                    confirmDialog.close();
                    EventBusCenter.get().post(new UpdateDataEvent("更新job配置列表"));
                    EventBusCenter.get().post(new MessageEvent("删除成功", MessageType.SUCCESS));
                }, e -> {
                    e.printStackTrace();
                });
    }

    public void triggerJob(Long id, ConfirmDialog confirmDialog) {
        Request.getInstance().create(JobApi.class).triggerJob(id)
                .subscribeOn(Schedulers.io())
                .map(new PayLoad<>())
                .observeOn(Schedulers.from(Platform::runLater))
                .subscribe(data -> {
                    confirmDialog.close();
                    EventBusCenter.get().post(new UpdateDataEvent("更新job配置列表"));
                    EventBusCenter.get().post(new MessageEvent("触发成功", MessageType.SUCCESS));
                }, e -> {
                    e.printStackTrace();
                });
    }

    public void updateJobStatus(Long id, Integer status, ConfirmDialog confirmDialog) {
        Request.getInstance().create(JobApi.class).updateJobStatus(id, status)
                .subscribeOn(Schedulers.io())
                .map(new PayLoad<>())
                .observeOn(Schedulers.from(Platform::runLater))
                .subscribe(data -> {
                    confirmDialog.close();
                    EventBusCenter.get().post(new UpdateDataEvent("更新job配置列表"));
                    EventBusCenter.get().post(new MessageEvent("操作成功", MessageType.SUCCESS));
                }, e -> {
                    e.printStackTrace();
                });
    }

    @Subscribe
    private void updateData(UpdateDataEvent menuEvent) {
        Platform.runLater(() -> {
            if ("更新job配置列表".equals(menuEvent.getMessage())) {
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


    public ObservableList<JobRespVO> getTableItems() {
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

    public ObservableList<LocalDateTime> getNextDateTimes() {
        return nextDateTimes;
    }
}

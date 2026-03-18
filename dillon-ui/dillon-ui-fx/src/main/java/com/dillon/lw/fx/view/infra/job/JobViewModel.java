package com.dillon.lw.fx.view.infra.job;

import cn.hutool.core.convert.Convert;
import com.dillon.lw.api.infra.JobApi;
import com.dillon.lw.fx.DefaultExceptionHandler;
import com.dillon.lw.fx.eventbus.EventBusCenter;
import com.dillon.lw.fx.eventbus.event.MessageEvent;
import com.dillon.lw.fx.eventbus.event.RefreshEvent;
import com.dillon.lw.fx.eventbus.event.UpdateDataEvent;
import com.dillon.lw.fx.mvvm.base.BaseViewModel;
import com.dillon.lw.fx.rx.FxSchedulers;
import com.dillon.lw.fx.rx.FxRx;
import com.dillon.lw.fx.utils.MessageType;
import com.dillon.lw.fx.view.layout.ConfirmDialog;
import com.dillon.lw.module.infra.controller.admin.job.vo.job.JobRespVO;
import com.dillon.lw.module.system.controller.admin.dict.vo.data.DictDataSimpleRespVO;
import com.dtflys.forest.Forest;
import com.google.common.eventbus.Subscribe;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

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


        Single
                /*
                 * 任务分页查询放到 IO 线程执行，
                 * 任务表格和分页属性的回填再切回 JavaFX UI 线程。
                 */
                .fromCallable(() -> Forest.client(JobApi.class).getJobPage(queryMap).getCheckedData())
                .subscribeOn(Schedulers.io())
                .observeOn(FxSchedulers.fx())
                .compose(FxRx.bindTo(this))
                .subscribe(data -> {
                    tableItems.setAll(data.getList());
                    total.set(data.getTotal().intValue());
                }, DefaultExceptionHandler::handle);


    }

    public void getJobNextTimes(Long id, Integer count) {
        nextDateTimes.clear();
        Single
                /*
                 * 下次执行时间查询只负责后台取数，
                 * 拿到结果后回到 JavaFX UI 线程刷新预览列表。
                 */
                .fromCallable(() -> Forest.client(JobApi.class).getJobNextTimes(id, count).getCheckedData())
                .subscribeOn(Schedulers.io())
                .observeOn(FxSchedulers.fx())
                .compose(FxRx.bindTo(this))
                .subscribe(data -> nextDateTimes.setAll(data), DefaultExceptionHandler::handle);
    }

    public void deleteJob(Long id, ConfirmDialog confirmDialog) {
        Single
                .fromCallable(() -> Forest.client(JobApi.class).deleteJob(id).getCheckedData())
                .subscribeOn(Schedulers.io())
                .observeOn(FxSchedulers.fx())
                .compose(FxRx.bindTo(this))
                .subscribe(data -> {
                    confirmDialog.close();
                    EventBusCenter.get().post(new UpdateDataEvent("更新job配置列表"));
                    EventBusCenter.get().post(new MessageEvent("删除成功", MessageType.SUCCESS));
                }, DefaultExceptionHandler::handle);
    }

    public void triggerJob(Long id, ConfirmDialog confirmDialog) {
        Single
                .fromCallable(() -> Forest.client(JobApi.class).triggerJob(id).getCheckedData())
                .subscribeOn(Schedulers.io())
                .observeOn(FxSchedulers.fx())
                .compose(FxRx.bindTo(this))
                .subscribe(data -> {
                    confirmDialog.close();
                    EventBusCenter.get().post(new MessageEvent("执行成功", MessageType.SUCCESS));
                }, DefaultExceptionHandler::handle);
    }

    public void updateJobStatus(Long id, Integer status, ConfirmDialog confirmDialog) {
        Single
                .fromCallable(() -> Forest.client(JobApi.class).updateJobStatus(id, status).getCheckedData())
                .subscribeOn(Schedulers.io())
                .observeOn(FxSchedulers.fx())
                .compose(FxRx.bindTo(this))
                .subscribe(data -> {
                    confirmDialog.close();
                    EventBusCenter.get().post(new UpdateDataEvent("更新job配置列表"));
                    EventBusCenter.get().post(new MessageEvent("操作成功", MessageType.SUCCESS));
                }, DefaultExceptionHandler::handle);
    }

    @Subscribe
    private void updateData(UpdateDataEvent menuEvent) {
        FxSchedulers.runOnFx(() -> {
            if ("更新job配置列表".equals(menuEvent.getMessage())) {
                loadTableData();
            }
        });
    }


    @Subscribe
    private void refresh(RefreshEvent event) {
        FxSchedulers.runOnFx(() -> loadTableData());
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

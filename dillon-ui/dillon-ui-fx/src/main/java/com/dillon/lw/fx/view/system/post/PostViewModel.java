package com.dillon.lw.fx.view.system.post;

import cn.hutool.core.util.ObjectUtil;
import com.dillon.lw.module.system.controller.admin.dept.vo.post.PostRespVO;
import com.google.common.eventbus.Subscribe;
import com.dillon.lw.api.system.PostApi;
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
import java.util.Map;

public class PostViewModel extends BaseViewModel {

    private SimpleIntegerProperty total = new SimpleIntegerProperty(0);
    private IntegerProperty pageNum = new SimpleIntegerProperty(0);
    private IntegerProperty pageSize = new SimpleIntegerProperty(10);

    private ObjectProperty<ObservableList<PostRespVO>> tableItems = new SimpleObjectProperty<>();

    private ObjectProperty<LocalDate> beginDate = new SimpleObjectProperty<>();
    private ObjectProperty<LocalDate> endDate = new SimpleObjectProperty<>();
    private StringProperty name = new SimpleStringProperty();
    private StringProperty code = new SimpleStringProperty();
    private ObjectProperty<Integer> status = new SimpleObjectProperty<>();

    public PostViewModel() {

        loadTableData();
    }


    public void loadTableData() {

        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("pageNo", pageNum.get() + 1);
        queryMap.put("pageSize", pageSize.get());
        queryMap.put("name", name.get());
        queryMap.put("code", code.get());
        queryMap.put("status", status.get());

        if (ObjectUtil.isAllNotEmpty(getBeginDate(), getEndDate())) {
            String sd = getBeginDate().atTime(0, 0, 0).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String ed = getEndDate().atTime(23, 59, 59).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            queryMap.put("createTime", new String[]{sd, ed});
        }
        queryMap.values().removeAll(Collections.singleton(null));
        Request.getInstance().create(PostApi.class).getPostPage(queryMap)
                .subscribeOn(Schedulers.io())
                .map(new PayLoad<>())
                .observeOn(Schedulers.from(Platform::runLater)).subscribe(data -> {

                    ObservableList<PostRespVO> userRespVOS = FXCollections.observableArrayList();
                    userRespVOS.addAll(data.getList());
                    tableItems.set(userRespVOS);
                    totalProperty().set(data.getTotal().intValue());

                }, throwable -> {
                    throwable.printStackTrace();
                });


    }

    public void deletePost(Long postId, ConfirmDialog confirmDialog) {

        Request.getInstance().create(PostApi.class).deletePost(postId)
                .subscribeOn(Schedulers.io())
                .map(new PayLoad<>())
                .observeOn(Schedulers.from(Platform::runLater))
                .subscribe(data -> {
                    EventBusCenter.get().post(new MessageEvent("删除成功", MessageType.SUCCESS));
                    EventBusCenter.get().post(new UpdateDataEvent("更新岗位列表"));
                    confirmDialog.close();
                });
    }


    public ObjectProperty<ObservableList<PostRespVO>> tableItemsProperty() {
        return tableItems;
    }

    public Integer getStatus() {
        return status.get();
    }

    public ObjectProperty<Integer> statusProperty() {
        return status;
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public String getCode() {
        return code.get();
    }

    public StringProperty codeProperty() {
        return code;
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

    @Subscribe
    private void refresh(RefreshEvent event) {
        Platform.runLater(() -> loadTableData());
    }

    @Subscribe
    private void updateData(UpdateDataEvent menuEvent) {
        Platform.runLater(() -> {
            if ("更新岗位列表".equals(menuEvent.getMessage())) {
                loadTableData();
            }
        });
    }

}

package com.dillon.lw.fx.view.system.log.operatelog;

import cn.hutool.core.util.ObjectUtil;
import com.dillon.lw.api.system.OperateLogApi;
import com.dillon.lw.api.system.UserApi;
import com.dillon.lw.framework.common.pojo.PageResult;
import com.dillon.lw.fx.eventbus.EventBusCenter;
import com.dillon.lw.fx.eventbus.event.MessageEvent;
import com.dillon.lw.fx.eventbus.event.RefreshEvent;
import com.dillon.lw.fx.eventbus.event.UpdateDataEvent;
import com.dillon.lw.fx.http.PayLoad;
import com.dillon.lw.fx.mvvm.base.BaseViewModel;
import com.dillon.lw.fx.utils.MessageType;
import com.dillon.lw.fx.view.layout.ConfirmDialog;
import com.dillon.lw.module.system.controller.admin.logger.vo.operatelog.OperateLogRespVO;
import com.dillon.lw.module.system.controller.admin.user.vo.user.UserSimpleRespVO;
import com.dtflys.forest.Forest;
import com.google.common.eventbus.Subscribe;
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
import java.util.concurrent.CompletableFuture;

public class OperateLogViewModel extends BaseViewModel {

    private SimpleIntegerProperty total = new SimpleIntegerProperty(0);
    private IntegerProperty pageNum = new SimpleIntegerProperty(0);
    private IntegerProperty pageSize = new SimpleIntegerProperty(10);

    private ObjectProperty<ObservableList<OperateLogRespVO>> tableItems = new SimpleObjectProperty<>();
    private ObjectProperty<UserSimpleRespVO> selUserItem = new SimpleObjectProperty<>();

    private ObservableList<UserSimpleRespVO> userSimpleRespVOObservableItems = FXCollections.observableArrayList();

    private ObjectProperty<LocalDate> beginDate = new SimpleObjectProperty<>();
    private ObjectProperty<LocalDate> endDate = new SimpleObjectProperty<>();
    private StringProperty bizId = new SimpleStringProperty();
    private StringProperty type = new SimpleStringProperty();
    private StringProperty subType = new SimpleStringProperty();
    private StringProperty action = new SimpleStringProperty();
    private ObjectProperty<UserSimpleRespVO> user = new SimpleObjectProperty<>();

    public OperateLogViewModel() {
        initData();
        loadTableData();
    }


    public void initData() {


        CompletableFuture.supplyAsync(() -> {
            return new PayLoad<List<UserSimpleRespVO>>().apply(Forest.client(UserApi.class).getSimpleUserList());
        }).thenAcceptAsync(listCommonResult -> {
            userSimpleRespVOObservableItems.setAll(listCommonResult);
        }, Platform::runLater).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });


    }

    public void loadTableData() {

        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("pageNo", pageNum.get() + 1);
        queryMap.put("pageSize", pageSize.get());

        if (selUserItem.get() != null) {
            queryMap.put("userId", selUserItem.get().getId());
        }
        queryMap.put("bizId", bizId.get());
        queryMap.put("type", type.get());
        queryMap.put("subType", subType.get());
        queryMap.put("action", action.get());

        if (ObjectUtil.isAllNotEmpty(getBeginDate(), getEndDate())) {
            String sd = getBeginDate().atTime(0, 0, 0).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String ed = getEndDate().atTime(23, 59, 59).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            queryMap.put("createTime", new String[]{sd, ed});
        }
        queryMap.values().removeAll(Collections.singleton(null));


        CompletableFuture.supplyAsync(() -> {
            return new PayLoad<PageResult<OperateLogRespVO>>().apply(Forest.client(OperateLogApi.class).pageOperateLog(queryMap));
        }).thenAcceptAsync(listCommonResult -> {
            ObservableList<OperateLogRespVO> userRespVOS = FXCollections.observableArrayList();
            userRespVOS.addAll(listCommonResult.getList());
            tableItems.set(userRespVOS);
            totalProperty().set(listCommonResult.getTotal().intValue());

        }, Platform::runLater).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });


    }

    public void deleteOperateLog(Long operateLogId, ConfirmDialog confirmDialog) {

        CompletableFuture.supplyAsync(() -> {
            return new PayLoad<Boolean>().apply(Forest.client(OperateLogApi.class).deleteOperateLog(operateLogId));
        }).thenAcceptAsync(data -> {
            EventBusCenter.get().post(new MessageEvent("删除成功", MessageType.SUCCESS));
            EventBusCenter.get().post(new UpdateDataEvent("更新操作日志列表"));
            confirmDialog.close();
        }, Platform::runLater).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });
    }

    public ObjectProperty<ObservableList<OperateLogRespVO>> tableItemsProperty() {
        return tableItems;
    }


    public String getType() {
        return type.get();
    }

    public StringProperty typeProperty() {
        return type;
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

    public String getBizId() {
        return bizId.get();
    }

    public StringProperty bizIdProperty() {
        return bizId;
    }

    public String getSubType() {
        return subType.get();
    }

    public StringProperty subTypeProperty() {
        return subType;
    }

    public String getAction() {
        return action.get();
    }

    public StringProperty actionProperty() {
        return action;
    }

    public UserSimpleRespVO getUser() {
        return user.get();
    }

    public ObservableList<UserSimpleRespVO> getUserSimpleRespVOObservableItems() {
        return userSimpleRespVOObservableItems;
    }

    public UserSimpleRespVO getSelUserItem() {
        return selUserItem.get();
    }

    public ObjectProperty<UserSimpleRespVO> selUserItemProperty() {
        return selUserItem;
    }

    public ObjectProperty<UserSimpleRespVO> userProperty() {
        return user;
    }


    @Subscribe
    private void refresh(RefreshEvent event) {
        Platform.runLater(() -> loadTableData());
    }

    @Subscribe
    private void updateData(UpdateDataEvent menuEvent) {
        Platform.runLater(() -> {
            if ("更新操作日志列表".equals(menuEvent.getMessage())) {
                loadTableData();
            }
        });
    }
}

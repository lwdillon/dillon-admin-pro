package com.dillon.lw.fx.view.system.log.operatelog;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import com.dillon.lw.api.system.OperateLogApi;
import com.dillon.lw.api.system.UserApi;
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
import com.dillon.lw.module.system.controller.admin.logger.vo.operatelog.OperateLogRespVO;
import com.dillon.lw.module.system.controller.admin.user.vo.user.UserSimpleRespVO;
import com.dtflys.forest.Forest;
import com.google.common.eventbus.Subscribe;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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


        Single
                /*
                 * 操作日志页面的用户下拉数据先在 IO 线程加载，
                 * 回到 JavaFX UI 线程后再填充下拉选项。
                 */
                .fromCallable(() -> Forest.client(UserApi.class).getSimpleUserList().getCheckedData())
                .subscribeOn(Schedulers.io())
                .observeOn(FxSchedulers.fx())
                .compose(FxRx.bindTo(this))
                .subscribe(listCommonResult -> userSimpleRespVOObservableItems.setAll(listCommonResult), DefaultExceptionHandler::handle);


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
            queryMap.put("createTime", ListUtil.of(sd, ed));
        }
        queryMap.values().removeAll(Collections.singleton(null));


        Single
                /*
                 * 操作日志分页查询改成标准 RxJava 链，
                 * 后台线程负责请求，JavaFX UI 线程负责回填表格和分页总数。
                 */
                .fromCallable(() -> Forest.client(OperateLogApi.class).pageOperateLog(queryMap).getCheckedData())
                .subscribeOn(Schedulers.io())
                .observeOn(FxSchedulers.fx())
                .compose(FxRx.bindTo(this))
                .subscribe(listCommonResult -> {
                    ObservableList<OperateLogRespVO> userRespVOS = FXCollections.observableArrayList();
                    userRespVOS.addAll(listCommonResult.getList());
                    tableItems.set(userRespVOS);
                    totalProperty().set(listCommonResult.getTotal().intValue());
                }, DefaultExceptionHandler::handle);


    }

    public void deleteOperateLog(Long operateLogId, ConfirmDialog confirmDialog) {

        Single
                .fromCallable(() -> Forest.client(OperateLogApi.class).deleteOperateLog(operateLogId).getCheckedData())
                .subscribeOn(Schedulers.io())
                .observeOn(FxSchedulers.fx())
                .compose(FxRx.bindTo(this))
                .subscribe(data -> {
                    EventBusCenter.get().post(new MessageEvent("删除成功", MessageType.SUCCESS));
                    EventBusCenter.get().post(new UpdateDataEvent("更新操作日志列表"));
                    confirmDialog.close();
                }, DefaultExceptionHandler::handle);
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
        FxSchedulers.runOnFx(() -> loadTableData());
    }

    @Subscribe
    private void updateData(UpdateDataEvent menuEvent) {
        FxSchedulers.runOnFx(() -> {
            if ("更新操作日志列表".equals(menuEvent.getMessage())) {
                loadTableData();
            }
        });
    }
}

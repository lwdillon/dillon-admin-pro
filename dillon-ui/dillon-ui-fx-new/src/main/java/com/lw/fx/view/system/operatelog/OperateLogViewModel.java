package com.lw.fx.view.system.operatelog;

import cn.hutool.core.util.ObjectUtil;
import com.lw.dillon.admin.module.system.controller.admin.logger.vo.operatelog.OperateLogRespVO;
import com.lw.dillon.admin.module.system.controller.admin.user.vo.user.UserSimpleRespVO;
import com.lw.fx.request.Request;
import com.lw.ui.request.api.system.OperateLogFeign;
import com.lw.ui.request.api.system.UserFeign;
import de.saxsys.mvvmfx.SceneLifecycle;
import de.saxsys.mvvmfx.ViewModel;
import io.datafx.core.concurrent.ProcessChain;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class OperateLogViewModel implements ViewModel, SceneLifecycle {

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

        ProcessChain.create()
                .addSupplierInExecutor(() -> Request.connector(UserFeign.class).getSimpleUserList())
                .addConsumerInPlatformThread(rel -> {
                    userSimpleRespVOObservableItems.setAll(rel.getData());
                })

                .run();


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

        ProcessChain.create()

                .addSupplierInExecutor(() -> Request.connector(OperateLogFeign.class).pageOperateLog(queryMap))
                .addConsumerInPlatformThread(listCommonResult -> {
                    if (listCommonResult.isSuccess()) {
                        ObservableList<OperateLogRespVO> userRespVOS = FXCollections.observableArrayList();
                        userRespVOS.addAll(listCommonResult.getData().getList());
                        tableItems.set(userRespVOS);
                        totalProperty().set(listCommonResult.getData().getTotal().intValue());
                    }
                })
                .run();


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

    @Override
    public void onViewAdded() {

    }

    @Override
    public void onViewRemoved() {

    }
}

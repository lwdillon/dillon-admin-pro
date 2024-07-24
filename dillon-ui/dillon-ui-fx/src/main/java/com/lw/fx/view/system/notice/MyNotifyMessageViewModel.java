package com.lw.fx.view.system.notice;

import cn.hutool.core.util.ObjectUtil;
import com.lw.dillon.admin.module.system.controller.admin.dict.vo.data.DictDataSimpleRespVO;
import com.lw.dillon.admin.module.system.controller.admin.notify.vo.message.NotifyMessageRespVO;
import com.lw.fx.request.Request;
import com.lw.fx.store.AppStore;
import com.lw.fx.util.MessageType;
import com.lw.fx.vo.NotifyMessageVo;
import com.lw.ui.request.api.system.NotifyMessageFeign;
import de.saxsys.mvvmfx.MvvmFX;
import de.saxsys.mvvmfx.SceneLifecycle;
import de.saxsys.mvvmfx.ViewModel;
import io.datafx.core.concurrent.ProcessChain;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.lw.ui.utils.DictTypeEnum.INFRA_BOOLEAN_STRING;

public class MyNotifyMessageViewModel implements ViewModel, SceneLifecycle {

    private SimpleIntegerProperty total = new SimpleIntegerProperty(0);
    private IntegerProperty pageNum = new SimpleIntegerProperty(0);
    private IntegerProperty pageSize = new SimpleIntegerProperty(10);

    private ObservableList<NotifyMessageVo> tableItems = FXCollections.observableArrayList();
    private ObservableList<DictDataSimpleRespVO> stautsItems = FXCollections.observableArrayList();

    private ObjectProperty<LocalDate> beginDate = new SimpleObjectProperty<>();
    private ObjectProperty<LocalDate> endDate = new SimpleObjectProperty<>();
    private ObjectProperty<Boolean> status = new SimpleObjectProperty<>();

    public MyNotifyMessageViewModel() {
        initData();
        loadTableData();
    }


    public void initData() {

        ProcessChain.create()
                .addSupplierInExecutor(() -> AppStore.getDictDataList(INFRA_BOOLEAN_STRING))
                .addConsumerInPlatformThread(rel -> {
                    stautsItems.setAll(rel);
                })

                .run();


    }

    public void loadTableData() {

        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("pageNo", pageNum.get() + 1);
        queryMap.put("pageSize", pageSize.get());

        queryMap.put("readStatus", status.getValue());

        if (ObjectUtil.isAllNotEmpty(getBeginDate(), getEndDate())) {
            String sd = getBeginDate().atTime(0, 0, 0).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String ed = getEndDate().atTime(23, 59, 59).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            queryMap.put("createTime", new String[]{sd, ed});
        }

        ProcessChain.create()
                .addRunnableInPlatformThread(() -> tableItems.clear())
                .addSupplierInExecutor(() -> Request.connector(NotifyMessageFeign.class).getMyMyNotifyMessagePage(queryMap))
                .addConsumerInPlatformThread(listCommonResult -> {
                    if (listCommonResult.isSuccess()) {

                        listCommonResult.getData().getList().forEach(menu -> tableItems.add(convert(menu)));
                        totalProperty().set(listCommonResult.getData().getTotal().intValue());
                    }
                })
                .run();

    }

    public NotifyMessageVo convert(NotifyMessageRespVO messageRespVO) {
        if (messageRespVO == null) {
            return null;
        }

        NotifyMessageVo messageVo = new NotifyMessageVo();

        messageVo.setId(messageRespVO.getId());
        messageVo.setReadStatus(messageRespVO.getReadStatus());
        messageVo.setCreateTime(messageRespVO.getCreateTime());
        messageVo.setReadTime(messageRespVO.getReadTime());
        messageVo.setTemplateCode(messageRespVO.getTemplateCode());
        messageVo.setTemplateId(messageRespVO.getTemplateId());
        messageVo.setUserId(messageRespVO.getUserId());
        messageVo.setUserType(messageRespVO.getUserType());
        messageVo.setTemplateContent(messageRespVO.getTemplateContent());
        messageVo.setTemplateNickname(messageRespVO.getTemplateNickname());
        messageVo.setTemplateType(messageRespVO.getTemplateType());
        messageVo.setTemplateParams(messageRespVO.getTemplateParams());

        return messageVo;
    }

    public void selAll(boolean sel) {

        for (NotifyMessageVo notifyMessageRespVO : tableItems) {
            notifyMessageRespVO.setSelectd(sel);
        }


    }

    public void updateAllNotifyMessageRead() {

        ProcessChain.create()
                .addSupplierInExecutor(() -> Request.connector(NotifyMessageFeign.class).updateAllNotifyMessageRead())
                .addConsumerInPlatformThread(rel -> {
                    if (rel.isSuccess()) {
                        loadTableData();
                        MvvmFX.getNotificationCenter().publish("message", "标记所有站内信为已读成功", MessageType.SUCCESS);
                        MvvmFX.getNotificationCenter().publish("notify","标记所有站内信为已读成功");
                    }
                })

                .run();

    }

    public void updateNotifyMessageRead() {

        Set<Long> ids = new HashSet<>();

        for (NotifyMessageVo notifyMessageRespVO : tableItems) {

            if (notifyMessageRespVO.isSelectd()) {
                ids.add(notifyMessageRespVO.getId());
            }
        }
        if (ids.isEmpty()) {
            MvvmFX.getNotificationCenter().publish("message", "请选择一条记录", MessageType.WARNING);
            return;
        }

        ProcessChain.create()
                .addSupplierInExecutor(() -> Request.connector(NotifyMessageFeign.class).updateNotifyMessageRead(ids.stream().toList()))
                .addConsumerInPlatformThread(rel -> {
                    if (rel.isSuccess()) {
                        loadTableData();
                        MvvmFX.getNotificationCenter().publish("message", "标记站内信为已读成功", MessageType.SUCCESS);
                        MvvmFX.getNotificationCenter().publish("notify","标记站内信为已读成功");

                    }
                })

                .run();

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

    public ObservableList<NotifyMessageVo> getTableItems() {
        return tableItems;
    }

    public ObservableList<DictDataSimpleRespVO> getStautsItems() {
        return stautsItems;
    }

    public Boolean getStatus() {
        return status.get();
    }

    public ObjectProperty<Boolean> statusProperty() {
        return status;
    }

    @Override
    public void onViewAdded() {

    }

    @Override
    public void onViewRemoved() {

    }
}

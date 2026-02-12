package com.dillon.lw.fx.view.system.notice;

import atlantafx.base.theme.Styles;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.dillon.lw.api.system.NotifyMessageApi;
import com.dillon.lw.fx.eventbus.EventBusCenter;
import com.dillon.lw.fx.eventbus.event.MessageEvent;
import com.dillon.lw.fx.utils.MessageType;
import com.dillon.lw.framework.common.pojo.PageResult;
import com.dillon.lw.module.system.controller.admin.notify.vo.message.NotifyMessageRespVO;
import com.dtflys.forest.Forest;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 我的消息页面（轻量版）。
 * <p>
 * 仅依赖现有接口：
 * 1. 查询我的站内信分页；
 * 2. 全部标记为已读；
 * 不改后端，便于开源项目快速阅读与二次扩展。
 * </p>
 */
public class MyNotifyMessagePane extends BorderPane {

    private static final int DEFAULT_PAGE_NO = 1;
    private static final int DEFAULT_PAGE_SIZE = 50;

    private final ObservableList<NotifyMessageRespVO> tableItems = FXCollections.observableArrayList();
    private final TableView<NotifyMessageRespVO> tableView = new TableView<>(tableItems);
    private final Label totalLabel = new Label("共 0 条");
    private final Button refreshButton = new Button("刷新");
    private final Button markAllReadButton = new Button("全部已读");

    public MyNotifyMessagePane() {
        initLayout();
        loadMyMessages();
    }

    private void initLayout() {
        setPadding(new Insets(10));

        Label title = new Label("我的消息");
        title.getStyleClass().add("title-3");

        refreshButton.getStyleClass().addAll(Styles.FLAT);
        markAllReadButton.getStyleClass().addAll(Styles.FLAT, Styles.ACCENT);

        refreshButton.setOnAction(e -> loadMyMessages());
        markAllReadButton.setOnAction(e -> markAllRead());

        HBox toolbar = new HBox(10, title, new HBox(), totalLabel, refreshButton, markAllReadButton);
        toolbar.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(toolbar.getChildren().get(1), javafx.scene.layout.Priority.ALWAYS);
        toolbar.setPadding(new Insets(0, 0, 10, 0));
        setTop(toolbar);

        initTableColumns();
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        setCenter(tableView);
    }

    private void initTableColumns() {
        TableColumn<NotifyMessageRespVO, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(cell -> new ReadOnlyStringWrapper(String.valueOf(cell.getValue().getId())));
        idCol.setMaxWidth(120);

        TableColumn<NotifyMessageRespVO, String> typeCol = new TableColumn<>("类型");
        typeCol.setCellValueFactory(cell -> new ReadOnlyStringWrapper(String.valueOf(cell.getValue().getTemplateType())));
        typeCol.setMaxWidth(90);

        TableColumn<NotifyMessageRespVO, String> fromCol = new TableColumn<>("发送人");
        fromCol.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().getTemplateNickname()));
        fromCol.setMaxWidth(140);

        TableColumn<NotifyMessageRespVO, String> contentCol = new TableColumn<>("内容");
        contentCol.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().getTemplateContent()));

        TableColumn<NotifyMessageRespVO, String> readCol = new TableColumn<>("状态");
        readCol.setCellValueFactory(cell -> new ReadOnlyStringWrapper(Boolean.TRUE.equals(cell.getValue().getReadStatus()) ? "已读" : "未读"));
        readCol.setMaxWidth(90);

        TableColumn<NotifyMessageRespVO, String> createTimeCol = new TableColumn<>("时间");
        createTimeCol.setCellValueFactory(cell -> new ReadOnlyStringWrapper(formatDateTime(cell.getValue().getCreateTime())));
        createTimeCol.setMaxWidth(180);

        tableView.getColumns().setAll(idCol, typeCol, fromCol, contentCol, readCol, createTimeCol);
    }

    private void loadMyMessages() {
        refreshButton.setDisable(true);
        Map<String, Object> pageVO = new HashMap<>();
        pageVO.put("pageNo", DEFAULT_PAGE_NO);
        pageVO.put("pageSize", DEFAULT_PAGE_SIZE);

        CompletableFuture
                .supplyAsync(() -> Forest.client(NotifyMessageApi.class).getMyMyNotifyMessagePage(pageVO).getCheckedData())
                .thenAcceptAsync(this::renderPageResult, Platform::runLater)
                .exceptionally(ex -> {
                    Platform.runLater(() -> EventBusCenter.get().post(new MessageEvent("加载我的消息失败", MessageType.DANGER)));
                    return null;
                })
                .whenComplete((unused, ex) -> Platform.runLater(() -> refreshButton.setDisable(false)));
    }

    private void renderPageResult(PageResult<NotifyMessageRespVO> pageResult) {
        tableItems.setAll(pageResult.getList());
        totalLabel.setText("共 " + pageResult.getTotal() + " 条");
    }

    private void markAllRead() {
        markAllReadButton.setDisable(true);
        CompletableFuture
                .supplyAsync(() -> Forest.client(NotifyMessageApi.class).updateAllNotifyMessageRead().getCheckedData())
                .thenAcceptAsync(success -> {
                    if (Boolean.TRUE.equals(success)) {
                        EventBusCenter.get().post(new MessageEvent("全部标记为已读", MessageType.SUCCESS));
                        loadMyMessages();
                    } else {
                        EventBusCenter.get().post(new MessageEvent("全部已读操作失败", MessageType.WARNING));
                    }
                }, Platform::runLater)
                .exceptionally(ex -> {
                    Platform.runLater(() -> EventBusCenter.get().post(new MessageEvent("全部已读操作失败", MessageType.DANGER)));
                    return null;
                })
                .whenComplete((unused, ex) -> Platform.runLater(() -> markAllReadButton.setDisable(false)));
    }

    private String formatDateTime(java.time.LocalDateTime time) {
        return time == null ? "-" : LocalDateTimeUtil.formatNormal(time);
    }
}

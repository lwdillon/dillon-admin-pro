package com.lw.fx.view.system.notice;

import com.lw.dillon.admin.module.system.controller.admin.notice.vo.NoticeRespVO;
import com.lw.fx.request.Request;
import com.lw.ui.request.api.system.NoticeFeign;
import de.saxsys.mvvmfx.SceneLifecycle;
import de.saxsys.mvvmfx.ViewModel;
import io.datafx.core.concurrent.ProcessChain;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.HashMap;
import java.util.Map;

public class NoticeViewModel implements ViewModel, SceneLifecycle {

    private SimpleIntegerProperty total = new SimpleIntegerProperty(0);
    private IntegerProperty pageNum = new SimpleIntegerProperty(0);
    private IntegerProperty pageSize = new SimpleIntegerProperty(10);

    private ObjectProperty<ObservableList<NoticeRespVO>> tableItems = new SimpleObjectProperty<>();

    private StringProperty title = new SimpleStringProperty();
    private ObjectProperty<Integer> status = new SimpleObjectProperty<>();

    public NoticeViewModel() {

        loadTableData();
    }



    public void loadTableData() {

        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("pageNo", pageNum.get() + 1);
        queryMap.put("pageSize", pageSize.get());

        queryMap.put("title", title.get());
        queryMap.put("status", status.get());

     
        ProcessChain.create()
                .addSupplierInExecutor(() -> Request.connector(NoticeFeign.class).getNoticePage(queryMap))
                .addConsumerInPlatformThread(listCommonResult -> {

                    if (listCommonResult.isSuccess()) {
                        ObservableList<NoticeRespVO> userRespVOS = FXCollections.observableArrayList();
                        userRespVOS.addAll(listCommonResult.getData().getList());
                        tableItems.set(userRespVOS);
                        totalProperty().set(listCommonResult.getData().getTotal().intValue());
                    }
                })
                .run();


    }




    public ObjectProperty<ObservableList<NoticeRespVO>> tableItemsProperty() {
        return tableItems;
    }

    public Integer getStatus() {
        return status.get();
    }

    public ObjectProperty<Integer> statusProperty() {
        return status;
    }

    public String getTitle() {
        return title.get();
    }

    public StringProperty titleProperty() {
        return title;
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

    @Override
    public void onViewAdded() {

    }

    @Override
    public void onViewRemoved() {

    }
}

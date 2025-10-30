package com.dillon.lw.fx.view.system.dict.data;

import cn.hutool.core.util.ObjectUtil;
import com.dillon.lw.api.system.DictDataApi;
import com.dillon.lw.api.system.DictTypeApi;
import com.dillon.lw.module.system.controller.admin.dict.vo.data.DictDataRespVO;
import com.dillon.lw.module.system.controller.admin.dict.vo.type.DictTypeSimpleRespVO;
import com.google.common.eventbus.Subscribe;
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

public class DictDataViewModel extends BaseViewModel {

    private SimpleIntegerProperty total = new SimpleIntegerProperty(0);
    private IntegerProperty pageNum = new SimpleIntegerProperty(0);
    private IntegerProperty pageSize = new SimpleIntegerProperty(10);

    private ObjectProperty<ObservableList<DictDataRespVO>> tableItems = new SimpleObjectProperty<>();
    private ObservableList<DictTypeSimpleRespVO> dictTypeSimpleRespVOItems = FXCollections.observableArrayList();
    private ObjectProperty<DictTypeSimpleRespVO> selDictTypeSimpleRespVO = new SimpleObjectProperty<>(new DictTypeSimpleRespVO());

    private ObjectProperty<LocalDate> beginDate = new SimpleObjectProperty<>();
    private ObjectProperty<LocalDate> endDate = new SimpleObjectProperty<>();
    private StringProperty label = new SimpleStringProperty();
    private ObjectProperty<Integer> status = new SimpleObjectProperty<>();

    public DictDataViewModel() {
        selDictTypeSimpleRespVOProperty().addListener(observable -> loadTableData());
    }

    public void loadDictTypeData(String dictType) {
        dictTypeSimpleRespVOItems.clear();
        Request.getInstance().create(DictTypeApi.class).getSimpleDictTypeList()
                .subscribeOn(Schedulers.io())
                .map(new PayLoad<>())
                .observeOn(Schedulers.from(Platform::runLater))
                .subscribe(data -> {
                    dictTypeSimpleRespVOItems.setAll(data);
                    if (ObjectUtil.isNotEmpty(dictType)) {
                        for (DictTypeSimpleRespVO item : data) {
                            if (dictType.equals(item.getType())) {
                                selDictTypeSimpleRespVOProperty().set(item);
                                break;
                            }
                        }
                    }
                }, e -> {
                    e.printStackTrace();
                });


    }

    public void loadTableData() {


        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("pageNo", pageNum.get() + 1);
        queryMap.put("pageSize", pageSize.get());

        queryMap.put("label", label.get());
        queryMap.put("dictType", selDictTypeSimpleRespVO.getValue().getType());
        queryMap.put("status", status.get());

        if (ObjectUtil.isAllNotEmpty(getBeginDate(), getEndDate())) {
            String sd = getBeginDate().atTime(0, 0, 0).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String ed = getEndDate().atTime(23, 59, 59).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            queryMap.put("createTime", new String[]{sd, ed});
        }

        queryMap.values().removeAll(Collections.singleton(null));


        Request.getInstance().create(DictDataApi.class).getDictTypePage(queryMap)
                .subscribeOn(Schedulers.io())
                .map(new PayLoad<>())
                .observeOn(Schedulers.from(Platform::runLater))
                .subscribe(data -> {
                    ObservableList<DictDataRespVO> userRespVOS = FXCollections.observableArrayList();
                    userRespVOS.addAll(data.getList());
                    tableItems.set(userRespVOS);
                    totalProperty().set(data.getTotal().intValue());
                }, throwable -> {
                    throwable.printStackTrace();
                });

    }

    public void deleteDictData(Long id, ConfirmDialog confirmDialog) {
        if (id == null) {
            return;
        }
        Request.getInstance().create(DictDataApi.class).deleteDictData(id)
                .subscribeOn(Schedulers.io())
                .map(new PayLoad<>())
                .observeOn(Schedulers.from(Platform::runLater))
                .subscribe(result -> {
                    EventBusCenter.get().post(new UpdateDataEvent("更新字典数据列表"));
                    EventBusCenter.get().post(new MessageEvent("删除成功", MessageType.SUCCESS));
                    confirmDialog.close();
                }, throwable -> {
                    System.err.println("删除字典数据异常：" + throwable.getMessage());
                });
    }

    @Subscribe
    private void updateData(UpdateDataEvent menuEvent) {
        Platform.runLater(() -> {
            if ("更新字典数据列表".equals(menuEvent.getMessage())) {
                loadTableData();
            }
        });
    }



    @Subscribe
    private void refresh(RefreshEvent event) {
        Platform.runLater(() -> loadTableData());
    }


    public ObjectProperty<ObservableList<DictDataRespVO>> tableItemsProperty() {
        return tableItems;
    }

    public Integer getStatus() {
        return status.get();
    }

    public ObjectProperty<Integer> statusProperty() {
        return status;
    }

    public String getLabel() {
        return label.get();
    }

    public StringProperty labelProperty() {
        return label;
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

    public ObservableList<DictDataRespVO> getTableItems() {
        return tableItems.get();
    }

    public ObservableList<DictTypeSimpleRespVO> getDictTypeSimpleRespVOItems() {
        return dictTypeSimpleRespVOItems;
    }

    public DictTypeSimpleRespVO getSelDictTypeSimpleRespVO() {
        return selDictTypeSimpleRespVO.get();
    }

    public ObjectProperty<DictTypeSimpleRespVO> selDictTypeSimpleRespVOProperty() {
        return selDictTypeSimpleRespVO;
    }


}

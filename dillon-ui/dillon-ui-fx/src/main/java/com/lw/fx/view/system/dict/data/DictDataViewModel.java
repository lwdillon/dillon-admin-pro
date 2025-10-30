package com.lw.fx.view.system.dict.data;

import cn.hutool.core.util.ObjectUtil;
import com.lw.dillon.admin.module.system.controller.admin.dict.vo.data.DictDataRespVO;
import com.lw.dillon.admin.module.system.controller.admin.dict.vo.type.DictTypeSimpleRespVO;
import com.lw.fx.request.Request;
import com.lw.ui.request.api.system.DictDataFeign;
import com.lw.ui.request.api.system.DictTypeFeign;
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

public class DictDataViewModel implements ViewModel, SceneLifecycle {

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

        ProcessChain.create()
               .addSupplierInExecutor(() -> Request.connector(DictTypeFeign.class).getSimpleDictTypeList())
                .addConsumerInPlatformThread(listCommonResult -> {
                    // 设置选中项为 name 等于 "abc" 的对象
                    for (DictTypeSimpleRespVO item : listCommonResult.getData()) {
                        if (dictType.equals(item.getType())) {
                            selDictTypeSimpleRespVOProperty().set(item);
                            break;
                        }
                    }
                    dictTypeSimpleRespVOItems.setAll(listCommonResult.getData());
                })
                .run();


    }

    public void loadTableData() {


        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("pageNo", pageNum.get() + 1);
        queryMap.put("pageSize", pageSize.get());

        queryMap.put("label", label.get());
        queryMap.put("dictType",selDictTypeSimpleRespVO.getValue().getType());
        queryMap.put("status", status.get());

        if (ObjectUtil.isAllNotEmpty(getBeginDate(), getEndDate())) {
            String sd = getBeginDate().atTime(0, 0, 0).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String ed = getEndDate().atTime(23, 59, 59).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            queryMap.put("createTime", new String[]{sd, ed});
        }

        ProcessChain.create()
                .addSupplierInExecutor(() -> Request.connector(DictDataFeign.class).getDictDataPage(queryMap))
                .addConsumerInPlatformThread(listCommonResult -> {

                    if (listCommonResult.isSuccess()) {
                        ObservableList<DictDataRespVO> userRespVOS = FXCollections.observableArrayList();
                        userRespVOS.addAll(listCommonResult.getData().getList());
                        tableItems.set(userRespVOS);
                        totalProperty().set(listCommonResult.getData().getTotal().intValue());
                    }
                })
                .run();


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

    @Override
    public void onViewAdded() {

    }

    @Override
    public void onViewRemoved() {

    }
}

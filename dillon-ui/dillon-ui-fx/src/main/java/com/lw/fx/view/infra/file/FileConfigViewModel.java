package com.lw.fx.view.infra.file;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import com.google.gson.JsonObject;
import com.lw.dillon.admin.module.infra.controller.admin.file.vo.config.FileConfigRespVO;
import com.lw.dillon.admin.module.system.controller.admin.dict.vo.data.DictDataSimpleRespVO;
import com.lw.fx.request.Request;
import com.lw.ui.request.api.file.FileConfigFeign;
import de.saxsys.mvvmfx.ViewModel;
import io.datafx.core.concurrent.ProcessChain;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileConfigViewModel implements ViewModel {
    private SimpleIntegerProperty total = new SimpleIntegerProperty(0);
    private IntegerProperty pageNum = new SimpleIntegerProperty(0);
    private IntegerProperty pageSize = new SimpleIntegerProperty(10);
    private ObjectProperty<LocalDate> beginDate = new SimpleObjectProperty<>();
    private ObjectProperty<LocalDate> endDate = new SimpleObjectProperty<>();

    private StringProperty name = new SimpleStringProperty();
    private ObjectProperty<DictDataSimpleRespVO> storage = new SimpleObjectProperty<>();

    private ObservableList<FileConfigRespVO> tableItems = FXCollections.observableArrayList();

    public FileConfigViewModel() {
        loadTableData();
    }


    public void loadTableData() {

        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("pageNo", pageNum.get() + 1);
        queryMap.put("pageSize", pageSize.get());

        queryMap.put("name", name.get());
        if (storage.get() != null) {
            queryMap.put("storage", Convert.toInt(storage.get().getValue(), null));
        }

        if (ObjectUtil.isAllNotEmpty(getBeginDate(), getEndDate())) {
            String sd = getBeginDate().atTime(0, 0, 0).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String ed = getEndDate().atTime(23, 59, 59).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            queryMap.put("createTime", new String[]{sd, ed});
        }

        ProcessChain.create()
                .addRunnableInPlatformThread(() -> tableItems.clear())
                .addSupplierInExecutor(() -> Request.connector(FileConfigFeign.class).getFileConfigPage(queryMap))
                .addConsumerInPlatformThread(listCommonResult -> {
                    if (listCommonResult.isSuccess()) {
                        List<JsonObject> list = listCommonResult.getData().getList();
                        for (JsonObject jsonObject:list) {
                            FileConfigRespVO fileConfigRespVO = new FileConfigRespVO();
                            fileConfigRespVO.setId(jsonObject.get("id").getAsLong());
                            fileConfigRespVO.setName(jsonObject.get("name").getAsString());
                            fileConfigRespVO.setStorage(jsonObject.get("storage").getAsInt());
                            fileConfigRespVO.setRemark(jsonObject.get("remark").getAsString());
                            fileConfigRespVO.setMaster(jsonObject.get("master").getAsBoolean());
                            fileConfigRespVO.setCreateTime(Convert.toLocalDateTime(jsonObject.get("createTime")));
                            tableItems.add(fileConfigRespVO);
                        }
                        totalProperty().set(listCommonResult.getData().getTotal().intValue());
                    }
                }).onException(e -> e.printStackTrace())
                .run();


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

    public ObservableList<FileConfigRespVO> getTableItems() {
        return tableItems;
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public DictDataSimpleRespVO getStorage() {
        return storage.get();
    }

    public ObjectProperty<DictDataSimpleRespVO> storageProperty() {
        return storage;
    }
}

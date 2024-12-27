package com.lw.fx.view.system.oauth2.token;

import com.lw.dillon.admin.module.system.controller.admin.dict.vo.data.DictDataSimpleRespVO;
import com.lw.dillon.admin.module.system.controller.admin.oauth2.vo.token.OAuth2AccessTokenRespVO;
import com.lw.fx.request.Request;
import com.lw.fx.store.AppStore;
import com.lw.ui.request.api.system.OAuth2TokenFeign;
import de.saxsys.mvvmfx.SceneLifecycle;
import de.saxsys.mvvmfx.ViewModel;
import io.datafx.core.concurrent.ProcessChain;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static com.lw.ui.utils.DictTypeEnum.USER_TYPE;

public class TokenViewModel implements ViewModel, SceneLifecycle {

    private SimpleIntegerProperty total = new SimpleIntegerProperty(0);
    private IntegerProperty pageNum = new SimpleIntegerProperty(0);
    private IntegerProperty pageSize = new SimpleIntegerProperty(10);

    private ObjectProperty<ObservableList<OAuth2AccessTokenRespVO>> tableItems = new SimpleObjectProperty<>();

    private ObservableList<DictDataSimpleRespVO> userTypeItems = FXCollections.observableArrayList();

    private ObjectProperty<DictDataSimpleRespVO> selUserType = new SimpleObjectProperty<>();
    private ObjectProperty<LocalDate> beginDate = new SimpleObjectProperty<>();
    private ObjectProperty<LocalDate> endDate = new SimpleObjectProperty<>();
    private StringProperty userId = new SimpleStringProperty();
    private StringProperty clientId = new SimpleStringProperty();

    public TokenViewModel() {
        initData();
        loadTableData();
    }

    public void initData() {

        ProcessChain.create()
                .addSupplierInExecutor(() -> AppStore.getDictDataList(USER_TYPE))
                .addConsumerInPlatformThread(rel -> {
                    userTypeItems.setAll(rel);
                })

                .run();


    }

    public void loadTableData() {

        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("pageNo", pageNum.get() + 1);
        queryMap.put("pageSize", pageSize.get());

        queryMap.put("userId", userId.get());
        if (selUserType.get() != null) {
            queryMap.put("userType", selUserType.get().getValue());
        }
        queryMap.put("clientId", clientId.get());


        ProcessChain.create()

                .addSupplierInExecutor(() -> Request.connector(OAuth2TokenFeign.class).getAccessTokenPage(queryMap))
                .addConsumerInPlatformThread(listCommonResult -> {
                    if (listCommonResult.isSuccess()) {
                        ObservableList<OAuth2AccessTokenRespVO> userRespVOS = FXCollections.observableArrayList();
                        userRespVOS.addAll(listCommonResult.getData().getList());
                        tableItems.set(userRespVOS);
                        totalProperty().set(listCommonResult.getData().getTotal().intValue());
                    }
                })
                .run();


    }


    public ObjectProperty<ObservableList<OAuth2AccessTokenRespVO>> tableItemsProperty() {
        return tableItems;
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

    public String getUserId() {
        return userId.get();
    }

    public StringProperty userIdProperty() {
        return userId;
    }

    public DictDataSimpleRespVO getSelUserType() {
        return selUserType.get();
    }

    public ObjectProperty<DictDataSimpleRespVO> selUserTypeProperty() {
        return selUserType;
    }

    public ObservableList<DictDataSimpleRespVO> getUserTypeItems() {
        return userTypeItems;
    }

    @Override
    public void onViewAdded() {

    }

    @Override
    public void onViewRemoved() {

    }
}

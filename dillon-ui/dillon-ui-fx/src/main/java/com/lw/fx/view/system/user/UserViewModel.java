package com.lw.fx.view.system.user;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.dlsc.gemsfx.daterange.DateRange;
import com.lw.dillon.admin.framework.common.pojo.CommonResult;
import com.lw.dillon.admin.module.system.controller.admin.dept.vo.dept.DeptSimpleRespVO;
import com.lw.dillon.admin.module.system.controller.admin.user.vo.user.UserRespVO;
import com.lw.dillon.admin.module.system.controller.admin.user.vo.user.UserUpdatePasswordReqVO;
import com.lw.fx.request.Request;
import com.lw.fx.view.control.FilterableTreeItem;
import com.lw.ui.request.api.system.DeptFeign;
import com.lw.ui.request.api.system.UserFeign;
import de.saxsys.mvvmfx.SceneLifecycle;
import de.saxsys.mvvmfx.ViewModel;
import io.datafx.core.concurrent.ProcessChain;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class UserViewModel implements ViewModel, SceneLifecycle {

    private SimpleIntegerProperty total = new SimpleIntegerProperty(0);
    private IntegerProperty pageNum = new SimpleIntegerProperty(0);
    private IntegerProperty pageSize = new SimpleIntegerProperty(10);
    private ObjectProperty<FilterableTreeItem<DeptSimpleRespVO>> deptTreeRoot = new SimpleObjectProperty<>();

    private ObjectProperty<ObservableList<UserRespVO>> tableItems = new SimpleObjectProperty<>();

    private ObjectProperty<DeptSimpleRespVO> selectDept = new SimpleObjectProperty<>(new DeptSimpleRespVO());
    private ObjectProperty<DateRange> dateRange = new SimpleObjectProperty<>(new DateRange("创建时间", LocalDate.MIN));
    private StringProperty username = new SimpleStringProperty();
    private StringProperty mobile = new SimpleStringProperty();
    private ObjectProperty<Integer> status = new SimpleObjectProperty<>();

    public UserViewModel() {

        loadTreeData();
        loadTableData();
    }

    public void loadTreeData() {

        ProcessChain.create()
                .addSupplierInExecutor(() -> Request.connector(DeptFeign.class).getSimpleDeptList())
                .addConsumerInPlatformThread(listCommonResult -> {

                    DeptSimpleRespVO respVO = new DeptSimpleRespVO();
                    respVO.setId(-1L);
                    respVO.setName("主类目");

                    FilterableTreeItem<DeptSimpleRespVO> root = new FilterableTreeItem<DeptSimpleRespVO>(respVO);
                    root.setExpanded(true);
                    // Build the tree
                    Map<Long, FilterableTreeItem<DeptSimpleRespVO>> nodeMap = new HashMap<>();
                    nodeMap.put(0l, root); // Root node


                    if (listCommonResult.isSuccess()) {
                        for (DeptSimpleRespVO deptSimpleRespVO : listCommonResult.getData()) {
                            FilterableTreeItem<DeptSimpleRespVO> node = new FilterableTreeItem<DeptSimpleRespVO>(deptSimpleRespVO);
                            nodeMap.put(deptSimpleRespVO.getId(), node);
                        }

                        listCommonResult.getData().forEach(menuSimpleRespVO -> {

                            FilterableTreeItem<DeptSimpleRespVO> parentNode = nodeMap.get(menuSimpleRespVO.getParentId());
                            FilterableTreeItem<DeptSimpleRespVO> childNode = nodeMap.get(menuSimpleRespVO.getId());
                            if (parentNode != null) {
                                parentNode.getInternalChildren().add(childNode);
                            }
                        });
                        deptTreeRoot.set(root);
                    }
                })
                .run();


    }

    public void loadTableData() {

        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("pageNo", pageNum.get() + 1);
        queryMap.put("pageSize", pageSize.get());

        queryMap.put("deptId", selectDept.get().getId() == null ? null : (selectDept.get().getId() == -1 ? null : selectDept.get().getId()));
        queryMap.put("username", username.get());
        queryMap.put("mobile", mobile.get());
        queryMap.put("status", status.get());

        if (ObjectUtil.isAllNotEmpty(dateRange.getValue().getStartDate(), dateRange.getValue().getEndDate())) {
            if (dateRange.getValue().getStartDate() != LocalDate.MIN && dateRange.getValue().getEndDate() != LocalDate.MIN) {
                String[] dateTimes = new String[2];
                dateTimes[0] = DateUtil.format(dateRange.getValue().getStartDate().atTime(0, 0, 0), "yyyy-MM-dd HH:mm:ss");
                dateTimes[1] = DateUtil.format(dateRange.getValue().getEndDate().atTime(23, 59, 59), "yyyy-MM-dd HH:mm:ss");
                queryMap.put("createTime", dateTimes);
            }

        }

        ProcessChain.create()
                .addSupplierInExecutor(() -> Request.connector(UserFeign.class).getUserPage(queryMap))
                .addConsumerInPlatformThread(listCommonResult -> {

                    if (listCommonResult.isSuccess()) {
                        ObservableList<UserRespVO> userRespVOS = FXCollections.observableArrayList();
                        userRespVOS.addAll(listCommonResult.getData().getList());
                        tableItems.set(userRespVOS);
                        totalProperty().set(listCommonResult.getData().getTotal().intValue());
                    }
                })
                .run();


    }

    public CommonResult<Boolean> updateUserPassword(UserUpdatePasswordReqVO passwordReqVO) {
        return Request.connector(UserFeign.class).updateUserPassword(passwordReqVO);
    }

    public ObjectProperty<FilterableTreeItem<DeptSimpleRespVO>> deptTreeRootProperty() {
        return deptTreeRoot;
    }


    public ObjectProperty<ObservableList<UserRespVO>> tableItemsProperty() {
        return tableItems;
    }

    public Integer getStatus() {
        return status.get();
    }

    public ObjectProperty<Integer> statusProperty() {
        return status;
    }

    public String getMobile() {
        return mobile.get();
    }

    public StringProperty mobileProperty() {
        return mobile;
    }

    public String getUsername() {
        return username.get();
    }

    public StringProperty usernameProperty() {
        return username;
    }

    public DateRange getDateRange() {
        return dateRange.get();
    }

    public ObjectProperty<DateRange> dateRangeProperty() {
        return dateRange;
    }

    public DeptSimpleRespVO getSelectDept() {
        return selectDept.get();
    }

    public ObjectProperty<DeptSimpleRespVO> selectDeptProperty() {
        return selectDept;
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

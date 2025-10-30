package com.dillon.lw.fx.view.system.user;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.dillon.lw.module.system.controller.admin.dept.vo.dept.DeptSimpleRespVO;
import com.dillon.lw.module.system.controller.admin.user.vo.user.UserRespVO;
import com.dillon.lw.module.system.controller.admin.user.vo.user.UserUpdatePasswordReqVO;
import com.dlsc.gemsfx.daterange.DateRange;
import com.google.common.eventbus.Subscribe;
import com.dillon.lw.api.system.DeptApi;
import com.dillon.lw.api.system.UserApi;
import com.dillon.lw.fx.eventbus.EventBusCenter;
import com.dillon.lw.fx.eventbus.event.MessageEvent;
import com.dillon.lw.fx.eventbus.event.RefreshEvent;
import com.dillon.lw.fx.eventbus.event.UpdateDataEvent;
import com.dillon.lw.fx.http.PayLoad;
import com.dillon.lw.fx.http.Request;
import com.dillon.lw.fx.mvvm.base.BaseViewModel;
import com.dillon.lw.fx.utils.MessageType;
import com.dillon.lw.fx.view.layout.ConfirmDialog;
import com.dillon.lw.fx.view.layout.FilterableTreeItem;
import io.reactivex.rxjava3.schedulers.Schedulers;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class UserViewModel extends BaseViewModel {

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

        Request.getInstance().create(DeptApi.class)
                .getSimpleDeptList()
                .subscribeOn(Schedulers.io())
                .map(new PayLoad<>())
                .observeOn(Schedulers.from(Platform::runLater))
                .subscribe(data -> {
                    DeptSimpleRespVO respVO = new DeptSimpleRespVO();
                    respVO.setId(-1L);
                    respVO.setName("主类目");

                    FilterableTreeItem<DeptSimpleRespVO> root = new FilterableTreeItem<DeptSimpleRespVO>(respVO);
                    root.setExpanded(true);
                    // Build the tree
                    Map<Long, FilterableTreeItem<DeptSimpleRespVO>> nodeMap = new HashMap<>();
                    nodeMap.put(0l, root); // Root node


                    for (DeptSimpleRespVO deptSimpleRespVO : data) {
                        FilterableTreeItem<DeptSimpleRespVO> node = new FilterableTreeItem<DeptSimpleRespVO>(deptSimpleRespVO);
                        nodeMap.put(deptSimpleRespVO.getId(), node);
                    }

                    data.forEach(menuSimpleRespVO -> {

                        FilterableTreeItem<DeptSimpleRespVO> parentNode = nodeMap.get(menuSimpleRespVO.getParentId());
                        FilterableTreeItem<DeptSimpleRespVO> childNode = nodeMap.get(menuSimpleRespVO.getId());
                        if (parentNode != null) {
                            parentNode.getInternalChildren().add(childNode);
                        }
                    });
                    deptTreeRoot.set(root);

                }, throwable -> {
                    throwable.printStackTrace();
                });

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
        queryMap.values().removeAll(Collections.singleton(null));

        Request.getInstance().create(UserApi.class)
                .getUserPage(queryMap)
                .subscribeOn(Schedulers.io())
                .map(new PayLoad<>())
                .observeOn(Schedulers.from(Platform::runLater))
                .subscribe(data -> {

                    ObservableList<UserRespVO> userRespVOS = FXCollections.observableArrayList();
                    userRespVOS.addAll(data.getList());
                    tableItems.set(userRespVOS);
                    totalProperty().set(data.getTotal().intValue());

                }, throwable -> {
                    throwable.printStackTrace();
                });

    }

    public void updateUserPassword(UserUpdatePasswordReqVO passwordReqVO, ConfirmDialog confirmDialog) {
        Request.getInstance().create(UserApi.class)
                .updateUserPassword(passwordReqVO)
                .subscribeOn(Schedulers.io())
                .map(new PayLoad<>())
                .observeOn(Schedulers.from(Platform::runLater))
                .subscribe(commonResult -> {
                    EventBusCenter.get().post(new MessageEvent("密码更新成功", MessageType.SUCCESS));
                    confirmDialog.close();
                }, throwable -> {
                    EventBusCenter.get().post(new MessageEvent("密码更新异常: " + throwable.getMessage(), MessageType.DANGER));
                    throwable.printStackTrace();
                });

    }

    public void delUser(Long userId, ConfirmDialog confirmDialog) {
        Request.getInstance().create(UserApi.class)
                .deleteUser(userId)
                .subscribeOn(Schedulers.io())
                .map(new PayLoad<>())
                .observeOn(Schedulers.from(Platform::runLater))
                .subscribe(commonResult -> {
                    EventBusCenter.get().post(new MessageEvent("删除成功", MessageType.SUCCESS));
                    EventBusCenter.get().post(new UpdateDataEvent("更新用户列表"));
                    confirmDialog.close();
                }, throwable -> {
                    EventBusCenter.get().post(new MessageEvent("删除失败: " + throwable.getMessage(), MessageType.DANGER));
                    throwable.printStackTrace();
                });

    }

    @Subscribe
    private void refresh(RefreshEvent event) {
        Platform.runLater(() -> loadTableData());
    }

    @Subscribe
    private void updateData(UpdateDataEvent menuEvent) {
        Platform.runLater(() -> {
            if ("更新用户列表".equals(menuEvent.getMessage())) {
                loadTableData();
            }
        });
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


}

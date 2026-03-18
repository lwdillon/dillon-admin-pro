package com.dillon.lw.fx.view.system.user;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.dillon.lw.api.system.DeptApi;
import com.dillon.lw.api.system.UserApi;
import com.dillon.lw.fx.DefaultExceptionHandler;
import com.dillon.lw.fx.eventbus.EventBusCenter;
import com.dillon.lw.fx.eventbus.event.MessageEvent;
import com.dillon.lw.fx.eventbus.event.RefreshEvent;
import com.dillon.lw.fx.eventbus.event.UpdateDataEvent;
import com.dillon.lw.fx.mvvm.base.BaseViewModel;
import com.dillon.lw.fx.rx.FxSchedulers;
import com.dillon.lw.fx.rx.FxRx;
import com.dillon.lw.fx.utils.MessageType;
import com.dillon.lw.fx.view.layout.ConfirmDialog;
import com.dillon.lw.fx.view.layout.FilterableTreeItem;
import com.dillon.lw.module.system.controller.admin.dept.vo.dept.DeptSimpleRespVO;
import com.dillon.lw.module.system.controller.admin.user.vo.user.UserRespVO;
import com.dillon.lw.module.system.controller.admin.user.vo.user.UserUpdatePasswordReqVO;
import com.dlsc.gemsfx.daterange.DateRange;
import com.dtflys.forest.Forest;
import com.google.common.eventbus.Subscribe;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserViewModel extends BaseViewModel {

    private SimpleIntegerProperty total = new SimpleIntegerProperty(0);
    private IntegerProperty pageNum = new SimpleIntegerProperty(0);
    private IntegerProperty pageSize = new SimpleIntegerProperty(10);
    private ObjectProperty<FilterableTreeItem<DeptSimpleRespVO>> deptTreeRoot = new SimpleObjectProperty<>();

    private ObjectProperty<ObservableList<UserRespVO>> tableItems = new SimpleObjectProperty<>();

    private ObjectProperty<DeptSimpleRespVO> selectDept = new SimpleObjectProperty<>(new DeptSimpleRespVO());
    private ObjectProperty<DateRange> dateRange = new SimpleObjectProperty<>();
    private StringProperty username = new SimpleStringProperty();
    private StringProperty mobile = new SimpleStringProperty();
    private ObjectProperty<Integer> status = new SimpleObjectProperty<>();

    public UserViewModel() {

        loadTreeData();
        loadTableData();
    }

    public void loadTreeData() {

        Single
                /*
                 * 部门树查询先在 IO 线程取回扁平列表，
                 * 再回到 JavaFX UI 线程构建过滤树节点，避免树构建过程阻塞后台请求线程。
                 */
                .fromCallable(() -> Forest.client(DeptApi.class).getSimpleDeptList().getCheckedData())
                .subscribeOn(Schedulers.io())
                .observeOn(FxSchedulers.fx())
                .compose(FxRx.bindTo(this))
                .subscribe(data -> {
                    DeptSimpleRespVO respVO = new DeptSimpleRespVO();
                    respVO.setId(-1L);
                    respVO.setName("主类目");

                    FilterableTreeItem<DeptSimpleRespVO> root = new FilterableTreeItem<DeptSimpleRespVO>(respVO);
                    root.setExpanded(true);
                    Map<Long, FilterableTreeItem<DeptSimpleRespVO>> nodeMap = new HashMap<>();
                    nodeMap.put(0l, root);

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
                }, DefaultExceptionHandler::handle);

    }

    public void loadTableData() {

        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("pageNo", pageNum.get() + 1);
        queryMap.put("pageSize", pageSize.get());

        queryMap.put("deptId", selectDept.get().getId() == null ? null : (selectDept.get().getId() == -1 ? null : selectDept.get().getId()));
        queryMap.put("username", username.get());
        queryMap.put("mobile", mobile.get());
        queryMap.put("status", status.get());

        if (ObjectUtil.isAllNotEmpty(dateRange.getValue(), dateRange.getValue())) {

            if (dateRange.getValue().getStartDate() != LocalDate.MIN && dateRange.getValue().getEndDate() != LocalDate.MIN) {
                java.lang.String startDate = DateUtil.format(dateRange.getValue().getStartDate().atTime(0, 0, 0), "yyyy-MM-dd HH:mm:ss");
                java.lang.String endDate = DateUtil.format(dateRange.getValue().getEndDate().atTime(23, 59, 59), "yyyy-MM-dd HH:mm:ss");
                queryMap.put("createTime", List.of(startDate, endDate));
            }


        }
        queryMap.values().removeAll(Collections.singleton(null));

        Single
                /*
                 * 用户分页查询保持和 Swing 模块一致的线程模型：
                 * 同步接口走 IO 线程，表格数据回填回到 JavaFX UI 线程。
                 */
                .fromCallable(() -> Forest.client(UserApi.class).getUserPage(queryMap).getCheckedData())
                .subscribeOn(Schedulers.io())
                .observeOn(FxSchedulers.fx())
                .compose(FxRx.bindTo(this))
                .subscribe(data -> {
                    ObservableList<UserRespVO> userRespVOS = FXCollections.observableArrayList();
                    userRespVOS.addAll(data.getList());
                    tableItems.set(userRespVOS);
                    totalProperty().set(data.getTotal().intValue());
                }, DefaultExceptionHandler::handle);

    }

    public void updateUserPassword(UserUpdatePasswordReqVO passwordReqVO, ConfirmDialog confirmDialog) {
        Single
                /*
                 * 修改密码成功后只涉及消息提示和弹窗关闭，
                 * 因此请求在后台线程执行，UI 动作统一在 JavaFX UI 线程收口。
                 */
                .fromCallable(() -> Forest.client(UserApi.class).updateUserPassword(passwordReqVO).getCheckedData())
                .subscribeOn(Schedulers.io())
                .observeOn(FxSchedulers.fx())
                .compose(FxRx.bindTo(this))
                .subscribe(commonResult -> {
                    EventBusCenter.get().post(new MessageEvent("密码更新成功", MessageType.SUCCESS));
                    confirmDialog.close();
                }, DefaultExceptionHandler::handle);

    }

    public void delUser(Long userId, ConfirmDialog confirmDialog) {
        Single
                /*
                 * 删除用户成功后需要广播列表刷新事件，
                 * 所以成功回调回到 JavaFX UI 线程后再统一处理提示和弹窗关闭。
                 */
                .fromCallable(() -> Forest.client(UserApi.class).deleteUser(userId).getCheckedData())
                .subscribeOn(Schedulers.io())
                .observeOn(FxSchedulers.fx())
                .compose(FxRx.bindTo(this))
                .subscribe(commonResult -> {
                    EventBusCenter.get().post(new MessageEvent("删除成功", MessageType.SUCCESS));
                    EventBusCenter.get().post(new UpdateDataEvent("更新用户列表"));
                    confirmDialog.close();
                }, DefaultExceptionHandler::handle);

    }

    @Subscribe
    private void refresh(RefreshEvent event) {
        FxSchedulers.runOnFx(() -> loadTableData());
    }

    @Subscribe
    private void updateData(UpdateDataEvent menuEvent) {
        FxSchedulers.runOnFx(() -> {
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

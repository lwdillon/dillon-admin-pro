package com.dillon.lw.fx.view.system.dept;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.dillon.lw.api.system.DeptApi;
import com.dillon.lw.api.system.UserApi;
import com.dillon.lw.fx.DefaultExceptionHandler;
import com.dillon.lw.fx.eventbus.EventBusCenter;
import com.dillon.lw.fx.eventbus.event.MessageEvent;
import com.dillon.lw.fx.eventbus.event.UpdateDataEvent;
import com.dillon.lw.fx.mvvm.base.BaseViewModel;
import com.dillon.lw.fx.mvvm.mapping.ModelWrapper;
import com.dillon.lw.fx.rx.FxSchedulers;
import com.dillon.lw.fx.rx.FxRx;
import com.dillon.lw.fx.utils.MessageType;
import com.dillon.lw.fx.view.layout.ConfirmDialog;
import com.dillon.lw.module.system.controller.admin.dept.vo.dept.DeptRespVO;
import com.dillon.lw.module.system.controller.admin.dept.vo.dept.DeptSaveReqVO;
import com.dillon.lw.module.system.controller.admin.dept.vo.dept.DeptSimpleRespVO;
import com.dillon.lw.module.system.controller.admin.user.vo.user.UserSimpleRespVO;
import com.dtflys.forest.Forest;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.util.HashMap;
import java.util.Map;

/**
 * 部门对话框视图模型
 *
 * @author wenli
 * @date 2023/02/12
 */
public class DeptFromViewModel extends BaseViewModel {
    public final static String ON_CLOSE = "close";

    private ObjectProperty<TreeItem<DeptSimpleRespVO>> deptTreeRoot = new SimpleObjectProperty<>();
    private ObjectProperty<TreeItem<DeptSimpleRespVO>> selectTreeItem = new SimpleObjectProperty<>();
    private ObjectProperty<UserSimpleRespVO> selectLeaderUser = new SimpleObjectProperty<>();
    private ObjectProperty<ObservableList<UserSimpleRespVO>> leaderUserList = new SimpleObjectProperty<>();
    /**
     * 包装器
     */
    private ModelWrapper<DeptSaveReqVO> wrapper = new ModelWrapper<>();

    public void addDept(ConfirmDialog confirmDialog) {

        wrapper.get().setLeaderUserId(selectLeaderUser.get() != null ? selectLeaderUser.get().getId() : null);
        wrapper.commit();
        DeptSaveReqVO deptSaveReqVO = wrapper.get();

        Single
                .fromCallable(() -> Forest.client(DeptApi.class).createDept(deptSaveReqVO).getCheckedData())
                .subscribeOn(Schedulers.io())
                .observeOn(FxSchedulers.fx())
                .compose(FxRx.bindTo(this))
                .subscribe(deptId -> {
                    EventBusCenter.get().post(new MessageEvent("添加成功！", MessageType.SUCCESS));
                    EventBusCenter.get().post(new UpdateDataEvent("更新部门列表", deptId));
                    confirmDialog.close();
                }, DefaultExceptionHandler::handle);
    }

    public void updateDept(ConfirmDialog confirmDialog) {
        wrapper.get().setLeaderUserId(selectLeaderUser.get() != null ? selectLeaderUser.get().getId() : null);
        wrapper.commit();
        DeptSaveReqVO deptSaveReqVO = wrapper.get();

        Single
                .fromCallable(() -> Forest.client(DeptApi.class).updateDept(deptSaveReqVO).getCheckedData())
                .subscribeOn(Schedulers.io())
                .observeOn(FxSchedulers.fx())
                .compose(FxRx.bindTo(this))
                .subscribe(rel -> {
                    EventBusCenter.get().post(new MessageEvent("更新成功！", MessageType.SUCCESS));
                    EventBusCenter.get().post(new UpdateDataEvent("更新部门列表", deptSaveReqVO.getId()));
                    confirmDialog.close();
                }, DefaultExceptionHandler::handle);
    }

    public void initData(DeptRespVO sysDept) {

        DeptSaveReqVO saveVO = new DeptSaveReqVO();
        BeanUtil.copyProperties(sysDept, saveVO);
        wrapper.set(saveVO);

        Single
                /*
                 * 部门编辑页初始化依赖“负责人列表 + 部门树”两份数据，
                 * 这里直接用 zip 并行加载，再一次性回到 JavaFX UI 线程填充表单。
                 */
                .zip(
                        Single.fromCallable(() -> Forest.client(UserApi.class).getSimpleUserList().getCheckedData()),
                        Single.fromCallable(() -> Forest.client(DeptApi.class).getSimpleDeptList().getCheckedData()),
                        (userList, deptList) -> {
                            Map<String, Object> result = new HashMap<String, Object>();
                            result.put("users", userList);
                            result.put("depts", deptList);
                            return result;
                        }
                )
                .subscribeOn(Schedulers.io())
                .observeOn(FxSchedulers.fx())
                .compose(FxRx.bindTo(this))
                .subscribe(result -> {
            java.util.List<UserSimpleRespVO> userList = (java.util.List<UserSimpleRespVO>) result.get("users");
            java.util.List<DeptSimpleRespVO> deptList = (java.util.List<DeptSimpleRespVO>) result.get("depts");
            leaderUserList.set(FXCollections.observableList(userList));

            for (UserSimpleRespVO respVO : userList) {
                if (ObjectUtil.equals(respVO.getId(), sysDept.getLeaderUserId())) {
                    selectLeaderUser.set(respVO);
                }
            }

            DeptSimpleRespVO respVO = new DeptSimpleRespVO();
            respVO.setId(0L);
            respVO.setName("主类目");

            TreeItem<DeptSimpleRespVO> root = new TreeItem<>(respVO);
            root.setExpanded(true);
            // Build the tree
            Map<Long, TreeItem<DeptSimpleRespVO>> nodeMap = new HashMap<>();
            nodeMap.put(0l, root); // Root node
            for (DeptSimpleRespVO dept : deptList) {
                TreeItem<DeptSimpleRespVO> node = new TreeItem<>(dept);
                nodeMap.put(dept.getId(), node);
            }

            deptList.forEach(deptSimpleRespVO -> {
                TreeItem<DeptSimpleRespVO> parentNode = nodeMap.get(deptSimpleRespVO.getParentId());
                TreeItem<DeptSimpleRespVO> childNode = nodeMap.get(deptSimpleRespVO.getId());
                if (parentNode != null) {
                    parentNode.getChildren().add(childNode);
                } else {
                    root.getChildren().add(childNode);
                }
            });

            if (nodeMap.get(wrapper.get().getParentId()) != null) {
                selectTreeItem.setValue(nodeMap.get(wrapper.get().getParentId()));
            } else {
                selectTreeItem.setValue(root);
            }
            deptTreeRoot.set(root);

        }, DefaultExceptionHandler::handle);
    }

    /**
     * 系统设置部门
     *
     * @param sysDept 系统部门
     */
    public void setDeptSaveReqVO(DeptSaveReqVO sysDept) {
        wrapper.set(sysDept);
        wrapper.reload();
    }

    /**
     * 父id属性
     *
     * @return {@link LongProperty}
     */
    public LongProperty parentIdProperty() {
        return wrapper.field("parentId", DeptSaveReqVO::getParentId, DeptSaveReqVO::setParentId, 0L);
    }


    /**
     * 部门名称属性
     *
     * @return {@link StringProperty}
     */
    public StringProperty nameProperty() {
        return wrapper.field("name", DeptSaveReqVO::getName, DeptSaveReqVO::setName, "");
    }

    /**
     * 显示顺序属性
     *
     * @return {@link IntegerProperty}
     */
    public IntegerProperty sortProperty() {
        return wrapper.field("sort", DeptSaveReqVO::getSort, DeptSaveReqVO::setSort, 0);
    }


    /**
     * 权限字符串
     *
     * @return {@link StringProperty}
     */
    public StringProperty emailProperty() {
        return wrapper.field("email", DeptSaveReqVO::getEmail, DeptSaveReqVO::setEmail, "");
    }


    /**
     * 缓存属性
     *
     * @return {@link StringProperty}
     */
    public StringProperty phoneProperty() {
        return wrapper.field("phone", DeptSaveReqVO::getPhone, DeptSaveReqVO::setPhone);
    }

    /**
     * 可见属性
     *
     * @return {@link StringProperty}
     */
    public LongProperty leaderUserIdProperty() {
        return wrapper.field("leaderUserId", DeptSaveReqVO::getLeaderUserId, DeptSaveReqVO::setLeaderUserId);
    }

    /**
     * 可见属性
     *
     * @return {@link StringProperty}
     */
    public IntegerProperty statusProperty() {
        return wrapper.field("status", DeptSaveReqVO::getStatus, DeptSaveReqVO::setStatus);
    }


    public ObjectProperty<TreeItem<DeptSimpleRespVO>> deptTreeRootProperty() {
        return deptTreeRoot;
    }


    public ObjectProperty<TreeItem<DeptSimpleRespVO>> selectTreeItemProperty() {
        return selectTreeItem;
    }

    public ObservableList<UserSimpleRespVO> getLeaderUserList() {
        return leaderUserList.get();
    }

    public ObjectProperty<ObservableList<UserSimpleRespVO>> leaderUserListProperty() {
        return leaderUserList;
    }

    public UserSimpleRespVO getSelectLeaderUser() {
        return selectLeaderUser.get();
    }

    public ObjectProperty<UserSimpleRespVO> selectLeaderUserProperty() {
        return selectLeaderUser;
    }


}

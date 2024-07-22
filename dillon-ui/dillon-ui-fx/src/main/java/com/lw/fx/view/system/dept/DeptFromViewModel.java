package com.lw.fx.view.system.dept;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.lw.dillon.admin.framework.common.pojo.CommonResult;
import com.lw.dillon.admin.module.system.controller.admin.dept.vo.dept.DeptRespVO;
import com.lw.dillon.admin.module.system.controller.admin.dept.vo.dept.DeptSaveReqVO;
import com.lw.dillon.admin.module.system.controller.admin.dept.vo.dept.DeptSimpleRespVO;
import com.lw.dillon.admin.module.system.controller.admin.user.vo.user.UserSimpleRespVO;
import com.lw.fx.request.Request;
import com.lw.ui.request.api.system.DeptFeign;
import com.lw.ui.request.api.system.UserFeign;
import de.saxsys.mvvmfx.SceneLifecycle;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.mapping.ModelWrapper;
import io.datafx.core.concurrent.ProcessChain;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

import java.util.HashMap;
import java.util.Map;

/**
 * 部门对话框视图模型
 *
 * @author wenli
 * @date 2023/02/12
 */
public class DeptFromViewModel implements ViewModel, SceneLifecycle {
    public final static String ON_CLOSE = "close";

    private ObjectProperty<TreeItem<DeptSimpleRespVO>> deptTreeRoot = new SimpleObjectProperty<>();
    private ObjectProperty<TreeItem<DeptSimpleRespVO>> selectTreeItem = new SimpleObjectProperty<>();
    private ObjectProperty<UserSimpleRespVO> selectLeaderUser = new SimpleObjectProperty<>();
    private ObjectProperty<ObservableList<UserSimpleRespVO>> leaderUserList = new SimpleObjectProperty<>();
    /**
     * 包装器
     */
    private ModelWrapper<DeptSaveReqVO> wrapper = new ModelWrapper<>();


    public void initialize() {

    }


    /**
     * 保存
     *
     * @param isEdit 是编辑
     * @return {@link Boolean}
     */
    public CommonResult save(boolean isEdit) {
        wrapper.get().setLeaderUserId(selectLeaderUser.get().getId());
        wrapper.commit();
        if (isEdit) {
            return Request.connector(DeptFeign.class).updateDept(wrapper.get());
        } else {
            return Request.connector(DeptFeign.class).createDept(wrapper.get());
        }

    }

    public void updateData(DeptRespVO sysDept, boolean isAdd) {

        DeptSaveReqVO saveVO = new DeptSaveReqVO();
        BeanUtil.copyProperties(sysDept, saveVO);
        ProcessChain.create()
                .addRunnableInPlatformThread(() -> wrapper.set(saveVO))
                .addSupplierInExecutor(() -> Request.connector(UserFeign.class).getSimpleUserList())
                .addConsumerInPlatformThread(rel->{

                    leaderUserList.set(FXCollections.observableList(rel.getData()));

                    for (UserSimpleRespVO respVO : rel.getData()) {
                        if (ObjectUtil.equals(respVO.getId(),sysDept.getLeaderUserId())) {
                            selectLeaderUser.set(respVO);
                        }
                    }
                })
                .addSupplierInExecutor(() -> Request.connector(DeptFeign.class).getSimpleDeptList())
                .addConsumerInPlatformThread(listCommonResult -> {

                    DeptSimpleRespVO respVO = new DeptSimpleRespVO();
                    respVO.setId(0L);
                    respVO.setName("主类目");

                    TreeItem<DeptSimpleRespVO> root = new TreeItem<>(respVO);
                    root.setExpanded(true);
                    // Build the tree
                    Map<Long, TreeItem<DeptSimpleRespVO>> nodeMap = new HashMap<>();
                    nodeMap.put(0l, root); // Root node


                    if (listCommonResult.isSuccess()) {
                        for (DeptSimpleRespVO dept : listCommonResult.getData()) {


                            TreeItem<DeptSimpleRespVO> node = new TreeItem<DeptSimpleRespVO>(dept);
                            nodeMap.put(dept.getId(), node);

                        }

                        listCommonResult.getData().forEach(deptSimpleRespVO -> {

                            TreeItem<DeptSimpleRespVO> parentNode = nodeMap.get(deptSimpleRespVO.getParentId());
                            TreeItem<DeptSimpleRespVO> childNode = nodeMap.get(deptSimpleRespVO.getId());
                            if (parentNode != null) {
                                parentNode.getChildren().add(childNode);
                            }


                        });

                        if (nodeMap.get(wrapper.get().getParentId()) != null) {
                            selectTreeItem.setValue(nodeMap.get(wrapper.get().getParentId()));
                        } else {
                            selectTreeItem.setValue(root);
                        }
                        deptTreeRoot.set(root);

                    }
                })

                .run();

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

    @Override
    public void onViewAdded() {

    }

    @Override
    public void onViewRemoved() {

    }
}

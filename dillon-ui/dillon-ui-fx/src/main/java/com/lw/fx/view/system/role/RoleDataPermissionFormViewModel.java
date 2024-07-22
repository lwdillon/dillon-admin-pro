package com.lw.fx.view.system.role;

import com.lw.dillon.admin.framework.common.pojo.CommonResult;
import com.lw.dillon.admin.module.system.controller.admin.dept.vo.dept.DeptSimpleRespVO;
import com.lw.dillon.admin.module.system.controller.admin.dict.vo.data.DictDataSimpleRespVO;
import com.lw.dillon.admin.module.system.controller.admin.permission.vo.permission.PermissionAssignRoleDataScopeReqVO;
import com.lw.dillon.admin.module.system.controller.admin.permission.vo.role.RoleRespVO;
import com.lw.fx.request.Request;
import com.lw.fx.store.AppStore;
import com.lw.ui.request.api.system.DeptFeign;
import com.lw.ui.request.api.system.PermissionFeign;
import com.lw.ui.utils.DictTypeEnum;
import de.saxsys.mvvmfx.ViewModel;
import io.datafx.core.concurrent.ProcessChain;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.TreeItem;

import java.util.*;

public class RoleDataPermissionFormViewModel implements ViewModel {

    private StringProperty name = new SimpleStringProperty();
    private StringProperty code = new SimpleStringProperty();
    private LongProperty roleId = new SimpleLongProperty();
    private IntegerProperty dataScope = new SimpleIntegerProperty();

    private ObjectProperty<CheckBoxTreeItem<DeptSimpleRespVO>> deptTreeRoot = new SimpleObjectProperty<>();
    private ObservableList<DictDataSimpleRespVO> dataScopeList = FXCollections.observableArrayList();

    public void getSimpleDeptList(RoleRespVO roleRespVO) {

        ProcessChain.create().addRunnableInPlatformThread(() -> {
                    List<DictDataSimpleRespVO> list = AppStore.getDictDataList(DictTypeEnum.SYSTEM_DATA_SCOPE);
                    dataScopeList.addAll(list);
                    name.set(roleRespVO.getName());
                    code.set(roleRespVO.getCode());
                    roleId.set(roleRespVO.getId());
                    dataScope.set(roleRespVO.getDataScope());
                })

                .addSupplierInExecutor(() -> Request.connector(DeptFeign.class).getSimpleDeptList())
                .addConsumerInPlatformThread(listCommonResult -> {
                    DeptSimpleRespVO respVO = new DeptSimpleRespVO();
                    respVO.setId(0L);
                    respVO.setName("主类目");

                    CheckBoxTreeItem<DeptSimpleRespVO> root = new CheckBoxTreeItem<>(respVO);
                    root.setExpanded(true);
                    // Build the treeExpanded
                    Map<Long, CheckBoxTreeItem<DeptSimpleRespVO>> nodeMap = new HashMap<>();
                    nodeMap.put(0l, root); // Root node


                    if (listCommonResult.isSuccess()) {
                        for (DeptSimpleRespVO dept : listCommonResult.getData()) {

                            CheckBoxTreeItem<DeptSimpleRespVO> item = new CheckBoxTreeItem<DeptSimpleRespVO>(dept);
                            nodeMap.put(dept.getId(), item);

                        }

                        listCommonResult.getData().forEach(deptSimpleRespVO -> {
                            CheckBoxTreeItem<DeptSimpleRespVO> parentNode = nodeMap.get(deptSimpleRespVO.getParentId());
                            CheckBoxTreeItem<DeptSimpleRespVO> childNode = nodeMap.get(deptSimpleRespVO.getId());
                            if (parentNode != null) {
                                parentNode.getChildren().add(childNode);
                            }
                            if (dataScopeProperty().getValue() == 2) {
                                childNode.setSelected(roleRespVO.getDataScopeDeptIds().contains(deptSimpleRespVO.getId()));
                            }


                        });

                        deptTreeRoot.set(root);
                    }
                })
                .run();
    }

    public CommonResult<Boolean> assignRoleDataScope() {
        PermissionAssignRoleDataScopeReqVO permissionAssignRoleDataScopeReqVO = new PermissionAssignRoleDataScopeReqVO();
        permissionAssignRoleDataScopeReqVO.setRoleId(roleId.get());
        permissionAssignRoleDataScopeReqVO.setDataScope(dataScope.get());
        Set<Long> selMenuIds = new HashSet<>();
        if (dataScope.getValue() == 2) {
            findSelectedItems(deptTreeRoot.get(), selMenuIds);

        }
        permissionAssignRoleDataScopeReqVO.setDataScopeDeptIds(selMenuIds);

        return Request.connector(PermissionFeign.class).assignRoleDataScope(permissionAssignRoleDataScopeReqVO);
    }

    private void findSelectedItems(CheckBoxTreeItem<DeptSimpleRespVO> item, Set<Long> selMenuIds) {
        if (item.isSelected()) {
            selMenuIds.add(item.getValue().getId());
        }

        for (TreeItem<DeptSimpleRespVO> child : item.getChildren()) {
            findSelectedItems((CheckBoxTreeItem<DeptSimpleRespVO>) child, selMenuIds);
        }
    }

    public CheckBoxTreeItem<DeptSimpleRespVO> getDeptTreeRoot() {
        return deptTreeRoot.get();
    }

    public ObjectProperty<CheckBoxTreeItem<DeptSimpleRespVO>> deptTreeRootProperty() {
        return deptTreeRoot;
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public String getCode() {
        return code.get();
    }

    public StringProperty codeProperty() {
        return code;
    }

    public long getRoleId() {
        return roleId.get();
    }

    public LongProperty roleIdProperty() {
        return roleId;
    }

    public ObservableList<DictDataSimpleRespVO> getDataScopeList() {
        return dataScopeList;
    }

    public int getDataScope() {
        return dataScope.get();
    }

    public IntegerProperty dataScopeProperty() {
        return dataScope;
    }
}

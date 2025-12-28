package com.dillon.lw.fx.view.system.role;

import com.dillon.lw.module.system.controller.admin.dept.vo.dept.DeptSimpleRespVO;
import com.dillon.lw.module.system.controller.admin.dict.vo.data.DictDataSimpleRespVO;
import com.dillon.lw.module.system.controller.admin.permission.vo.permission.PermissionAssignRoleDataScopeReqVO;
import com.dillon.lw.module.system.controller.admin.permission.vo.role.RoleRespVO;
import com.dillon.lw.api.system.DeptApi;
import com.dillon.lw.api.system.PermissionApi;
import com.dillon.lw.utils.DictTypeEnum;
import com.dillon.lw.fx.eventbus.EventBusCenter;
import com.dillon.lw.fx.eventbus.event.MessageEvent;
import com.dillon.lw.fx.eventbus.event.UpdateDataEvent;
import com.dillon.lw.fx.http.PayLoad;
import com.dillon.lw.fx.mvvm.base.BaseViewModel;
import com.dillon.lw.fx.store.AppStore;
import com.dillon.lw.fx.utils.MessageType;
import com.dillon.lw.fx.view.layout.ConfirmDialog;
import com.dtflys.forest.Forest;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.TreeItem;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class RoleDataPermissionFormViewModel extends BaseViewModel {

    private StringProperty name = new SimpleStringProperty();
    private StringProperty code = new SimpleStringProperty();
    private LongProperty roleId = new SimpleLongProperty();
    private IntegerProperty dataScope = new SimpleIntegerProperty();

    private ObjectProperty<CheckBoxTreeItem<DeptSimpleRespVO>> deptTreeRoot = new SimpleObjectProperty<>();
    private ObservableList<DictDataSimpleRespVO> dataScopeList = FXCollections.observableArrayList();

    public void getSimpleDeptList(RoleRespVO roleRespVO) {
        List<DictDataSimpleRespVO> list = AppStore.getDictDataList(DictTypeEnum.SYSTEM_DATA_SCOPE);
        dataScopeList.addAll(list);
        name.set(roleRespVO.getName());
        code.set(roleRespVO.getCode());
        roleId.set(roleRespVO.getId());
        dataScope.set(roleRespVO.getDataScope());

        CompletableFuture.supplyAsync(() -> {
            return new PayLoad<List<DeptSimpleRespVO>>().apply(Forest.client(DeptApi.class).getSimpleDeptList());
        }).thenAcceptAsync(data -> {
            DeptSimpleRespVO respVO = new DeptSimpleRespVO();
            respVO.setId(0L);
            respVO.setName("主类目");

            CheckBoxTreeItem<DeptSimpleRespVO> root = new CheckBoxTreeItem<>(respVO);
            root.setExpanded(true);
            // Build the tree
            Map<Long, CheckBoxTreeItem<DeptSimpleRespVO>> nodeMap = new HashMap<>();
            nodeMap.put(0l, root); // Root node

            for (DeptSimpleRespVO dept : data) {
                CheckBoxTreeItem<DeptSimpleRespVO> item = new CheckBoxTreeItem<>(dept);
                nodeMap.put(dept.getId(), item);
            }

            data.forEach(deptSimpleRespVO -> {
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
        }, Platform::runLater).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });
    }

    public void assignRoleDataScope(ConfirmDialog confirmDialog) {
        PermissionAssignRoleDataScopeReqVO permissionAssignRoleDataScopeReqVO = new PermissionAssignRoleDataScopeReqVO();
        permissionAssignRoleDataScopeReqVO.setRoleId(roleId.get());
        permissionAssignRoleDataScopeReqVO.setDataScope(dataScope.get());
        Set<Long> selMenuIds = new HashSet<>();
        if (dataScope.getValue() == 2) {
            findSelectedItems(deptTreeRoot.get(), selMenuIds);

        }
        permissionAssignRoleDataScopeReqVO.setDataScopeDeptIds(selMenuIds);

        CompletableFuture.supplyAsync(() -> {
            return new PayLoad<Boolean>().apply(Forest.client(PermissionApi.class).assignRoleDataScope(permissionAssignRoleDataScopeReqVO));
        }).thenAcceptAsync(data -> {
            confirmDialog.close();
            EventBusCenter.get().post(new UpdateDataEvent("更新角色列表"));
            EventBusCenter.get().post(new MessageEvent("操作成功", MessageType.SUCCESS));
        }, Platform::runLater).exceptionally(e -> {
            e.printStackTrace();
            return null;
        });

    }


    private void findSelectedItems(CheckBoxTreeItem<DeptSimpleRespVO> item, Set<Long> selMenuIds) {
        if (item.isSelected() || item.isIndeterminate()) {
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

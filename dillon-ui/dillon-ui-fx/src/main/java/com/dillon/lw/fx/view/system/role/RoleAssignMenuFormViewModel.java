package com.dillon.lw.fx.view.system.role;

import com.dillon.lw.api.system.MenuApi;
import com.dillon.lw.api.system.PermissionApi;
import com.dillon.lw.fx.DefaultExceptionHandler;
import com.dillon.lw.fx.eventbus.EventBusCenter;
import com.dillon.lw.fx.eventbus.event.MessageEvent;
import com.dillon.lw.fx.eventbus.event.UpdateDataEvent;
import com.dillon.lw.fx.mvvm.base.BaseViewModel;
import com.dillon.lw.fx.rx.FxSchedulers;
import com.dillon.lw.fx.rx.FxRx;
import com.dillon.lw.fx.utils.MessageType;
import com.dillon.lw.fx.view.layout.ConfirmDialog;
import com.dillon.lw.module.system.controller.admin.permission.vo.menu.MenuSimpleRespVO;
import com.dillon.lw.module.system.controller.admin.permission.vo.permission.PermissionAssignRoleMenuReqVO;
import com.dillon.lw.module.system.controller.admin.permission.vo.role.RoleRespVO;
import com.dtflys.forest.Forest;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.TreeItem;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RoleAssignMenuFormViewModel extends BaseViewModel {
    private StringProperty name = new SimpleStringProperty();
    private StringProperty code = new SimpleStringProperty();
    private LongProperty roleId = new SimpleLongProperty();
    private ObservableSet<Long> menuIds = FXCollections.observableSet();
    private ObjectProperty<CheckBoxTreeItem<MenuSimpleRespVO>> menuTreeRoot = new SimpleObjectProperty<>();


    public void getSimpleMenuList(RoleRespVO roleRespVO) {
        name.set(roleRespVO.getName());
        code.set(roleRespVO.getCode());
        roleId.set(roleRespVO.getId());

        Single
                /*
                 * 角色已有菜单和完整菜单树互不依赖，直接并行加载，
                 * 最后统一回到 JavaFX UI 线程构建勾选树。
                 */
                .zip(
                        Single.fromCallable(() -> Forest.client(PermissionApi.class).getRoleMenuList(roleRespVO.getId()).getCheckedData()),
                        Single.fromCallable(() -> Forest.client(MenuApi.class).getSimpleMenuList().getCheckedData()),
                        (assignedMenuIds, menuList) -> {
                            Map<String, Object> result = new HashMap<String, Object>();
                            result.put("menuIds", assignedMenuIds);
                            result.put("menuList", menuList);
                            return result;
                        }
                )
                .subscribeOn(Schedulers.io())
                .observeOn(FxSchedulers.fx())
                .compose(FxRx.bindTo(this))
                .subscribe(result -> {
            menuIds.clear();
            menuIds.addAll((java.util.Set<Long>) result.get("menuIds"));
            java.util.List<MenuSimpleRespVO> menuList = (java.util.List<MenuSimpleRespVO>) result.get("menuList");
            MenuSimpleRespVO respVO = new MenuSimpleRespVO();
            respVO.setId(0L);
            respVO.setName("主类目");

            CheckBoxTreeItem<MenuSimpleRespVO> root = new CheckBoxTreeItem<>(respVO);
            root.setExpanded(true);
            // Build the treeExpanded
            Map<Long, CheckBoxTreeItem<MenuSimpleRespVO>> nodeMap = new HashMap<>();
            nodeMap.put(0l, root); // Root node


            for (MenuSimpleRespVO menu : menuList) {

                CheckBoxTreeItem<MenuSimpleRespVO> item = new CheckBoxTreeItem<>(menu);
                nodeMap.put(menu.getId(), item);

            }

            menuList.forEach(menuSimpleRespVO -> {
                CheckBoxTreeItem<MenuSimpleRespVO> parentNode = nodeMap.get(menuSimpleRespVO.getParentId());
                CheckBoxTreeItem<MenuSimpleRespVO> childNode = nodeMap.get(menuSimpleRespVO.getId());
                if (parentNode != null) {
                    parentNode.getChildren().add(childNode);
                }
                childNode.setSelected(menuIds.contains(menuSimpleRespVO.getId()));

            });

            menuTreeRoot.set(root);

        }, DefaultExceptionHandler::handle);

    }

    public void assignRoleMenu(ConfirmDialog confirmDialog) {
        PermissionAssignRoleMenuReqVO permissionAssignRoleMenuReqVO = new PermissionAssignRoleMenuReqVO();
        permissionAssignRoleMenuReqVO.setRoleId(roleId.get());

        Set<Long> selMenuIds = new HashSet<>();
        findSelectedItems(menuTreeRoot.get(), selMenuIds);
        permissionAssignRoleMenuReqVO.setMenuIds(selMenuIds);

        Single
                .fromCallable(() -> Forest.client(PermissionApi.class).assignRoleMenu(permissionAssignRoleMenuReqVO).getCheckedData())
                .subscribeOn(Schedulers.io())
                .observeOn(FxSchedulers.fx())
                .compose(FxRx.bindTo(this))
                .subscribe(data -> {
                    confirmDialog.close();
                    EventBusCenter.get().post(new UpdateDataEvent("更新角色列表"));
                    EventBusCenter.get().post(new MessageEvent("分配成功", MessageType.SUCCESS));
                }, DefaultExceptionHandler::handle);
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

    public CheckBoxTreeItem<MenuSimpleRespVO> getMenuTreeRoot() {
        return menuTreeRoot.get();
    }

    public ObjectProperty<CheckBoxTreeItem<MenuSimpleRespVO>> menuTreeRootProperty() {
        return menuTreeRoot;
    }

    public ObservableSet<Long> getMenuIds() {
        return menuIds;
    }

    private void findSelectedItems(CheckBoxTreeItem<MenuSimpleRespVO> item, Set<Long> selMenuIds) {
        if (item.isSelected() || item.isIndeterminate()) {
            selMenuIds.add(item.getValue().getId());
        }

        for (TreeItem<MenuSimpleRespVO> child : item.getChildren()) {
            findSelectedItems((CheckBoxTreeItem<MenuSimpleRespVO>) child, selMenuIds);
        }
    }


}

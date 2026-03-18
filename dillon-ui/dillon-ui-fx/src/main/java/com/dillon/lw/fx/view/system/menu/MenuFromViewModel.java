package com.dillon.lw.fx.view.system.menu;

import cn.hutool.core.bean.BeanUtil;
import com.dillon.lw.api.system.MenuApi;
import com.dillon.lw.fx.DefaultExceptionHandler;
import com.dillon.lw.fx.eventbus.EventBusCenter;
import com.dillon.lw.fx.eventbus.event.MessageEvent;
import com.dillon.lw.fx.eventbus.event.SideMenuEvent;
import com.dillon.lw.fx.eventbus.event.UpdateDataEvent;
import com.dillon.lw.fx.mvvm.base.BaseViewModel;
import com.dillon.lw.fx.mvvm.mapping.ModelWrapper;
import com.dillon.lw.fx.rx.FxSchedulers;
import com.dillon.lw.fx.rx.FxRx;
import com.dillon.lw.fx.utils.MessageType;
import com.dillon.lw.fx.view.layout.ConfirmDialog;
import com.dillon.lw.module.system.controller.admin.permission.vo.menu.MenuRespVO;
import com.dillon.lw.module.system.controller.admin.permission.vo.menu.MenuSaveVO;
import com.dillon.lw.module.system.controller.admin.permission.vo.menu.MenuSimpleRespVO;
import com.dtflys.forest.Forest;
import javafx.beans.property.*;
import javafx.scene.control.TreeItem;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.util.HashMap;
import java.util.Map;

/**
 * 菜单对话框视图模型
 *
 * @author wenli
 * @date 2023/02/12
 */
public class MenuFromViewModel extends BaseViewModel {
    public final static String ON_CLOSE = "close";

    private ObjectProperty<TreeItem<MenuSimpleRespVO>> menuTreeRoot = new SimpleObjectProperty<>();
    private ObjectProperty<TreeItem<MenuSimpleRespVO>> selectTreeItem = new SimpleObjectProperty<>();
    private ModelWrapper<MenuSaveVO> wrapper = new ModelWrapper<>();


    public MenuFromViewModel() {
        sortProperty().set(-1);
    }


    /**
     * 更新菜单
     *
     * @return {@link Boolean}
     */
    public void updateMenu(ConfirmDialog dialog) {
        wrapper.commit();
        MenuSaveVO menuSaveVO = wrapper.get();
        Single
                .fromCallable(() -> Forest.client(MenuApi.class).updateMenu(menuSaveVO).getCheckedData())
                .subscribeOn(Schedulers.io())
                .observeOn(FxSchedulers.fx())
                .compose(FxRx.bindTo(this))
                .subscribe(result -> {
                    EventBusCenter.get().post(new UpdateDataEvent("更新菜单列表", menuSaveVO.getId()));
                    EventBusCenter.get().post(new SideMenuEvent("更新菜单"));
                    EventBusCenter.get().post(new MessageEvent("修改成功", MessageType.SUCCESS));
                    dialog.close();
                }, DefaultExceptionHandler::handle);

    }

    public void createMenu(ConfirmDialog dialog) {
        wrapper.commit();
        MenuSaveVO menuSaveVO = wrapper.get();
        Single
                .fromCallable(() -> Forest.client(MenuApi.class).createMenu(menuSaveVO).getCheckedData())
                .subscribeOn(Schedulers.io())
                .observeOn(FxSchedulers.fx())
                .compose(FxRx.bindTo(this))
                .subscribe(menuId -> {
                    EventBusCenter.get().post(new UpdateDataEvent("更新菜单列表", menuId));
                    EventBusCenter.get().post(new SideMenuEvent("更新菜单"));
                    EventBusCenter.get().post(new MessageEvent("添加成功", MessageType.SUCCESS));
                    dialog.close();
                }, DefaultExceptionHandler::handle);

    }

    public void updateData(MenuRespVO sysMenu) {


        MenuSaveVO saveVO = new MenuSaveVO();
        BeanUtil.copyProperties(sysMenu, saveVO);
        setMenuRespVO(saveVO);

        Single
                .fromCallable(() -> Forest.client(MenuApi.class).getSimpleMenuList().getCheckedData())
                .subscribeOn(Schedulers.io())
                .observeOn(FxSchedulers.fx())
                .compose(FxRx.bindTo(this))
                .subscribe(listCommonResult -> {

            MenuSimpleRespVO respVO = new MenuSimpleRespVO();
            respVO.setId(0L);
            respVO.setName("主类目");

            TreeItem<MenuSimpleRespVO> root = new TreeItem<>(respVO);
            root.setExpanded(true);
            // Build the tree
            Map<Long, TreeItem<MenuSimpleRespVO>> nodeMap = new HashMap<>();
            nodeMap.put(0L, root); // Root node

            for (MenuSimpleRespVO menu : listCommonResult) {
                if (menu.getType() == 3) {
                    continue;
                }

                TreeItem<MenuSimpleRespVO> node = new TreeItem<MenuSimpleRespVO>(menu);
                nodeMap.put(menu.getId(), node);

            }

            listCommonResult.forEach(menuSimpleRespVO -> {
                if (menuSimpleRespVO.getType() != 3) {
                    TreeItem<MenuSimpleRespVO> parentNode = nodeMap.get(menuSimpleRespVO.getParentId());
                    TreeItem<MenuSimpleRespVO> childNode = nodeMap.get(menuSimpleRespVO.getId());
                    if (parentNode != null) {
                        parentNode.getChildren().add(childNode);
                    }
                }

            });

            menuTreeRoot.set(root);
            if (nodeMap.get(sysMenu.getParentId()) != null) {
                selectTreeItem.set(nodeMap.get(sysMenu.getParentId()));
            } else {
                selectTreeItem.set(root);
            }


        }, throwable -> {
            DefaultExceptionHandler.handle(throwable);
            setMenuRespVO(new MenuSaveVO());
        });


    }

    /**
     * 系统设置菜单
     *
     * @param sysMenu 系统菜单
     */
    public void setMenuRespVO(MenuSaveVO sysMenu) {
        wrapper.set(sysMenu);
        wrapper.reload();
    }


    public ObjectProperty<TreeItem<MenuSimpleRespVO>> menuTreeRootProperty() {
        return menuTreeRoot;
    }

    public ObjectProperty<TreeItem<MenuSimpleRespVO>> selectTreeItemProperty() {
        return selectTreeItem;
    }

    public IntegerProperty sortProperty() {
        return wrapper.field("sort", MenuSaveVO::getSort, MenuSaveVO::setSort, 0);
    }

    public StringProperty nameProperty() {
        return wrapper.field("name", MenuSaveVO::getName, MenuSaveVO::setName, "");
    }


    public StringProperty iconProperty() {
        return wrapper.field("icon", MenuSaveVO::getIcon, MenuSaveVO::setIcon, "");
    }

    public StringProperty pathProperty() {
        return wrapper.field("path", MenuSaveVO::getPath, MenuSaveVO::setPath, "");
    }

    public StringProperty componentProperty() {
        return wrapper.field("component", MenuSaveVO::getComponent, MenuSaveVO::setComponent, "");
    }

    public StringProperty componentNameProperty() {
        return wrapper.field("componentName", MenuSaveVO::getComponentName, MenuSaveVO::setComponent, "");
    }

    public IntegerProperty statusProperty() {
        return wrapper.field("status", MenuSaveVO::getStatus, MenuSaveVO::setStatus, 0);
    }

    public IntegerProperty typeProperty() {
        return wrapper.field("type", MenuSaveVO::getType, MenuSaveVO::setType, 0);
    }

    public LongProperty parentIdProperty() {
        return wrapper.field("parentId", MenuSaveVO::getParentId, MenuSaveVO::setParentId, 0L);
    }

    public StringProperty permissionProperty() {
        return wrapper.field("permission", MenuSaveVO::getPermission, MenuSaveVO::setPermission, "");
    }

    public BooleanProperty visibleProperty() {
        return wrapper.field("visible", MenuSaveVO::getVisible, MenuSaveVO::setVisible, false);
    }

    public BooleanProperty alwaysShowProperty() {
        return wrapper.field("alwaysShow", MenuSaveVO::getAlwaysShow, MenuSaveVO::setAlwaysShow, false);
    }

    public BooleanProperty keepAliveProperty() {
        return wrapper.field("keepAlive", MenuSaveVO::getKeepAlive, MenuSaveVO::setKeepAlive, false);
    }


}

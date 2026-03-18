package com.dillon.lw.fx.view.system.menu;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.dillon.lw.api.system.MenuApi;
import com.dillon.lw.fx.DefaultExceptionHandler;
import com.dillon.lw.fx.eventbus.EventBusCenter;
import com.dillon.lw.fx.eventbus.event.MessageEvent;
import com.dillon.lw.fx.eventbus.event.RefreshEvent;
import com.dillon.lw.fx.eventbus.event.SideMenuEvent;
import com.dillon.lw.fx.eventbus.event.UpdateDataEvent;
import com.dillon.lw.fx.mvvm.base.BaseViewModel;
import com.dillon.lw.fx.rx.FxSchedulers;
import com.dillon.lw.fx.rx.FxRx;
import com.dillon.lw.fx.utils.MessageType;
import com.dillon.lw.fx.view.layout.ConfirmDialog;
import com.dillon.lw.module.system.controller.admin.permission.vo.menu.MenuListReqVO;
import com.dillon.lw.module.system.controller.admin.permission.vo.menu.MenuRespVO;
import com.dtflys.forest.Forest;
import com.google.common.eventbus.Subscribe;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TreeItem;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.util.HashMap;
import java.util.Map;


/**
 * 菜单管理视图模型
 *
 * @author wenli
 * @date 2023/02/15
 */
public class MenuManageViewModel extends BaseViewModel {
    public final static String OPEN_ALERT = "OPEN_ALERT";

    private ObjectProperty<TreeItem<MenuRespVO>> treeItemObjectProperty = new SimpleObjectProperty<>(new TreeItem<>());
    private ObjectProperty<TreeItem<MenuRespVO>> selectedTreeItemProperty = new SimpleObjectProperty<>();
    private SimpleStringProperty searchText = new SimpleStringProperty("");
    private SimpleStringProperty statusText = new SimpleStringProperty("全部");
    private Long pendingSelectMenuId;


    public String getSearchText() {
        return searchText.get();
    }

    public SimpleStringProperty searchTextProperty() {
        return searchText;
    }

    public String getStatusText() {
        return statusText.get();
    }

    public SimpleStringProperty statusTextProperty() {
        return statusText;
    }

    public void initialize() {

    }

    @Override
    public void dispose() {
        super.dispose();
    }

    public ObjectProperty<TreeItem<MenuRespVO>> treeItemObjectPropertyProperty() {
        return treeItemObjectProperty;
    }

    public ObjectProperty<TreeItem<MenuRespVO>> selectedTreeItemProperty() {
        return selectedTreeItemProperty;
    }

    /**
     * 获取菜单列表
     */

    public void query() {
        treeItemObjectProperty.setValue(null);
        String status = statusText.getValue();
        MenuListReqVO menuListReqVO = new MenuListReqVO();
        menuListReqVO.setName(searchText.getValue());
        menuListReqVO.setStatus(StrUtil.equals("全部", status) ? null : (StrUtil.equals("正常", status) ? 0 : 1));
        Map<String, Object> queryMap = BeanUtil.beanToMap(menuListReqVO, false, true);

        Single
                /*
                 * 菜单列表查询先在 IO 线程获取完整菜单树数据，
                 * 再回到 JavaFX UI 线程构建 TreeItem，避免界面线程阻塞。
                 */
                .fromCallable(() -> Forest.client(MenuApi.class).getMenuList(queryMap).getCheckedData())
                .subscribeOn(Schedulers.io())
                .observeOn(FxSchedulers.fx())
                .compose(FxRx.bindTo(this))
                .subscribe(data -> {
                    TreeItem<MenuRespVO> rootItem = new TreeItem<>(new MenuRespVO());
                    rootItem.setExpanded(true);
                    Map<Long, TreeItem<MenuRespVO>> itemMap = new HashMap<>();
                    itemMap.put(0L, rootItem);

                    for (MenuRespVO menu : data) {
                        TreeItem<MenuRespVO> treeItem = new TreeItem<>(menu);
                        itemMap.put(menu.getId(), treeItem);
                    }

                    data.forEach(menuRespVO -> {
                        TreeItem<MenuRespVO> parentNode = itemMap.get(menuRespVO.getParentId());
                        TreeItem<MenuRespVO> childNode = itemMap.get(menuRespVO.getId());
                        if (parentNode != null) {
                            parentNode.getChildren().add(childNode);
                        }
                    });

                    treeItemObjectProperty.setValue(rootItem);
                    if (pendingSelectMenuId != null) {
                        TreeItem<MenuRespVO> target = itemMap.get(pendingSelectMenuId);
                        if (target != null) {
                            expandToRoot(target);
                            selectedTreeItemProperty.set(target);
                        }
                        pendingSelectMenuId = null;
                    }
                }, DefaultExceptionHandler::handle);


    }


    public void rest() {
        searchTextProperty().setValue("");
        statusText.setValue("全部");
        EventBusCenter.get().post(new UpdateDataEvent("更新菜单列表"));
    }

    public void deleteMenu(Long menuId, ConfirmDialog dialog) {
        Single
                /*
                 * 删除菜单后需要立刻刷新侧边菜单和当前列表，
                 * 所以成功回调统一切回 JavaFX UI 线程处理事件派发与弹窗关闭。
                 */
                .fromCallable(() -> Forest.client(MenuApi.class).deleteMenu(menuId).getCheckedData())
                .subscribeOn(Schedulers.io())
                .observeOn(FxSchedulers.fx())
                .compose(FxRx.bindTo(this))
                .subscribe(commonResult -> {
                    EventBusCenter.get().post(new SideMenuEvent("更新菜单"));
                    EventBusCenter.get().post(new MessageEvent("删除成功", MessageType.SUCCESS));
                    EventBusCenter.get().post(new UpdateDataEvent("更新菜单列表"));
                    dialog.close();
                }, DefaultExceptionHandler::handle);
    }

    @Subscribe
    private void updateData(UpdateDataEvent menuEvent) {
        FxSchedulers.runOnFx(() -> {
            if ("更新菜单列表".equals(menuEvent.getMessage())) {
                Object data = menuEvent.getData();
                if (data instanceof Number) {
                    pendingSelectMenuId = ((Number) data).longValue();
                }
                query();
            }
        });
    }

    @Subscribe
    private void refresh(RefreshEvent event) {
        FxSchedulers.runOnFx(() -> query());
    }

    private void expandToRoot(TreeItem<MenuRespVO> node) {
        TreeItem<MenuRespVO> current = node;
        while (current != null) {
            current.setExpanded(true);
            current = current.getParent();
        }
    }


}

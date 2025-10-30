package com.dillon.lw.fx.view.system.menu;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.dillon.lw.module.system.controller.admin.permission.vo.menu.MenuListReqVO;
import com.dillon.lw.module.system.controller.admin.permission.vo.menu.MenuRespVO;
import com.google.common.eventbus.Subscribe;
import com.dillon.lw.api.system.MenuApi;
import com.dillon.lw.fx.eventbus.EventBusCenter;
import com.dillon.lw.fx.eventbus.event.MessageEvent;
import com.dillon.lw.fx.eventbus.event.RefreshEvent;
import com.dillon.lw.fx.eventbus.event.SideMenuEvent;
import com.dillon.lw.fx.eventbus.event.UpdateDataEvent;
import com.dillon.lw.fx.http.PayLoad;
import com.dillon.lw.fx.http.Request;
import com.dillon.lw.fx.mvvm.base.BaseViewModel;
import com.dillon.lw.fx.utils.MessageType;
import com.dillon.lw.fx.view.layout.ConfirmDialog;
import io.reactivex.rxjava3.schedulers.Schedulers;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TreeItem;

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
    private SimpleStringProperty searchText = new SimpleStringProperty("");
    private SimpleStringProperty statusText = new SimpleStringProperty("全部");


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

        Request.getInstance().create(MenuApi.class).getMenuList(queryMap)
                .subscribeOn(Schedulers.io())
                .map(new PayLoad<>())
                .observeOn(Schedulers.from(Platform::runLater)).subscribe(data -> {

                    // 创建根节点（空的或具有实际数据）
                    TreeItem<MenuRespVO> rootItem = new TreeItem<>(new MenuRespVO());
                    rootItem.setExpanded(true);
                    // 将菜单列表转换为树形结构
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

                }, throwable -> {
                    throwable.printStackTrace();
                });


    }


    public void rest() {
        searchTextProperty().setValue("");
        statusText.setValue("全部");
        EventBusCenter.get().post(new UpdateDataEvent("更新菜单列表"));
    }

    public void deleteMenu(Long menuId, ConfirmDialog dialog) {
        Request.getInstance().create(MenuApi.class).deleteMenu(menuId)
                .subscribeOn(Schedulers.io())
                .map(new PayLoad<>())
                .observeOn(Schedulers.from(Platform::runLater))
                .subscribe(commonResult -> {
                    EventBusCenter.get().post(new SideMenuEvent("更新菜单"));
                    EventBusCenter.get().post(new MessageEvent("删除成功", MessageType.SUCCESS));
                    EventBusCenter.get().post(new UpdateDataEvent("更新菜单列表"));
                    dialog.close();

                }, throwable -> {
                    throwable.printStackTrace();
                });
    }

    @Subscribe
    private void updateData(UpdateDataEvent menuEvent) {
        Platform.runLater(() -> {
            if("更新菜单列表".equals(menuEvent.getMessage())) {
                query();
            }
        });
    }

    @Subscribe
    private void refresh(RefreshEvent event) {
        Platform.runLater(() -> query());
    }




}

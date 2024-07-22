package com.lw.fx.view.system.menu;

import cn.hutool.core.util.StrUtil;
import com.lw.dillon.admin.framework.common.pojo.CommonResult;
import com.lw.dillon.admin.module.system.controller.admin.permission.vo.menu.MenuListReqVO;
import com.lw.dillon.admin.module.system.controller.admin.permission.vo.menu.MenuRespVO;
import com.lw.fx.request.Request;
import com.lw.ui.request.api.system.MenuFeign;
import de.saxsys.mvvmfx.SceneLifecycle;
import de.saxsys.mvvmfx.ViewModel;
import io.datafx.core.concurrent.ProcessChain;
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
public class MenuManageViewModel implements ViewModel, SceneLifecycle {
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

    public void someAction() {
        publish(OPEN_ALERT, "Some Error has happend");
    }


    @Override
    public void onViewAdded() {
        System.err.println("------add");
    }


    @Override
    public void onViewRemoved() {
        unsubscribe(OPEN_ALERT, (s, objects) -> {
        });
    }


    public ObjectProperty<TreeItem<MenuRespVO>> treeItemObjectPropertyProperty() {
        return treeItemObjectProperty;
    }

    /**
     * 获取菜单列表
     */

    public void query() {
        String status = statusText.getValue();
        MenuListReqVO menuListReqVO = new MenuListReqVO();
        menuListReqVO.setName(searchText.getValue());
        menuListReqVO.setStatus(StrUtil.equals("全部", status) ? null : (StrUtil.equals("正常", status) ? 0 : 1));

        ProcessChain.create()
                .addSupplierInExecutor(() -> {
                    return Request.connector(MenuFeign.class).getMenuList(menuListReqVO);
                })
                .addConsumerInPlatformThread(r -> {

                    if (r.isSuccess()) {
                        // 创建根节点（空的或具有实际数据）
                        TreeItem<MenuRespVO> rootItem = new TreeItem<>(new MenuRespVO());
                        rootItem.setExpanded(true);
                        // 将菜单列表转换为树形结构
                        Map<Long, TreeItem<MenuRespVO>> itemMap = new HashMap<>();
                        itemMap.put(0L, rootItem);

                        for (MenuRespVO menu : r.getData()) {
                            TreeItem<MenuRespVO> treeItem = new TreeItem<>(menu);
                            itemMap.put(menu.getId(), treeItem);

                        }

                        r.getData().forEach(menuRespVO -> {
                            TreeItem<MenuRespVO> parentNode = itemMap.get(menuRespVO.getParentId());
                            TreeItem<MenuRespVO> childNode = itemMap.get(menuRespVO.getId());
                            if (parentNode != null) {
                                parentNode.getChildren().add(childNode);
                            }


                        });


                        treeItemObjectProperty.set(rootItem);
                    } else {

                    }
                })
                .onException(e -> {
                    e.printStackTrace();
                })
                .withFinal(() -> {
                })
                .run();

    }


    public CommonResult<Boolean> remove(Long menuId) {
       return Request.connector(MenuFeign.class).deleteMenu(menuId);
    }

    public void rest() {
        searchTextProperty().setValue("");
        statusText.setValue("全部");
    }


}

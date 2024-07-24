package com.lw.fx.view.system.menu;

import cn.hutool.core.bean.BeanUtil;
import com.lw.dillon.admin.framework.common.pojo.CommonResult;
import com.lw.dillon.admin.module.system.controller.admin.permission.vo.menu.MenuRespVO;
import com.lw.dillon.admin.module.system.controller.admin.permission.vo.menu.MenuSaveVO;
import com.lw.dillon.admin.module.system.controller.admin.permission.vo.menu.MenuSimpleRespVO;
import com.lw.fx.request.Request;
import com.lw.ui.request.api.system.MenuFeign;
import de.saxsys.mvvmfx.SceneLifecycle;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.mapping.ModelWrapper;
import io.datafx.core.concurrent.ProcessChain;
import javafx.beans.property.*;
import javafx.scene.control.TreeItem;

import java.util.HashMap;
import java.util.Map;

/**
 * 菜单对话框视图模型
 *
 * @author wenli
 * @date 2023/02/12
 */
public class MenuFromViewModel implements ViewModel, SceneLifecycle {
    public final static String ON_CLOSE = "close";

    private ObjectProperty<TreeItem<MenuSimpleRespVO>> menuTreeRoot = new SimpleObjectProperty<>();
    private ObjectProperty<TreeItem<MenuSimpleRespVO>> selectTreeItem = new SimpleObjectProperty<>();
    /**
     * 包装器
     */
    private ModelWrapper<MenuSaveVO> wrapper = new ModelWrapper<>();


    public void initialize() {
        sortProperty().set(-1);
    }


    /**
     * 保存
     *
     * @param isEdit 是编辑
     * @return {@link Boolean}
     */
    public CommonResult save(boolean isEdit) {
        wrapper.commit();
        if (isEdit) {
            return Request.connector(MenuFeign.class).updateMenu(wrapper.get());
        } else {
            return Request.connector(MenuFeign.class).createMenu(wrapper.get());
        }

    }

    public void updateData(MenuRespVO sysMenu, boolean isAdd) {

        MenuSaveVO saveVO = new MenuSaveVO();
        BeanUtil.copyProperties(sysMenu, saveVO);
        ProcessChain.create()
                .addRunnableInPlatformThread(() -> wrapper.set(saveVO))
                .addSupplierInExecutor(() -> Request.connector(MenuFeign.class).getSimpleMenuList())
                .addConsumerInPlatformThread(listCommonResult -> {

                    MenuSimpleRespVO respVO = new MenuSimpleRespVO();
                    respVO.setId(0L);
                    respVO.setName("主类目");

                    TreeItem<MenuSimpleRespVO> root = new TreeItem<>(respVO);
                    root.setExpanded(true);
                    // Build the tree
                    Map<Long, TreeItem<MenuSimpleRespVO>> nodeMap = new HashMap<>();
                    nodeMap.put(0l, root); // Root node


                    if (listCommonResult.isSuccess()) {
                        for (MenuSimpleRespVO menu : listCommonResult.getData()) {
                            if (menu.getType() == 3) {
                                continue;
                            }

                            TreeItem<MenuSimpleRespVO> node = new TreeItem<MenuSimpleRespVO>(menu);
                            nodeMap.put(menu.getId(), node);

                        }

                        listCommonResult.getData().forEach(menuSimpleRespVO -> {
                            if (menuSimpleRespVO.getType() != 3) {
                                TreeItem<MenuSimpleRespVO> parentNode = nodeMap.get(menuSimpleRespVO.getParentId());
                                TreeItem<MenuSimpleRespVO> childNode = nodeMap.get(menuSimpleRespVO.getId());
                                if (parentNode != null) {
                                    parentNode.getChildren().add(childNode);
                                }
                            }

                        });

                        if (nodeMap.get(wrapper.get().getParentId()) != null) {
                            selectTreeItem.setValue(nodeMap.get(wrapper.get().getParentId()));
                        } else {
                            selectTreeItem.setValue(root);
                        }
                        menuTreeRoot.set(root);

                    }
                })

                .run();

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

    /**
     * 父id属性
     *
     * @return {@link LongProperty}
     */
    public LongProperty parentIdProperty() {
        return wrapper.field("parentId", MenuSaveVO::getParentId, MenuSaveVO::setParentId, 0L);
    }


    /**
     * 菜单类型属性
     *
     * @return {@link StringProperty}
     */
    public IntegerProperty menuTypeProperty() {
        return wrapper.field("type", MenuSaveVO::getType, MenuSaveVO::setType, 2);
    }

    /**
     * 图标属性
     *
     * @return {@link StringProperty}
     */
    public StringProperty iconProperty() {
        return wrapper.field("iconFont", MenuSaveVO::getIconFont, MenuSaveVO::setIconFont, "");
    }

    /**
     * 菜单名称属性
     *
     * @return {@link StringProperty}
     */
    public StringProperty nameProperty() {
        return wrapper.field("name", MenuSaveVO::getName, MenuSaveVO::setName, "");
    }

    /**
     * 显示顺序属性
     *
     * @return {@link IntegerProperty}
     */
    public IntegerProperty sortProperty() {
        return wrapper.field("sort", MenuSaveVO::getSort, MenuSaveVO::setSort, 0);
    }


    /**
     * 路由地址属性
     *
     * @return {@link StringProperty}
     */
    public StringProperty pathProperty() {
        return wrapper.field("path", MenuSaveVO::getPath, MenuSaveVO::setPath, "");
    }

    /**
     * 组件路径属性
     *
     * @return {@link StringProperty}
     */
    public StringProperty componentProperty() {
        return wrapper.field("componentFx", MenuSaveVO::getComponentFx, MenuSaveVO::setComponentFx, "");
    }

    /**
     * 组件路径属性
     *
     * @return {@link StringProperty}
     */
    public StringProperty componentNameProperty() {
        return wrapper.field("componentName", MenuSaveVO::getComponentName, MenuSaveVO::setComponentName, "");
    }

    /**
     * 权限字符串
     *
     * @return {@link StringProperty}
     */
    public StringProperty permissionProperty() {
        return wrapper.field("permission", MenuSaveVO::getPermission, MenuSaveVO::setPermission, "");
    }


    /**
     * 缓存属性
     *
     * @return {@link StringProperty}
     */
    public BooleanProperty keepAliveProperty() {
        return wrapper.field("keepAlive", MenuSaveVO::getKeepAlive, MenuSaveVO::setKeepAlive, false);
    }

    /**
     * 可见属性
     *
     * @return {@link StringProperty}
     */
    public BooleanProperty visibleProperty() {
        return wrapper.field("visible", MenuSaveVO::getVisible, MenuSaveVO::setVisible, false);
    }

    public BooleanProperty alwayShowProperty() {
        return wrapper.field("alwayShow", MenuSaveVO::getAlwaysShow, MenuSaveVO::setAlwaysShow, false);
    }

    /**
     * 状态属性
     *
     * @return {@link StringProperty}
     */
    public IntegerProperty statusProperty() {
        return wrapper.field("status", MenuSaveVO::getStatus, MenuSaveVO::setStatus, 1);
    }


    public ObjectProperty<TreeItem<MenuSimpleRespVO>> menuTreeRootProperty() {
        return menuTreeRoot;
    }


    public ObjectProperty<TreeItem<MenuSimpleRespVO>> selectTreeItemProperty() {
        return selectTreeItem;
    }

    @Override
    public void onViewAdded() {

    }

    @Override
    public void onViewRemoved() {

    }
}

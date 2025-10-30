package com.lw.fx.view.main;

import atlantafx.base.theme.Styles;
import atlantafx.base.theme.Tweaks;
import com.lw.dillon.admin.framework.common.pojo.CommonResult;
import com.lw.dillon.admin.module.system.controller.admin.auth.vo.AuthPermissionInfoRespVO;
import com.lw.fx.request.Request;
import com.lw.fx.store.AppStore;
import com.lw.fx.util.MessageType;
import com.lw.fx.websocket.SSLWebSocketClient;
import com.lw.ui.request.api.config.ConfigFeign;
import com.lw.ui.request.api.system.AuthFeign;
import com.lw.ui.request.api.system.NotifyMessageFeign;
import de.saxsys.mvvmfx.MvvmFX;
import de.saxsys.mvvmfx.SceneLifecycle;
import de.saxsys.mvvmfx.ViewModel;
import io.datafx.core.concurrent.ProcessChain;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Side;
import javafx.scene.control.*;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.ArrayList;
import java.util.List;

public class MainViewModel implements ViewModel, SceneLifecycle {

    private StringProperty nickName = new SimpleStringProperty("");
    private StringProperty unreadCount = new SimpleStringProperty("");
    private ObservableList<MenuButton> menuButtonObservableList = FXCollections.observableArrayList();
    private ObjectProperty<TreeItem<AuthPermissionInfoRespVO.MenuVO>> treeItemObjectProperty = new SimpleObjectProperty<>(new TreeItem<>());
    private SimpleBooleanProperty maximized = new SimpleBooleanProperty(false);

    public MainViewModel() {
        loadPermissionInfo();
    }

    private void loadPermissionInfo() {
        ProcessChain.create()
                .addSupplierInExecutor(() -> {
                    return Request.connector(AuthFeign.class).getPermissionInfo();
                })
                .addConsumerInPlatformThread(r -> {

                    if (r.isSuccess()) {
                        List<MenuButton> menuButtons=new ArrayList<>();
                        // 创建根节点（空的或具有实际数据）
                        TreeItem<AuthPermissionInfoRespVO.MenuVO> rootItem = new TreeItem<>(new AuthPermissionInfoRespVO.MenuVO());
                        rootItem.setExpanded(true);
                        nickName.set(r.getData().getUser().getNickname().substring(0, 2));
                        AppStore.setAuthPermissionInfoRespVO(r.getCheckedData());
                        // 将ObservableList转换为TreeItem
                        for (AuthPermissionInfoRespVO.MenuVO menu : r.getData().getMenus()) {
                            rootItem.getChildren().add(createTreeItem(menu));
                            menuButtons.add(createMultiLevelMenu(menu));
                        }
                        treeItemObjectProperty.set(rootItem);
                        menuButtonObservableList.setAll(menuButtons);
                    }
                })
                .addSupplierInExecutor(() -> {
                    String key = "fx.theme.userid." + AppStore.getAuthPermissionInfoRespVO().getUser().getId();
                    CommonResult<String> commonResult = Request.connector(ConfigFeign.class).getConfigKey(key);
                    return commonResult.getCheckedData();
                }).addConsumerInPlatformThread(userTheme -> {
//                    var tm = ThemeManager.getInstance();
//                    tm.setTheme(userTheme);
                })
                .addSupplierInExecutor(() -> Request.connector(NotifyMessageFeign.class).getUnreadNotifyMessageCount())
                .addConsumerInPlatformThread(rel -> {
                    if (rel.isSuccess()) {
                        unreadCount.set(rel.getData() == 0 ? "" : rel.getData() + "");
                    }

                })

                .onException(e -> {
                    unreadCount.set("");
                    e.printStackTrace();
                })
                .withFinal(() -> {
                })
                .run();
    }

    public void loginOut(boolean exit) {
        ProcessChain.create()
                .addSupplierInExecutor(() -> {
                    return Request.connector(AuthFeign.class).logout();
                })
                .addConsumerInPlatformThread(r -> {

                    if (r.isSuccess()) {
                        SSLWebSocketClient.getInstance().loginOut();


                    }
                })
                .onException(e -> {
                    e.printStackTrace();
                })
                .withFinal(() -> {
                    if (exit) {
                        System.exit(0);
                    } else {
                        MvvmFX.getNotificationCenter().publish("showLoginRegister");
                        MvvmFX.getNotificationCenter().publish("message", "退出成功！", MessageType.SUCCESS);
                    }
                })
                .run();
    }

    // 将ObservableList转换为TreeItem
    private TreeItem<AuthPermissionInfoRespVO.MenuVO> createTreeItem(AuthPermissionInfoRespVO.MenuVO menu) {
        TreeItem<AuthPermissionInfoRespVO.MenuVO> treeItem = new TreeItem<>(menu);
        if (menu.getChildren() != null && !menu.getChildren().isEmpty()) {
            for (AuthPermissionInfoRespVO.MenuVO childMenu : menu.getChildren()) {
                treeItem.getChildren().add(createTreeItem(childMenu));
            }
        }
        return treeItem;
    }

    /**
     * 递归创建多级菜单并返回 MenuButton 列表
     */
    private MenuButton createMultiLevelMenu(AuthPermissionInfoRespVO.MenuVO menu) {

        MenuButton menuButton = new MenuButton(menu.getName());
        menuButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        menuButton.setPopupSide(Side.RIGHT);
        menuButton.getStyleClass().addAll(Tweaks.NO_ARROW, Styles.FLAT);
        menuButton.setTooltip(new Tooltip(menu.getName()));
        try {
            menuButton.setGraphic(FontIcon.of(Feather.findByDescription(menu.getIconFont()),40));
        } catch (Exception e) {
            menuButton.setGraphic(FontIcon.of(Feather.STAR));
        }
        if (menu.hasChildren()) {
            // 如果有子菜单，递归生成子菜单
            for (AuthPermissionInfoRespVO.MenuVO child : menu.getChildren()) {
                addSubMenu(menuButton, child);
            }
        }else {
            menuButton.setOnAction(event -> {
                MvvmFX.getNotificationCenter().publish("addTabView", menu);
            });
        }
        return menuButton;

    }

    /**
     * 为 MenuButton 添加子菜单项
     */
    private void addSubMenu(MenuButton parent, AuthPermissionInfoRespVO.MenuVO child) {
        if (child.hasChildren()) {
            // 有子菜单，创建新的 MenuButton
            MenuButton subMenuButton = new MenuButton(child.getName());
            subMenuButton.setPopupSide(Side.RIGHT);
            subMenuButton.getStyleClass().addAll(Styles.FLAT);
            try {
                subMenuButton.setGraphic(FontIcon.of(Feather.findByDescription(child.getIconFont())));
            } catch (Exception e) {
                subMenuButton.setGraphic(FontIcon.of(Feather.STAR));
            }
            for (AuthPermissionInfoRespVO.MenuVO subChild : child.getChildren()) {
                addSubMenu(subMenuButton, subChild);
            }
            parent.getItems().add(new CustomMenuItem(subMenuButton, false));
        } else {
            // 没有子菜单，创建 MenuItem
            MenuItem menuItem = new MenuItem(child.getName());
            try {
                menuItem.setGraphic(FontIcon.of(Feather.findByDescription(child.getIconFont())));
            } catch (Exception e) {
                menuItem.setGraphic(FontIcon.of(Feather.STAR));
            }
            menuItem.setOnAction(event -> {
                MvvmFX.getNotificationCenter().publish("addTabView", child);
            });
            parent.getItems().add(menuItem);
        }
    }


    public String getNickName() {
        return nickName.get();
    }

    public StringProperty nickNameProperty() {
        return nickName;
    }

    public String getUnreadCount() {
        return unreadCount.get();
    }

    public StringProperty unreadCountProperty() {
        return unreadCount;
    }

    public TreeItem<AuthPermissionInfoRespVO.MenuVO> getTreeItemObjectProperty() {
        return treeItemObjectProperty.get();
    }

    public ObjectProperty<TreeItem<AuthPermissionInfoRespVO.MenuVO>> treeItemObjectPropertyProperty() {
        return treeItemObjectProperty;
    }

    public ObservableList<MenuButton> getMenuButtonObservableList() {
        return menuButtonObservableList;
    }

    @Override
    public void onViewAdded() {

    }

    @Override
    public void onViewRemoved() {

    }

    public boolean isMaximized() {
        return maximized.get();
    }

    public SimpleBooleanProperty maximizedProperty() {
        return maximized;
    }



}

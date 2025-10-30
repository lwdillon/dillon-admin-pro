package com.lw.fx.view.main;

import com.lw.dillon.admin.framework.common.pojo.CommonResult;
import com.lw.dillon.admin.module.system.controller.admin.auth.vo.AuthPermissionInfoRespVO;
import com.lw.fx.request.Request;
import com.lw.fx.store.AppStore;
import com.lw.fx.theme.ThemeManager;
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
import javafx.scene.control.TreeItem;


public class MainViewModel implements ViewModel, SceneLifecycle {
    private ObjectProperty<TreeItem<AuthPermissionInfoRespVO.MenuVO>> treeItemObjectProperty = new SimpleObjectProperty<>();


    private final ReadOnlyObjectWrapper<AuthPermissionInfoRespVO.MenuVO> addTab = new ReadOnlyObjectWrapper<>();

    private SimpleBooleanProperty maximized = new SimpleBooleanProperty(false);
    private SimpleObjectProperty theme = new SimpleObjectProperty();
    private SimpleBooleanProperty showNavigationBar = new SimpleBooleanProperty(true);


    private StringProperty title = new SimpleStringProperty(System.getProperty("app.name"));
    private StringProperty nickName = new SimpleStringProperty();
    private StringProperty unreadCount = new SimpleStringProperty();


    public void initialize() {

        getPermissionInfo();

        MvvmFX.getNotificationCenter().subscribe("notify", (key, payload) -> getPermissionInfo());
        MvvmFX.getNotificationCenter().subscribe("updateMenu", (key, payload) -> getPermissionInfo());
    }

    public String getTitle() {
        return title.get();
    }

    public StringProperty titleProperty() {
        return title;
    }

    public void setTitle(String title) {
        this.title.set(title);
    }


    /**
     * 在视图中添加
     */
    @Override
    public void onViewAdded() {

//        setTheme(ThemeManager.getInstance().getDefaultTheme());
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

    /**
     * 在视图中删除
     */
    @Override
    public void onViewRemoved() {

        System.err.println("------remove");
    }


    public SimpleBooleanProperty maximizedProperty() {
        return maximized;
    }


    public boolean isShowNavigationBar() {
        return showNavigationBar.get();
    }

    public SimpleBooleanProperty showNavigationBarProperty() {
        return showNavigationBar;
    }

    public void setShowNavigationBar(boolean showNavigationBar) {
        this.showNavigationBar.set(showNavigationBar);
    }

    public Object getTheme() {
        return theme.get();
    }


    public ReadOnlyObjectProperty<AuthPermissionInfoRespVO.MenuVO> addTabProperty() {
        return addTab.getReadOnlyProperty();
    }

    public void getPermissionInfo() {

        ProcessChain.create()
                .addSupplierInExecutor(() -> {
                    return Request.connector(AuthFeign.class).getPermissionInfo();
                })
                .addConsumerInPlatformThread(r -> {

                    if (r.isSuccess()) {
                        // 创建根节点（空的或具有实际数据）
                        TreeItem<AuthPermissionInfoRespVO.MenuVO> rootItem = new TreeItem<>(new AuthPermissionInfoRespVO.MenuVO());
                        rootItem.setExpanded(true);
                        nickName.set(r.getData().getUser().getNickname().substring(0, 2));
                        AppStore.setAuthPermissionInfoRespVO(r.getCheckedData());
                        // 将ObservableList转换为TreeItem
                        for (AuthPermissionInfoRespVO.MenuVO menu : r.getData().getMenus()) {
                            rootItem.getChildren().add(createTreeItem(menu));
                        }
                        treeItemObjectProperty.set(rootItem);
                    } else {

                    }
                })
                .addSupplierInExecutor(() -> {
                    String key = "fx.theme.userid." + AppStore.getAuthPermissionInfoRespVO().getUser().getId();
                    CommonResult<String> commonResult = Request.connector(ConfigFeign.class).getConfigKey(key);
                    return commonResult.getCheckedData();
                }).addConsumerInPlatformThread(userTheme -> {
                    var tm = ThemeManager.getInstance();
                    tm.setTheme(userTheme);
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

    public ObjectProperty<TreeItem<AuthPermissionInfoRespVO.MenuVO>> treeItemObjectPropertyProperty() {
        return treeItemObjectProperty;
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
}

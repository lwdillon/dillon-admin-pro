package com.dillon.lw.fx.view.main;

import atlantafx.base.theme.Styles;
import atlantafx.base.theme.Tweaks;
import cn.hutool.core.util.StrUtil;
import com.dillon.lw.api.infra.ConfigApi;
import com.dillon.lw.api.system.AuthApi;
import com.dillon.lw.api.system.UserProfileApi;
import com.dillon.lw.framework.common.pojo.PageResult;
import com.dillon.lw.fx.eventbus.EventBusCenter;
import com.dillon.lw.fx.eventbus.event.LogoutEvent;
import com.dillon.lw.fx.eventbus.event.MessageEvent;
import com.dillon.lw.fx.eventbus.event.SideMenuEvent;
import com.dillon.lw.fx.eventbus.event.ThemeEvent;
import com.dillon.lw.fx.http.PayLoad;
import com.dillon.lw.fx.mvvm.base.BaseViewModel;
import com.dillon.lw.fx.store.AppStore;
import com.dillon.lw.fx.utils.MessageType;
import com.dillon.lw.fx.view.home.DashboardView;
import com.dillon.lw.module.infra.controller.admin.config.vo.ConfigRespVO;
import com.dillon.lw.module.infra.controller.admin.config.vo.ConfigSaveReqVO;
import com.dillon.lw.module.system.controller.admin.auth.vo.AuthPermissionInfoRespVO;
import com.dillon.lw.module.system.controller.admin.user.vo.profile.UserProfileRespVO;
import com.dtflys.forest.Forest;
import com.google.common.eventbus.Subscribe;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.util.Duration;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.dillon.lw.fx.utils.NodeUtils.getIcon;

public class MainViewModel extends BaseViewModel {
    private StringProperty userNameProperty = new SimpleStringProperty("");
    private ObjectProperty<TreeItem<AuthPermissionInfoRespVO.MenuVO>> treeItemObjectProperty = new SimpleObjectProperty<>(new TreeItem<>());
    private ObservableList<MenuButton> menuButtonObservableList = FXCollections.observableArrayList();
    private ObjectProperty<TreeItem<AuthPermissionInfoRespVO.MenuVO>> selectedTreeItem = new SimpleObjectProperty<>();
    private MenuButton currentlyOpenedMenu = null;
    private ObjectProperty<UserProfileRespVO> userProfileRespVO = new SimpleObjectProperty<>();
    private BooleanProperty darkMode = new SimpleBooleanProperty(AppStore.isDarkMode());

    public MainViewModel() {

        initData();
    }

    public void initData() {

        CompletableFuture.supplyAsync(() -> {
            return new PayLoad<AuthPermissionInfoRespVO>().apply(Forest.client(AuthApi.class).getPermissionInfo());
        }).thenAcceptAsync(data -> {
            AppStore.setAuthPermissionInfoRespVO(data);
            userNameProperty.set(StrUtil.subSuf(data.getUser().getNickname(), data.getUser().getNickname().length() - 1));

            // 处理成功的响应数据
            bindTreeViewRoot(data.getMenus());
            bindMenuButton(data.getMenus());
            if (selectedTreeItem.get() != null) {
                setSelectedTreeItem(selectedTreeItem.get());
            } else {
                setSelectedTreeItem(treeItemObjectProperty.get().getChildren().get(0));
            }

        }, Platform::runLater).exceptionally(throwable -> {
            // 处理错误
            System.err.println("Error: " + throwable.getMessage());
            return null;
        });

        CompletableFuture.supplyAsync(() -> {
            return new PayLoad<UserProfileRespVO>().apply(Forest.client(UserProfileApi.class).getUserProfile());
        }).thenAcceptAsync(response -> {
            userProfileRespVO.set(response);
        }, Platform::runLater).thenApplyAsync(unused -> {
            // 获取用户主题
            String key = userProfileRespVO.get().getId() + "";
            return new PayLoad<String>().apply(Forest.client(ConfigApi.class).getConfigKey(key));
        }).thenAcceptAsync(data -> {
            setDarkMode(StrUtil.contains(data, "dark"));
            updateTheme();
        }, Platform::runLater).exceptionally(throwable -> {
            // 处理错误
            System.err.println("Error: " + throwable.getMessage());
            return null;
        });
    }

    public void loginOut(boolean exeit) {
        AppStore.setToken(null);
        AppStore.setAuthPermissionInfoRespVO(null);
        AppStore.setDictDataListMap(null);
        // 退出登录
        CompletableFuture.supplyAsync(() -> {
            return new PayLoad<Boolean>().apply(Forest.client(AuthApi.class).logout());
        }).thenAcceptAsync(data -> {
            if (exeit) {
                System.exit(0);
            } else {
                EventBusCenter.get().post(new LogoutEvent());
                EventBusCenter.get().post(new MessageEvent("退出成功！", MessageType.SUCCESS));
            }
        }, Platform::runLater).exceptionally(throwable -> {
            // 处理错误
            EventBusCenter.get().post(new MessageEvent("退出失败！", MessageType.DANGER));
            System.err.println("Error: " + throwable.getMessage());
            return null;
        });
    }

    private TreeItem<AuthPermissionInfoRespVO.MenuVO> convertToTreeItem(AuthPermissionInfoRespVO.MenuVO menu) {
        TreeItem<AuthPermissionInfoRespVO.MenuVO> item = new TreeItem<>(menu);
        // 为每个节点添加监听器
        if (menu.getChildren() != null) {
            for (AuthPermissionInfoRespVO.MenuVO child : menu.getChildren()) {
                item.getChildren().add(convertToTreeItem(child));
            }
        }
        return item;
    }

    public void bindTreeViewRoot(List<AuthPermissionInfoRespVO.MenuVO> treeList) {
        AuthPermissionInfoRespVO.MenuVO homeMenu = new AuthPermissionInfoRespVO.MenuVO();
        homeMenu.setParentId(0L);
        homeMenu.setName("首页");
        homeMenu.setComponentFx(DashboardView.class.getName());
        homeMenu.setIcon("lw-home");
        homeMenu.setAlwaysShow(true);
        treeList.add(0, homeMenu);
        TreeItem<AuthPermissionInfoRespVO.MenuVO> root = new TreeItem<>(new AuthPermissionInfoRespVO.MenuVO()); // 空节点作为 root
        for (AuthPermissionInfoRespVO.MenuVO topMenu : treeList) {
            root.getChildren().add(convertToTreeItem(topMenu));
        }
        treeItemObjectProperty.setValue(root);
    }

    public void bindMenuButton(List<AuthPermissionInfoRespVO.MenuVO> treeList) {
        menuButtonObservableList.clear();
        List<MenuButton> menuButtons = treeList.stream()
                .map(this::createRecursiveMenuButton)
                .collect(Collectors.toList());
        menuButtonObservableList.addAll(menuButtons);
    }

    // 递归创建 MenuButton
    private MenuButton createRecursiveMenuButton(AuthPermissionInfoRespVO.MenuVO menuVO) {
        MenuButton menuButton = new MenuButton(menuVO.getName());
        configureMenuButton(menuButton, menuVO);

        List<AuthPermissionInfoRespVO.MenuVO> children = menuVO.getChildren();
        if (children != null && !children.isEmpty()) {
            for (AuthPermissionInfoRespVO.MenuVO child : children) {
                if (hasChildren(child)) {
                    menuButton.getItems().add(createRecursiveMenu(child));
                } else {
                    menuButton.getItems().add(createMenuItem(child));
                }
            }
        } else {
            menuButton.setTooltip(new Tooltip(menuVO.getName()));
            menuButton.setOnMouseClicked(e -> selectTreeItemByName(menuVO.getName()));
        }

        setupHoverEffects(menuButton);
        return menuButton;
    }

    // 提取公用配置逻辑
    private void configureMenuButton(MenuButton menuButton, AuthPermissionInfoRespVO.MenuVO menuVO) {
        menuButton.setMaxSize(47, 47);
        menuButton.setMinSize(47, 47);
        menuButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        menuButton.setPopupSide(Side.RIGHT);
        menuButton.getStyleClass().addAll(Tweaks.NO_ARROW, Styles.FLAT);
        menuButton.setId("nar-menu-button");
        menuButton.setGraphic(getIcon(menuVO.getIcon()));

    }

    // 鼠标悬浮效果封装
    private void setupHoverEffects(MenuButton menuButton) {
        menuButton.setOnMouseEntered(event -> {
            if (currentlyOpenedMenu != null && currentlyOpenedMenu != menuButton) {
                currentlyOpenedMenu.hide();
            }
            menuButton.show();
            currentlyOpenedMenu = menuButton;

            animateScale(menuButton.getGraphic(), 1.2);
        });

        menuButton.setOnMouseExited(e -> animateScale(menuButton.getGraphic(), 1.0));
    }

    // 缩放动画
    private void animateScale(Node node, double scale) {
        if (node == null) {
            return;
        }
        ScaleTransition st = new ScaleTransition(Duration.millis(200), node);
        st.setToX(scale);
        st.setToY(scale);
        st.play();
    }

    // 创建子菜单
    private Menu createRecursiveMenu(AuthPermissionInfoRespVO.MenuVO menuVO) {
        Menu menu = new Menu(menuVO.getName());
        menu.setGraphic(getIcon(menuVO.getIcon()));
        List<AuthPermissionInfoRespVO.MenuVO> children = menuVO.getChildren();

        if (children != null && !children.isEmpty()) {
            for (AuthPermissionInfoRespVO.MenuVO child : children) {
                if (hasChildren(child)) {
                    menu.getItems().add(createRecursiveMenu(child));
                } else {
                    menu.getItems().add(createMenuItem(child));
                }
            }
        }
        return menu;
    }

    // 创建最终菜单项
    private MenuItem createMenuItem(AuthPermissionInfoRespVO.MenuVO menuVO) {
        MenuItem item = new MenuItem(menuVO.getName());
        item.setGraphic(getIcon(menuVO.getIcon()));
        item.setOnAction(e -> selectTreeItemByName(menuVO.getName()));
        return item;
    }

    // 判断是否有子菜单
    private boolean hasChildren(AuthPermissionInfoRespVO.MenuVO menuVO) {
        return menuVO.getChildren() != null && !menuVO.getChildren().isEmpty();
    }

    // 封装选择逻辑
    private void selectTreeItemByName(String name) {
        TreeItem<AuthPermissionInfoRespVO.MenuVO> selected = findFirstNodeByName(treeItemObjectProperty.get(), name);
        if (selected != selectedTreeItem.get()) {
            selectedTreeItem.setValue(selected);
        }
    }


    public ObjectProperty<TreeItem<AuthPermissionInfoRespVO.MenuVO>> treeItemObjectPropertyProperty() {
        return treeItemObjectProperty;
    }

    public ObservableList<MenuButton> getMenuButtonObservableList() {
        return menuButtonObservableList;
    }

    public String getUserNameProperty() {
        return userNameProperty.get();
    }

    public StringProperty userNamePropertyProperty() {
        return userNameProperty;
    }

    public TreeItem<AuthPermissionInfoRespVO.MenuVO> getSelectedTreeItem() {
        return selectedTreeItem.get();
    }

    public ObjectProperty<TreeItem<AuthPermissionInfoRespVO.MenuVO>> selectedTreeItemProperty() {
        return selectedTreeItem;
    }

    public void setSelectedTreeItem(TreeItem<AuthPermissionInfoRespVO.MenuVO> selectedTreeItem) {
        this.selectedTreeItem.set(selectedTreeItem);
    }

    public List<TreeItem<AuthPermissionInfoRespVO.MenuVO>> findTreeItemsById(String targetId) {
        List<TreeItem<AuthPermissionInfoRespVO.MenuVO>> result = new ArrayList<>();
        findMatchingItems(treeItemObjectProperty.get(), targetId, result);
        return result;
    }

    public void navigate(TreeItem<AuthPermissionInfoRespVO.MenuVO> current) {
        selectedTreeItem.set(current);
    }

    public List<TreeItem<AuthPermissionInfoRespVO.MenuVO>> getPathToRoot(TreeItem<AuthPermissionInfoRespVO.MenuVO> item) {
        List<TreeItem<AuthPermissionInfoRespVO.MenuVO>> path = new ArrayList<>();
        TreeItem<AuthPermissionInfoRespVO.MenuVO> current = item;

        while (current != null) {
            path.add(current);
            current = current.getParent();
        }

        // 当前是从下往上加的，反转成从上往下
        Collections.reverse(path);
        return path;
    }

    private void findMatchingItems(TreeItem<AuthPermissionInfoRespVO.MenuVO> current,
                                   String targetId,
                                   List<TreeItem<AuthPermissionInfoRespVO.MenuVO>> result) {
        if (current == null || current.getValue() == null) {
            return;
        }

        if (current.isLeaf() && StrUtil.contains(current.getValue().getName(), targetId)) {
            result.add(current);
        }

        for (TreeItem<AuthPermissionInfoRespVO.MenuVO> child : current.getChildren()) {
            findMatchingItems(child, targetId, result);
        }
    }

    public TreeItem<AuthPermissionInfoRespVO.MenuVO> findFirstLeaf(TreeItem<AuthPermissionInfoRespVO.MenuVO> root) {
        if (root == null) {
            return null;
        }

        TreeItem<AuthPermissionInfoRespVO.MenuVO> current = root;
        while (!current.isLeaf()) {
            // 有子节点，取第一个子节点继续往下找
            current = current.getChildren().get(0);
        }
        return current;
    }


    public TreeItem<AuthPermissionInfoRespVO.MenuVO> findFirstNodeByName(TreeItem<AuthPermissionInfoRespVO.MenuVO> root, String targetName) {
        if (root == null) {
            return null;
        }

        // 当前节点匹配
        if (targetName.equals(root.getValue().getName())) {
            return root;
        }

        // 遍历子节点
        for (TreeItem<AuthPermissionInfoRespVO.MenuVO> child : root.getChildren()) {
            TreeItem<AuthPermissionInfoRespVO.MenuVO> found = findFirstNodeByName(child, targetName);
            if (found != null && found.isLeaf()) {
                return found;
            }
        }

        return null;
    }

    public void updateTheme() {

        AppStore.setDarkMode(isDarkMode());
        String value = isDarkMode() ? "primer-dark.css" : "primer-light.css";
        EventBusCenter.get().post(new ThemeEvent(value, isDarkMode()));
    }

    public void updateThemeConfig() {

        String value = isDarkMode() ? "primer-dark.css" : "primer-light.css";
        String key = userProfileRespVO.get().getId() + "";
        ConfigSaveReqVO saveReqVO = new ConfigSaveReqVO();
        saveReqVO.setKey(key);
        saveReqVO.setName("用户主题");
        saveReqVO.setValue(value);
        saveReqVO.setCategory("UI");
        saveReqVO.setVisible(true);
        Map<String, Object> map = new HashMap<>();
        map.put("key", key);

        CompletableFuture.supplyAsync(() -> {
            return new PayLoad<PageResult<ConfigRespVO>>().apply(Forest.client(ConfigApi.class).getConfigPage(map));
        }).thenAcceptAsync(respVO -> {
            if (respVO != null && respVO.getTotal() > 0) {
                saveReqVO.setId(respVO.getList().get(0).getId());
                new PayLoad<Boolean>().apply(Forest.client(ConfigApi.class).updateConfig(saveReqVO));
            } else {
                new PayLoad<Long>().apply(Forest.client(ConfigApi.class).createConfig(saveReqVO));
            }
        }).exceptionally(throwable -> {
            // 处理错误
            System.err.println("Error: " + throwable.getMessage());
            return null;
        });
    }

    public boolean isDarkMode() {
        return darkMode.get();
    }

    public BooleanProperty darkModeProperty() {
        return darkMode;
    }

    public void setDarkMode(boolean darkMode) {
        this.darkMode.set(darkMode);
    }

    public UserProfileRespVO getUserProfileRespVO() {
        return userProfileRespVO.get();
    }

    public ObjectProperty<UserProfileRespVO> userProfileRespVOProperty() {
        return userProfileRespVO;
    }

    @Subscribe
    private void updateSideMenu(SideMenuEvent menuEvent) {
        initData();
    }


}

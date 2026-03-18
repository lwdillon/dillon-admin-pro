package com.dillon.lw.fx.view.main;

import atlantafx.base.controls.Breadcrumbs;
import atlantafx.base.controls.Spacer;
import atlantafx.base.theme.Styles;
import atlantafx.base.theme.Tweaks;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.dillon.lw.api.system.NotifyMessageApi;
import com.dillon.lw.fx.MainApp;
import com.dillon.lw.fx.Resources;
import com.dillon.lw.fx.eventbus.EventBusCenter;
import com.dillon.lw.fx.eventbus.event.MainTabEvent;
import com.dillon.lw.fx.eventbus.event.MessageEvent;
import com.dillon.lw.fx.eventbus.event.RefreshEvent;
import com.dillon.lw.fx.eventbus.event.ThemeEvent;
import com.dillon.lw.fx.mvvm.base.BaseView;
import com.dillon.lw.fx.mvvm.loader.ViewLoader;
import com.dillon.lw.fx.utils.Lazy;
import com.dillon.lw.fx.utils.MessageType;
import com.dillon.lw.fx.utils.NodeUtils;
import com.dillon.lw.fx.view.system.notice.MyNotifyMessagePane;
import com.dillon.lw.fx.rx.FxSchedulers;
import com.dillon.lw.fx.view.infra.apilog.ApiAccessLogView;
import com.dillon.lw.fx.view.infra.apilog.ApiErrorLogView;
import com.dillon.lw.fx.view.infra.config.ConfigView;
import com.dillon.lw.fx.view.infra.file.FileConfigView;
import com.dillon.lw.fx.view.infra.file.FileListView;
import com.dillon.lw.fx.view.infra.job.JobView;
import com.dillon.lw.fx.view.layout.SearchDialog;
import com.dillon.lw.fx.view.system.config.UserInfoView;
import com.dillon.lw.fx.view.system.dept.DeptManageView;
import com.dillon.lw.fx.view.system.dict.DictTypeView;
import com.dillon.lw.fx.view.system.log.loginlog.LoginLogView;
import com.dillon.lw.fx.view.system.log.operatelog.OperateLogView;
import com.dillon.lw.fx.view.system.menu.MenuManageView;
import com.dillon.lw.fx.view.system.post.PostView;
import com.dillon.lw.fx.view.system.role.RoleView;
import com.dillon.lw.fx.view.system.user.UserView;
import com.dillon.lw.fx.websocket.WebSocketNoticeService;
import com.dillon.lw.module.system.controller.admin.auth.vo.AuthPermissionInfoRespVO;
import com.dillon.lw.module.system.controller.admin.permission.vo.role.RoleSimpleRespVO;
import com.dillon.lw.module.system.controller.admin.user.vo.profile.UserProfileRespVO;
import com.dtflys.forest.Forest;
import com.dlsc.gemsfx.AvatarView;
import com.dlsc.gemsfx.SVGImageView;
import com.google.common.eventbus.Subscribe;
import com.goxr3plus.fxborderlessscene.borderless.BorderlessScene;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import static com.dillon.lw.fx.utils.NodeUtils.getIcon;


public class MainView extends BaseView<MainViewModel> {
    private static final int BADGE_COUNT_MAX = 99;

    private boolean showSidebar = true; // 记录菜单状态
    private boolean expand = true; // 记录菜单状态
    private double lastSideBarWidth = 300D; // 记录上次窗口宽度
    private Lazy<SearchDialog> searchDialog;
    private Label bellBadgeLabel;
    private int unreadNoticeCount = 0;


    @FXML
    private StackPane rootPane;
    @FXML
    private Pane backgroundImgPane;

    @FXML
    private Button bellButton;

    @FXML
    private Button closeBut;

    @FXML
    private VBox contentBox;

    @FXML
    private HBox contentPane;

    @FXML
    private HBox crumbsBox;

    @FXML
    private Button foldMenuButton;

    @FXML
    private Label logoLabel;

    @FXML
    private Button maximizeBut;

    @FXML
    private Button minimizeBut;

    @FXML
    private Button refreshButton;

    @FXML
    private Button setButton;

    @FXML
    private Button showSidebarButton;

    @FXML
    private VBox sidebMenuBarBox;

    @FXML
    private VBox sidebarBox;

    @FXML
    private TreeView<AuthPermissionInfoRespVO.MenuVO> siderTreeView;

    @FXML
    private HBox statusBox;

    @FXML
    private TabPane tabPane;

    @FXML
    private Button themeBut;

    @FXML
    private HBox titleBox;

    @FXML
    private AvatarView userAvatarView;

    @FXML
    private HBox searchBox;

    @FXML
    private SVGImageView logoSvgView;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        EventBusCenter.get().register(this);
        logoSvgView.setSvgUrl("/icons/dillon.svg");
        Stop[] stops = new Stop[]{
                new Stop(0, Color.web("#6a11cb")),
                new Stop(0.25, Color.web("#56ccf2")),
                new Stop(0.5, Color.web("#f6d02f")),
                new Stop(0.75, Color.web("#feb47b")),
                new Stop(1, Color.web("#ff7e5f"))


        };
        LinearGradient gradient = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE, stops);
        logoLabel.setTextFill(gradient);
        // 设置 clip，实现圆角裁剪
        Rectangle clip = new Rectangle();
        clip.setArcWidth(20);
        clip.setArcHeight(20);
        rootPane.setClip(clip);

        rootPane.layoutBoundsProperty().addListener((obs, oldVal, newVal) -> {
            clip.setWidth(newVal.getWidth());
            clip.setHeight(newVal.getHeight());
        });

//        logoLabel.getStyleClass().addAll(TITLE_3);
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
        tabPane.setId("main-tab-pane");
        userAvatarView.setImage(null);
        userAvatarView.initialsProperty().bind(viewModel.userNamePropertyProperty());
        // 添加右键菜单
        ContextMenu contextMenu = createContextMenu(tabPane);
        tabPane.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY && isOnTab(event)) {
                contextMenu.show(tabPane, event.getScreenX(), event.getScreenY());
            } else {
                contextMenu.hide();
            }
        });

        userAvatarView.setOnMouseClicked(event -> showPersonalInformation());


        Region minus = new Region();
        minus.getStyleClass().add("minus-icon");

        Region resizeMax = new Region();
        resizeMax.getStyleClass().add("resize-max-icon");

        Region resizeMin = new Region();
        resizeMin.getStyleClass().add("resize-min-icon");

        Region close = new Region();
        close.getStyleClass().add("close-icon");


        setButton.getStyleClass().addAll(Styles.FLAT, Styles.BUTTON_CIRCLE);
        themeBut.getStyleClass().addAll(Styles.FLAT, Styles.BUTTON_CIRCLE);
        bellButton.getStyleClass().addAll(Styles.FLAT, Styles.BUTTON_CIRCLE);
        initBellBadge();
        showSidebarButton.getStyleClass().addAll(Styles.FLAT, Styles.BUTTON_ICON);
        refreshButton.getStyleClass().addAll(Styles.FLAT, Styles.BUTTON_ICON);
        foldMenuButton.getStyleClass().addAll(Styles.FLAT, Styles.BUTTON_ICON);
        refreshButton.setOnAction(event -> EventBusCenter.get().post(new RefreshEvent("")));
        bellButton.setOnAction(event -> openMyNotifyMessageTab());

        minimizeBut.setOnAction(actionEvent -> {

            if (minimizeBut.getScene() instanceof BorderlessScene) {
                ((BorderlessScene) maximizeBut.getScene()).minimizeStage();
            }
        });
        minimizeBut.getStyleClass().addAll(Styles.FLAT);
        minimizeBut.setGraphic(minus);

        maximizeBut.setGraphic(resizeMax);
        maximizeBut.getStyleClass().addAll(Styles.FLAT);
        maximizeBut.setOnAction(actionEvent -> {

            if (maximizeBut.getScene() instanceof BorderlessScene) {

                ((BorderlessScene) maximizeBut.getScene()).maximizeStage();
//                viewModel.maximizedProperty().set( ((BorderlessScene) maximizeBut.getScene()).isMaximized());
            }
        });


        closeBut.getStyleClass().addAll(Styles.FLAT);
        closeBut.setGraphic(close);
        closeBut.setOnAction(actionEvent -> {
            if (MainApp.confirmAndMarkExit(closeBut.getScene().getWindow())) {
                viewModel.loginOut(true);
            }
        });


        var crumbs = new Breadcrumbs<TreeItem<AuthPermissionInfoRespVO.MenuVO>>();
        crumbs.setCrumbFactory(crumb -> {
            FontIcon icon = new FontIcon();
            if (crumb.isFirst()) {
                icon = getIcon("lw-home");
            } else {
                icon = getIcon(crumb.getValue().getValue().getIcon());
            }
            var btn = new Button(crumb.getValue().getValue().getName(), icon);
            btn.setStyle("-fx-font-weight: bold;");
            btn.getStyleClass().add(Styles.FLAT);
            btn.setFocusTraversable(false);
            return btn;
        });
        crumbs.setDividerFactory(item -> {
            if (item == null) {
                return null;
            }

            return !item.isLast() ? new FontIcon(Feather.CHEVRON_RIGHT) : null;
        });
        crumbs.selectedCrumbProperty().addListener((obs, old, val) -> {
            if (val == null) {
                return;
            }
            if (val.getValue().isLeaf()) {
                viewModel.setSelectedTreeItem(val.getValue());
            } else {
                if (val.getValue().getParent() == null) {

                    tabPane.getSelectionModel().select(0);
                } else {
                    viewModel.setSelectedTreeItem(viewModel.findFirstLeaf(val.getValue()));
                }
            }

        });

        crumbsBox.getChildren().add(crumbs);


        showSidebarButton.setOnAction(event -> {
            showSidebar = !showSidebar;
            setShowSidebar(showSidebar);
        });

        foldMenuButton.setOnAction(event -> {
            expand = !expand;
            setExpand(expand);
        });
        themeBut.setOnAction(event -> {
            viewModel.setDarkMode(!viewModel.isDarkMode());
            viewModel.updateTheme();
            viewModel.updateThemeConfig();
        });
        siderTreeView.setFixedCellSize(45);
        siderTreeView.rootProperty().bind(viewModel.treeItemObjectPropertyProperty());
        siderTreeView.setShowRoot(false);

        Styles.toggleStyleClass(siderTreeView, Tweaks.EDGE_TO_EDGE);
        Styles.toggleStyleClass(siderTreeView, Tweaks.ALT_ICON);
        siderTreeView.setCellFactory(p -> new NavTreeCell());

        Bindings.bindContent(sidebMenuBarBox.getChildren(), viewModel.getMenuButtonObservableList());


        searchDialog = new Lazy<>(() -> {
            var dialog = new SearchDialog(viewModel);
            dialog.setClearOnClose(true);
            return dialog;
        });

        searchBox.setOnMouseClicked(e -> openSearchDialog());
// 监听 TreeView -> ViewModel
        siderTreeView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal.isLeaf()) {
                List<TreeItem<AuthPermissionInfoRespVO.MenuVO>> list = viewModel.getPathToRoot(newVal);
                Breadcrumbs.BreadCrumbItem<TreeItem<AuthPermissionInfoRespVO.MenuVO>> root = Breadcrumbs.buildTreeModel(list.toArray(TreeItem[]::new));
                crumbs.setSelectedCrumb(root);
                viewModel.selectedTreeItemProperty().set(newVal);
                // 渐变动画：淡入
                FadeTransition fadeIn = new FadeTransition(Duration.millis(300), crumbs);
                fadeIn.setFromValue(0);
                fadeIn.setToValue(1);
                fadeIn.play();
            }

        });
        // 监听 ViewModel -> TreeView
        viewModel.selectedTreeItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal.isLeaf()) {
                siderTreeView.getSelectionModel().select(newVal);

                int row = siderTreeView.getRow(newVal);
                if (row >= 0) {
                    // 居中滚动，5 为经验值，可调
                    siderTreeView.scrollTo(Math.max(row - 5, 0));
                }
                addTab(newVal);
            }

        });
        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                var selectedTab = tabPane.getSelectionModel().getSelectedItem();
                if (selectedTab != null) {
                    viewModel.setSelectedTreeItem((TreeItem<AuthPermissionInfoRespVO.MenuVO>) selectedTab.getUserData());
                    // 渐变动画：淡入
                    FadeTransition fadeIn = new FadeTransition(Duration.millis(300), selectedTab.getContent());
                    fadeIn.setFromValue(0);
                    fadeIn.setToValue(1);
                    fadeIn.play();
                }
            }
        });

//        MvvmFX.getNotificationCenter().subscribe("updateMenu3", (key, payload) -> viewModel.initData());
        // 启动 WebSocket（进入主界面后生效）并注册消息处理。
        WebSocketNoticeService.getInstance().setMessageListener(this::onWebSocketMessage);
        WebSocketNoticeService.getInstance().start(com.dillon.lw.fx.store.AppStore.getToken());
        loadUnreadNoticeCount();

    }


    private <T> Breadcrumbs.BreadCrumbItem<T> getTreeItemByIndex(Breadcrumbs.BreadCrumbItem<T> node, int index) {
        var counter = index;
        var current = node;
        while (counter > 0 && current.getParent() != null) {
            current = (Breadcrumbs.BreadCrumbItem<T>) current.getParent();
            counter--;
        }
        return current;
    }

    private ContextMenu createContextMenu(TabPane tabPane) {
        ContextMenu contextMenu = new ContextMenu();

        // 关闭当前标签
        MenuItem closeCurrent = new MenuItem("关闭当前");
        closeCurrent.setOnAction(e -> {
            Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
            if (selectedTab != null && selectedTab.isClosable()) {
                tabPane.getTabs().remove(selectedTab);
            }
        });

        // 关闭左侧标签
        MenuItem closeLeft = new MenuItem("关闭左侧");
        closeLeft.setOnAction(e -> {
            Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
            if (selectedTab != null) {
                int selectedIndex = tabPane.getTabs().indexOf(selectedTab);
                tabPane.getTabs().removeIf(tab -> tabPane.getTabs().indexOf(tab) < selectedIndex && tab.isClosable());
            }
        });

        // 关闭右侧标签
        MenuItem closeRight = new MenuItem("关闭右侧");
        closeRight.setOnAction(e -> {
            Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
            if (selectedTab != null) {
                int selectedIndex = tabPane.getTabs().indexOf(selectedTab);
                tabPane.getTabs().removeIf(tab -> tabPane.getTabs().indexOf(tab) > selectedIndex && tab.isClosable());
            }
        });

        // 关闭其它标签
        MenuItem closeOthers = new MenuItem("关闭其它");
        closeOthers.setOnAction(e -> {
            Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
            if (selectedTab != null) {
                tabPane.getTabs().removeIf(tab -> tab != selectedTab && tab.isClosable());
            }
        });

        // 关闭所有标签
        MenuItem closeAll = new MenuItem("关闭所有");
        closeAll.setOnAction(e -> tabPane.getTabs().removeIf(Tab::isClosable));
        contextMenu.getItems().addAll(closeCurrent, closeLeft, closeRight, closeOthers, closeAll);
        return contextMenu;
    }

    private void showPersonalInformation() {
        UserProfileRespVO user = viewModel.getUserProfileRespVO();
        FontIcon fontIcon1 = new FontIcon(Feather.SETTINGS);
        fontIcon1.setIconSize(24);
        FontIcon fontIcon2 = new FontIcon(Feather.LOG_OUT);
        fontIcon2.setIconSize(24);
        ContextMenu contextMenu = new ContextMenu();

        AvatarView avatarView = new AvatarView();
        avatarView.setAvatarShape(AvatarView.AvatarShape.ROUND);
        avatarView.setImage(null);

        avatarView.setInitials(StrUtil.subSuf(user.getNickname(), user.getNickname().length() - 1));
        HBox hBox = new HBox();
        hBox.setSpacing(10);
        hBox.setAlignment(Pos.CENTER_LEFT);
        Label usernameLabel = new Label(user.getUsername());
        usernameLabel.getStyleClass().add("title-2");
        hBox.getChildren().add(usernameLabel);
        for (RoleSimpleRespVO role : user.getRoles()) {
            Label roleLabel = new Label(role.getName());
            roleLabel.setPrefHeight(40);
            roleLabel.setPadding(new Insets(5, 5, 5, 5));
            roleLabel.setStyle("-fx-font-weight: 100;-fx-text-fill: -color-accent-emphasis;-fx-background-color: -color-accent-subtle;-fx-background-radius: 10;");
            hBox.getChildren().add(roleLabel);
        }
        Label emailLabel = new Label(user.getEmail());
        emailLabel.getStyleClass().add("title-4");
        GridPane grid = new GridPane();
        grid.setPrefHeight(60);
        grid.setMaxHeight(60);
        grid.setMaxWidth(180);

        grid.setHgap(10);                   // 列间距
        grid.setVgap(10);                   // 行间距

        // 按 (列, 行) 的方式添加到GridPane
        grid.add(avatarView, 0, 0, 1, 2);
        grid.add(hBox, 1, 0, 1, 1);
        grid.add(emailLabel, 1, 1, 1, 1);

        HBox.setHgrow(emailLabel, Priority.ALWAYS);
        // 让 usernameLabel 水平居中对齐
        GridPane.setHalignment(avatarView, HPos.CENTER);
        GridPane.setHgrow(hBox, Priority.ALWAYS);
        CustomMenuItem menuItem = new CustomMenuItem(grid);
        menuItem.setId("user-menu-item");

        MenuItem menuItem1 = new MenuItem("个人中心");
        menuItem1.setGraphic(fontIcon1);
        MenuItem gitItem = new MenuItem("Gitee");
        gitItem.setGraphic(new FontIcon(Feather.GITHUB));

        MenuItem menuItem2 = new MenuItem("退出系统");
        menuItem2.setGraphic(fontIcon2);

        MenuItem menuItem3 = new MenuItem("锁定屏幕");
        menuItem3.setGraphic(new FontIcon(Feather.LOCK));
        contextMenu.getItems().addAll(menuItem, new SeparatorMenuItem(), gitItem, menuItem1, new SeparatorMenuItem(), menuItem3, new SeparatorMenuItem(), menuItem2);
        Bounds bounds = userAvatarView.localToScreen(userAvatarView.getBoundsInLocal());
        contextMenu.show(userAvatarView, bounds.getMinX(), bounds.getMinY() + bounds.getHeight());

        menuItem2.setOnAction(actionEvent -> viewModel.loginOut(false));
        menuItem1.setOnAction(actionEvent -> {
            UserInfoView userInfoView = ViewLoader.load(UserInfoView.class);
            EventBusCenter.get().post(new MainTabEvent("fth-user", "个人中心", userInfoView.getNode()));

        });


    }

    private boolean isOnTab(MouseEvent event) {
        // 检查点击是否在TabPane顶部的60像素范围内
        double mouseY = event.getY();
        return mouseY >= 0 && mouseY <= 60;
    }


    private void setShowSidebar(boolean isShow) {

        // VBox 淡出动画
        FadeTransition boxFadeOut = new FadeTransition(Duration.millis(300), sidebarBox);
        Timeline widthAnimation = new Timeline();

        if (isShow) {
            boxFadeOut.setFromValue(0.0);
            boxFadeOut.setToValue(1.0);

            // VBox 宽度调整动画
            KeyValue kv = new KeyValue(sidebarBox.prefWidthProperty(), lastSideBarWidth); // 收缩宽度
            KeyFrame kf = new KeyFrame(Duration.millis(300), kv);
            widthAnimation.getKeyFrames().add(kf);
        } else {
            boxFadeOut.setFromValue(1.0);
            boxFadeOut.setToValue(0.0);

            // VBox 宽度调整动画
            KeyValue kv = new KeyValue(sidebarBox.prefWidthProperty(), 0D); // 收缩宽度
            KeyFrame kf = new KeyFrame(Duration.millis(300), kv);
            widthAnimation.getKeyFrames().add(kf);
        }

        // 使用 ParallelTransition 同时播放动画
        ParallelTransition parallelTransition = new ParallelTransition(boxFadeOut, widthAnimation);
        parallelTransition.setOnFinished(event -> {


        });
        parallelTransition.play();

    }

    private void setExpand(boolean expand) {

        // TreeView 淡入动画
        FadeTransition treeFadeIn = new FadeTransition(Duration.millis(300), siderTreeView);

        // VBox 淡出动画
        FadeTransition boxFadeOut = new FadeTransition(Duration.millis(300), sidebMenuBarBox);
        Timeline widthAnimation = new Timeline();
        if (expand) {
            foldMenuButton.setGraphic(new FontIcon(Feather.CHEVRONS_LEFT));
            boxFadeOut.setFromValue(1.0);
            boxFadeOut.setToValue(0.0);
            treeFadeIn.setFromValue(0.0);
            treeFadeIn.setToValue(1.0);
            // VBox 宽度调整动画
            KeyValue kv = new KeyValue(sidebarBox.prefWidthProperty(), 300D); // 收缩宽度
            KeyFrame kf = new KeyFrame(Duration.millis(300), kv);
            widthAnimation.getKeyFrames().add(kf);
        } else {
            foldMenuButton.setGraphic(new FontIcon(Feather.CHEVRONS_RIGHT));
            boxFadeOut.setFromValue(0.0);
            boxFadeOut.setToValue(1.0);
            treeFadeIn.setFromValue(1.0);
            treeFadeIn.setToValue(0.0);
            // VBox 宽度调整动画
            KeyValue kv = new KeyValue(sidebarBox.prefWidthProperty(), 64D); // 收缩宽度
            KeyFrame kf = new KeyFrame(Duration.millis(300), kv);
            widthAnimation.getKeyFrames().add(kf);
        }

        // 使用 ParallelTransition 同时播放动画
        ParallelTransition parallelTransition = new ParallelTransition(treeFadeIn, boxFadeOut, widthAnimation);
        parallelTransition.setOnFinished(event -> {
            lastSideBarWidth = expand ? 300D : 64D;
        });
        logoLabel.setVisible(expand);
        logoLabel.setManaged(expand);

        sidebMenuBarBox.setVisible(expand ? false : true);
        siderTreeView.setVisible(expand ? true : false);
        parallelTransition.play();

    }

    @Subscribe
    private void updateTheme(ThemeEvent event) {
        Platform.runLater(() -> {
            animateThemeChange(Duration.millis(350));
            // 1️⃣ 图标
            themeBut.setGraphic(
                    new FontIcon(viewModel.isDarkMode() ? Feather.SUN : Feather.MOON)
            );

            // 2️⃣ UserAgent：只放“纯主题”
            MainApp.setUserAgentStylesheet(
                    Resources.resolve("/styles/" + event.getTheme())
            );


        });


    }

    private void animateThemeChange(Duration duration) {
        Scene scene = rootPane.getScene();
        Image snapshot = scene.snapshot(null);
        Pane root = (Pane) scene.getRoot();

        ImageView imageView = new ImageView(snapshot);
        root.getChildren().add(imageView); // add snapshot on top

        var transition = new Timeline(new KeyFrame(Duration.ZERO, new KeyValue(imageView.opacityProperty(), 1, Interpolator.EASE_OUT)), new KeyFrame(duration, new KeyValue(imageView.opacityProperty(), 0, Interpolator.EASE_OUT)));
        transition.setOnFinished(e -> root.getChildren().remove(imageView));
        transition.play();
    }

    public static final class NavTreeCell extends TreeCell<AuthPermissionInfoRespVO.MenuVO> {

        private static final PseudoClass GROUP = PseudoClass.getPseudoClass("group");

        private final HBox root;
        private final Label titleLabel;
        private final Node arrowIcon;
        private final Label tagLabel;

        public NavTreeCell() {
            super();

            titleLabel = new Label();
            titleLabel.setGraphicTextGap(10);
            titleLabel.getStyleClass().add("title");

            arrowIcon = new FontIcon();
            arrowIcon.getStyleClass().add("arrow");

            tagLabel = new Label("1");
            tagLabel.setAlignment(Pos.CENTER);
            tagLabel.getStyleClass().add("tag");

            root = new HBox();
            root.setAlignment(Pos.CENTER_LEFT);
            root.getChildren().setAll(titleLabel, new Spacer(), tagLabel, arrowIcon);
            root.setCursor(Cursor.HAND);
            root.getStyleClass().add("container");
            root.setMaxWidth(300);

            root.setOnMouseClicked(e -> {

                TreeItem item = getTreeItem();

                if (CollUtil.isNotEmpty(item.getChildren()) && e.getButton() == MouseButton.PRIMARY) {
                    item.setExpanded(!item.isExpanded());
                    // scroll slightly above the target
                    getTreeView().scrollTo(getTreeView().getRow(item) - 10);
                }
            });

            getStyleClass().add("nav-tree-cell");
        }

        @Override
        protected void updateItem(AuthPermissionInfoRespVO.MenuVO nav, boolean empty) {
            super.updateItem(nav, empty);

            if (nav == null || empty) {
                setGraphic(null);
                titleLabel.setText(null);
                titleLabel.setGraphic(null);

            } else {
                setGraphic(root);

                titleLabel.setText(nav.getName());
                titleLabel.setGraphic(getIcon(nav.getIcon()));

                // 👇 鼠标移入自动弹出菜单
                root.setOnMouseEntered(event -> {
                    ScaleTransition st = new ScaleTransition(Duration.millis(200), titleLabel.getGraphic());
                    st.setToX(1.2);
                    st.setToY(1.2);
                    st.play();
                });

                root.setOnMouseExited(e -> {
                    ScaleTransition st = new ScaleTransition(Duration.millis(200), titleLabel.getGraphic());
                    st.setToX(1.0);
                    st.setToY(1.0);
                    st.play();
                });


                boolean group = CollUtil.isNotEmpty(nav.getChildren());
                pseudoClassStateChanged(GROUP, group);
                NodeUtils.toggleVisibility(arrowIcon, group);
                NodeUtils.toggleVisibility(tagLabel, group);
            }
        }
    }

    private void openSearchDialog() {
        var dialog = searchDialog.get();
        dialog.show(rootPane.getScene());
        Platform.runLater(dialog::begForFocus);
    }

    private void addTab(TreeItem<AuthPermissionInfoRespVO.MenuVO> item) {

        String title = item.getValue().getName();
        String icon = item.getValue().getIcon();
        String path = item.getValue().getComponentFx();
        boolean alwaysShow = item.getValue().getAlwaysShow();
        // 判断是否已有相同标题的 Tab
        for (Tab tab : tabPane.getTabs()) {
            if (title.equals(tab.getText())) {
                tabPane.getSelectionModel().select(tab);
                return; // 已存在则选中并返回
            }
        }
        Node node = null;
        Class clazz = null;
        try {
            if (StrUtil.equals("菜单管理", title)) {
                clazz = MenuManageView.class;
            } else if (StrUtil.equals("岗位管理", title)) {
                clazz = PostView.class;
            } else if (StrUtil.equals("用户管理", title)) {
                clazz = UserView.class;
            } else if (StrUtil.equals("角色管理", title)) {
                clazz = RoleView.class;
            } else if (StrUtil.equals("部门管理", title)) {
                clazz = DeptManageView.class;
            } else if (StrUtil.equals("字典管理", title)) {
                clazz = DictTypeView.class;
            } else if (StrUtil.equals("登录日志", title)) {
                clazz = LoginLogView.class;
            } else if (StrUtil.equals("操作日志", title)) {
                clazz = OperateLogView.class;
            } else if (StrUtil.equals("文件配置", title)) {
                clazz = FileConfigView.class;
            } else if (StrUtil.equals("文件列表", title)) {
                clazz = FileListView.class;
            } else if (StrUtil.equals("定时任务", title)) {
                clazz = JobView.class;
            } else if (StrUtil.equals("配置管理", title)) {
                clazz = ConfigView.class;
            } else if (StrUtil.equals("访问日志", title)) {
                clazz = ApiAccessLogView.class;
            } else if (StrUtil.equals("错误日志", title)) {
                clazz = ApiErrorLogView.class;
            } else if (StrUtil.equals("错误日志", title)) {
                clazz = ApiErrorLogView.class;
            } else if (StrUtil.equals("AI 对话", title)) {
//                clazz = AiChatView.class;
            } else {
                clazz = Class.forName(path);
            }
            node = ViewLoader.load(clazz).getNode();
        } catch (ClassNotFoundException e) {
            node = new Label(title);
        }
        StackPane fixedPane = new StackPane(node);
        fixedPane.setStyle("-fx-background-color: -color-bg-inset;");
//        fixedPane.setPadding(new Insets(15));

        Tab tab = new Tab(title);
        tab.setGraphic(getIcon(icon));
        tab.setUserData(item);
        tab.setContent(fixedPane);
        tab.setClosable(StrUtil.equals(title, "首页") ? false : true);
        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().select(tab);

        // 渐变动画：淡入
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), fixedPane);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
    }


    @Subscribe
    private void addTab(MainTabEvent event) {


        Platform.runLater(() -> {

            String title = event.getTabName();
            String icon = event.getIcon();
            Node node = event.getTabContent();
            boolean alwaysShow = false;
            // 判断是否已有相同标题的 Tab
            for (Tab tab : tabPane.getTabs()) {
                if (title.equals(tab.getText())) {
                    tabPane.getSelectionModel().select(tab);
                    return; // 已存在则选中并返回
                }
            }


            StackPane fixedPane = new StackPane(node);
            fixedPane.setStyle("-fx-background-color: -color-bg-subtle;");
            fixedPane.setPadding(new Insets(15));

            Tab tab = new Tab(title);
            tab.setGraphic(getIcon(icon));
            tab.setContent(fixedPane);
            tab.setClosable(StrUtil.equals(title, "首页") ? false : true);
            tabPane.getTabs().add(tab);
            tabPane.getSelectionModel().select(tab);

            // 渐变动画：淡入
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), fixedPane);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.play();

        });

    }

    /**
     * 初始化铃铛图标：固定按钮大小不变，角标右上角锚定。
     * 数字增大时仅向左扩展，保持和其它顶部按钮协调。
     */
    private void initBellBadge() {
        FontIcon bellIcon = new FontIcon(Feather.BELL);
        bellIcon.setIconSize(18);

        bellBadgeLabel = new Label();
        bellBadgeLabel.setId("main-notice-badge");
        bellBadgeLabel.setVisible(false);
        bellBadgeLabel.setManaged(false);

        StackPane bellGraphic = new StackPane(bellIcon, bellBadgeLabel);
        bellGraphic.setPrefSize(20, 20);
        bellGraphic.setMinSize(20, 20);
        bellGraphic.setMaxSize(20, 20);
        StackPane.setAlignment(bellBadgeLabel, Pos.TOP_RIGHT);
        StackPane.setMargin(bellBadgeLabel, new Insets(-3, -5, 0, 0));
        bellButton.setGraphic(bellGraphic);
    }

    private void loadUnreadNoticeCount() {
        Single
                /*
                 * 未读消息数查询不需要阻塞主界面：
                 * 后台线程拉取计数，角标刷新回到 JavaFX UI 线程执行。
                 */
                .fromCallable(() -> Forest.client(NotifyMessageApi.class).getUnreadNotifyMessageCount().getCheckedData())
                .subscribeOn(Schedulers.io())
                .observeOn(FxSchedulers.fx())
                .subscribe(count -> setUnreadNoticeCount(Math.max(0, count == null ? 0 : count.intValue())), ex -> {
                });
    }

    private void onWebSocketMessage(String rawMessage) {
        try {
            JSONObject frame = JSONUtil.parseObj(rawMessage);
            if (!"notice-push".equals(frame.getStr("type"))) {
                return;
            }
            String title = "系统通知";
            String content = frame.getStr("content");
            if (StrUtil.isNotBlank(content)) {
                try {
                    JSONObject notice = JSONUtil.parseObj(content);
                    if (notice.containsKey("title")) {
                        title = notice.getStr("title");
                    }
                } catch (Exception ignored) {
                }
            }
            final String finalTitle = title;
            Platform.runLater(() -> {
                increaseUnreadNoticeCount();
                EventBusCenter.get().post(new MessageEvent("收到消息：" + finalTitle, MessageType.INFO));
            });
        } catch (Exception ignored) {
            // 兼容非标准消息帧，避免影响主流程。
        }
    }

    private void openMyNotifyMessageTab() {
        resetUnreadNoticeCount();
        EventBusCenter.get().post(new MainTabEvent("fth-bell", "我的消息", new MyNotifyMessagePane()));
    }

    private void setUnreadNoticeCount(int count) {
        unreadNoticeCount = Math.max(0, count);
        refreshBellBadge();
    }

    private void increaseUnreadNoticeCount() {
        setUnreadNoticeCount(unreadNoticeCount + 1);
    }

    private void resetUnreadNoticeCount() {
        setUnreadNoticeCount(0);
    }

    private void refreshBellBadge() {
        if (bellBadgeLabel == null) {
            return;
        }
        if (unreadNoticeCount <= 0) {
            bellBadgeLabel.setVisible(false);
            bellBadgeLabel.setManaged(false);
            return;
        }
        String text = unreadNoticeCount > BADGE_COUNT_MAX ? "99+" : String.valueOf(unreadNoticeCount);
        bellBadgeLabel.setText(text);
        bellBadgeLabel.setVisible(true);
        bellBadgeLabel.setManaged(true);

        // 右边缘锚定：位数越多只增加左侧宽度。
        double width;
        if (text.length() <= 1) {
            width = 12;
        } else if (text.length() == 2) {
            width = 18;
        } else {
            width = 24;
        }
        bellBadgeLabel.setMinWidth(width);
        bellBadgeLabel.setPrefWidth(width);
        bellBadgeLabel.setMaxWidth(width);
    }

    @Override
    public void onRemove() {
        super.onRemove();
        EventBusCenter.get().unregister(this);
        WebSocketNoticeService.getInstance().stop();
    }
}

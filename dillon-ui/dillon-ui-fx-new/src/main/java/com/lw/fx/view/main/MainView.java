package com.lw.fx.view.main;

import atlantafx.base.theme.Styles;
import atlantafx.base.theme.Theme;
import atlantafx.base.theme.Tweaks;
import cn.hutool.core.util.StrUtil;
import com.goxr3plus.fxborderlessscene.borderless.BorderlessScene;
import com.lw.dillon.admin.module.system.controller.admin.auth.vo.AuthPermissionInfoRespVO;
import com.lw.fx.Resources;
import com.lw.fx.themes.ThemeManager;
import com.lw.fx.view.home.DashboardView;
import com.lw.fx.view.home.DashboardViewModel;
import com.lw.fx.view.system.config.UserInfoView;
import com.lw.fx.view.system.config.UserInfoViewModel;
import com.lw.fx.view.system.menu.MenuManageView;
import de.saxsys.mvvmfx.*;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.util.Duration;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MainView implements FxmlView<MainViewModel>, Initializable {
    private boolean isCollapsed = false; // 记录菜单状态
    @InjectViewModel
    private MainViewModel viewModel;
    private TreeView<AuthPermissionInfoRespVO.MenuVO> menuTree;
    private VBox menuBox;


    @FXML
    private Pane backgroundImagePane;

    @FXML
    private Pane backgroundPane;

    @FXML
    private Button closeBut;

    @FXML
    private HBox contentPane;

    @FXML
    private MenuItem loginOutBut;
    @FXML
    private MenuItem individualCenterBut;

    @FXML
    private Label logoLabel;

    @FXML
    private Button maximizeBut;

    @FXML
    private Button menuBarBut;

    @FXML
    private StackPane menuPane;

    @FXML
    private Button minimizeBut;

    @FXML
    private VBox navigationPane;

    @FXML
    private HBox statusPane;

    @FXML
    private TabPane tabPane;

    @FXML
    private MenuButton themeBut;

    @FXML
    private HBox titlePane;

    @FXML
    private MenuButton userBut;

    @FXML
    private Label versionLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
        // 添加右键菜单
        ContextMenu contextMenu = createContextMenu(tabPane);
        tabPane.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY && isOnTab(event)) {
                contextMenu.show(tabPane, event.getScreenX(), event.getScreenY());
            } else {
                contextMenu.hide();
            }
        });

        userBut.textProperty().bind(viewModel.nickNameProperty());
        themeBut.getStyleClass().addAll(Styles.FLAT, Tweaks.NO_ARROW);

        menuTree = new TreeView<>();
        menuTree.setFixedCellSize(45);
        menuTree.rootProperty().bind(viewModel.treeItemObjectPropertyProperty());
        menuTree.setShowRoot(false);
        Styles.toggleStyleClass(menuTree, Tweaks.EDGE_TO_EDGE);
        menuTree.setCellFactory(tv -> new TreeCell<AuthPermissionInfoRespVO.MenuVO>() {
            @Override
            protected void updateItem(AuthPermissionInfoRespVO.MenuVO item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    HBox box = new HBox(10);
                    box.setAlignment(Pos.CENTER_LEFT);
                    Label label = new Label(item.getName());
                    Label tagLabel = new Label();
                    box.getChildren().addAll(label, tagLabel);
                    try {
                        label.setGraphic(FontIcon.of(Feather.findByDescription(item.getIconFont())));
                    } catch (Exception e) {
                        label.setGraphic(FontIcon.of(Feather.STAR));
                    }
                    setGraphic(box);
                }
            }
        });
        menuBox = new VBox(10);
        menuBox.getStyleClass().add("menu-box");
        menuBox.setAlignment(Pos.TOP_CENTER);
        Bindings.bindContent(menuBox.getChildren(), viewModel.getMenuButtonObservableList());
        menuBox.setVisible(false);
        menuPane.getChildren().addAll(menuTree, menuBox);

        logoLabel.setText(System.getProperty("app.name"));

        Region menuIcon = new Region();
        menuIcon.getStyleClass().add("menu-icon");

        Region minus = new Region();
        minus.getStyleClass().add("minus-icon");

        Region resizeMax = new Region();
        resizeMax.getStyleClass().add("resize-max-icon");

        Region resizeMin = new Region();
        resizeMin.getStyleClass().add("resize-min-icon");

        Region close = new Region();
        close.getStyleClass().add("close-icon");

        Region themeIcon = new Region();
        themeIcon.getStyleClass().add("theme-button-icon");

        themeBut.getStyleClass().addAll(Styles.FLAT);
        themeBut.setGraphic(themeIcon);

        menuBarBut.getStyleClass().addAll(Styles.FLAT);
        menuBarBut.setGraphic(menuIcon);

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
                viewModel.maximizedProperty().set( ((BorderlessScene) maximizeBut.getScene()).isMaximized());
            }
        });


        closeBut.getStyleClass().addAll(Styles.FLAT);
        closeBut.setGraphic(close);
        closeBut.setOnAction(actionEvent -> {
            System.exit(0);
        });

        viewModel.maximizedProperty().addListener((observableValue, oldVal, newVal) -> {
            maximizeBut.setTooltip(new Tooltip(newVal ? "还原" : "最大化"));
            maximizeBut.setGraphic(newVal ? resizeMin : resizeMax);

        });

        menuBarBut.setOnAction(event -> {

            if (isCollapsed) {
                expandMenu(); // 展开菜单
            } else {
                collapseMenu(); // 折叠菜单
            }
            isCollapsed = !isCollapsed; // 更新状态

        });
        ViewTuple<DashboardView, DashboardViewModel> viewTuple = FluentViewLoader.fxmlView(DashboardView.class).load();
        loddTab("主页", Feather.HOME, true,viewTuple.getView());

        // 设置选择事件监听器
        menuTree.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                AuthPermissionInfoRespVO.MenuVO selectedMenu = newValue.getValue();
                if (StrUtil.isNotBlank(selectedMenu.getComponentFx())) {
                    loddTab(selectedMenu);
                }
            }
        });

        MvvmFX.getNotificationCenter().subscribe("themeUpdate", (k, p) -> {

            if (ThemeManager.getInstance().getCurrentTheme() != null) {
                if (ThemeManager.getInstance().getCurrentTheme().isDarkMode()) {
                    backgroundImagePane.setId("background-image-pane-dark");
                } else {
                    backgroundImagePane.setId("background-image-pane");
                }
            }
        });

        MvvmFX.getNotificationCenter().subscribe("addTabView", (k, payload) -> {

            Platform.runLater(() -> {
                loddTab((AuthPermissionInfoRespVO.MenuVO) payload[0]);

            });
        });

        loginOutBut.setOnAction(actionEvent -> viewModel.loginOut(false));
        individualCenterBut.setOnAction(actionEvent -> {

            ViewTuple<UserInfoView, UserInfoViewModel> viewA = FluentViewLoader.fxmlView(UserInfoView.class).load();
            loddTab("个人中心", Feather.USER, true,viewA.getView());
        });
        initTheme();
    }


    private void collapseMenu() {
        // TreeView 淡出动画
        FadeTransition treeFadeOut = new FadeTransition(Duration.millis(300), menuTree);
        treeFadeOut.setFromValue(1.0);
        treeFadeOut.setToValue(0.0);

        // VBox 淡入动画
        FadeTransition boxFadeIn = new FadeTransition(Duration.millis(300), menuBox);
        boxFadeIn.setFromValue(0.0);
        boxFadeIn.setToValue(1.0);

        // VBox 宽度调整动画
        Timeline widthAnimation = new Timeline();
        KeyValue kv = new KeyValue(navigationPane.prefWidthProperty(), 60); // 目标宽度
        KeyFrame kf = new KeyFrame(Duration.millis(300), kv);
        widthAnimation.getKeyFrames().add(kf);


        // 使用 ParallelTransition 同时播放动画
        ParallelTransition parallelTransition = new ParallelTransition(treeFadeOut, boxFadeIn, widthAnimation);
        parallelTransition.setOnFinished(event -> {
            var image = new ImageView(new Image(Resources.getResource("images/app-icon.png").toString()));
            image.setFitWidth(40);
            image.setFitHeight(40);
            logoLabel.setGraphic(image);
            logoLabel.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            versionLabel.setText(System.getProperty("app.version"));

        });

        menuTree.setVisible(false);
        menuBox.setVisible(true);
        parallelTransition.play();

    }

    private void expandMenu() {
        // TreeView 淡入动画
        FadeTransition treeFadeIn = new FadeTransition(Duration.millis(300), menuTree);
        treeFadeIn.setFromValue(0.0);
        treeFadeIn.setToValue(1.0);

        // VBox 淡出动画
        FadeTransition boxFadeOut = new FadeTransition(Duration.millis(300), menuBox);
        boxFadeOut.setFromValue(1.0);
        boxFadeOut.setToValue(0.0);

        // VBox 宽度调整动画
        Timeline widthAnimation = new Timeline();
        KeyValue kv = new KeyValue(navigationPane.prefWidthProperty(), 300); // 收缩宽度
        KeyFrame kf = new KeyFrame(Duration.millis(300), kv);
        widthAnimation.getKeyFrames().add(kf);

        // 使用 ParallelTransition 同时播放动画
        ParallelTransition parallelTransition = new ParallelTransition(treeFadeIn, boxFadeOut, widthAnimation);
        parallelTransition.setOnFinished(event -> {
            var image = new ImageView(new Image(Resources.getResource("images/app-icon.png").toString()));
            image.setFitWidth(32);
            image.setFitHeight(32);
            logoLabel.setGraphic(image);
            logoLabel.setContentDisplay(ContentDisplay.LEFT);
            versionLabel.setText(System.getProperty("app.name") + " " + System.getProperty("app.version"));
        });

        menuTree.setVisible(true);
        menuBox.setVisible(false);
        parallelTransition.play();
    }


    private void initTheme() {


        List<CheckMenuItem> menuItems = new ArrayList<>();
        for (Theme theme : ThemeManager.getInstance().getThemes()) {
            CheckMenuItem checkMenuItem = new CheckMenuItem(theme.getName());
            checkMenuItem.setUserData(theme);
            menuItems.add(checkMenuItem);
        }


        // 将所有 CheckMenuItem 放入列表，便于管理
        themeBut.getItems().addAll(menuItems);

        // 设置互斥行为
        for (CheckMenuItem item : menuItems) {
            item.setOnAction(event -> {
                // 确保只有当前选中的项被选中
                for (CheckMenuItem otherItem : menuItems) {
                    if (otherItem != item) {
                        otherItem.setSelected(false);
                    }
                }
                // 输出选中的项
                if (item.isSelected()) {

                    if (item != null && item.getUserData() instanceof Theme) {

                        Theme theme = (Theme) item.getUserData();
                        ThemeManager.getInstance().setTheme(theme);

                    }

                }
            });
        }
    }


    private void loddTab(AuthPermissionInfoRespVO.MenuVO obj) {
        var title = obj.getName();
        String component = obj.getComponentFx();
        String iconStr = obj.getIconFont();
        Tab tab = null;
        String finalTitle = title;
        var tabOptional = tabPane.getTabs().stream()
                .filter(t -> StrUtil.equals(t.getText(), finalTitle))
                .findFirst();

        if (tabOptional.isPresent()) {
            tab = tabOptional.get();
        } else {

            Class clazz = null;

            if (StrUtil.equals("菜单管理", title)) {
                clazz = MenuManageView.class;
//            } else if (StrUtil.equals("用户管理", title)) {
//                clazz = UserView.class;
//            } else if (StrUtil.equals("角色管理", title)) {
//                clazz = RoleView.class;
//            } else if (StrUtil.equals("部门管理", title)) {
//                clazz = DeptManageView.class;
//            } else if (StrUtil.equals("岗位管理", title)) {
//                clazz = PostView.class;
//            } else if (StrUtil.equals("字典管理", title)) {
//                clazz = DictTypeView.class;
//            } else if (StrUtil.equals("参数设置", title)) {
//                clazz = ConfigView.class;
//            } else if (StrUtil.equals("通知公告", title)) {
//                clazz = NoticeView.class;
//            } else if (StrUtil.equals("操作日志", title)) {
//                clazz = OperLogView.class;
//            } else if (StrUtil.equals("登录日志", title)) {
//                clazz = LoginInforView.class;
//            } else if (StrUtil.equals("服务监控", title)) {
//                clazz = MonitorView.class;
//            } else if (StrUtil.equals("若依官网", title)) {
//                title = "主页2";
//                iconStr = "home";
//                clazz = CountryDashboardView.class;
            } else {

                if (component.contains("/")) {
//                    clazz = ToolView.class;
                } else {

                    try {
                        clazz = Class.forName(component);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
            }


            Node node = null;

            if (clazz == Boolean.class) {
//                ViewTuple<ToolView, ToolViewModel> viewTuple = FluentViewLoader.fxmlView(ToolView.class).load();
//                viewTuple.getViewModel().setUrl("http://vue.ruoyi.vip/" + ((JSONObject) obj).getStr("component").replace("/index", "").replace("/list", "List"));
//                MvvmFX.getNotificationCenter().publish("addTab", "若依演示", "", viewTuple.getView());
//                return;
            } else {
                node = FluentViewLoader.fxmlView(clazz).load().getView();
            }
            tab = new Tab(title);
            tab.setId("main-tab");
            try {
                tab.setGraphic(FontIcon.of(Feather.findByDescription(iconStr)));
            } catch (Exception e) {
                tab.setGraphic(FontIcon.of(Feather.STAR));
            }
            node.setStyle("-fx-padding: 10px");
//            tab.setGraphic(new FontIcon(WIcon.findByDescription("lw-" + iconStr)));
            tab.setContent(node);
            tabPane.getTabs().add(tab);

        }

        tabPane.getSelectionModel().select(tab);

        FadeTransition fadeTransition = new FadeTransition(Duration.millis(400), tab.getContent());
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
        fadeTransition.play();
    }

    private void loddTab(String title, Ikon icon, Boolean colse, Parent node) {
        node.setStyle("-fx-padding: 10px");
        Tab tab = null;
        var tabOptional = tabPane.getTabs().stream()
                .filter(t -> StrUtil.equals(t.getText(), title))
                .findFirst();

        if (tabOptional.isPresent()) {
            tab = tabOptional.get();
            tabPane.getTabs().remove(tab);
        }
        tab = new Tab(title);
        tab.setClosable(colse);
        tab.setId("main-tab");
        FontIcon fontIcon = new FontIcon(icon);
        fontIcon.setIconSize(24);
        tab.setGraphic(fontIcon);
        tabPane.getTabs().add(tab);
        tab.setContent(node);
        tabPane.getSelectionModel().select(tab);
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(400), tab.getContent());
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
        fadeTransition.play();
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

    private boolean isOnTab(MouseEvent event) {
        // 检查点击是否在TabPane顶部的60像素范围内
        double mouseY = event.getY();
        return mouseY >= 0 && mouseY <= 60;
    }

}

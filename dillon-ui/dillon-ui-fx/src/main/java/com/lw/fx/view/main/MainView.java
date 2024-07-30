package com.lw.fx.view.main;

import animatefx.animation.AnimateFXInterpolator;
import animatefx.animation.AnimationFX;
import animatefx.animation.FadeIn;
import animatefx.animation.FadeOut;
import animatefx.util.ParallelAnimationFX;
import atlantafx.base.controls.Popover;
import atlantafx.base.theme.Tweaks;
import cn.hutool.core.util.StrUtil;
import com.dlsc.gemsfx.AvatarView;
import com.goxr3plus.fxborderlessscene.borderless.BorderlessScene;
import com.lw.dillon.admin.module.system.controller.admin.auth.vo.AuthPermissionInfoRespVO;
import com.lw.fx.util.Lazy;
import com.lw.fx.view.general.ThemePage;
import com.lw.fx.view.home.DashboardView;
import com.lw.fx.view.home.DashboardViewModel;
import com.lw.fx.view.system.config.UserInfoView;
import com.lw.fx.view.system.config.UserInfoViewModel;
import com.lw.fx.view.system.menu.MenuManageView;
import de.saxsys.mvvmfx.*;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2AL;

import java.net.URL;
import java.util.ResourceBundle;

import static atlantafx.base.theme.Styles.*;

public class MainView implements FxmlView<MainViewModel>, Initializable {

    @FXML
    private BorderPane rootPane;
    @FXML
    private Button themeBut;
    @FXML
    private AvatarView userBut;
    @FXML
    private Button minimizeBut;
    @FXML
    private Button maximizeBut;
    @FXML
    private Button noticeBut;
    @FXML
    private Label tagLabel;
    @FXML
    private Button closeBut;
    @FXML
    private Button logoBut;
    @FXML
    private ToggleButton menuButton;
    @FXML
    private TabPane tabPane;
    @FXML
    private VBox sideBox;

    private Popover popover;

    private NavTree<AuthPermissionInfoRespVO.MenuVO> sideMenu;

    private Lazy<ThemeDialog> themeDialog;

    @InjectViewModel
    private MainViewModel mainViewModel;

    public MainView() {
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        popover = new Popover();
        popover.setHeaderAlwaysVisible(false);
        popover.setArrowLocation(Popover.ArrowLocation.TOP_CENTER);
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
        rootPane.getStyleClass().add("main-view");
        sideMenu = new NavTree<>();
        VBox.setVgrow(sideMenu, Priority.ALWAYS);
        sideBox.getChildren().add(sideMenu);
        menuButton.selectedProperty().addListener((observableValue, aBoolean, newValue) -> {
//            sideMenu.expansion(newValue);
        });

        sideMenu.rootProperty().bind(mainViewModel.treeItemObjectPropertyProperty());
        sideMenu.setShowRoot(false);
        // 使用 CSS 隐藏列标题
        themeDialog = new Lazy<>(() -> {
            var dialog = new ThemeDialog();
            dialog.setClearOnClose(true);
            return dialog;
        });

        // 设置选择事件监听器
        sideMenu.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                AuthPermissionInfoRespVO.MenuVO selectedMenu = newValue.getValue();
                if (StrUtil.isNotBlank(selectedMenu.getComponentFx())) {
                    loddTab(selectedMenu);
                }
            }
        });

        Region theme = new Region();
        theme.getStyleClass().add("theme-icon");

        Region minus = new Region();
        minus.getStyleClass().add("minus-icon");

        Region resizeMax = new Region();
        resizeMax.getStyleClass().add("resize-max-icon");

        Region resizeMin = new Region();
        resizeMin.getStyleClass().add("resize-min-icon");

        Region close = new Region();
        close.getStyleClass().add("close-icon");

        mainViewModel.maximizedProperty().addListener((observableValue, oldVal, newVal) -> {
            maximizeBut.setTooltip(new Tooltip(newVal ? "还原" : "最大化"));
            maximizeBut.setGraphic(newVal ? resizeMin : resizeMax);

        });


        logoBut.textProperty().bindBidirectional(mainViewModel.titleProperty());
        logoBut.getStyleClass().addAll(BUTTON_OUTLINED, FLAT, "title-1");
        userBut.initialsProperty().bind(mainViewModel.nickNameProperty());
        themeBut.setGraphic(theme);
        themeBut.setOnAction(e -> openThemeDialog());
        userBut.setOnMouseClicked(event -> showPersonalInformation());
        minimizeBut.setOnAction(actionEvent -> {

            if (minimizeBut.getScene() instanceof BorderlessScene) {
                ((BorderlessScene) maximizeBut.getScene()).minimizeStage();
            }
        });
        minimizeBut.setGraphic(minus);

        maximizeBut.setGraphic(resizeMax);
        maximizeBut.setOnAction(actionEvent -> {

            if (maximizeBut.getScene() instanceof BorderlessScene) {

                ((BorderlessScene) maximizeBut.getScene()).maximizeStage();
            }
        });

        closeBut.setGraphic(close);
        closeBut.setOnAction(actionEvent -> {
            mainViewModel.loginOut(true);
        });


        menuButton.setSelected(true);
        menuButton.setGraphic(new FontIcon(Feather.MENU));
        menuButton.getStyleClass().addAll(FLAT, BUTTON_CIRCLE);
        initListeners();


        noticeBut.setOnMouseClicked(actionEvent ->{
            ViewTuple<MessageView, MessageViewModel> viewTuple = FluentViewLoader.fxmlView(MessageView.class).load();
            popover.setContentNode(viewTuple.getView());
            popover.show(noticeBut);
        } );
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(400), rootPane);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
        fadeTransition.play();

        ViewTuple<DashboardView, DashboardViewModel> viewTuple = FluentViewLoader.fxmlView(DashboardView.class).load();
        loddTab("主页", Material2AL.HOME, false, viewTuple.getView());
        MvvmFX.getNotificationCenter().subscribe("showThemePage", (key, payload) -> {
            // trigger some actions
            Platform.runLater(() -> {
                loddTab("个性化设置", Material2AL.APP_SETTINGS_ALT, true, new ThemePage());

            });
        });
        toggleStyleClass(sideMenu, Tweaks.ALT_ICON);

        tagLabel.visibleProperty().bind(Bindings.createBooleanBinding( () -> {
                    String text = mainViewModel.unreadCountProperty().get();
                    return text != null && !text.isEmpty();
                },
                mainViewModel.unreadCountProperty()));
        tagLabel.textProperty().bindBidirectional(mainViewModel.unreadCountProperty());
    }


    private void initListeners() {
        menuButton.setOnAction(actionEvent -> {

            if (menuButton.isSelected()) {
                AnimationFX animationFX = new FadeOut(sideBox);
                Timeline timeline = new Timeline(new KeyFrame(Duration.millis(0),
                        new KeyValue(sideBox.prefWidthProperty(), 0, AnimateFXInterpolator.EASE)),
                        new KeyFrame(Duration.millis(200),
                                new KeyValue(sideBox.prefWidthProperty(), 300D, AnimateFXInterpolator.EASE)));

                animationFX.setTimeline(timeline);
                ParallelAnimationFX parallelAnimationFX
                        = new ParallelAnimationFX(new FadeIn(sideBox), animationFX);
                parallelAnimationFX.play();
            } else {
                AnimationFX animationFX = new FadeOut(sideBox);
                Timeline timeline = new Timeline(new KeyFrame(Duration.millis(0),
                        new KeyValue(sideBox.prefWidthProperty(), 300D, AnimateFXInterpolator.EASE)),
                        new KeyFrame(Duration.millis(200),
                                new KeyValue(sideBox.prefWidthProperty(), 0, AnimateFXInterpolator.EASE)));

                animationFX.setTimeline(timeline);
                ParallelAnimationFX parallelAnimationFX
                        = new ParallelAnimationFX(new FadeIn(sideBox), animationFX);
                parallelAnimationFX.play();
            }
        });
        mainViewModel.addTabProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                loddTab(newValue);
            }
        });
        MvvmFX.getNotificationCenter().subscribe("addTab", (key, payload) -> {

            // trigger some actions
            Platform.runLater(() -> {
                loddTab((String) payload[0], (Ikon) payload[1], true, (Parent) payload[2]);

            });

        });


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

    private void showPersonalInformation() {
        FontIcon fontIcon1 = new FontIcon(Feather.SETTINGS);
        fontIcon1.setIconSize(24);
        FontIcon fontIcon2 = new FontIcon(Feather.LOG_OUT);
        fontIcon2.setIconSize(24);
        ContextMenu contextMenu = new ContextMenu();
        MenuItem menuItem1 = new MenuItem("个人中心");
        menuItem1.setId("menuItem");
        menuItem1.setGraphic(fontIcon1);
        MenuItem menuItem2 = new MenuItem("退出系统");
        menuItem2.setGraphic(fontIcon2);
        menuItem2.setId("menuItem");
        contextMenu.getItems().addAll(menuItem1, new SeparatorMenuItem(), menuItem2);
        Bounds bounds = userBut.localToScreen(userBut.getBoundsInLocal());
        contextMenu.show(userBut, bounds.getMinX(), bounds.getMinY() + bounds.getHeight());

        menuItem2.setOnAction(actionEvent -> mainViewModel.loginOut(false));
        menuItem1.setOnAction(actionEvent -> {

            ViewTuple<UserInfoView, UserInfoViewModel> viewTuple = FluentViewLoader.fxmlView(UserInfoView.class).load();
            MvvmFX.getNotificationCenter().publish("addTab", "个人中心", Feather.USER, viewTuple.getView());
        });


    }


    private void openThemeDialog() {
        var dialog = themeDialog.get();
        dialog.show(rootPane.getScene());
        Platform.runLater(dialog::requestFocus);
    }

}

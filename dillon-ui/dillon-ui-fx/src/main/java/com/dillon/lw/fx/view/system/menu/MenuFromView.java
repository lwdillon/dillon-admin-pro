package com.dillon.lw.fx.view.system.menu;

import atlantafx.base.controls.CustomTextField;
import atlantafx.base.controls.Popover;
import atlantafx.base.controls.ToggleSwitch;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.NumberUtil;
import com.dillon.lw.module.system.controller.admin.permission.vo.menu.MenuSimpleRespVO;
import com.dillon.lw.fx.mvvm.base.BaseView;
import com.dillon.lw.fx.view.layout.IconBrowser;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2MZ;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

/**
 * 视图菜单对话框
 *
 * @author wenli
 * @date 2023/02/15
 */
public class MenuFromView extends BaseView<MenuFromViewModel> {

    private Popover popover;
    private TreeView<MenuSimpleRespVO> menuTree;
    private Node iconBrowser;

    @FXML
    private HBox alwaysShowBox;
    @FXML
    private ToggleSwitch alwaysShowToggleBut;
    @FXML
    private ToggleButton buttonToggleBut;
    @FXML
    private HBox componentBox;
    @FXML
    private HBox componentNameBox;
    @FXML
    private TextField componentNameTextField;
    @FXML
    private TextField componentTextField;
    @FXML
    private ToggleButton dirToggleBut;
    @FXML
    private HBox iconBox;
    @FXML
    private TextField iconTextField;
    @FXML
    private HBox keepAliveBox;
    @FXML
    private ToggleSwitch keepAliveToggleBut;
    @FXML
    private ToggleButton menuToggleBut;
    @FXML
    private CustomTextField menuTreeTextField;
    @FXML
    private TextField nameTextField;
    @FXML
    private HBox pathBox;
    @FXML
    private TextField pathTextField;
    @FXML
    private HBox permissionBox;
    @FXML
    private TextField permissionTextField;
    @FXML
    private VBox rootBox;
    @FXML
    private HBox sortBox;
    @FXML
    private Spinner<Integer> sortNumFeild;
    @FXML
    private HBox statusBox;
    @FXML
    private ToggleSwitch statusToggleBut;
    @FXML
    private ToggleGroup toggleGroup1;
    @FXML
    private HBox visibleBox;
    @FXML
    private ToggleSwitch visibleToggleBut;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initIconPopover();
        initMenuTreePopover();
        bindProperties();
        bindVisibility();
        bindToggleListeners();
    }

    private void initMenuTreePopover() {
        menuTreeTextField.setRight(FontIcon.of(Feather.CHEVRON_DOWN));
        menuTree = new TreeView<>();
        popover = new Popover();
        popover.setHeaderAlwaysVisible(false);
        popover.setArrowLocation(Popover.ArrowLocation.TOP_CENTER);

        menuTree.setCellFactory(cell -> new TreeCell<>() {
            @Override
            protected void updateItem(MenuSimpleRespVO item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName());
            }
        });

        LongProperty parentIdProperty = viewModel.parentIdProperty();

        menuTree.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                viewModel.selectTreeItemProperty().set(newVal);
                parentIdProperty.set(newVal.getValue().getId());
                menuTreeTextField.setText(newVal.getValue().getName());
            }
            popover.hide();
        });

        viewModel.selectTreeItemProperty().addListener((obs, oldVal, newVal) -> menuTree.getSelectionModel().select(newVal));

        menuTree.rootProperty().bind(viewModel.menuTreeRootProperty());
        menuTreeTextField.setEditable(false);
        menuTreeTextField.setOnMouseClicked(event -> {
            menuTree.setPrefWidth(menuTreeTextField.getWidth() - 50);
            popover.setContentNode(menuTree);
            popover.show(menuTreeTextField);
        });
    }

    private void initIconPopover() {
        iconBrowser = createIconBrowser();
        iconTextField.setOnMouseClicked(event -> {
            iconBrowser.maxWidth(iconTextField.getWidth());
            popover.setContentNode(iconBrowser);
            popover.show(iconTextField);
        });
    }

    private Node createIconBrowser() {
        CustomTextField filterText = new CustomTextField();
        filterText.setLeft(new FontIcon(Material2MZ.SEARCH));
        filterText.setPrefWidth(300);

        HBox filterBox = new HBox(filterText);
        filterBox.setAlignment(Pos.CENTER);

        List<Ikon> icons = new ArrayList<>(Arrays.asList(Feather.values()));
        IconBrowser browser = new IconBrowser(5, icons);
        VBox.setVgrow(browser, Priority.ALWAYS);

        browser.filterProperty().bind(filterText.textProperty());
        browser.setMinHeight(500);
        browser.setMinWidth(600);
        browser.valueProperty().addListener((obs, oldVal, newVal) -> {
            iconTextField.setText(newVal.getDescription());
            popover.hide();
        });

        return new VBox(10, filterBox, browser);
    }

    private void bindProperties() {
        nameTextField.textProperty().bindBidirectional(viewModel.nameProperty());
        iconTextField.textProperty().bindBidirectional(viewModel.iconProperty());
        pathTextField.textProperty().bindBidirectional(viewModel.pathProperty());
        componentTextField.textProperty().bindBidirectional(viewModel.componentProperty());
        componentNameTextField.textProperty().bindBidirectional(viewModel.componentNameProperty());
        permissionTextField.textProperty().bindBidirectional(viewModel.permissionProperty());
        visibleToggleBut.selectedProperty().bindBidirectional(viewModel.visibleProperty());
        alwaysShowToggleBut.selectedProperty().bindBidirectional(viewModel.alwaysShowProperty());
        keepAliveToggleBut.selectedProperty().bindBidirectional(viewModel.keepAliveProperty());

        IntegerProperty typeProperty = viewModel.typeProperty();
        dirToggleBut.setUserData(1);
        menuToggleBut.setUserData(2);
        buttonToggleBut.setUserData(3);

        toggleGroup1.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle != null) {
                typeProperty.set(Convert.toInt(newToggle.getUserData()));
            }
        });

        typeProperty.addListener((obs, oldVal, newVal) -> {
            if (NumberUtil.equals(newVal, 1)) {
                dirToggleBut.setSelected(true);
            } else if (NumberUtil.equals(newVal, 2)) {
                menuToggleBut.setSelected(true);
            } else {
                buttonToggleBut.setSelected(true);
            }
        });

        IntegerProperty sortProperty = viewModel.sortProperty();
        sortNumFeild.valueProperty().addListener((obs, oldVal, newVal) -> sortProperty.set(newVal));
        sortProperty.addListener((obs, oldVal, newVal) ->
                sortNumFeild.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, (Integer) newVal))
        );

        IntegerProperty statusProperty = viewModel.statusProperty();
        statusProperty.addListener((obs, oldVal, newVal) -> statusToggleBut.setSelected(NumberUtil.equals(newVal, 0)));
        statusToggleBut.selectedProperty().addListener((obs, oldVal, newVal) -> statusProperty.set(newVal ? 0 : 1));
    }

    private void bindVisibility() {
        iconBox.managedProperty().bind(iconBox.visibleProperty());
        pathBox.managedProperty().bind(pathBox.visibleProperty());
        componentBox.managedProperty().bind(componentBox.visibleProperty());
        componentNameBox.managedProperty().bind(componentNameBox.visibleProperty());
        permissionBox.managedProperty().bind(permissionBox.visibleProperty());
        sortBox.managedProperty().bind(sortBox.visibleProperty());
        statusBox.managedProperty().bind(statusBox.visibleProperty());
        visibleBox.managedProperty().bind(visibleBox.visibleProperty());
        alwaysShowBox.managedProperty().bind(alwaysShowBox.visibleProperty());
        keepAliveBox.managedProperty().bind(keepAliveBox.visibleProperty());
    }

    private void bindToggleListeners() {
        buttonToggleBut.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) showButton();
        });
        menuToggleBut.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) showMenu();
        });
        dirToggleBut.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) showDirectory();
        });
    }

    private void showDirectory() {
        iconBox.setVisible(true);
        pathBox.setVisible(true);
        componentBox.setVisible(false);
        componentNameBox.setVisible(false);
        permissionBox.setVisible(false);
        sortBox.setVisible(true);
        statusBox.setVisible(true);
        visibleBox.setVisible(true);
        alwaysShowBox.setVisible(true);
        keepAliveBox.setVisible(false);
    }

    private void showMenu() {
        iconBox.setVisible(true);
        pathBox.setVisible(true);
        componentBox.setVisible(true);
        componentNameBox.setVisible(true);
        permissionBox.setVisible(true);
        sortBox.setVisible(true);
        statusBox.setVisible(true);
        visibleBox.setVisible(true);
        alwaysShowBox.setVisible(true);
        keepAliveBox.setVisible(true);
    }

    private void showButton() {
        iconBox.setVisible(false);
        pathBox.setVisible(false);
        componentBox.setVisible(false);
        componentNameBox.setVisible(false);
        permissionBox.setVisible(true);
        sortBox.setVisible(true);
        statusBox.setVisible(true);
        visibleBox.setVisible(false);
        alwaysShowBox.setVisible(false);
        keepAliveBox.setVisible(false);
    }

    @Override
    public void onRemove() {
        super.onRemove();
    }
}

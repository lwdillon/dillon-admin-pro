package com.lw.fx.view.system.menu;

import atlantafx.base.controls.CustomTextField;
import atlantafx.base.controls.Popover;
import atlantafx.base.controls.ToggleSwitch;
import atlantafx.base.theme.Tweaks;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.NumberUtil;
import com.lw.dillon.admin.module.system.controller.admin.permission.vo.menu.MenuSimpleRespVO;
import com.lw.fx.view.control.IconBrowser;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
import java.util.ResourceBundle;

import static atlantafx.base.theme.Styles.toggleStyleClass;

/**
 * 视图菜单对话框
 *
 * @author wenli
 * @date 2023/02/15
 */
public class MenuFromView implements FxmlView<MenuFromViewModel>, Initializable {

    @InjectViewModel
    private MenuFromViewModel viewModel;

    private Popover popover;
    private TreeView<MenuSimpleRespVO> menuTree;
    @FXML
    private ToggleSwitch alwaysShowToggleBut;

    @FXML
    private ToggleButton buttonToggleBut;

    @FXML
    private TextField componentNameTextField;

    @FXML
    private TextField componentTextField;

    @FXML
    private ToggleButton dirToggleBut;

    @FXML
    private TextField iconTextField;

    @FXML
    private ToggleSwitch keepAliveToggleBut;

    @FXML
    private ToggleButton menuToggleBut;

    @FXML
    private CustomTextField menuTreeTextField;

    @FXML
    private TextField nameTextField;

    @FXML
    private TextField pathTextField;

    @FXML
    private TextField permissionTextField;

    @FXML
    private Spinner<Integer> sortNumFeild;

    @FXML
    private ToggleSwitch statusToggleBut;

    @FXML
    private ToggleGroup toggleGroup1;

    @FXML
    private ToggleSwitch visibleToggleBut;
    @FXML
    private VBox rootBox;

    private Node iconBrowser;

    /**
     * 初始化
     *
     * @param url            url
     * @param resourceBundle 资源包
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        menuTreeTextField.setRight(FontIcon.of(Feather.CHEVRON_DOWN));
        menuTree = new TreeView<>();
        iconBrowser = iconBrowser();
        popover = new Popover();
        popover.setHeaderAlwaysVisible(false);
        popover.setArrowLocation(Popover.ArrowLocation.TOP_CENTER);
        menuTree.setCellFactory(cell -> new TreeCell<>() {
            @Override
            protected void updateItem(MenuSimpleRespVO menuSimpleRespVO, boolean empty) {
                super.updateItem(menuSimpleRespVO, empty);
                if (menuSimpleRespVO == null || empty) {
                    setText(null);
                } else {
                    setText(menuSimpleRespVO.getName());
                }
            }
        });
        menuTree.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                viewModel.selectTreeItemProperty().setValue(newValue);
                viewModel.parentIdProperty().set(newValue.getValue().getId());
                menuTreeTextField.setText(newValue.getValue().getName());
            }
            if (popover != null) {
                popover.hide();
            }

        });
        viewModel.selectTreeItemProperty().addListener((observableValue, menuSimpleRespVOTreeItem, t1) -> menuTree.getSelectionModel().select(t1));

        menuTreeTextField.setEditable(false);
        menuTreeTextField.setOnMouseClicked(actionEvent -> {
            menuTree.setPrefWidth(menuTreeTextField.getWidth() - 50);
            popover.setContentNode(menuTree);
            popover.show(menuTreeTextField);
            viewModel.selectTreeItemProperty().get().setExpanded(true);
        });
        iconTextField.setOnMouseClicked(actionEvent -> {
            iconBrowser.maxWidth(iconTextField.getWidth());
            popover.setContentNode(iconBrowser);
            popover.show(iconTextField);
            viewModel.selectTreeItemProperty().get().setExpanded(true);
        });
        nameTextField.textProperty().bindBidirectional(viewModel.nameProperty());
        menuTree.rootProperty().bind(viewModel.menuTreeRootProperty());
        toggleGroup1.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (toggleGroup1.getSelectedToggle() != null) {
                viewModel.menuTypeProperty().set(Convert.toInt(toggleGroup1.getSelectedToggle().getUserData()));
            }
        });
        dirToggleBut.setUserData(1);
        menuToggleBut.setUserData(2);
        buttonToggleBut.setUserData(3);
        viewModel.menuTypeProperty().addListener((observable, oldValue, newValue) -> {
            if (NumberUtil.equals(newValue, 1)) {
                dirToggleBut.setSelected(true);
            } else if (NumberUtil.equals(2, newValue)) {
                menuToggleBut.setSelected(true);
            } else {
                buttonToggleBut.setSelected(true);
            }
        });

        sortNumFeild.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, 0));
        viewModel.sortProperty().addListener((observable, oldValue, newValue) -> {
            sortNumFeild.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, newValue.intValue()));
        });

        viewModel.sortProperty().addListener((observable, oldValue, newValue) -> viewModel.sortProperty().setValue(newValue));
        viewModel.statusProperty().addListener((observableValue, number, t1) -> statusToggleBut.setSelected(NumberUtil.equals(t1, 0)));
        statusToggleBut.selectedProperty().addListener((observableValue, aBoolean, t1) -> viewModel.statusProperty().set(t1 ? 0 : 1));
        iconTextField.textProperty().bindBidirectional(viewModel.iconProperty());
        pathTextField.textProperty().bindBidirectional(viewModel.pathProperty());
        componentTextField.textProperty().bindBidirectional(viewModel.componentProperty());
        componentNameTextField.textProperty().bindBidirectional(viewModel.componentNameProperty());
        permissionTextField.textProperty().bindBidirectional(viewModel.permissionProperty());
        visibleToggleBut.selectedProperty().bindBidirectional(viewModel.visibleProperty());
        alwaysShowToggleBut.selectedProperty().bindBidirectional(viewModel.alwayShowProperty());
        keepAliveToggleBut.selectedProperty().bindBidirectional(viewModel.keepAliveProperty());

        toggleStyleClass(menuTree, Tweaks.ALT_ICON);
    }


    private Node iconBrowser() {


        var filterText = new CustomTextField();
        filterText.setLeft(new FontIcon(Material2MZ.SEARCH));
        filterText.setPrefWidth(300);

        var filterBox = new HBox(filterText);
        filterBox.setAlignment(Pos.CENTER);

        var icons = new ArrayList<Ikon>();
        icons.addAll(Arrays.asList(Feather.values()));
        var iconBrowser = new IconBrowser(5, icons);
        VBox.setVgrow(iconBrowser, Priority.ALWAYS);
        iconBrowser.filterProperty().bind(filterText.textProperty());
        iconBrowser.setMinHeight(500);
        iconBrowser.setMinWidth(600);
        iconBrowser.valueProperty().addListener((observableValue, ikon, t1) -> {
            iconTextField.setText(t1.getDescription());
            popover.hide();
        });
        return new VBox(10, filterBox, iconBrowser);
    }


}

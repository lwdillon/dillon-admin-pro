/* SPDX-License-Identifier: MIT */

package com.dillon.lw.fx.view.layout;

import atlantafx.base.controls.CustomTextField;
import atlantafx.base.controls.Spacer;
import atlantafx.base.theme.Styles;
import atlantafx.base.theme.Tweaks;
import com.dillon.lw.fx.view.main.MainViewModel;
import com.dillon.lw.module.system.controller.admin.auth.vo.AuthPermissionInfoRespVO;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TreeItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2AL;
import org.kordamp.ikonli.material2.Material2MZ;

import java.util.function.Consumer;

public final class SearchDialog extends ModalDialog {


    private CustomTextField searchField;
    private ListView<TreeItem<AuthPermissionInfoRespVO.MenuVO>> resultList;

    private MainViewModel mainViewModel;

    public SearchDialog(MainViewModel mainViewModel) {
        super();

        this.mainViewModel = mainViewModel;

//        setId("search-dialog");
        header.setTitle("查找");
        header.setGraphic(new FontIcon(Feather.SEARCH));
        content.setBody(createContent());
        content.setFooter(createDefaultFooter());
        content.setPrefSize(600, 440);

        init();
    }

    private VBox createContent() {
        var placeholder = new Label("您的搜索结果将在此处显示");
        placeholder.getStyleClass().add(Styles.TITLE_4);

        searchField = new CustomTextField();
        searchField.setLeft(new FontIcon(Material2MZ.SEARCH));
        VBox.setVgrow(searchField, Priority.NEVER);

        Consumer<TreeItem<AuthPermissionInfoRespVO.MenuVO>> clickHandler = item -> {
            if (item.getValue() != null) {
                close();
                mainViewModel.navigate(item);
            }
        };

        resultList = new ListView<>();
        resultList.setPlaceholder(placeholder);
        resultList.getStyleClass().add(Tweaks.EDGE_TO_EDGE);
        resultList.setCellFactory(c -> new ResultListCell(clickHandler));
        VBox.setVgrow(resultList, Priority.ALWAYS);
        VBox box = new VBox(10, searchField, resultList);
        box.setAlignment(Pos.TOP_CENTER);
        return box;
    }

    private void init() {
        searchField.textProperty().addListener((obs, old, val) -> {
            if (val == null || val.length() < 2) {
                resultList.getItems().clear();
                return;
            }

            resultList.getItems().setAll(mainViewModel.findTreeItemsById(val));
        });

        searchField.addEventFilter(KeyEvent.KEY_RELEASED, e -> {
            if (e.getCode() == KeyCode.DOWN && !resultList.getItems().isEmpty()) {
                resultList.getSelectionModel().selectFirst();
                resultList.requestFocus();
            }
        });

        resultList.setOnKeyPressed(e -> {
            var selectionModel = resultList.getSelectionModel();
            if (e.getCode() == KeyCode.ENTER && !selectionModel.isEmpty()) {
                close();
                mainViewModel.navigate(selectionModel.getSelectedItem());
            }
        });
    }

    public void begForFocus() {
        searchField.requestFocus();
    }

    /// ////////////////////////////////////////////////////////////////////////

    public static final class ResultListCell extends ListCell<TreeItem<AuthPermissionInfoRespVO.MenuVO>> {

        private final HBox root;
        private final Label parentLabel;
        private final Label targetLabel;

        public ResultListCell(Consumer<TreeItem<AuthPermissionInfoRespVO.MenuVO>> clickHandler) {
            super();

            parentLabel = new Label();
            parentLabel.getStyleClass().add(Styles.TEXT_MUTED);

            var separatorIcon = new FontIcon(Material2AL.CHEVRON_RIGHT);
            separatorIcon.getStyleClass().add("icon-subtle");

            var returnIcon = new FontIcon(Material2AL.KEYBOARD_RETURN);
            returnIcon.getStyleClass().add("icon-subtle");

            targetLabel = new Label();
            targetLabel.getStyleClass().add(Styles.TEXT_BOLD);

            root = new HBox(parentLabel, separatorIcon, targetLabel, new Spacer(), returnIcon);
            root.setAlignment(Pos.CENTER_LEFT);

            setOnMouseClicked(e -> {
                if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2) {
                    clickHandler.accept(getItem());
                }
            });
        }

        @Override
        protected void updateItem(TreeItem<AuthPermissionInfoRespVO.MenuVO> item, boolean empty) {
            super.updateItem(item, empty);

            if (item == null || empty) {
                setGraphic(null);
            } else {
                parentLabel.setGraphic(new FontIcon(Material2MZ.STAR));
                parentLabel.setText(item.getParent().getValue().getName());
                targetLabel.setGraphic(new FontIcon(Feather.STAR));
                targetLabel.setText(item.getValue().getName());
                setGraphic(root);
            }
        }
    }


}

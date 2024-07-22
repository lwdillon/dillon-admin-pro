/* SPDX-License-Identifier: MIT */

package com.lw.fx.view.main;

import atlantafx.base.controls.Spacer;
import atlantafx.base.theme.Tweaks;
import cn.hutool.core.collection.CollUtil;
import com.lw.dillon.admin.module.system.controller.admin.auth.vo.AuthPermissionInfoRespVO;
import com.lw.fx.util.NodeUtils;
import javafx.css.PseudoClass;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

public final class NavTree<T> extends TreeView<AuthPermissionInfoRespVO.MenuVO> {

    public NavTree() {
        super();

        getStyleClass().addAll(Tweaks.EDGE_TO_EDGE);
        setShowRoot(false);
        setCellFactory(p -> new NavTreeCell());
    }

    ///////////////////////////////////////////////////////////////////////////

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
            root.getChildren().setAll(titleLabel, new Spacer(), tagLabel,arrowIcon);
            root.setCursor(Cursor.HAND);
            root.getStyleClass().add("container");
            root.setMaxWidth(300);

            root.setOnMouseClicked(e -> {

                TreeItem item=getTreeItem();

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

                try {
                    titleLabel.setGraphic(FontIcon.of(Feather.findByDescription(nav.getIconFont())));
                } catch (Exception e) {
                    titleLabel.setGraphic(FontIcon.of(Feather.STAR));
                }


                boolean group= CollUtil.isNotEmpty(nav.getChildren());
                pseudoClassStateChanged(GROUP, group);
                NodeUtils.toggleVisibility(arrowIcon, group);
                NodeUtils.toggleVisibility(tagLabel, group);
            }
        }
    }


}

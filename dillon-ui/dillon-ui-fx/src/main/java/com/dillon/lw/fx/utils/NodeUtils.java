/* SPDX-License-Identifier: MIT */

package com.dillon.lw.fx.utils;

import com.dillon.lw.fx.icon.WIcon;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.List;
import java.util.Objects;

public final class NodeUtils {

    public static void toggleVisibility(Node node, boolean on) {
        node.setVisible(on);
        node.setManaged(on);
    }

    public static void setAnchors(Node node, Insets insets) {
        if (insets.getTop() >= 0) {
            AnchorPane.setTopAnchor(node, insets.getTop());
        }
        if (insets.getRight() >= 0) {
            AnchorPane.setRightAnchor(node, insets.getRight());
        }
        if (insets.getBottom() >= 0) {
            AnchorPane.setBottomAnchor(node, insets.getBottom());
        }
        if (insets.getLeft() >= 0) {
            AnchorPane.setLeftAnchor(node, insets.getLeft());
        }
    }

    public static void setScrollConstraints(ScrollPane scrollPane,
                                            ScrollPane.ScrollBarPolicy vbarPolicy, boolean fitHeight,
                                            ScrollPane.ScrollBarPolicy hbarPolicy, boolean fitWidth) {
        scrollPane.setVbarPolicy(vbarPolicy);
        scrollPane.setFitToHeight(fitHeight);
        scrollPane.setHbarPolicy(hbarPolicy);
        scrollPane.setFitToWidth(fitWidth);
    }

    public static boolean isDoubleClick(MouseEvent e) {
        return e.getButton().equals(MouseButton.PRIMARY) && e.getClickCount() == 2;
    }

    public static <T> T getChildByIndex(Parent parent, int index, Class<T> contentType) {
        List<Node> children = parent.getChildrenUnmodifiable();
        if (index < 0 || index >= children.size()) {
            return null;
        }
        Node node = children.get(index);
        return contentType.isInstance(node) ? contentType.cast(node) : null;
    }

    public static boolean isDescendant(Node ancestor, Node descendant) {
        if (ancestor == null) {
            return true;
        }

        while (descendant != null) {
            if (descendant == ancestor) {
                return true;
            }
            descendant = descendant.getParent();
        }
        return false;
    }
    public static void addStyleClass(TableColumn<?, ?> c, String styleClass, String... excludes) {
        Objects.requireNonNull(c);
        Objects.requireNonNull(styleClass);

        if (excludes != null && excludes.length > 0) {
            c.getStyleClass().removeAll(excludes);
        }
        c.getStyleClass().add(styleClass);
    }

    public static FontIcon getIcon(String icon) {
        try {
            String[] iconStr = icon.split(":");
            if (iconStr.length > 1) {
                icon = "lw-"+iconStr[1];
            }
            if(icon.startsWith("fth-")) {
                return FontIcon.of(Feather.findByDescription( icon));
            }
            return FontIcon.of(WIcon.findByDescription( icon));
        } catch (Exception e) {
            return FontIcon.of(Feather.STAR);
        }
    }
}

package com.dillon.lw.fx.view.layout;

import atlantafx.base.controls.ModalPane;
import atlantafx.base.theme.Styles;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.function.Consumer;

public class ConfirmDialog {

    private final VBox dialog;
    private final VBox contentBox;
    private final ModalPane modalPane;

    private ConfirmDialog(Builder builder) {
        this.modalPane = builder.modalPane;

        Label titleLabel = new Label(builder.title);
        titleLabel.getStyleClass().add("title-4");
        titleLabel.setGraphic(new FontIcon(Feather.INFO));
        titleLabel.setMaxWidth(Double.MAX_VALUE);

        Button closeBtn = new Button(null, new FontIcon(Feather.X));
        closeBtn.getStyleClass().addAll(Styles.BUTTON_ICON, Styles.FLAT, Styles.DANGER);
        closeBtn.setOnAction(event -> close());

        HBox titleBox = new HBox(titleLabel, closeBtn);
        titleBox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(titleLabel, Priority.ALWAYS);

        Node contentNode = builder.customContent != null
                ? builder.customContent
                : new Label(builder.message);

        if (contentNode instanceof Label) {
            ((Label) contentNode).getStyleClass().add("title-3");
            ((Label) contentNode).setAlignment(Pos.CENTER);
            ((Label) contentNode).setMaxWidth(Double.MAX_VALUE);
        }

        VBox.setVgrow(contentNode, Priority.ALWAYS);
        contentBox = new VBox(contentNode);
        VBox.setVgrow(contentBox, Priority.ALWAYS);

        Button okBtn = new Button(builder.okText);
        Button cancelBtn = new Button(builder.cancelText);

        HBox buttonBox = new HBox(10, cancelBtn, okBtn);
        buttonBox.setAlignment(Pos.BOTTOM_RIGHT);

        dialog = new VBox(16, titleBox, contentBox, buttonBox);
        dialog.getStyleClass().add("sample");

        if (builder.dialogWidth > 0) {
            dialog.setPrefWidth(builder.dialogWidth);
            dialog.setMaxWidth(builder.dialogWidth);
        }

        if (builder.dialogHeight > 0) {
            dialog.setPrefHeight(builder.dialogHeight);
            dialog.setMaxHeight(builder.dialogHeight);
        }

        okBtn.setOnAction(e -> {
            if (builder.onConfirm != null) builder.onConfirm.accept(this);
        });

        cancelBtn.setOnAction(e -> {
            if (builder.onCancel != null) builder.onCancel.accept(this);
        });
    }

    public void show() {
        modalPane.setPersistent(true);
        modalPane.show(dialog);

        FadeTransition mpFade = new FadeTransition(Duration.millis(300), modalPane);
        mpFade.setFromValue(0);
        mpFade.setToValue(1);

        FadeTransition fade = new FadeTransition(Duration.millis(300), dialog);
        fade.setFromValue(0);
        fade.setToValue(1);

        ScaleTransition scale = new ScaleTransition(Duration.millis(300), dialog);
        scale.setFromX(0.75);
        scale.setFromY(0.75);
        scale.setToX(1);
        scale.setToY(1);

        new ParallelTransition(mpFade,fade, scale).play();
    }

    public void close() {
        FadeTransition mpFade = new FadeTransition(Duration.millis(300), modalPane);
        mpFade.setFromValue(1);
        mpFade.setToValue(0);

        FadeTransition fade = new FadeTransition(Duration.millis(300), dialog);
        fade.setFromValue(1);
        fade.setToValue(0);

        ScaleTransition scale = new ScaleTransition(Duration.millis(300), dialog);
        scale.setFromX(1);
        scale.setFromY(1);
        scale.setToX(0.75);
        scale.setToY(0.75);

        ParallelTransition transition = new ParallelTransition(mpFade,fade, scale);
        transition.setOnFinished(e -> {
            modalPane.hide();
            modalPane.setPersistent(false);
            modalPane.setScaleX(1);
            modalPane.setScaleY(1);
        });
        transition.play();
    }

    public VBox getDialog() {
        return dialog;
    }

    public VBox getContentBox() {
        return contentBox;
    }

    // === Builder 类 ===
    public static class Builder {
        private final ModalPane modalPane;
        private String title = "提示";
        private String message = "确认操作？";
        private String okText = "确认";
        private String cancelText = "取消";
        private Node customContent;
        private double dialogWidth = 0;
        private double dialogHeight = 0;
        private Consumer<ConfirmDialog> onConfirm;
        private Consumer<ConfirmDialog> onCancel;

        public Builder(ModalPane modalPane) {
            this.modalPane = modalPane;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder okText(String okText) {
            this.okText = okText;
            return this;
        }

        public Builder cancelText(String cancelText) {
            this.cancelText = cancelText;
            return this;
        }

        public Builder content(Node content) {
            this.customContent = content;
            return this;
        }

        public Builder width(double width) {
            this.dialogWidth = width;
            return this;
        }

        public Builder height(double height) {
            this.dialogHeight = height;
            return this;
        }

        public Builder onConfirm(Consumer<ConfirmDialog> onConfirm) {
            this.onConfirm = onConfirm;
            return this;
        }

        public Builder onCancel(Consumer<ConfirmDialog> onCancel) {
            this.onCancel = onCancel;
            return this;
        }

        public ConfirmDialog build() {
            return new ConfirmDialog(this);
        }
    }
}
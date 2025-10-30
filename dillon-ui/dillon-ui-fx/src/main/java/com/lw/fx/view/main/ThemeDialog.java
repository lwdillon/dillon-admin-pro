/* SPDX-License-Identifier: MIT */

package com.lw.fx.view.main;

import com.lw.dillon.admin.module.infra.controller.admin.config.vo.ConfigRespVO;
import com.lw.dillon.admin.module.infra.controller.admin.config.vo.ConfigSaveReqVO;
import com.lw.fx.request.Request;
import com.lw.fx.store.AppStore;
import com.lw.fx.theme.SamplerTheme;
import com.lw.fx.theme.ThemeManager;
import com.lw.fx.view.control.ModalDialog;
import com.lw.ui.request.api.config.ConfigFeign;
import io.datafx.core.concurrent.ProcessChain;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

import java.util.Objects;

final class ThemeDialog extends ModalDialog {

    private final TilePane thumbnailsPane = new TilePane(20, 20);
    private final ToggleGroup thumbnailsGroup = new ToggleGroup();

    public ThemeDialog() {
        super();

        setId("theme-dialog");
        header.setTitle("Select a theme");
        content.setBody(createContent());
        content.setFooter(null);

        updateThumbnails();

        thumbnailsGroup.selectedToggleProperty().addListener((obs, old, val) -> {
            if (val != null && val.getUserData() instanceof SamplerTheme theme) {
                ThemeManager.getInstance().setTheme(theme);
                updateThemeConfig(theme);
            }
        });
    }

    private void updateThemeConfig(SamplerTheme theme){

        ProcessChain.create()
                .addSupplierInExecutor(() -> {
                    String key = "fx.theme.userid." + AppStore.getAuthPermissionInfoRespVO().getUser().getId();
                    ConfigRespVO configRespVO = Request.connector(ConfigFeign.class).getConfig(key).getCheckedData();
                    if (configRespVO == null) {
                        ConfigSaveReqVO saveReqVO = new ConfigSaveReqVO();
                        saveReqVO.setKey(key);
                        saveReqVO.setValue(theme.getName());
                        saveReqVO.setVisible(true);
                        saveReqVO.setCategory("ui");
                        saveReqVO.setName("用户主题");
                        return Request.connector(ConfigFeign.class).createConfig(saveReqVO);
                    }else {
                        ConfigSaveReqVO saveReqVO = new ConfigSaveReqVO();
                        saveReqVO.setId(configRespVO.getId());
                        saveReqVO.setName(configRespVO.getName());
                        saveReqVO.setCategory(configRespVO.getCategory());
                        saveReqVO.setValue(theme.getName());
                        saveReqVO.setKey(configRespVO.getKey());
                        saveReqVO.setVisible(true);
                        return Request.connector(ConfigFeign.class).updateConfig(saveReqVO);
                    }
                })
                .addConsumerInPlatformThread(r -> {

                    if (r.isSuccess()) {



                    }
                })
                .onException(e -> {
                    e.printStackTrace();
                })

                .run();
    }
    private VBox createContent() {
        thumbnailsPane.setAlignment(Pos.TOP_CENTER);
        thumbnailsPane.setPrefColumns(3);
        thumbnailsPane.setStyle("-color-thumbnail-border:-color-border-subtle;");

        var root = new VBox(thumbnailsPane);
        root.setPadding(new Insets(20));

        return root;
    }

    private void updateThumbnails() {
        var tm = ThemeManager.getInstance();

        thumbnailsPane.getChildren().clear();
        tm.getRepository().getAll().forEach(theme -> {
            var thumbnail = new ThemeThumbnail(theme);
            thumbnail.setToggleGroup(thumbnailsGroup);
            thumbnail.setUserData(theme);
            thumbnail.setSelected(Objects.equals(
                    tm.getTheme().getName(),
                    theme.getName()
            ));
            thumbnailsPane.getChildren().add(thumbnail);
        });
    }
}

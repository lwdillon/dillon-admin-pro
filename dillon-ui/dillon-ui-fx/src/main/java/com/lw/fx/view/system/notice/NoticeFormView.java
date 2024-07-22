package com.lw.fx.view.system.notice;

import atlantafx.base.controls.ToggleSwitch;
import cn.hutool.core.util.NumberUtil;
import com.lw.dillon.admin.module.system.controller.admin.dict.vo.data.DictDataSimpleRespVO;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.web.HTMLEditor;

import java.net.URL;
import java.util.ResourceBundle;

public class NoticeFormView implements FxmlView<NoticeFormViewModel>, Initializable {
    @InjectViewModel
    private NoticeFormViewModel viewModel;
    @FXML
    private HTMLEditor contentHtmlEditor;


    @FXML
    private ToggleSwitch statusBut;

    @FXML
    private TextField titleField;

    @FXML
    private ComboBox<DictDataSimpleRespVO> typeComboBox;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        typeComboBox.setItems(viewModel.getTypeItems());
        typeComboBox.valueProperty().bindBidirectional(viewModel.selTtemProperty());
        viewModel.contentProperty().addListener((observableValue, s, t1) -> {
            contentHtmlEditor.setHtmlText(t1);
        });

        titleField.textProperty().bindBidirectional(viewModel.titleProperty());


        viewModel.statusProperty().addListener((observableValue, number, t1) -> statusBut.setSelected(NumberUtil.equals(t1, 0)));
        statusBut.selectedProperty().addListener((observableValue, aBoolean, t1) -> viewModel.statusProperty().set(t1 ? 0 : 1));

        viewModel.subscribe("commitHtmText",(key, payload) -> {
            viewModel.contentProperty().set(contentHtmlEditor.getHtmlText());

        });
    }




}

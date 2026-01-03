package com.dillon.lw.fx.view.system.dict;

import atlantafx.base.controls.ModalPane;
import cn.hutool.core.date.DateUtil;
import com.dillon.lw.fx.eventbus.EventBusCenter;
import com.dillon.lw.fx.eventbus.event.MainTabEvent;
import com.dillon.lw.fx.mvvm.base.BaseView;
import com.dillon.lw.fx.mvvm.loader.ViewLoader;
import com.dillon.lw.fx.view.layout.ConfirmDialog;
import com.dillon.lw.fx.view.layout.PagingControl;
import com.dillon.lw.fx.view.system.dict.data.DictDataView;
import com.dillon.lw.module.system.controller.admin.dict.vo.type.DictTypeRespVO;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

import static atlantafx.base.theme.Styles.*;

public class DictTypeView extends BaseView<DictTypeViewModel> implements Initializable {

    @FXML
    private Button addBut;

    @FXML
    private TableColumn<DictTypeRespVO, String> typeCol;

    @FXML
    private TextField codeField;

    @FXML
    private VBox contentPane;

    @FXML
    private TableColumn<DictTypeRespVO, LocalDateTime> createTimeCol;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private TableColumn<DictTypeRespVO, Long> idCol;

    @FXML
    private TableColumn<DictTypeRespVO, String> nameCol;

    @FXML
    private TextField nameField;

    @FXML
    private TableColumn<DictTypeRespVO, Long> optCol;

    @FXML
    private Button resetBut;

    @FXML
    private StackPane rootPane;

    @FXML
    private Button searchBut;


    @FXML
    private DatePicker startDatePicker;

    @FXML
    private TableColumn<DictTypeRespVO, Integer> statusCol;

    @FXML
    private ComboBox<?> statusCombo;

    @FXML
    private TableView<DictTypeRespVO> tableView;

    @FXML
    private TableColumn<DictTypeRespVO, String> remarkCol;

    private PagingControl pagingControl;

    private ModalPane modalPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        modalPane = new ModalPane();
        rootPane.getChildren().add(modalPane);
        pagingControl = new PagingControl();
        contentPane.getChildren().add(pagingControl);

        pagingControl.totalProperty().bind(viewModel.totalProperty());
        viewModel.pageNumProperty().bind(pagingControl.pageNumProperty());
        viewModel.pageSizeProperty().bind(pagingControl.pageSizeProperty());
        pagingControl.pageNumProperty().addListener((observable, oldValue, newValue) -> {
            viewModel.loadTableData();
        });

        pagingControl.pageSizeProperty().addListener((observable, oldValue, newValue) -> {
            viewModel.loadTableData();
        });
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setStyle("-fx-alignment: CENTER");

        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        typeCol.setStyle("-fx-alignment: CENTER");

        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setStyle("-fx-alignment: CENTER");

        remarkCol.setCellValueFactory(new PropertyValueFactory<>("remark"));
        remarkCol.setStyle("-fx-alignment: CENTER");

        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setStyle("-fx-alignment: CENTER");


        createTimeCol.setCellValueFactory(new PropertyValueFactory<>("createTime"));
        createTimeCol.setStyle("-fx-alignment: CENTER");

        createTimeCol.setCellFactory(new Callback<TableColumn<DictTypeRespVO, LocalDateTime>, TableCell<DictTypeRespVO, LocalDateTime>>() {
            @Override
            public TableCell<DictTypeRespVO, LocalDateTime> call(TableColumn<DictTypeRespVO, LocalDateTime> param) {
                return new TableCell<>() {
                    @Override
                    protected void updateItem(LocalDateTime item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                        } else {
                            if (item != null) {
                                this.setText(DateUtil.format(item, "yyyy-MM-dd HH:mm:ss"));
                            }
                        }

                    }
                };
            }
        });
        statusCol.setCellFactory(col -> {
            return new TableCell<>() {
                @Override
                protected void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        Button state = new Button();
                        if (item != null && item == 0) {
                            state.setText("正常");
                            state.getStyleClass().addAll(BUTTON_OUTLINED, SUCCESS);
                        } else {
                            state.setText("关闭");
                            state.getStyleClass().addAll(BUTTON_OUTLINED, DANGER);
                        }
                        HBox box = new HBox(state);
                        box.setPadding(new Insets(7, 7, 7, 7));
                        box.setAlignment(Pos.CENTER);
                        setGraphic(box);
                    }
                }
            };
        });
        optCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        optCol.setCellFactory(new Callback<TableColumn<DictTypeRespVO, Long>, TableCell<DictTypeRespVO, Long>>() {
            @Override
            public TableCell<DictTypeRespVO, Long> call(TableColumn<DictTypeRespVO, Long> param) {

                TableCell cell = new TableCell<DictTypeRespVO, Long>() {
                    @Override
                    protected void updateItem(Long item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            Button dataBut = new Button("数据");
                            dataBut.setOnAction(event -> {
                                DictDataView dictDataView = ViewLoader.load(DictDataView.class);
                                dictDataView.getViewModel().loadDictTypeData(getTableRow().getItem().getType());
                                EventBusCenter.get().post(new MainTabEvent("fth-database", "字典数据", dictDataView.getNode()));
                            });
                            dataBut.setGraphic(FontIcon.of(Feather.EDIT));
                            dataBut.getStyleClass().addAll(FLAT, ACCENT);


                            Button editBut = new Button("修改");
                            editBut.setOnAction(event -> showFormView(getTableRow().getItem().getId()));
                            editBut.setGraphic(FontIcon.of(Feather.EDIT));
                            editBut.getStyleClass().addAll(FLAT, ACCENT);

                            Button delBut = new Button("删除");
                            delBut.setOnAction(actionEvent -> showDelDialog(getTableRow().getItem()));
                            delBut.setGraphic(FontIcon.of(Feather.TRASH));
                            delBut.getStyleClass().addAll(FLAT, DANGER);
                            HBox box = new HBox(dataBut, editBut, delBut);
                            box.setAlignment(Pos.CENTER);
//                            box.setSpacing(7);
                            setGraphic(box);
                        }
                    }
                };
                return cell;
            }
        });
        tableView.itemsProperty().bind(viewModel.tableItemsProperty());

        nameField.textProperty().bindBidirectional(viewModel.nameProperty());
        codeField.textProperty().bindBidirectional(viewModel.typeProperty());

        statusCombo.getSelectionModel().selectedIndexProperty().addListener((observableValue, number, t1) -> viewModel.statusProperty().set(t1.intValue()));

        searchBut.setOnAction(actionEvent -> viewModel.loadTableData());
        resetBut.setOnAction(actionEvent -> {
            nameField.setText(null);
            codeField.setText(null);
            statusCombo.getSelectionModel().select(null);
            startDatePicker.setValue(null);
            endDatePicker.setValue(null);
        });
        startDatePicker.valueProperty().bindBidirectional(viewModel.beginDateProperty());
        endDatePicker.valueProperty().bindBidirectional(viewModel.endDateProperty());

        addBut.setOnAction(actionEvent -> showFormView(null));
    }


    /**
     * 显示编辑对话框
     */
    private void showFormView(Long id) {

        boolean isAdd = (id == null);
        DictTypeFormView load = ViewLoader.load(DictTypeFormView.class);
        load.getViewModel().query(id);
        new ConfirmDialog.Builder(modalPane)
                .title(isAdd ? "添加字典类型" : "编辑字典类型")
                .content(load.getNode())
                .width(450)
                .height(450)
                .onConfirm(d -> {
                    if (isAdd) {
                        load.getViewModel().addDictType(d);
                    } else {
                        load.getViewModel().updateDictType(d);
                    }
                })
                .onCancel(ConfirmDialog::close)
                .build()
                .show();

    }


    private void showDelDialog(DictTypeRespVO respVO) {
        new ConfirmDialog.Builder(modalPane)
                .title("删除字典类型")
                .message("确定要删除字典类型【 " + respVO.getName() + " 】吗?")
                .width(450)
                .height(150)
                .onConfirm(d -> {
                    viewModel.deleteDictType(respVO.getId(), d);
                })
                .onCancel(ConfirmDialog::close)
                .build()
                .show();

    }
}

package com.dillon.lw.fx.view.system.dict.data;

import atlantafx.base.controls.ModalPane;
import cn.hutool.core.date.DateUtil;
import com.dillon.lw.module.system.controller.admin.dict.vo.data.DictDataRespVO;
import com.dillon.lw.module.system.controller.admin.dict.vo.type.DictTypeSimpleRespVO;
import com.dillon.lw.fx.mvvm.base.BaseView;
import com.dillon.lw.fx.mvvm.loader.ViewLoader;
import com.dillon.lw.fx.view.layout.ConfirmDialog;
import com.dillon.lw.fx.view.layout.PagingControl;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
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

public class DictDataView extends BaseView<DictDataViewModel> implements Initializable {

    @FXML
    private Button addBut;

    @FXML
    private TableColumn<?, ?> colorTypeCol;

    @FXML
    private VBox contentPane;

    @FXML
    private TableColumn<DictDataRespVO, LocalDateTime> createTimeCol;

    @FXML
    private TableColumn<DictDataRespVO, String> cssClassCol;

    @FXML
    private ComboBox<DictTypeSimpleRespVO> dictNameComboBox;

    @FXML
    private TableColumn<DictDataRespVO, Long> idCol;

    @FXML
    private TableColumn<DictDataRespVO, String> labelCol;

    @FXML
    private TextField loabelField;

    @FXML
    private TableColumn<DictDataRespVO, Long> optCol;

    @FXML
    private TableColumn<DictDataRespVO, String> remarkCol;

    @FXML
    private Button resetBut;

    @FXML
    private StackPane rootPane;

    @FXML
    private Button searchBut;

    @FXML
    private TableColumn<DictDataRespVO, Integer> sortCol;

    @FXML
    private TableColumn<DictDataRespVO, Integer> statusCol;

    @FXML
    private ComboBox<DictTypeSimpleRespVO> statusCombo;

    @FXML
    private TableView<DictDataRespVO> tableView;

    @FXML
    private TableColumn<DictDataRespVO, String> valueCol;

    private PagingControl pagingControl;

    private ModalPane modalPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dictNameComboBox.setItems(viewModel.getDictTypeSimpleRespVOItems());
        modalPane = new ModalPane();
        rootPane.getChildren().add(modalPane);
        pagingControl = new PagingControl();
        contentPane.getChildren().add(pagingControl);

        dictNameComboBox.valueProperty().bindBidirectional(viewModel.selDictTypeSimpleRespVOProperty());
        // 设置按钮上显示的文字（当前选中项）
        dictNameComboBox.setButtonCell(new ListCell<DictTypeSimpleRespVO>() {
            @Override
            protected void updateItem(DictTypeSimpleRespVO item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName());
            }
        });
        dictNameComboBox.setCellFactory(new Callback<ListView<DictTypeSimpleRespVO>, ListCell<DictTypeSimpleRespVO>>() {
            @Override
            public ListCell<DictTypeSimpleRespVO> call(ListView<DictTypeSimpleRespVO> param) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(DictTypeSimpleRespVO item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            setText(item.getName());
                        }
                    }
                };
            }
        });

        pagingControl.totalProperty().bind(viewModel.totalProperty());
        viewModel.pageNumProperty().bind(pagingControl.pageNumProperty());
        viewModel.pageSizeProperty().bind(pagingControl.pageSizeProperty());
        pagingControl.pageNumProperty().addListener((observable, oldValue, newValue) -> {
            viewModel.loadTableData();
        });

        pagingControl.pageSizeProperty().addListener((observable, oldValue, newValue) -> {
            viewModel.loadTableData();
        });
        labelCol.setCellValueFactory(new PropertyValueFactory<>("label"));
        labelCol.setStyle("-fx-alignment: CENTER");

        valueCol.setCellValueFactory(new PropertyValueFactory<>("value"));
        valueCol.setStyle("-fx-alignment: CENTER");

        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setStyle("-fx-alignment: CENTER");

        remarkCol.setCellValueFactory(new PropertyValueFactory<>("remark"));
        remarkCol.setStyle("-fx-alignment: CENTER");

        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setStyle("-fx-alignment: CENTER");

        sortCol.setCellValueFactory(new PropertyValueFactory<>("sort"));
        sortCol.setStyle("-fx-alignment: CENTER");

        colorTypeCol.setCellValueFactory(new PropertyValueFactory<>("colorType"));
        colorTypeCol.setStyle("-fx-alignment: CENTER");

        cssClassCol.setCellValueFactory(new PropertyValueFactory<>("cssClass"));
        cssClassCol.setStyle("-fx-alignment: CENTER");

        createTimeCol.setCellValueFactory(new PropertyValueFactory<>("createTime"));
        createTimeCol.setStyle("-fx-alignment: CENTER");

        createTimeCol.setCellFactory(new Callback<TableColumn<DictDataRespVO, LocalDateTime>, TableCell<DictDataRespVO, LocalDateTime>>() {
            @Override
            public TableCell<DictDataRespVO, LocalDateTime> call(TableColumn<DictDataRespVO, LocalDateTime> param) {
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
        optCol.setCellFactory(new Callback<TableColumn<DictDataRespVO, Long>, TableCell<DictDataRespVO, Long>>() {
            @Override
            public TableCell<DictDataRespVO, Long> call(TableColumn<DictDataRespVO, Long> param) {

                TableCell cell = new TableCell<DictDataRespVO, Long>() {
                    @Override
                    protected void updateItem(Long item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            Button editBut = new Button("修改");
                            editBut.setOnAction(event -> showDictDataFormView(getTableRow().getItem().getId()));
                            editBut.setGraphic(FontIcon.of(Feather.EDIT));
                            editBut.getStyleClass().addAll(FLAT, ACCENT);

                            Button delBut = new Button("删除");
                            delBut.setOnAction(actionEvent -> showDelDialog(getTableRow().getItem()));
                            delBut.setGraphic(FontIcon.of(Feather.TRASH));
                            delBut.getStyleClass().addAll(FLAT, DANGER);
                            HBox box = new HBox(editBut, delBut);
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

        statusCombo.getSelectionModel().selectedIndexProperty().addListener((observableValue, number, t1) -> viewModel.statusProperty().set(t1.intValue()));

        searchBut.setOnAction(actionEvent -> viewModel.loadTableData());
        resetBut.setOnAction(actionEvent -> {
            statusCombo.getSelectionModel().select(null);
        });
        loabelField.textProperty().bindBidirectional(viewModel.labelProperty());
        addBut.setOnAction(actionEvent -> showDictDataFormView(null));
    }


    /**
     * 显示编辑对话框
     */
    private void showDictDataFormView(Long dictId) {

        boolean isAdd = (dictId == null);

        DictDataFormView load = ViewLoader.load(DictDataFormView.class);
        load.getViewModel().selDictTypeProperty().set(viewModel.getSelDictTypeSimpleRespVO().getType());
        load.getViewModel().query(dictId);
        Node centNode = load.getNode();
        new ConfirmDialog.Builder(modalPane)
                .title(isAdd ? "添加字典数据" : "编辑字典数据")
                .content(centNode)
                .width(450)
                .height(450)
                .onConfirm(d -> {
                    if (isAdd) {
                        load.getViewModel().addDictData(d);
                    } else {
                        load.getViewModel().updateDictData(d);
                    }
                })
                .onCancel(ConfirmDialog::close)
                .build()
                .show();

    }


    private void showDelDialog(DictDataRespVO respVO) {

        new ConfirmDialog.Builder(modalPane)
                .title("删除字典数据")
                .message("确定要删除字典数据【 " + respVO.getLabel() + " 】吗?")
                .width(450)
                .height(150)
                .onConfirm(d -> {
                    viewModel.deleteDictData(respVO.getId(), d);
                })
                .onCancel(ConfirmDialog::close)
                .build()
                .show();
    }
}

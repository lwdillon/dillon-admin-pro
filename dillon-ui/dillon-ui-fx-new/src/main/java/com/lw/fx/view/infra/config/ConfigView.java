package com.lw.fx.view.infra.config;

import cn.hutool.core.date.DateUtil;
import com.dlsc.gemsfx.DialogPane;
import com.lw.dillon.admin.module.infra.controller.admin.config.vo.ConfigRespVO;
import com.lw.dillon.admin.module.system.controller.admin.dict.vo.data.DictDataSimpleRespVO;
import com.lw.fx.request.Request;
import com.lw.fx.store.AppStore;
import com.lw.fx.util.MessageType;
import com.lw.fx.view.control.PagingControl;
import com.lw.fx.view.control.WFXGenericDialog;
import com.lw.ui.request.api.config.ConfigFeign;
import de.saxsys.mvvmfx.*;
import io.datafx.core.concurrent.ProcessChain;
import javafx.collections.FXCollections;
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
import java.util.Map;
import java.util.ResourceBundle;

import static atlantafx.base.theme.Styles.*;
import static com.lw.ui.utils.DictTypeEnum.INFRA_BOOLEAN_STRING;
import static com.lw.ui.utils.DictTypeEnum.INFRA_CONFIG_TYPE;

public class ConfigView implements FxmlView<ConfigViewModel>, Initializable {

    @InjectViewModel
    private ConfigViewModel viewModel;
    @FXML
    private Button addBut;

    @FXML
    private TableColumn<ConfigRespVO, ?> categoryCol;

    @FXML
    private TextField keyField;

    @FXML
    private VBox contentPane;

    @FXML
    private TableColumn<ConfigRespVO, LocalDateTime> createTimeCol;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private TableColumn<ConfigRespVO, ?> idCol;

    @FXML
    private TableColumn<ConfigRespVO, ?> keyCol;

    @FXML
    private TableColumn<ConfigRespVO, ?> nameCol;

    @FXML
    private TextField nameField;

    @FXML
    private TableColumn<ConfigRespVO, Long> optCol;

    @FXML
    private TableColumn<ConfigRespVO, ?> remarkCol;

    @FXML
    private Button resetBut;

    @FXML
    private StackPane rootPane;

    @FXML
    private Button searchBut;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private ComboBox<DictDataSimpleRespVO> typeCombo;

    @FXML
    private TableView<ConfigRespVO> tableView;

    @FXML
    private TableColumn<ConfigRespVO, Integer> typeCol;

    @FXML
    private TableColumn<ConfigRespVO, ?> valueCol;

    @FXML
    private TableColumn<ConfigRespVO, Boolean> visibleCol;

    private PagingControl pagingControl;

    private DialogPane dialogPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dialogPane = new DialogPane();
        rootPane.getChildren().add(dialogPane);
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


        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setStyle("-fx-alignment: CENTER");

        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        categoryCol.setStyle("-fx-alignment: CENTER");

        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setStyle("-fx-alignment: CENTER");

        keyCol.setCellValueFactory(new PropertyValueFactory<>("key"));
        keyCol.setStyle("-fx-alignment: CENTER");

        valueCol.setCellValueFactory(new PropertyValueFactory<>("value"));
        valueCol.setStyle("-fx-alignment: CENTER");

        visibleCol.setCellValueFactory(new PropertyValueFactory<>("visible"));
        visibleCol.setStyle("-fx-alignment: CENTER");
        visibleCol.setCellFactory(new Callback<TableColumn<ConfigRespVO, Boolean>, TableCell<ConfigRespVO, Boolean>>() {
            @Override
            public TableCell<ConfigRespVO, Boolean> call(TableColumn<ConfigRespVO, Boolean> param) {
                return new TableCell<>() {
                    @Override
                    protected void updateItem(Boolean item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                            setGraphic(null);
                        } else {


                            DictDataSimpleRespVO dict = AppStore.getDictDataValueMap(INFRA_BOOLEAN_STRING).get(item + "");
                            Button state = new Button(dict.getLabel());
                            switch (dict.getColorType()) {
                                case "primary":
                                    state.getStyleClass().addAll(BUTTON_OUTLINED, ACCENT);
                                    break;
                                case "success":
                                    state.getStyleClass().addAll(BUTTON_OUTLINED, SUCCESS);
                                    break;
                                case "info":
                                    state.getStyleClass().addAll(BUTTON_OUTLINED);
                                    break;
                                case "warning":
                                    state.getStyleClass().addAll(BUTTON_OUTLINED, WARNING);
                                    break;
                                case "danger":
                                    state.getStyleClass().addAll(BUTTON_OUTLINED, DANGER);
                                    break;
                                default:
                                    state.getStyleClass().addAll(BUTTON_OUTLINED);
                            }

                            HBox box = new HBox(state);
                            box.setPadding(new Insets(7, 7, 7, 7));
                            box.setAlignment(Pos.CENTER);
                            setGraphic(box);


                        }

                    }
                };
            }
        });
        createTimeCol.setCellValueFactory(new PropertyValueFactory<>("createTime"));
        createTimeCol.setStyle("-fx-alignment: CENTER");

        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        typeCol.setStyle("-fx-alignment: CENTER");
        typeCol.setCellFactory(new Callback<TableColumn<ConfigRespVO, Integer>, TableCell<ConfigRespVO, Integer>>() {
            @Override
            public TableCell<ConfigRespVO, Integer> call(TableColumn<ConfigRespVO, Integer> param) {
                return new TableCell<>() {
                    @Override
                    protected void updateItem(Integer item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                            setGraphic(null);
                        } else {


                            DictDataSimpleRespVO dict = AppStore.getDictDataValueMap(INFRA_CONFIG_TYPE).get(item + "");
                            Button state = new Button(dict.getLabel());
                            switch (dict.getColorType()) {
                                case "primary":
                                    state.getStyleClass().addAll(BUTTON_OUTLINED, ACCENT);
                                    break;
                                case "success":
                                    state.getStyleClass().addAll(BUTTON_OUTLINED, SUCCESS);
                                    break;
                                case "info":
                                    state.getStyleClass().addAll(BUTTON_OUTLINED);
                                    break;
                                case "warning":
                                    state.getStyleClass().addAll(BUTTON_OUTLINED, WARNING);
                                    break;
                                case "danger":
                                    state.getStyleClass().addAll(BUTTON_OUTLINED, DANGER);
                                    break;
                                default:
                                    state.getStyleClass().addAll(BUTTON_OUTLINED);
                            }

                            HBox box = new HBox(state);
                            box.setPadding(new Insets(7, 7, 7, 7));
                            box.setAlignment(Pos.CENTER);
                            setGraphic(box);


                        }

                    }
                };
            }
        });

        remarkCol.setCellValueFactory(new PropertyValueFactory<>("remark"));
        remarkCol.setStyle("-fx-alignment: CENTER");

        createTimeCol.setCellFactory(new Callback<TableColumn<ConfigRespVO, LocalDateTime>, TableCell<ConfigRespVO, LocalDateTime>>() {
            @Override
            public TableCell<ConfigRespVO, LocalDateTime> call(TableColumn<ConfigRespVO, LocalDateTime> param) {
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

        optCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        optCol.setCellFactory(new Callback<TableColumn<ConfigRespVO, Long>, TableCell<ConfigRespVO, Long>>() {
            @Override
            public TableCell<ConfigRespVO, Long> call(TableColumn<ConfigRespVO, Long> param) {

                TableCell cell = new TableCell<ConfigRespVO, Long>() {
                    @Override
                    protected void updateItem(Long item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                            setGraphic(null);
                        } else {

                            Button editBut = new Button("修改");
                            editBut.setOnAction(event -> showFormView(getTableRow().getItem().getId()));
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

        nameField.textProperty().bindBidirectional(viewModel.nameProperty());
        keyField.textProperty().bindBidirectional(viewModel.keyProperty());


        searchBut.setOnAction(actionEvent -> viewModel.loadTableData());
        resetBut.setOnAction(actionEvent -> {
            nameField.setText(null);
            keyField.setText(null);
            typeCombo.getSelectionModel().select(null);
            startDatePicker.setValue(null);
            endDatePicker.setValue(null);
            viewModel.loadTableData();
        });
        startDatePicker.valueProperty().bindBidirectional(viewModel.beginDateProperty());
        endDatePicker.valueProperty().bindBidirectional(viewModel.endDateProperty());

        addBut.setOnAction(actionEvent -> showFormView(null));

        typeCombo.setItems(FXCollections.observableArrayList(AppStore.getDictDataList(INFRA_CONFIG_TYPE)));
        typeCombo.valueProperty().bindBidirectional(viewModel.typeProperty());
    }


    /**
     * 显示编辑对话框
     */
    private void showFormView(Long id) {
        WFXGenericDialog dialog = new WFXGenericDialog();

        boolean isAdd = (id == null);
        ViewTuple<ConfigFormView, ConfigFormViewModel> load = FluentViewLoader.fxmlView(ConfigFormView.class).load();
        load.getViewModel().query(id);
        dialog.addActions(Map.entry(new Button("取消"), event -> dialog.close()), Map.entry(new Button("确定"), event -> {
            ProcessChain.create().addSupplierInExecutor(() -> {
                return load.getViewModel().save(isAdd);
            }).addConsumerInPlatformThread(r -> {
                if (r.isSuccess()) {
                    MvvmFX.getNotificationCenter().publish("message", "保存成功", MessageType.SUCCESS);

                    dialog.close();
                    viewModel.loadTableData();

                }
            }).onException(e -> e.printStackTrace()).run();
        }));

        dialog.setHeaderIcon(FontIcon.of(Feather.INFO));
        dialog.setHeaderText(id != null ? "编辑配置" : "添加配置");
        dialog.setContent(load.getView());
        dialog.show(rootPane.getScene());

    }


    private void showDelDialog(ConfigRespVO respVO) {
        WFXGenericDialog dialog = new WFXGenericDialog();
        dialog.setHeaderIcon(FontIcon.of(Feather.INFO));
        dialog.setHeaderText("删除配置");
        dialog.addActions(Map.entry(new Button("取消"), event -> dialog.close()), Map.entry(new Button("确定"), event -> {
            ProcessChain.create().addSupplierInExecutor(() -> {
                return Request.connector(ConfigFeign.class).deleteConfig(respVO.getId());
            }).addConsumerInPlatformThread(r -> {
                if (r.isSuccess()) {
                    MvvmFX.getNotificationCenter().publish("message", "删除成功", MessageType.SUCCESS);

                    dialog.close();
                    viewModel.loadTableData();
                }
            }).onException(e -> e.printStackTrace()).run();
        }));

        dialog.setContent(new Label("是否确认删除名称为" + respVO.getName() + "的数据项？"));
        dialog.show(rootPane.getScene());
    }
}

package com.lw.fx.view.system.operatelog;

import cn.hutool.core.date.DateUtil;
import com.dlsc.gemsfx.DialogPane;
import com.lw.dillon.admin.module.system.controller.admin.logger.vo.operatelog.OperateLogRespVO;
import com.lw.dillon.admin.module.system.controller.admin.user.vo.user.UserSimpleRespVO;
import com.lw.fx.request.Request;
import com.lw.fx.util.MessageType;
import com.lw.fx.view.control.PagingControl;
import com.lw.fx.view.control.WFXGenericDialog;
import com.lw.ui.request.api.system.OperateLogFeign;
import de.saxsys.mvvmfx.*;
import io.datafx.core.concurrent.ProcessChain;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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

public class OperateLogView implements FxmlView<OperateLogViewModel>, Initializable {

    @InjectViewModel
    private OperateLogViewModel viewModel;
    @FXML
    private TableColumn<OperateLogRespVO, String> actionCol;

    @FXML
    private TextField actionField;

    @FXML
    private TableColumn<OperateLogRespVO, Long> bizIdCol;

    @FXML
    private TextField bizIdField;

    @FXML
    private VBox contentPane;


    @FXML
    private Button emptyBut;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private TableColumn<OperateLogRespVO, Long> idCol;

    @FXML
    private TableColumn<OperateLogRespVO, String> operIpCol;

    @FXML
    private TableColumn<OperateLogRespVO, LocalDateTime> createTimeCol;

    @FXML
    private TableColumn<OperateLogRespVO, Long> optCol;

    @FXML
    private Button resetBut;

    @FXML
    private StackPane rootPane;

    @FXML
    private Button searchBut;

    @FXML
    private DatePicker startDatePicker;


    @FXML
    private TableColumn<OperateLogRespVO, String> subTypeCol;

    @FXML
    private TextField subTypeField;

    @FXML
    private TableView<OperateLogRespVO> tableView;

    @FXML
    private TableColumn<OperateLogRespVO, String> typeCol;

    @FXML
    private TextField typeField;

    @FXML
    private ComboBox<UserSimpleRespVO> userComboBox;

    @FXML
    private TableColumn<OperateLogRespVO, String> userNameCol;

    private PagingControl pagingControl;

    private DialogPane dialogPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        userComboBox.setItems(viewModel.getUserSimpleRespVOObservableItems());
        userComboBox.valueProperty().addListener((observableValue, userSimpleRespVO, t1) -> {
            viewModel.selUserItemProperty().set(t1);
        });
        userComboBox.setCellFactory(cell->new ListCell<>(){
            @Override
            protected void updateItem(UserSimpleRespVO userSimpleRespVO, boolean b) {
                super.updateItem(userSimpleRespVO, b);
                if (b) {
                    setText("");
                }else {
                    setText(userSimpleRespVO.getNickname()+"("+userSimpleRespVO.getId()+")");
                }
            }
        });
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

        userNameCol.setCellValueFactory(new PropertyValueFactory<>("userName"));
        userNameCol.setStyle("-fx-alignment: CENTER");

        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        typeCol.setStyle("-fx-alignment: CENTER");

        subTypeCol.setCellValueFactory(new PropertyValueFactory<>("subType"));
        subTypeCol.setStyle("-fx-alignment: CENTER");

        actionCol.setCellValueFactory(new PropertyValueFactory<>("action"));
        actionCol.setStyle("-fx-alignment: CENTER");

        bizIdCol.setCellValueFactory(new PropertyValueFactory<>("bizId"));
        bizIdCol.setStyle("-fx-alignment: CENTER");

        operIpCol.setCellValueFactory(new PropertyValueFactory<>("userIp"));
        operIpCol.setStyle("-fx-alignment: CENTER");


        createTimeCol.setCellValueFactory(new PropertyValueFactory<>("createTime"));
        createTimeCol.setStyle("-fx-alignment: CENTER");
        createTimeCol.setCellFactory(new Callback<TableColumn<OperateLogRespVO, LocalDateTime>, TableCell<OperateLogRespVO, LocalDateTime>>() {
            @Override
            public TableCell<OperateLogRespVO, LocalDateTime> call(TableColumn<OperateLogRespVO, LocalDateTime> param) {
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
        optCol.setCellFactory(new Callback<TableColumn<OperateLogRespVO, Long>, TableCell<OperateLogRespVO, Long>>() {
            @Override
            public TableCell<OperateLogRespVO, Long> call(TableColumn<OperateLogRespVO, Long> param) {

                TableCell cell = new TableCell<OperateLogRespVO, Long>() {
                    @Override
                    protected void updateItem(Long item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                            setGraphic(null);
                        } else {


                            Button editBut = new Button("详情");
                            editBut.setOnAction(event -> showFormView(getTableRow().getItem()));
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

        typeField.textProperty().bindBidirectional(viewModel.typeProperty());
        subTypeField.textProperty().bindBidirectional(viewModel.subTypeProperty());
        actionField.textProperty().bindBidirectional(viewModel.actionProperty());
        bizIdField.textProperty().bindBidirectional(viewModel.bizIdProperty());


        searchBut.setOnAction(actionEvent -> viewModel.loadTableData());
        resetBut.setOnAction(actionEvent -> {
            typeField.setText(null);
            subTypeField.setText(null);
            actionField.setText(null);
            bizIdField.setText(null);
            userComboBox.getSelectionModel().select(null);
            startDatePicker.setValue(null);
            endDatePicker.setValue(null);
        });
        startDatePicker.valueProperty().bindBidirectional(viewModel.beginDateProperty());
        endDatePicker.valueProperty().bindBidirectional(viewModel.endDateProperty());

    }


    /**
     * 显示编辑对话框
     */
    private void showFormView(OperateLogRespVO operateLogRespVO) {
        WFXGenericDialog dialog = new WFXGenericDialog();

        ViewTuple<OperateLogDetailView, OperateLogDetailViewModel> load = FluentViewLoader.fxmlView(OperateLogDetailView.class).load();
        load.getViewModel().queryDictType(operateLogRespVO);
        dialog.addActions(

                Map.entry(new Button("确定"), event -> {
                    dialog.close();
                })
        );

        dialog.setHeaderIcon(FontIcon.of(Feather.INFO));
        dialog.setHeaderText( "详情");
        dialog.setContent(load.getView());
        dialog.show(rootPane.getScene());

    }


    private void showDelDialog(OperateLogRespVO respVO) {
        WFXGenericDialog dialog = new WFXGenericDialog();
        dialog.setHeaderIcon(FontIcon.of(Feather.INFO));
        dialog.setHeaderText("删除");
        dialog.addActions(
                Map.entry(new Button("取消"), event -> dialog.close()),
                Map.entry(new Button("确定"), event -> {
                    ProcessChain.create()
                            .addSupplierInExecutor(() -> {
                                return Request.connector(OperateLogFeign.class).deleteOperateLog(respVO.getId());
                            })
                            .addConsumerInPlatformThread(r -> {
                                if (r.isSuccess()) {
                                    dialog.close();
                                    MvvmFX.getNotificationCenter().publish("message", "删除成功", MessageType.SUCCESS);

                                    viewModel.loadTableData();
                                }
                            }).onException(e -> e.printStackTrace())
                            .run();
                })
        );

        dialog.setContent(new Label("是否确认删除名称为" +respVO.getType()+"-" +respVO.getSubType() + "的数据项？"));
        dialog.show(rootPane.getScene());
    }
}

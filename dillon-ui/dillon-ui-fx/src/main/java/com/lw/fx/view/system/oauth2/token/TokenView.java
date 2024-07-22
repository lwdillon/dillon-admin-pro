package com.lw.fx.view.system.oauth2.token;

import cn.hutool.core.date.DateUtil;
import com.dlsc.gemsfx.DialogPane;
import com.lw.dillon.admin.module.system.controller.admin.dict.vo.data.DictDataSimpleRespVO;
import com.lw.dillon.admin.module.system.controller.admin.oauth2.vo.token.OAuth2AccessTokenRespVO;
import com.lw.fx.request.Request;
import com.lw.fx.store.AppStore;
import com.lw.fx.util.MessageType;
import com.lw.fx.view.control.PagingControl;
import com.lw.fx.view.control.WFXGenericDialog;
import com.lw.ui.request.api.system.OAuth2TokenFeign;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.MvvmFX;
import io.datafx.core.concurrent.ProcessChain;
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
import static com.lw.ui.utils.DictTypeEnum.USER_TYPE;

public class TokenView implements FxmlView<TokenViewModel>, Initializable {

    @InjectViewModel
    private TokenViewModel viewModel;
    @FXML
    private TableColumn<?, ?> accessTokenCol;

    @FXML
    private TextField clientIdField;

    @FXML
    private VBox contentPane;

    @FXML
    private TableColumn<OAuth2AccessTokenRespVO, LocalDateTime> createTimeCol;

    @FXML
    private TableColumn<OAuth2AccessTokenRespVO, LocalDateTime> expiresTimeCol;

    @FXML
    private TableColumn<OAuth2AccessTokenRespVO, Long> optCol;

    @FXML
    private TableColumn<OAuth2AccessTokenRespVO, String> refreshTokenCol;

    @FXML
    private Button resetBut;

    @FXML
    private StackPane rootPane;

    @FXML
    private Button searchBut;

    @FXML
    private TableView<OAuth2AccessTokenRespVO> tableView;

    @FXML
    private TableColumn<OAuth2AccessTokenRespVO, Long> userIdCol;

    @FXML
    private TextField userIdField;

    @FXML
    private TableColumn<OAuth2AccessTokenRespVO, Integer> userTypeCol;

    @FXML
    private ComboBox<DictDataSimpleRespVO> userTypeComboBox;

    private PagingControl pagingControl;

    private DialogPane dialogPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        userTypeComboBox.setItems(viewModel.getUserTypeItems());
        userTypeComboBox.valueProperty().bindBidirectional(viewModel.selUserTypeProperty());
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
        accessTokenCol.setCellValueFactory(new PropertyValueFactory<>("accessToken"));
        accessTokenCol.setStyle("-fx-alignment: CENTER");

        refreshTokenCol.setCellValueFactory(new PropertyValueFactory<>("refreshToken"));
        refreshTokenCol.setStyle("-fx-alignment: CENTER");

        userIdCol.setCellValueFactory(new PropertyValueFactory<>("userId"));
        userIdCol.setStyle("-fx-alignment: CENTER");

        userTypeCol.setCellValueFactory(new PropertyValueFactory<>("userType"));
        userTypeCol.setStyle("-fx-alignment: CENTER");
        userTypeCol.setCellFactory(new Callback<TableColumn<OAuth2AccessTokenRespVO, Integer>, TableCell<OAuth2AccessTokenRespVO, Integer>>() {
            @Override
            public TableCell<OAuth2AccessTokenRespVO, Integer> call(TableColumn<OAuth2AccessTokenRespVO, Integer> param) {
                return new TableCell<>() {
                    @Override
                    protected void updateItem(Integer item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText(null);
                            setGraphic(null);
                        } else {


                            DictDataSimpleRespVO dict = AppStore.getDictDataValueMap(USER_TYPE).get(item + "");
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
        expiresTimeCol.setCellValueFactory(new PropertyValueFactory<>("expiresTime"));
        expiresTimeCol.setStyle("-fx-alignment: CENTER");
        expiresTimeCol.setCellFactory(new Callback<TableColumn<OAuth2AccessTokenRespVO, LocalDateTime>, TableCell<OAuth2AccessTokenRespVO, LocalDateTime>>() {
            @Override
            public TableCell<OAuth2AccessTokenRespVO, LocalDateTime> call(TableColumn<OAuth2AccessTokenRespVO, LocalDateTime> param) {
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

        createTimeCol.setCellValueFactory(new PropertyValueFactory<>("createTime"));
        createTimeCol.setStyle("-fx-alignment: CENTER");
        createTimeCol.setCellFactory(new Callback<TableColumn<OAuth2AccessTokenRespVO, LocalDateTime>, TableCell<OAuth2AccessTokenRespVO, LocalDateTime>>() {
            @Override
            public TableCell<OAuth2AccessTokenRespVO, LocalDateTime> call(TableColumn<OAuth2AccessTokenRespVO, LocalDateTime> param) {
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
        optCol.setCellFactory(new Callback<TableColumn<OAuth2AccessTokenRespVO, Long>, TableCell<OAuth2AccessTokenRespVO, Long>>() {
            @Override
            public TableCell<OAuth2AccessTokenRespVO, Long> call(TableColumn<OAuth2AccessTokenRespVO, Long> param) {

                TableCell cell = new TableCell<OAuth2AccessTokenRespVO, Long>() {
                    @Override
                    protected void updateItem(Long item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                            setGraphic(null);
                        } else {


                            Button delBut = new Button("强退");
                            delBut.setOnAction(actionEvent -> showDelDialog(getTableRow().getItem()));
                            delBut.setGraphic(FontIcon.of(Feather.LOG_OUT));
                            delBut.getStyleClass().addAll(FLAT, DANGER);
                            HBox box = new HBox(delBut);
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

        userIdField.textProperty().bindBidirectional(viewModel.userIdProperty());
        clientIdField.textProperty().bindBidirectional(viewModel.userIdProperty());

        searchBut.setOnAction(actionEvent -> viewModel.loadTableData());
        resetBut.setOnAction(actionEvent -> {
            userIdField.setText(null);
            clientIdField.setText(null);
            userTypeComboBox.setValue(null);

        });

    }


    private void showDelDialog(OAuth2AccessTokenRespVO respVO) {
        WFXGenericDialog dialog = new WFXGenericDialog();
        dialog.setHeaderIcon(FontIcon.of(Feather.INFO));
        dialog.setHeaderText("强退");
        dialog.addActions(
                Map.entry(new Button("取消"), event -> dialog.close()),
                Map.entry(new Button("确定"), event -> {
                    ProcessChain.create()
                            .addSupplierInExecutor(() -> {
                                return Request.connector(OAuth2TokenFeign.class).deleteAccessToken(respVO.getAccessToken());
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

        dialog.setContent(new Label("是否确认删除名称为" + respVO.getUserId() + "的数据项？"));
        dialog.show(rootPane.getScene());
    }
}

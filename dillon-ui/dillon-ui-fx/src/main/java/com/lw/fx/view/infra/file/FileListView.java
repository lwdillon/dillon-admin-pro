package com.lw.fx.view.infra.file;

import atlantafx.base.controls.ModalPane;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.lw.dillon.admin.module.infra.controller.admin.file.vo.file.FileRespVO;
import com.lw.fx.request.Request;
import com.lw.fx.util.MessageType;
import com.lw.fx.view.control.PagingControl;
import com.lw.fx.view.control.WFXGenericDialog;
import com.lw.ui.request.api.file.FileFeign;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.MvvmFX;
import io.datafx.core.concurrent.ProcessChain;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.ResourceBundle;

import static atlantafx.base.theme.Styles.DANGER;
import static atlantafx.base.theme.Styles.FLAT;

public class FileListView implements FxmlView<FileListViewModel>, Initializable {
    @InjectViewModel
    private FileListViewModel viewModel;
    @FXML
    private VBox contentPane;

    @FXML
    private TableColumn<FileRespVO, LocalDateTime> createTimeCol;

    @FXML
    private Button uploadBut;

    @FXML
    private DatePicker endDatePicker;


    @FXML
    private TableColumn<FileRespVO, ?> nameCol;

    @FXML
    private TableColumn<FileRespVO, Long> optCol;

    @FXML
    private TableColumn<FileRespVO, ?> pathCol;

    @FXML
    private TableColumn<FileRespVO, ?> urlCol;

    @FXML
    private Button resetBut;

    @FXML
    private StackPane rootPane;

    @FXML
    private Button searchBut;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private TableColumn<FileRespVO, ?> sizeCol;
    @FXML
    private TableColumn<FileRespVO, ?> typeCol;
    @FXML
    private TableColumn<FileRespVO, String> centerCol;

    @FXML
    private TableView<FileRespVO> tableView;

    @FXML
    private TextField typeField;
    @FXML
    private TextField pathField;

    private PagingControl pagingControl;

    private ImageView modalImage = new ImageView();

    private final ModalPane modalPane = new ModalPane();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        pagingControl = new PagingControl();
        rootPane.getChildren().add(0, modalPane);
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


        pathField.textProperty().bindBidirectional(viewModel.pathProperty());
        typeField.textProperty().bindBidirectional(viewModel.typeProperty());


        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setStyle("-fx-alignment: CENTER");

        pathCol.setCellValueFactory(new PropertyValueFactory<>("path"));
        pathCol.setStyle("-fx-alignment: CENTER");

        urlCol.setCellValueFactory(new PropertyValueFactory<>("url"));
        urlCol.setStyle("-fx-alignment: CENTER");
        sizeCol.setCellValueFactory(new PropertyValueFactory<>("size"));
        sizeCol.setStyle("-fx-alignment: CENTER");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        typeCol.setStyle("-fx-alignment: CENTER");
        centerCol.setCellValueFactory(new PropertyValueFactory<>("url"));
        centerCol.setStyle("-fx-alignment: CENTER");
        createTimeCol.setCellValueFactory(new PropertyValueFactory<>("createTime"));
        createTimeCol.setStyle("-fx-alignment: CENTER");
        createTimeCol.setCellFactory(new Callback<TableColumn<FileRespVO, LocalDateTime>, TableCell<FileRespVO, LocalDateTime>>() {
            @Override
            public TableCell<FileRespVO, LocalDateTime> call(TableColumn<FileRespVO, LocalDateTime> param) {
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

        centerCol.setCellFactory(new Callback<TableColumn<FileRespVO, String>, TableCell<FileRespVO, String>>() {
            @Override
            public TableCell<FileRespVO, String> call(TableColumn<FileRespVO, String> param) {
                return new TableCell<>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                            setGraphic(null);
                        } else {

                            FileRespVO fileRespVO = getTableRow().getItem();
                            if (StrUtil.contains(fileRespVO.getType(), "image")) {
                                Image image = new Image(item, true);
                                ImageView view = new ImageView();
                                view.setImage(image);
                                view.setFitWidth(180);
                                view.setFitHeight(120);
                                view.setCursor(Cursor.HAND);

                                view.setOnMouseClicked(evt -> {
                                    modalImage.setImage(view.getImage());
                                    modalPane.show(modalImage);
                                    modalImage.requestFocus();
                                });
                                VBox vBox = new VBox(view);
                                vBox.setPadding(new Insets(10,10,10,10));
                                vBox.setAlignment(Pos.CENTER);
                                setGraphic(vBox);

                            }else {
                                Hyperlink hyperlink = new Hyperlink("下载");
                                hyperlink.setOnAction(event -> {
                                    try {
                                        // Check if Desktop API is supported on the current platform
                                        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                                            Desktop.getDesktop().browse(new URI(item));
                                        } else {
//                                            showAlert("Error", "Desktop API is not supported on this platform.");
                                        }
                                    } catch (IOException | URISyntaxException e) {
//                                        showAlert("Error", "Failed to open URL: " + e.getMessage());
                                    }
                                });
                                VBox vBox = new VBox(hyperlink);
                                vBox.setAlignment(Pos.CENTER);
                                setGraphic(vBox);
                            }

                        }

                    }
                };
            }
        });

        optCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        optCol.setCellFactory(new Callback<TableColumn<FileRespVO, Long>, TableCell<FileRespVO, Long>>() {
            @Override
            public TableCell<FileRespVO, Long> call(TableColumn<FileRespVO, Long> param) {

                TableCell cell = new TableCell<FileRespVO, Long>() {
                    @Override
                    protected void updateItem(Long item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                            setGraphic(null);
                        } else {

                            Button delBut = new Button("删除");
                            delBut.setOnAction(actionEvent -> showDelDialog(getTableRow().getItem()));
                            delBut.setGraphic(FontIcon.of(Feather.TRASH));
                            delBut.getStyleClass().addAll(FLAT, DANGER);
                            HBox box = new HBox(delBut);
                            box.setAlignment(Pos.CENTER);
                            setGraphic(box);
                        }
                    }
                };
                return cell;
            }
        });

        tableView.setItems(viewModel.getTableItems());

        uploadBut.setOnAction(actionEvent -> addFile());
        searchBut.setOnAction(actionEvent -> viewModel.loadTableData());
        resetBut.setOnAction(actionEvent -> {
            pathField.setText(null);
            typeField.setText(null);
            startDatePicker.setValue(null);
            endDatePicker.setValue(null);
            viewModel.loadTableData();
        });
    }

    private void addFile() {
        var fileChooser = new FileChooser();
        fileChooser.setTitle("选择要上传的文件");
        File file = fileChooser.showOpenDialog(rootPane.getScene().getWindow());
        if (file == null) {
            return;
        }

        ProcessChain.create()
                .addSupplierInExecutor(() -> {

                    return Request.fileConnector(FileFeign.class).uploadFile("", file);
                })
                .addConsumerInPlatformThread(r -> {
                    if (r.isSuccess()) {

                        viewModel.loadTableData();
                    } else {
                        MvvmFX.getNotificationCenter().publish("message", r.getMsg(), MessageType.WARNING);

                    }

                }).onException(e -> e.printStackTrace())
                .run();

    }


    private void showDelDialog(FileRespVO respVO) {
        WFXGenericDialog dialog = new WFXGenericDialog();
        dialog.setHeaderIcon(FontIcon.of(Feather.INFO));
        dialog.setHeaderText("删除");
        dialog.addActions(
                Map.entry(new Button("取消"), event -> dialog.close()),
                Map.entry(new Button("确定"), event -> {
                    ProcessChain.create()
                            .addSupplierInExecutor(() -> {
                                return Request.connector(FileFeign.class).deleteFile(respVO.getId());
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

        dialog.setContent(new Label("是否确认删除名称为" + respVO.getName() + "的数据项？"));
        dialog.show(rootPane.getScene());
    }


}

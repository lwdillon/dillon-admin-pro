package com.dillon.lw.fx.view.system.post;

import atlantafx.base.controls.ModalPane;
import cn.hutool.core.date.DateUtil;
import com.dillon.lw.fx.mvvm.base.BaseView;
import com.dillon.lw.fx.mvvm.loader.ViewLoader;
import com.dillon.lw.fx.view.layout.ConfirmDialog;
import com.dillon.lw.fx.view.layout.PagingControl;
import com.dillon.lw.module.system.controller.admin.dept.vo.post.PostRespVO;
import javafx.fxml.FXML;
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

public class PostView extends BaseView<PostViewModel> {

    @FXML
    private Button addBut;

    @FXML
    private TableColumn<PostRespVO, String> codeCol;

    @FXML
    private TextField codeField;

    @FXML
    private VBox contentPane;

    @FXML
    private TableColumn<PostRespVO, LocalDateTime> createTimeCol;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private TableColumn<PostRespVO, Long> idCol;

    @FXML
    private TableColumn<PostRespVO, String> nameCol;

    @FXML
    private TextField nameField;

    @FXML
    private TableColumn<PostRespVO, Long> optCol;

    @FXML
    private Button resetBut;

    @FXML
    private StackPane rootPane;

    @FXML
    private Button searchBut;

    @FXML
    private TableColumn<PostRespVO, Integer> sortCol;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private TableColumn<PostRespVO, Integer> statusCol;

    @FXML
    private ComboBox<?> statusCombo;

    @FXML
    private TableView<PostRespVO> tableView;

    @FXML
    private TableColumn<PostRespVO, String> remarkCol;

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

        codeCol.setCellValueFactory(new PropertyValueFactory<>("code"));
        codeCol.setStyle("-fx-alignment: CENTER");

        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setStyle("-fx-alignment: CENTER");

        remarkCol.setCellValueFactory(new PropertyValueFactory<>("remark"));
        remarkCol.setStyle("-fx-alignment: CENTER");

        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setStyle("-fx-alignment: CENTER");

        sortCol.setCellValueFactory(new PropertyValueFactory<>("sort"));
        sortCol.setStyle("-fx-alignment: CENTER");

        createTimeCol.setCellValueFactory(new PropertyValueFactory<>("createTime"));
        createTimeCol.setStyle("-fx-alignment: CENTER");

        createTimeCol.setCellFactory(new Callback<TableColumn<PostRespVO, LocalDateTime>, TableCell<PostRespVO, LocalDateTime>>() {
            @Override
            public TableCell<PostRespVO, LocalDateTime> call(TableColumn<PostRespVO, LocalDateTime> param) {
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
        optCol.setCellFactory(new Callback<TableColumn<PostRespVO, Long>, TableCell<PostRespVO, Long>>() {
            @Override
            public TableCell<PostRespVO, Long> call(TableColumn<PostRespVO, Long> param) {

                TableCell cell = new TableCell<PostRespVO, Long>() {
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
        codeField.textProperty().bindBidirectional(viewModel.codeProperty());

        statusCombo.getSelectionModel().selectedIndexProperty().addListener((observableValue, number, t1) -> viewModel.statusProperty().set(t1.intValue()));

        searchBut.setOnAction(actionEvent -> viewModel.loadTableData());
        resetBut.setOnAction(actionEvent -> {
            nameField.setText(null);
            codeField.setText(null);
            statusCombo.getSelectionModel().select(null);
            startDatePicker.setValue(null);
            endDatePicker.setValue(null);
            viewModel.loadTableData();
        });
        startDatePicker.valueProperty().bindBidirectional(viewModel.beginDateProperty());
        endDatePicker.valueProperty().bindBidirectional(viewModel.endDateProperty());

        addBut.setOnAction(actionEvent -> showFormView(null));
    }


    /**
     * 显示编辑对话框
     */
    private void showFormView(Long id) {
        PostFormView postFormView = ViewLoader.load(PostFormView.class);
        postFormView.getViewModel().query(id);
        new ConfirmDialog.Builder(modalPane)
                .title(id != null ? "编辑岗位" : "添加岗位")
                .content(postFormView.getNode())
                .width(450)
                .height(450)
                .onConfirm(d -> {
                    if (id == null) {
                        postFormView.getViewModel().createPost(d);
                    } else {
                        postFormView.getViewModel().updatePost(d);
                    }
                })
                .onCancel(ConfirmDialog::close)
                .build()
                .show();


    }


    private void showDelDialog(PostRespVO respVO) {

        new ConfirmDialog.Builder(modalPane)
                .title("删除岗位")
                .message("确定删除岗位【" + respVO.getName() + "】吗？")
                .width(400)
                .height(100)
                .onConfirm(d -> {
                    viewModel.deletePost(respVO.getId(), d);
                })
                .onCancel(ConfirmDialog::close)
                .build()
                .show();

    }
}

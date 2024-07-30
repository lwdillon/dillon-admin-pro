package com.lw.fx.view.main;

import com.lw.dillon.admin.module.system.controller.admin.notify.vo.message.NotifyMessageRespVO;
import com.lw.fx.util.MessageType;
import com.lw.fx.view.system.notice.MyNotifyMessageView;
import com.lw.fx.view.system.notice.MyNotifyMessageViewModel;
import de.saxsys.mvvmfx.*;
import io.datafx.core.concurrent.ProcessChain;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2AL;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import static atlantafx.base.theme.Styles.ACCENT;

public class MessageView implements FxmlView<MessageViewModel>, Initializable {
    @FXML
    private ListView<NotifyMessageRespVO> listView;

    @FXML
    private Hyperlink markersBut;

    @FXML
    private Hyperlink viewAllBut;

    @InjectViewModel
    private MessageViewModel viewModel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        listView.setItems(viewModel.getItems());
        listView.setFixedCellSize(65);
        listView.setCellFactory(cell -> new ListCell<>() {
            @Override
            protected void updateItem(NotifyMessageRespVO notifyMessageRespVO, boolean b) {
                super.updateItem(notifyMessageRespVO, b);
                if (b) {
                    setText("");
                    setGraphic(null);
                } else {

                    Label content = new Label(notifyMessageRespVO.getTemplateNickname() + " :  " + notifyMessageRespVO.getTemplateContent());
                    content.getStyleClass().addAll("text-caption");
                    content.setMaxHeight(Double.MAX_VALUE);
                    VBox.setVgrow(content, Priority.ALWAYS);
                    content.setPadding(new Insets(0, 10, 0, 10));

                    Label createTime = new Label(notifyMessageRespVO.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                    createTime.getStyleClass().addAll("text-muted");

                    Label typeBut = new Label("系统消息");
                    typeBut.setGraphic(new FontIcon(Material2AL.LABEL));
                    typeBut.getStyleClass().addAll(ACCENT);

                    Pane pane = new Pane();
                    HBox.setHgrow(pane, Priority.ALWAYS);

                    HBox hBox = new HBox(10, createTime, pane, typeBut);
                    hBox.setAlignment(Pos.CENTER_LEFT);
                    hBox.setPadding(new Insets(0, 10, 0, 10));

                    VBox vBox = new VBox(5, content, hBox);
                    vBox.setPadding(new Insets(10, 0, 0, 0));
                    hBox.setStyle("-fx-border-width: 0 0 1 0;-fx-border-color: -color-border-default;");
                    setGraphic(vBox);
                }
            }
        });
        markersBut.visibleProperty().bind(Bindings.createBooleanBinding(() -> {
            return viewModel.getItems().isEmpty() == false;
        }, viewModel.getItems()));
        viewAllBut.setOnMouseClicked(mouseEvent -> {
            ViewTuple<MyNotifyMessageView, MyNotifyMessageViewModel> viewTuple = FluentViewLoader.fxmlView(MyNotifyMessageView.class).load();
            MvvmFX.getNotificationCenter().publish("addTab", "我的消息", Feather.MAIL, viewTuple.getView());
        });
        markersBut.setOnMouseClicked(mouseEvent -> {
            ProcessChain.create()
                    .addSupplierInExecutor(() -> viewModel.updateNotifyMessageRead())
                    .addConsumerInPlatformThread(rel -> {
                        if (rel.isSuccess()) {
                            MvvmFX.getNotificationCenter().publish("message", "标记为已读成功", MessageType.SUCCESS);
                            MvvmFX.getNotificationCenter().publish("notify", "标记为已读成功");
                            viewModel.getUnreadNotifyMessageList();
                        }
                    }).run();
        });
    }
}

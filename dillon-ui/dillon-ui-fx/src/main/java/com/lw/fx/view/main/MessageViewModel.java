package com.lw.fx.view.main;

import com.lw.dillon.admin.framework.common.pojo.CommonResult;
import com.lw.dillon.admin.module.system.controller.admin.notify.vo.message.NotifyMessageRespVO;
import com.lw.fx.request.Request;
import com.lw.fx.util.MessageType;
import com.lw.fx.vo.NotifyMessageVo;
import com.lw.ui.request.api.system.NotifyMessageFeign;
import de.saxsys.mvvmfx.MvvmFX;
import de.saxsys.mvvmfx.SceneLifecycle;
import de.saxsys.mvvmfx.ViewModel;
import io.datafx.core.concurrent.ProcessChain;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.HashSet;
import java.util.Set;

public class MessageViewModel implements ViewModel, SceneLifecycle {

    private ObservableList<NotifyMessageRespVO> items = FXCollections.observableArrayList();

    public MessageViewModel() {
        getUnreadNotifyMessageList();
    }

    public CommonResult<Boolean> updateNotifyMessageRead() {

        Set<Long> ids = new HashSet<>();

        for (NotifyMessageRespVO notifyMessageRespVO : items) {
            ids.add(notifyMessageRespVO.getId());
        }
        return Request.connector(NotifyMessageFeign.class).updateNotifyMessageRead(ids.stream().toList());

    }


    public void getUnreadNotifyMessageList() {

        ProcessChain.create().addRunnableInPlatformThread(() -> items.clear())
                .addSupplierInExecutor(() -> {
                    return Request.connector(NotifyMessageFeign.class).getUnreadNotifyMessageList(10);
                })
                .addConsumerInPlatformThread(r -> {

                    if (r.isSuccess()) {
                        items.setAll(r.getData());
                    } else {

                    }
                })
                .onException(e -> {
                    e.printStackTrace();
                })
                .withFinal(() -> {
                })
                .run();

    }

    @Override
    public void onViewAdded() {

    }

    @Override
    public void onViewRemoved() {

    }

    public ObservableList<NotifyMessageRespVO> getItems() {
        return items;
    }
}

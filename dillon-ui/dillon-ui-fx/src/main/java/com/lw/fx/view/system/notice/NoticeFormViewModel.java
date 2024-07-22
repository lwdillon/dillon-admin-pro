package com.lw.fx.view.system.notice;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjUtil;
import com.lw.dillon.admin.framework.common.pojo.CommonResult;
import com.lw.dillon.admin.module.system.controller.admin.dict.vo.data.DictDataSimpleRespVO;
import com.lw.dillon.admin.module.system.controller.admin.notice.vo.NoticeSaveReqVO;
import com.lw.fx.request.Request;
import com.lw.fx.store.AppStore;
import com.lw.ui.request.api.system.NoticeFeign;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.mapping.ModelWrapper;
import io.datafx.core.concurrent.ProcessChain;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import static com.lw.ui.utils.DictTypeEnum.SYSTEM_NOTICE_TYPE;

public class NoticeFormViewModel implements ViewModel {

    private ModelWrapper<NoticeSaveReqVO> wrapper = new ModelWrapper<>();


    private ObservableList<DictDataSimpleRespVO> typeItems = FXCollections.observableArrayList();
    private ObjectProperty<DictDataSimpleRespVO> selTtem = new SimpleObjectProperty<>();

    public NoticeFormViewModel() {
        initData();
    }

    public NoticeSaveReqVO getUserSaveReqVO() {
        if (selTtem.get() != null) {
            typeProperty().set(Convert.toInt(selTtem.get().getValue()));
        }
        wrapper.commit();
        return wrapper.get();
    }

    public void initData() {

        ProcessChain.create()
                .addSupplierInExecutor(() -> AppStore.getDictDataList(SYSTEM_NOTICE_TYPE))
                .addConsumerInPlatformThread(rel -> {
                    typeItems.setAll(rel);
                })

                .run();


    }

    public void query(Long id) {

        ProcessChain.create()
                .addSupplierInExecutor(() -> {
                    if (id == null) {
                        return new NoticeSaveReqVO();
                    }
                    return Request.connector(NoticeFeign.class).getNotice(id).getData();
                })
                .addConsumerInPlatformThread(r -> {
                    NoticeSaveReqVO reqVO = new NoticeSaveReqVO();
                    BeanUtil.copyProperties(r, reqVO);
                    setNotice(reqVO);

                    // 设置选中项为 name 等于 "abc" 的对象
                    for (DictDataSimpleRespVO item : typeItems) {
                        if (ObjUtil.equals(reqVO.getType(), Convert.toInt(item.getValue()))) {
                            selTtem.set(item);
                            break;
                        }
                    }

                })
                .onException(e -> e.printStackTrace())
                .run();
    }

    /**
     * 系统设置菜单
     */
    public void setNotice(NoticeSaveReqVO roleRespVO) {

        wrapper.set(roleRespVO);
        wrapper.reload();
    }

    public CommonResult saveUser(boolean isAdd) {

        if (isAdd) {
            return Request.connector(NoticeFeign.class).createNotice(getUserSaveReqVO());
        } else {
            return Request.connector(NoticeFeign.class).updateNotice(getUserSaveReqVO());
        }
    }

    public StringProperty titleProperty() {
        return wrapper.field("title", NoticeSaveReqVO::getTitle, NoticeSaveReqVO::setTitle, "");
    }

    public IntegerProperty typeProperty() {
        return wrapper.field("type", NoticeSaveReqVO::getType, NoticeSaveReqVO::setType);
    }

    public LongProperty idProperty() {
        return wrapper.field("id", NoticeSaveReqVO::getId, NoticeSaveReqVO::setId);
    }

    public StringProperty contentProperty() {
        return wrapper.field("content", NoticeSaveReqVO::getContent, NoticeSaveReqVO::setContent);
    }

    public IntegerProperty statusProperty() {
        return wrapper.field("status", NoticeSaveReqVO::getStatus, NoticeSaveReqVO::setStatus);
    }


    public void commitHtmText() {
        publish("commitHtmText", "commitHtmText");
    }

    public ObservableList<DictDataSimpleRespVO> getTypeItems() {
        return typeItems;
    }

    public DictDataSimpleRespVO getSelTtem() {
        return selTtem.get();
    }

    public ObjectProperty<DictDataSimpleRespVO> selTtemProperty() {
        return selTtem;
    }
}


package com.lw.fx.view.system.notice;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.Convert;
import com.lw.dillon.admin.framework.common.pojo.CommonResult;
import com.lw.dillon.admin.module.system.controller.admin.dict.vo.data.DictDataSimpleRespVO;
import com.lw.dillon.admin.module.system.controller.admin.notify.vo.template.NotifyTemplateSaveReqVO;
import com.lw.fx.request.Request;
import com.lw.fx.store.AppStore;
import com.lw.ui.request.api.system.NotifyTemplateFeign;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.mapping.ModelWrapper;
import io.datafx.core.concurrent.ProcessChain;
import javafx.beans.property.*;

import static com.lw.ui.utils.DictTypeEnum.COMMON_STATUS;
import static com.lw.ui.utils.DictTypeEnum.SYSTEM_NOTIFY_TEMPLATE_TYPE;

public class NotifyTemplateFormViewModel implements ViewModel {

    private ModelWrapper<NotifyTemplateSaveReqVO> wrapper = new ModelWrapper<>();

    private ObjectProperty<DictDataSimpleRespVO> selType = new SimpleObjectProperty<>();
    private ObjectProperty<DictDataSimpleRespVO> selStatus = new SimpleObjectProperty<>();


    public NotifyTemplateFormViewModel() {
        initData();
    }

    public NotifyTemplateSaveReqVO getUserSaveReqVO() {

        if (selType != null) {
            typeProperty().set(Convert.toInt(selType.get().getValue()));
        }

        if (selStatus != null) {
            statusProperty().set(Convert.toInt(selStatus.get().getValue()));
        }

        wrapper.commit();
        return wrapper.get();
    }

    public void initData() {


    }

    public void query(Long id) {

        ProcessChain.create()
                .addSupplierInExecutor(() -> {
                    if (id == null) {
                        return new NotifyTemplateSaveReqVO();
                    }
                    return Request.connector(NotifyTemplateFeign.class).getNotifyTemplate(id).getData();
                })
                .addConsumerInPlatformThread(r -> {
                    NotifyTemplateSaveReqVO reqVO = new NotifyTemplateSaveReqVO();
                    BeanUtil.copyProperties(r, reqVO);
                    setNotice(reqVO);

                    DictDataSimpleRespVO typeDict = AppStore.getDictDataValueMap(SYSTEM_NOTIFY_TEMPLATE_TYPE).get(reqVO.getType() + "");
                    selType.set(typeDict);

                    DictDataSimpleRespVO status = AppStore.getDictDataValueMap(COMMON_STATUS).get(reqVO.getStatus() + "");
                    selStatus.set(status);

                })
                .onException(e -> e.printStackTrace())
                .run();
    }

    /**
     * 系统设置菜单
     */
    public void setNotice(NotifyTemplateSaveReqVO roleRespVO) {

        wrapper.set(roleRespVO);
        wrapper.reload();
    }

    public CommonResult save(boolean isAdd) {

        if (isAdd) {
            return Request.connector(NotifyTemplateFeign.class).createNotifyTemplate(getUserSaveReqVO());
        } else {
            return Request.connector(NotifyTemplateFeign.class).updateNotifyTemplate(getUserSaveReqVO());
        }
    }

    public LongProperty idProperty() {
        return wrapper.field("id", NotifyTemplateSaveReqVO::getId, NotifyTemplateSaveReqVO::setId);
    }

    public StringProperty nameProperty() {
        return wrapper.field("name", NotifyTemplateSaveReqVO::getName, NotifyTemplateSaveReqVO::setName, "");
    }

    public StringProperty codeProperty() {
        return wrapper.field("code", NotifyTemplateSaveReqVO::getCode, NotifyTemplateSaveReqVO::setCode, "");
    }

    public IntegerProperty typeProperty() {
        return wrapper.field("type", NotifyTemplateSaveReqVO::getType, NotifyTemplateSaveReqVO::setType);
    }

    public StringProperty nicknameProperty() {
        return wrapper.field("nickname", NotifyTemplateSaveReqVO::getNickname, NotifyTemplateSaveReqVO::setNickname, "");
    }

    public StringProperty contentProperty() {
        return wrapper.field("content", NotifyTemplateSaveReqVO::getContent, NotifyTemplateSaveReqVO::setContent);
    }

    public StringProperty remarkProperty() {
        return wrapper.field("remark", NotifyTemplateSaveReqVO::getRemark, NotifyTemplateSaveReqVO::setRemark);
    }

    public IntegerProperty statusProperty() {
        return wrapper.field("status", NotifyTemplateSaveReqVO::getStatus, NotifyTemplateSaveReqVO::setStatus);
    }

    public DictDataSimpleRespVO getSelType() {
        return selType.get();
    }

    public ObjectProperty<DictDataSimpleRespVO> selTypeProperty() {
        return selType;
    }

    public void setSelType(DictDataSimpleRespVO selType) {
        this.selType.set(selType);
    }

    public DictDataSimpleRespVO getSelStatus() {
        return selStatus.get();
    }

    public ObjectProperty<DictDataSimpleRespVO> selStatusProperty() {
        return selStatus;
    }
}


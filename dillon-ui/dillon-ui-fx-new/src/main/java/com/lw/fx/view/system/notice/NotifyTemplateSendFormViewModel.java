package com.lw.fx.view.system.notice;

import cn.hutool.core.convert.Convert;
import com.lw.dillon.admin.framework.common.pojo.CommonResult;
import com.lw.dillon.admin.module.system.controller.admin.dict.vo.data.DictDataSimpleRespVO;
import com.lw.dillon.admin.module.system.controller.admin.notify.vo.template.NotifyTemplateRespVO;
import com.lw.dillon.admin.module.system.controller.admin.notify.vo.template.NotifyTemplateSendReqVO;
import com.lw.dillon.admin.module.system.controller.admin.user.vo.user.UserSimpleRespVO;
import com.lw.fx.request.Request;
import com.lw.ui.request.api.system.NotifyTemplateFeign;
import com.lw.ui.request.api.system.UserFeign;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.mapping.ModelWrapper;
import io.datafx.core.concurrent.ProcessChain;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.HashMap;
import java.util.Map;

public class NotifyTemplateSendFormViewModel implements ViewModel {

    private ModelWrapper<NotifyTemplateRespVO> wrapper = new ModelWrapper<>();

    private ObjectProperty<DictDataSimpleRespVO> userType = new SimpleObjectProperty<>();
    private ObjectProperty<UserSimpleRespVO> selUser = new SimpleObjectProperty<>();

    private Map<String, Object> templateParams = new HashMap<>();

    private ObservableList<UserSimpleRespVO> userItems= FXCollections.observableArrayList();

    public NotifyTemplateSendFormViewModel() {
        initData();
    }

    public NotifyTemplateRespVO getUserSaveReqVO() {


        wrapper.commit();
        return wrapper.get();
    }

    public void initData() {

        ProcessChain.create()
                .addSupplierInExecutor(() -> {
                    return Request.connector(UserFeign.class).getSimpleUserList().getData();
                })
                .addConsumerInPlatformThread(r -> {
                    userItems.setAll(r);

                })
                .onException(e -> e.printStackTrace())
                .run();
    }

    public void query(Long id) {

        ProcessChain.create()
                .addSupplierInExecutor(() -> {
                    return Request.connector(NotifyTemplateFeign.class).getNotifyTemplate(id).getData();
                })
                .addConsumerInPlatformThread(r -> {
                    setNotice(r);

                })
                .onException(e -> e.printStackTrace())
                .run();
    }

    public CommonResult<Long> sendNotify() {
        NotifyTemplateSendReqVO reqVO = new NotifyTemplateSendReqVO();
        reqVO.setUserId(Convert.toLong(selUser.get().getId(), null));
        reqVO.setUserType(Convert.toInt(userType.get().getValue(), null));
        reqVO.setTemplateCode(codeProperty().get());
        reqVO.setTemplateParams(templateParams.isEmpty() ? null : templateParams);
        return Request.connector(NotifyTemplateFeign.class).sendNotify(reqVO);
    }

    /**
     * 系统设置菜单
     */
    public void setNotice(NotifyTemplateRespVO roleRespVO) {

        wrapper.set(roleRespVO);
        wrapper.reload();

        publish("params");
    }


    public StringProperty contentProperty() {
        return wrapper.field("content", NotifyTemplateRespVO::getContent, NotifyTemplateRespVO::setContent);
    }

    public ObservableList<String> paramsProperty() {
        return wrapper.field("params", NotifyTemplateRespVO::getParams, NotifyTemplateRespVO::setParams);
    }

    public StringProperty codeProperty() {
        return wrapper.field("code", NotifyTemplateRespVO::getCode, NotifyTemplateRespVO::setCode);
    }

    public DictDataSimpleRespVO getUserType() {
        return userType.get();
    }

    public ObjectProperty<DictDataSimpleRespVO> userTypeProperty() {
        return userType;
    }



    public void putParams(String params, String value) {
        templateParams.put(params, value);
    }

    public ObservableList<UserSimpleRespVO> getUserItems() {
        return userItems;
    }

    public UserSimpleRespVO getSelUser() {
        return selUser.get();
    }

    public ObjectProperty<UserSimpleRespVO> selUserProperty() {
        return selUser;
    }
}


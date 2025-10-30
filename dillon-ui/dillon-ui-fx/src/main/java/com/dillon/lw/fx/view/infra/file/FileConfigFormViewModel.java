package com.dillon.lw.fx.view.infra.file;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.dillon.lw.module.infra.controller.admin.file.vo.config.FileConfigSaveReqVO;
import com.dillon.lw.module.system.controller.admin.dict.vo.data.DictDataSimpleRespVO;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.dillon.lw.api.infra.FileConfigApi;
import com.dillon.lw.fx.eventbus.EventBusCenter;
import com.dillon.lw.fx.eventbus.event.MessageEvent;
import com.dillon.lw.fx.eventbus.event.UpdateDataEvent;
import com.dillon.lw.fx.http.PayLoad;
import com.dillon.lw.fx.http.Request;
import com.dillon.lw.fx.mvvm.base.BaseViewModel;
import com.dillon.lw.fx.store.AppStore;
import com.dillon.lw.fx.utils.MessageType;
import com.dillon.lw.fx.view.layout.ConfirmDialog;
import io.reactivex.rxjava3.schedulers.Schedulers;
import javafx.application.Platform;
import javafx.beans.property.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.dillon.lw.utils.DictTypeEnum.INFRA_FILE_STORAGE;


public class FileConfigFormViewModel extends BaseViewModel {

    private ObjectProperty<Long> id = new SimpleObjectProperty<>();
    private StringProperty name = new SimpleStringProperty();
    private StringProperty remark = new SimpleStringProperty();
    private StringProperty domain = new SimpleStringProperty();
    private StringProperty basePath = new SimpleStringProperty();
    private StringProperty host = new SimpleStringProperty();
    private StringProperty port = new SimpleStringProperty();
    private StringProperty username = new SimpleStringProperty();
    private StringProperty password = new SimpleStringProperty();
    private StringProperty mode = new SimpleStringProperty();
    private StringProperty endpoint = new SimpleStringProperty();
    private StringProperty bucket = new SimpleStringProperty();
    private StringProperty accessKey = new SimpleStringProperty();
    private StringProperty accessSecret = new SimpleStringProperty();
    private BooleanProperty edit = new SimpleBooleanProperty(false);


    private ObjectProperty<DictDataSimpleRespVO> selStorage = new SimpleObjectProperty<>();

    public void updateData(Long id, boolean isAdd) {

        this.id.set(id);
        edit.set(!isAdd);

        if (id == null) {
            return;
        }


        Request.getInstance().create(FileConfigApi.class).getFileConfig(id)
                .subscribeOn(Schedulers.io())
                .map(new PayLoad<>())
                .observeOn(Schedulers.from(Platform::runLater))
                .subscribe(jsonObject -> {
                    FileConfigSaveReqVO saveReqVO = new FileConfigSaveReqVO();
                    saveReqVO.setName(jsonObject.get("name").getAsString());
                    saveReqVO.setStorage(jsonObject.get("storage").getAsInt());
                    saveReqVO.setRemark(jsonObject.get("remark").getAsString());
                    saveReqVO.setId(jsonObject.get("id").getAsLong());
                    JsonObject config = jsonObject.getAsJsonObject("config");
                    Map<String, Object> map = new HashMap<>();
                    Iterator<String> keys = config.keySet().iterator();
                    while (keys.hasNext()) {
                        String key = keys.next();
                        JsonElement element = config.get(key);
                        String value = null;

                        if (element != null && !element.isJsonNull()) {
                            value = element.getAsString();
                        } else {
                            // 可以选择赋默认值
                            value = "";
                        }
                        map.put(key, value);
                    }
                    saveReqVO.setConfig(map);


                    name.set(saveReqVO.getName());
                    remark.set(saveReqVO.getRemark());
                    if (saveReqVO.getConfig() != null) {

                        domain.set(Convert.toStr(saveReqVO.getConfig().get("domain")));
                        basePath.set(Convert.toStr(saveReqVO.getConfig().get("basePath")));
                        host.set(Convert.toStr(saveReqVO.getConfig().get("host")));
                        port.set(Convert.toStr(saveReqVO.getConfig().get("port")));
                        username.set(Convert.toStr(saveReqVO.getConfig().get("username")));
                        password.set(Convert.toStr(saveReqVO.getConfig().get("password")));
                        mode.set(Convert.toStr(saveReqVO.getConfig().get("mode")));
                        endpoint.set(Convert.toStr(saveReqVO.getConfig().get("endpoint")));
                        bucket.set(Convert.toStr(saveReqVO.getConfig().get("bucket")));
                        accessKey.set(Convert.toStr(saveReqVO.getConfig().get("accessKey")));
                        accessSecret.set(Convert.toStr(saveReqVO.getConfig().get("accessSecret")));

                    }

                    if (isEdit()) {
                        DictDataSimpleRespVO sel = AppStore.getDictDataValueMap(INFRA_FILE_STORAGE).get(saveReqVO.getStorage() + "");
                        selStorage.set(sel);
                    }

                }, e -> {
                    e.printStackTrace();
                });

    }



    public void createFileConfig(ConfirmDialog confirmDialog) {

        Request.getInstance().create(FileConfigApi.class).createFileConfig(getSaveReqVO())
                .subscribeOn(Schedulers.io())
                .map(new PayLoad<>())
                .observeOn(Schedulers.from(Platform::runLater))
                .subscribe(data -> {
                    EventBusCenter.get().post(new UpdateDataEvent("更新文件配置列表"));
                    EventBusCenter.get().post(new MessageEvent("保存成功", MessageType.SUCCESS));
                    confirmDialog.close();
                }, e -> {
                    e.printStackTrace();
                });
    }
    public void updateFileConfig(ConfirmDialog confirmDialog) {

        Request.getInstance().create(FileConfigApi.class).updateFileConfig(getSaveReqVO())
                .subscribeOn(Schedulers.io())
                .map(new PayLoad<>())
                .observeOn(Schedulers.from(Platform::runLater))
                .subscribe(data -> {
                    EventBusCenter.get().post(new UpdateDataEvent("更新文件配置列表"));
                    EventBusCenter.get().post(new MessageEvent("更新成功", MessageType.SUCCESS));
                    confirmDialog.close();
                }, e -> {
                    e.printStackTrace();
                });
    }

    public FileConfigSaveReqVO getSaveReqVO() {
        FileConfigSaveReqVO saveReqVO = new FileConfigSaveReqVO();

        saveReqVO.setName(name.get());
        saveReqVO.setId(id.get());
        saveReqVO.setRemark(remark.get());
        saveReqVO.setStorage(Convert.toInt(selStorage.get().getValue(), null));
        Map<String, Object> config = new HashMap<>();
        createConfig("domain", domain.get(), config);
        createConfig("basePath", basePath.get(), config);
        createConfig("host", host.get(), config);
        createConfig("username", username.get(), config);
        createConfig("mode", mode.get(), config);
        createConfig("endpoint", endpoint.get(), config);
        createConfig("bucket", bucket.get(), config);
        createConfig("accessKey", accessKey.get(), config);
        createConfig("accessSecret", accessSecret.get(), config);
        saveReqVO.setConfig(config);
        return saveReqVO;
    }

    private void createConfig(String key, String value, Map<String, Object> config) {

        if (StrUtil.isNotBlank(value)) {
            config.put(key, value);
        }

    }

    public DictDataSimpleRespVO getSelStorage() {
        return selStorage.get();
    }

    public ObjectProperty<DictDataSimpleRespVO> selStorageProperty() {
        return selStorage;
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public String getRemark() {
        return remark.get();
    }

    public StringProperty remarkProperty() {
        return remark;
    }

    public String getDomain() {
        return domain.get();
    }

    public StringProperty domainProperty() {
        return domain;
    }

    public String getBasePath() {
        return basePath.get();
    }

    public StringProperty basePathProperty() {
        return basePath;
    }

    public String getHost() {
        return host.get();
    }

    public StringProperty hostProperty() {
        return host;
    }

    public String getPort() {
        return port.get();
    }

    public StringProperty portProperty() {
        return port;
    }

    public String getUsername() {
        return username.get();
    }

    public StringProperty usernameProperty() {
        return username;
    }

    public String getPassword() {
        return password.get();
    }

    public StringProperty passwordProperty() {
        return password;
    }

    public String getMode() {
        return mode.get();
    }

    public StringProperty modeProperty() {
        return mode;
    }

    public String getEndpoint() {
        return endpoint.get();
    }

    public StringProperty endpointProperty() {
        return endpoint;
    }

    public String getBucket() {
        return bucket.get();
    }

    public StringProperty bucketProperty() {
        return bucket;
    }

    public String getAccessKey() {
        return accessKey.get();
    }

    public StringProperty accessKeyProperty() {
        return accessKey;
    }

    public String getAccessSecret() {
        return accessSecret.get();
    }

    public StringProperty accessSecretProperty() {
        return accessSecret;
    }

    public long getId() {
        return id.get();
    }

    public ObjectProperty<Long> idProperty() {
        return id;
    }

    public boolean isEdit() {
        return edit.get();
    }

    public BooleanProperty editProperty() {
        return edit;
    }
}

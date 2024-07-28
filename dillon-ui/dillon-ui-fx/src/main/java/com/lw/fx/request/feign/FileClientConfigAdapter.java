package com.lw.fx.request.feign;

import cn.hutool.core.util.StrUtil;
import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.lw.dillon.admin.module.infra.framework.file.core.client.FileClientConfig;
import com.lw.dillon.admin.module.infra.framework.file.core.client.db.DBFileClientConfig;
import com.lw.dillon.admin.module.infra.framework.file.core.client.ftp.FtpFileClientConfig;
import com.lw.dillon.admin.module.infra.framework.file.core.client.local.LocalFileClientConfig;
import com.lw.dillon.admin.module.infra.framework.file.core.client.s3.S3FileClientConfig;
import com.lw.dillon.admin.module.infra.framework.file.core.client.sftp.SftpFileClientConfig;

import java.io.IOException;

// TypeAdapter 实现
public class FileClientConfigAdapter extends TypeAdapter<FileClientConfig> {

    @Override
    public void write(JsonWriter out, FileClientConfig value) throws IOException {
        out.beginObject();

        out.endObject();
    }

    @Override
    public FileClientConfig read(JsonReader in) throws IOException {
        JsonObject jsonObject = JsonParser.parseReader(in).getAsJsonObject();
        String className = jsonObject.get("@class").getAsString();
        className = StrUtil.subAfter(className, ".", true);

        switch (className) {
            case "DBFileClientConfig":
                return new Gson().fromJson(jsonObject, DBFileClientConfig.class);
            case "FtpFileClientConfig":
                return new Gson().fromJson(jsonObject, FtpFileClientConfig.class);
            case "LocalFileClientConfig":
                return new Gson().fromJson(jsonObject, LocalFileClientConfig.class);
            case "SftpFileClientConfig":
                return new Gson().fromJson(jsonObject, SftpFileClientConfig.class);
            case "S3FileClientConfig":
                return new Gson().fromJson(jsonObject, S3FileClientConfig.class);
            default:
                throw new JsonParseException("未知的 FileClientConfig 类型：" + className);
        }
    }
}
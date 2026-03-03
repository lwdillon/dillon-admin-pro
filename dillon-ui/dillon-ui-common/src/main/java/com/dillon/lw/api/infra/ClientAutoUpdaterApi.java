package com.dillon.lw.api.infra;


import com.dillon.lw.api.BaseApi;
import com.dillon.lw.framework.common.pojo.CommonResult;
import com.dillon.lw.module.infra.controller.admin.client.vo.ClientUpdateRespVO;
import com.dtflys.forest.callback.OnProgress;
import com.dtflys.forest.annotation.Get;
import com.dtflys.forest.extensions.DownloadFile;

import java.io.File;

public interface ClientAutoUpdaterApi extends BaseApi {


    @Get("infra/client/update/latest/swing")
    CommonResult<ClientUpdateRespVO> latestSwing();

    @Get("infra/client/update/latest/javafx")
    CommonResult<ClientUpdateRespVO> latestJavaFx();

    @DownloadFile(dir = "${0}", filename = "${1}")
    @Get("infra/client/update/download/swing")
    File downloadFile(String dir, String filename, OnProgress onProgress);

    @DownloadFile(dir = "${0}", filename = "${1}")
    @Get("infra/client/update/download/javafx")
    File downloadJavaFxFile(String dir, String filename, OnProgress onProgress);
}

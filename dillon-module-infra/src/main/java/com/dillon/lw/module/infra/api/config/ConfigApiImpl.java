package com.dillon.lw.module.infra.api.config;

import com.dillon.lw.module.infra.dal.dataobject.config.ConfigDO;
import com.dillon.lw.module.infra.service.config.ConfigService;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;

/**
 * 参数配置 API 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class ConfigApiImpl implements ConfigApi {

    @Resource
    private ConfigService configService;

    @Override
    public String getConfigValueByKey(String key) {
        ConfigDO config = configService.getConfigByKey(key);
        return config != null ? config.getValue() : null;
    }

}

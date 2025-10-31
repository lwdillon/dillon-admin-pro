package com.dillon.lw.module.infra.api.config;

/**
 * 参数配置 API 接口
 *
 * @author liwen
 */
public interface ConfigApi {

    /**
     * 根据参数键查询参数值
     *
     * @param key 参数键
     * @return 参数值
     */
    String getConfigValueByKey(String key);

}

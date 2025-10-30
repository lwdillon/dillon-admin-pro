package com.dillon.lw.module.system.framework.web.config;

import com.dillon.lw.framework.swagger.config.DillonSwaggerAutoConfiguration;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * system 模块的 web 组件的 Configuration
 *
 * @author 芋道源码
 */
@Configuration(proxyBeanMethods = false)
public class SystemWebConfiguration {

    /**
     * system 模块的 API 分组
     */
    @Bean
    public GroupedOpenApi systemGroupedOpenApi() {
        return DillonSwaggerAutoConfiguration.buildGroupedOpenApi("system");
    }

}

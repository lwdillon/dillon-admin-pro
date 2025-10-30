package com.dillon.lw.framework.encrypt.config;

import com.dillon.lw.framework.common.enums.WebFilterOrderEnum;
import com.dillon.lw.framework.encrypt.core.filter.ApiEncryptFilter;
import com.dillon.lw.framework.web.config.WebProperties;
import com.dillon.lw.framework.web.core.handler.GlobalExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import static com.dillon.lw.framework.web.config.DillonWebAutoConfiguration.createFilterBean;

@AutoConfiguration
@Slf4j
@EnableConfigurationProperties(ApiEncryptProperties.class)
@ConditionalOnProperty(prefix = "dillon.api-encrypt", name = "enable", havingValue = "true")
public class DillonApiEncryptAutoConfiguration {

    @Bean
    public FilterRegistrationBean<ApiEncryptFilter> apiEncryptFilter(WebProperties webProperties,
                                                                     ApiEncryptProperties apiEncryptProperties,
                                                                     RequestMappingHandlerMapping requestMappingHandlerMapping,
                                                                     GlobalExceptionHandler globalExceptionHandler) {
        ApiEncryptFilter filter = new ApiEncryptFilter(webProperties, apiEncryptProperties,
                requestMappingHandlerMapping, globalExceptionHandler);
        return createFilterBean(filter, WebFilterOrderEnum.API_ENCRYPT_FILTER);

    }

}

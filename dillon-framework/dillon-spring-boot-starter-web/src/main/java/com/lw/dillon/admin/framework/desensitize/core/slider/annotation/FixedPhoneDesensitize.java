package com.lw.dillon.admin.framework.desensitize.core.slider.annotation;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.lw.dillon.admin.framework.desensitize.core.base.annotation.DesensitizeBy;
import com.lw.dillon.admin.framework.desensitize.core.slider.handler.FixedPhoneDesensitization;

import java.lang.annotation.*;

/**
 * 固定电话
 *
 * @author gaibu
 */
@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@DesensitizeBy(handler = FixedPhoneDesensitization.class)
public @interface FixedPhoneDesensitize {

    /**
     * 前缀保留长度
     */
    int prefixKeep() default 4;

    /**
     * 后缀保留长度
     */
    int suffixKeep() default 2;

    /**
     * 替换规则，固定电话;比如：01086551122 脱敏之后为 0108*****22
     */
    String replacer() default "*";

}

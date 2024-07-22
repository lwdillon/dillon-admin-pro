package com.lw.dillon.admin.framework.desensitize.core.slider.annotation;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.lw.dillon.admin.framework.desensitize.core.base.annotation.DesensitizeBy;
import com.lw.dillon.admin.framework.desensitize.core.slider.handler.PasswordDesensitization;

import java.lang.annotation.*;

/**
 * 密码
 *
 * @author gaibu
 */
@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@DesensitizeBy(handler = PasswordDesensitization.class)
public @interface PasswordDesensitize {

    /**
     * 前缀保留长度
     */
    int prefixKeep() default 0;

    /**
     * 后缀保留长度
     */
    int suffixKeep() default 0;

    /**
     * 替换规则，密码;
     *
     * 比如：123456 脱敏之后为 ******
     */
    String replacer() default "*";

}

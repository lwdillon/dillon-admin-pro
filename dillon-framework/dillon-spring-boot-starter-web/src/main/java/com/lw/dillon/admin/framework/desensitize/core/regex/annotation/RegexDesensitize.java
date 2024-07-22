package com.lw.dillon.admin.framework.desensitize.core.regex.annotation;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.lw.dillon.admin.framework.desensitize.core.base.annotation.DesensitizeBy;
import com.lw.dillon.admin.framework.desensitize.core.regex.handler.DefaultRegexDesensitizationHandler;

import java.lang.annotation.*;

/**
 * 正则脱敏注解
 *
 * @author gaibu
 */
@Documented
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@DesensitizeBy(handler = DefaultRegexDesensitizationHandler.class)
public @interface RegexDesensitize {

    /**
     * 匹配的正则表达式（默认匹配所有）
     */
    String regex() default "^[\\s\\S]*$";

    /**
     * 替换规则，会将匹配到的字符串全部替换成 replacer
     *
     * 例如：regex=123; replacer=******
     * 原始字符串 123456789
     * 脱敏后字符串 ******456789
     */
    String replacer() default "******";
}

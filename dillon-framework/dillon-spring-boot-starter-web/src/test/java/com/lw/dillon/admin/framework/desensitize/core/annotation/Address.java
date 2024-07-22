package com.lw.dillon.admin.framework.desensitize.core.annotation;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.lw.dillon.admin.framework.desensitize.core.DesensitizeTest;
import com.lw.dillon.admin.framework.desensitize.core.base.annotation.DesensitizeBy;
import com.lw.dillon.admin.framework.desensitize.core.handler.AddressHandler;

import java.lang.annotation.*;

/**
 * 地址
 *
 * 用于 {@link DesensitizeTest} 测试使用
 *
 * @author gaibu
 */
@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@DesensitizeBy(handler = AddressHandler.class)
public @interface Address {

    String replacer() default "*";

}

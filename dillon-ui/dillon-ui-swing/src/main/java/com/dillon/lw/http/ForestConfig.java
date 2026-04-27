package com.dillon.lw.http;

import com.dillon.lw.forest.interceptors.ErrorInterceptor;
import com.dillon.lw.forest.interceptors.HASmartSource;
import com.dillon.lw.forest.logging.SensitiveLogHandler;
import com.dillon.lw.http.interceptors.TokenInterceptor;
import com.dtflys.forest.Forest;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.interceptor.Interceptor;

import java.util.ArrayList;
import java.util.List;

/**
 * Forest 全局配置类
 * <p>
 * 统一配置：
 * 1. 全局拦截器（如 Token、异常处理）
 * 2. 智能主备地址选择策略（HASmartSource）
 * <p>
 * 适用于：
 * - Swing / JavaFX 客户端
 * - 非 Spring 环境下使用 Forest 的 HTTP 调用
 */
public class ForestConfig {

    /**
     * 主服务是否存活的状态标记
     * <p>
     * true  ：主服务可用，优先走主地址
     * false ：主服务不可用，切换至备用地址
     * <p>
     * volatile：
     * - 保证多线程环境下状态可见性
     * - 供拦截器 / 地址源实时读取
     */
    public static volatile boolean isMasterAlive = true;

    /**
     * 初始化 Forest 全局配置
     * <p>
     * 建议在应用启动时（main 方法或启动类）调用一次
     */
    public static void init() {

        // 获取 Forest 的全局配置对象（单例）
        ForestConfiguration configuration = Forest.config();

        // 如果需要统一前缀路径，可通过变量方式配置
        // configuration.setVariableValue("baseUrl", "/admin-api/");

        // ========== 全局拦截器配置 ==========
        // 注意：拦截器的执行顺序与添加顺序一致
        List<Class<? extends Interceptor>> interceptors = new ArrayList<>();

        // 异常拦截器：统一处理请求异常、超时等情况
        interceptors.add(ErrorInterceptor.class);

        // Token 拦截器：自动为请求添加认证信息
        interceptors.add(TokenInterceptor.class);

        // 设置全局拦截器链
        configuration.setInterceptors(interceptors);
        configuration.setLogHandler(new SensitiveLogHandler());

        // ========== 高可用地址策略 ==========
        // 自定义地址源：
        // - 根据 isMasterAlive 动态选择主 / 备服务地址
        // - 支持主备切换、故障隔离
        configuration.setBaseAddressSourceClass(HASmartSource.class);
    }
}

package com.dillon.lw.framework.idempotent.config;

import com.dillon.lw.framework.idempotent.core.aop.IdempotentAspect;
import com.dillon.lw.framework.idempotent.core.keyresolver.impl.DefaultIdempotentKeyResolver;
import com.dillon.lw.framework.idempotent.core.keyresolver.impl.ExpressionIdempotentKeyResolver;
import com.dillon.lw.framework.idempotent.core.keyresolver.IdempotentKeyResolver;
import com.dillon.lw.framework.idempotent.core.keyresolver.impl.UserIdempotentKeyResolver;
import com.dillon.lw.framework.idempotent.core.redis.IdempotentRedisDAO;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import com.dillon.lw.framework.redis.config.DillonRedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;

@AutoConfiguration(after = DillonRedisAutoConfiguration.class)
public class DillonIdempotentConfiguration {

    @Bean
    public IdempotentAspect idempotentAspect(List<IdempotentKeyResolver> keyResolvers, IdempotentRedisDAO idempotentRedisDAO) {
        return new IdempotentAspect(keyResolvers, idempotentRedisDAO);
    }

    @Bean
    public IdempotentRedisDAO idempotentRedisDAO(StringRedisTemplate stringRedisTemplate) {
        return new IdempotentRedisDAO(stringRedisTemplate);
    }

    // ========== 各种 IdempotentKeyResolver Bean ==========

    @Bean
    public DefaultIdempotentKeyResolver defaultIdempotentKeyResolver() {
        return new DefaultIdempotentKeyResolver();
    }

    @Bean
    public UserIdempotentKeyResolver userIdempotentKeyResolver() {
        return new UserIdempotentKeyResolver();
    }

    @Bean
    public ExpressionIdempotentKeyResolver expressionIdempotentKeyResolver() {
        return new ExpressionIdempotentKeyResolver();
    }

}

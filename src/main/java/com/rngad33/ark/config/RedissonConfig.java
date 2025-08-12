package com.rngad33.ark.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Redisson配置
 */
@Configuration
public class RedissonConfig {

    /**
     * 初始化Redisson客户端
     *
     * @param redisProperties
     * @return
     */
    @Bean
    public RedissonClient redissonClient(RedisProperties redisProperties) {
        Config config = new Config();
        // 构建Redis连接地址
        String address = "redis://" + redisProperties.getHost() + ":" + redisProperties.getPort();
        // 配置Redisson连接
        config.useSingleServer()
                .setAddress(address)
                .setDatabase(redisProperties.getDatabase());
        // 如果有密码则设置密码
        if (redisProperties.getPassword() != null) {
            config.useSingleServer().setPassword(redisProperties.getPassword());
        }
        return Redisson.create(config);
    }
}

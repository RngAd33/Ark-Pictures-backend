package com.rngad33.web.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 对象存储配置
 */
@Configuration
@ConfigurationProperties(prefix = "cos.client")
public class CosClientConfig {

    /**
     * 域名
     */
    private String host;

    /**
     * id
     */
    private String secretId;

    /**
     * 密钥
     */
    private String secretKey;

    /**
     * 地区
     */
    private String region;

    /**
     * 桶名
     */
    private String bucket;

}
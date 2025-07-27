package com.lihainuo.imgify.server.config;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.region.Region;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 腾讯云cos配置
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "cos.client")
public class CosClientConfig {

    /**
     * 存储桶域名
     */
    private String host;

    /**
     * 存储桶 SecretId
     */
    private String secretId;

    /**
     * 存储桶 SecretKey
     */
    private String secretKey;

    /**
     * 存储桶所属地域
     */
    private String region;

    /**
     * 存储桶名称
     */
    private String bucket;

    @Bean
    public COSClient cosClient() {
        COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);
        ClientConfig clientConfig = new ClientConfig(new Region(region));
        return new COSClient(cred, clientConfig);
    }

}

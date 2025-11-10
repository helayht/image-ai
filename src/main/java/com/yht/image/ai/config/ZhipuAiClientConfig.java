package com.yht.image.ai.config;

import ai.z.openapi.ZhipuAiClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description
 * @Author hela
 * @Date 2025/11/6
 */
@Configuration
public class ZhipuAiClientConfig {
    @Value("${ai.api-key.chat-glm}")
    private String API_KEY;

    @Bean("zhipuAiClient")
    public ZhipuAiClient zhipuAiClient(){
        return ZhipuAiClient.builder().apiKey(API_KEY).build();
    }
}

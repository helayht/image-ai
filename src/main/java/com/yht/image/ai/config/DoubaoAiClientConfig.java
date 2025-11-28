package com.yht.image.ai.config;

import ai.z.openapi.ZhipuAiClient;
import com.volcengine.ark.runtime.service.ArkService;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * @Description
 * @Author hela
 * @Date 2025/11/17
 */
@Configuration
public class DoubaoAiClientConfig {
    @Value("${ai.api-key.doubao}")
    private String API_KEY;
    static String baseUrl = "https://ark.cn-beijing.volces.com/api/v3";

    @Bean("doubaoAiClient")
    public ArkService doubaoAiClient(){
        ConnectionPool connectionPool = new ConnectionPool(5, 1, TimeUnit.SECONDS);
        Dispatcher dispatcher = new Dispatcher();
        return ArkService.builder()
                .baseUrl(baseUrl)
                .dispatcher(dispatcher)
                .connectionPool(connectionPool)
                .apiKey(API_KEY)
                .build();
    }
}

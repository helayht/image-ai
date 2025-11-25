package com.yht.image.ai.config;

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
 * @Date 2025/11/24
 */
@Configuration
public class DoubaoMultimodalConfig {
    @Value("${ai.api-key.doubao}")
    private String API_KEY;
    static String baseUrl = "https://ark.cn-beijing.volces.com/api/v3";
    static ConnectionPool connectionPool = new ConnectionPool(5, 1, TimeUnit.SECONDS);
    static Dispatcher dispatcher = new Dispatcher();

    @Bean("doubaoMultimodalClient")
    public ArkService doubaoAiClient() {
        return ArkService
                .builder()
                .dispatcher(dispatcher)
                .connectionPool(connectionPool)
                .baseUrl(baseUrl)
                .apiKey(API_KEY)
                .build();
    }
}

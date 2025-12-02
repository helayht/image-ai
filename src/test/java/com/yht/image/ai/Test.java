package com.yht.image.ai;

import com.alibaba.fastjson2.JSON;
import com.yht.image.ai.controller.dto.ChatRequestDTO;
import com.yht.image.ai.service.IImageService;
import com.yht.image.ai.service.ILLMService;
import com.yht.image.ai.service.ai.IAIService;
import com.yht.image.ai.service.entity.ChatResultEntity;
import com.yht.image.ai.types.common.Constants;
import com.yht.image.ai.types.util.IRedisService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @Description
 * @Author hela
 * @Date 2025/11/5
 */
@SpringBootTest
public class Test {
    @Resource
    private IImageService imageService;
    @Resource
    private IRedisService redisService;
    @Resource
    private ILLMService llmService;
    @Resource
    ILLMService multimodalService;

    @Value("${server.domain}")
    private String domain;
    @Resource(name = "doubao-seedream-4-0")
    IAIService doubaoService;
    @org.junit.jupiter.api.Test
    public void test() throws InterruptedException {
        ChatRequestDTO chatRequestDTO = new ChatRequestDTO();
        chatRequestDTO.setPrompt("生成一张月亮照片");
        List<String> models = new ArrayList<>();
        models.add("cogview-3-flash");
        models.add("cogview-3-flash");
        chatRequestDTO.setModels(models);
        chatRequestDTO.setSize("1024x1024");
        String taskId = imageService.textToImage(chatRequestDTO);
        System.out.println(taskId);
        CountDownLatch latch = new CountDownLatch(2);
        latch.await();
    }

    @org.junit.jupiter.api.Test
    public void test1() {
       redisService.setValue("openId","code");
       System.out.println((String) redisService.getValue("openId"));
    }

    @org.junit.jupiter.api.Test
    public void DoubaoTest() throws IOException {
        llmService.vedioTOText(domain+"/uploads/vedio.mp4");
    }
}

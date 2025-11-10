package com.yht.image.ai;

import com.alibaba.fastjson2.JSON;
import com.yht.image.ai.controller.dto.ChatRequestDTO;
import com.yht.image.ai.service.IImageService;
import com.yht.image.ai.service.ai.impl.ChatGLMService;
import com.yht.image.ai.service.entity.ChatResultEntity;
import com.yht.image.ai.types.common.Constants;
import com.yht.image.ai.types.util.IRedisService;
import jakarta.annotation.Resource;
import org.springframework.boot.test.context.SpringBootTest;

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
    private ChatGLMService chatGLMService;
    @Resource
    private IImageService imageService;
    @Resource
    private IRedisService redisService;
    @org.junit.jupiter.api.Test
    public void test() throws InterruptedException {
        ChatRequestDTO chatRequestDTO = new ChatRequestDTO();
        chatRequestDTO.setPrompt("生成一张月亮照片");
        List<String> models = new ArrayList<>();
        models.add("cogview-3-flash");
        models.add("cogview-3-flash");
        chatRequestDTO.setModels(models);
        chatRequestDTO.setSize("1024x1024");
        String taskId = imageService.TextToImage(chatRequestDTO);
        System.out.println(taskId);
        CountDownLatch latch = new CountDownLatch(2);
        latch.await();
    }

    @org.junit.jupiter.api.Test
    public void test1() {
       String value = redisService.getValue(Constants.RedisKey.TASK_RESULT_KEY+"19201326-7fcf-4d41-8882-09b04c64b3b9");
       List<ChatResultEntity> results = JSON.parseArray(value, ChatResultEntity.class);
       System.out.println(results);
    }

}

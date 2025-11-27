package com.yht.image.ai;

import com.alibaba.fastjson2.JSON;
import com.yht.image.ai.controller.dto.ChatRequestDTO;
import com.yht.image.ai.service.IImageService;
import com.yht.image.ai.service.IMultimodalService;
import com.yht.image.ai.service.ai.IAIService;
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
    private IImageService imageService;
    @Resource
    private IRedisService redisService;
    @Resource
    IMultimodalService multimodalService;
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
       String value = redisService.getValue(Constants.RedisKey.TASK_RESULT_KEY+"ee0e40a2-3ab8-4f96-a63e-3f7a621746b3");
       List<ChatResultEntity> results = JSON.parseArray(value, ChatResultEntity.class);
       System.out.println(results);
    }

    @org.junit.jupiter.api.Test
    public void DoubaoTest(){
        multimodalService.imageTOText("https://files.imagetourl.net/uploads/1763967123863-85eb91ab-16be-4df3-be97-b1eabbb92a0d.jpg");
    }
}

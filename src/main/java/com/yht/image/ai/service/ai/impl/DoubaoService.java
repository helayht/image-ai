package com.yht.image.ai.service.ai.impl;

import com.volcengine.ark.runtime.model.images.generation.GenerateImagesRequest;
import com.volcengine.ark.runtime.model.images.generation.ImagesResponse;
import com.volcengine.ark.runtime.model.images.generation.ResponseFormat;
import com.volcengine.ark.runtime.service.ArkService;
import com.yht.image.ai.controller.dto.ChatRequestDTO;
import com.yht.image.ai.service.ai.IAIService;
import com.yht.image.ai.service.entity.ChatResultEntity;
import jakarta.annotation.Resource;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @Description
 * @Author hela
 * @Date 2025/11/17
 */
@Component("doubao-seedream-4-0")
public class DoubaoService implements IAIService {
    @Resource(name = "doubaoAiClient")
    private ArkService client;
    @Override
    public ChatResultEntity creatImage(ChatRequestDTO chatRequestDTO) {
        GenerateImagesRequest generateRequest = GenerateImagesRequest.builder()
                .model("doubao-seedream-4-0-250828")
                .prompt(chatRequestDTO.getPrompt())
                .size(chatRequestDTO.getSize())
                .sequentialImageGeneration("disabled")
                .responseFormat(ResponseFormat.Url)
                .stream(false)
                .watermark(false)
                .build();
        ImagesResponse imagesResponse = client.generateImages(generateRequest);
        ChatResultEntity chatResultEntity = new ChatResultEntity();
        chatResultEntity.setImageURL(imagesResponse.getData().get(0).getUrl());
        chatResultEntity.setDateTime(new Date());
        chatResultEntity.setModelId("doubao-seedream-4-0");
        chatResultEntity.setModelName("豆包");
        client.shutdownExecutor();
        return chatResultEntity;
    }
}

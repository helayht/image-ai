package com.yht.image.ai.service.ai.impl;

import ai.z.openapi.ZhipuAiClient;
import ai.z.openapi.core.Constants;
import ai.z.openapi.service.image.CreateImageRequest;
import ai.z.openapi.service.image.ImageResponse;
import com.yht.image.ai.controller.dto.ChatRequestDTO;
import com.yht.image.ai.service.ai.IAIService;
import com.yht.image.ai.service.entity.ChatResultEntity;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @Description
 * @Author hela
 * @Date 2025/11/5
 */
@Component("cogview-3-flash")
public class ChatGLMService implements IAIService {
    @Resource
    ZhipuAiClient client;

    public ChatResultEntity creatImage(ChatRequestDTO chatRequestDTO) {
        // Create image generation request
        CreateImageRequest request = CreateImageRequest.builder()
                .model(Constants.ModelCogView3Flash)
                .prompt(chatRequestDTO.getPrompt())
                .size(chatRequestDTO.getSize())
                .watermarkEnabled(false)
                .build();
        ImageResponse response = client.images().createImage(request);
        if (!response.isSuccess()) {
            throw new RuntimeException(response.getMsg());
        }
        ChatResultEntity chatResultEntity = new ChatResultEntity();
        chatResultEntity.setImageURL(response.getData().getData().get(0).getUrl());
        chatResultEntity.setDateTime(new Date());
        chatResultEntity.setModelId(chatResultEntity.getModelId());
        chatResultEntity.setModelName("智谱清言");
        return chatResultEntity;
    }
}

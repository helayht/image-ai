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

import java.util.Arrays;
import java.util.Date;
import java.util.List;
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

    public ChatResultEntity imageCreatImage(ChatRequestDTO chatRequestDTO,List images,int maxImages){
        GenerateImagesRequest.SequentialImageGenerationOptions sequentialImageGenerationOptions = new GenerateImagesRequest.SequentialImageGenerationOptions();
        sequentialImageGenerationOptions.setMaxImages(maxImages);
        GenerateImagesRequest generateRequest = GenerateImagesRequest.builder()
                .model("doubao-seedream-5-0-260128")
                .prompt(chatRequestDTO.getPrompt())
                .image(images)
                .responseFormat(ResponseFormat.Url)
                .size(chatRequestDTO.getSize())
                .sequentialImageGeneration("auto")
                .sequentialImageGenerationOptions(sequentialImageGenerationOptions)
                .stream(true)
                .watermark(true)
                .build();

        System.out.println(generateRequest.toString());

        String[] generatedUrls = new String[maxImages];
        final int[] index = {0};

        client.streamGenerateImages(generateRequest)
                .doOnError(Throwable::printStackTrace)
                .blockingForEach(
                        choice -> {
                            if (choice == null) return;
                            if ("image_generation.partial_failed".equals(choice.getType())) {
                                if (choice.getError() != null) {
                                    System.err.println("Stream generate images error: " + choice.getError());
                                    if (choice.getError().getCode() != null && choice.getError().getCode().equals("InternalServiceError")) {
                                        throw new RuntimeException("Server error, terminating stream.");
                                    }
                                }
                            }
                            else if ("image_generation.partial_succeeded".equals(choice.getType())) {
                                if (choice.getError() == null && choice.getUrl() != null && !choice.getUrl().isEmpty()) {
                                    System.out.printf("recv.Size: %s, recv.Url: %s%n", choice.getSize(), choice.getUrl());
                                    generatedUrls[index[0]++] = choice.getUrl();
                                }
                            }
                            else if ("image_generation.completed".equals(choice.getType())) {
                                if (choice.getError() == null && choice.getUsage() != null) {
                                    System.out.println("recv.Usage: " + choice.getUsage().toString());
                                }
                            }
                        }
                );

        ChatResultEntity chatResultEntity = new ChatResultEntity();
        String generatedUrl = "";
        for(int i = 0; i < generatedUrls.length; i++){
            generatedUrl += generatedUrls[i];
            if(i != generatedUrls.length - 1){
                generatedUrl += ",";
            }
        }
        chatResultEntity.setImageURL(generatedUrl);
        chatResultEntity.setDateTime(new Date());
        chatResultEntity.setModelId("doubao-seedream-5-0-260128");
        chatResultEntity.setModelName("豆包");
        client.shutdownExecutor();
        return chatResultEntity;
    }

}

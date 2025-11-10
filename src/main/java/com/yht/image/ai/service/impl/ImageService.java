package com.yht.image.ai.service.impl;

import com.alibaba.fastjson2.JSON;
import com.yht.image.ai.controller.dto.ChatRequestDTO;
import com.yht.image.ai.controller.dto.TaskResponseDTO;
import com.yht.image.ai.service.IImageService;
import com.yht.image.ai.service.ai.IAIService;
import com.yht.image.ai.service.entity.ChatResultEntity;
import com.yht.image.ai.types.common.Constants;
import com.yht.image.ai.types.util.IRedisService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * @Description
 * @Author hela
 * @Date 2025/11/5
 */
@Service
public class ImageService implements IImageService {
    private final Map<String, IAIService> aiServiceMap;
    private IRedisService redisService;

    public ImageService(Map<String, IAIService> aiServiceMap, IRedisService redisService) {
        this.aiServiceMap = aiServiceMap;
        this.redisService = redisService;
    }

    @Override
    public String TextToImage(ChatRequestDTO chatRequestDTO) {
        String taskId = UUID.randomUUID().toString();
        List<CompletableFuture<ChatResultEntity>> futures = new ArrayList<>();

        for (String model : chatRequestDTO.getModels()) {
            IAIService aiService = aiServiceMap.get(model);
            if (aiService == null) {
                throw new RuntimeException("模型不存在");
            }
            CompletableFuture<ChatResultEntity> future = CompletableFuture.supplyAsync(() -> {
                return aiService.creatImage(chatRequestDTO);
            });
            futures.add(future);
        }

        // 等待所有任务完成并将结果保存到 Redis
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenRun(() -> {
                    List<ChatResultEntity> results = new ArrayList<>();
                    for (CompletableFuture<ChatResultEntity> future : futures) {
                        try {
                            results.add(future.get());
                        } catch (Exception e) {
                            redisService.setValue(Constants.RedisKey.TASK_STATUS_KEY+taskId, "failed");
                            throw new RuntimeException(e);
                        }
                    }
                    redisService.setValue(Constants.RedisKey.TASK_STATUS_KEY+taskId, "success");
                    redisService.setValue(Constants.RedisKey.TASK_RESULT_KEY +taskId, JSON.toJSONString(results));
                });
        redisService.setValue(Constants.RedisKey.TASK_STATUS_KEY+taskId, "running");
        return taskId;
    }

    @Override
    public TaskResponseDTO queryResultByTaskId(String taskId) {
        String status = redisService.getValue(Constants.RedisKey.TASK_STATUS_KEY+taskId);
        String result = redisService.getValue(Constants.RedisKey.TASK_RESULT_KEY + taskId);
        List<ChatResultEntity> results = JSON.parseArray(result, ChatResultEntity.class);
        TaskResponseDTO taskResponseDTO = new TaskResponseDTO();
        taskResponseDTO.setStatus(status);
        taskResponseDTO.setChatResultEntityList(results);
        return taskResponseDTO;
    }
}

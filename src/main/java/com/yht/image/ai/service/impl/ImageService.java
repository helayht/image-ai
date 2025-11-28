package com.yht.image.ai.service.impl;

import com.alibaba.fastjson2.JSON;
import com.yht.image.ai.controller.dto.ChatRequestDTO;
import com.yht.image.ai.controller.dto.TaskResponseDTO;
import com.yht.image.ai.service.IImageService;
import com.yht.image.ai.service.ILLMService;
import com.yht.image.ai.service.ai.IAIService;
import com.yht.image.ai.service.entity.ChatResultEntity;
import com.yht.image.ai.types.common.Constants;
import com.yht.image.ai.types.util.IRedisService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
@Slf4j
public class ImageService implements IImageService {
    private final Map<String, IAIService> aiServiceMap;
    private IRedisService redisService;

    @Resource
    private ILLMService llmService;

    @Value("${server.domain}")
    private String serverDomain;
    @Value("${upload.path}")
    private String uploadPath;

    public ImageService(Map<String, IAIService> aiServiceMap, IRedisService redisService) {
        this.aiServiceMap = aiServiceMap;
        this.redisService = redisService;
    }

    @Override
    public String textToImage(ChatRequestDTO chatRequestDTO) {
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
        redisService.setValue(Constants.RedisKey.TASK_CONVERSATIONS_KEY+taskId,chatRequestDTO.getConversationsId());
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

    @Override
    public String imageToImage(ChatRequestDTO chatRequestDTO, String imageUrl) throws IOException {
        log.info("生成imageUrl:{}", imageUrl);
        String description = llmService.imageTOText(imageUrl);
        String prompt =llmService.imagePromptOptimization(chatRequestDTO.getPrompt(),description);
        chatRequestDTO.setPrompt(prompt);
        return textToImage(chatRequestDTO);
    }

    @Override
    public String createFileUrl(MultipartFile file) throws IOException {
        //获取上传文件全名
        String filename = file.getOriginalFilename();
        //截取文件后缀名
        String s = filename.substring(filename.lastIndexOf("."));
        //使用UUID拼接文件后缀名 防止文件名重复 导致被覆盖
        String replace = UUID.randomUUID().toString().replace("-", "")+s;
        Path path = Paths.get(uploadPath, replace);
        Files.createDirectories(path.getParent());
        file.transferTo(path);
        String domain = serverDomain.endsWith("/") ? serverDomain : serverDomain + "/";
        return domain + "uploads/" + replace;
    }

    @Override
    public String vedioToImage(ChatRequestDTO chatRequestDTO, String vedioUrl) throws IOException {
        log.info("生成vedioUrl:{}", vedioUrl);
        String description = llmService.vedioTOText(vedioUrl);
        String prompt = llmService.vedioPromptOptimization(chatRequestDTO.getPrompt(),description);
        chatRequestDTO.setPrompt(prompt);
        return textToImage(chatRequestDTO);
    }
}

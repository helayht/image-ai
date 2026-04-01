package com.yht.image.ai.service.impl;

import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.exception.UploadFileException;
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
        Integer conversationsId = null;
        if (redisService.isExists(Constants.RedisKey.TASK_CONVERSATIONS_KEY + taskId)) {
            conversationsId = redisService.getValue(Constants.RedisKey.TASK_CONVERSATIONS_KEY + taskId);
        }
        List<ChatResultEntity> results = JSON.parseArray(result, ChatResultEntity.class);
        TaskResponseDTO taskResponseDTO = new TaskResponseDTO();
        taskResponseDTO.setStatus(status);
        taskResponseDTO.setChatResultEntityList(results);
        taskResponseDTO.setConversationsId(conversationsId);
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

    @Override
    public String templateImage(String templateCode, String username, MultipartFile[] images) throws IOException {
        if (images == null || images.length == 0) {
            throw new RuntimeException("图片不能为空");
        }
        IAIService aiService = aiServiceMap.get("doubao-seedream-4-0");
        if (aiService == null) {
            throw new RuntimeException("模型不存在");
        }

        ChatRequestDTO chatRequestDTO = new ChatRequestDTO();
        chatRequestDTO.setUsername(username);
        chatRequestDTO.setPrompt(getTemplatePrompt(templateCode));
        chatRequestDTO.setSize("2K");

        List<String> imageUrls = new ArrayList<>();
        for (MultipartFile image : images) {
            imageUrls.add(createFileUrl(image));
        }

        String taskId = UUID.randomUUID().toString();
        redisService.setValue(Constants.RedisKey.TASK_STATUS_KEY + taskId, "running");

        CompletableFuture
                .supplyAsync(() -> {
                    try {
                        return aiService.imageCreatImage(chatRequestDTO, imageUrls, 2);
                    } catch (NoApiKeyException e) {
                        throw new RuntimeException(e);
                    } catch (UploadFileException e) {
                        throw new RuntimeException(e);
                    }
                })
                .thenAccept(result -> {
                    List<ChatResultEntity> results = new ArrayList<>();
                    results.add(result);
                    redisService.setValue(Constants.RedisKey.TASK_RESULT_KEY + taskId, JSON.toJSONString(results));
                    redisService.setValue(Constants.RedisKey.TASK_STATUS_KEY + taskId, "success");
                })
                .exceptionally(exception -> {
                    redisService.setValue(Constants.RedisKey.TASK_STATUS_KEY + taskId, "failed");
                    return null;
                });

        return taskId;
    }

    private String getTemplatePrompt(String templateCode) {
        if ("id_photo".equals(templateCode)) {
            return "  请以用户上传的人物照片为基础，生成一张标准、真实、干净的证件照。\n" +
                    "  要求：\n" +
                    "  1. 保留人物真实面部特征、发型、年龄感和身份特征，不要明显改变长相。\n" +
                    "  2. 对人物进行自然的人像修饰，提升清晰度与观感，但禁止过度美颜、磨皮、夸张瘦脸或五官变形。\n" +
                    "  3. 自动优化构图，使人物居中，主体突出，适合证件照展示。\n" +
                    "  4. 背景简洁、纯净、均匀，整体画面整洁专业。\n" +
                    "  5. 光线自然，肤色正常，避免曝光过度、阴影过重、色偏或噪点。\n" +
                    "  6. 衣着与整体形象应端正得体，符合正式证件照的视觉要求。\n" +
                    "  7. 保证头部、肩部、面部完整清晰，不裁切关键区域。\n" +
                    "  8. 输出风格应真实、规范、专业，具有标准证件照质感。\n" +
                    "  9. 不添加文字、水印、边框、装饰元素。\n" +
                    "  10.背景为蓝色\n" +
                    "  输出两张高质量且风格不同的证件照图片。";
        }
        if ("background_replace".equals(templateCode)) {
            return "请以第1张图片中的主体为核心，保留主体的外观、姿态、面部特征、服装、发型和主体比例，不改变主体身份与主要细节。\n" +
                    "  将第2张图片作为新的背景场景，完成自然融合。\n" +
                    "  要求：\n" +
                    "  1. 主体必须完整清晰，不要裁切身体关键区域。\n" +
                    "  2. 背景替换后，光线、色温、阴影和透视关系要自然一致。\n" +
                    "  3. 保留主体边缘细节，避免抠图痕迹、白边、错位、重影。\n" +
                    "  4. 背景中的无关人物或杂乱元素可适当弱化，突出主体。\n" +
                    "  5. 整体画面真实、干净、协调，具有自然摄影感。\n" +
                    "  6. 不添加额外文字、水印、边框，不改变图片主题。\n" +
                    "  输出两张完成背景替换后的高质量且风格不同的图片。";
        }
        if ("product_with_model".equals(templateCode)) {
            return "请将第1张图片中的人物与第2张图片中的商品自然结合，生成一张适合电商展示和内容种草的真人带货图。\n" +
                    "  要求：\n" +
                    "  1. 保留人物的面部特征、发型、体态和整体身份特征，不要明显改变人物形象。\n" +
                    "  2. 准确保留商品的外观、颜色、材质、结构和品牌特征，不要随意变形或替换商品细节。\n" +
                    "  3. 人物与商品之间的互动要自然，姿势合理，可以表现为手持、展示、贴近陈列或场景搭配。\n" +
                    "  4. 整体画面应突出商品，同时保留人物亲和力和表现力。\n" +
                    "  5. 光线、透视、清晰度、色调保持统一，避免拼接感、悬浮感和比例失衡。\n" +
                    "  6. 背景可根据商品属性进行适度优化，使画面更适合带货宣传，但不要喧宾夺主。\n" +
                    "  7. 输出风格偏真实、精致、清爽，适合电商主图、详情页或种草图使用。\n" +
                    "  8. 不添加文字、水印、Logo、边框。\n" +
                    "  输出两张高质量真人带货展示且风格不同的图片。";
        }

        throw new RuntimeException("模板不存在");
    }
}

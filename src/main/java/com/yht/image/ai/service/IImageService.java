package com.yht.image.ai.service;

import com.yht.image.ai.controller.dto.ChatRequestDTO;
import com.yht.image.ai.controller.dto.TaskResponseDTO;
import com.yht.image.ai.service.entity.ChatResultEntity;

import java.util.List;

/**
 * @Description
 * @Author hela
 * @Date 2025/11/5
 */
public interface IImageService {
    String TextToImage(ChatRequestDTO chatRequestDTO);

    TaskResponseDTO queryResultByTaskId(String taskId);
}

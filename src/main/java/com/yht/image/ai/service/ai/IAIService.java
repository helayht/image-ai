package com.yht.image.ai.service.ai;

import com.yht.image.ai.controller.dto.ChatRequestDTO;
import com.yht.image.ai.service.entity.ChatResultEntity;

import java.util.List;

/**
 * @Description
 * @Author hela
 * @Date 2025/11/6
 */
public interface IAIService {
    ChatResultEntity creatImage(ChatRequestDTO chatRequestDTO);

    ChatResultEntity imageCreatImage(ChatRequestDTO chatRequestDTO, List images, int maxImages);
}

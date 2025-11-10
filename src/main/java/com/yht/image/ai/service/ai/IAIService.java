package com.yht.image.ai.service.ai;

import com.yht.image.ai.controller.dto.ChatRequestDTO;
import com.yht.image.ai.service.entity.ChatResultEntity;

/**
 * @Description
 * @Author hela
 * @Date 2025/11/6
 */
public interface IAIService {
    ChatResultEntity creatImage(ChatRequestDTO chatRequestDTO);
}

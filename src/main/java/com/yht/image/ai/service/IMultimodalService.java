package com.yht.image.ai.service;

import com.yht.image.ai.controller.dto.ChatRequestDTO;

/**
 * @Description 多模态服务
 * @Author hela
 * @Date 2025/11/17
 */
public interface IMultimodalService {
    String imageTOText(String imageUrl);
}

package com.yht.image.ai.service;

import com.yht.image.ai.controller.dto.ChatRequestDTO;
import com.yht.image.ai.controller.dto.MessagesResponseDTO;
import com.yht.image.ai.mapper.po.Conversations;
import com.yht.image.ai.service.entity.ChatResultEntity;

import java.util.List;

/**
 * @Description
 * @Author hela
 * @Date 2025/11/25
 */
public interface IMessagesService {
    Integer addUserMessage(ChatRequestDTO chatRequestDTO, String imageUrl);

    void addAssistantMessage(List<ChatResultEntity> chatResultEntityList, String taskId);

    List<MessagesResponseDTO> getMessageList(Integer conversationsId);

    List<Conversations> getConversationsList(String username);
}

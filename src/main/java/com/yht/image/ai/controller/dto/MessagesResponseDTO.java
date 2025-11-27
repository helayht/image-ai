package com.yht.image.ai.controller.dto;

import com.yht.image.ai.service.entity.ChatResultEntity;
import lombok.Data;

import java.util.List;

/**
 * @Description
 * @Author hela
 * @Date 2025/11/26
 */
@Data
public class MessagesResponseDTO {
    private String title;
    private String content;
    private String createdTime;
    private String role;
    private String attachmentURL;
    private List<AssistantMessage> assistantMessages;
    @Data
    public static class AssistantMessage{
        private String modelName;
        private String modelId;
        private String imageURL;
    }
}

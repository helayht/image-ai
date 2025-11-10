package com.yht.image.ai.controller.dto;

import com.yht.image.ai.service.entity.ChatResultEntity;
import lombok.Data;
import org.apache.catalina.LifecycleState;

import java.util.List;

/**
 * @Description
 * @Author hela
 * @Date 2025/11/10
 */
@Data
public class TaskResponseDTO {
    private List<ChatResultEntity> chatResultEntityList;
    private String status;
}

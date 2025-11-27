package com.yht.image.ai.controller.dto;

import lombok.Data;

import java.util.List;

/**
 * @Description
 * @Author hela
 * @Date 2025/11/6
 */
@Data
public class ChatRequestDTO {
    private String prompt;
    private List<String> models;
    private String mode;
    private Integer conversationsId;
    private String size = "1024x1024";

}

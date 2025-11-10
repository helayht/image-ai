package com.yht.image.ai.service.entity;

import lombok.Data;

import java.util.Date;

/**
 * @Description
 * @Author hela
 * @Date 2025/11/6
 */
@Data
public class ChatResultEntity {
    private String imageURL;
    private String modelName;
    private String modelId;
    private Date dateTime;
}

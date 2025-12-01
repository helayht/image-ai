package com.yht.image.ai.service.entity;

import lombok.Data;

import java.util.Date;

/**
 * @Description
 * @Author hela
 * @Date 2025/12/1
 */
@Data
public class UserMessageEntity {
    private String openId;
    private String fromUserName;
    private String msgType;
    private String content;
    private String event;
    private Date createTime;

}

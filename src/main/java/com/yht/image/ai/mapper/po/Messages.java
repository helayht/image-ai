package com.yht.image.ai.mapper.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * @Description
 * @Author hela
 * @Date 2025/11/25
 */
@Data
public class Messages {
    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;
    private Integer conversationsId;
    private String role;
    private String content;
    private String ModelId;
    private String ModelName;
    private String ImageUrl;
    private String createdTime;

}

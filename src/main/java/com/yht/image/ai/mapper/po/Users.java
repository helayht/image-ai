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
public class Users {
    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;
    private String username;
    private String password;
}

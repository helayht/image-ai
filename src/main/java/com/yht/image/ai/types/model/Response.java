package com.yht.image.ai.types.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Description
 * @Author hela
 * @Date 2025/11/6
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Response<T> implements Serializable {
    private String code;
    private String info;
    private T data;
}

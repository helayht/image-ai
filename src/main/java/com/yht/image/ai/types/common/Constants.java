package com.yht.image.ai.types.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @Description
 * @Author hela
 * @Date 2025/11/6
 */
public class Constants {

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public enum ResponseCode {
        SUCCESS("200", "成功"),
        FAIL("500", "失败")
        ;
        private String code;
        private String message;
    }

    public static class RedisKey{
        public static String TASK_RESULT_KEY = "task_result_";
        public static String TASK_STATUS_KEY = "task_status_";
    }
}

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
        /**
         * 任务结果缓存
         */
        public static String TASK_RESULT_KEY = "task_result_";
        /**
         * 任务状态缓存
         */
        public static String TASK_STATUS_KEY = "task_status_";
        /**
         * 会话缓存
         */
        public static String TASK_CONVERSATIONS_KEY = "task_conversations_";
    }
}

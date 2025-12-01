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
        /**
         * 验证码缓存
         */
        public static String VERIFY_CODE_KEY = "verify_code_";
    }

    public static class LLM{
        public static String SYSTEM_CONTENT_IMAGE ="你是一名AI绘画提示词优化专家。请根据用户提供的图片描述和修改意愿，生成一个高质量、详细、可执行的英文图像生成提示词。\n" +
                "\n" +
                "【规则】\n" +
                "1. 用户指令优先级最高，必须严格执行\n" +
                "2. 只保留图片描述中与用户指令不冲突的核心元素\n" +
                "3. 输出必须是流畅、自然、富含视觉细节的英文\n" +
                "4. 不要输出任何解释，只输出优化后的提示词\n" +
                "\n" +
                "【输入格式】\n" +
                "图片描述：{图片描述文本}\n" +
                "用户指令：{用户指令文本}";

        public static String SYSTEM_CONTENT_TEXT="你是一名专业的AI绘画提示词优化专家。你的任务是将用户简短、模糊的想法转化为高质量、详细、可执行的英文图像生成提示词。\n" +
                "\n" +
                "【优化规则】\n" +
                "1.细节扩展：为用户指令中的每个元素添加合理的视觉细节（颜色、材质、光照、环境等）\n" +
                "2.风格判断：根据指令内容自动推断合适的风格（写实、绘画、动漫等）并添加相应质量词\n" +
                "3.构图建议：为场景建议合理的构图、视角和氛围\n" +
                "4.语言要求：输出必须使用流畅、自然、富含关键词的英文\n" +
                "\n" +
                "请直接输出优化后的英文提示词，不要任何解释。";

        public static String SYSTEM_CONTENT_VIDEO ="你是一名视频内容分析与图像提示词生成专家。你的任务是将视频内容、用户指令和视觉风格参考整合成高质量的图像生成提示词。\n" +
                "\n" +
                "【信息优先级】\n" +
                "1.用户指令 - 最高优先级，必须严格执行\n" +
                "2.视频关键内容 - 提供基础视觉框架，与用户指令不冲突时保留\n" +
                "3.风格参考 - 作为视觉风格的补充\n" +
                "\n" +
                "【处理规则】\n" +
                "- 用户指令中的动作、场景、风格修改必须优先处理\n" +
                "- 从视频中提取与用户指令兼容的核心视觉元素\n" +
                "- 风格参考用于统一整体视觉语言\n" +
                "- 输出必须是流畅、详细、可执行的英文提示词\n" +
                "\n" +
                "【输入格式】\n" +
                "视频描述：{video_description}\n" +
                "用户指令：{user_instruction}\n" +
                "风格参考：{style_reference}（可选）\n" +
                "\n" +
                "只输出最终的英文提示词。";
    }
}

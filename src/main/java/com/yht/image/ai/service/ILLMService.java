package com.yht.image.ai.service;

/**
 * @Description
 * @Author hela
 * @Date 2025/11/17
 */
public interface ILLMService {
    String imageTOText(String imageUrl);

    String imagePromptOptimization(String prompt,String imageDescription);

    String textPromptOptimization(String prompt);
}

package com.yht.image.ai.service;

import java.io.IOException;

/**
 * @Description
 * @Author hela
 * @Date 2025/11/17
 */
public interface ILLMService {
    String imageTOText(String imageUrl);

    String imagePromptOptimization(String prompt,String imageDescription);

    String textPromptOptimization(String prompt);

    String vedioTOText(String vedioUrl) throws IOException;

    String vedioPromptOptimization(String prompt, String description);
}

package com.yht.image.ai.service.impl;

import com.volcengine.ark.runtime.model.completion.chat.ChatCompletionContentPart;
import com.volcengine.ark.runtime.model.completion.chat.ChatCompletionRequest;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessage;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessageRole;
import com.volcengine.ark.runtime.service.ArkService;
import com.yht.image.ai.service.ILLMService;
import com.yht.image.ai.types.common.Constants;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * @Description 多模态服务
 * @Author hela
 * @Date 2025/11/17
 */
@Service
public class LLMService implements ILLMService {
    @Resource(name = "doubaoAiClient")
    private ArkService service;
    @Value("${upload.path}")
    private String uploadPath;

    @Override
    public String imageTOText(String imageUrl) {
        List<ChatMessage> messages = new ArrayList<>();
        List<ChatCompletionContentPart> multiParts = new ArrayList<>();
        multiParts.add(ChatCompletionContentPart.builder().type("image_url").imageUrl(
                new ChatCompletionContentPart.ChatCompletionContentPartImageURL(imageUrl)).build());
        multiParts.add(ChatCompletionContentPart.builder().type("text").text("图片主要讲了什么?").build());
        final ChatMessage userMessage = ChatMessage.builder().role(ChatMessageRole.USER)
                .multiContent(multiParts).build();
        messages.add(userMessage);
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model("doubao-seed-1-6-vision-250815")
                .messages(messages)
                .build();

        String result = (String) service.createChatCompletion(chatCompletionRequest).getChoices().get(0).getMessage().getContent();
        service.shutdownExecutor();
        return result;
    }

    @Override
    public String textPromptOptimization(String prompt) {
        List<ChatMessage> messages = new ArrayList<>();
        ChatMessage systemMessage = ChatMessage.builder().role(ChatMessageRole.SYSTEM).content(Constants.LLM.SYSTEM_CONTENT_TEXT).build();
        ChatMessage userMessage = ChatMessage.builder().role(ChatMessageRole.USER).content(prompt).build();
        messages.add(systemMessage);
        messages.add(userMessage);

        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model("deepseek-v3-250324")
                .messages(messages)
                .build();
        String result = (String) service.createChatCompletion(chatCompletionRequest).getChoices().get(0).getMessage().getContent();
        service.shutdownExecutor();
        return result;
    }

    @Override
    public String vedioTOText(String vedioUrl) throws IOException {
        List<ChatMessage> messagesForReqList = new ArrayList<>();
        // 构建消息内容（修复内容部分构建方式）
        List<ChatCompletionContentPart> contentParts = new ArrayList<>();

//         图片部分使用builder模式
        contentParts.add(ChatCompletionContentPart.builder()
                .type("video_url")
                .videoUrl(new ChatCompletionContentPart.ChatCompletionContentPartVideoURL(vedioUrl, 2))
                .build());

        // 文本部分使用builder模式
        contentParts.add(ChatCompletionContentPart.builder()
                .type("text")
                .text("What's in the video?")
                .build());

        // 创建消息
        messagesForReqList.add(ChatMessage.builder()
                .role(ChatMessageRole.USER)
                .multiContent(contentParts)
                .build());

        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model("doubao-seed-1-6-vision-250815") //Replace with Model ID .
                .messages(messagesForReqList)
                .build();

        String result = (String) service.createChatCompletion(chatCompletionRequest).getChoices().get(0).getMessage().getContent();
        service.shutdownExecutor();
        return result;
    }

    @Override
    public String vedioPromptOptimization(String prompt, String description) {
        List<ChatMessage> messages = new ArrayList<>();
        String userPrompt = "视频描述：" + description + "\n" + "用户指令：" + prompt;
        ChatMessage systemMessage = ChatMessage.builder().role(ChatMessageRole.SYSTEM).content(Constants.LLM.SYSTEM_CONTENT_VIDEO).build();
        ChatMessage userMessage = ChatMessage.builder().role(ChatMessageRole.USER).content(userPrompt).build();
        messages.add(systemMessage);
        messages.add(userMessage);

        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model("deepseek-v3-250324")
                .messages(messages)
                .build();
        String result = (String) service.createChatCompletion(chatCompletionRequest).getChoices().get(0).getMessage().getContent();
        service.shutdownExecutor();
        return result;
    }

    @Override
    public String imagePromptOptimization(String prompt, String imageDescription) {
        List<ChatMessage> messages = new ArrayList<>();
        String userPrompt = "图片描述：" + imageDescription + "\n" + "用户指令：" + prompt;
        ChatMessage systemMessage = ChatMessage.builder().role(ChatMessageRole.SYSTEM).content(Constants.LLM.SYSTEM_CONTENT_IMAGE).build();
        ChatMessage userMessage = ChatMessage.builder().role(ChatMessageRole.USER).content(userPrompt).build();
        messages.add(systemMessage);
        messages.add(userMessage);

        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model("deepseek-v3-250324")
                .messages(messages)
                .build();
        String result = (String) service.createChatCompletion(chatCompletionRequest).getChoices().get(0).getMessage().getContent();
        service.shutdownExecutor();
        return result;
    }
}

package com.yht.image.ai.service.impl;

import com.volcengine.ark.runtime.model.completion.chat.ChatCompletionContentPart;
import com.volcengine.ark.runtime.model.completion.chat.ChatCompletionRequest;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessage;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessageRole;
import com.volcengine.ark.runtime.service.ArkService;
import com.yht.image.ai.service.IMultimodalService;
import jakarta.annotation.Resource;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import org.apache.tomcat.util.codec.binary.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Description 多模态服务
 * @Author hela
 * @Date 2025/11/17
 */
@Service
public class MultimodalService implements IMultimodalService {
    @Resource(name = "doubaoMultimodalClient")
    private ArkService service;

    @Override
    public String imageTOText(String imageUrl) {
        final List<ChatMessage> messages = new ArrayList<>();
        final List<ChatCompletionContentPart> multiParts = new ArrayList<>();
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
        System.out.println(result);
        return result;
    }
}

package com.yht.image.ai.service.ai.impl;

import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversation;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationParam;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationResult;
import com.alibaba.dashscope.common.MultiModalMessage;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.exception.UploadFileException;
import com.alibaba.dashscope.utils.Constants;
import com.yht.image.ai.controller.dto.ChatRequestDTO;
import com.yht.image.ai.service.ai.IAIService;
import com.yht.image.ai.service.entity.ChatResultEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @Description
 * @Author hela
 * @Date 2026/4/1
 */
@Component("qwen-image-2.0-pro")
public class TongYiService implements IAIService {

    static {
        // 以下为中国（北京）地域url，若使用新加坡地域的模型，需将url替换为：https://dashscope-intl.aliyuncs.com/api/v1
        Constants.baseHttpApiUrl = "https://dashscope.aliyuncs.com/api/v1";
    }

    // 新加坡和北京地域的API Key不同。获取API Key：https://help.aliyun.com/zh/model-studio/get-api-key
    // 若没有配置环境变量，请用百炼 API Key 将下行替换为：apiKey="sk-xxx"
//    @Value("${ai.api-key.tongyi}")
    static private String apiKey = "sk-f4f980d4d2874faf9b765999b9149ea5";

    @Override
    public ChatResultEntity creatImage(ChatRequestDTO chatRequestDTO) {
        return null;
    }

    @Override
    public ChatResultEntity imageCreatImage(ChatRequestDTO chatRequestDTO, List<String> images, int maxImages) throws NoApiKeyException, UploadFileException {
        MultiModalConversation conv = new MultiModalConversation();
        List<Map<String,Object>> contents = new ArrayList<>();
        if(images!= null) for(String image : images){
            Map<String, Object> content = new HashMap<>();
            content.put("image", image);
            contents.add(content);
        }
        Map<String,Object> prompt = new HashMap<>();
        prompt.put("text", chatRequestDTO.getPrompt());
        contents.add(prompt);

        // 模型支持输入1-3张图片
        MultiModalMessage userMessage = MultiModalMessage.builder().role(Role.USER.getValue()).content(contents).build();
        // qwen-image-2.0系列、qwen-image-edit-max、qwen-image-edit-plus系列支持输出1-6张图片
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("watermark", false);
        parameters.put("negative_prompt", " ");
        parameters.put("n", 2);
        parameters.put("prompt_extend", true);
        parameters.put("size", "2048*2048");

        MultiModalConversationParam param = MultiModalConversationParam.builder()
                .apiKey(apiKey)
                .model("qwen-image-2.0-pro")
                .messages(Collections.singletonList(userMessage))
                .parameters(parameters)
                .build();

        MultiModalConversationResult result = conv.call(param);
        // 如需查看完整响应，请取消下行注释
        // System.out.println(JsonUtils.toJson(result));
        ChatResultEntity chatResultEntity = new ChatResultEntity();
        String generatedUrl = "";
        List<Map<String, Object>> contentList = result.getOutput().getChoices().get(0).getMessage().getContent();
        for (Map<String, Object> content : contentList) {
            if (content.containsKey("image")) {
                System.out.println("输出图像" + content.get("image"));
                generatedUrl += content.get("image").toString();
                generatedUrl += ",";
            }
        }
        chatResultEntity.setImageURL(generatedUrl);
        chatResultEntity.setDateTime(new Date());
        chatResultEntity.setModelId("qwen-image-2.0-pro");
        chatResultEntity.setModelName("通义千问");
        return chatResultEntity;
    }
}

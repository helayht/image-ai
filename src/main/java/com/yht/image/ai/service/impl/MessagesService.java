package com.yht.image.ai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yht.image.ai.controller.dto.ChatRequestDTO;
import com.yht.image.ai.controller.dto.MessagesResponseDTO;
import com.yht.image.ai.mapper.ConversationsMapper;
import com.yht.image.ai.mapper.MessagesMapper;
import com.yht.image.ai.mapper.po.Conversations;
import com.yht.image.ai.mapper.po.Messages;
import com.yht.image.ai.service.IMessagesService;
import com.yht.image.ai.service.entity.ChatResultEntity;
import com.yht.image.ai.types.common.Constants;
import com.yht.image.ai.types.util.IRedisService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Description
 * @Author hela
 * @Date 2025/11/25
 */
@Service
public class MessagesService implements IMessagesService {
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    @Resource
    private MessagesMapper messagesMapper;
    @Resource
    private ConversationsMapper conversationsMapper;
    @Resource
    private IRedisService redisService;
    @Override
    public Integer addUserMessage(ChatRequestDTO chatRequestDTO, String imageUrl) {
        Conversations conversations = new Conversations();
        Date date = new Date();
        String createdTime = dateFormat.format(date);
        if(chatRequestDTO.getConversationsId() == null){
            conversations.setTitle(chatRequestDTO.getPrompt());
            conversations.setCreatedTime(createdTime);
            conversations.setUsername(chatRequestDTO.getUsername());
            conversationsMapper.insert(conversations);
            chatRequestDTO.setConversationsId(conversations.getId());
        }
        Messages messages = new Messages();
        messages.setRole("user");
        messages.setContent(chatRequestDTO.getPrompt());
        messages.setImageUrl(imageUrl);
        messages.setCreatedTime(createdTime);
        messages.setConversationsId(chatRequestDTO.getConversationsId());
        messagesMapper.insert(messages);
        return messages.getConversationsId();
    }


    @Override
    public void addAssistantMessage(List<ChatResultEntity> chatResultEntityList, String taskId) {
        Date date = new Date();
        String createdTime = dateFormat.format(date);
        Messages messages = new Messages();
        StringBuffer modelId = new StringBuffer();
        StringBuffer modelName = new StringBuffer();
        StringBuffer imageUrl = new StringBuffer();
        int flag=0;
        for (ChatResultEntity chatResultEntity : chatResultEntityList) {
            if(flag==0){
                modelName.append(chatResultEntity.getModelName());
                modelId.append(chatResultEntity.getModelId());
                imageUrl.append(chatResultEntity.getImageURL());
            }else{
                modelName.append(",").append(chatResultEntity.getModelName());
                modelId.append(",").append(chatResultEntity.getModelId());
                imageUrl.append(",").append(chatResultEntity.getImageURL());
            }
            flag=1;
        }
        messages.setRole("assistant");
        messages.setModelId(modelId.toString());
        messages.setModelName(modelName.toString());
        messages.setImageUrl(imageUrl.toString());
        messages.setCreatedTime(createdTime);
        Integer conversationsId = redisService.getValue(Constants.RedisKey.TASK_CONVERSATIONS_KEY+taskId);
            messages.setConversationsId(conversationsId);
        messagesMapper.insert(messages);
    }

    @Override
    public List<MessagesResponseDTO> getMessageList(Integer conversationsId) {
        QueryWrapper<Messages> messagesQueryWrapper = new QueryWrapper<>();
        messagesQueryWrapper.eq("conversations_id", conversationsId);
        messagesQueryWrapper.orderByAsc("created_time");
        List<Messages> messages = messagesMapper.selectList(messagesQueryWrapper);
        QueryWrapper<Conversations> conversationsQueryWrapper = new QueryWrapper<>();
        conversationsQueryWrapper.eq("id", conversationsId);
        Conversations conversations = conversationsMapper.selectOne(conversationsQueryWrapper);
        List<MessagesResponseDTO> messagesResponseDTOS = new ArrayList<>();
        for(Messages message : messages){
            MessagesResponseDTO messagesResponseDTO = new MessagesResponseDTO();
            messagesResponseDTO.setTitle(conversations.getTitle());
            messagesResponseDTO.setContent(message.getContent());
            messagesResponseDTO.setRole(message.getRole());
            messagesResponseDTO.setCreatedTime(message.getCreatedTime());
            String imageUrl = message.getImageUrl();
            String modelName = message.getModelName();
            String modelId = message.getModelId();
            if(message.getRole().equals("assistant")){
                String[] imageUrls = imageUrl.split(",");
                String[] modelNames = modelName.split(",");
                String[] modelIds = modelId.split(",");
                List<MessagesResponseDTO.AssistantMessage> assistantMessages = new ArrayList<>();
                for(int i=0;i<imageUrls.length;i++){
                    MessagesResponseDTO.AssistantMessage assistantMessage = new MessagesResponseDTO.AssistantMessage();
                    assistantMessage.setModelName(modelNames[i]);
                    assistantMessage.setModelId(modelIds[i]);
                    assistantMessage.setImageURL(imageUrls[i]);
                    assistantMessages.add(assistantMessage);
                }
                messagesResponseDTO.setAssistantMessages(assistantMessages);
                messagesResponseDTOS.add(messagesResponseDTO);
            }else{
                messagesResponseDTO.setAttachmentURL(imageUrl);
                messagesResponseDTOS.add(messagesResponseDTO);
            }
        }
        return messagesResponseDTOS;
    }

    @Override
    public List<Conversations> getConversationsList(String username) {
        QueryWrapper<Conversations> conversationsQueryWrapper = new QueryWrapper<>();
        conversationsQueryWrapper.eq("username", username);
        return conversationsMapper.selectList(conversationsQueryWrapper);
    }

    @Override
    public boolean delConversations(List<Integer> conversationsId) {
        return conversationsMapper.deleteBatchIds(conversationsId) > 0;
    }

    @Override
    public boolean updateConversationsTitle(Integer conversationsId, String title) {
        QueryWrapper<Conversations> conversationsQueryWrapper = new QueryWrapper<>();
        conversationsQueryWrapper.eq("id", conversationsId);
        Conversations conversations = new Conversations();
        conversations.setTitle(title);
        return conversationsMapper.update(conversations, conversationsQueryWrapper) > 0;
    }
}
